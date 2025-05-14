
Probar Auth

eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoibWVkaWNvIiwic3ViIjoiMjcwOGQxN2YtNGYxNC00YzljLTk1OWQtZjUyNmVlZDgyMzA4IiwiaWF0IjoxNzQ3MjU0MTA3LCJleHAiOjE3NDczNDA1MDd9.oXJB-c_UIalrfXmKVwKOmw3iUaU-D3VRbel-NSKpxuo

eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoicGFjaWVudGUiLCJzdWIiOiJkYzZjMmZiNC04YmIyLTQ2NmEtOGMxOC05ODRmZjQ3OWNkZWIiLCJpYXQiOjE3NDcyNTU4ODUsImV4cCI6MTc0NzM0MjI4NX0.f-GAONCdxCA0kxH1XlKi067WUzvW5tcuCr2zHD2Wcr0

``` bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "doctor@hospital.com",
    "password": "password123",
    "firstName": "Dr. Juan",
    "lastName": "Pérez",
    "role": "medico"
  }'


  curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "doctor@hospital.com",
    "password": "password123"
  }'


  curl -X POST http://localhost:8081/api/auth/validate \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

Probar Patients
```bash
curl -X POST http://localhost:8082/api/patients \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TU_TOKEN_AQUI" \
  -d '{
    "userId": "patient-001",
    "firstName": "María",
    "lastName": "González",
    "email": "maria.gonzalez@email.com",
    "phone": "+521234567890",
    "dateOfBirth": "1990-05-15",
    "gender": "F",
    "address": "Calle Principal 123, Zapopan, Jalisco",
    "bloodType": "A+",
    "allergies": "Penicilina",
    "medicalHistory": "Hipertensión leve",
    "emergencyContact": "Juan González",
    "emergencyPhone": "+520987654321"
  }

curl -X GET http://localhost:8082/api/patients \
  -H "Authorization: Bearer TU_TOKEN_AQUI"

curl -X GET http://localhost:8082/api/patients/ID_DEL_PACIENTE \
  -H "Authorization: Bearer TU_TOKEN_AQUI"

curl -X GET http://localhost:8082/api/patients/user/patient-001 \
  -H "Authorization: Bearer TU_TOKEN_AQUI"

curl -X PUT http://localhost:8082/api/patients/ID_DEL_PACIENTE \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TU_TOKEN_AQUI" \
  -d '{
    "userId": "patient-001",
    "firstName": "María",
    "lastName": "González López",
    "email": "maria.gonzalez@email.com",
    "phone": "+521234567890",
    "dateOfBirth": "1990-05-15",
    "gender": "F",
    "address": "Calle Principal 123, Zapopan, Jalisco",
    "bloodType": "A+",
    "allergies": "Penicilina, Aspirina",
    "medicalHistory": "Hipertensión leve, Asma",
    "emergencyContact": "Juan González",
    "emergencyPhone": "+520987654321"
  }


curl -X DELETE http://localhost:8082/api/patients/ID_DEL_PACIENTE \
  -H "Authorization: Bearer TU_TOKEN_AQUI"

# 8. Probar acceso sin token (debe fallar)
curl -X GET http://localhost:8082/api/patients

# 9. Crear un usuario paciente
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "paciente1@email.com",
    "password": "password123",
    "firstName": "Pedro",
    "lastName": "López",
    "role": "paciente"
  }'

# 10. Login como paciente
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "paciente1@email.com",
    "password": "password123"
  }'

# 11. Intentar crear un paciente como rol paciente (debe fallar)
curl -X POST http://localhost:8082/api/patients \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN_DEL_PACIENTE" \
  -d '{
    "userId": "patient-003",
    "firstName": "Test",
    "lastName": "Test",
    "email": "test@email.com"
  }'

  # 2. Crear cita usando el ID del paciente existente
echo "2. Creando cita para paciente existente..."
curl -X POST "$APPOINTMENT_URL" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "patientId": "1dee65f4-6298-40aa-a941-9cd1182bb307",
    "patientName": "María González",
    "doctorId": "doctor-uid-123",
    "doctorName": "Dr. Juan Pérez",
    "dateTime": "2025-01-20T10:00:00",
    "duration": 30,
    "reason": "Consulta general",
    "specialty": "Medicina General",
    "notes": "Primera visita"
  }

echo -e "\n"

# 3. Obtener citas del paciente
echo "3. Obteniendo citas del paciente..."
curl -X GET "$APPOINTMENT_URL/patient/1dee65f4-6298-40aa-a941-9cd1182bb307" \
  -H "Authorization: Bearer $TOKEN" | jq '.' 2>/dev/null || cat

echo -e "\n"

# 4. También puedes obtener el paciente por su userId y usar ese ID
echo "4. Obteniendo paciente por userId para usar en citas..."
PATIENT_RESPONSE=$(curl -s -X GET "$PATIENT_URL/user/patient-001" \
  -H "Authorization: Bearer $TOKEN")

PATIENT_ID=$(echo $PATIENT_RESPONSE | grep -o '"id":"[^"]*' | sed 's/"id":"//')
echo "Patient ID encontrado: $PATIENT_ID"
echo ""

# 5. Crear otra cita usando el userId para buscar
echo "5. Creando segunda cita usando el patientId recuperado..."
curl -X POST "$APPOINTMENT_URL" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"patientId\": \"$PATIENT_ID\",
    \"patientName\": \"María González\",
    \"doctorId\": \"doctor-uid-123\",
    \"doctorName\": \"Dr. Juan Pérez\",
    \"dateTime\": \"2025-01-21T11:00:00\",
    \"duration\": 45,
    \"reason\": \"Revisión de resultados\",
    \"specialty\": \"Medicina General\",
    \"notes\": \"Segunda visita - control\"
  }" | jq '.' 2>/dev/null || cat
```