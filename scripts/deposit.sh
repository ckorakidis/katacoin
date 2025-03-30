#!/bin/bash

curl -X POST http://localhost:8080/api/ledger/deposit \
  -H "Content-Type: application/json" \
  -d '{"amount": 100.00}' \
  -w "\nStatus: %{http_code}\n"
