# Everyday Money Hub

A unified money-hub demo: **checking, savings, credit cards, and investments in
one API** — the "everyday banking" idea where a client sees all of their money
in a single view and moves it between accounts freely.

Built as a weekend project in the style of Edward Jones' *Everyday Solutions*
("bank in a box") launch: one digital home for bank, card, and investment
accounts instead of three separate logins.

## What it does

- **Accounts** — open checking / savings / credit-card / investment accounts
  (`POST /api/accounts`, `GET /api/accounts`, `GET /api/accounts/{id}`)
- **Money movement** — transfer between any two accounts (`POST /api/transfers`,
  `GET /api/transfers`). Credit cards behave like real cards: paying one down
  reduces what is owed; drawing from one is a cash advance capped by the limit.
- **Unified overview** — `GET /api/overview?owner=Jane` returns every account
  plus rolled-up totals: cash, investments, credit owed, and net worth.
- **Events** — every lifecycle change emits `ACCOUNT_CREATED`,
  `TRANSFER_INITIATED`, `TRANSFER_COMPLETED` (logged by default; real Kafka
  behind `app.events.kafka-enabled=true`).

## Stack

Java 17 · Spring Boot 3.2 · Spring Data JPA · Bean Validation · Kafka-ready
event publishing · H2 (default) / Postgres (`postgres` profile) ·
docker-compose for Postgres + Kafka.

Money is stored in **cents** (long), never floats — standard ledger practice.

## Run it

```bash
mvn spring-boot:run
```

Then:

```bash
# open three accounts for one client
curl -s -X POST localhost:8080/api/accounts \
  -H 'Content-Type: application/json' \
  -d '{"ownerName":"Jane","type":"CHECKING","openingBalanceCents":500000}'
curl -s -X POST localhost:8080/api/accounts \
  -H 'Content-Type: application/json' \
  -d '{"ownerName":"Jane","type":"CREDIT_CARD","openingBalanceCents":120000,"creditLimitCents":1000000}'
curl -s -X POST localhost:8080/api/accounts \
  -H 'Content-Type: application/json' \
  -d '{"ownerName":"Jane","type":"INVESTMENT","openingBalanceCents":2000000}'

# pay the card down from checking (ids 1 -> 2)
curl -s -X POST localhost:8080/api/transfers \
  -H 'Content-Type: application/json' \
  -d '{"fromAccountId":1,"toAccountId":2,"amountCents":30000}'

# the everyday view: everything in one place
curl -s 'localhost:8080/api/overview?owner=Jane'
```

With Docker available: `docker compose up -d` for Postgres + Kafka, then
`mvn spring-boot:run -Dspring-boot.run.profiles=postgres` (and set
`app.events.kafka-enabled=true` for real event publishing).

## Why this shape

A wealth-management client's money lives in several products; the interesting
engineering is the **single consistent view and the movement between them** —
one ledger, one event stream, product-aware transfer rules. That is what this
demo exercises.
