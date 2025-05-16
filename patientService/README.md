# Patient Service - Explicación Detallada

## Componentes y su Funcionamiento

### PatientServiceApplication.java
- **Función**: Punto de entrada principal de la aplicación Spring Boot.
- **Cómo funciona**: Inicializa el contexto de Spring y arranca el servidor web en el puerto configurado (8082).

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
  - Configura que todos los endpoints de `/api/patients/**` requieran autenticación
  - Registra el filtro JwtAuthFilter para interceptar y validar tokens
  - Habilita las anotaciones @PreAuthorize para control de acceso basado en roles

### JwtAuthFilter.java
- **Función**: Filtra todas las peticiones para verificar y validar el token JWT.
- **Cómo funciona**:
  - Intercepta cada petición HTTP
  - Busca "Authorization Bearer {token}"
  - Valida el token usando la misma clave secreta que AuthService
  - Extrae el nombre de usuario (uid) y el rol del token
  - Establece un objeto Authentication en el SecurityContext
  - Asigna autoridades basadas en el rol (ROLE_ADMIN, ROLE_MEDICO, ROLE_PACIENTE)
  - Permite que la petición continúe si el token es válido

### Patient.java
- **Función**: Define la estructura de datos para los pacientes.
- **Cómo funciona**:
  - Define campos médicos y personales como id, userId, firstName, lastName, etc.
  - El campo userId conecta con el usuario en AuthService
  - Almacena información médica como dateOfBirth, bloodType, allergies, medicalHistory
  - Incluye información de contacto como email, phone, address
  - Contiene información de emergencia como emergencyContact y emergencyPhone

### PatientService.java
- **Función**: Implementa la lógica de negocio para gestión de pacientes.
- **Cómo funciona**:
  - **Crear paciente**: 
    - Genera un ID único (UUID)
    - Almacena el paciente en la colección "patients" en Firestore
  - **Obtener paciente por ID**: 
    - Busca en Firestore usando el ID único del paciente
  - **Obtener paciente por userId**: 
    - Busca usando el ID del usuario de autenticación
    - Permite vincular el usuario autenticado con su información de paciente
  - **Obtener todos los pacientes**: 
    - Recupera toda la colección para administradores y médicos
  - **Actualizar paciente**: 
    - Actualiza campos específicos manteniendo el mismo ID
  - **Eliminar paciente**: 
    - Elimina el documento correspondiente en Firestore

### PatientController.java
- **Función**: Expone los endpoints REST para la gestión de pacientes.
- **Cómo funciona**:
  - Define rutas para las operaciones CRUD (Create, Read, Update, Delete)
  - Implementa control de acceso basado en roles usando @PreAuthorize
  - Verifica que un paciente solo pueda acceder a su propia información
  - Procesa datos JSON de entrada
  - Llama a los métodos apropiados en PatientService
  - Maneja errores y excepciones adecuadamente
  - Devuelve respuestas HTTP con JSON y códigos de estado apropiados

## Endpoints

### 1. Crear Paciente
**Endpoint**: `POST /api/patients`

**Permisos**: Solo roles ADMIN y MEDICO.

**Request Authorization**:
```
Authorization: Bearer {jwt_token}
```

**Request Body**:
```json
{
  "userId": "patient-user-id",
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
```

**Proceso**:
1. El controller verifica el rol del usuario (ADMIN o MEDICO)
2. Valida el formato de los datos recibidos
3. Pasa la información al servicio
4. El servicio asigna un ID único (UUID)
5. Almacena el paciente en Firestore
6. Retorna el paciente creado con su ID

**Response (201 Created)**

### 2. Obtener Paciente por ID
**Endpoint**: `GET /api/patients/{id}`

**Permisos**: Roles ADMIN, MEDICO, PACIENTE (con restricciones)

**Request Authorization**:
```
Authorization: Bearer {jwt_token}
```

**Proceso**:
1. El controller extrae el ID de la ruta
2. Verifica el rol del usuario solicitante
3. Si es PACIENTE, verifica que solo pueda acceder a su propia información
4. El servicio busca el documento en Firestore usando el ID
5. Retorna el paciente o un error 404 si no se encuentra

**Response (200 OK)**:
```json
{
  "id": "1dee65f4-6298-40aa-a941-9cd1182bb307",
  "userId": "patient-user-id",
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
```

### 3. Obtener Paciente por UserId
**Endpoint**: `GET /api/patients/user/{userId}`

**Permisos**: Roles ADMIN, MEDICO, PACIENTE (con restricciones)

**Request Authorization**:
```
Authorization: Bearer {jwt_token}
```

**Proceso**:
1. El controller extrae el userId de la ruta
2. Verifica el rol del usuario solicitante
3. Si es PACIENTE, verifica que solo pueda acceder a su propia información
4. El servicio busca en Firestore filtrando por el campo userId
5. Retorna el paciente o un error 404 si no se encuentra

**Response (200 OK)**: Similar al endpoint anterior.

### 4. Obtener Todos los Pacientes
**Endpoint**: `GET /api/patients`

**Permisos**: Solo roles ADMIN y MEDICO.

**Request Authorization**:
```
Authorization: Bearer {jwt_token}
```

**Proceso**:
1. El controller verifica el rol del usuario (ADMIN o MEDICO)
2. El servicio recupera todos los documentos de la colección "patients"
3. Convierte los documentos a objetos Patient
4. Retorna la lista de pacientes

**Response (200 OK)**:
```json
[
  {
    "id": "1dee65f4-6298-40aa-a941-9cd1182bb307",
    "userId": "patient-user-id",
    "firstName": "María",
    "lastName": "González",
    "email": "maria.gonzalez@email.com",
    // resto de campos...
  },
  {
    "id": "2aef75b8-7432-49ab-c062-8de3283cc419",
    "userId": "patient-user-id-2",
    "firstName": "Carlos",
    "lastName": "Rodríguez",
    // resto de campos...
  }
]
```

### 5. Actualizar Paciente
**Endpoint**: `PUT /api/patients/{id}`

**Permisos**: Solo roles ADMIN y MEDICO.

**Request Authorization**:
```
Authorization: Bearer {jwt_token}
```

**Request Body**: Similar al de creación con los campos actualizados.

**Proceso**:
1. El controller extrae el ID de la ruta
2. Verifica el rol del usuario (ADMIN o MEDICO)
3. Valida el formato de los datos recibidos
4. El servicio actualiza el documento en Firestore manteniendo el mismo ID
5. Retorna el paciente actualizado

**Response (200 OK)**:
```json
{
  "id": "1dee65f4-6298-40aa-a941-9cd1182bb307",
  "userId": "patient-user-id",
  "firstName": "María",
  "lastName": "González López",
  // campos actualizados...
}
```

### 6. Eliminar Paciente
**Endpoint**: `DELETE /api/patients/{id}`

**Permisos**: Solo rol ADMIN.

**Request Authorization**:
```
Authorization: Bearer {jwt_token}
```

**Proceso Interno**:
1. El controller extrae el ID de la ruta
2. Verifica que el usuario tenga rol ADMIN
3. El servicio elimina el documento de Firestore
4. Retorna una respuesta exitosa sin contenido

**Response (204 No Content)**

## Seguridad y Control de Acceso

- **Validación JWT**: Todas las peticiones requieren un token JWT válido.
- **Control por roles**:
  - **ADMIN**: Acceso completo a todos los endpoints
  - **MEDICO**: Puede crear, ver y actualizar pacientes
  - **PACIENTE**: Solo puede ver su propia información
- **Restricciones de acceso**: Un paciente no puede acceder a datos de otros pacientes.
- **Validación de datos**: Se realizan validaciones en inputs para prevenir datos maliciosos.

## Comunicación con otros Servicios

- **AuthService**: El PatientService confía en los tokens JWT generados por AuthService.
  - No hay comunicación directa entre servicios
  - La verificación del token se realiza localmente
  - La misma clave secreta se comparte entre servicios

- **AppointmentService**: 
  - AppointmentService usa los IDs de pacientes generados por PatientService
  - No hay comunicación directa, solo referencias a IDs