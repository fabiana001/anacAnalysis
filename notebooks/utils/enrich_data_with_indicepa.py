def enrich_data(data):
    """
    Given a list of cf, return a dataframe with additional information (i.e. ["cf", "cod_amm", "regione", "provincia", "comune", "indirizzo", "tipologia_istat", "tipologia_amm"])
    """
    import pandas as pd
    import numpy
    import csv
    
    file_amministrazioni = "data/indicePA/amministrazioni.txt"
    file_aoo = "data/indicePA/aoo.txt"
    file_ou = "data/indicePA/ou.txt"
    file_PEC_CECPAC = "data/indicePA/pec.txt"
    file_serv_fatt = "data/indicePA/serv_fatt.txt"
    
    df_amm = pd.read_csv(file_amministrazioni, sep="\t", dtype=object)
    df_amm = df_amm[[ 'cod_amm','Cf','Comune', 'Provincia', 'Regione', 'Indirizzo', 'tipologia_istat', 'tipologia_amm', 'acronimo']]
    cf_set_amm = set(df_amm.Cf.values)
    
    df_serv_fatt = pd.read_csv(file_serv_fatt, sep="\t", dtype=object)
    df_serv_fatt = df_serv_fatt.rename(columns={'cf': 'Cf'})
    
    cf_set_serv_fatt = set(df_serv_fatt.Cf.values)
    cf_set_amm = set(df_amm.Cf.values)
    
    columns = ["cf", "cod_amm", "regione", "provincia", "comune", "indirizzo", "tipologia_istat", "tipologia_amm"]
            
    final_df = pd.DataFrame(columns=columns)
    
    count = 0
        
    for cf in data:
        if(cf in cf_set_amm):

            try:
                cod_amm = df_amm.loc[df_amm['Cf'] == cf].iloc[0]['cod_amm']
                take0 = df_amm.loc[df_amm['cod_amm'] == cod_amm].iloc[0]
                regione = take0['Regione'].replace("\t", "")
                provincia = str(take0['Provincia']).replace("\t", "")
                comune = take0['Comune'].replace("\t", "")
                indirizzo = take0['Indirizzo'].replace("\t", "")
                tipologia_istat = take0['tipologia_istat'].replace("\t", "")
                tipologia_amm = take0['tipologia_amm'].replace("\t", "")

                res = [cf, cod_amm, regione, provincia, comune, indirizzo, tipologia_istat, tipologia_amm]
                final_df.loc[len(final_df)] = res
            except: # catch *all* exceptions
                print("CF in df_amm",cf)
        elif(cf in cf_set_serv_fatt):
            try:
                cod_amm = df_serv_fatt.loc[df_serv_fatt['Cf'] == cf].iloc[0]['cod_amm']
                take0 = df_amm.loc[df_amm['cod_amm'] == cod_amm].iloc[0]
                regione = take0['Regione'].replace("\t", "")
                provincia = str(take0['Provincia']).replace("\t", "")
                comune = take0['Comune'].replace("\t", "")
                indirizzo = take0['Indirizzo'].replace("\t", "")
                tipologia_istat = take0['tipologia_istat'].replace("\t", "")
                tipologia_amm = take0["tipologia_amm"].replace("\t", "")
                res = [cf, cod_amm, regione, provincia, comune, indirizzo, tipologia_istat, tipologia_amm]
                final_df.loc[len(final_df)] = res
            except: # catch *all* exceptions
                print("CF in df_serv_fatt",cf)
        else:
            count = count + 1

    print("Totale cf non presenti in IndicePA: ", count)
    return final_df
