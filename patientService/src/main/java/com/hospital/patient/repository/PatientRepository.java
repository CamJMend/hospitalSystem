package com.hospital.patient.repository;

import com.hospital.patient.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    
    Optional<Patient> findByIdentificationNumber(String identificationNumber);
    
    List<Patient> findByLastNameContainingIgnoreCase(String lastName);
    
    List<Patient> findByUserIdAndActiveTrue(Long userId);
    
    boolean existsByIdentificationNumber(String identificationNumber);
    
    List<Patient> findByActive(boolean active);
}