# Readme

to start neo4j run

```
docker-compose up -d neo4j
```

It stores the data in the folder `neo4j_data`.
When you run the neo4j the first time it asks you to change the password, thus go to `http://localhost:7474` and change it properly.
Don't forget to update the password in the scripts.
<<<<<<< HEAD
=======

```
MATCH  (n:Struttura)-[r:SEMANTIC]->(b) WHERE n.relevant_terms contains 'auto' or n.codice_fiscale contains '00012140208' return n,r,b limit 1000;
```
>>>>>>> feature/web_api