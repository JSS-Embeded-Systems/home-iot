Start mongo DB
```bash
brew services start mongodb-community@8.0
```
Start the Mqtt broker
```bash
./moquette.sh
```

Build the image of the server
```bash
mvn clean package
```
or 
```bash
./mvnw clean package
```