# SpringBootProjectScopeIndia

This project is a **Spring Boot web application** developed during my internship at **Scope India, Thiruvananthapuram**.  
It includes a **Contact Form** and **Student Registration Form** where user details are stored in a database.  
The project demonstrates Java Full Stack concepts such as Spring Boot, REST API, MySQL, and HTML/CSS integration.

---

## 🚀 Features
- Contact Form and Student Registration with database integration  
- RESTful API using Spring Boot  
- MVC Architecture (Controller → Service → Repository)  
- HTML, CSS, and Bootstrap frontend integration  
- Data stored in MySQL database  
- Server runs on port **8081**

---

## ⚙️ Configuration Instructions

Before running this project, please make sure to update the following configuration files:

### 1. **`application.properties`**
You **must** modify these fields according to your own environment:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/your_database_name
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password

# Server Port
server.port=8081

# Email Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_generated_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
