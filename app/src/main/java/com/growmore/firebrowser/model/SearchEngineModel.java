package com.growmore.firebrowser.model;

public class SearchEngineModel {
    private String id;
    private String title;
    private String link;
    private boolean isChecked = false;

    public SearchEngineModel(String id, String title, String link){
        this.id = id;
        this.title = title;
        this.link = link;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getLink() {
        return link;
    }
    public void setLink(String link) {
        this.link = link;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public boolean isChecked() {
        return isChecked;
    }
    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
