Sample script 

Given first usage of java outside of algorithmic problems / base functionality of java. There is likely a severe lack of coding standards & correct usage of frameworks.


Improvemnets
1. Testing
2. Improved error handling for example MongoDb client doens't connect
3. Documnent API using swagger
4. Seperate API into multiple, Data Access Layer (with entity), Business Layer / passing and Handler/Controller.

API needs to decide if generic to feed through objects or if structure is enforced through ORM i.e. map DbObjects into ORM functionality. Currently just fed from database straight back to API. This would allow for passing and appropriately dealing with data for example IP could be stored as unsigned int and passed back in the frontend. 
