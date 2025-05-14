package com.hospital.patient.dto;

import com.hospital.patient.model.Patient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientDto {
    
    private Long id;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String firstName;
    
    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    private String lastName;
    
    @NotBlank(message = "El número de identificación es obligatorio")
    private String identificationNumber;
    
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    private LocalDate dateOfBirth;
    
    @Email(message = "El correo electrónico debe ser válido")
    private String email;
    
    @Size(max = 15, message = "El número de teléfono no debe exceder los 15 caracteres")
    private String phoneNumber;
    
    private String address;
    
    @Size(max = 1000, message = "El historial médico no debe exceder los 1000 caracteres")
    private String medicalHistory;
    
    private Patient.BloodType bloodType;
    
    private Long userId;
}