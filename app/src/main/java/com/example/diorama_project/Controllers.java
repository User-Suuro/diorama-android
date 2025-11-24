package com.example.diorama_project;

public class Controllers {

    private int id;
    private boolean front_switch;
    private boolean back_switch;
    private boolean left_switch;
    private boolean inside_switch;
    private boolean is_arduino;
    private String createdAt;

    // ---- Getters ----
    public int getId() {
        return id;
    }

    public boolean isFront_switch() {
        return front_switch;
    }

    public boolean isBack_switch() {
        return back_switch;
    }

    public boolean isLeft_switch() {
        return left_switch;
    }

    public boolean isInside_switch() {
        return inside_switch;
    }

    public boolean isArduino() {
        return is_arduino;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    // ---- Setters ----
    public void setId(int id) {
        this.id = id;
    }

    public void setFront_switch(boolean front_switch) {
        this.front_switch = front_switch;
    }

    public void setBack_switch(boolean back_switch) {
        this.back_switch = back_switch;
    }

    public void setLeft_switch(boolean left_switch) {
        this.left_switch = left_switch;
    }

    public void setInside_switch(boolean inside_switch) {
        this.inside_switch = inside_switch;
    }

    public void setArduino(boolean is_arduino) {
        this.is_arduino = is_arduino;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
