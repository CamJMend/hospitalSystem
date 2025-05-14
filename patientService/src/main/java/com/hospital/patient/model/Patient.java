package com.hospital.patient.model;

public class Patient {
    private String id;
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String dateOfBirth; // Cambiado de LocalDate a String
    private String gender;
    private String address;
    private String bloodType;
    private String allergies;
    private String medicalHistory;
    private String emergencyContact;
    private String emergencyPhone;
    
    public Patient() {}
    
    // Constructor con todos los campos
    public Patient(String id, String userId, String firstName, String lastName, 
                  String email, String phone, String dateOfBirth, String gender,
                  String address, String bloodType, String allergies, String medicalHistory,
                  String emergencyContact, String emergencyPhone) {
        this.id = id;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.address = address;
        this.bloodType = bloodType;
        this.allergies = allergies;
        this.medicalHistory = medicalHistory;
        this.emergencyContact = emergencyContact;
        this.emergencyPhone = emergencyPhone;
    }
    
    // Getters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getDateOfBirth() { return dateOfBirth; }
    public String getGender() { return gender; }
    public String getAddress() { return address; }
    public String getBloodType() { return bloodType; }
    public String getAllergies() { return allergies; }
    public String getMedicalHistory() { return medicalHistory; }
    public String getEmergencyContact() { return emergencyContact; }
    public String getEmergencyPhone() { return emergencyPhone; }
    
    // Setters
    public void setId(String id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public void setGender(String gender) { this.gender = gender; }
    public void setAddress(String address) { this.address = address; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }
    public void setAllergies(String allergies) { this.allergies = allergies; }
    public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }
    public void setEmergencyPhone(String emergencyPhone) { this.emergencyPhone = emergencyPhone; }
}