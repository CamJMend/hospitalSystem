package com.hospital.appointment.service;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.hospital.appointment.model.Appointment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class AppointmentService {
    
    private static final Logger log = LoggerFactory.getLogger(AppointmentService.class);
    private final Firestore firestore;
    private static final String COLLECTION_NAME = "appointments";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    public AppointmentService(Firestore firestore) {
        this.firestore = firestore;
    }
    
    public Appointment createAppointment(Appointment appointment) throws ExecutionException, InterruptedException {
        appointment.setId(UUID.randomUUID().toString());
        appointment.setStatus("SCHEDULED");
        appointment.setCreatedAt(LocalDateTime.now().format(formatter));
        appointment.setUpdatedAt(LocalDateTime.now().format(formatter));
        
        // Verificar disponibilidad del doctor
        if (!isDoctorAvailable(appointment.getDoctorId(), appointment.getDateTime(), appointment.getDuration())) {
            throw new RuntimeException("El doctor no está disponible en ese horario");
        }
        
        firestore.collection(COLLECTION_NAME)
                .document(appointment.getId())
                .set(appointment)
                .get();
        
        log.info("Appointment created with ID: {}", appointment.getId());
        return appointment;
    }
    
    public Appointment getAppointmentById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        Appointment appointment = docRef.get().get().toObject(Appointment.class);
        
        if (appointment == null) {
            throw new RuntimeException("Appointment not found with ID: " + id);
        }
        
        return appointment;
    }
    
    public List<Appointment> getAppointmentsByPatientId(String patientId) throws ExecutionException, InterruptedException {
        QuerySnapshot querySnapshot = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("patientId", patientId)
                .orderBy("dateTime", Query.Direction.DESCENDING)
                .get()
                .get();
        
        return querySnapshot.getDocuments().stream()
                .map(doc -> doc.toObject(Appointment.class))
                .collect(Collectors.toList());
    }
    
    public List<Appointment> getAppointmentsByDoctorId(String doctorId) throws ExecutionException, InterruptedException {
        QuerySnapshot querySnapshot = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("doctorId", doctorId)
                .orderBy("dateTime", Query.Direction.DESCENDING)
                .get()
                .get();
        
        return querySnapshot.getDocuments().stream()
                .map(doc -> doc.toObject(Appointment.class))
                .collect(Collectors.toList());
    }
    
    public List<Appointment> getAppointmentsByDateRange(String start, String end) 
            throws ExecutionException, InterruptedException {
        QuerySnapshot querySnapshot = firestore.collection(COLLECTION_NAME)
                .whereGreaterThanOrEqualTo("dateTime", start)
                .whereLessThanOrEqualTo("dateTime", end)
                .orderBy("dateTime", Query.Direction.ASCENDING)
                .get()
                .get();
        
        return querySnapshot.getDocuments().stream()
                .map(doc -> doc.toObject(Appointment.class))
                .collect(Collectors.toList());
    }
    
    public Appointment updateAppointment(String id, Appointment appointment) throws ExecutionException, InterruptedException {
        appointment.setId(id);
        appointment.setUpdatedAt(LocalDateTime.now().format(formatter));
        
        // Si se está cambiando la fecha/hora, verificar disponibilidad
        if (appointment.getDateTime() != null) {
            Appointment existingAppointment = getAppointmentById(id);
            if (!existingAppointment.getDateTime().equals(appointment.getDateTime())) {
                if (!isDoctorAvailable(appointment.getDoctorId(), appointment.getDateTime(), appointment.getDuration())) {
                    throw new RuntimeException("El doctor no está disponible en ese horario");
                }
            }
        }
        
        firestore.collection(COLLECTION_NAME)
                .document(id)
                .set(appointment)
                .get();
        
        log.info("Appointment updated with ID: {}", id);
        return appointment;
    }
    
    public void cancelAppointment(String id, String cancellationReason) throws ExecutionException, InterruptedException {
        Appointment appointment = getAppointmentById(id);
        appointment.setStatus("CANCELLED");
        appointment.setNotes(appointment.getNotes() != null ? 
                appointment.getNotes() + " | Cancelado: " + cancellationReason : 
                "Cancelado: " + cancellationReason);
        appointment.setUpdatedAt(LocalDateTime.now().format(formatter));
        
        firestore.collection(COLLECTION_NAME)
                .document(id)
                .set(appointment)
                .get();
        
        log.info("Appointment cancelled with ID: {}", id);
    }
    
    public void completeAppointment(String id, String notes) throws ExecutionException, InterruptedException {
        Appointment appointment = getAppointmentById(id);
        appointment.setStatus("COMPLETED");
        appointment.setNotes(notes);
        appointment.setUpdatedAt(LocalDateTime.now().format(formatter));
        
        firestore.collection(COLLECTION_NAME)
                .document(id)
                .set(appointment)
                .get();
        
        log.info("Appointment completed with ID: {}", id);
    }
    
    private boolean isDoctorAvailable(String doctorId, String dateTime, Integer duration) 
            throws ExecutionException, InterruptedException {
        LocalDateTime requestedTime = LocalDateTime.parse(dateTime, formatter);
        LocalDateTime endTime = requestedTime.plusMinutes(duration);
        
        QuerySnapshot querySnapshot = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("doctorId", doctorId)
                .whereIn("status", List.of("SCHEDULED", "CONFIRMED"))
                .get()
                .get();
        
        for (var doc : querySnapshot.getDocuments()) {
            Appointment existingAppointment = doc.toObject(Appointment.class);
            LocalDateTime existingStart = LocalDateTime.parse(existingAppointment.getDateTime(), formatter);
            LocalDateTime existingEnd = existingStart.plusMinutes(existingAppointment.getDuration());
            
            if (!(endTime.isBefore(existingStart) || requestedTime.isAfter(existingEnd))) {
                return false;
            }
        }
        
        return true;
    }
}