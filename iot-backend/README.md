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

If you want to start the project go into the
target folder and run the .jar file
```bash
java -jar target/name.jar
```