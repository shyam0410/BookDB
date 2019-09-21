package com.android.bookdb.Model;

public class BookInformationModel {

    private String name;
    private String author;
    private String coverPageUri;

    public String getCoverPageUri() {
        return coverPageUri;
    }

    public void setCoverPageUri(String coverPageUri) {
        this.coverPageUri = coverPageUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
