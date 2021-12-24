# ReportGenerator
JavaFX Application that communicates with API to generate a list of all customer's on file.  The list is then exported as a CSV file for the customer so it can be imported in to their external billing software application.  Application created using Java, JavaFX, SOAP Requests, XML and CSS.
<br><br>
Upon start of the application, the user is presented with a login screen.  The user has an option to swap between light and dark mode.<br> 
The application makes a SOAP request to an API to validate the login credentials.<br>
![loginLight](https://user-images.githubusercontent.com/68821944/147367841-e1004aa8-96ff-4e8d-8f02-d2eb6370eae7.png)
![loginDark](https://user-images.githubusercontent.com/68821944/147367840-1c5cd3fb-8b7a-4cdc-9d30-2bdf04e97e93.png)

<br><br>
Upon successful login, the application stores a valid session taken for all future API requests.<br>
Once logged in, the user clicks Get Report.<br>
![loggedinStartLight](https://user-images.githubusercontent.com/68821944/147367842-49a778f0-ec2d-4796-87b5-818aa9f4772d.png)
![loggedinStartDark](https://user-images.githubusercontent.com/68821944/147367843-0d522261-d9f1-4190-917b-a62911fd3b4b.png)

<br><br>
The application then makes a SOAP request to generate a list of customers and stores the list in a 2D array. The UI updates via an additional thread to let the user know it is compiling a list of customers.<br>
![gettingReportLight](https://user-images.githubusercontent.com/68821944/147367847-14f49c2b-4aa0-4bd6-9f72-7c4e515e001f.png)
![gettingReportDark](https://user-images.githubusercontent.com/68821944/147367849-402b2775-d9c5-40fc-984c-a15accda3d39.png)

<br><br>
After the customer list is created and stored, the application iterates the 2D array making a SOAP request for each customer to pull information from the database. This information is then added to the second layer of the 2D array. The UI updates with a progress bar.<br>
![generatingReportLight](https://user-images.githubusercontent.com/68821944/147367852-d56e5cc2-5aa0-483c-8fbe-004ecadea9b2.png)
![generatingReportDark](https://user-images.githubusercontent.com/68821944/147367853-ce00e099-a558-476e-af51-58542da1cae9.png)

<br><br>
Once the report is complete, a "Save Report" button displays. Once the user clicks the Save Report button, the application iterates through the 2D array of customer information. For each item, it utilizes a String Builder to output the customer information in to a string. Once it has iterated through all items in the array, it writes the file as a .csv to a location the user chooses.<br>
![savingReport](https://user-images.githubusercontent.com/68821944/147367862-803b5cd4-3f25-47f3-9ca2-a083588edaac.png)

<br><br>
After saving the report, the UI updates to indicate the report has been saved.
![reportSavedLight](https://user-images.githubusercontent.com/68821944/147367860-d9d1fd88-d2bf-4bfc-be78-7ec6c3ebb3ed.png)
![reportSavedDark](https://user-images.githubusercontent.com/68821944/147367861-c1d146fa-1fbc-47a4-a9d9-5c59a7745e72.png)


