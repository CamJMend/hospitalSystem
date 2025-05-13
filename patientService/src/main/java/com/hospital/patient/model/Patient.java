package com.hospital.patient.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Patient {
    private String id;
    private String name;
    private String lastName;
    private String identification;
    private String email;
    private String phone;
    private String address;
    private String birthDate;
    private String bloodType;
    
    // Método para convertir a objeto Protobuf
    public com.hospital.patient.proto.Patient toProto() {
        return com.hospital.patient.proto.Patient.newBuilder()
            .setId(id)
            .setName(name)
            .setLastName(lastName)
            .setIdentification(identification)
            .setEmail(email)
            .setPhone(phone)
            .setAddress(address)
            .setBirthDate(birthDate)
            .setBloodType(bloodType)
            .build();
    }
    
    // Método para convertir desde objeto Protobuf
    public static Patient fromProto(com.hospital.patient.proto.Patient proto) {
        return Patient.builder()
            .id(proto.getId())
            .name(proto.getName())
            .lastName(proto.getLastName())
            .identification(proto.getIdentification())
            .email(proto.getEmail())
            .phone(proto.getPhone())
            .address(proto.getAddress())
            .birthDate(proto.getBirthDate())
            .bloodType(proto.getBloodType())
            .build();
    }
}