package org.deafop.digital_library.models;

public class SubjectModel {
    int id;
    String name;
    String icon;

    public SubjectModel(int id, String name, String icon) {
        this.id = id;
        this.name = name;
        this.icon = icon;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getIcon() { return icon; }
}
