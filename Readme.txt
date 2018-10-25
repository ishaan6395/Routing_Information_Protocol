Steps to run this program:

1) Copy the files on this folder to glados/ queeg/ commet/ rhea server.
2) Log in to every server using your credentials.
3) Commands on specific machines:
	command to run on Glados:
	- javac router2.java
	- java router2 configs.txt
	
	command to run on Queeg:
	- javac router2.java
	- java router2 configsqueeg.txt
	
	command to run on Rhea:
	- javac router2.java
	- java router2 configsrhea.txt
	
	command to run on commet:
	- javac router2.java
	- java router2 configscommet.txt

4)	Topology Description:
	Consider a ring Topology like with the following weight:
	,,,,,,,, 	,,,,,,,
	|Glados|____2___|Queeg|
	```````		```````
	  |		   |
	  |		   |
	  |6		   |4
	  |		   |
	  |		   |
	,,,,,,,,	,,,,,,	
	|Commet|____8___|Rhea|
	````````  	``````
	All routers will be connected to each other one by one and will update the weight accordingly.
	
5) On stopping a router the weights are supposed to update according to the latest route.
	  
