# Changelog

Todas las versiones importantes de este proyecto se documentan aquí.

## [1.3.0] - 2025-05-18
### Refactorizado
- Refactorización de clases para pruebas unitarias: `AdaptadorClasificación` y `AdaptadorRanking`.
- Nuevas clases para manejar la lógica de negocio, pruebas unitarias de clasificación y ranking.

### Agregado
- Verificación por correo electrónico en el registro como requisito para iniciar sesión.
- Requisitos en la contraseña: mayúscula, número y carácter especial.

## [1.2.0] - 2025-05-16
### Refactorizado
- Separación de la lógica de Firebase.

### Agregado
- Pruebas unitarias iniciales para `PerfilFragment` y `EditarPerfilActivity`.

## [1.1.0] - 2025-05-14
### Mejorado
- Actualización de `EditarPerfilActivity` con vistas configuradas según el tipo de usuario (entrenador o jugador).

## [1.0.1] - 2025-04-30
### Agregado
- Layout personalizado en el perfil según el tipo de usuario (jugador/entrenador).
- Ícono para iniciar sesión con correo electrónico.

### Corregido
- Mejoras visuales y de diseño en los campos del formulario de inicio de sesión y registro.

## [1.0.0] - 2025-04-07
### Agregado
- Estructura base del proyecto Android.
- Actividades y fragments: `LogIn`, `MainActivity`, `Perfil`, `EditarPerfil`, `Ranking`, `Clasificación`.
- Funcionalidades:
  - Registro e inicio de sesión con correo.
  - Recuperación de contraseña.
  - Registro de usuarios por tipo (jugador o entrenador) y equipo.
  - Selección de equipo, y visualización de partidos según el usuario.
  - Centralización de funcionalidades de Firebase (`FirebaseController`).
  - Logout funcional.
  - Inicio de sesión con Google.
- Diseño:
  - Formularios estilizados.
  - Layout final de LogIn y Registro.
  - Uso de `AlertDialog` validando campos obligatorios.

### Documentación
- Comentarios en formato Javadoc.

