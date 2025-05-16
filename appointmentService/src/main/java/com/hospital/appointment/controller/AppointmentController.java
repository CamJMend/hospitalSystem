package com.hospital.appointment.controller;

import com.hospital.appointment.model.Appointment;
import com.hospital.appointment.service.AppointmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    
    private static final Logger log = LoggerFactory.getLogger(AppointmentController.class);
    private final AppointmentService appointmentService;
    
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO', 'PACIENTE')")
    public ResponseEntity<Appointment> createAppointment(@RequestBody Appointment appointment, Authentication auth) {
        try {
            if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PACIENTE"))) {
                appointment.setPatientId(auth.getName());
            }
            
            Appointment createdAppointment = appointmentService.createAppointment(appointment);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAppointment);
        } catch (Exception e) {
            log.error("Error creating appointment", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO', 'PACIENTE')")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable String id, Authentication auth) {
        try {
            Appointment appointment = appointmentService.getAppointmentById(id);
            
            if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PACIENTE"))) {
                if (!appointment.getPatientId().equals(auth.getName())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }
            
            return ResponseEntity.ok(appointment);
        } catch (Exception e) {
            log.error("Error getting appointment", e);
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO', 'PACIENTE')")
    public ResponseEntity<List<Appointment>> getAppointmentsByPatient(@PathVariable String patientId, Authentication auth) {
        try {
            if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PACIENTE"))) {
                if (!patientId.equals(auth.getName())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }
            
            List<Appointment> appointments = appointmentService.getAppointmentsByPatientId(patientId);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            log.error("Error getting appointments by patient", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO')")
    public ResponseEntity<List<Appointment>> getAppointmentsByDoctor(@PathVariable String doctorId, Authentication auth) {
        try {
            if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MEDICO"))) {
                if (!doctorId.equals(auth.getName())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }
            
            List<Appointment> appointments = appointmentService.getAppointmentsByDoctorId(doctorId);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            log.error("Error getting appointments by doctor", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO')")
    public ResponseEntity<List<Appointment>> getAppointmentsByDateRange(
            @RequestParam String start,
            @RequestParam String end) {
        try {
            List<Appointment> appointments = appointmentService.getAppointmentsByDateRange(start, end);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            log.error("Error getting appointments by date range", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO')")
    public ResponseEntity<Appointment> updateAppointment(@PathVariable String id, @RequestBody Appointment appointment) {
        try {
            Appointment updatedAppointment = appointmentService.updateAppointment(id, appointment);
            return ResponseEntity.ok(updatedAppointment);
        } catch (Exception e) {
            log.error("Error updating appointment", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO', 'PACIENTE')")
    public ResponseEntity<Void> cancelAppointment(@PathVariable String id, 
                                                @RequestBody Map<String, String> request,
                                                Authentication auth) {
        try {
            Appointment appointment = appointmentService.getAppointmentById(id);
            
            if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PACIENTE"))) {
                if (!appointment.getPatientId().equals(auth.getName())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }
            
            String reason = request.get("reason");
            appointmentService.cancelAppointment(id, reason);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error cancelling appointment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/{id}/complete")
    @PreAuthorize("hasRole('MEDICO')")
    public ResponseEntity<Void> completeAppointment(@PathVariable String id, 
                                                  @RequestBody Map<String, String> request,
                                                  Authentication auth) {
        try {
            Appointment appointment = appointmentService.getAppointmentById(id);
            
            if (!appointment.getDoctorId().equals(auth.getName())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            String notes = request.get("notes");
            appointmentService.completeAppointment(id, notes);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error completing appointment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}