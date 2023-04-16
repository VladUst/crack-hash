package com.example.crackhash.controller;

import com.example.crackhash.model.CrackHashWorkerResponse;
import com.example.crackhash.model.RequestState;
import com.example.crackhash.model.RequestStatus;
import com.example.crackhash.service.ManagerService;
import com.example.crackhash.components.TaskManager;
import com.example.crackhash.utils.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.JAXBException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class ManagerController {
    private final ConcurrentHashMap<String, RequestState> requests = new ConcurrentHashMap<>();

    @Autowired
    private ManagerService managerService;

    @Autowired
    private TaskManager taskManager;

    @Async
    @PostMapping("/api/hash/crack")
    public CompletableFuture<ResponseEntity<HashMap<String, String>>> crackHash(@RequestBody HashMap<String, Object> requestBody) throws JAXBException {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String hash = (String) requestBody.get("hash");
                int maxLength = (int) requestBody.get("maxLength");
                String requestId = UUID.randomUUID().toString();
                RequestState requestState = new RequestState(requestId);
                requests.put(requestId, requestState);
                managerService.sendTask(requestId, hash, maxLength);
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    public void run() {
                        RequestState requestState = requests.get(requestId);
                        requestState.setStatus(RequestStatus.ERROR);
                    }
                }, AppConfig.TIMEOUT);
                HashMap<String, String> responseBody = new HashMap<>();
                responseBody.put("requestId", requestId);
                return ResponseEntity.ok(responseBody);
            } catch (Exception e) {
                HashMap<String, String> responseBody = new HashMap<>();
                responseBody.put("error", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
            }
        });
    }

    @GetMapping("/api/hash/status")
    public ResponseEntity<HashMap<String, Object>> getRequestStatus(@RequestParam String requestId) {
        RequestState request = requests.get(requestId);
        return managerService.getStatus(request);
    }

    @PostMapping("/internal/api/manager/hash/crack/request")
    public void receiveCrackResult(@RequestBody String xml) throws JAXBException {
        CrackHashWorkerResponse response = managerService.readXmlResponse(xml);
        RequestState requestState = requests.get(response.getRequestId());
        if (requestState.getStatus() == RequestStatus.ERROR){
            return;
        }
        requestState.setStatus(RequestStatus.READY);
        List<String> data = requestState.getData();
        if (data!= null) {
            data.addAll(response.getAnswers());
            requestState.setData(data);
        } else {
            requestState.setData(response.getAnswers());
        }
        System.out.println("Response: " + response.getAnswers());
    }
}
