package com.example.manager.controller;

import com.example.manager.components.TaskManager;
import com.example.manager.model.AppConfig;
import com.example.manager.model.CrackHashWorkerResponse;
import com.example.manager.model.RequestState;
import com.example.manager.model.RequestStatus;
import com.example.manager.service.ManagerService;
import com.example.manager.service.RequestStateService;
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
    private RequestStateService requestStateService;

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
                requestStateService.save(requestState);
                //requests.put(requestId, requestState);
                managerService.sendTask(requestId, hash, maxLength);
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    public void run() {
                        RequestState request = requestStateService.getRequest(requestId);
                        if (request.getStatus() == RequestStatus.IN_PROGRESS){
                            request.setStatus(RequestStatus.ERROR);
                            requestStateService.save(request);
                        }
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
        //RequestState request = requests.get(requestId);
        RequestState request = requestStateService.getRequest(requestId);
        return managerService.getStatus(request);
    }

    @PostMapping("/internal/api/manager/hash/crack/request")
    public void receiveCrackResult(@RequestBody String xml) throws JAXBException {
        CrackHashWorkerResponse response = managerService.readXmlResponse(xml);
        String requestId = response.getRequestId();
        //RequestState requestState = requests.get(response.getRequestId());
        RequestState requestState = requestStateService.getRequest(requestId);
        if (requestState.getStatus() == RequestStatus.ERROR){
            return;
        }
        requestState.setStatus(RequestStatus.READY);
        List<String> data = requestState.getData();
        if (data != null) {
            data.addAll(response.getAnswers());
            requestState.setData(data);
        } else {
            requestState.setData(response.getAnswers());
        }
        requestStateService.save(requestState);
        System.out.println("Response: " + response.getAnswers());
    }
}
