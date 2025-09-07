# SecureSpringbootApp

## Description

**SecureSpringbootApp** is a secure web application built with **Spring Boot** and **PostgreSQL**.  
This application demonstrates secure authentication practices using **Spring Security**, including:

- User authentication with **BCrypt password hashing**  
- Role-based access control  
- HTTPS enforcement  
- Security headers such as **HSTS**, **CSP**, and frame options  

This project is part of the **Secure Software Development** course assignment.  
It illustrates secure handling of user login and access management while connecting to a PostgreSQL backend.

---

## Features

- **Secure login** with BCrypt-hashed passwords  
- **Role-based authentication** for API endpoints  
- **HSTS** and **Content Security Policy** headers  
- **CSRF protection** enabled (exceptions for `/actuator/**`)  
- Enforced **HTTPS** for all requests  

---

## Getting Started

### Prerequisites

- **Java 17+**  
- **Maven 3.8+**  
- **PostgreSQL** database  
- **Git**  

### Setup Instructions

1. **Clone the repository**

```bash
git clone https://github.com/Kejmerkew/MSCS-535-A01_Project1
cd SecureSpringbootApp
```

2. **Build**
```bash
mvn clean package
```

3. **Create keystore**
```bash
keytool -genkeypair \
  -alias secureapp \
  -keyalg RSA \
  -keysize 2048 \
  -storetype PKCS12 \
  -keystore keystore.p12 \
  -validity 3650 \
  -storepass changeit \
  -keypass changeit \
  -dname "CN=localhost, OU=IT, O=ExampleCorp, L=City, S=State, C=US"
```

4. **Start a Postgres instance (local or Docker)**
```bash
sudo docker run --name pg-secure -e POSTGRES_PASSWORD=pgpass -e POSTGRES_DB=appdb -p 5432:5432 -d postgres
```

5. **Run**
```bash
java -jar target/secure-springboot-app-0.1.0.jar
```

6. **Use cur to register a user and log in:**
```bash
curl -k -X POST https://localhost:8443/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"S3cureP@ssw0rd","email":"alice@example.com"}'
```
