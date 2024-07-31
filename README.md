# Teste técnico backend Caju

## Rodando o projeto (Docker)

```bash
docker-compose up -d
```

- Acesse em: http://localhost:8080/transactions

## Rodando o projeto (Local)

- Copie o arquivo `.env.example` para `.env` e altere as configurações de acordo.

```bash
./gradlew bootRun
```

## Dados de demonstação

O banco inclui alguns dados de demonstração:

- Uma conta com o id `accountId` = "1"
- Saldos: `CASH`: `300`, `FOOD`: `200`, `MEAL`: `100`
- Estabelecimentos:
    - `"SUPER MARKET"`, categoria: `FOOD`
    - `"PAGSEGURO"`, categoria: `CASH`
    - `"UBER EATS"`, categoria: `MEAL`

## Endpoint

- `POST` `/transactions`

```json
{
  "account": "1",
  "totalAmount": "100.01",
  "mcc": "5811",
  "merchant": "UBER EATS"
}
```
