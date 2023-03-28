package com.example.worker2.controller;

import com.example.worker2.components.TaskManager;
import com.example.worker2.service.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.bind.JAXBException;
import java.util.List;

@RestController
public class WorkerController {

    @Autowired
    private WorkerService workerService;

    @Autowired
    TaskManager taskManager;

    @PostMapping("/internal/api/worker/hash/crack/task")
    public ResponseEntity<String> handleTask(@RequestBody String xml) throws JAXBException {
        workerService.readXml(xml);
        taskManager.addRequestTask(new Runnable() {
            @Override
            public void run() {
                try {
                    List<String> wordsList = workerService.getCrackedWords();
                    System.out.println("Hash: " + wordsList);
                    workerService.sendResult(wordsList);
                } catch (JAXBException e) {
                    e.printStackTrace();
                }
            }
        });
        return ResponseEntity.ok("worker 2 received task");
    }
}
