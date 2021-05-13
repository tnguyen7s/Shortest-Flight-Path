# Java_AirportNetwork_Graph

*Create a graph data structures: airport as a vertex and route between two airports as a route
*Main problem solved by the app: find the shortest path for a flight to take to travel from airport A to airport B (using Dijkstra's algorithm)

1. Class files: 
+ Airport implements Clonable: stores information of a vertex, override hashCode(), equals(), toString(), clone
+ Route implements Clonable: stores information of an edge, override  hashCode(), equals(), toString(), clone()
+ Data: undertakes the communication with MySQL (loading data from MySQL to the app, inserting/updating/deleting data in MySQL database)
+ AirportGraph extends Data (non-generic class): all operations related to graphs 
+ Pair implements Comparable interface

2. Libraries:
+ Jung Library for graph visualization: http://jung.sourceforge.net/
+ MySQL Connector/J driver to communicate with MySQL server: https://dev.mysql.com/doc/connector-j/8.0/en/

Example Running Result:
![image](https://user-images.githubusercontent.com/70489535/118126195-9da59f00-b3bd-11eb-9387-81cfe344f3ad.png)
![image](https://user-images.githubusercontent.com/70489535/118126227-abf3bb00-b3bd-11eb-8c61-14aa7dd06dc7.png)





