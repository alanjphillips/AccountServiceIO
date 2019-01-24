Scala, Http4s, Cats-Effect


Basic Account level locking for updates using Cats-Effect Ref[IO] and Semaphore[IO]


- The following links describe the API calls. Use a Rest client such as Postman on Chrome.

[Create Account](#create-an-account-to-be-source-of-the-money-transfer)

[Perform Money Transfer](#perform-transfer-from-source-account-number-as-path-param-with-value-1000-representing-joey)

[Perform Deposit](#perform-deposit-to-account-as-path-param-with-value-1000-representing-joey)

[Get all Accounts](#get-all-accounts)

[Get single Account](#get-an-account)


***
### Create an account to be source of the money transfer:
***
```
Method: Post
Uri:    http://127.0.0.1:8080/accounts
Header: Content-Type: application/json
```

#### Body:

```json
{
  "accHolderName":"Joey",
  "balance":200
}
```

#### Response: Note the account `number` in the successful response with Http Status Code = 200 OK:

```json
{
  "accNumber": "1000",
  "accHolderName": "Joey",
  "balance": 200
}
```


***
### Create another account to be destination of the money transfer:
***

#### Body:
```json
{
  "accHolderName":"JoeJoeJr",
  "balance":0
}
```


***
### Perform transfer from source account number as path param with value `1000` representing `Joey`:
***
```
Method: Post
Uri:    http://127.0.0.1:8080/accounts/1000/transfer
Header: Content-Type: application/json
```

#### Body:

```json
{
  "destAccNum":"1001",
  "transferAmount":99
}
```

#### Response: Successful transfer will have Http Status Code = 200 OK

```json
{
  "sourceAccount": {
    "accNumber": "1000",
    "accHolderName": "Joey",
    "balance": 101
  },
  "destAccount": {
    "accNumber": "1001",
    "accHolderName": "JoeJoeJr",
    "balance": 99
  },
  "transferAmount": 99
}
```
#### Errors:

Insufficient funds in source account will have Http Status Code = 400 Bad Request
```json
{
  "sourceAccNum": "1000",
  "destAccNum": "1001",
  "transferAmount": 99,
  "description": "Not enough funds available in account: 1000 "
}
```

Account Not Found for source account path param or destination `destAccNum` in json body will have Http Status Code = 404 Not Found
```json
{
  "accountNumber": "1000000",
  "description": "Account Number doesn't exist: 1000000"
}
```


***
### Perform deposit to account as path param with value `1000` representing `Joey`:
***
```
Method: Post
Uri:    http://127.0.0.1:8080/accounts/1000/deposit
Header: Content-Type: application/json
```

#### Body:

```json
{
"depositAmount":10000
}
```

#### Response: Successful transfer will have Http Status Code = 200 OK

```json
{
  "account": {
    "accNumber": "1001",
    "accHolderName": "Junior",
    "balance": 120200
  },
  "depositAmount": 10000
}
```

#### Errors:

Account Not Found for destination account number in path param will have Http Status Code = 404 Not Found
```json
{
  "accountNumber": "1000000",
  "description": "Account Number doesn't exist: 1000000"
}
```


***
### Get all accounts:
***
```
Method: Get
Uri:    http://127.0.0.1:8080/accounts
Header: Content-Type: application/json
```

#### Response:

```json
[
  {
    "accNumber": "1001",
    "accHolderName": "Junior",
    "balance": 200
  },
  {
    "accNumber": "1000",
    "accHolderName": "Joey",
    "balance": 200
  }
]
```


***
### Get an account
***
```
Method: Get
Uri:    http://127.0.0.1:8080/accounts/1000
Header: Content-Type: application/json
```

#### Response:

```json
{
  "accNumber": "1000",
  "accHolderName": "Joey",
  "balance": 200
}
```

#### Errors:

Account Not Found for account number in path param will have Http Status Code = 404 Not Found
```json
{
  "accountNumber": "1000000",
  "description": "Account Number doesn't exist: 1000000"
}
```