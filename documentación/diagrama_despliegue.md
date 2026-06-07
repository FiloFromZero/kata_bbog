# Diagrama de Despliegue del Proyecto (DEV / PROD)

Este archivo contiene la especificación en Mermaid del flujo de despliegue dinámico continuo (CD) configurado para los ambientes de Desarrollo y Producción.

```mermaid
graph TB
    %% --- GitHub Section ---
    subgraph GITHUB["💻 Repositorio GitHub"]
        direction LR
        BRANCH_DEV["🌿 Rama: develop<br/>(Desarrollo)"]
        BRANCH_PROD["🌿 Rama: main<br/>(Producción)"]
    end

    %% --- GitHub Actions Section ---
    subgraph ACTIONS["⚙️ GitHub Actions (CI/CD Pipeline)"]
        TRIGGER{{"¿Qué rama disparó?"}}
        BUILD["📦 Build JAR<br/>./mvnw clean package"]

        TF_DEV["🚀 Terraform (DEV)<br/>-backend-config='key=dev/terraform.tfstate'<br/>spring_profile=dev"]
        TF_PROD["🚀 Terraform (PROD)<br/>-backend-config='key=prod/terraform.tfstate'<br/>spring_profile=prod"]
    end

    %% --- AWS Remote State ---
    subgraph AWS_BACKEND["🗄️ Backend Remoto (AWS)"]
        TF_STATE_S3["🪣 AWS S3: kata-cloud-tfstate-mateo<br/>(Estado Remoto de Terraform)"]
    end

    %% --- AWS DEV Section ---
    subgraph AWS_DEV["🟢 AWS Ambiente DEV (Desarrollo)"]
        S3_DEV["🪣 Bucket S3 DEV<br/>app.jar (DEV)"]
        SG_DEV["🔒 Security Group DEV<br/>(Permite puerto 8080)"]

        subgraph EC2_DEV["🖥️ Instancia EC2 DEV (t3.micro)"]
            APP_DEV["☕ Spring Boot App (DEV)<br/>Puerto: 8080<br/>spring.profiles.active=dev"]
            DB_DEV["🐳 Docker Postgres DEV<br/>Puerto: 5432<br/>DB: customers_dev"]
        end
    end

    %% --- AWS PROD Section ---
    subgraph AWS_PROD["🔴 AWS Ambiente PROD (Producción)"]
        S3_PROD["🪣 Bucket S3 PROD<br/>app.jar (PROD)"]
        SG_PROD["🔒 Security Group PROD<br/>(Permite puerto 9090)"]

        subgraph EC2_PROD["🖥️ Instancia EC2 PROD (t2.micro)"]
            APP_PROD["☕ Spring Boot App (PROD)<br/>Puerto: 9090<br/>spring.profiles.active=prod"]
            DB_PROD["🐳 Docker Postgres PROD<br/>Puerto: 5432<br/>DB: customers_prod"]
        end
    end

    %% --- Connections ---
    BRANCH_DEV -->|Push / Merge| TRIGGER
    BRANCH_PROD -->|Push / Merge| TRIGGER

    TRIGGER -->|develop| BUILD
    BUILD --> TF_DEV

    TRIGGER -->|main| BUILD
    BUILD --> TF_PROD

    %% State Management
    TF_DEV <-->|Lee/Escribe: dev/terraform.tfstate| TF_STATE_S3
    TF_PROD <-->|Lee/Escribe: prod/terraform.tfstate| TF_STATE_S3

    %% Deployment DEV
    TF_DEV -->|1. Sube JAR| S3_DEV
    TF_DEV -->|2. Despliega/Actualiza EC2| EC2_DEV
    SG_DEV -.->|Protege| EC2_DEV
    S3_DEV -.->|3. EC2 descarga JAR| APP_DEV
    APP_DEV -->|Conexión Local| DB_DEV

    %% Deployment PROD
    TF_PROD -->|1. Sube JAR| S3_PROD
    TF_PROD -->|2. Despliega/Actualiza EC2| EC2_PROD
    SG_PROD -.->|Protege| EC2_PROD
    S3_PROD -.->|3. EC2 descarga JAR| APP_PROD
    APP_PROD -->|Conexión Local| DB_PROD

    %% Style classes
    classDef devColor fill:#e1f5fe,stroke:#03a9f4,stroke-width:2px;
    classDef prodColor fill:#ffebee,stroke:#f44336,stroke-width:2px;
    classDef gitColor fill:#eceff1,stroke:#607d8b,stroke-width:2px;
    classDef actionColor fill:#ede7f6,stroke:#673ab7,stroke-width:2px;
    classDef stateColor fill:#fff8e1,stroke:#ffb300,stroke-width:2px;

    class BRANCH_DEV,AWS_DEV,S3_DEV,EC2_DEV,APP_DEV,DB_DEV,SG_DEV devColor;
    class BRANCH_PROD,AWS_PROD,S3_PROD,EC2_PROD,APP_PROD,DB_PROD,SG_PROD prodColor;
    class GITHUB gitColor;
    class ACTIONS,TRIGGER,BUILD,TF_DEV,TF_PROD actionColor;
    class AWS_BACKEND,TF_STATE_S3 stateColor;
```
