package sk.stuba.fiit.perconik.ivda.server.beans;

import org.apache.log4j.Logger;

import javax.el.ELContext;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Created by Seky on 20. 8. 2014.
 */
public final class FacesUtil {
    private static final Logger LOGGER = Logger.getLogger(FacesUtil.class);

    public static Map<String, String> getQueryParams() {
        return getFacesEContext().getRequestParameterMap();
    }

    public static String getQueryParam(String key) {
        return getFacesEContext().getRequestParameterMap().get(key);
    }

    public static HttpServletRequest getRequest() {
        return (HttpServletRequest) getFacesEContext().getRequest();
    }

    public static HttpServletResponse getResponse() {
        return (HttpServletResponse) getFacesEContext().getResponse();
    }

    public static ExternalContext getFacesEContext() {
        return FacesContext.getCurrentInstance().getExternalContext();
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String bean) {
        ELContext elContext = FacesContext.getCurrentInstance().getELContext();
        return (T) FacesContext.getCurrentInstance().getApplication().getELResolver().getValue(elContext, null, bean);
    }

    public static HttpSession getSession() {
        return (HttpSession) getFacesEContext().getSession(true);
    }

    public static String getContextPath() {
        return getFacesEContext().getRequestContextPath();
    }

    public static void addMessage(String msg, FacesMessage.Severity severity) {
        LOGGER.info("Msg:" + msg);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, msg, null));
    }
}
