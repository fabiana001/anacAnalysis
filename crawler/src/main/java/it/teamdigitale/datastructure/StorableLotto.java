package it.teamdigitale.datastructure;

import com.google.common.base.Optional;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;


public class StorableLotto {
    String cig;
    String cfStrutturaProponente;
    String denominazioneStrutturaProponente;
    String oggetto;
    String sceltaContraente;
    BigDecimal importoAggiudicazione;
    BigDecimal importoSommeLiquidate;
    String dataInizio;
    String dataUltimazione;
    String jsonPartecipanti;
    String jsonAggiudicatari;
    int totalePartecipanti;
    int totaleAggiudicatari;
    int totaleRaggruppamentopartecipanti;
    int totaleRaggruppamentoAggiudicatari;
    String cfPrimoaggiudicatario;
    String denominazionePrimoaggiudicatario;

    @Override
    public String toString() {
        return "StorableLotto{" +
                "cig='" + cig + '\'' +
                ", cfStrutturaProponente='" + cfStrutturaProponente + '\'' +
                ", denominazioneStrutturaProponente='" + denominazioneStrutturaProponente + '\'' +
                ", oggetto='" + oggetto + '\'' +
                ", sceltaContraente='" + sceltaContraente + '\'' +
                ", importoAggiudicazione=" + importoAggiudicazione +
                ", importoSommeLiquidate=" + importoSommeLiquidate +
                ", dataInizio=" + dataInizio +
                ", dataUltimazione=" + dataUltimazione +
                ", jsonPartecipanti='" + jsonPartecipanti + '\'' +
                ", jsonAggiudicatari='" + jsonAggiudicatari + '\'' +
                ", totalePartecipanti=" + totalePartecipanti +
                ", totaleAggiudicatari=" + totaleAggiudicatari +
                ", totaleRaggruppamentopartecipanti=" + totaleRaggruppamentopartecipanti +
                ", totaleRaggruppamentoAggiudicatari=" + totaleRaggruppamentoAggiudicatari +
                ", cfPrimoaggiudicatario='" + cfPrimoaggiudicatario + '\'' +
                ", denominazionePrimoaggiudicatario='" + denominazionePrimoaggiudicatario + '\'' +
                '}';
    }

    public static String getHeader(String sep) {
        return "cig" + sep +
                "cfStrutturaProponente" + sep +
                "denominazioneStrutturaProponente" + sep +
                "oggetto" + sep +
                "sceltaContraente" + sep +
                "importoAggiudicazione" + sep +
                "importoSommeLiquidate" + sep +
                "dataInizio" + sep +
                "dataUltimazione" + sep +
                "jsonPartecipanti" + sep +
                "jsonAggiudicatari" + sep +
                "totalePartecipanti" +sep +
                "totaleAggiudicatari" + sep +
                "totaleRaggruppamentopartecipanti" + sep +
                "totaleRaggruppamentoAggiudicatari" + sep +
                "cfPrimoaggiudicatario" + sep +
                "denominazionePrimoaggiudicatario";

    }

    public String toCSV() {
        return cig + "," +
                cfStrutturaProponente + "," +
                denominazioneStrutturaProponente + "," +
                oggetto + "," +
                sceltaContraente + "," +
                importoAggiudicazione + "," +
                importoSommeLiquidate + "," +
                dataInizio + "," +
                dataUltimazione + "," +
                jsonPartecipanti + "," +
                jsonAggiudicatari + "," +
                totalePartecipanti + "," +
                totaleAggiudicatari + "," +
                totaleRaggruppamentopartecipanti + "," +
                totaleRaggruppamentoAggiudicatari + "," +
                cfPrimoaggiudicatario + "," +
                denominazionePrimoaggiudicatario;
    }

    public String toTSV() {
        String cig = Optional.fromNullable(this.cig).or("").replaceAll("\t", "");
        String cfStrutturaProponente = Optional.fromNullable(this.cfStrutturaProponente).or("").replaceAll("\t", "");
        String denominazioneStrutturaProponente = Optional.fromNullable(this.denominazioneStrutturaProponente).or("").replaceAll("\t", " ");
        String oggetto = Optional.fromNullable(this.oggetto).or("").replaceAll("\t", " ");
        String sceltaContraente = Optional.fromNullable(this.sceltaContraente).or("").replaceAll("\t", "");
        String dataInizio = Optional.fromNullable(this.dataInizio).or("").replaceAll("\t", "");
        String dataUltimazione = Optional.fromNullable(this.dataUltimazione).or("").replaceAll("\t", "");
        String jsonPartecipanti = Optional.fromNullable(this.jsonPartecipanti).or("").replaceAll("\t","");
        String jsonAggiudicatari = Optional.fromNullable(this.jsonAggiudicatari).or("").replaceAll("\t", "");
        String cfPrimoaggiudicatario = Optional.fromNullable(this.cfPrimoaggiudicatario).or("").replaceAll("\t","");
        String denominazionePrimoaggiudicatario = Optional.fromNullable(this.denominazionePrimoaggiudicatario).or("").replaceAll("\t"," ");

        String tsv =  cig + "\t" +
                cfStrutturaProponente + "\t" +
                denominazioneStrutturaProponente + "\t" +
                oggetto + "\t" +
                sceltaContraente + "\t" +
                importoAggiudicazione + "\t" +
                importoSommeLiquidate + "\t" +
                dataInizio + "\t" +
                dataUltimazione + "\t" +
                jsonPartecipanti + "\t" +
                jsonAggiudicatari + "\t" +
                totalePartecipanti + "\t" +
                totaleAggiudicatari + "\t" +
                totaleRaggruppamentopartecipanti + "\t" +
                totaleRaggruppamentoAggiudicatari + "\t" +
                cfPrimoaggiudicatario + "\t" +
                denominazionePrimoaggiudicatario;

        return tsv.replaceAll("\n", " ");
    }

    public StorableLotto(String cig, String cfStrutturaProponente, String denominazioneStrutturaProponente, String oggetto, String sceltaContraente, BigDecimal importoAggiudicazione, BigDecimal importoSommeLiquidate, String dataInizio, String dataUltimazione, String jsonPartecipanti, String jsonAggiudicatari, int totalePartecipanti, int totaleAggiudicatari, int totaleRaggruppamentopartecipanti, int totaleRaggruppamentoAggiudicatari, String cfPrimoaggiudicatario, String denominazionePrimoaggiudicatario) {
        this.cig = cig;
        this.cfStrutturaProponente = cfStrutturaProponente;
        this.denominazioneStrutturaProponente = denominazioneStrutturaProponente;
        this.oggetto = oggetto;
        this.sceltaContraente = sceltaContraente;
        this.importoAggiudicazione = importoAggiudicazione;
        this.importoSommeLiquidate = importoSommeLiquidate;
        this.dataInizio = dataInizio;
        this.dataUltimazione = dataUltimazione;
        this.jsonPartecipanti = jsonPartecipanti;
        this.jsonAggiudicatari = jsonAggiudicatari;
        this.totalePartecipanti = totalePartecipanti;
        this.totaleAggiudicatari = totaleAggiudicatari;
        this.totaleRaggruppamentopartecipanti = totaleRaggruppamentopartecipanti;
        this.totaleRaggruppamentoAggiudicatari = totaleRaggruppamentoAggiudicatari;
        this.cfPrimoaggiudicatario = cfPrimoaggiudicatario;
        this.denominazionePrimoaggiudicatario = denominazionePrimoaggiudicatario;
    }

    public String getCig() {
        return cig;
    }

    public void setCig(String cig) {
        this.cig = cig;
    }

    public String getCfStrutturaProponente() {
        return cfStrutturaProponente;
    }

    public void setCfStrutturaProponente(String cfStrutturaProponente) {
        this.cfStrutturaProponente = cfStrutturaProponente;
    }

    public String getDenominazioneStrutturaProponente() {
        return denominazioneStrutturaProponente;
    }

    public void setDenominazioneStrutturaProponente(String denominazioneStrutturaProponente) {
        this.denominazioneStrutturaProponente = denominazioneStrutturaProponente;
    }

    public String getOggetto() {
        return oggetto;
    }

    public void setOggetto(String oggetto) {
        this.oggetto = oggetto;
    }

    public String getSceltaContraente() {
        return sceltaContraente;
    }

    public void setSceltaContraente(String sceltaContraente) {
        this.sceltaContraente = sceltaContraente;
    }

    public BigDecimal getImportoAggiudicazione() {
        return importoAggiudicazione;
    }

    public void setImportoAggiudicazione(BigDecimal importoAggiudicazione) {
        this.importoAggiudicazione = importoAggiudicazione;
    }

    public BigDecimal getImportoSommeLiquidate() {
        return importoSommeLiquidate;
    }

    public void setImportoSommeLiquidate(BigDecimal importoSommeLiquidate) {
        this.importoSommeLiquidate = importoSommeLiquidate;
    }

    public String getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(String dataInizio) {
        this.dataInizio = dataInizio;
    }

    public String getDataUltimazione() {
        return dataUltimazione;
    }

    public void setDataUltimazione(String dataUltimazione) {
        this.dataUltimazione = dataUltimazione;
    }

    public String getJsonPartecipanti() {
        return jsonPartecipanti;
    }

    public void setJsonPartecipanti(String jsonPartecipanti) {
        this.jsonPartecipanti = jsonPartecipanti;
    }

    public String getJsonAggiudicatari() {
        return jsonAggiudicatari;
    }

    public void setJsonAggiudicatari(String jsonAggiudicatari) {
        this.jsonAggiudicatari = jsonAggiudicatari;
    }

    public int getTotalePartecipanti() {
        return totalePartecipanti;
    }

    public void setTotalePartecipanti(int totalePartecipanti) {
        this.totalePartecipanti = totalePartecipanti;
    }

    public int getTotaleAggiudicatari() {
        return totaleAggiudicatari;
    }

    public void setTotaleAggiudicatari(int totaleAggiudicatari) {
        this.totaleAggiudicatari = totaleAggiudicatari;
    }

    public int getTotaleRaggruppamentopartecipanti() {
        return totaleRaggruppamentopartecipanti;
    }

    public void setTotaleRaggruppamentopartecipanti(int totaleRaggruppamentopartecipanti) {
        this.totaleRaggruppamentopartecipanti = totaleRaggruppamentopartecipanti;
    }

    public int getTotaleRaggruppamentoAggiudicatari() {
        return totaleRaggruppamentoAggiudicatari;
    }

    public void setTotaleRaggruppamentoAggiudicatari(int totaleRaggruppamentoAggiudicatari) {
        this.totaleRaggruppamentoAggiudicatari = totaleRaggruppamentoAggiudicatari;
    }

    public String getCfPrimoaggiudicatario() {
        return cfPrimoaggiudicatario;
    }

    public void setCfPrimoaggiudicatario(String cfPrimoaggiudicatario) {
        this.cfPrimoaggiudicatario = cfPrimoaggiudicatario;
    }

    public String getDenominazionePrimoaggiudicatario() {
        return denominazionePrimoaggiudicatario;
    }

    public void setDenominazionePrimoaggiudicatario(String denominazionePrimoaggiudicatario) {
        this.denominazionePrimoaggiudicatario = denominazionePrimoaggiudicatario;
    }
}
