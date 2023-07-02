package com.example.krasivo;

public class Catalog {
    private String name;
    private String image;

    public Catalog(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }
    public String getImage() {
        return image;
    }
}