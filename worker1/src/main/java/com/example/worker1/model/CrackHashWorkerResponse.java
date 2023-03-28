package com.example.worker1.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(
        name = "CrackHashWorkerResponse"
)
public class CrackHashWorkerResponse {

    private String requestId;

    private int partNumber;

    private List<String> answers;

    public String getRequestId() {
        return requestId;
    }

    @XmlElement
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public int getPartNumber() {
        return partNumber;
    }

    @XmlElement
    public void setPartNumber(int partNumber) {
        this.partNumber = partNumber;
    }

    public List<String> getAnswers() {
        return answers;
    }

    @XmlElement
    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }
}
