# gateway-service

## Authorization

The gateway authenticates the JWT through `auth-service` and checks the
permissions returned for the account's current role.

| Request | Permission |
| --- | --- |
| `GET /products`, `GET /products/**` | `product.read` |
| `POST /products`, `DELETE /products/**` | `product.write` |
| `GET /orders`, `GET /orders/**` | `order.read` |
| `POST /orders` | `order.write` |
| `GET /exchanges/**` | `exchange.read` |
| `POST /accounts/{id}/roles` | `account.admin` |

Missing or invalid authentication returns `401`; an authenticated account
without the required permission receives `403`.
