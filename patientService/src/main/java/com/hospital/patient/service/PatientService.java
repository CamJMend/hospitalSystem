package com.hospital.patient.service;

import com.hospital.patient.dto.PatientDto;
import com.hospital.patient.dto.PatientResponseDto;
import com.hospital.patient.model.Patient;

import java.util.List;

public interface PatientService {
    
    PatientResponseDto createPatient(PatientDto patientDto);
    
    PatientResponseDto getPatientById(Long id);
    
    PatientResponseDto getPatientByIdentificationNumber(String identificationNumber);
    
    List<PatientResponseDto> getAllPatients();
    
    List<PatientResponseDto> searchPatientsByLastName(String lastName);
    
    List<PatientResponseDto> getPatientsByUserId(Long userId);
    
    PatientResponseDto updatePatient(Long id, PatientDto patientDto);
    
    void deletePatient(Long id);
    
    PatientResponseDto activatePatient(Long id);
}