package sk.stuba.fiit.perconik.ivda.server;

import com.google.visualization.datasource.DataSourceServlet;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.query.Query;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.Date;

/**
 * Created by Seky on 17. 7. 2014.
 */
public final class TimelineServlet extends DataSourceServlet {
    private static final Logger logger = Logger.getLogger(TimelineServlet.class.getName());

    public Date convertDate(String dateString) {
        try {
            if (dateString == null || dateString.isEmpty()) throw new Exception();
            Long time = Long.valueOf(dateString);
            return new Date(time);
        } catch (Exception e) {
            throw new WebApplicationException("Bad date format " + dateString, Response.Status.BAD_REQUEST);
        }
    }

    @Override
    public DataTable generateDataTable(Query query, HttpServletRequest req) {
        // Pohyb okna nema vplyv na zmenu datumu, cize tensie okno zobrazuje to iste len ide o responzivny dizajn
        // Vplyv na rozsah ma jedine  zoom
        // Cize musime vypocitat sirku okna a poslat to sem

        /*
        Returns an array of item indices whose range or value falls within the start and end properties, which each one of them is a Date object.
         */
        Date start = convertDate(req.getParameter("start"));
        Date end = convertDate(req.getParameter("end"));
        Integer width;
        try {
            width = Integer.valueOf(req.getParameter("width"));
        } catch(NumberFormatException e)  {
            throw new WebApplicationException("Bad width format ", Response.Status.BAD_REQUEST);
        }
        logger.info("Start: " + start + " end:" + end + " width:" + width);

        // Create a data table.
        MyDataTable data = new MyDataTable();

        // Fill the data table.
        //try {
            data.AddExample();
        //} catch (TypeMismatchException e) {
        //    System.out.println("Invalid type!");
        //}
        return data;
    }

    @Override
    protected boolean isRestrictedAccessMode() {
        return false;
    }
}