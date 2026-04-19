package com.aja.model;

/**
 * DTO para representar los comentarios dentro de un hilo del foro.
 */
public class CommentDto {
    private String content;
    private String author;
    private String date;

    public CommentDto() {}

    public CommentDto(String content, String author, String date) {
        this.content = content;
        this.author = author;
        this.date = date;
    }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}