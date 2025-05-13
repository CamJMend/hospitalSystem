package com.hospital.patient.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.hospital.patient.model.Patient;
import com.hospital.patient.proto.Patient.Builder;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PatientEventProducer {

    @Value("${kafka.topic.patient-created}")
    private String patientCreatedTopic;
    
    @Value("${kafka.topic.patient-updated}")
    private String patientUpdatedTopic;
    
    @Value("${kafka.topic.patient-deleted}")
    private String patientDeletedTopic;
    
    @Autowired
    private KafkaTemplate<String, byte[]> kafkaTemplate;
    
    public void sendPatientCreatedEvent(Patient patient) {
        try {
            // Convertir el objeto de paciente a Protobuf
            com.hospital.patient.proto.Patient protoPatient = patient.toProto();
            
            // Enviar el mensaje a Kafka
            kafkaTemplate.send(patientCreatedTopic, patient.getId(), protoPatient.toByteArray());
            log.info("Patient created event sent to Kafka for patient: {}", patient.getId());
        } catch (Exception e) {
            log.error("Error sending patient created event to Kafka", e);
        }
    }
    
    public void sendPatientUpdatedEvent(Patient patient) {
        try {
            // Convertir el objeto de paciente a Protobuf
            com.hospital.patient.proto.Patient protoPatient = patient.toProto();
            
            // Enviar el mensaje a Kafka
            kafkaTemplate.send(patientUpdatedTopic, patient.getId(), protoPatient.toByteArray());
            log.info("Patient updated event sent to Kafka for patient: {}", patient.getId());
        } catch (Exception e) {
            log.error("Error sending patient updated event to Kafka", e);
        }
    }
    
    public void sendPatientDeletedEvent(String patientId) {
        try {
            // Para eliminar, solo necesitamos enviar el ID del paciente
            kafkaTemplate.send(patientDeletedTopic, patientId, patientId.getBytes());
            log.info("Patient deleted event sent to Kafka for patient: {}", patientId);
        } catch (Exception e) {
            log.error("Error sending patient deleted event to Kafka", e);
        }
    }
}