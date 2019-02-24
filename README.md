***
### Scala, Http4s, Cats-Effect, FS2, Kafka
***

Http Server (Http4s + Circe). Publishes to Kafka topics

Basic Account level locking for updates using Cats-Effect Ref[IO] and Semaphore[IO] in AccountInMemoryDatabase

Kafka Consumer (FS2 + Apache Kafka Consumer + Circe Decoding)

Kafka Publisher (IO Async + Apache Kafka Publisher + Circe Encoding)

Kafka Circe Serializers/Deserializers

***
### Kubernetes / Minikube / Helm Charts Kafka setup and topic test guide
***
```
> minikube start

> helm init

> kubectl create serviceaccount --namespace kube-system tiller

> kubectl create clusterrolebinding tiller-cluster-rule \
   --clusterrole=cluster-admin --serviceaccount=kube-system:tiller

> kubectl patch deploy --namespace kube-system tiller-deploy \
   -p '{"spec":{"template":{"spec":{"serviceAccount":"tiller"}}}}'

> cd Development/projects/  # cp-helm-charts cloned from github to this folder, left only kafka and zookeeper enabled in values.yaml

> helm install cp-helm-charts  # wanton-tarsier = release name in this example

> helm status wanton-tarsier

> kubectl create -f cp-helm-charts/examples/kafka-client.yaml # See output of helm status for kafka-client.yaml file content and save to local

> kubectl exec -it kafka-client -- /bin/bash

> kafka-topics --zookeeper wanton-tarsier-cp-zookeeper-headless:2181 --topic wanton-tarsier-topic --create --partitions 1 --replication-factor 1 --if-not-exists

> kafka-topics --list --zookeeper wanton-tarsier-cp-zookeeper-headless:2181

> echo "$MESSAGE" | kafka-console-producer --broker-list wanton-tarsier-cp-kafka-headless:9092 --topic wanton-tarsier-topic

> kafka-console-consumer --bootstrap-server wanton-tarsier-cp-kafka-headless:9092 --topic wanton-tarsier-topic --from-beginning
```

***
### Run using docker-compose:
***
```
> docker-machine start default

> eval "$(docker-machine env default)"

> sbt clean docker:publishLocal

> docker-compose up
```

***
### The following links describe the API calls. Use a Rest client such as Postman on Chrome.
***

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
