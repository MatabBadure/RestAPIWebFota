**README FOR HILLROMVEST**
--------------------------

Introduction:
-------------
This is a Hill-Rom Vest web application. Which will provide visualisation for the Hill-Rom vest users / physicians and associates of Hill-Rom.

Tech-Stack
----------

    - JDK 1.8 
    - SpringBoot 
    - Oracle 
    - Spring Security (token based security). 

Tools Used
----------
    - Apache tomcat 8
    - Eclipse / Spring tool suite
    - Maven 3
    - SQL Developer

Setup
-----

 1. This application is built using JHipster tool. Below shows the details of setting up dev environment.
 2. Install Java from the Oracle website.
 3. Install Maven (recommended). 
 4. Install Git from git-scm.com. 

Configuring Eclipse
-------------------
https://jhipster.github.io/configuring_ide_eclipse.html

Running application locally
---------------------------
Command to run the application locally
mvn spring-boot:run
The application will be available on http://localhost:8080

Configurations
--------------
Maven has 2 profiles configured:

    - Dev
    - Production.

For the development configurations are under : `src/main/resources/config/application-dev.yml`

For the production configurations are under : `src/main/resources/config/application-prod.yml`

**References**
----------
https://jhipster.github.io/
