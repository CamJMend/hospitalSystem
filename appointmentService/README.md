# Appointment Service - Explicación Detallada

## Componentes y su Funcionamiento

### AppointmentServiceApplication.java
- **Función**: Punto de entrada principal de la aplicación Spring Boot.
- **Cómo funciona**: Inicializa el contexto de Spring y arranca el servidor web en el puerto configurado (8083).

### FirebaseConfig.java
- **Función**: Configura la conexión con Firebase para almacenamiento.
- **Cómo funciona**: 
  - Lee el archivo de credenciales `firebase-config.json`
  - Inicializa la conexión a Firebase si no existe ya
  - Configura Firestore como base de datos
  - Expone el bean Firestore para inyección de dependencias

### SecurityConfig.java
- **Función**: Configura los aspectos de seguridad de la aplicación.
- **Cómo funciona**:
  - Configura CORS para permitir peticiones desde diferentes dominios
  - Deshabilita CSRF ya que usamos autenticación basada en tokens
  - Establece la política de sesiones como STATELESS
  - Configura que todos los endpoints de `/api/appointments/**` requieran autenticación
  - Registra el filtro JwtAuthFilter para interceptar y validar tokens
  - Habilita las anotaciones @PreAuthorize para control de acceso basado en roles

### JwtAuthFilter.java
- **Función**: Filtra todas las peticiones para verificar y validar el token JWT.
- **Cómo funciona**:
  - Intercepta cada petición HTTP
  - Busca el header "Authorization" con formato "Bearer {token}"
  - Valida el token usando la misma clave secreta que AuthService
  - Extrae el nombre de usuario (uid) y el rol del token
  - Establece un objeto Authentication en el SecurityContext
  - Asigna autoridades basadas en el rol (ROLE_ADMIN, ROLE_MEDICO, ROLE_PACIENTE)
  - Permite que la petición continúe si el token es válido

### Appointment.java
- **Función**: Define la estructura de datos para las citas médicas.
- **Cómo funciona**:
  - Define campos para asociar la cita con paciente y doctor (patientId, doctorId)
  - Incluye nombres para facilitar visualización sin joins (patientName, doctorName)
  - Mantiene información temporal como dateTime y duration
  - Implementa un sistema de estados (status): SCHEDULED, CONFIRMED, CANCELLED, COMPLETED
  - Almacena razón de la cita, notas médicas y especialidad
  - Registra metadatos como createdAt y updatedAt para auditoría

### AppointmentService.java
- **Función**: Implementa la lógica de negocio para gestión de citas.
- **Cómo funciona**:
  - **Crear cita**: 
    - Genera un ID único (UUID)
    - Verifica la disponibilidad del doctor en ese horario
    - Establece estado inicial como SCHEDULED
    - Almacena la cita en Firestore
  - **Obtener cita por ID**: 
    - Busca en Firestore usando el ID único de la cita
  - **Obtener citas por paciente**: 
    - Filtra citas por patientId
    - Ordena por fecha descendente
  - **Obtener citas por doctor**: 
    - Filtra citas por doctorId
    - Ordena por fecha descendente
  - **Obtener citas por rango de fechas**: 
    - Filtra citas que estén dentro de las fechas start y end
    - Ordena cronológicamente
  - **Actualizar cita**: 
    - Verifica disponibilidad si cambia la fecha/hora
    - Actualiza la cita manteniendo el mismo ID
  - **Cancelar cita**: 
    - Cambia el estado a CANCELLED
    - Registra la razón de cancelación en las notas
  - **Completar cita**: 
    - Cambia el estado a COMPLETED
    - Registra notas médicas del procedimiento
  - **Verificar disponibilidad**: 
    - Comprueba si hay solapamiento con otras citas del doctor
    - Tiene en cuenta la duración de las citas

### AppointmentController.java
- **Función**: Expone los endpoints REST para la gestión de citas.
- **Cómo funciona**:
  - Define rutas para las operaciones de gestión de citas
  - Implementa control de acceso basado en roles usando @PreAuthorize
  - Para pacientes, asegura que solo puedan ver y cancelar sus propias citas
  - Para médicos, verifica que solo completen citas que les pertenecen
  - Procesa datos JSON de entrada
  - Llama a los métodos apropiados en AppointmentService
  - Maneja errores y excepciones adecuadamente
  - Devuelve respuestas HTTP con JSON y códigos de estado apropiados

## Endpoints REST

### 1. Crear Cita
**Endpoint**: `POST /api/appointments`

**Permisos**: Roles ADMIN, MEDICO, PACIENTE

**Request Authorization**:
```
Authorization: Bearer {jwt_token}
```

**Request Body**:
```json
{
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
```

**Proceso**:
1. El controller verifica el rol del usuario
2. Si es PACIENTE, automáticamente asigna el patientId al ID del usuario
3. Valida el formato de los datos recibidos
4. Pasa la información al servicio
5. El servicio verifica la disponibilidad del doctor en ese horario
6. Si está disponible, asigna un ID único (UUID)
7. Establece el estado a "SCHEDULED"
8. Registra fechas de creación y actualización
9. Almacena la cita en Firestore
10. Retorna la cita creada con su ID

**Response (201 Created)**:

### 2. Obtener Cita por ID
**Endpoint**: `GET /api/appointments/{id}`

**Permisos**: Roles ADMIN, MEDICO, PACIENTE (con restricciones)

**Request Authorization**:
```
Authorization: Bearer {jwt_token}
```

**Proceso**:
1. El controller extrae el ID de la ruta
2. Verifica el rol del usuario
3. Si es PACIENTE, verifica que solo pueda acceder a sus propias citas
4. El servicio busca la cita en Firestore
5. Retorna la cita o un error 404 si no se encuentra

**Response (200 OK)**:
```json
{
  "id": "3fb87d92-7412-49aa-c062-8de3283cc419",
  "patientId": "1dee65f4-6298-40aa-a941-9cd1182bb307",
  "patientName": "María González",
  "doctorId": "doctor-uid-123",
  "doctorName": "Dr. Juan Pérez",
  "dateTime": "2025-01-20T10:00:00",
  "duration": 30,
  "status": "SCHEDULED",
  "reason": "Consulta general",
  "specialty": "Medicina General",
  "notes": "Primera visita",
  "createdAt": "2025-01-15T15:30:45",
  "updatedAt": "2025-01-15T15:30:45"
}
```

### 3. Obtener Citas por Paciente
**Endpoint**: `GET /api/appointments/patient/{patientId}`

**Permisos**: Roles ADMIN, MEDICO, PACIENTE (con restricciones)

**Request Authorization**:
```
Authorization: Bearer {jwt_token}
```

**Proceso**:
1. El controller extrae el patientId de la ruta
2. Verifica el rol del usuario
3. Si es PACIENTE, verifica que solo pueda acceder a sus propias citas
4. El servicio busca en Firestore filtrando por patientId
5. Ordena las citas por fecha descendente
6. Retorna la lista de citas

**Response (200 OK)**:
```json
[
  {
    "id": "3fb87d92-7412-49aa-c062-8de3283cc419",
    "patientId": "1dee65f4-6298-40aa-a941-9cd1182bb307",
    "patientName": "María González",
    "doctorId": "doctor-uid-123",
    "doctorName": "Dr. Juan Pérez",
    "dateTime": "2025-01-20T10:00:00",
    "status": "SCHEDULED",
    // ...
  },
  {
    "id": "5cd98e03-8523-50bb-d173-9ef4394dd520",
    "patientId": "1dee65f4-6298-40aa-a941-9cd1182bb307",
    "patientName": "María González",
    "doctorId": "doctor-uid-123",
    "doctorName": "Dr. Juan Pérez",
    "dateTime": "2025-01-25T15:00:00",
    "status": "SCHEDULED",
    // ...
  }
]
```

### 4. Obtener Citas por Doctor
**Endpoint**: `GET /api/appointments/doctor/{doctorId}`

**Permisos**: Roles ADMIN, MEDICO (con restricciones)

**Request Authorization**:
```
Authorization: Bearer {jwt_token}
```

**Proceso**:
1. El controller extrae el doctorId de la ruta
2. Verifica el rol del usuario
3. Si es MEDICO, verifica que solo pueda acceder a sus propias citas
4. El servicio busca en Firestore filtrando por doctorId
5. Ordena las citas por fecha descendente
6. Retorna la lista de citas

**Response (200 OK)**:
```json
[
  {
    "id": "3fb87d92-7412-49aa-c062-8de3283cc419",
    "patientId": "1dee65f4-6298-40aa-a941-9cd1182bb307",
    "patientName": "María González",
    "doctorId": "doctor-uid-123",
    "doctorName": "Dr. Juan Pérez",
    "dateTime": "2025-01-20T10:00:00",
    "status": "SCHEDULED",
    // otros campos...
  },
  // otras citas...
]
```

### 5. Obtener Citas por Rango de Fechas
**Endpoint**: `GET /api/appointments?start=2025-01-01T00:00:00&end=2025-01-31T23:59:59`

**Permisos**: Roles ADMIN, MEDICO

**Request Authorization**:
```
Authorization: Bearer {jwt_token}
```

**Proceso**:
1. El controller extrae los parámetros start y end de la query
2. Verifica que el usuario tenga rol ADMIN o MEDICO
3. El servicio busca en Firestore filtrando citas entre esas fechas
4. Ordena las citas cronológicamente
5. Retorna la lista de citas

**Response (200 OK)**:
```json
[
  {
    "id": "3fb87d92-7412-49aa-c062-8de3283cc419",
    "patientId": "1dee65f4-6298-40aa-a941-9cd1182bb307",
    "patientName": "María González",
    "doctorId": "doctor-uid-123",
    "doctorName": "Dr. Juan Pérez",
    "dateTime": "2025-01-20T10:00:00",
    "status": "SCHEDULED",
    // otros campos...
  },
  // otras citas...
]
```

### 6. Actualizar Cita
**Endpoint**: `PUT /api/appointments/{id}`

**Permisos**: Roles ADMIN, MEDICO

**Request Authorization**:
```
Authorization: Bearer {jwt_token}
```

**Request Body**: Similar al de creación con los campos actualizados.

**Proceso**:
1. El controller extrae el ID de la ruta
2. Verifica que el usuario tenga rol ADMIN o MEDICO
3. Valida los datos recibidos
4. Si se está cambiando la fecha/hora, verifica la disponibilidad del doctor
5. Actualiza el campo updatedAt con la fecha actual
6. El servicio actualiza la cita en Firestore
7. Retorna la cita actualizada

**Response (200 OK)**:
```json
{
  "id": "3fb87d92-7412-49aa-c062-8de3283cc419",
  "patientId": "1dee65f4-6298-40aa-a941-9cd1182bb307",
  "patientName": "María González",
  "doctorId": "doctor-uid-123",
  "doctorName": "Dr. Juan Pérez",
  "dateTime": "2025-01-20T11:00:00", // Hora actualizada
  "duration": 45, // Duración actualizada
  "status": "SCHEDULED",
  "reason": "Consulta general - actualizada",
  // otros campos actualizados...
  "updatedAt": "2025-01-16T09:45:30" // Fecha de actualización
}
```

### 7. Cancelar Cita
**Endpoint**: `POST /api/appointments/{id}/cancel`

**Permisos**: Roles ADMIN, MEDICO, PACIENTE (con restricciones)

**Request Authorization**:
```
Authorization: Bearer {jwt_token}
```

**Request Body**:
```json
{
  "reason": "Paciente no puede asistir"
}
```

**Proceso**:
1. El controller extrae el ID de la ruta
2. Verifica el rol del usuario
3. Si es PACIENTE, verifica que solo pueda cancelar sus propias citas
4. El servicio busca la cita en Firestore
5. Actualiza el estado a "CANCELLED"
6. Añade la razón de cancelación a las notas
7. Actualiza el campo updatedAt
8. Guarda los cambios en Firestore
9. Retorna una respuesta exitosa sin contenido

**Response (204 No Content)**

### 8. Completar Cita
**Endpoint**: `POST /api/appointments/{id}/complete`

**Permisos**: Solo rol MEDICO (solo el médico asignado)

**Request Authorization**:
```
Authorization: Bearer {jwt_token}
```

**Request Body**:
```json
{
  "notes": "Paciente con buena recuperación. Próximo control en 3 meses."
}
```

**Proceso**:
1. El controller extrae el ID de la ruta
2. Verifica que el usuario tenga rol MEDICO
3. Verifica que el usuario sea el médico asignado a la cita
4. El servicio busca la cita en Firestore
5. Actualiza el estado a "COMPLETED"
6. Establece las notas médicas
7. Actualiza el campo updatedAt
8. Guarda los cambios en Firestore
9. Retorna una respuesta exitosa sin contenido

**Response (204 No Content)**

## Lógica de Verificación de Disponibilidad

Una característica importante del servicio es el algoritmo para verificar disponibilidad de doctores y prevenir solapamientos de citas:

**Funcionamiento**:
1. Cuando se crea o actualiza una cita con nueva fecha/hora:
   - Convierte las cadenas de fecha a objetos LocalDateTime
   - Calcula la hora de finalización sumando la duración
2. Busca todas las citas activas (SCHEDULED, CONFIRMED) del doctor
3. Para cada cita existente:
   - Calcula su hora de inicio y fin
   - Verifica si hay solapamiento con la nueva cita
   - Hay solapamiento si la nueva cita comienza antes del fin de la existente Y termina después del inicio de la existente
4. Si hay algún solapamiento, rechaza la nueva cita
5. Si no hay solapamientos, permite la creación/actualización

## Estados de Citas

El servicio implementa un flujo de estados para las citas:

- **SCHEDULED**: Estado inicial de una cita recién creada
- **CONFIRMED**: (No implementado) Para citas confirmadas por el médico
- **CANCELLED**: Cuando una cita ha sido cancelada por cualquier parte
- **COMPLETED**: Cuando la cita ha sido realizada y el médico ha registrado las notas

## Seguridad y Control de Acceso

- **Validación JWT**: Todas las peticiones requieren un token JWT válido.
- **Control por roles**:
  - **ADMIN**: Acceso completo a todos los endpoints
  - **MEDICO**: 
     - Puede crear, ver y actualizar citas
     - Solo puede completar sus propias citas
     - Solo puede ver sus propias citas (excepto admin)
  - **PACIENTE**:
     - Puede crear citas (donde automáticamente es el paciente)
     - Puede ver solo sus propias citas
     - Puede cancelar solo sus propias citas
- **Restricciones de acceso**: Implementadas a nivel de controlador con validaciones explícitas.

## Comunicación con otros Servicios

- **AuthService**: El AppointmentService confía en los tokens JWT generados por AuthService.
  - No hay comunicación directa entre servicios
  - La verificación del token se realiza localmente
  - La misma clave secreta se comparte entre servicios

- **PatientService**: 
  - AppointmentService usa los IDs de pacientes generados por PatientService
  - No hay comunicación directa, solo referencias a IDs de pacientes