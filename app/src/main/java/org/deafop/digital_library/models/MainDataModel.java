package org.deafop.digital_library.models;

public class MainDataModel {

    private final String name;
    private final int id;
    private final String image;
    private final String type;

    public MainDataModel(String name, int id, String image, String type) {
        this.name = name;
        this.id = id;
        this.image = image;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public String getType() {
        return type;
    }
}
