package com.example.crackhash.model;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(
        name = "CrackHashManagerRequest"
)
public class CrackHashManagerRequest {
    private String requestId;
    private int partNumber;
    private int partCount;
    private String hash;
    private int maxLength;
    private String alphabet;

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

    public int getPartCount() {
        return partCount;
    }

    @XmlElement
    public void setPartCount(int partCount) {
        this.partCount = partCount;
    }

    public String getHash() {
        return hash;
    }

    @XmlElement
    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getMaxLength() {
        return maxLength;
    }

    @XmlElement
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public String getAlphabet() {
        return alphabet;
    }

    @XmlElement
    public void setAlphabet(String alphabet) {
        this.alphabet = alphabet;
    }
}
