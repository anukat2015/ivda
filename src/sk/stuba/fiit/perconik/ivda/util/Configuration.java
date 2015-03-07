package sk.stuba.fiit.perconik.ivda.util;

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


@SuppressWarnings("AccessOfSystemProperties")
@XmlRootElement
public final class Configuration implements Serializable {

    public static final String CONFIG_DIR;
    private static final long serialVersionUID = -1848584185011896784L;
    private static final Logger LOGGER = Logger.getLogger(Configuration.class.getName());
    private static final String FILENAME = "configuration.xml";
    private static final String LOGGING_PROPERTIES_FILE = "log4j.properties";
    private static JAXBContext CONTEXT;

    // Configuration properties
    private Map<String, String> astRcs = new HashMap<>(16);
    private URI uacaLink;
    private URI cordLink;
    private Map<String, String> developers;
    private Boolean cacheEnabled;
    private Integer cacheResponseDuration;
    private Long activityMinIntervalTh;
    private Long browserVsWrittenCodeTh;

    static {
        // Load conf dir
        String defaultDir = System.getProperty("user.dir") + File.separator + "WEB-INF" + File.separator + "conf";
        CONFIG_DIR = System.getProperty("config.dir", defaultDir);

        // Prepare log4j
        //if (!CONFIG_DIR.equals(defaultDir)) { // tzv. Program bezi pravdepodobne lokalne
            String log4jLoggingPropFile = new File(CONFIG_DIR, LOGGING_PROPERTIES_FILE).getAbsolutePath();
            PropertyConfigurator.configure(log4jLoggingPropFile);
        //}

        try {
            CONTEXT = JAXBContext.newInstance(Configuration.class);
        } catch (JAXBException ex) {
            LOGGER.log(Level.ERROR, null, ex);
        }
    }

    private Configuration() {
    }

    public static Configuration getInstance() {
        return ConfigurationHolder.INSTANCE;
    }

    private static synchronized Configuration read() {
        try {
            File file = new File(CONFIG_DIR, FILENAME);
            LOGGER.info("Configuration file: " + file.getAbsolutePath());
            return (Configuration) CONTEXT.createUnmarshaller().unmarshal(file);
        } catch (Exception e) {
            LOGGER.error("Configuration not loaded", e);
            throw new RuntimeException("Configuration not loaded", e);
        }
    }

    private static Marshaller getMarshaller() throws JAXBException {
        Marshaller marshaller = CONTEXT.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", true);
        return marshaller;
    }

    @Override
    public String toString() {
        try {
            StringWriter writer = new StringWriter();
            getMarshaller().marshal(this, writer);
            return writer.getBuffer().toString();
        } catch (Exception e) {
            LOGGER.error("Configuration can not be marshalled", e);
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

    public URI getCordLink() {
        return cordLink;
    }

    public void setCordLink(URI cordLink) {
        this.cordLink = cordLink;
    }

    public Map<String, String> getDevelopers() {
        return developers;
    }

    public void setDevelopers(Map<String, String> developers) {
        this.developers = developers;
    }

    private static class ConfigurationHolder {
        private static final Configuration INSTANCE = read();
    }

    public Boolean getCacheEnabled() {
        return cacheEnabled;
    }

    public void setCacheEnabled(Boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }

    public Integer getCacheResponseDuration() {
        return cacheResponseDuration;
    }

    public void setCacheResponseDuration(Integer cacheResponseDuration) {
        this.cacheResponseDuration = cacheResponseDuration;
    }

    public Long getActivityMinIntervalTh() {
        return activityMinIntervalTh;
    }

    public void setActivityMinIntervalTh(Long activityMinIntervalTh) {
        this.activityMinIntervalTh = activityMinIntervalTh;
    }

    public Long getBrowserVsWrittenCodeTh() {
        return browserVsWrittenCodeTh;
    }

    public void setBrowserVsWrittenCodeTh(Long browserVsWrittenCodeTh) {
        this.browserVsWrittenCodeTh = browserVsWrittenCodeTh;
    }
}
