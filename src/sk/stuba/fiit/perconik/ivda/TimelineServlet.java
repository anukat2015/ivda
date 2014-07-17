package sk.stuba.fiit.perconik.ivda;

import com.google.visualization.datasource.DataSourceServlet;
import com.google.visualization.datasource.base.TypeMismatchException;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.query.Query;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * Created by Seky on 17. 7. 2014.
 */
public class TimelineServlet extends DataSourceServlet {

    @Override
    public DataTable generateDataTable(Query query, HttpServletRequest request) {
        // Create a data table.
        DataTable data = new DataTable();
        ArrayList<ColumnDescription> cd = new ArrayList<ColumnDescription>();
        cd.add(new ColumnDescription("name", ValueType.TEXT, "Animal name"));
        cd.add(new ColumnDescription("link", ValueType.TEXT, "Link to wikipedia"));
        cd.add(new ColumnDescription("population", ValueType.NUMBER, "Population size"));
        cd.add(new ColumnDescription("vegeterian", ValueType.BOOLEAN, "Vegetarian?"));

        data.addColumns(cd);

        // Fill the data table.
        try {
            data.addRowFromValues("Aye-aye", "http://en.wikipedia.org/wiki/Aye-aye", 100, true);
            data.addRowFromValues("Sloth", "http://en.wikipedia.org/wiki/Sloth", 300, true);
            data.addRowFromValues("Leopard", "http://en.wikipedia.org/wiki/Leopard", 50, false);
            data.addRowFromValues("Tiger", "http://en.wikipedia.org/wiki/Tiger", 80, false);
        } catch (TypeMismatchException e) {
            System.out.println("Invalid type!");
        }
        return data;
    }

    /**
     * NOTE: By default, this function returns true, which means that cross
     * domain requests are rejected.
     * This check is disabled here so examples can be used directly from the
     * address bar of the browser. Bear in mind that this exposes your
     * data source to xsrf attacks.
     * If the only use of the data source url is from your application,
     * that runs on the same domain, it is better to remain in restricted mode.
     */
    @Override
    protected boolean isRestrictedAccessMode() {
        return false;
    }
}