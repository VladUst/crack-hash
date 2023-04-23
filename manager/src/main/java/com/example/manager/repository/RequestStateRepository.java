package com.example.manager.repository;

import com.example.manager.model.RequestState;
import com.example.manager.model.RequestStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestStateRepository extends MongoRepository<RequestState, String> {
    RequestState findByRequestId(String requestId);
}
