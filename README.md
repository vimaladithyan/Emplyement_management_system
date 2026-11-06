# Employee Management System — Spring Core + JPA (PRD Implementation)

Standalone Java console app. **No Spring Boot, no Spring MVC, no Spring Data.**
Spring Framework 6 (3.1.x is line 6.1.8 here) IoC + `spring-jdbc/orm/tx`,
Jakarta Persistence + Hibernate 6, MySQL, Java 17, Maven.

## Package structure (PRD §10)
```
com.company.Main                         console entry (AnnotationConfigApplicationContext)
com.company.config.AppConfig            @Configuration/@ComponentScan/@EnableTransactionManagement
                                        @PropertySource + @Value + @Bean (DataSource, EMF, Tx, IdGenerator)
com.company.entity.Employee             @Entity -> employees table
com.company.repository.EmployeeRepository / EmployeeRepositoryImpl  (@Repository, @PersistenceContext)
com.company.service.EmployeeService / EmployeeServiceImpl  (@Service, @Transactional, ctor injection)
com.company.notification.NotificationService                interface
                                        EmailNotificationService       (@Service @Primary)
                                        SmsNotificationService          (@Service, @Qualifier target)
com.company.util.EmployeeIdGenerator    @Bean @Scope("prototype") custom bean
src/main/resources/application.properties   DB + Hibernate + notification config
```

## Spring Core concepts demonstrated (PRD §6/§12)
- `@Configuration @ComponentScan @Bean` — manual vs auto registration
- `@Autowired` constructor injection in `EmployeeServiceImpl`
- `@Primary` → default `EmailNotificationService`; `@Qualifier("smsNotificationService")` → SMS (Main menu 7)
- `@Scope`: singleton (default, `EmployeeService`) vs `@Scope("prototype")` (`EmployeeIdGenerator`) — Main menu 8
- `@PostConstruct/@PreDestroy` in service + both notification services + id generator (prototype `@PreDestroy` intentionally not invoked — see `EmployeeIdGenerator` javadoc)
- `@Value` + `@PropertySource("application.properties")`
- `JpaTransactionManager` + `@Transactional` / `readOnly=true`; Main menu 6 = intentional rollback demo

## DB setup (MySQL)
```sql
CREATE DATABASE IF NOT EXISTS employee_management;
-- table `employees` is auto-created/updated by Hibernate (hbm2ddl=update)
```
Edit `src/main/resources/application.properties` (`db.url/username/password`) if needed.

## Commands (run inside this folder)
```powershell
mvn clean compile
mvn exec:java                  # runs com.company.Main (menu 0-8)
mvn test
mvn clean package
```
> Note: `mvn` was not found on PATH on this machine when created (JDK 26 present, project targets Java 17). Install Maven or open in IntelliJ IDEA as a Maven project, then run the commands above.
