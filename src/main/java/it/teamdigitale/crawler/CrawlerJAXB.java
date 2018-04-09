package it.teamdigitale.crawler;

import it.teamdigitale.xmlschema.Pubblicazione;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CrawlerJAXB implements CrawlerFactory{
    Logger logger = LogManager.getLogger(this.getClass().getName());

    static final int CONNECTION_TIMEOUT_MS = 60000;
    static final int MAX_TOTAL = 200;
    static final int MAX_PER_ROUTE = 20;

    JAXBContext jaxbContext = JAXBContext.newInstance(Pubblicazione.Data.class);

    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    HttpClient client = setClient();

    public CrawlerJAXB() throws JAXBException {
    }


    @Override
    public List<Pubblicazione.Data.Lotto> run(String url) {
        //TODO return Data to avoid exception when crawling urls with special chars (e.g. &) as: http://cercalatuascuola.istruzione.it/cercalatuascuola/istituti/BGIC804004/finanza/AVCP?annoScolastico=201718&annoBilancio=2017
        //TODO should handle both Pubblicazioni and Indici

        logger.info("Analysis for url "+ url);

        long start = System.currentTimeMillis();
        Optional<String> optContent = getContent(url);
        long total = System.currentTimeMillis() - start;
        logger.info("\tHttp request completed in " + total + " millisec." );
        if(total > CONNECTION_TIMEOUT_MS) {
            logger.info("\tWARN TIMEOUT. Content size http request: " + optContent.orElse("").length());
        }
        if(!optContent.isPresent())
            return Collections.emptyList();

        Optional<String> substring = getDataRegex(optContent.get());
        if(substring.isPresent()) {
            return getLotti(substring, url);

        } else {
            List<String> urls = getIndiceRegex(optContent.get());
            List<Pubblicazione.Data.Lotto> lotti = new ArrayList<>();
            for(String u: urls){
                List<Pubblicazione.Data.Lotto> data = run(u);
                lotti.addAll(data);
            }
            Pubblicazione.Data res = new Pubblicazione.Data();
            return  lotti;

        }
    }

    /**
     *
     * @param url
     * @return the http content
     */
    private Optional<String> getContent(String url){
        HttpResponse response = null;

        try {
            URI u = new URI(url.trim());

            HttpGet request = new HttpGet(u);

            //request.setConfig(config);
            response = client.execute(request);

            if (response.getStatusLine().getStatusCode() >= 300) {
                throw new RuntimeException(String.format("failure - received a %d for %s.",
                        response.getStatusLine().getStatusCode(), request.getURI().toString()));
            }

            HttpEntity entity = response.getEntity();
            String content = IOUtils.toString(entity.getContent(), "UTF-8");
            return Optional.of(content);

        } catch (URISyntaxException e) {
            logger.error("URISyntaxException with url " + url.toString() + " due the following error " + e.getMessage());
            //e.printStackTrace();
        } catch (IOException e) {
            logger.error("IOException with url " + url.toString() + " due the following error " + e.getMessage());
            //e.printStackTrace();
        } catch (RuntimeException e) {
            logger.error("RuntimeException with url " + url.toString() + " due the following error " + e.getMessage());
            //e.printStackTrace();
        } finally {
            HttpClientUtils.closeQuietly(response);
        }

        return  Optional.empty();
    }

    @Deprecated
    private Optional<Pubblicazione> getPubblicazione(Optional<String> optContent){

        if(!optContent.isPresent())
            return Optional.empty();


        String tags = "(oggetto|ragioneSociale|denominazione|sceltaContraente)";
        String patternOggetto = "<(?:" + tags + ")>([\\S\\s]+?)</\\1>";

        try {
            Pattern pattern = Pattern.compile(patternOggetto);

            String data = optContent.get();
            String res = StringEscapeUtils.unescapeXml(data.substring(data.indexOf("<")));

            Matcher matcher = pattern.matcher(res);

            while (matcher.find()) {
                String substring = matcher.group(2).replaceAll("<", " ").replaceAll(">", " ").replaceAll("&", "e");
                res = new StringBuilder(res).replace(matcher.start(2), matcher.end(2), substring).toString();
            }

            Pubblicazione pubblicazione = (Pubblicazione) jaxbUnmarshaller.unmarshal(new StringReader(res));
            System.out.println("\tCrawlerJackson size: " + pubblicazione.getData().getLotto().size());

            return Optional.of(pubblicazione);

        } catch (JAXBException e) {
            e.printStackTrace();
            System.err.println("JAXBException " + e.getMessage());
        } catch (StringIndexOutOfBoundsException e){
            System.err.println("No xmlFile downloaded");
        }

        return Optional.empty();
    }

    private HttpClient setClient(){
        RequestConfig config = RequestConfig
                .custom()
                .setConnectTimeout(CONNECTION_TIMEOUT_MS)
                .setConnectionRequestTimeout(CONNECTION_TIMEOUT_MS)
                .setSocketTimeout(CONNECTION_TIMEOUT_MS)
                .build();

        PoolingHttpClientConnectionManager syncConnectionManager = new PoolingHttpClientConnectionManager();
        syncConnectionManager.setMaxTotal(MAX_TOTAL);
        syncConnectionManager.setDefaultMaxPerRoute(MAX_PER_ROUTE);
        return HttpClientBuilder.create().setDefaultRequestConfig(config).setConnectionManager(syncConnectionManager).build();

    }


    /**
     *
     * @param optContent
     * @return the data
     */
    private List<Pubblicazione.Data.Lotto> getLotti(Optional<String> optContent, String url) {

        if(!optContent.isPresent())
            return Collections.emptyList();


        String tags = "(oggetto|ragioneSociale|denominazione|sceltaContraente)";
        String patternOggetto = "<(?:" + tags + ")>([\\S\\s]+?)</\\1>";

        try {
            Pattern pattern = Pattern.compile(patternOggetto);

            String content = optContent.get();
            String res = StringEscapeUtils.unescapeXml(content.substring(content.indexOf("<")));

            Matcher matcher = pattern.matcher(res);

            while (matcher.find()) {
                String substring = matcher.group(2).replaceAll("<", " ").replaceAll(">", " ").replaceAll("&", "e");
                res = new StringBuilder(res).replace(matcher.start(2), matcher.end(2), substring).toString();
            }

            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(IOUtils.toInputStream(res, "UTF-8"));

            Pubblicazione.Data data = jaxbUnmarshaller.unmarshal(xmlEventReader, Pubblicazione.Data.class).getValue();
            logger.info("\tCrawlerJAXB size: " + data.getLotto().size());

            return data.getLotto();

        } catch (JAXBException e) {
            //e.printStackTrace();
            logger.error("JAXBException with url" + url.toString() + " due the following error " + e.getMessage());
        } catch (StringIndexOutOfBoundsException e){
            logger.error("No xmlFile downloaded for url " + url.toString());
        } catch (XMLStreamException e) {
            logger.error("XMLStreamException for url " + url.toString()+ " due the following error " + e.getMessage());
        } catch (IOException e) {
            logger.error("IOException " + e.getMessage());
        }

        return  Collections.emptyList();
    }

    /**
     *
     * @param text
     * @return the substring between <data> tags or None
     */
    private Optional<String> getDataRegex(String text)  {
        String substring = StringUtils.substringBetween(text, "<data>", "</data>");
        if(substring == null)
            return Optional.empty();
        else if(substring.length() > 0){
            return Optional.of("<data>"+substring+"</data>");
        } else return Optional.empty();
    }

    /**
     *
     * @param indexXml
     * @return the list of all urls
     */
    private List<String> getIndiceRegex(String indexXml)  {
        ArrayList<String> urls = new ArrayList<>();

        String res = StringEscapeUtils.unescapeXml(indexXml);

        try {
            Pattern pattern = Pattern.compile("<linkDataset>([\\S\\s]+?)</linkDataset>");
            Matcher matcher = pattern.matcher(res);
            while (matcher.find()) {
                String url = matcher.group(1).trim();
                urls.add(url);
            }
        } catch (Exception e) {
            logger.info("Exception during Index extraction: " + e.getMessage());

        }
        return urls;
    }

    public static void main(String[] args) throws IOException, JAXBException {

        CrawlerJAXB cr = new CrawlerJAXB();
        String path = CrawlerJAXB.class.getClassLoader().getResource("no_venezia.xml").getPath();

        byte[] bytes = Files.readAllBytes(Paths.get(path));
        String text = new String(bytes, StandardCharsets.UTF_8);
//
        text = StringEscapeUtils.unescapeXml(text.replaceAll("\n", ""));
        Optional<String> substring = cr.getDataRegex(text);
        List<Pubblicazione.Data.Lotto> data = cr.getLotti(substring, "pippo");
        System.out.println(data.size());
    }
}
