import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import it.teamdigitale.crawler.CrawlerJAXB;
import it.teamdigitale.xmlschema.Pubblicazione;
import org.apache.commons.io.IOUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;
import javax.xml.xpath.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Prova {


    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException, XMLStreamException {

        String path = CrawlerJAXB.class.getClassLoader().getResource("no_venezia.xml").getPath();

        byte[] bytes = Files.readAllBytes(Paths.get(path));
        String text = new String(bytes, StandardCharsets.UTF_8);

        String tags = "(oggetto|ragioneSociale|denominazione|sceltaContraente)";
        String patternOggetto = "<(?:" + tags + ")>([\\S\\s]+?)</\\1>";
        String res = StringEscapeUtils.unescapeXml(text.substring(text.indexOf("<")));
        Pattern pattern = Pattern.compile(patternOggetto);

        Matcher matcher = pattern.matcher(res);

        while (matcher.find()) {
            String substring = matcher.group(2).replaceAll("<", " ").replaceAll(">", " ").replaceAll("&", "e");
            res = new StringBuilder(res).replace(matcher.start(2), matcher.end(2), substring).toString();
        }


        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        XMLEventReader eventReader = xmlInputFactory.createXMLEventReader(IOUtils.toInputStream(res, "UTF-8"));


        //XMLEvent event = eventReader.nextEvent();

        while(eventReader.hasNext())
        {
            XMLEvent event = eventReader.nextEvent();

            if (event.isStartElement()) {
                Iterator<Attribute> itr = event.asStartElement().getAttributes();
                while(itr.hasNext()){
                    Attribute attribute = itr.next();
                    System.out.println(attribute);
                    //attribute. //get name and value here
                }
            }
        }


    }
}
