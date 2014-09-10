package sk.stuba.fiit.perconik.ivda.server.servlets;

import sk.stuba.fiit.perconik.ivda.util.Configuration;
import sk.stuba.fiit.perconik.ivda.util.cache.OfyService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by Seky on 10. 9. 2014.
 * Event handler pri spusteni a vypnuti deploya.
 */
public class AppInitializer implements ServletContextListener {
    /**
     * Zabezspec aby sa staticke prvky nacitali v spravnom poradi.
     *
     * @param servletContextEvent
     */
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        Configuration.getInstance();
        OfyService.ofy();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
