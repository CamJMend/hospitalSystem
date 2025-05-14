package com.hospital.patient.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.patient.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PatientEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topic.patient-created}")
    private String patientCreatedTopic;

    @Value("${kafka.topic.patient-updated}")
    private String patientUpdatedTopic;

    @Value("${kafka.topic.patient-deleted}")
    private String patientDeletedTopic;

    @Autowired
    public PatientEventProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendPatientCreatedEvent(Patient patient) {
        try {
            String patientJson = objectMapper.writeValueAsString(patient);
            kafkaTemplate.send(patientCreatedTopic, patient.getId(), patientJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing patient to JSON", e);
        }
    }

    public void sendPatientUpdatedEvent(Patient patient) {
        try {
            String patientJson = objectMapper.writeValueAsString(patient);
            kafkaTemplate.send(patientUpdatedTopic, patient.getId(), patientJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing patient to JSON", e);
        }
    }

    public void sendPatientDeletedEvent(Patient patient) {
        try {
            String patientJson = objectMapper.writeValueAsString(patient);
            kafkaTemplate.send(patientDeletedTopic, patient.getId(), patientJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing patient to JSON", e);
        }
    }
}