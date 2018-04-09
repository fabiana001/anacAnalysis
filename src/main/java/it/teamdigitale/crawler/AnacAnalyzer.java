package it.teamdigitale.crawler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import it.teamdigitale.datastructure.StorableLotto;
import it.teamdigitale.datastructure.StringHandler;
import it.teamdigitale.xmlschema.Pubblicazione;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;



public class AnacAnalyzer {

    //public static final String SEPARATOR_JSON = "\t";
    public static final String SEPARATOR_DATA= "\t";
    Logger logger = LogManager.getLogger(this.getClass().getName());


    public ArrayList<IndiceGeneralInfo> getIndiceGeneralInfo() throws IOException {

        InputStream data = getClass().getResourceAsStream("/response.json");
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode rootNode = objectMapper.readTree(data);
        JsonNode resultNode = rootNode.path("result");

        ArrayList<IndiceGeneralInfo> a = new ArrayList<>();
        for (Iterator<JsonNode> e = resultNode.elements(); e.hasNext(); ) {
            try {
                IndiceGeneralInfo indice = objectMapper.treeToValue(e.next(), IndiceGeneralInfo.class);
                a.add(indice);
            } catch (JsonProcessingException ex) {
                logger.error("Unable to convert the object " + e.toString() + " due the following error: " + ex.getMessage());
                ex.printStackTrace();
            }
        }

        return a;

    }

    public static void writeIndiceGeneralInfo(String filename, ArrayList<IndiceGeneralInfo> array) throws IOException {
        File file = new File(filename);
        String header = String.format("%s,%s,%s,%s,%s,%s\n", "identificativoPEC", "codiceFiscale", "ragioneSociale", "url", "dataUltimoTentativoAccessoUrl", "esitoUltimoTentativoAccessoUrl");

        FileUtils.writeStringToFile(file, header, Charset.defaultCharset(), true);

        for (IndiceGeneralInfo i : array) {
            String line = String.format("%s,%s,%s,%s,%s,%s\n", i.identificativoPEC, i.codiceFiscale, i.ragioneSociale, i.url, i.dataUltimoTentativoAccessoUrl, i.esitoUltimoTentativoAccessoUrl);
            FileUtils.writeStringToFile(file, line, Charset.defaultCharset(), true);
        }
    }

    /**
     * Used to handle array with elements having all fields null
     * @param partecipanti
     * @return
     */
    private static int getSizeRaggruppamentiPartecipanti(Pubblicazione.Data.Lotto.Partecipanti partecipanti){
        int size = 0;
        try{
            for (Pubblicazione.Data.Lotto.Partecipanti.Raggruppamento r: partecipanti.getRaggruppamento()){
                if (!r.getMembro().isEmpty())
                    size++;
            }
            return size;
        } catch (Exception e) {
            return size;
        }
    }

    /**
     * Used to handle array with elements having all fields null
     * @param aggiudicatari
     * @return
     */
    private  int getSizeRaggruppamentiAggudicatari(Pubblicazione.Data.Lotto.Aggiudicatari aggiudicatari){
        int size = 0;
        try{
            for (Pubblicazione.Data.Lotto.Aggiudicatari.AggiudicatarioRaggruppamento r: aggiudicatari.getAggiudicatarioRaggruppamento()){
                if (!r.getMembro().isEmpty())
                    size++;
            }

            return size;
        }catch (Exception e) {
            return size;
        }
    }

    private  String removeSeparator(String string, String separator){
        return string.replaceAll(separator,"");
    }

    public  void run(String filenameUrlAnalized, String outputFilename) throws IOException, JAXBException {

        File fileUrlAnalyzed = new File(filenameUrlAnalized);
        File fileOutputFilename = new File(outputFilename);

        FileUtils.writeStringToFile(fileUrlAnalyzed, "url\n", Charset.defaultCharset(), true);
        FileUtils.writeStringToFile(fileOutputFilename, StorableLotto.getHeader(SEPARATOR_DATA)+"\n", Charset.defaultCharset(), true);

        ArrayList<IndiceGeneralInfo> a = getIndiceGeneralInfo();

        CrawlerFactory c = new CrawlerJAXB();
        ObjectWriter ow = new ObjectMapper().writer();

        for(IndiceGeneralInfo indice: a){
            if(!indice.esitoUltimoTentativoAccessoUrl.toLowerCase().equals("successo"))
                continue;

            Long startingTime = System.currentTimeMillis();

            List<Pubblicazione.Data.Lotto> lotti = c.run(indice.url);
            ArrayList<StorableLotto> storableLottoList = new ArrayList<>();
            for(Pubblicazione.Data.Lotto lotto: lotti){
                String cig = lotto.getCig();
                String oggetto = lotto.getOggetto();
                String sceltaContraente = lotto.getSceltaContraente();
                BigDecimal importoAggiudicazione = lotto.getImportoAggiudicazione();
                BigDecimal importoSommeLiquidate = lotto.getImportoSommeLiquidate();
                String dataInizio = null;
                String dataUltimazione = null ;
                String codiceFiscaleStrutturaProponente = lotto.getStrutturaProponente().getCodiceFiscaleProp();
                String ragioneSocialeStrutturaProponente = lotto.getStrutturaProponente().getDenominazione();

                if(lotto.getTempiCompletamento()!= null){
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    if(lotto.getTempiCompletamento().getDataInizio()!= null)
                        dataInizio = formatter.format(lotto.getTempiCompletamento().getDataInizio().toGregorianCalendar().getTime());
                    if(lotto.getTempiCompletamento().getDataUltimazione()!= null)
                        dataUltimazione = formatter.format(lotto.getTempiCompletamento().getDataUltimazione().toGregorianCalendar().getTime());

                }

                String json_partecipanti;

                try {
                    json_partecipanti = Optional.ofNullable(removeSeparator(ow.writeValueAsString(lotto.getPartecipanti()), "\\\\t")).orElse("{}");
                } catch (JsonProcessingException e) {
                    json_partecipanti = "{}";
                    //e.printStackTrace();
                }

                String json_aggiudicatari;
                try {
                    json_aggiudicatari = Optional.ofNullable(removeSeparator(ow.writeValueAsString(lotto.getAggiudicatari()), "\\\\t")).orElse("{}");
                } catch (JsonProcessingException e) {
                    json_aggiudicatari = "{}";
                    //e.printStackTrace();
                }
                int totale_partecipanti = 0;
                if(lotto.getPartecipanti() != null && lotto.getPartecipanti().getPartecipante() != null)
                    totale_partecipanti = lotto.getPartecipanti().getPartecipante().size();

                int totale_raggruppamento_partecipanti = getSizeRaggruppamentiPartecipanti(lotto.getPartecipanti());

                int totale_aggiudicatari = 0;
                if(lotto.getAggiudicatari() != null && lotto.getAggiudicatari().getAggiudicatario() != null)
                    totale_aggiudicatari = lotto.getAggiudicatari().getAggiudicatario().size();

                int totale_raggruppamento_aggiudicatari = getSizeRaggruppamentiAggudicatari(lotto.getAggiudicatari());

                String cf_aggiudicatario = "Raggruppamento";
                String denominazione_aggiudicatario = "Raggruppamento";

                if(lotto.getAggiudicatari() == null){
                    cf_aggiudicatario = null;
                    denominazione_aggiudicatario = null;
                } else if(lotto.getAggiudicatari().getAggiudicatario()!= null && lotto.getAggiudicatari().getAggiudicatario().size() > 0){
                    cf_aggiudicatario =  lotto.getAggiudicatari().getAggiudicatario().get(0).getCodiceFiscale();

                    if(lotto.getAggiudicatari().getAggiudicatario().get(0).getCodiceFiscale() == null)
                        cf_aggiudicatario =  lotto.getAggiudicatari().getAggiudicatario().get(0).getIdentificativoFiscaleEstero();
                    denominazione_aggiudicatario =  lotto.getAggiudicatari().getAggiudicatario().get(0).getRagioneSociale();
                }


                StorableLotto sl = new StorableLotto(
                        StringHandler.removeSpecialSymbols(cig),
                        StringHandler.removeSpecialSymbols(codiceFiscaleStrutturaProponente),
                        StringHandler.removeSpecialSymbols(ragioneSocialeStrutturaProponente),
                        StringHandler.removeSpecialSymbols(oggetto),
                        StringHandler.removeSpecialSymbols(sceltaContraente),
                        importoAggiudicazione,
                        importoSommeLiquidate,
                        dataInizio,
                        dataUltimazione,
                        json_partecipanti,
                        json_aggiudicatari,
                        totale_partecipanti,
                        totale_aggiudicatari,
                        totale_raggruppamento_partecipanti,
                        totale_raggruppamento_aggiudicatari,
                        StringHandler.removeSpecialSymbols(cf_aggiudicatario),
                        StringHandler.removeSpecialSymbols(denominazione_aggiudicatario));
                storableLottoList.add(sl);
            }

        Long totalTime = System.currentTimeMillis() - startingTime;
            logger.info("\tExtraction completed in "+ totalTime +" millisec. Elements extracted: "+ storableLottoList.size());
        for(StorableLotto sl: storableLottoList ){
            FileUtils.writeStringToFile(fileOutputFilename,  sl.toTSV() + "\n", Charset.defaultCharset(), true);

        }
        FileUtils.writeStringToFile(fileUrlAnalyzed, indice.url + "\n", Charset.defaultCharset(), true);

    }

}



    public static void main(String[] args) throws IOException, JAXBException {

        AnacAnalyzer anac = new AnacAnalyzer();
        //ArrayList<IndiceGeneralInfo> a = AnacAnalyzer.getIndiceGeneralInfo();
        //AnacAnalyzer.writeIndiceGeneralInfo("test.csv", a);
        Long suffix = System.currentTimeMillis();
        anac.run("anacDatasetUrl_" +suffix +".txt", "anacDataset_" +suffix +".csv");

    }
}
