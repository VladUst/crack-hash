package com.example.crackhash.service;

import com.example.crackhash.model.CrackHashManagerRequest;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;

import static org.paukov.combinatorics.CombinatoricsFactory.createPermutationWithRepetitionGenerator;
import static org.paukov.combinatorics.CombinatoricsFactory.createVector;

@Service
public class WorkerService {
    private String requestId;
    private int partNumber;
    private int partCount;
    private String hash;
    private int maxLength;
    private String alphabet;

    public void readXml(String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(CrackHashManagerRequest.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        StringReader reader = new StringReader(xml);
        CrackHashManagerRequest request = (CrackHashManagerRequest) unmarshaller.unmarshal(reader);
        this.requestId = request.getRequestId();
        this.hash = request.getHash();
        this.maxLength = request.getMaxLength();
        this.alphabet = request.getAlphabet();
        this.partNumber = request.getPartNumber();
        this.partCount = request.getPartCount();
    }

    public String crackHash() {
        ICombinatoricsVector<String> vector = createVector(alphabet.split(""));
        Generator<String> gen = createPermutationWithRepetitionGenerator(vector, this.maxLength);
        Iterator<ICombinatoricsVector<String>> iterator = gen.iterator();
        int chunkSize = (int) Math.ceil((double) maxLength / partCount);
        int start = chunkSize * partNumber;
        int end = Math.min(chunkSize * (partNumber + 1), (int) Math.pow(alphabet.length(), maxLength));
        for (int i = 0; i < start; i++) {
            if (iterator.hasNext()) {
                iterator.next();
            }
        }
        while (iterator.hasNext() && start < end) {
            String word = String.join("", iterator.next().getVector());
            if (generateHash(word).equals(hash)) {
                return word;
                //sendResult(word);
                //break;
            }
        }
        return "";
    }

    public void sendResult(String word) {
        System.out.println(word);
        // отправка результата на сервер
    }

    private String generateHash(String str) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(StandardCharsets.UTF_8.encode(str));
            String hashResult = String.format("%032x", new BigInteger(1, md5.digest()));
            return hashResult;
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error creating MessageDigest instance: {}" + e.getMessage());
            return null;
        }
    }
}
