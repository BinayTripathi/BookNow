# AccountBrowser


[![Spring_Boot Framework](https://img.shields.io/badge/Springboot-2.2.6.RELEASE_Framework-blue.svg?style=plastic)](https://start.spring.io/) |[![Java](https://img.shields.io/badge/Java-java-blue.svg?style=plastic)](https://www.oracle.com/java/technologies/javase-jdk8-downloads.html) | ![GitHub language count](https://img.shields.io/github/languages/count/BinayTripathi/ServiceVictoria-ChildrenInQueue.svg) | ![GitHub top language](https://img.shields.io/github/languages/top/BinayTripathi/ServiceVictoria-ChildrenInQueue.svg) |![GitHub repo size in bytes](https://img.shields.io/github/repo-size/BinayTripathi/ServiceVictoria-ChildrenInQueue.svg) 
| --- | ---          | ---        | ---      | ---        | 

---------------------------------------

## Repository codebase
 
The repository consists of projects as below:


| # |Project Name | Project detail| Environment |
| ---| ---  | ---            | --- |
| 1 | AccountBrowser| Project with JPA, HATEOAS & REST | [![.SpringBoot framework](https://img.shields.io/badge/Springboot-2.2.6.RELEASE_Framework-blue.svg?style=plastic)](https://start.spring.io/)|

### Summary

The overall objective of the applications :
```
>   To demonstrate JPA and Rest API creation. 

>   HATEOAS has been used with the REST APIs - so that we clickable links from Account to Transactions are available

>   Further used Flyway for database versioning.

>   Sample Unit/Integration test using JUNIT5 has been added.

```


### Application design detail

>   The application consists of 
*  A client executable jar (AccountBrowser-0.0.1-SNAPSHOT.jar)

### Setup detail

##### Environment Setup detail

> Download/install   	
>	1.	[![Maven](https://img.shields.io/badge/Mavan-3.6.3-blue.svg?style=plastic)](https://maven.apache.org/download.cgi) to build project and run test suite
>   
>   2. [![Java](https://img.shields.io/badge/Java-1.8_-blue.svg?style=plastic)](https://www.oracle.com/java/technologies/javase-jdk8-downloads.html) to run the project
>   
>	3. [![STS](https://img.shields.io/badge/Spring_Tool_Suite-STS-blue.svg?style=plastic)](https://spring.io/tools) to run/debug the applications
>	

##### Project Setup detail

>   1. Please clone or download the repository from [![github](https://img.shields.io/badge/git-hub-blue.svg?style=plastic)]https://github.com/BinayTripathi/AccountBrowser) 
>   
#####  To build / run the application

>   1. Open a new command prompt and browse to the root folder ( **AccountBrowser** ) of the application 
>   
>   2. Enter following command to build the project : **mvn clean install** 
>   
>   3. Enter following command to run the project with its default configuration: **java -jar target\AccountBrowser-0.0.1-SNAPSHOT.jar**
>   
>   4. To check im-momory database browse to **http://localhost:8080/h2-console/login.jsp**  [**JDBC url** : jdbc:h2:mem:accdet;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;  **User id** : 
test  . No password.]
>   
>   5. End point exposed check for accounts held by user  **http://localhost:8080/accounts/user/{userName}**  [Example : http://localhost:8080/accounts/user/user1 ] . 
>   
>   6. Response to above is json with all the accounts held by user if any else appropriate error is provided. **It also contains (HATEOS enabled) url associated with each account which can be clicked on the browser to see the transactions associated with that account as json**  
>   
>   7. Transactions for any account can also be obtained by directly checking for the end point **http://localhost:8080/transactions/account/{account-number}**  example http://localhost:8080/transactions/account/AC1



##### To open the project in Spring Tool Suite (or Eclipse)
>   1. Open **Spring Tool Suite** .
>   2. Select **File** ->  **Import** and then select **Existing Maven Projects**
>   3. Browse to  **AccountBrowser** select pom.xml and click **Finish** to import the project.
>   4. Run/Debug the project

### Support or Contact

Having any trouble? Please read out this [documentation](https://github.com/BinayTripathi/AccountBrowser/blob/master/README.md) or [contact](mailto:binay.mckv@gmail.com) and to sort it out.

  [![HitCount](http://hits.dwyl.com/BinayTripathi/AccountBrowser.svg)](http://hits.dwyl.com/BinayTripathi/AccountBrowser) | ![GitHub contributors](https://img.shields.io/github/contributors/BinayTripathi/AccountBrowser)|
 | --- | --- |


