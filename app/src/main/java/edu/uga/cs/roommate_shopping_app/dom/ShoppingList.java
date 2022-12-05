package edu.uga.cs.roommate_shopping_app.dom;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class ShoppingList {
    private HashMap<String, Object> items;
    private HashMap<String, Object> basket;
    private HashMap<String, Object> purchased;
    private String status;
    private String name;
    private String id;
    private String price;

    public ShoppingList() {}

    public ShoppingList(HashMap<String, Object> items, HashMap<String, Object> basket, HashMap<String, Object> purchased, String status, String name, String price) {
        this.items = items;
        this.basket = basket;
        this.purchased = purchased;
        this.status = status;
        this.name = name;
        this.price = price;


    }//constructor

    public String getName() {
        return this.name;
    }
    public String getStatus () {
        return this.status;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }
    public String getPrice() {return price;}

    public HashMap<String, Object> getItems() {
        return this.items;
    }
    public void setListItems(HashMap<String, Object> items) {
        this.items = items;
    }

    public HashMap<String, Object> getBasketItems() {
        return this.basket;
    }
    public void setBasketItems(HashMap<String, Object> basket) {
        this.basket = basket;
    }

    public HashMap<String, Object> getPurchasedItems() {
        return this.purchased;
    }
    public void setPurchasedItems(HashMap<String, Object> purchased) {
        this.purchased = purchased;
    }

    /*
     * data looks like this:
     * "groceries": {
     *      "complete": false,
     *      "price": 69.69,
     *      "items": {
     *          "romaine lettuce": {
     *              "checked": false
     *          }
     *          "cheeseburger": {
     *              "checked": false
     *          }
     *      }
     * }
     */
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", this.name);
        result.put("status", this.status);
        result.put("items", this.items);
        result.put("price", this.price);
        result.put("basket", this.basket);
        return result;
    }//toMap

    public static class ListItem {
        private String name;
        private boolean isChecked;

        public ListItem(String name, boolean isChecked) {
            this.name = name;
            this.isChecked = isChecked;
        }//constructor

        public String getName() {
            return name;
        }

        public boolean isChecked() {
            return isChecked;
        }
    }//class ListItem

    public static class ListBasketItem {
        private String name;
        private boolean isChecked;

        public ListBasketItem(String name, boolean isChecked) {
            this.name = name;
            this.isChecked = isChecked;
        }//constructor

        public String getName() {
            return name;
        }

        public boolean isChecked() {
            return isChecked;
        }
    }//class ListItem

}//class ShoppingList
