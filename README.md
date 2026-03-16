# 🏨 Restful-Booker API Automation

[![Automated API Testing](https://github.com/aryoprayogi/RestfulBookerAutomation/actions/workflows/run-api-test.yml/badge.svg)](https://github.com/aryoprayogi/RestfulBookerAutomation/actions)
![Java](https://img.shields.io/badge/Java-21-blue?logo=java)
![REST Assured](https://img.shields.io/badge/REST_Assured-5.4.0-green?logo=json)
![Allure Report](https://img.shields.io/badge/Allure_Report-Enabled-orange?logo=allure)

## 📌 Overview
This repository contains a robust and scalable API test automation framework built for the [Restful-Booker](https://restful-booker.herokuapp.com/) API. It demonstrates enterprise-standard QA practices, encompassing End-to-End (E2E) business flows, comprehensive negative testing, and continuous integration.

## 🛠️ Tech Stack
* **Language:** Java 21
* **API Library:** REST Assured
* **Test Framework:** TestNG
* **Build Tool:** Maven
* **Reporting:** Allure Report
* **CI/CD:** GitHub Actions
* **Data Generation:** Java Faker

## 🚀 Key Features
* **End-to-End Testing:** Covers the full booking lifecycle (Create, Read, Update, Delete).
* **Negative Testing:** Validates edge cases, invalid payloads, unauthorized access, and bad requests.
* **Dynamic Test Data:** Utilizes POJO and Java Faker to generate randomized, robust test data.
* **Contract Testing:** Ensures response structures match predefined JSON schemas.
* **Automated CI/CD:** Integrated with GitHub Actions to automatically trigger test executions on every push in a cloud Linux environment.

## 💻 Local Execution

1. **Clone the repository:**
   ```bash
   git clone https://github.com/aryoprayogi/RestfulBookerAutomation.git
   ```
2. **Navigate to the project directory:**
   ```bash
   cd RestfulBookerAutomation
   ```
3. **Run the tests:**
   ```bash
   mvn clean test
   ```

## 📊 View Test Report
This project uses Allure for comprehensive and interactive test reporting. After running the tests, generate and open the report using:
```bash
mvn allure:serve
```
