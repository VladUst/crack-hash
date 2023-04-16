package com.example.worker1.controller;

import com.example.worker1.components.TaskManager;
import com.example.worker1.model.CrackHashManagerRequest;
import com.example.worker1.service.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.bind.JAXBException;
import java.util.*;

@RestController
public class WorkerController {

    @Autowired
    private WorkerService workerService;

    @Autowired
    private TaskManager taskManager;

    @PostMapping("/internal/api/worker/hash/crack/task")
    public ResponseEntity<String> handleTask(@RequestBody String xml) {
        try {
            taskManager.addRequestTask(new Runnable() {
                @Override
                public void run() {
                    try {
                        CrackHashManagerRequest crackRequest = workerService.readXml(xml);
                        List<String> wordsList = workerService.getCrackedWords(crackRequest);
                        System.out.println("Hash: " + wordsList);
                        workerService.sendResult(wordsList, crackRequest.getRequestId(), crackRequest.getPartNumber());
                    } catch (JAXBException e) {
                        e.printStackTrace();
                    }
                }
            });
            return ResponseEntity.ok("worker 1 received task");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
