# Json Database
Database storing data in json format. The repository consist of the database server and client communicating with the server.

## Client arguments:
-t command
* get - get value with specified key
* set - set value
* delete - delete value
* exit - shutdown the server

-k key

-v value

-in path

you might use this flag to specify path to a file containing a command to be executed, in json format

Example:\
-in "test.json"\
-t "get" -k "person"
