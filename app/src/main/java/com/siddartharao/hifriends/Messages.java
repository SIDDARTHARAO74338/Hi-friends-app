package com.siddartharao.hifriends;


public class Messages {

    String message;
    String message_top;
    String senderId;
    String timeStamp;
    String userUrl;
    String emoji;

    public Messages() {
    }

    public Messages(String message,String message_top, String senderId, String timeStamp,String userUrl,String emoji) {
        this.message = message;
        this.message_top=message_top;
        this.senderId = senderId;
        this.timeStamp = timeStamp;
        this.userUrl = userUrl;
        this.emoji = emoji;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage_top() {
        return message_top;
    }

    public void setMessage_top(String message_top) {
        this.message_top = message_top;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUserUrl() {
        return userUrl;
    }

    public void setUserUrl(String userUrl) {
        this.userUrl = userUrl;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }
}
