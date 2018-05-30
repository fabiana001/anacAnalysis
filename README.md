
## ANAC analysis
This project allow us to extract Anac dataset, to realize some descriptive analysis, apply machine learning algorithms, and generate a graph of semantic relationships among public administrations and private companies.

We present this project at [Data Driven Conference](https://2018.datadriveninnovation.org/it/). [Here](https://www.slideshare.net/DataDrivenInnovation/machine-learning-on-public-procurement-open-data-the-anac-case-study-fabiana-lanotte) the presentation.

The project in organized in three main sections:
- *crawler*: java project for crawling data. Starting from a [json file](https://github.com/fabiana001/anacAnalysis/blob/master/crawler/src/main/resources/response.json) containing information about all public administration, it extract the dataset used for our analysis.
- *notebooks*: python project used for dataset analysis.
- *web_api*: a docker-composer file which starts a Neo4j instance and a React web-app which visualize a graph sample.

This is a prototype, so feel free to improve this project o suggest us alternative solutions.   
