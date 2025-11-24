package com.example.diorama_project;

public class Sensors {

    private int id;
    private int visitors_val;
    private int claps_val;
    private int lums_val;
    private String createdAt;

    // ---- Getters ----
    public int getId() {
        return id;
    }

    public int getVisitors_val() {
        return visitors_val;
    }

    public int getClaps_val() {
        return claps_val;
    }

    public int getLums_val() {
        return lums_val;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    // ---- Setters ----
    public void setId(int id) {
        this.id = id;
    }

    public void setVisitors_val(int visitors_val) {
        this.visitors_val = visitors_val;
    }

    public void setClaps_val(int claps_val) {
        this.claps_val = claps_val;
    }

    public void setLums_val(int lums_val) {
        this.lums_val = lums_val;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
