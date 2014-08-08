package sk.stuba.fiit.perconik.ivda;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;


@XmlRootElement
public final class Configuration implements Serializable {

    public static final String CONFIG_DIR;
    private static final Logger logger = Logger.getLogger(Configuration.class.getName());
    private static final String FILENAME = "configuration.xml";
    private static final String LOGGING_PROPERTIES_FILE = "log4j.properties";
    private static JAXBContext context = null;
    private static Configuration instance = null;
    private Map<String, String> astRcs = new HashMap<String, String>();
    private URI uacaLink;

    static {
        // Load conf dir
        CONFIG_DIR = System.getProperty("config.dir", System.getProperty("user.dir") + File.separator + "conf");

        // Prepare log4j
        String log4jLoggingPropFile = new File(CONFIG_DIR, LOGGING_PROPERTIES_FILE).getAbsolutePath();
        PropertyConfigurator.configureAndWatch(log4jLoggingPropFile, 30000);

        try {
            context = JAXBContext.newInstance(Configuration.class);
        } catch (JAXBException ex) {
            logger.log(Level.ERROR, null, ex);
        }
    }

    private Configuration() {
    }

    public static Configuration getInstance() {
        if (instance == null) {
            instance = read();
        }
        return instance;
    }

    public static Configuration read() {
        try {
            File file = new File(CONFIG_DIR, FILENAME);
            logger.log(Level.INFO, "Configuration file: " + file.getAbsolutePath());
            return (Configuration) context.createUnmarshaller().unmarshal(file);
        } catch (Exception e) {
            logger.error("Configuration not loaded", e);
            throw new RuntimeException(e);
        }
    }

    public void write() {
        try {
            File file = new File(CONFIG_DIR, FILENAME);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty("jaxb.formatted.output", true);
            marshaller.marshal(this, file);
            logger.info("Configuration file saved: " + file.getAbsolutePath());
        } catch (Exception e) {
            logger.error("Configuration can not be marshalled.", e);
        }
    }

    @Override
    public String toString() {
        try {
            StringWriter writer = new StringWriter();
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty("jaxb.formatted.output", true);
            marshaller.marshal(this, writer);
            return writer.getBuffer().toString();
        } catch (Exception e) {
            logger.error("Configuration can not be marshalled", e);
        }
        return ToStringBuilder.reflectionToString(this);
    }

    public URI getUacaLink() {
        return uacaLink;
    }

    public void setUacaLink(URI uacaLink) {
        this.uacaLink = uacaLink;
    }

    public Map<String, String> getAstRcs() {
        return astRcs;
    }

    public void setAstRcs(Map<String, String> astRcs) {
        this.astRcs = astRcs;
    }
}
