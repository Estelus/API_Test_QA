#Intrum-QA
## Table of contents
* [General info](#general-info)
* [Technologies](#technologies)
* [Setup](#setup)

## General info
* This project contains 6 test cases for public REST API https://gorest.co.in/public/v2/users
* The plugin generates XML reports in the directory target/surefire-reports
* Test data is stored at testData.csv
* One of the test classes transform test data io CSV file and proper format

## Technologies
* Java 20
* Rest Assured
* Maven
* Junit Jupiter
* Pararell tests
* SureFire

## Setup

1. Use JDK 20
2. Start all test by using -mvn test (Set up apache-maven-3.9.5 for computer is required) 
3. To open a test report go to the following project directory: target/site/surefire-report.html and use internet browser
