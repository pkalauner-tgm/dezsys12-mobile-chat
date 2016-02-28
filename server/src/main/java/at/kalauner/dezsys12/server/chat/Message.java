package at.kalauner.dezsys12.server.chat;


import java.sql.Timestamp;

/**
 * Created by Paul on 28.02.2016.
 */
public class Message {
    private String sender;
    private String timestamp;
    private String content;


    public Message() {
        this(null, null);
    }

    public Message(String content) {
        this(null, content);
    }

    public Message(String sender, String content) {
        this.sender = sender;
        this.timestamp = new Timestamp(System.currentTimeMillis()).toString();
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
