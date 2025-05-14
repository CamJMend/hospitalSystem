package com.hospital.patient.service;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.hospital.patient.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class PatientService {
    
    private static final Logger log = LoggerFactory.getLogger(PatientService.class);
    private final Firestore firestore;
    private static final String COLLECTION_NAME = "patients";
    
    public PatientService(Firestore firestore) {
        this.firestore = firestore;
    }
    
    public Patient createPatient(Patient patient) throws ExecutionException, InterruptedException {
        patient.setId(UUID.randomUUID().toString());
        
        firestore.collection(COLLECTION_NAME)
                .document(patient.getId())
                .set(patient)
                .get();
        
        log.info("Patient created with ID: {}", patient.getId());
        return patient;
    }
    
    public Patient getPatientById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        Patient patient = docRef.get().get().toObject(Patient.class);
        
        if (patient == null) {
            throw new RuntimeException("Patient not found with ID: " + id);
        }
        
        return patient;
    }
    
    public Patient getPatientByUserId(String userId) throws ExecutionException, InterruptedException {
        QuerySnapshot querySnapshot = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .get()
                .get();
        
        if (querySnapshot.isEmpty()) {
            throw new RuntimeException("Patient not found with userId: " + userId);
        }
        
        return querySnapshot.getDocuments().get(0).toObject(Patient.class);
    }
    
    public List<Patient> getAllPatients() throws ExecutionException, InterruptedException {
        QuerySnapshot querySnapshot = firestore.collection(COLLECTION_NAME).get().get();
        
        return querySnapshot.getDocuments().stream()
                .map(doc -> doc.toObject(Patient.class))
                .collect(Collectors.toList());
    }
    
    public Patient updatePatient(String id, Patient patient) throws ExecutionException, InterruptedException {
        patient.setId(id);
        
        firestore.collection(COLLECTION_NAME)
                .document(id)
                .set(patient)
                .get();
        
        log.info("Patient updated with ID: {}", id);
        return patient;
    }
    
    public void deletePatient(String id) throws ExecutionException, InterruptedException {
        firestore.collection(COLLECTION_NAME)
                .document(id)
                .delete()
                .get();
        
        log.info("Patient deleted with ID: {}", id);
    }
}