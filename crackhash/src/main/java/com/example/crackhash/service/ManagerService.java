package com.example.crackhash.service;

import com.example.crackhash.components.TaskManager;
import com.example.crackhash.model.RequestState;
import com.example.crackhash.model.RequestStatus;
import com.example.crackhash.utils.AppConfig;
import com.example.crackhash.model.CrackHashManagerRequest;
import com.example.crackhash.model.CrackHashWorkerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;

@Service
public class ManagerService {

    public void sendTask(String requestId, String hash, int maxLength) throws JAXBException {
        RestTemplate restTemplate = new RestTemplate();

        for (int i = 0; i < AppConfig.WORKERS_NUMBER; ++i) {
            CrackHashManagerRequest objToSend = new CrackHashManagerRequest();
            objToSend.setRequestId(requestId);
            objToSend.setHash(hash);
            objToSend.setMaxLength(maxLength);
            objToSend.setAlphabet(AppConfig.ALPHABET);
            objToSend.setPartCount(AppConfig.WORKERS_NUMBER);
            objToSend.setPartNumber(i);
            HttpEntity<String> request = createRequestToWorker(objToSend);
            String url = String.format("http://localhost:%d/internal/api/worker/hash/crack/task", 8081 + i);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            System.out.println(response.getBody());
        }
    }

    public HttpEntity<String> createRequestToWorker(CrackHashManagerRequest objToSend) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(CrackHashManagerRequest.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        StringWriter sw = new StringWriter();
        marshaller.marshal(objToSend, sw);
        String xmlString = sw.toString();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<String> request = new HttpEntity<>(xmlString, headers);
        return request;
    }

    public CrackHashWorkerResponse readXmlResponse(String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(CrackHashWorkerResponse.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        StringReader reader = new StringReader(xml);
        CrackHashWorkerResponse response = (CrackHashWorkerResponse) unmarshaller.unmarshal(reader);
        return response;
    }

    public ResponseEntity<HashMap<String, Object>> getStatus(RequestState request) {
        if (request == null) {
            return ResponseEntity.notFound().build();
        }
        if (request.getStatus() == RequestStatus.IN_PROGRESS) {
            HashMap<String, Object> responseBody = new HashMap<>();
            responseBody.put("status", "IN_PROGRESS");
            responseBody.put("data", null);
            return ResponseEntity.ok(responseBody);
        } else if (request.getStatus() == RequestStatus.READY) {
            HashMap<String, Object> responseBody = new HashMap<>();
            responseBody.put("status", "READY");
            responseBody.put("data", request.getData());
            return ResponseEntity.ok(responseBody);
        }
        else if (request.getStatus() == RequestStatus.ERROR) {
            HashMap<String, Object> responseBody = new HashMap<>();
            responseBody.put("status", "ERROR");
            responseBody.put("data", null);
            return ResponseEntity.ok(responseBody);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
