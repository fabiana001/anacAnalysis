import os
import sys
import json
import traceback
import pandas as pd
from rdflib import Graph, Literal, BNode, Namespace, RDF, URIRef, Namespace

class RDFConverter:

    # prefixes
    tds  = Namespace("http://td.com/schema/")
    rel  = Namespace("http://td.com/schema/rel/")
    pa   = Namespace("http://td.com/pa/")
    gara = Namespace("http://td.com/gara/")
    fornitore = Namespace("http://td.com/fornitore/")
    
    def __init__(self, input_file, sep="\t", chunksize=1000):
        self.input_file = input_file
        self.graph = Graph()
        self.data  = pd.read_csv(input_file, sep=sep, dtype=object, chunksize=chunksize)

        self.__num_gara  = 0
        self.__num_pa    = 0
        self.__num_fornitore = 0
        self.__num_else  = 0
        self.graph.bind("fornitore", URIRef(str(RDFConverter.fornitore)))
        self.graph.bind("tds",  URIRef(str(RDFConverter.tds)))
        self.graph.bind("rel",  URIRef(str(RDFConverter.rel)))
        self.graph.bind("pa",   URIRef(str(RDFConverter.pa)))
        self.graph.bind("gara", URIRef(str(RDFConverter.gara)))
        self.graph.bind("rdf",  URIRef(str(RDF)))
    
    def convert(self):
        for chunk in self.data:
            for index, row in chunk.iterrows():
                try:
                    self.__convert_row(row)
                except Exception as e:
                    exc_type, exc_obj, exc_tb = sys.exc_info()
                    fname = os.path.split(exc_tb.tb_frame.f_code.co_filename)[1]
                    print("!!! Exception at row: " + str(index))
                    print(e)
                    print(exc_type, fname, exc_tb.tb_lineno)
                    print(row)
                    traceback.format_exc()
                    # log_error(str(e))
                    print()

                if(index % 100000 == 0):
                    print(index)
        print("--- Triples generated ---")
    
    def __convert_row(self, row):
        current_gara = URIRef( RDFConverter.gara + self.__get_new_id("gara") )
        current_pa  = self.__get_pa(row['cfStrutturaProponente'])

        # extra triples
        self.add_triple(current_gara, RDF.type, RDFConverter.tds.gara)
        self.add_triple(current_pa,   RDF.type, RDFConverter.tds.pa)
        self.add_triple(current_pa, RDFConverter.rel.propone, current_gara)

        # cells triples
        # -- cig
        self.__convert_cig(current_gara, row['cig'])

        # -- cfStrutturaProponente
        self.__convert_cf_pa(current_pa, row['cfStrutturaProponente'])

        # -- denominazioneStrutturaProponente
        self.__convert_denominazione_pa(current_pa, row['cfStrutturaProponente'])

        # -- oggetto
        self.__convert_oggetto(current_gara, row['oggetto'])

        # -- sceltaContraente
        self.__convert_scelta_contraente(current_gara, row['sceltaContraente'])

        # -- importoAggiudicazione
        self.__convert_importo_aggiudicazione(current_gara, RDFConverter.to_float_but_carefully(row['importoAggiudicazione']))
        
        # -- importoSommeLiquidate
        self.__convert_importo_somme_liquidate(current_gara, RDFConverter.to_float_but_carefully(row['importoSommeLiquidate']))
        
        # -- dataInizio
        self.__convert_data_inizio(current_gara, row['dataInizio'])
        
        # -- dataUltimazione
        self.__convert_data_fine(current_gara, row['dataUltimazione'])
        
        # -- jsonPartecipanti
        self.__convert_partecipanti(current_gara, row['jsonPartecipanti'])
        
        # -- jsonAggiudicatari
        self.__convert_aggiudicatari(current_gara, row['jsonAggiudicatari'])



    def __convert_cig(self, current_gara, cig):
        if cig != "0000000000":
            self.add_literal_triple(current_gara, RDFConverter.rel.cig, cig)

    def __convert_cf_pa(self, current_pa, cf):
        self.add_literal_triple(current_pa, RDFConverter.rel.cf, cf)

    def __convert_denominazione_pa(self, current_pa, denominazione):
        self.add_literal_triple(current_pa, RDFConverter.rel.denominazione, denominazione)
        
    def __convert_oggetto(self, current_gara, oggetto):
        self.add_literal_triple(current_gara, RDFConverter.rel.oggetto, oggetto)

    def __convert_scelta_contraente(self, current_gara, scelta_contraente):
        self.add_literal_triple(current_gara, RDFConverter.rel.scelta_contraente, scelta_contraente)

    def __convert_importo_aggiudicazione(self, current_gara, importo_aggiudicazione):
        self.add_literal_triple(current_gara, RDFConverter.rel.importo_aggiudicazione, importo_aggiudicazione)

    def __convert_importo_somme_liquidate(self, current_gara, importo_somme_liquidate):
        self.add_literal_triple(current_gara, RDFConverter.rel.importo_somme_liquidate, importo_somme_liquidate)

    def __convert_data_inizio(self, current_gara, data_inizio):
        self.add_literal_triple(current_gara, RDFConverter.rel.data_inizio, data_inizio)

    def __convert_data_fine(self, current_gara, data_fine):
        self.add_literal_triple(current_gara, RDFConverter.rel.data_fine, data_fine)

    def __convert_partecipanti(self, current_gara, partecipanti):
        json_partecipanti = json.loads(partecipanti)
        if json_partecipanti["partecipante"]:

            for partecipante in json_partecipanti["partecipante"]:
                fornitore = self.__get_fornitore(partecipante['codiceFiscale'])
                self.add_triple(fornitore, RDF.type, RDFConverter.tds.fornitore)
                self.add_triple(fornitore, RDFConverter.rel.partecipa, current_gara)

                self.add_literal_triple(fornitore, RDFConverter.rel.cf,  partecipante['codiceFiscale'])
                self.add_literal_triple(fornitore, RDFConverter.rel.ife, partecipante['identificativoFiscaleEstero'])
                self.add_literal_triple(fornitore, RDFConverter.rel.ragione_sociale, partecipante['ragioneSociale'])
            
        if json_partecipanti["raggruppamento"]:
            
            for gruppi in json_partecipanti["raggruppamento"]:
                gruppo = gruppi["membro"]
                blank_node = BNode()
                for membro in gruppo:

                    fornitore = self.__get_fornitore(membro['codiceFiscale'])
                    self.add_triple(fornitore,  RDF.type, RDFConverter.tds.fornitore)
                    self.add_triple(fornitore,  RDFConverter.rel.membro, blank_node)
                    self.add_triple(fornitore,  URIRef(RDFConverter.rel + membro['ruolo'][3:].replace(" ", "-")), blank_node)
                    self.add_triple(blank_node, RDFConverter.rel.partecipa, current_gara)

                    self.add_literal_triple(fornitore, RDFConverter.rel.cf,  membro['codiceFiscale'])
                    self.add_literal_triple(fornitore, RDFConverter.rel.ife, membro['identificativoFiscaleEstero'])
                    self.add_literal_triple(fornitore, RDFConverter.rel.ragione_sociale, membro['ragioneSociale'])

    def __convert_aggiudicatari(self, current_gara, aggiudicatari):
        json_aggiudicatari = json.loads(aggiudicatari)
        if json_aggiudicatari["aggiudicatario"]:
            
            for aggiudicatario in json_aggiudicatari["aggiudicatario"]:
                fornitore = self.__get_fornitore(aggiudicatario['codiceFiscale'])
                self.add_triple(fornitore, RDF.type, RDFConverter.tds.fornitore)
                self.add_triple(fornitore, RDFConverter.rel.vince, current_gara)

                self.add_literal_triple(fornitore, RDFConverter.rel.cf, aggiudicatario['codiceFiscale'])
                self.add_literal_triple(fornitore, RDFConverter.rel.ife, aggiudicatario['identificativoFiscaleEstero'])
                self.add_literal_triple(fornitore, RDFConverter.rel.ragione_sociale, aggiudicatario['ragioneSociale'])
            
        if json_aggiudicatari["aggiudicatarioRaggruppamento"]:
            array_aggiudicatari = json_aggiudicatari['aggiudicatarioRaggruppamento'][0]['membro']
            cf_membri = [membro['codiceFiscale'] for membro in array_aggiudicatari]
            query = self.__find_blank_node_query(current_gara, cf_membri)
            query_result = self.graph.query(query)
            if len(query_result) == 1:
                for res in query_result:
                    self.add_triple(res[0], RDFConverter.rel.vince, current_gara)
        


    def __find_blank_node_query(self, gara, cfs):
        query_params = ""
        var_alias = 0

        for cf in cfs:
            query_params = query_params + "?" + str(var_alias) + " rel:cf \"" + str(cf) + "\" . \n\t"
            query_params = query_params + "?" + str(var_alias) + " rel:membro ?n . \n\t"
            var_alias += 1

        query = """SELECT DISTINCT ?n
                   WHERE {""" + query_params + """
                    ?n rel:partecipa <""" + str(gara) + """> .
                }"""

        return query
        

    def add_triple(self, s, p, o):
        try:
            if s and p and o:
                self.graph.add( (s, p, o) )
        except Exception as e:
            exc_type, exc_obj, exc_tb = sys.exc_info()
            fname = os.path.split(exc_tb.tb_frame.f_code.co_filename)[1]
            print("!!! Bad Triple: " + str(s) + " " + str(p) + " " + str(o))
            print("!!! Bad Triple: " + s + " " + p + " " + o)
            traceback.format_exc()
            # log_error(str(e))
            print(e)
            # print(exc_type, fname, exc_tb.tb_lineno + "\n")

    def add_literal_triple(self, s, p, o):
        if s and p and o and o is not None:
            if isinstance(o, str):
                o = o.replace(" ", "-")
            self.graph.add( (s, p, Literal(o)) )

    def print_on_file(self, output_file, format="turtle"):
        serialized = self.graph.serialize(format=format)

        with open(output_file, "w+") as file:
            file.write(str(serialized, "utf-8").replace("\\n", "\n"))

    def __get_pa(self, cf_pa):
        node = self.graph.value(None, RDFConverter.rel.cf, Literal(cf_pa))
        if not node:
            node = URIRef(RDFConverter.pa + self.__get_new_id("pa"))
        return node

    def __get_fornitore(self, cf_fornitore):
        node = self.graph.value(None, RDFConverter.rel.cf, Literal(cf_fornitore))
        if not node:
            node = URIRef(RDFConverter.fornitore + self.__get_new_id("fornitore"))
        return node

    def to_float_but_carefully(s):
        try:
            return float(s)
        except ValueError:
            return s


    def __get_new_id(self, node_type):
        id = ""
        if node_type == "gara":
            id = "g" + str(self.__num_gara)
            self.__num_gara += 1

        elif node_type == "fornitore":
            id = "f" + str(self.__num_fornitore)
            self.__num_fornitore += 1

        elif node_type == "pa":
            id = "p" + str(self.__num_pa)
            self.__num_pa += 1

        else:
            id = "x" + str(self.__num_else)
            self.__num_else += 1

        return id