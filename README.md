# Plataforma de Gestión de Clientes - Banco de Bogotá (Kata)

Este proyecto consiste en una aplicación completa para la administración y control de clientes corporativos, diseñada bajo los lineamientos visuales del Banco de Bogotá.

El sistema está dividido en dos partes principales:
1. **Front-End**: Una aplicación SPA moderna en **Angular 22** construida con componentes `standalone`, reactividad nativa mediante **Signals**, el nuevo cargador declarativo **`httpResource()`**, y arquitectura responsiva.
2. **Back-End**: Una API RESTful en **Spring Boot 3** estructurada con **Arquitectura Hexagonal (Puertos y Adaptadores)**, persistencia JPA con **PostgreSQL**, y seguridad dual (Basic Auth y JWT).

---

## 🌐 Ambientes y Direcciones de Acceso

La infraestructura está automatizada en **AWS** usando **Terraform** y cuenta con dos ambientes independientes:

### 🟢 Ambiente de Desarrollo (DEV)
* **Rama Git**: `develop`
* **Front-End (S3 Web Hosting)**: [http://kata-cloud-frontend-dev-b7bd1e69.s3-website-us-east-1.amazonaws.com](http://kata-cloud-frontend-dev-b7bd1e69.s3-website-us-east-1.amazonaws.com)
* **Back-End (API Gateway/DNS)**: `http://kata-bbog-dev-mateo.duckdns.org:8080`
* **Credenciales por Defecto**: Usuario `admin-dev` / Contraseña `admin-dev`

### 🔴 Ambiente de Producción (PROD)
* **Rama Git**: `main`
* **Front-End (S3 Web Hosting)**: [http://kata-cloud-frontend-prod-ac567e28.s3-website-us-east-1.amazonaws.com](http://kata-cloud-frontend-prod-ac567e28.s3-website-us-east-1.amazonaws.com)
* **Back-End (API Gateway/DNS)**: `http://kata-bbog-prod-mateo.duckdns.org:9090`
* **Credenciales por Defecto**: Usuario `admin-prod` / Contraseña definida de forma segura en las variables de entorno.

---

## 🚀 Proceso de Despliegue Continuo (CI/CD)

El despliegue a AWS está completamente automatizado a través de **GitHub Actions** (`.github/workflows/cd.yml`). El flujo se activa con cada confirmación de código:

```
[Desarrollador local]
        │
        ├─── Push a la rama 'develop' ───► Despliegue automático a AWS DEV
        │
        └─── Push a la rama 'main' ──────► Despliegue automático a AWS PROD
```

### ¿Cómo empaqueta el pipeline las aplicaciones?
Para garantizar que cada despliegue sea limpio y reproducible, el pipeline realiza los siguientes procesos de empaquetado:

* **Back-End (Spring Boot / Maven)**:
  - Se compila y empaqueta en un único archivo ejecutable autopropulsado mediante el comando:
    ```bash
    ./mvnw clean package -DskipTests
    ```
  - **Resultado**: Se genera un archivo comprimido de tipo fat-JAR en `Back-End/target/app.jar` que contiene todo el código compilado junto con el servidor embebido Tomcat y las dependencias del sistema.
  - Este JAR se sube directamente a un bucket S3 de almacenamiento de artefactos de AWS, sirviendo como única fuente de despliegue para la instancia EC2.

* **Front-End (Angular 22 / Node.js)**:
  - Tras instalar las dependencias con `npm ci`, la aplicación se empaqueta como un conjunto de recursos web estáticos (SPA):
    - **En Desarrollo (DEV)**: Se ejecuta `npm run build -- --configuration=development` que empaqueta con soporte para depuración y etiquetas visuales de desarrollo.
    - **En Producción (PROD)**: Se ejecuta `npm run build` que minimiza y ofusca el código, generando hashing único de nombres de archivo para optimizar la caché agresiva del navegador.
  - **Resultado**: Todo el paquete distribuible de la SPA se genera en la carpeta `Front-End/dist/Front-End/browser` (que incluye el `index.html`, archivos Javascript `.js`, hojas de estilo CSS `.css` y activos vectoriales de imagen).

### ¿Qué hace el Pipeline de CD en cada ejecución?
1. **Compilación del Backend**: Empaqueta el archivo `app.jar` omitiendo pruebas unitarias.
2. **Construcción del Frontend**: Empaqueta la aplicación Angular en su configuración correspondiente de ambiente.
3. **Sincronización en S3**:
   - El código compilado de Angular se sincroniza en el bucket S3 estático correspondiente.
   - El archivo `app.jar` se sube a un bucket S3 de almacenamiento de artefactos.
4. **Aplicación de Terraform**: Inicializa el estado en S3 y aplica el plan para crear/actualizar los recursos (Security Groups, buckets S3 y servidores EC2).

---

## 🧪 Integración Continua (CI) y Ejecución de Pruebas

El proyecto cuenta con un flujo de **Integración Continua (CI)** configurado en `.github/workflows/ci.yml`. Este pipeline se ejecuta de forma automática en cada `push` a las ramas `develop` y `main`, así como en cada Pull Request hacia `main`, asegurando que los cambios no rompan la estabilidad del sistema antes del despliegue (CD).

### 1. Pruebas del Back-End (Spring Boot)
El pipeline de CI compila el código y ejecuta toda la suite de pruebas del backend utilizando el comando:
```bash
./mvnw clean test
```

La suite del backend se compone de tres niveles de verificación:
* **Pruebas Unitarias (`domain` & `application`)**: Validan el comportamiento aislado de las entidades de negocio y la lógica de los servicios del Core Hexagonal de forma rápida sin levantar infraestructura.
* **Pruebas de Integración (`infrastructure`)**: Verifican el correcto funcionamiento de los adaptadores web (REST controllers) y la configuración de seguridad (filtros JWT/Basic Auth) contra componentes de Spring.
* **Pruebas de Aceptación BDD (`bdd`)**: Utilizan **Cucumber** y la sintaxis Gherkin (escrita en lenguaje natural en archivos `.feature`) para verificar el comportamiento de extremo a extremo de las APIs con escenarios reales simulados.

### 2. Pruebas del Front-End (Angular 22)
El Front-End está configurado con **Vitest**, la herramienta moderna de testing por defecto de Angular 22 que reemplaza al motor clásico de Karma para ejecutar pruebas unitarias en memoria de forma ultrarrápida.
* Para ejecutar las pruebas unitarias del frontend localmente, navega a la carpeta de Angular y ejecuta:
  ```bash
  npm run test
  ```

---

## 🔄 Automatización del Ciclo de Vida del Servidor (EC2)

Para asegurar que los cambios del Backend se ejecuten de inmediato en AWS sin intervenciones manuales, se configuró una regla de ciclo de vida nativa en Terraform:

* **Trigger de Reemplazo**: En el archivo `ec2.tf`, la directiva `replace_triggered_by = [aws_s3_object.app_jar]` monitoriza el hash (Etag) de la versión del backend subida a S3.
* **Comportamiento**: Cuando compilas y subes una nueva versión del backend, Terraform detecta el cambio en el JAR, da de baja automáticamente el servidor EC2 viejo y levanta uno nuevo con la versión de código más reciente en su script de inicialización (`user-data.sh`).

---

## 🛠️ Flujo de Trabajo para Promocionar Cambios

Cuando completes el desarrollo de una característica en `develop` y quieras llevarla a producción (`main`), ejecuta en tu consola:

```bash
# 1. Asegúrate de subir tus cambios y subirlos en develop
git checkout develop
git add .
git commit -m "feat: mi nueva característica"
git push origin develop

# 2. Cámbiate a main y trae lo último del servidor
git checkout main
git pull origin main

# 3. Fusiona develop en main
git merge develop

# 4. Envía a producción (Esto arranca el pipeline en GitHub)
git push origin main

# 5. Regresa a tu entorno de desarrollo
git checkout develop
```

---

## 📄 Documentación Adicional
* Ver [Diagrama de Despliegue Físico](documentación/diagrama_despliegue.md): Representa visualmente la infraestructura en la nube y conexiones de red en AWS.
* Ver [Diagrama de Arquitectura Lógica](documentación/diagrama_arquitectura.md): Representa visualmente el patrón de Arquitectura Hexagonal por capas (Puertos y Adaptadores) y componentes de Angular.
