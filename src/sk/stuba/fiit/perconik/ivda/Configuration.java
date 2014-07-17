package sk.stuba.fiit.perconik.ivda;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;


@XmlRootElement
public class Configuration implements Serializable {

    private static final Logger logger = Logger.getLogger(Configuration.class.getName());
    private static JAXBContext context = null;
    private static Configuration instance = null;

    private static final String FILENAME = "configuration.xml";
    public static final String LOGGING_PROPERTIES_FILE = "log4j.properties";
    private static final String CONFIG_DIR;

    private Map<String, String> mapa = new HashMap<String, String>();
    private String url = "";

    static {
        try {
            context = JAXBContext.newInstance(Configuration.class);
        } catch (JAXBException ex) {
            logger.log(Level.ERROR, null, ex);
        }
        CONFIG_DIR = System.getProperty("config.dir");
    }

    private Configuration() {
        mapa.put("debug", "0");
    }

    public static Configuration getInstance() {
        if (instance == null) {
            instance = read();
            if (instance == null) {
                File file = new File(CONFIG_DIR, FILENAME);
                String error = "Configuration file not found '" + file.getAbsolutePath();
                logger.error(error);
                throw new RuntimeException(error);
            }
            logger.info("*********** Configuration **************\n" + instance.toString());
        }
        return instance;
    }

    public void write() {
        FileOutputStream writer = null;
        XMLEncoder encoder = null;
        try {
            File file = new File(CONFIG_DIR, FILENAME);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty("jaxb.formatted.output", true);
            marshaller.marshal(this, file);
            logger.info("Configuration file saved: " + file.getAbsolutePath());
        } catch (Throwable ex) {
            logger.log(Level.ERROR, null, ex);
        }
    }

    public static Configuration read() {
        Configuration konstanten = null;

        try {
            File file = new File(CONFIG_DIR, FILENAME);
            logger.log(Level.INFO, "Configuratio file: " + file.getAbsolutePath());
            konstanten = (Configuration) context.createUnmarshaller().unmarshal(file);
        } catch (Throwable ex) {
            logger.warn("Configuration not loaded");
        }
        return konstanten;
    }

    @Override
    public String toString() {
        String result = null;
        try {
            StringWriter writer = new StringWriter();
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty("jaxb.formatted.output", true);
            marshaller.marshal(this, writer);
            result = writer.getBuffer().toString();
        } catch (JAXBException ex) {
            Logger.getLogger(Configuration.class.getName()).log(Level.ERROR, null, ex);
        }
        if (result == null) {
            result = ToStringBuilder.reflectionToString(this);
        }
        return result;
    }
}
