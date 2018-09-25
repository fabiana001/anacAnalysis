import pandas as pd

class Gara(object):
    """Define an object that corresponds to a Gara"""
    
    def __init__(self, cig, df):
        """Attributes of the Gara
        
        :cig: unique identifier of the gara
        :df: dataframe that contains the gare
        """
        
        self.cig = cig
        self.row_gara = df[df['CIG'] == cig]
        self.fake = self.row_gara['EsisteCIG'][0]
        
        if len(self.row_gara) > 1:
            print ("""Il numero di gare corrispondenti al CIG selezionato
                   è maggiore di 1. Questa situazione può verificarsi quando 
                   si riscontra la presenza di gare duplicate nel dataset o
                   quando ....""")
            
        if self.fake:
            print ("""Il codice identificativo unico di gara non è
                    riconosciuto tra quelli certificati.
                   """)
        
        
        self.aggiudicatario = []
        
        for part in self.row_gara['Partecipanti'][0]:
            print (part)
            try:
                if part['aggiudicatario'] == '1':
                    self.aggiudicatario += [part]
                    break
            except TypeError:
                print ('Nessun partecipante')
                continue

            
        
        print ("E' stato creato l'oggetto della gara (CIG): ", self.cig)
        
    def extract_attribute(self, attribute):
        """Get the inserted attribute
        
        :attribute: name of the attribute to check"""
        
        print ("Il ", attribute, ' della gara è: ', self.row_gara[attribute][0])
        return self.row_gara[attribute]
    
    

class StazioneAppaltante(object):
    """La classe ritorna l'oggetto StazioneAppaltante"""
    
    def __init__(self, df, cf_sa):
        """Restituisce le info geografiche della stazione
        
        :df: dataframe da utilizzare
        :cf_sa: codice fiscale della stazione appaltante
        """
        
        self.cf_sa = cf_sa
        self.df = df[df['CodiceFiscaleAmministrazione'] == cf_sa]
        self.cap = self.df['CapStazioneAppaltante'][0]
        self.citta = self.df['CittaStazioneAppaltante'][0]
        self.natura_giuridica = self.df['NaturaGiuridicaStazioneAppaltante'][0]
        self.indirizzo = self.df['IndirizzoStazioneAppaltante'][0]
        self.provincia = self.df['ProvianciaStazioneAppaltante'][0]
        self.denominazione = self.df['DenominazioneStazioneAppaltante_cc'][0]
        
    
    def _totale_appalti(self):
        """Restituisce il numero totale di appalti della stazione"""
        return self.df.shape[0]
    
    def _appalti_mag_x(self, x):
        """Restituisce il df delle gare con importo maggiore uguale di x
        
        :x: valore dell'importo rispetto cui si filtra il df
        """
        
        df_mag_x = self.df[(self.df['ImportoAggiudicazioneGara'] >= x)]
        
        return df_mag_x
    
    def _appalti_min_x(self, x):
        """Restituisce il df delle gare con importo minore uguale di x
        
        :x: valore dell'importo rispetto cui si filtra il df
        """
        
        df_min_x = self.df[(self.df['ImportoAggiudicazioneGara'] <= x)]
        
        return df_min_x
    
    def _appalti_specifica_scelta_contraente(self, lista_tipologie_affidamento):
        """Restituisce il df degli appalti corrispondenti alle tipologie 
        di affidamento indicate
        
        :lista_tipologie_affidamento: tipologie di affidamento da filtrare e tenere
        """
        
        return self.df['SceltaContraente'].isin(lista_tipologie_affidamento)
    
    def _appalti_affidamento_y_mag_x(self, lista_tipologie_affidamento, x):
        """Restituisce il df degli appalti con importo maggiore a x e con
        tipologie di affidamento stabilite nella lista in input
        
        :x: importo minimo dell'appalto
        :lista_tipologie_affidamento: tipologie di affidamento da filtrare e tenere
        """
        df_mag_x = self._appalti_mag_x(x)
        df_y_mag_x = df_mag_x[df_mag_x['SceltaContraente'].isin(lista_tipologie_affidamento)]
        
        return df_y_mag_x
    
    def _percentuale_appalti_tipologia_y_su_totale_appalti(self, lista_tipologie_affidamento, x):
        """Restituisce la percentuale di appalti con importo superiore ad x e con
        affidamento stabilito dalla lista sul totale degli appalti della stazione
        appaltante.
        
        :x: importo minimo dell'appalto
        :lista_tipologie_affidamento: tipologie di affidamento da filtrare e tenere
        """
        
        n_tot = self._totale_appalti()
        n_spec = self._appalti_affidamento_y_mag_x(lista_tipologie_affidamento, x).shape[0]
        
        return n_spec/n_tot*100
        
