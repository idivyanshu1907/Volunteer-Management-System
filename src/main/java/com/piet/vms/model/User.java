package com.piet.vms.model;

public record User(long id, String name, String registrationNumber, String email, Role role, Long departmentId) {}
