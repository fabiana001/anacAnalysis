package it.teamdigitale.datastructure;

public class Partecipanti {
    String cig;
    String codiceFiscale;
    String ragioneSociale;
    boolean isAggiudicatario;

    public String getCig() {
        return cig;
    }

    public void setCig(String cig) {
        this.cig = cig;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

    public String getRagioneSociale() {
        return ragioneSociale;
    }

    public void setRagioneSociale(String ragioneSociale) {
        this.ragioneSociale = ragioneSociale;
    }

    public boolean isAggiudicatario() {
        return isAggiudicatario;
    }

    public void setAggiudicatario(boolean aggiudicatario) {
        isAggiudicatario = aggiudicatario;
    }
}
