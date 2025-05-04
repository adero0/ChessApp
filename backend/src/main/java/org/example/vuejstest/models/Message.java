package org.example.vuejstest.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Message {
    @JsonProperty("username")
    private String username;

    @JsonProperty("text")
    private String text;

    @JsonProperty("timestamp")
    private String timestamp;

    public Message(String username, String text, String timestamp) {
        this.username = username;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return this.username;
    }

    public String getText() {
        return this.text;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    @JsonProperty("username")
    public void setUsername(String username) {
        this.username = username;
    }

    @JsonProperty("text")
    public void setText(String text) {
        this.text = text;
    }

    @JsonProperty("timestamp")
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Message other)) return false;
        if (!other.canEqual((Object) this)) return false;
        final Object this$username = this.getUsername();
        final Object other$username = other.getUsername();
        if (!Objects.equals(this$username, other$username)) return false;
        final Object this$text = this.getText();
        final Object other$text = other.getText();
        if (!Objects.equals(this$text, other$text)) return false;
        final Object this$timestamp = this.getTimestamp();
        final Object other$timestamp = other.getTimestamp();
        return Objects.equals(this$timestamp, other$timestamp);
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Message;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $username = this.getUsername();
        result = result * PRIME + ($username == null ? 43 : $username.hashCode());
        final Object $text = this.getText();
        result = result * PRIME + ($text == null ? 43 : $text.hashCode());
        final Object $timestamp = this.getTimestamp();
        result = result * PRIME + ($timestamp == null ? 43 : $timestamp.hashCode());
        return result;
    }

    public String toString() {
        return "Message(username=" + this.getUsername() + ", text=" + this.getText() + ", timestamp=" + this.getTimestamp() + ")";
    }
}