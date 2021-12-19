# Java_AirportNetwork_Graph
Which route I can take to travel between any two airports in the smallest amount of time?
* This application creates a graph data structure: airports as vertices and routes between any two airports as an edge.
* The problem: find the shortest path for a flight to take to travel from airport A to airport B (using Dijkstra's algorithm)
* The application utilizes an open-source database that is created and maintained by OpenFlight. The link to the database can be found here: https://openflights.org/data.html

1. Class files: 
+ Airport implements Clonable: stores information of a vertex, override hashCode(), equals(), toString(), clone
+ Route implements Clonable: stores information of an edge, override  hashCode(), equals(), toString(), clone()
+ Data: undertakes the communication between the application and the MySQL DBMSs(loading data from MySQL to the app, inserting/updating/deleting data into/from MySQL database)
+ AirportGraph extends Data (non-generic class): all operations related to the graph.
+ Pair implements Comparable interface

2. Libraries:
+ Jung Library for graph visualization: http://jung.sourceforge.net/
+ MySQL Connector/J driver to communicate with MySQL server: https://dev.mysql.com/doc/connector-j/8.0/en/

Example Running Result:
![image](https://user-images.githubusercontent.com/70489535/118126195-9da59f00-b3bd-11eb-9387-81cfe344f3ad.png)
![image](https://user-images.githubusercontent.com/70489535/118126227-abf3bb00-b3bd-11eb-8c61-14aa7dd06dc7.png)





