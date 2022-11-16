package bulletinboard;

import java.util.Date;

public class Message {
    public String sender;
    public Date date;
    public String subject;
    public String body;

    // constructor
    public Message(String sender, String subject, String body) {
        this.sender = sender;
        this.subject = subject;
        this.body = body;
        this.date = new Date();
    }
}
