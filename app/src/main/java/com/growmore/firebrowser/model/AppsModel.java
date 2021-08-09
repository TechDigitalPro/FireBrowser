package com.growmore.firebrowser.model;

import java.util.ArrayList;

public class AppsModel {
    private String title, description;
    private String icon, link, packageName;
    private String buttonColor, buttonText;
    private ArrayList<String> screenshot;

    public AppsModel(){}

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getIcon() {
        return icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }
    public String getLink() {
        return link;
    }
    public void setLink(String link) {
        this.link = link;
    }
    public String getPackageName() {
        return packageName;
    }
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    public String getButtonColor() {
        return buttonColor;
    }
    public void setButtonColor(String buttonColor) {
        this.buttonColor = buttonColor;
    }
    public String getButtonText() {
        return buttonText;
    }
    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }
    public ArrayList<String> getScreenshot() {
        return screenshot;
    }
    public void setScreenshot(ArrayList<String> screenshot) {
        this.screenshot = screenshot;
    }
}
