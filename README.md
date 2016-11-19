# Natural_Language_processing

In  this project, We have created a Project where user can enter the query in natural language or directly select the details from the table using  the Graphical Interface.

Here  we have used Oracale 10g Database as a Primary Database. using JDBC connections, We can connect to the database.

This Project mainly consists of  4 modules:

                      1. NLP Engine Module (Engine File).
                    
                      2. Parser Module  (NLBean Module)
                      
                      3. Query Builder Module (DBInterface File).
                     
                     4. Code Optimizier Module (NLP File).
                      
Engine Module:

This module takes the query in the natural language and separates the query into keys such as constants,variables, names,keywords and operators. 

Parser Module:

Analyzer module supplies keys to the Parser module. In this module we are concerned with grouping tokens into larger syntactic classes such as expressions, statements and procedure.

Query Builder Module:

In query builder module after parsing,it will construct the SQL query based on the data available.

Code Optimizer Module:

This is the top level module for NLP operation. It is the one which is responsible for retrieving the data from database depending upon the query supplied. 

#running Application.
 
 Install  Orcale 10g database.
 
 Create a Users in database. 
 
 Grant all privilages to the  user using Sql Command line.
 
 Create some Tables in  the databases created.
 
 Finally Run the run.bat file(BATCH FILE).
 
 #Seeing Results on UI
 
 Select the Database in the left most block.
 
 Select Table in the middle block.
 
 Select Column on  the right most.
 
 Hit DOQuery Button. which Generates Query and displays the Result in Result Block.
 
 
 
 #ScreenShot.
 
 I have Included some Screenshots of the application. you can Find them in Screenshots folder.
 


