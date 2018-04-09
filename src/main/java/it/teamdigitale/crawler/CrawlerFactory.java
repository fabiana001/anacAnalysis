package it.teamdigitale.crawler;

import it.teamdigitale.xmlschema.Pubblicazione;

import java.util.List;

public interface CrawlerFactory {

    //public Optional<Pubblicazione> run(String url);
    public List<Pubblicazione.Data.Lotto> run(String url);

}
