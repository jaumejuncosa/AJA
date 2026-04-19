package com.aja.model;

public class PostMessageDto {
    private Long id;
    private String text;
    private UserDto user;
    private String creationDate;
    private TopicDto topic;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public UserDto getUser() { return user; }
    public void setUser(UserDto user) { this.user = user; }

    public String getCreationDate() { return creationDate; }
    public void setCreationDate(String creationDate) { this.creationDate = creationDate; }

    public TopicDto getTopic() { return topic; }
    public void setTopic(TopicDto topic) { this.topic = topic; }

    public String getAuthor() {
        return user != null ? user.getUsername() : "anónimo";
    }
}