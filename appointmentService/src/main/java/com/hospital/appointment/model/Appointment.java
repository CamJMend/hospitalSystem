package com.hospital.appointment.model;

import java.time.LocalDateTime;

public class Appointment {
    private String id;
    private String patientId;
    private String patientName;
    private String doctorId;
    private String doctorName;
    private String dateTime;
    private Integer duration; // en minutos
    private String status; // SCHEDULED, CONFIRMED, CANCELLED, COMPLETED
    private String reason;
    private String notes;
    private String specialty;
    private String createdAt;
    private String updatedAt;
    
    public Appointment() {}
    
    public Appointment(String id, String patientId, String patientName, String doctorId, 
                      String doctorName, String dateTime, Integer duration, String status,
                      String reason, String notes, String specialty, String createdAt, String updatedAt) {
        this.id = id;
        this.patientId = patientId;
        this.patientName = patientName;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.dateTime = dateTime;
        this.duration = duration;
        this.status = status;
        this.reason = reason;
        this.notes = notes;
        this.specialty = specialty;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters
    public String getId() { return id; }
    public String getPatientId() { return patientId; }
    public String getPatientName() { return patientName; }
    public String getDoctorId() { return doctorId; }
    public String getDoctorName() { return doctorName; }
    public String getDateTime() { return dateTime; }
    public Integer getDuration() { return duration; }
    public String getStatus() { return status; }
    public String getReason() { return reason; }
    public String getNotes() { return notes; }
    public String getSpecialty() { return specialty; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    
    // Setters
    public void setId(String id) { this.id = id; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }
    public void setDuration(Integer duration) { this.duration = duration; }
    public void setStatus(String status) { this.status = status; }
    public void setReason(String reason) { this.reason = reason; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}