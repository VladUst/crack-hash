package com.example.crackhash.controller;

import com.example.crackhash.service.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.bind.JAXBException;

@RestController
public class WorkerController {

    @Autowired
    private WorkerService workerService;

    @PostMapping("/internal/api/worker/hash/crack/task")
    public void handleTask(@RequestBody String xml) throws JAXBException {
        workerService.readXml(xml);
        String crackedHash = workerService.crackHash();
        System.out.println("Hash: " + crackedHash);
        /*int partSize = calculatePartSize(maxLength, partCount);

        List<Worker> workers = new ArrayList<>();
        for (int i = partNumber; i < partCount; i++) {
            int chunkStart = i * partSize;
            int chunkEnd = chunkStart + partSize;
            if (i == request.getPartCount() - 1) {
                chunkEnd = request.getMaxLength();
            }
            Worker worker = new Worker(hash, maxLength, alphabet, chunkStart, chunkEnd);
            workers.add(worker);
            executor.execute(worker);
        }
        System.out.println(xml);*/
    }
    private int calculatePartSize(int maxLength, int partCount) {
        return (int) Math.ceil((double) maxLength / partCount);
    }
}


