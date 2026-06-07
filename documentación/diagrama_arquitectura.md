# Diagrama de Arquitectura Lógica de la Aplicación

Este documento detalla la arquitectura de software lógica y la interacción de componentes por capas de la aplicación de gestión de clientes.

## Diagrama de Arquitectura

El siguiente diagrama en Mermaid representa el flujo lógico de información y la división de responsabilidades de las capas del Front-End (Angular 22), el Back-End (Spring Boot) y la Base de Datos (PostgreSQL):

```mermaid
graph TD
    %% --- Front-End (Angular 22) ---
    subgraph FRONTEND["🎨 Front-End (Angular 22 — Client-Side)"]
        subgraph FE_UI["Capa de Presentación (Vistas / UI)"]
            LOGIN_COMP["🔑 LoginComponent<br/>(Sign-in / Credenciales)"]
            DASH_COMP["📊 DashboardComponent<br/>(CRUD / Búsqueda / Paginación)"]
            TOAST_UI["🔔 Toast Component<br/>(Notificaciones Flotantes)"]
        end

        subgraph FE_SERVICES["Capa de Servicios e Integración"]
            AUTH_SERV["🔐 AuthService<br/>(Signals: token, isAuthenticated)"]
            CUST_SERV["👥 CustomerService<br/>(httpResource() - Angular 22)"]
            TOAST_SERV["💬 ToastService<br/>(Signal state: toasts[])"]
        end

        subgraph FE_CORE["Capa de Middleware y Seguridad"]
            AUTH_GUARD["🛡️ AuthGuard<br/>(Protección de rutas)"]
            AUTH_INTER["⚙️ AuthInterceptor<br/>(Inyección de JWT / Auth 401)"]
        end

        subgraph FE_MODELS["Capa de Modelos y Pipes"]
            CUST_MODEL["📦 customer.model.ts<br/>(Interfaces de Datos)"]
            TIME_PIPE["⏱️ RelativeTimePipe<br/>(Formato de Fechas en Español)"]
        end
    end

    %% --- Back-End (Spring Boot) ---
    subgraph BACKEND["☕ Back-End (Spring Boot — Hexagonal Architecture)"]
        
        %% Primary Adapters
        subgraph PRIMARY_ADAPTERS["🔌 Driving Adapters (Adaptadores Primarios / Entrada)"]
            SEC_CONFIG["🔒 SecurityConfig<br/>(Basic Auth / JWT Filter / CORS)"]
            JWT_PROV["🔑 JwtTokenProvider<br/>(Generación / Validación de Token)"]
            AUTH_CTRL["🚪 AuthController<br/>(POST /auth/login)"]
            CUST_CTRL["👤 CustomerController<br/>(Endpoints REST CRUD)"]
        end

        %% Application & Domain (The Core Hexagon)
        subgraph HEXAGON_CORE["⬡ Core Hexagon (Dominio y Aplicación)"]
            subgraph IN_PORTS["📥 Input Ports (Casos de Uso)"]
                USE_CASES["CreateCustomerUseCase<br/>ListCustomersUseCase<br/>UpdateCustomerUseCase<br/>DeleteCustomerUseCase"]
            end
            
            subgraph APP_SERVICES["⚙️ Application Services"]
                SERVICES["CreateCustomerService<br/>ListCustomersService<br/>UpdateCustomerService<br/>DeleteCustomerService"]
            end

            subgraph DOMAIN_CORE["📦 Domain Model & Ports Out"]
                CUST_ENTITY["🧑 Customer (Domain Entity)"]
                CUST_PORT_OUT["📤 CustomerRepository (Output Port Interface)"]
            end
        end

        %% Secondary Adapters
        subgraph SECONDARY_ADAPTERS["🔌 Driven Adapters (Adaptadores Secundarios / Salida)"]
            CUST_ADAPTER_OUT["🗄️ CustomerPersistenceAdapter<br/>(Implementación de Puerto de Salida)"]
            JPA_REPO["🌱 CustomerJpaRepository<br/>(Spring Data JPA)"]
            JPA_ENTITY["📋 CustomerJpaEntity<br/>(Mapeo de Hibernate / PostgreSQL)"]
            CUST_MAPPER["🔄 CustomerMapper<br/>(Traducción Dominio <-> JPA Entity)"]
        end
    end

    %% --- Database ---
    subgraph DATABASE["🗄️ Base de Datos (PostgreSQL)"]
        DB_TABLES["📋 Tabla: Customers<br/>(UUID primary key, email, etc.)"]
    end

    %% --- Logical Flows & Connections ---
    
    %% User navigation and security guards
    LOGIN_COMP -->|Redirección tras Login| DASH_COMP
    DASH_COMP -->|Verifica Acceso| AUTH_GUARD
    AUTH_GUARD -->|Lee estado reactivo| AUTH_SERV

    %% UI to Services
    LOGIN_COMP -->|Llama auth| AUTH_SERV
    DASH_COMP -->|Llama CRUD| CUST_SERV
    DASH_COMP -->|Dispara alertas| TOAST_SERV
    TOAST_SERV -->|Dibuja en pantalla| TOAST_UI
    DASH_COMP -->|Formatea fechas| TIME_PIPE
    CUST_SERV -->|Usa tipos de datos| CUST_MODEL

    %% Services Interception & Network Calls
    CUST_SERV -->|"Petición HTTP"| AUTH_INTER
    AUTH_INTER -->|"Inyecta Bearer JWT"| SEC_CONFIG
    AUTH_SERV -->|"Basic Auth (Base64)"| SEC_CONFIG

    %% API Processing (Primary Adapters)
    SEC_CONFIG -->|"Filtra & Valida Token"| JWT_PROV
    SEC_CONFIG -->|"Despacha a"| AUTH_CTRL
    SEC_CONFIG -->|"Despacha a"| CUST_CTRL

    %% Hexagon Internal Interaction
    AUTH_CTRL -->|"Verifica Credenciales"| JWT_PROV
    CUST_CTRL -->|"Invoca Casos de Uso"| USE_CASES
    USE_CASES -->|"Implementados por"| SERVICES
    SERVICES -->|"Manipula Entidad"| CUST_ENTITY
    SERVICES -->|"Persiste mediante"| CUST_PORT_OUT

    %% Driven Adapters Interaction
    CUST_PORT_OUT -->|"Implementado por"| CUST_ADAPTER_OUT
    CUST_ADAPTER_OUT -->|"Mapea Entidades"| CUST_MAPPER
    CUST_ADAPTER_OUT -->|"Usa Repository"| JPA_REPO
    JPA_REPO -->|"Consulta/Guarda"| JPA_ENTITY
    JPA_ENTITY -->|"SQL Query / Hibernate"| DB_TABLES

    %% Style classes
    classDef feColor fill:#e1f5fe,stroke:#03a9f4,stroke-width:2px;
    classDef adapterColor fill:#f5f5f5,stroke:#9e9e9e,stroke-width:2px;
    classDef coreColor fill:#f3e5f5,stroke:#8e24aa,stroke-width:2px;
    classDef dbColor fill:#fff8e1,stroke:#ffb300,stroke-width:2px;

    class LOGIN_COMP,DASH_COMP,TOAST_UI,AUTH_SERV,CUST_SERV,TOAST_SERV,AUTH_GUARD,AUTH_INTER,CUST_MODEL,TIME_PIPE feColor;
    class SEC_CONFIG,JWT_PROV,AUTH_CTRL,CUST_CTRL,CUST_ADAPTER_OUT,JPA_REPO,JPA_ENTITY,CUST_MAPPER adapterColor;
    class USE_CASES,SERVICES,CUST_ENTITY,CUST_PORT_OUT coreColor;
    class DB_TABLES dbColor;

    style FRONTEND fill:#f1f9ff,stroke:#03a9f4,stroke-dasharray: 5 5;
    style BACKEND fill:#fafafa,stroke:#757575,stroke-dasharray: 5 5;
    style PRIMARY_ADAPTERS fill:#fdfdfd,stroke:#b0bec5,stroke-width:1px;
    style SECONDARY_ADAPTERS fill:#fdfdfd,stroke:#b0bec5,stroke-width:1px;
    style HEXAGON_CORE fill:#fcf8ff,stroke:#ab47bc,stroke-width:1.5px;
    style DATABASE fill:#fffef3,stroke:#ffb300,stroke-dasharray: 5 5;
```

## Descripción de las Capas

### 1. Front-End (Angular 22)
* **Presentación (UI)**: Componentes independientes (`standalone`) configurados con detección de cambios optimizada `ChangeDetectionStrategy.OnPush` para máximo rendimiento y micro-interacciones suaves.
* **Servicios**: Integra la nueva característica **`httpResource()`** de Angular 22 para un manejo de recursos reactivo y simplificado (evitando la suscripción manual y manejando el estado de carga implícitamente).
* **Middleware y Seguridad**: Interceptor funcional que adjunta el token JWT y realiza deslogueo automático ante errores `401`, y un guard que previene accesos no autenticados al dashboard.

### 2. Back-End (Spring Boot — Arquitectura Hexagonal)
* **Adaptadores de Entrada (Driving/Primary Adapters)**:
  - **REST Controllers (`web` package)**: `CustomerController` y `AuthController` actúan como puntos de entrada de la aplicación, traduciendo peticiones HTTP a llamadas lógicas del sistema.
  - **Filtros de Seguridad**: `SecurityConfig` intercepta peticiones, valida credenciales básicas o valida la firma del token JWT a través de `JwtTokenProvider`.
* **Núcleo de la Aplicación (Core Hexagon)**:
  - **Puertos de Entrada (`in` ports)**: Interfaces puras como `CreateCustomerUseCase` y `ListCustomersUseCase` que declaran los servicios disponibles para el exterior.
  - **Servicios de Aplicación**: Clases como `CreateCustomerService` que implementan los casos de uso, orquestando las validaciones y llamando a los puertos de persistencia de salida.
  - **Modelo de Dominio**: La entidad pura `Customer` que encapsula la lógica, atributos y restricciones del dominio de negocio.
  - **Puertos de Salida (`out` ports)**: Interface `CustomerRepository` que declara cómo se deben salvar los datos de manera agnóstica a la tecnología.
* **Adaptadores de Salida (Driven/Secondary Adapters)**:
  - **Persistencia (`persistence` package)**: `CustomerPersistenceAdapter` implementa el puerto de salida `CustomerRepository`. Utiliza `CustomerMapper` para traducir de la entidad pura de dominio a la entidad `CustomerJpaEntity` y se apoya en `CustomerJpaRepository` (Spring Data JPA / Hibernate) para guardar los datos en **PostgreSQL**.

### 3. Base de Datos (PostgreSQL)
* Modelo relacional con identificadores UUID autogenerados en formato estándar para representar de forma única a cada cliente.
