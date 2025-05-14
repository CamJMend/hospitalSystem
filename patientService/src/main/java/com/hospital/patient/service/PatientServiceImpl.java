package com.hospital.patient.service;

import com.hospital.patient.dto.PatientDto;
import com.hospital.patient.dto.PatientResponseDto;
import com.hospital.patient.exception.ResourceNotFoundException;
import com.hospital.patient.model.Patient;
import com.hospital.patient.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    @Override
    @Transactional
    public PatientResponseDto createPatient(PatientDto patientDto) {
        // Verificar si ya existe un paciente con el mismo número de identificación
        if (patientRepository.existsByIdentificationNumber(patientDto.getIdentificationNumber())) {
            throw new IllegalArgumentException("Ya existe un paciente con el número de identificación: " 
                    + patientDto.getIdentificationNumber());
        }
        
        // Mapear DTO a entidad
        Patient patient = mapToEntity(patientDto);
        Patient savedPatient = patientRepository.save(patient);
        
        // Mapear entidad a DTO de respuesta
        return PatientResponseDto.fromPatient(savedPatient);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponseDto getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", "id", id));
        
        return PatientResponseDto.fromPatient(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponseDto getPatientByIdentificationNumber(String identificationNumber) {
        Patient patient = patientRepository.findByIdentificationNumber(identificationNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", "identificationNumber", identificationNumber));
        
        return PatientResponseDto.fromPatient(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientResponseDto> getAllPatients() {
        List<Patient> patients = patientRepository.findByActive(true);
        
        return patients.stream()
                .map(PatientResponseDto::fromPatient)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientResponseDto> searchPatientsByLastName(String lastName) {
        List<Patient> patients = patientRepository.findByLastNameContainingIgnoreCase(lastName);
        
        return patients.stream()
                .filter(Patient::isActive)
                .map(PatientResponseDto::fromPatient)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientResponseDto> getPatientsByUserId(Long userId) {
        List<Patient> patients = patientRepository.findByUserIdAndActiveTrue(userId);
        
        return patients.stream()
                .map(PatientResponseDto::fromPatient)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PatientResponseDto updatePatient(Long id, PatientDto patientDto) {
        // Verificar si el paciente existe
        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", "id", id));
        
        // Verificar si se está cambiando el número de identificación y si ya existe
        if (!existingPatient.getIdentificationNumber().equals(patientDto.getIdentificationNumber()) &&
                patientRepository.existsByIdentificationNumber(patientDto.getIdentificationNumber())) {
            throw new IllegalArgumentException("Ya existe un paciente con el número de identificación: " 
                    + patientDto.getIdentificationNumber());
        }
        
        // Actualizar los campos del paciente
        existingPatient.setFirstName(patientDto.getFirstName());
        existingPatient.setLastName(patientDto.getLastName());
        existingPatient.setIdentificationNumber(patientDto.getIdentificationNumber());
        existingPatient.setDateOfBirth(patientDto.getDateOfBirth());
        existingPatient.setEmail(patientDto.getEmail());
        existingPatient.setPhoneNumber(patientDto.getPhoneNumber());
        existingPatient.setAddress(patientDto.getAddress());
        existingPatient.setMedicalHistory(patientDto.getMedicalHistory());
        existingPatient.setBloodType(patientDto.getBloodType());
        
        // Si hay un cambio en userId, actualizarlo
        if (patientDto.getUserId() != null) {
            existingPatient.setUserId(patientDto.getUserId());
        }
        
        // Guardar los cambios
        Patient updatedPatient = patientRepository.save(existingPatient);
        
        return PatientResponseDto.fromPatient(updatedPatient);
    }

    @Override
    @Transactional
    public void deletePatient(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", "id", id));
        
        // Soft delete: marcar como inactivo en lugar de eliminar
        patient.setActive(false);
        patientRepository.save(patient);
    }

    @Override
    @Transactional
    public PatientResponseDto activatePatient(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", "id", id));
        
        patient.setActive(true);
        Patient activatedPatient = patientRepository.save(patient);
        
        return PatientResponseDto.fromPatient(activatedPatient);
    }
    
    // Método auxiliar para mapear de DTO a entidad
    private Patient mapToEntity(PatientDto patientDto) {
        return Patient.builder()
                .id(patientDto.getId())
                .firstName(patientDto.getFirstName())
                .lastName(patientDto.getLastName())
                .identificationNumber(patientDto.getIdentificationNumber())
                .dateOfBirth(patientDto.getDateOfBirth())
                .email(patientDto.getEmail())
                .phoneNumber(patientDto.getPhoneNumber())
                .address(patientDto.getAddress())
                .medicalHistory(patientDto.getMedicalHistory())
                .bloodType(patientDto.getBloodType())
                .userId(patientDto.getUserId())
                .active(true)
                .build();
    }
}