# Educational Testing System (ETS)

A Java desktop application that generates quizzes from a categorised question
bank, evaluates answers, tracks student performance over time, and provides
personalised feedback.

**CSC61204 Software Construction — Group Assignment (April 2026)**
**Scenario 2: Educational Testing System | SDG 4 Targets: 4.1, 4.6**
**Group: [Group Name]**

## Group Members

| Name | Student ID |
|------|------------|
| [Tan Ming Reo] | [0381828] |
| [Chang Huai Jia] | [0384199] |
| [Rui Magato] | [0365597] |
| [Azhan Zareer Ahmed] | [0382759] |
| [Abdulla Muawia Moosa] | [0381925] |

## Features

- Quiz generation from a categorised question bank (Builder pattern)
- Swappable question-selection algorithms (Strategy pattern)
- Answer evaluation with instant feedback
- Performance history tracking per student
- Live questions from the Open Trivia DB API, with offline fallback
  question bank for graceful degradation
- Student and Admin roles

## Requirements

- Java 21 or later ([link to Adoptium/Oracle download])
- Internet connection (optional — the app falls back to a local
  question bank when offline)

## How to Run

```bash
mvn clean package
java -jar target/ETS-1.0-SNAPSHOT.jar
```

### Demo Accounts

| Role | Username | Password |
|------|----------|----------|
| Student | [username] | [password] |
| Admin | [username] | [password] |

## Running the Tests

```bash
mvn test
```

To generate the JaCoCo coverage report (output at `target/site/jacoco/index.html`):

```bash
mvn clean test jacoco:report
```

## Tech Stack

- Java 21, Swing (GUI)
- JUnit 5 + JaCoCo (testing and coverage)
- Open Trivia DB API (question source) — https://opentdb.com
- Maven (build), GitHub Actions (CI)

## Project Structure
```
src/main/java/
  [your.package].model/       — domain classes (Quiz, Question, Result, User)
  [your.package].dto/         — data transfer objects (e.g., API response mappings)
  [your.package].builder/     — Builder pattern (quiz construction)
  [your.package].strategy/    — Strategy pattern (question-selection algorithms)
  [your.package].services/    — business logic (quiz generation, evaluation, feedback)
  [your.package].repo/        — JSON persistence (users, results)
  [your.package].api/         — Open Trivia DB client and response parsing
  [your.package].controller/  — controllers linking GUI actions to services (MVC)
  [your.package].gui/         — Swing views (MVC)
  [your.package].exception/   — custom exceptions
  [your.package].util/        — shared helpers
src/test/java/                — JUnit 5 test suite
```
