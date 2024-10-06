package model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InvitationMessage {

    private String sender;
    private String receiver;
    private String response; // Có thể là "accept" hoặc "reject"
    
    @JsonCreator
    public InvitationMessage(
            @JsonProperty("sender") String sender,
            @JsonProperty("receiver") String receiver) {
        this.sender = sender;
        this.receiver = receiver;
        this.response = null; // Phản hồi có thể không có lúc gửi
    }

    public InvitationMessage(String sender, String receiver, String response) {
        this.sender = sender;
        this.receiver = receiver;
        this.response = response;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "InvitationMessage{" +
                "sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", response='" + response + '\'' +
                '}';
    }
}
