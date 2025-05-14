package com.hospital.patient.service;

import com.hospital.patient.firebase.PatientFirebaseRepository;
import com.hospital.patient.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    private final PatientFirebaseRepository patientRepository;

    @Autowired
    public PatientService(PatientFirebaseRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public String createPatient(Patient patient) {
        return patientRepository.save(patient);
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
            return patientRepository.save(patient);
        }
        return null;
    }

    public void deletePatient(String id) {
        Optional<Patient> patient = patientRepository.findById(id);
        if (patient.isPresent()) {
            patientRepository.deleteById(id);
        }
    }

    public List<Patient> getPatientsByName(String name) {
        return patientRepository.findByName(name);
    }
}