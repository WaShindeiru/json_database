# Hyperskill_JSON_Database
Project link: https://hyperskill.org/projects/65
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
