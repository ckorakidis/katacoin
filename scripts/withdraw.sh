#!/bin/bash

curl -X POST http://localhost:8080/api/ledger/withdraw \
  -H "Content-Type: application/json" \
  -d '{"amount": 50.00}' \
  -w "\nStatus: %{http_code}\n"
