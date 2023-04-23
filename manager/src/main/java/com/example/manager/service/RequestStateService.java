package com.example.manager.service;

import com.example.manager.model.RequestState;
import com.example.manager.model.RequestStatus;
import com.example.manager.repository.RequestStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RequestStateService {
    @Autowired
    private RequestStateRepository requestStateRepository;

    public void save(RequestState requestStateMongo) {
        requestStateRepository.save(requestStateMongo);
    }

    public RequestState getRequest(String requestId) {
        return requestStateRepository.findByRequestId(requestId);
    }
}
