package com.example.manager.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
@Document
public class RequestState {
    @Id
    private String requestId;
    private RequestStatus status;
    private List<String> data;

    public RequestState(String requestId) {
        this.requestId = requestId;
        this.status = RequestStatus.IN_PROGRESS;
        this.data = null;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    public String getRequestId() {
        return requestId;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public List<String> getData() {
        return data;
    }
}
