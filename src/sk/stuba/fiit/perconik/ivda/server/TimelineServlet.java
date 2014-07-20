package sk.stuba.fiit.perconik.ivda.server;

import com.google.common.collect.ImmutableList;
import com.google.visualization.datasource.DataSourceServlet;
import com.google.visualization.datasource.base.TypeMismatchException;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.query.Query;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.GregorianCalendar;
import com.ibm.icu.util.TimeZone;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;

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

    private static List<ColumnDescription> columnDescriptions =
            ImmutableList.copyOf(new ColumnDescription[]{
                    new ColumnDescription("start", ValueType.DATETIME, "Start date"),
                    new ColumnDescription("end", ValueType.DATETIME, "End date"),
                    new ColumnDescription("content", ValueType.TEXT, "Content"),
                    new ColumnDescription("group", ValueType.TEXT, "Group") ,
                    new ColumnDescription("className", ValueType.TEXT, "ClassName")
            });

    @Override
    public DataTable generateDataTable(Query query, HttpServletRequest req) {

        Date start = convertDate(req.getParameter("start"));
        Date end = convertDate(req.getParameter("end"));
        logger.info("Start: " + start + " end:" + end);

        // Create a data table.
        DataTable data = new DataTable();
        data.addColumns(columnDescriptions);

        // Fill the data table.
        try {
            GregorianCalendar now = new GregorianCalendar();
            now.setTimeZone(TimeZone.getTimeZone("GMT"));
            GregorianCalendar later = new GregorianCalendar();
            later.setTimeZone(TimeZone.getTimeZone("GMT"));
            later.add(Calendar.HOUR, 1);

            data.addRowFromValues(now, later, "akcia", "Lukas", "normal");
        } catch (TypeMismatchException e) {
            System.out.println("Invalid type!");
        }
        return data;
    }

    @Override
    protected boolean isRestrictedAccessMode() {
        return false;
    }
}