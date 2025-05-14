package com.hospital.patient.dto;

import com.hospital.patient.model.Patient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientResponseDto {
    
    private Long id;
    private String firstName;
    private String lastName;
    private String identificationNumber;
    private LocalDate dateOfBirth;
    private String email;
    private String phoneNumber;
    private String address;
    private String medicalHistory;
    private Patient.BloodType bloodType;
    private boolean active;
    
    // Constructor para convertir de Patient a PatientResponseDto
    public static PatientResponseDto fromPatient(Patient patient) {
        return PatientResponseDto.builder()
                .id(patient.getId())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .identificationNumber(patient.getIdentificationNumber())
                .dateOfBirth(patient.getDateOfBirth())
                .email(patient.getEmail())
                .phoneNumber(patient.getPhoneNumber())
                .address(patient.getAddress())
                .medicalHistory(patient.getMedicalHistory())
                .bloodType(patient.getBloodType())
                .active(patient.isActive())
                .build();
    }
}