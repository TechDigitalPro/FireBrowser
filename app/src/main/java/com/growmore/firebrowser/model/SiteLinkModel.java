package com.growmore.firebrowser.model;

public class SiteLinkModel {

    private int id;
    private int image;
    private int title;
    private String link;

    public SiteLinkModel(int id, int image, int title, String link){
        this.id = id;
        this.image = image;
        this.title = title;
        this.link = link;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getImage() {
        return image;
    }
    public void setImage(int image) {
        this.image = image;
    }
    public int getTitle() {
        return title;
    }
    public void setTitle(int title) {
        this.title = title;
    }
    public String getLink() {
        return link;
    }
    public void setLink(String link) {
        this.link = link;
    }
}
