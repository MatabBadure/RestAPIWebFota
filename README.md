**README FOR HILLROMVEST**
--------------------------

Introduction:
-------------
This is a Hill-Rom Vest web application. Which will provide visualisation for the Hill-Rom vest users / physicians and associates of Hill-Rom.

Tech-Stack
----------

    - JDK 1.8 
    - SpringBoot 
    - Angular.js 1.3 
    - Oracle 
    - Spring Security (token based security). 
    - Grunt

Tools Used
----------
    - Apache tomcat 7
    - Eclipse / Spring tool suite
    - Maven 3
    - Bower
    - Node .10 or higher
    - Grunt
    - SQL Developer
    - yeoman generator

Setup
-----

 1. This application is built using JHipster tool. Below shows the details of setting up dev environment.
 2. Install Java from the Oracle website.
 3. Install Maven (recommended). 
 4. Install Git from git-scm.com. 
 5. Install Node.js from the Node.js website. This will also install npm, which is the node package manager we are using in the next commands.
 6. Install Yeoman: npm install -g yo
 7. Install Bower: npm install -g bower
 8. Install Grunt with npm install -g grunt-cli 
 9. Install JHipster: npm install -g generator-jhipster

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
