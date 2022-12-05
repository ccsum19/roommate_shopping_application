package edu.uga.cs.roommate_shopping_app.dom;

public class InboxMessage {
    private String message;
    private String houseID;
    private String houseName;
    private String invitedBy;

    public InboxMessage() {}

    public InboxMessage(String message, String houseID, String houseName, String invitedBy) {
        this.message = message;
        this.houseID = houseID;
        this.houseName = houseName;
        this.invitedBy = invitedBy;
    }

    public String getHouseID() {
        return houseID;
    }
    public String getHouseName() {
        return houseName;
    }
}
