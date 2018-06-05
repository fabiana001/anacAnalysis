
## ANAC Notebooks
This folder contains several Jupiter notebooks used for descriptive analysis, for generating ML models and for creating the semantic graph between public administrations and private companies.

The notebooks are organized as following:
- *[Analisi IndicePA](https://github.com/fabiana001/anacAnalysis/blob/master/notebooks/1_Analisi%20IndicePA.ipynb)*: It requires as input the indicePa open data ([here](http://www.indicepa.gov.it/documentale/n-opendata.php) to download the required files) and the list of ANAC public administrations (you need to call the rest api https://dati.anticorruzione.it/rest/legge190/ricerca). The notebook generates a tsv file containing the enriched dataset;
- *[Prima analisi esplorativa](https://github.com/fabiana001/anacAnalysis/blob/master/notebooks/2_Pima%20Analisi%20esplorativa%20ANAC.ipynb)*: runs a simple descriptive analysis on crawled ANAC dataset (you can download the dataset [here](https://cs2.cloudspc.it:8079/swift/v1/ANAC_dataset)) and extracts all public procurements published in 2017. This dataset will be used for the below analysis;
- *[Analisi singolo fornitore](https://github.com/fabiana001/anacAnalysis/blob/master/notebooks/3_Analisi_singolo_fornitore.ipynb)*: runs descriptive analysis for a given private company;
- *[Analisi PA](https://github.com/fabiana001/anacAnalysis/blob/master/notebooks/4_Analisi_PA.ipynb)*: runs descriptive analysis for a given public administration;
- *[Graph layer generation](https://github.com/fabiana001/anacAnalysis/blob/master/notebooks/6_Graph_layer_generation.ipynb)*: generate the semantic graph among public administrations and private companies;     
