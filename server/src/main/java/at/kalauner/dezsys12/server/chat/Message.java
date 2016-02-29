package at.kalauner.dezsys12.server.chat;


import java.sql.Timestamp;

/**
 * Created by Paul on 28.02.2016.
 */
public class Message {
    private int id;
    private String chatRoomId;
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
       this(null, sender, content);
    }

    public Message(String chatRoomId, String sender, String content) {
        this.chatRoomId = chatRoomId;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }
}
