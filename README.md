# Java_AirportNetwork_Graph
1. Class files: 
+ Airport implements Clonable: stores information of a vertex, override hashCode(), equals(), toString(), clone
+ Route implements Clonable: stores information of an edge, override  hashCode(), equals(), toString(), clone()
+ Data: undertakes the communication with MySQL (loading data from MySQL to the app, inserting/updating/deleting data in MySQL database)
+ AirportGraph extends Data (non-generic class): all operations related to graphs 
+ Pair implements Comparable interface
2. Libraries:
Jung Library for graph visualization: http://jung.sourceforge.net/
MySQL Connector/J driver to communicate with MySQL server: https://dev.mysql.com/doc/connector-j/8.0/en/



