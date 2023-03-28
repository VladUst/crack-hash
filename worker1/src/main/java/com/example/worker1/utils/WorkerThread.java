package com.example.worker1.utils;

import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;

import static org.paukov.combinatorics.CombinatoricsFactory.createPermutationWithRepetitionGenerator;
import static org.paukov.combinatorics.CombinatoricsFactory.createVector;

public class WorkerThread implements Runnable {

    private List<String> words;
    private String alphabet;
    private int partNumber;
    private int partCount;
    private String hash;
    private int curLength;

    public WorkerThread(List<String> words, String alphabet, int partNumber, int partCount, String hash, int curLength) {
        this.words = words;
        this.alphabet = alphabet;
        this.partNumber = partNumber;
        this.partCount = partCount;
        this.hash = hash;
        this.curLength = curLength;
    }

    @Override
    public void run() {
        processCommand();
    }

    private void processCommand() {
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
