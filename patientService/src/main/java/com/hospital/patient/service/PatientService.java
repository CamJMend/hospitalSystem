package com.hospital.patient.service;

import com.hospital.patient.firebase.PatientFirebaseRepository;
import com.hospital.patient.kafka.PatientEventProducer;
import com.hospital.patient.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    private final PatientFirebaseRepository patientRepository;
    private final PatientEventProducer eventProducer;

    @Autowired
    public PatientService(PatientFirebaseRepository patientRepository, PatientEventProducer eventProducer) {
        this.patientRepository = patientRepository;
        this.eventProducer = eventProducer;
    }

    public String createPatient(Patient patient) {
        String patientId = patientRepository.save(patient);
        // Send event to Kafka about new patient creation
        eventProducer.sendPatientCreatedEvent(patient);
        return patientId;
    }

    public Optional<Patient> getPatientById(String id) {
        return patientRepository.findById(id);
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public String updatePatient(Patient patient) {
        Optional<Patient> existingPatient = patientRepository.findById(patient.getId());
        if (existingPatient.isPresent()) {
            String patientId = patientRepository.save(patient);
            // Send event to Kafka about patient update
            eventProducer.sendPatientUpdatedEvent(patient);
            return patientId;
        }
        return null; // Or throw exception if patient doesn't exist
    }

    public void deletePatient(String id) {
        Optional<Patient> patient = patientRepository.findById(id);
        if (patient.isPresent()) {
            patientRepository.deleteById(id);
            // Send event to Kafka about patient deletion
            eventProducer.sendPatientDeletedEvent(patient.get());
        }
    }

    public List<Patient> getPatientsByName(String name) {
        return patientRepository.findByName(name);
    }
}