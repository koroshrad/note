#1)How to run your application:
To run the application, you need to run the main method of the class (NoteApplication) that has the @SpringBootApplication annotation. 
If the application is packaged as a jar, it can be run from the command line using the command: java -jar <jar-file-name>.jar

#2)Instructions to the UX team (i.e. how to use your API)

For the UX team, they need to make HTTP requests to the API using a tool like Postman or a client-side library like axios.
The API descriptions provided by the Swagger annotations will help them understand the request and response format for each endpoint.

#3)Your choice of technology and the reasons for using them (and any alternatives you considered)
The code uses Java, Spring Boot framework and Swagger for the implementation. 
Spring Boot provides a convenient way to build a production-ready REST API quickly, while Swagger helps with API documentation and testing.

#4)If you were to spend more time on this task, what would you change and what other key features would you add.


    Error handling - Add more robust error handling to handle exceptions and return appropriate error messages to the client.
    
    Logging - Implement logging to track events and diagnose issues.
    
    Security - Enhance security by implementing authentication and authorization, such as using JWT tokens or OAuth2.
    
    Caching - Add caching to improve performance and reduce server load.
    
    Scalability - Make the API scalable to handle a large number of requests by implementing load balancing, caching, and adding additional servers.

Other key features that I would add:

    Documentation - Add comprehensive documentation for the API, including clear and concise documentation for endpoints, request and response formats, and error codes.
    
    Versioning - Implement versioning to allow for backward compatibility and to allow clients to choose the version of the API that they want to use.
    
    Pagination - Implement pagination for long lists of items to improve performance and reduce response time.
    
    Search and Filtering - Add search and filtering capabilities to allow clients to retrieve specific data based on specific criteria.
    
    Real-time data updates - Implement real-time updates using websockets or similar technologies to provide clients with the most up-to-date information.

