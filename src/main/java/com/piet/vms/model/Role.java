package com.piet.vms.model;

public enum Role {
    STUDENT("Student"), HOST("Host / Club Captain"), HOD("HOD"), PROCTOR("Proctor");
    private final String label;
    Role(String label) { this.label = label; }
    public String label() { return label; }
    @Override public String toString() { return label; }
}
