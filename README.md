## MineStat 

A rewrite of the Minecraft JMX Plugin adding additioal persistance and export features and makeing it a more generic statistics plugin 


To Build this you will need a working installation of the Simple Build Tool then just run this in the project root 

  sbt assembly 

This will create the Jar file in target/ 


## TODO 

Add a type flag so we can have things like which kind of block was destroyed ( ignored by some persisters ) 
Add the process to snapshot the persisted data at regular intervals ( 5min ) 

Idealy every five minutes we would roll up the entire persistance table into nice objects and insert them as documents into the data store 

Add Option to log events ( then count the events , LogIn , LogOff , ... )

Add JMX Support ( In Progress ) 
