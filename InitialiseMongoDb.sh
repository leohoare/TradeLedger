# Initialise on port 27777 (port mapping) using latest mongodb image
docker run -d -p 27777:27017 --name mongodb mongo:latest

# Copy sample data into container (tmp folder)
docker cp ./sample-data/users.json mongodb:/tmp/user.json
docker cp ./sample-data/events.json mongodb:/tmp/events.json

# MongoDb Import datasets
docker exec mongodb mongoimport -d UserData -c users --file /tmp/user.json --jsonArray
docker exec mongodb mongoimport -d UserData -c events --file /tmp/events.json --jsonArray
