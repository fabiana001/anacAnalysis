import numpy as np
import pandas as pd
import csv
import datetime
from time import time
from sklearn.metrics.pairwise import cosine_similarity
import faiss

from nltk.stem.snowball import ItalianStemmer
from sklearn.feature_extraction.text import TfidfVectorizer
from scipy.sparse.csr import csr_matrix #need this if you want to save tfidf_matrix
import nltk
#nltk.download('stopwords')
from nltk.corpus import stopwords
from pylab import *

from IPython.display import Image
from IPython.display import display

def inner_product_denseMatrix(matrix, k):
    """
    matrix: input dense matrix. If matrix is normalized (norm L2), then the inner product coincides with the cosine.
    Note if matrix is sparse use the method cosine_similarity_sparseMatrix()
    k: number of best elements to return for each matrix element

    returns the matrices I, D where I is the results matrix, containing the best k document indices. Matrix D is the matrix of squared distances. It has the same shape as I and indicates for each result vector at the query’s squared Euclidean distance.

    """

    index = faiss.IndexFlatIP(matrix.shape[1])   # build the index using inner product (ip)
    print("Train index: ",index.is_trained)
    index.add(matrix)                  # add vectors to the index
    print("Total elements to analyze: ",index.ntotal)

    D, I = index.search(matrix, k) # sanity check

    return I, D

def cosine_similarity_sparseMatrix(m1, k , batch_size=100):
    """
    m1: input sparse matrix. If m1 is dense run cosine_similarity_denseMatrix
    k: number of best elements to return for each matrix element
    batch_size

    returns the matrices I, D where I is the results matrix, containing the best k document indices. Matrix D is the matrix of cosine similarity having the same shape as I.
    """

    n_rows = m1.shape[0]
    similarity_matrix = np.ndarray((n_rows,k), "float32")
    document_matrix = np.ndarray((n_rows,k), int)

    for row_i in range(0, int(m1.shape[0] / batch_size) + 1):
        t0 = time.time()

        start = row_i * batch_size
        end = min([(row_i + 1) * batch_size, m1.shape[0]])
        if end <= start:
            print("inconsistent indices between starting batch and adding batch")
            print(start, end)
            break
        rows = m1[start: end]
        sim = cosine_similarity(rows, m1).astype(np.float32) # rows is O(1) size

        res = [getKNN(sim[i], k) for i in range(sim.shape[0])]

        rows =[row[0] for row in res]
        similarities = [row[1] for row in res]


        document_matrix[start : end] = rows
        similarity_matrix[start :end] = similarities


        total = time.time() - t0
        print("Cosine similarity batch %d done in %0.3f sec" % (start, total))

    return document_matrix, similarity_matrix

def getKNN(sim_array, k):
    """
    sim_array: cosine similarity array
    k: number of nearest neighbors to return

    return the K nearest neighbors of input array with its similarity
    """

    args_ids = np.argsort(sim_array.data)[-k:]
    reverted_args_id = args_ids[::-1]
    similarities = [round(x,2) for x in sim_array[reverted_args_id]]

    return reverted_args_id, similarities

def aggregateByOggetto(col_groupBy, col_toAgg, df):
    """
    col_groupBy: è la colonna per la quale effettuare l'aggragazione
    col_toAgg: è la colonna contenente le informazioni non strutturate, i.e. campo "oggetto"
    df: input dataframe
    """

    df_noEmpty = df.dropna(subset=[col_toAgg], axis=0)
    new_series = df_noEmpty.groupby(col_groupBy)[col_toAgg].apply(lambda x: ' '.join(x))

    return pd.DataFrame(new_series)

def createSyntheticDF(df, columns_to_aggregate = {("cfStrutturaProponente","PA"), ("cfPrimoaggiudicatario","AGG")}):
    """
    df: input dataframe
    columns_to_aggregate: dictionary K,V where K is the column in df to aggregate and V is the structure type (i.e. tipoStruttura in the final dataframe)

    returns a df having the following structure  pd.DataFrame(columns=['codiceFiscaleStruttura', 'oggetto', "tipoStruttura"])
    """

    final_df = pd.DataFrame(columns=['codiceFiscaleStruttura', 'oggetto', "tipoStruttura"])

    for col, type_col in columns_to_aggregate:
        t0 = time.time()

        df_no_null = df[pd.notnull(df[col])]
        aggregate_df = aggregateByOggetto(col, "oggetto", df_no_null)
        total = time.time() - t0

        aggregate_df['tipoStruttura'] = [type_col] * aggregate_df.shape[0]
        aggregate_df.reset_index(inplace = True)
        aggregate_df.rename(columns={col: 'codiceFiscaleStruttura'}, inplace = True)

        final_df = final_df.append(aggregate_df)

        print("Aggregation of attribute %s done in %0.3f sec" % (col, total))
    return final_df

def top_tfidf_feats(row, features, top_n=25):
    ''' Get top n tfidf values in the input row and return them with their corresponding feature names (e.g. stand:0.14 catering:0.12 fornitura:0.10).'''
    topn_ids = np.argsort(row)[::-1][:top_n]
    #top_feats = [(features[i], row[i]) for i in topn_ids if row[i] > 0]
    top_feats = [features[i] + ":" + "{0:.2f}".format(row[i]) for i in topn_ids if row[i] > 0]
    #df = pd.DataFrame(top_feats)
    #df.columns = ['feature', 'tfidf']
    return top_feats

def top_feats_in_doc(Xtr, features, row_id, top_n=25):
    ''' Top tfidf features in specific document (matrix row) '''
    row = np.squeeze(Xtr[row_id].toarray())
    return top_tfidf_feats(row, features, top_n)

def get_best_terms_frequencies(Xtr, features, rows_id, top_n=25):
    return [' '.join(top_feats_in_doc(Xtr,  features, row ,top_n)) for row in rows_id]

def calculate_embedding(tf, tfidf_matrix, embedding_model, n_rows, weigth_embedding = False) :
    """
    tf: Tf-idf vectorizer
    tfidf_matrix: matrix of tfidf
    n_row: number of rows to analize
    weigth_terms: Boolean. If true weigth the term embedding with its tfidf

    returns embedded matrix for each row in tfidf_matrix
    """

    embedded_matrix = np.zeros((n_rows,300), "float32")
    index_term_vocabulary = tf.get_feature_names()

    for i in range(0, n_rows):

        counter_d = 0

        #for term in the document:
        for index_t in tfidf_matrix[i].indices:

            tfidf = tfidf_matrix[i,index_t]
            term = index_term_vocabulary[index_t]

            if term in embedding_model.vocab:

                    if(weigth_embedding):
                        counter_d += tfidf
                        embedded_matrix[i] += (embedding_model.get_vector(term) * tfidf)
                    else:
                        counter_d += 1
                        embedded_matrix[i] += embedding_model.get_vector(term)


        if counter_d > 0:

            embedded_matrix[i] /= counter_d

    return embedded_matrix

class StemmedCountVectorizer(TfidfVectorizer):

    def build_analyzer(self):
        analyzer = super(StemmedCountVectorizer, self).build_analyzer()
        return lambda doc: ([italian_stemmer.stem(w) for w in analyzer(doc)])

def get_TFIDFmatrix_vect(data, do_stemming):
    """
    data: input textual collection
    do_stemming: boolean. If True execute stemming, otherwise analyze only tokenized words (words are composed at least 2 chars and do not contains numbers)

    returns a tuple <tf, matrix> where tf is the vectorizer and matrix is the normalized matrix of tfidf
    """

    min_df = 10

    if do_stemming:
        italian_stemmer = ItalianStemmer()
        tf = textual_analysis.StemmedCountVectorizer(token_pattern=u'([a-z]{2,})', min_df = min_df, analyzer="word", stop_words=stopwords.words('italian'),  norm='l2')

    else:
        tf = TfidfVectorizer(token_pattern=u'([a-z]{2,})',  sublinear_tf = True, use_idf = True, stop_words=stopwords.words('italian'), max_df = 0.1, min_df = min_df, norm='l2') #CountVectorizer supports counts of N-grams of words or consecutive characters.

    matrix = tf.fit_transform(data)

    return matrix, tf

def plotTopNWords(sorted_frequents_words, N, title):
    """frequents_words: list of frequents words. Type: tuple(str, numpy.int64)
       N: number of words to plot
    """

    x, y = zip(*sorted_frequents_words[0:N]) # unpack a list of pairs into two tuples

    fig = plt.figure()
    ax1 = fig.add_subplot(111)  # Create matplotlib axes
    ax1.set_title(title)
    plt.ylabel('avg tf-idf score')
    ax1.plot(x, y)

    for tl in ax1.get_xticklabels():
        tl.set_rotation(90)

    file_name = 'imgs/top'+str(N)+'_words_plot.png'
    fig.tight_layout()
    plt.savefig(file_name, pad = 0) #png

    plt.clf
    return file_name

def boxplotdata(data_to_plot, title, x_names, filename):
    """
    data_to_plot: array containing variables to plot (e.g. [a,b,c] where a,b,c are arrays)
    title of the boxplot
    x_names: list containing axis names (e.g. ["boxplot1", "boxplot2"])
    filename: filename where saving the boxplot
    """

    dim = range(1,len(x_names) +1)

    # Create a figure instance
    fig = plt.figure(1, figsize=(9, 6))

    # Create an axes instance
    ax = fig.add_subplot(111)

    # Create the boxplot
    bp = ax.boxplot(data_to_plot)
    plt.xticks(dim, x_names)
    plt.title(title)


    for line in bp['medians']:
        # get position data for median line
        x, y = line.get_xydata()[1] # top of median line
        # overlay median value
        text(x + 0.05, y, '%.2f' % y, horizontalalignment='center') # draw above, centered

    for line in bp['boxes']:
        x, y = line.get_xydata()[0] # bottom of left line
        text(x - 0.05,y, '%.2f' % y, horizontalalignment='center', verticalalignment='top')      # below
        x, y = line.get_xydata()[3] # bottom of right line
        text(x - 0.05,y, '%.2f' % y, horizontalalignment='center', verticalalignment='top')      # below

    plt.savefig(filename)
    plt.clf()
    plt.close()

from pylab import *

def bar_plot(values, title, filename):

    # Turn interactive plotting off
    plt.ioff()

    p1 = plt.bar(values.index, values.values)

    plt.xlabel('# common documents')
    plt.title(title)

    #plt.show()
    plt.savefig(filename)
    plt.close()
    plt.clf
    i = Image(filename)
    display(i)

def get_intersection(set1, set2):
    intersection = []
    rows = len(set1)
    k = len(set1[0])
    for row in range(0, rows):
        i =  set(set1[row]).intersection(set(set2[row]))
        #res = len(i) / k
        #intersection.append(float("{0:.2f}".format(res)))
        res = len(i)
        intersection.append(res)

    #return pd.value_counts(intersection)
    return intersection

def box_plot(values, axis_names, title, filename):

    # Create a figure instance
    fig = plt.figure(1, figsize=(9, 6))

    # Create an axes instance
    ax = fig.add_subplot(111)

    # Create the boxplot
    bp = ax.boxplot(values)
    dim = range(1, len(axis_names)+1)

    plt.xticks(dim, axis_names, rotation=90)
    plt.title(title)


    for line in bp['medians']:
        # get position data for median line
        x, y = line.get_xydata()[1] # top of median line
        # overlay median value
        text(x + 0.05, y, '%.2f' % y, horizontalalignment='center') # draw above, centered

    for line in bp['boxes']:
        x, y = line.get_xydata()[0] # bottom of left line
        text(x - 0.05,y, '%.2f' % y, horizontalalignment='center', verticalalignment='top')      # below
        x, y = line.get_xydata()[3] # bottom of right line
        text(x - 0.05,y, '%.2f' % y, horizontalalignment='center', verticalalignment='top')      # below

    # Save the figure

    fig.savefig(filename, bbox_inches='tight')

    plt.close()
    plt.clf
    i = Image(filename)
    display(i)
