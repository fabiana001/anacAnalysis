# Readme

to start neo4j run

> docker-compose up -d neo4j

It stores the data in the folder  *neo4j_data*.
When you run the neo4j the first time it asks you to change the password, thus go to `http://localhost:7474` and change it properly.
Don't forget to update the password in the scripts.

## Bulk Download

``` bash 
USING PERIODIC COMMIT 500
LOAD CSV WITH HEADERS FROM "file:///nodes.csv" AS csvLine
CREATE (p:Node { id: toInteger(csvLine.id_s), 
type_id: toInteger(csvLine.type_id),
node_type: csvLine.node_type,
fiscal_code: csvLine.fiscal_code,
relevant_terms: csvLine.relevant_terms,
region: csvLine.region,
province: csvLine.province,
city: csvLine.city,
name: csvLine.name,
istat_code: csvLine.istat_code,
administrative_code: csvLine.administrative_code,
company_type: csvLine.company_type,
nation: csvLine.nation})

CREATE CONSTRAINT ON (node:Node) ASSERT node.id IS UNIQUE

##creazione edges
USING PERIODIC COMMIT 500
LOAD CSV WITH HEADERS FROM "file:///edges.csv" AS csvLine
MATCH (node1:Node { id: toInteger(csvLine.src)}), (node2:Node { id: toInteger(csvLine.dst)})
CREATE (node1)-[:SEMANTIC { score: csvLine.score }]->(node2)

[INDEXING]
CREATE INDEX ON :Node(fiscal_code);
CREATE INDEX ON :Node(type_id);
```

## Query Example
```
MATCH  (n:Struttura)-[r:SEMANTIC]->(b) WHERE n.relevant_terms contains 'auto' or n.codice_fiscale contains '00012140208' return n,r,b limit 1000;
```

