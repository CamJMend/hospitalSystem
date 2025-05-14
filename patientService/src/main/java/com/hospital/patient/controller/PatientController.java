package com.hospital.patient.controller;

import com.hospital.patient.dto.PatientDto;
import com.hospital.patient.dto.PatientResponseDto;
import com.hospital.patient.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_DOCTOR')")
    public ResponseEntity<PatientResponseDto> createPatient(@Valid @RequestBody PatientDto patientDto) {
        PatientResponseDto createdPatient = patientService.createPatient(patientDto);
        return new ResponseEntity<>(createdPatient, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_DOCTOR', 'ROLE_PATIENT')")
    public ResponseEntity<PatientResponseDto> getPatientById(@PathVariable Long id, HttpServletRequest request) {
        // Verificar si es un paciente consultando su propio registro
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PATIENT"))) {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            // Si es un paciente, verificar que está accediendo a sus propios datos
            // obtener el paciente para verificar el userId
            PatientResponseDto patient = patientService.getPatientById(id);
            
            // Si el userId del token no coincide con el userId del paciente, denegar acceso
            if (!userId.equals(patient.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        
        // Si es admin, doctor o el propio paciente, permitir acceso
        PatientResponseDto patient = patientService.getPatientById(id);
        return ResponseEntity.ok(patient);
    }

    @GetMapping("/identification/{identificationNumber}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_DOCTOR')")
    public ResponseEntity<PatientResponseDto> getPatientByIdentificationNumber(
            @PathVariable String identificationNumber) {
        PatientResponseDto patient = patientService.getPatientByIdentificationNumber(identificationNumber);
        return ResponseEntity.ok(patient);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_DOCTOR')")
    public ResponseEntity<List<PatientResponseDto>> getAllPatients() {
        List<PatientResponseDto> patients = patientService.getAllPatients();
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_DOCTOR')")
    public ResponseEntity<List<PatientResponseDto>> searchPatientsByLastName(
            @RequestParam("lastName") String lastName) {
        List<PatientResponseDto> patients = patientService.searchPatientsByLastName(lastName);
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/user")
    @PreAuthorize("hasAnyAuthority('ROLE_PATIENT')")
    public ResponseEntity<List<PatientResponseDto>> getPatientsByUserId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<PatientResponseDto> patients = patientService.getPatientsByUserId(userId);
        return ResponseEntity.ok(patients);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_DOCTOR')")
    public ResponseEntity<PatientResponseDto> updatePatient(
            @PathVariable Long id, @Valid @RequestBody PatientDto patientDto) {
        PatientResponseDto updatedPatient = patientService.updatePatient(id, patientDto);
        return ResponseEntity.ok(updatedPatient);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<PatientResponseDto> activatePatient(@PathVariable Long id) {
        PatientResponseDto activatedPatient = patientService.activatePatient(id);
        return ResponseEntity.ok(activatedPatient);
    }
    
    // Endpoint público para validar que el servicio está funcionando
    @GetMapping("/public/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Patient Service is running!");
    }
}