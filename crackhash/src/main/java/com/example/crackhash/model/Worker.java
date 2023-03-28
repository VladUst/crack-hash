package com.example.crackhash.model;

import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;

import static org.paukov.combinatorics.CombinatoricsFactory.createPermutationWithRepetitionGenerator;
import static org.paukov.combinatorics.CombinatoricsFactory.createVector;

public class Worker implements Runnable {

    private final String hash;
    private final String alphabet;
    private final int maxLength;
    private final int start;
    private final int end;

    public Worker(String hash, int maxLength, String alphabet, int start, int end) {
        this.hash = hash;
        this.maxLength = maxLength;
        this.alphabet = alphabet;
        this.start = start;
        this.end = end;
    }

    @Override
    public void run() {
        ICombinatoricsVector<String> vector = createVector(alphabet.split(""));
        Generator<String> gen = createPermutationWithRepetitionGenerator(vector, this.maxLength);
        Iterator<ICombinatoricsVector<String>> iterator = gen.iterator();
        for (int i = 0; i < start; i++) {
            if (iterator.hasNext()) {
                iterator.next();
            }
        }
        while (iterator.hasNext() && start < end) {
            String word = String.join("", iterator.next().getVector());
            if (generateHash(word).equals(hash)) {
                sendResult(word);
                break;
            }
        }
    }

    private void sendResult(String word) {
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
