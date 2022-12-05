package edu.uga.cs.roommate_shopping_app.dom;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String username = "";
    private String email = "";
    private String houseID = "";
    private String firstname = "";
    private String lastname = "";
    private String houseName = "";

    public User() {}

    public User(String email, String username, String houseID, String firstname, String lastname, String houseName) {
        this.email = email;
        this.username = username;
        this.houseID = houseID;
        this.firstname = firstname;
        this.lastname = lastname;
        this.houseName = houseName;
    }

    public String getUsername() {
        return username;
    }
    public String getHouseID() {
        return houseID;
    }
    public String getFirstname() {
        if(firstname != null)
            return firstname;
        else return "null";
    }
    public String getLastname() {
        return lastname;
    }
    public String getHouseName(){
        return houseName;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email", this.email);
        result.put("username", this.username);
        result.put("houseID", this.houseID);
        result.put("firstName", this.firstname);
        result.put("lastName", this.lastname);
        return result;
    }
}
