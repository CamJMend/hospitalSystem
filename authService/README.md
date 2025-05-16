# Auth Service - Explicación Detallada

## Componentes y su Funcionamiento

### AuthServiceApplication.java
- **Función**: Punto de entrada principal de la aplicación Spring Boot.
- **Cómo funciona**: Inicializa el contexto de Spring y arranca el servidor web en el puerto configurado (8081).

### FirebaseConfig.java
- **Función**: Configura la conexión con Firebase para autenticación y almacenamiento.
- **Cómo funciona**: 
  - Lee el archivo de credenciales `firebase-config.json`
  - Inicializa la conexión a Firebase si no existe ya
  - Configura Firestore como base de datos
  - Expone los beans necesarios para inyección de dependencias

### SecurityConfig.java
- **Función**: Configura los aspectos de seguridad de la aplicación.
- **Cómo funciona**:
  - Configura CORS (Cross-Origin Resource Sharing) para permitir peticiones desde diferentes dominios
  - Deshabilita CSRF (Cross-Site Request Forgery) ya que usamos autenticación basada en tokens
  - Establece la política de sesiones como STATELESS (sin estado)
  - Define qué rutas son públicas (como `/api/auth/**`) y cuáles requieren autenticación
  - Configura el manejo de excepciones de seguridad

### User.java
- **Función**: Define la estructura de datos para los usuarios del sistema.
- **Cómo funciona**:
  - Define campos como uid, email, password, role, firstName, lastName, etc.
  - Implementa getters y setters para estos campos
  - Sirve como modelo para mapear datos de/hacia Firestore
  - Los roles posibles son: `paciente`, `medico`, y `admin`

### JwtUtil.java
- **Función**: Proporciona utilidades para trabajar con tokens JWT.
- **Cómo funciona**:
  - Genera tokens JWT con un tiempo de expiración configurado
  - Incluye el rol del usuario en los claims del token
  - Valida tokens existentes verificando firma y expiración
  - Extrae información como el username (uid) y rol del token
  - Utiliza una clave secreta configurable para firmar los tokens

### AuthService.java
- **Función**: Implementa la lógica de negocio para autenticación.
- **Cómo funciona**:
  - **Registro**: Crea nuevos usuarios en Firestore
    - Recibe datos del usuario
    - Almacena la información en Firestore
    - Asigna un rol al usuario (paciente, médico, admin)
  - **Login**: Autentica usuarios y genera tokens
    - Verifica credenciales
    - Recupera el rol del usuario
    - Genera un token JWT con el rol incluido
  - **Validación de tokens**: Verifica si un token es válido
    - Comprueba la firma
    - Verifica que no haya expirado

### AuthController.java
- **Función**: Expone los endpoints REST para la autenticación.
- **Cómo funciona**:
  - Maneja las peticiones HTTP para registro, login y validación
  - Procesa datos JSON de entrada
  - Llama a los métodos apropiados en AuthService
  - Devuelve respuestas HTTP con JSON y códigos de estado apropiados
  - Maneja errores y excepciones de forma adecuada

## Endpoints

### 1. Registro de Usuario
**Endpoint**: `POST /api/auth/register`

**Descripción**: Registra un nuevo usuario en el sistema con un rol específico.

**Request Body**:
```json
{
  "email": "usuario@ejemplo.com",
  "password": "password123",
  "firstName": "Nombre",
  "lastName": "Apellido",
  "role": "paciente" // paciente, medico, admin
}
```

**Proceso**:
1. El controller recibe la petición y valida el formato JSON
2. Pasa los datos al servicio de autenticación
3. El servicio crea un nuevo registro en Firestore
4. Se asignan los roles como metadatos del usuario
5. Se retorna el usuario creado sin la contraseña

**Response (200 OK)**

### 2. Login
**Endpoint**: `POST /api/auth/login`

**Descripción**: Autentica al usuario y devuelve un token JWT.

**Request Body**:
```json
{
  "email": "usuario@ejemplo.com",
  "password": "password123"
}
```

**Proceso**:
1. El controller recibe las credenciales
2. El servicio verifica si el usuario existe en Firestore
3. Se valida la contraseña
4. Se recupera el rol del usuario
5. Se genera un token JWT firmado incluyendo el uid y rol
6. Se devuelve el token al cliente

**Response**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer"
}
```

### 3. Validación de Token
**Endpoint**: `POST /api/auth/validate`

**Descripción**: Valida si un token JWT es válido.

**Request Authorization**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Proceso**:
1. Se extrae el token del header Authorization
2. Se verifica la firma digital usando la clave secreta
3. Se comprueba que el token no haya expirado
4. Se retorna el resultado de la validación

**Response**:
```json
{
  "valid": true
}
```

## Flujo de Comunicación

1. **Cliente → AuthService**: Solicitudes de registro, login o validación.
2. **AuthService → Firestore**: Almacenamiento y recuperación de datos de usuario.
3. **AuthService → Cliente**: Devuelve tokens JWT o información de usuario.
4. **Cliente → Otros Microservicios**: Utiliza el token JWT para autenticarse.

## Mecanismo de Seguridad

- Los tokens JWT están firmados con una clave secreta compartida entre microservicios
- La expiración de tokens está configurada en 24 horas por defecto
- Solo los endpoints de autenticación son públicos
- El sistema no almacena contraseñas en texto plano
- La validación de tokens se realiza en cada solicitud a servicios protegidos