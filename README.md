## Running the application

To run the application you will need: 

1. Java 1.8+ installed and appended to you $PATH variable
2. Maven 3.5.0+ installed and appended to you $PATH variable
3. PostgreSQL v9.6.6 installed, running on 5432 port with active user:('postgres', '123456').
4. Available 2222 port for starting the web server

Points 3 and 4 above are not straight and can be violated in case if you edit resources/nodes-server.yml as needed

To run the app follow next steps:

 1. Open three instance of CMD windows (Windows) or Terminal (MacOS, Linux) in the source folder
 2. Build the application using `mvn clean package`
 3. In the seconds window run: `java -jar discovery-server/target/discovery-1.1.0.RELEASE.jar` and wait for it to start up
 4. In the third window run: `java -jar nodes-server/target/nodes-1.1.0.RELEASE.jar` and wait for it to start up
 5. In the first window run: `java -jar gateway-server/target/gateway-1.1.0.RELEASE.jar` and wait for it to start up

 
 Alternatively, you may start just the node service without gateway and discovery. For that you will need to comment
 `@EnableDiscoveryClient` on `com.chisw.microservices.nodes.NodesServer` and then:
 
  1. Open one instance of CMD windows (Windows) or Terminal (MacOS, Linux) in the source folder
  2. Build the application using `mvn clean package`
  3. In the window run: `java -jar /nodes-server/target/nodes-1.1.0.RELEASE.jar` and wait for it to start up
 
 
 ## REST
 
 1. `GET /node/14`     -   get node by with `id = 4` 
 
 2. `GET /node/14/descendants` - get all descendants with node `id = 14`, you may use `page=X&size=Y&sort=Z` parameters for this endpoint
 
 3. `GET /node/14/ancestors` - get all ancestors with node `id = 14`, you may use `page=X&size=Y&sort=Z` parameters for this endpoint

 4. `POST /node`      
 
      `{
        "id":"14",
        "parentId":"15"
      }`
      
       -   create new node with `id = 14` and append it to parent with `id = 15`
      
 5. `PUT /node/14`          
 
      `{
         "id":"15"
       }` 
       
       -   change node with id = 14 so that it id is changed to id = 15 with all corresponding children are adjusted

        
 6. `DELETE /node/14`      -   delete node with id = 14 with all children


## Possible issues


If you have different PostgreSQL version, it may happen that application will fail on startup when running [`resources/schema.sql` or `resources/data.sql`].

In this case you would need to perform corresponding operations manually and exclude [`resources/schema.sql` or `resources/data.sql`] 
from startup configuration
 
