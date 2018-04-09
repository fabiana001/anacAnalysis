//package it.teamdigitale.crawler;
//
//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.core.io.CharacterEscapes;
//import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
//import com.fasterxml.jackson.dataformat.xml.XmlMapper;
//import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
//import it.teamdigitale.xmlschema.Pubblicazione;
//import org.apache.commons.io.IOUtils;
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.config.RequestConfig;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.HttpClientBuilder;
//
//import java.io.IOException;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.Optional;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//@Deprecated
//public class CrawlerJackson {
//
//    static final int CONNECTION_TIMEOUT_MS = 30000;
//    HttpClient client = HttpClientBuilder.create().build();
//    RequestConfig config = RequestConfig
//            .custom()
//            .setConnectTimeout(CONNECTION_TIMEOUT_MS)
//            .setConnectionRequestTimeout(CONNECTION_TIMEOUT_MS)
//            .setSocketTimeout(CONNECTION_TIMEOUT_MS)
//            .build();
//
//    XmlMapper mapper = setMapper();
//
//    private static Optional<Pubblicazione> getPubblicazioneFromText(Optional<String> text){
//
//        if(!text.isPresent())
//            return Optional.empty();
//
//        JacksonXmlModule module = new JacksonXmlModule();
//        module.setDefaultUseWrapper(false);
//
//        XmlMapper mapper = new XmlMapper(module);
//        mapper.registerModule(new JaxbAnnotationModule());
//
//        String tags = "(oggetto|ragioneSociale|denominazione|sceltaContraente)";
//        String patternOggetto = "<(?:" + tags + ")>([\\S\\s]+?)</\\1>";
//
//        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
//        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
//        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
//
//        try {
//            Pattern pattern = Pattern.compile(patternOggetto);
//
//            String res = text.get();
//            Matcher matcher = pattern.matcher(res);
//            while (matcher.find()) {
//                String pippo = matcher.group(2).replaceAll("<", " ").replaceAll(">", " ").replaceAll("&", "e");
//                res = new StringBuilder(res).replace(matcher.start(2), matcher.end(2), pippo).toString();
//            }
//
//            //System.out.println(res);
//
//            Pubblicazione pubblicazione = mapper.readValue(res, Pubblicazione.class);
//            return Optional.of(pubblicazione);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            return Optional.empty();
//        }
//    }
//
//    public static void main(String[] args) throws IOException {
//
//        CrawlerJackson cr = new CrawlerJackson();
//        Optional<Pubblicazione> res = cr.run("http://www.halleyweb.com/c058002/zf/index.php/dataset/appalti-2016.xml");
//        System.out.println(res.get().getData().getLotto().size());
//
//
////        String path = CrawlerJackson.class.getClassLoader().getResource("no_venezia.xml").getPath();
////
////        byte[] bytes = Files.readAllBytes(Paths.get(path));
////        String text = new String(bytes, StandardCharsets.UTF_8);
////
////        text = StringEscapeUtils.unescapeXml(text.replaceAll("\n", ""));
////        System.out.println(text);
////
////        Optional<Pubblicazione> res = CrawlerJackson.getPubblicazioneFromText(Optional.of(text));
////        System.out.println(res.get().getData().getLotto().size());
//
//
//    }
//
//    private XmlMapper setMapper(){
//        // start with set of characters known to require escaping (double-quote, backslash etc)
//        int[] esc = CharacterEscapes.standardAsciiEscapesForJSON();
//        // and force escaping of a few others:
//        esc['<'] = CharacterEscapes.ESCAPE_STANDARD;
//        esc['>'] = CharacterEscapes.ESCAPE_STANDARD;
//        esc['&'] = CharacterEscapes.ESCAPE_STANDARD;
//        esc['\''] = CharacterEscapes.ESCAPE_STANDARD;
//
//
//        JacksonXmlModule module = new JacksonXmlModule();
//        module.setDefaultUseWrapper(false);
//
//        XmlMapper mapper = new XmlMapper(module);
//        mapper.registerModule(new JaxbAnnotationModule());
//
//        return mapper;
//    }
//
//    private Optional<HttpEntity> getEntity(String url){
//
//        Optional<HttpEntity> res = Optional.empty();
//        try {
//            URI u = new URI(url);
//            HttpGet request = new HttpGet(u);
//
//            request.setConfig(config);
//            HttpResponse response = client.execute(request);
//
//            if (response.getStatusLine().getStatusCode() >= 300) {
//                throw new RuntimeException(String.format("failure - received a %d for %s.",
//                        response.getStatusLine().getStatusCode(), request.getURI().toString()));
//            }
//
//            HttpEntity entity = response.getEntity();
//            return Optional.of(entity);
//
//        } catch (URISyntaxException e) {
//            System.err.print("URISyntaxException with url " + url.toString() + "due the following error " + e.getMessage());
//            //e.printStackTrace();
//        } catch (IOException e) {
//            //e.printStackTrace();
//        } catch (RuntimeException e) {
//            System.err.print("RuntimeException with url " + url.toString() + "due the following error " + e.getMessage());
//            //e.printStackTrace();
//        }
//
//        return res;
//    }
//
//    public Optional<Pubblicazione> run(String url){
//
//        Optional<HttpEntity> optEntity = getEntity(url);
//        return getPubblicazione(optEntity);
//    }
//
//    private Optional<Pubblicazione> getPubblicazione(Optional<HttpEntity> optEntity){
//
//        if(!optEntity.isPresent())
//            return Optional.empty();
//
//        JacksonXmlModule module = new JacksonXmlModule();
//        module.setDefaultUseWrapper(false);
//
//        XmlMapper mapper = new XmlMapper(module);
//        mapper.registerModule(new JaxbAnnotationModule());
//
//        String tags = "(oggetto|ragioneSociale|denominazione|sceltaContraente)";
//        String patternOggetto = "<(?:" + tags + ")>([\\S\\s]+?)</\\1>";
//
//
//        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
//        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
//        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
//
//        try {
//            Pattern pattern = Pattern.compile(patternOggetto);
//
//            String res = IOUtils.toString(optEntity.get().getContent(), "UTF-8");
//
//            Matcher matcher = pattern.matcher(res);
//
//            while (matcher.find()) {
//                String substring = matcher.group(2).replaceAll("<", " ").replaceAll(">", " ").replaceAll("&", "e");
//                res = new StringBuilder(res).replace(matcher.start(2), matcher.end(2), substring).toString();
//            }
//
//            Pubblicazione pubblicazione = mapper.readValue(res.replaceAll("\n", "").replaceAll("\t", ""), Pubblicazione.class);
//            return Optional.of(pubblicazione);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            return Optional.empty();
//        }
//    }
//}
