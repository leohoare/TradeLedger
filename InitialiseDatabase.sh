# Initialise on port 27777 (port mapping) using latest mongodb image
docker run -d -p 27777:27017 --name mongodb mongo:latest

for file in ./sample-data/*.json
do
    filename=`basename $file`
    collectionName=`echo $filename | sed 's/\.json$//'`
    # Copy sample data into container (tmp folder)
    docker cp $file mongodb:/tmp/$filename
    # MongoDb Import datasets
    docker exec mongodb mongoimport -d AppropriateDatabaseName -c $collectionName --file /tmp/$filename --jsonArray
done