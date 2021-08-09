package com.growmore.firebrowser.model;

public class LanguageModel {
    private String title;
    private boolean isChecked;

    public LanguageModel(String title, boolean isChecked){
        this.title = title;
        this.isChecked = isChecked;
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
