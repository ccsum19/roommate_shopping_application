package edu.uga.cs.roommate_shopping_app.dom;

public class House {
    private String id;
    private String name;

    public House() {}

    public House(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
}

