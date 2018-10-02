import pandas as pd

def aggregate_importi(provincia, df_importi_agg, tot_province_bis):
    """Creazione del df che riporta il flusso 
    finanziario dalle stazioni appaltanti di una provincia ai
    fornitori distribuitisul territorio italiano.
    
    :provincia: provincia da considerare
    """
    
    sub_df = df_importi_agg[df_importi_agg['ProvinciaSA'] == provincia]
    df_sum = sub_df.groupby('ProvinciaAggiudicatario').sum()
    df_sum['Provincia'] = df_sum.index
    
    to_add = tot_province_bis.difference(set(df_sum.index))
    values_to_add = [0]*len(to_add)
    zeros_to_add = list(zip(values_to_add, to_add))
    df_sum_extra = pd.DataFrame(zeros_to_add, columns=['Importo', 'Provincia'])
    to_map_zeros = pd.concat([df_sum, df_sum_extra], ignore_index=True)
    to_map = df_sum
    return to_map, to_map_zeros