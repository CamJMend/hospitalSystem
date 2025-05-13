package com.hospital.patient.firebase;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.hospital.patient.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Repository
public class PatientFirebaseRepository {

    private final Firestore firestore;
    private final CollectionReference patientsCollection;

    @Autowired
    public PatientFirebaseRepository(Firestore firestore) {
        this.firestore = firestore;
        this.patientsCollection = firestore.collection("patients");
    }

    public String save(Patient patient) {
        if (patient.getId() == null || patient.getId().isEmpty()) {
            // Create new document with auto-generated ID
            DocumentReference docRef = patientsCollection.document();
            patient.setId(docRef.getId());
            docRef.set(patient);
            return docRef.getId();
        } else {
            // Update existing document
            DocumentReference docRef = patientsCollection.document(patient.getId());
            docRef.set(patient);
            return patient.getId();
        }
    }

    public Optional<Patient> findById(String id) {
        try {
            DocumentReference docRef = patientsCollection.document(id);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();
            
            if (document.exists()) {
                return Optional.of(document.toObject(Patient.class));
            } else {
                return Optional.empty();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error finding patient", e);
        }
    }

    public List<Patient> findAll() {
        try {
            ApiFuture<QuerySnapshot> future = patientsCollection.get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            List<Patient> patients = new ArrayList<>();
            for (QueryDocumentSnapshot document : documents) {
                patients.add(document.toObject(Patient.class));
            }
            return patients;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error retrieving patients", e);
        }
    }

    public void deleteById(String id) {
        patientsCollection.document(id).delete();
    }

    public List<Patient> findByName(String name) {
        try {
            Query query = patientsCollection.whereEqualTo("name", name);
            ApiFuture<QuerySnapshot> future = query.get();
            
            List<Patient> patients = new ArrayList<>();
            for (DocumentSnapshot document : future.get().getDocuments()) {
                patients.add(document.toObject(Patient.class));
            }
            return patients;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error finding patients by name", e);
        }
    }
}