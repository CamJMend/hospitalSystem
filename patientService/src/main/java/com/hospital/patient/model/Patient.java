package com.hospital.patient.model;

import com.google.cloud.firestore.annotation.DocumentId;

public class Patient {
    
    @DocumentId
    private String id;
    private String name;
    private String lastName;
    private String dateOfBirth;
    private String gender;
    private String email;
    private String phone;
    private String address;
    private String medicalRecordNumber;
    private String insuranceInfo;
    
    // Default constructor required for Firestore
    public Patient() {
    }
    
    public Patient(String id, String name, String lastName, String dateOfBirth, 
                  String gender, String email, String phone, String address, 
                  String medicalRecordNumber, String insuranceInfo) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.medicalRecordNumber = medicalRecordNumber;
        this.insuranceInfo = insuranceInfo;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getMedicalRecordNumber() {
        return medicalRecordNumber;
    }
    
    public void setMedicalRecordNumber(String medicalRecordNumber) {
        this.medicalRecordNumber = medicalRecordNumber;
    }
    
    public String getInsuranceInfo() {
        return insuranceInfo;
    }
    
    public void setInsuranceInfo(String insuranceInfo) {
        this.insuranceInfo = insuranceInfo;
    }
    
    @Override
    public String toString() {
        return "Patient{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", gender='" + gender + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", medicalRecordNumber='" + medicalRecordNumber + '\'' +
                ", insuranceInfo='" + insuranceInfo + '\'' +
                '}';
    }
}