package com.sagar.notesdemo.Model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Data {

    private String title;
    private String description;
    private String datetime;

    public Data() {

    }

    public Data(String title, String description, String datetime) {
        this.title = title;
        this.description = description;
        this.datetime = datetime;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("description", description);
        result.put("datetime", datetime);

        return result;
    }
    // [END post_to_map]

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

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
