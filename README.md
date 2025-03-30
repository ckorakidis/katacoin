# 💰 Ledger API

A simple RESTful API built with Spring Boot that simulates a basic ledger. 

Supports deposits, withdrawals, balance tracking, and transaction history - all in-memory, no database required.

---

## ✅ Features

- Record **deposits** and **withdrawals**
- View current **balance**
- Fetch **transaction history** (latest first)
- **Validation** for input (no negative or zero amounts)
- **Custom error handling** for business rules
- Fully documented with **Swagger/OpenAPI**

---

## 📦 Technologies

- ![Java](https://img.shields.io/badge/Java-21-blue)
  ![Spring Boot](https://img.shields.io/badge/Spring--Boot-3.4.4-brightgreen)
- Spring Web, Validation
- Springdoc OpenAPI (Swagger UI)
- Maven

---

## 🚀 Running the App

1. Clone the repo
2. To run the app:

```bash
./mvnw spring-boot:run
```
3. Access the API at http://localhost:8080
4. Open Swagger UI at: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
    - Or view raw spec at [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
---

## 📚 API Endpoints

| Method | Endpoint | Description |
| ------ | -------- | ----------- |
| POST | /api/ledger/deposit | Deposit money |
| POST | /api/ledger/withdraw |	Withdraw money |
| GET |	/api/ledger/balance | Get current balance |
| GET |	/api/ledger/transactions | Get transaction history |

### 📥 Request Body (for deposit/withdraw)
```json
{
  "amount": 100.00
}
```
---

## 💡 Assumptions
* All data is stored in-memory - restarting the app clears all data.
* No user/account separation (single global ledger).
* No authentication/authorisation required.
* No logging/monitoring - as per task constraints.

---

## ⚠️ Error Handling
Invalid amount (e.g. missing, zero, or negative)
```json
{
  "error": "Validation failed",
  "fieldErrors": [
    {
      "field": "amount",
      "messages": [
        "Amount must be >= 0.01"
      ]
    }
  ]
}

```

Missing field example ({} body):
```json
{
  "error": "Validation failed",
  "fieldErrors": [
    {
      "field": "amount",
      "messages": [
        "Amount is required"
      ]
    }
  ]
}
```

Insufficient balance
```text
Insufficient balance to withdraw 100.00
```
