#  AquaMovil - Backend API

Este proyecto contiene el servicio backend de **AquaMovil** desarrollado con **Spring Boot** y **MySQL 8.0**, totalmente dockerizado para facilitar su despliegue y portabilidad en entornos de desarrollo y producción.

---

##  Requisitos Previos

Para ejecutar este proyecto solo necesitas tener instalado:
* **Docker Desktop**
* **Docker Compose**

*(No es necesario tener instalado Java, Maven o MySQL localmente en el sistema operativo).*

---

##  Instalación y Despliegue

Sigue estos pasos para levantar el ecosistema completo:

1. **Clonar el repositorio:**
   ```bash
   git clone [https://github.com/JuanDiaz1992/aquamovilBackVirginiaDocker.git](https://github.com/JuanDiaz1992/aquamovilBackVirginiaDocker.git)
   cd AquamovilBack
Levantar los contenedores: Ejecuta el siguiente comando para construir la imagen de Java y activar la base de datos:

Bash
docker-compose up -d --build
Verificar el estado: Puedes revisar que los contenedores estén corriendo con:

Bash
docker ps
La API estará disponible en: http://localhost:8086/virginia

##  Persistencia de Datos y Base de Datos
El entorno está configurado para ser completamente portátil.

Volumen Local: Los datos de MySQL se guardan automáticamente en la carpeta ./db_data. Esto permite que al mover el proyecto, no se pierda la información.

Acceso Externo: Puedes conectar herramientas como DBeaver o Workbench usando:

Host: localhost

Puerto: 3306

Usuario: root

Password: admin

Base de datos: admin_user_aquamovil-virginia

[!IMPORTANT] Primera ejecución: Si la base de datos está vacía, importa el script SQL de inicialización ubicado en la carpeta /scripts_db a través de tu cliente MySQL.

##  Comandos de Mantenimiento
Si necesitas realizar mantenimiento, utiliza estos comandos en la terminal:

Ver logs de la App en tiempo real: docker logs -f spring_boot_app

Detener los servicios (sin borrar datos): docker-compose stop

Reiniciar todo desde cero: docker-compose down && docker-compose up -d

Limpiar imágenes antiguas (ahorro de espacio): docker system prune -f

© 2026 AquaMovil - Gestión de Desarrollo.