
## About this project
This application is a storage array simulator for operating LUNs and exposes REST API to clients.  The following functions are supported:
  1. Create one or multiple LUN’s.
  2. Resize a LUN.
  3. Remove a LUN.
  4. Retrieve the information (size) of a LUN.
  5. Concurrent requests.
  6. Data persistence.

The project is built based on the following frameworks: 
  * Spring Boot/Spring MVC(with embedded Tomcat) -- for IoC container and REST services
  * Spring Data JPA(Hibernate) -- for O-R mapping
  * H2 database(embedded) -- for persistence storage
  * Swagger -- for REST API documentation and live demo 


## REST API
Resource name: lun

Operations:
  * POST /lun/ -- creates one or multiple LUNs with the given size
  * PUT /lun/ -- Update a LUN
  * GET /lun/{id} -- Retrieves the info of a LUN with the give id
  * GET /lun/_list -- Retrieves all the LUN info
  * DELETE /lun/{id} -- Deletes a LUN with the given id
For the detailed information of each operation(input/output model schema, return code, etc.), please run the application and see the API document in Swagger UI(http://localhost:8080/swagger-ui.html)

![Lun Simulator Swagger UI](https://raw.githubusercontent.com/mengqhai/appbasement/master/lunsimulator/swagger-ui-screenshot.png)

## How to run the application
Please make sure you've Java 1.8 installed.
1. Check out the code
2. If you have Maven installed(v3.3.9 at least), execute the following command in the project root:
```
mvn package && java -jar target/lunsimulator-0.0.1-SNAPSHOT.jar
```
   If you don't Maven installed, use the 'mvnw' script in project root to replace the mvn command above.  It will automatically download Maven for you.
   
3. After the application is fully started, open the Swagger UI in the your web browser: http://localhost:8080/swagger-ui.html
4. Try it out by invoking the REST API from Swagger UI.

A screenshot(swagger-ui-screenshot.png) of the Swagger UI can be found in the project root.


