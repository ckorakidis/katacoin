#!/bin/bash

curl http://localhost:8080/api/ledger/transactions \
  -H "Accept: application/json" \
  -w "\nStatus: %{http_code}\n"
