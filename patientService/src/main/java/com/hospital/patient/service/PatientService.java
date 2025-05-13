package com.hospital.patient.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hospital.patient.kafka.PatientEventProducer;
import com.hospital.patient.model.Patient;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PatientService {

    private final Map<String, Patient> patientRepository = new HashMap<>();
    
    @Autowired
    private PatientEventProducer eventProducer;
    
    public Patient createPatient(Patient patient) {
        String patientId = UUID.randomUUID().toString();
        patient.setId(patientId);
        
        patientRepository.put(patientId, patient);
        log.info("Patient created: {}", patient);
        
        // Publicar evento en Kafka
        eventProducer.sendPatientCreatedEvent(patient);
        
        return patient;
    }
    
    public Patient getPatient(String id) {
        return patientRepository.get(id);
    }
    
    public Patient updatePatient(Patient patient) {
        if (patientRepository.containsKey(patient.getId())) {
            patientRepository.put(patient.getId(), patient);
            log.info("Patient updated: {}", patient);
            
            // Publicar evento en Kafka
            eventProducer.sendPatientUpdatedEvent(patient);
            
            return patient;
        }
        return null;
    }
    
    public boolean deletePatient(String id) {
        Patient patient = patientRepository.remove(id);
        if (patient != null) {
            log.info("Patient deleted with ID: {}", id);
            
            // Publicar evento en Kafka
            eventProducer.sendPatientDeletedEvent(id);
            
            return true;
        }
        return false;
    }
    
    public List<Patient> getAllPatients(int page, int size) {
        List<Patient> patients = new ArrayList<>(patientRepository.values());
        
        int start = page * size;
        int end = Math.min(start + size, patients.size());
        
        if (start > patients.size()) {
            return new ArrayList<>();
        }
        
        return patients.subList(start, end);
    }
    
    public int getTotalPatientsCount() {
        return patientRepository.size();
    }
}