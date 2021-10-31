<h1 align="center">AsteTIW</h1>
<p align="center">
    <img alt="AsteTIW" src="./WebContent/images/logo.jpg" >  <br>
    Project for "Tecnologie Informatiche per il Web" 2021 course <br>
    Implementation of an <strong>online Auction Website</strong>
</p>


<!-- ## Summary -->
<!-- too short summary not needed? -->
<!-- + [Technologies](#Technologies) -->
<!-- + [Versions](#Versions) -->
<!-- + [Features](#Features) -->

<!-- --- -->

## Technologies
Project created using:
<!-- back/front divided - better to put all together? -->
* Back end
    * Tomcat (web server)
    * Java servlet
    * MySQL
* Front end    
    * HTML5
    * Thymeleaf (template engine)
    * CSS
    * Javascript


## Versions
The specification of the project requires two different implementation:

### HTML pure version
This version must be done without the use of JavaScript.
The pages were built dynamically, with the information from the database, using the template engine **Thymeleaf** 

### JavaScript version
After the login page, each user interaction is managed without the complete reloading of the page, but with an a
asynchronous request to the server that updates only a single element of the page


## Features
+ Login page <!-- with username and password -->
+ Sell page: 
    + contains a form to create a new auction for an item 
    <!-- (the new auction will be added to the DB) -->
+ Buy page:
    + contains a **search bar**, to search an item by keyword, from the above list
    + shows a **list of available auction** (not expired), for the logged user
    + shows a **list of won auction** for the logged user
+ Auction details page:
    + contains all the information about the auction for that item 
    + shows a **list of bids** made by other users for that auction
+ Database containing data about:
    + registered users
    + registered auction
    + users bids
