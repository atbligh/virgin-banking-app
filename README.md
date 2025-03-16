# Getting Started

### Requirements
- Java 21 (LTS)
- Maven 3.8+

This is a ```Maven Spring Boot``` project that can be run:

- via an IDE e.g. Intellij
- command line from project root: ```mvn spring-boot:run```
- as a Docker container from project root: ```docker compose -f docker-compose.yml up``` (required Docker Desktop to be installed)

Swagger UI: http://localhost:8080/banking-app/swagger-ui/index.html

OpenAPI Description: http://localhost:8080/banking-app/v3/api-docs

Use the RESTful endpoint: http://localhost:8080/banking-app/transaction/all to get all transactions
and then use the other RESTful endpoints (see swagger UI above) to get specific data for categories and years.

There are currently 500 data records with the following categories:

[Jewelry, Industrial, Tools, Home, Health, Toys, Games, Shoes, Kids, Movies, Sports, Grocery, Automotive, Computers, Beauty, Baby, Music, Clothing, Outdoors, Garden, Books, Electronics]

### Technologies & Frameworks Used

- Java 21
- Spring Boot
- Maven
- Swagger
- OpenAPI
- JUnit
- Mockito
- Lombok

### Application Architecture

The application is a RESTful API that uses Spring Boot to expose endpoints for getting
transaction data.

The application is structured in 3 logical layers:

- Controller: Exposes the RESTful endpoints
- Service: Contains the business logic
- Data: Contains the data loading logic

```BigDecimal``` is used to represent monetary values to avoid floating point precision issues.

```MoneyUtils``` is used to format/parse the monetary values, ensuring that they are always rounded uniformly and scaled to 2 decimal places.

The bulk of the application business logic is contained with the ```TransactionServiceImpl``` class.

#### Locales

The application locale is set by the property ```app.locale``` and is currently set to ```en_GB```. The locale is used to derive the currency symbol and load date data. 
Dates in the ```en_GB``` locale use ```Sept``` for date months (```MMM```) not ```Sep```. To facilitate ```Sep``` the ```en_US``` locale would need to be used. This has
not been tested yet but in theory you could change this to ```en_US``` (or any locale) and load data with monetary amounts using the currency symbol of that locale. 
With more time this dynamic locale support could be refined and fully tested. This could be further split into a locale for the application and a locale for data loading
to allow data loading in one locale and then API responses in another locale. In may even be better to have the API responses locale agnostic (no currency symbol),
allowing the calling process/application to decide this.

#### Data Loading

Data loading is interfaced and is currently loaded from a CSV file. To add another data loader:

- implement the ```DataLoader``` interface
- Annotate with the  ```@Service``` annotation and add a qualifying name use ```CsvDataLoader``` as an example
- Set the ```app.data-type``` property in ```application.properties``` to the qualifying name of the new data loader
- The bean factory will automatically pick this up and use it to load data when the application runs, see ```AppConfig.dataLoader()```

### Testing

Constructor dependency injection is used to allow ease of testing.

#### Unit Tests

Unit tests are located in the ```src/test ``` directory and can be run independently of integration tests using the ```maven-surefire-plugin```.
Unit tests are run during the Maven ```test```.

#### Integration Tests

Integration tests are kept separately from unit tests and are located in the ```src/integration-test``` directory. Integration tests class
names must be suffixed with '```IT```' e.g. ```TransactionControllerIT```. This ensures they are not run with unit tests during the Maven ```test``` phase.
The ```maven-failsafe-plugin``` runs the integration tests during the Maven ```integration-test``` and ```verify``` phases.

### Next Steps

- Complete the tests in ```TransactionControllerTest``` and ```TransactionControllerIT```, there are ```TODO``` comments these classes for this
- Add more unit tests to enhance application robustness
- Add locale configuration for currency symbol and date loading (dates)
- Replace the CSV data loading with a dedicated/separate data source
- Possibly enhance the average spend per month for category endpoint to also group by year, the spec wasn't clear on that
- Add a front end user interface
- Add RESTful endpoint security
- Build out a CI/CD pipeline
