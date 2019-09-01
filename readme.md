Given it's my first usage of java outside of algorithmic problems / base functionality of java. There is likely a severe lack of coding standards & correct usage of frameworks / dependencies.

Running
1. ./InitialiseDatabase

Runs a docker mongodb docker container on 27777 and injects data from ./sample-data/*

2. cd Application/ && ./gradlew bootRun

Runs service on port 8500

Improvemnets
1. Apply MongoDb filters correctly.
2. Testing
3. Improved error handling for example MongoDb client doens't connect
4. Documnent API using swagger
5. Seperate API into multiple, Data Access Layer (with entity), Business Layer / passing and Handler/Controller.
6. API not routed correctly, currently supports all HTTP operations not just get.

Decision needs to be made if API is generic and designed to feed data directly through API or if structure is enforced through ORM. Currently just fed from database straight back to API. This would allow for passing and appropriately dealing with data for example IP could be stored as unsigned int and passed back through to allow for easier query filtering. 
