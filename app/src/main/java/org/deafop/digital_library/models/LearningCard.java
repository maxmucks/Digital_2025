package org.deafop.digital_library.models;

public class LearningCard {
    public String title;
    public int color, icon;
    public String description; // Add description field

    public LearningCard(String title, int color, int icon) {
        this.title = title;
        this.color = color;
        this.icon = icon;
        this.description = "";
    }

    public LearningCard(String title, int color, int icon, String description) {
        this.title = title;
        this.color = color;
        this.icon = icon;
        this.description = description;
    }

    // Getter methods
    public String getTitle() {
        return title;
    }

    public int getColor() {
        return color;
    }

    public int getIcon() {
        return icon;
    }

    public String getDescription() {
        return description;
    }
}