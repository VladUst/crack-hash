package com.example.worker2.service;

import com.example.worker2.model.CrackHashManagerRequest;
import com.example.worker2.model.CrackHashWorkerResponse;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    public List<String> getCrackedWords() {
        List<String> words = new ArrayList<>();
        for(int curLength = 1; curLength <= this.maxLength; ++curLength){
            ICombinatoricsVector<String> vector = createVector(alphabet.split(""));
            Generator<String> gen = createPermutationWithRepetitionGenerator(vector, curLength);
            Iterator<ICombinatoricsVector<String>> iterator = gen.iterator();
            int chunkSize = (int) Math.ceil(Math.pow(alphabet.length(), curLength) * 1.0 / partCount);;
            int start = chunkSize * partNumber;
            int end = Math.min(chunkSize * (partNumber + 1), (int) Math.pow(alphabet.length(), curLength));
            for (int i = 0; i < start; i++) {
                if (iterator.hasNext()) {
                    iterator.next();
                }
            }
            while (iterator.hasNext() && start < end) {
                String word = String.join("", iterator.next().getVector());
                if (generateHash(word).equals(hash)) {
                    words.add(word);
                }
            }
        }
        return words;
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

    public void sendResult(List<String> words) throws JAXBException {
        CrackHashWorkerResponse objToSend = new CrackHashWorkerResponse();
        RestTemplate restTemplate = new RestTemplate();
        objToSend.setAnswers(words);
        objToSend.setPartNumber(this.partNumber);
        objToSend.setRequestId(this.requestId);
        HttpEntity<String> request = createRequest(objToSend);
        String url = "http://localhost:8080/internal/api/manager/hash/crack/request";
        restTemplate.postForEntity(url, request, String.class);
    }

    public HttpEntity<String> createRequest(CrackHashWorkerResponse objToSend) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(CrackHashWorkerResponse.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        StringWriter sw = new StringWriter();
        marshaller.marshal(objToSend, sw);
        String xmlString = sw.toString();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<String> request = new HttpEntity<>(xmlString, headers);
        return request;
    }
}
