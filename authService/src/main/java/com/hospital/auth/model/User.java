package com.hospital.auth.model;

public class User {
    private String uid;
    private String email;
    private String password;
    private String role; // paciente, medico, admin
    private String firstName;
    private String lastName;
    private boolean enabled;
    
    public User() {}
    
    public User(String uid, String email, String password, String role, 
                String firstName, String lastName, boolean enabled) {
        this.uid = uid;
        this.email = email;
        this.password = password;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.enabled = enabled;
    }
    
    // Getters
    public String getUid() {return uid;}
    public String getEmail() {return email;}
    public String getPassword() {return password;}
    public String getRole() {return role;}
    public String getFirstName() {return firstName;}
    public String getLastName() {return lastName;}
    public boolean isEnabled() {return enabled;}
    

    // Setters
    public void setUid(String uid) {this.uid = uid;}
    public void setEmail(String email) {this.email = email;}
    public void setPassword(String password) {this.password = password;}
    public void setRole(String role) {this.role = role;}
    public void setFirstName(String firstName) {this.firstName = firstName;}
    public void setLastName(String lastName) {this.lastName = lastName;}
    public void setEnabled(boolean enabled) {this.enabled = enabled;}
}