package sk.stuba.fiit.perconik.ivda.server;

import com.google.common.collect.ImmutableList;
import com.google.visualization.datasource.base.TypeMismatchException;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.GregorianCalendar;
import com.ibm.icu.util.TimeZone;

import java.util.List;

/**
 * Created by Seky on 22. 7. 2014.
 */
public class MyDataTable extends DataTable {
    private static List<ColumnDescription> columnDescriptions = ImmutableList.copyOf(
            new ColumnDescription[]{
                    new ColumnDescription("start", ValueType.DATETIME, "Start date"),
                    new ColumnDescription("end", ValueType.DATETIME, "End date"),
                    new ColumnDescription("content", ValueType.TEXT, "Content"),
                    new ColumnDescription("group", ValueType.TEXT, "Group"),
                    new ColumnDescription("className", ValueType.TEXT, "ClassName")
            });

    public MyDataTable() {
        super();
        addColumns(columnDescriptions);
    }

    public void AddExample() {
        try {
            GregorianCalendar now = new GregorianCalendar();
            now.setTimeZone(TimeZone.getTimeZone("GMT"));
            GregorianCalendar later = new GregorianCalendar();
            later.setTimeZone(TimeZone.getTimeZone("GMT"));
            later.add(Calendar.HOUR, 1);

            add("Lukas", now, later, "akcia", ClassName.AVAILABLE);
        } catch (TypeMismatchException e) {
            System.out.println("Invalid type!");
        }
    }

    public void add(String group, GregorianCalendar start, GregorianCalendar end, String content, ClassName className) throws TypeMismatchException {
        addRowFromValues(start, end, content, group, className,toString());
    }

    public enum ClassName {
        UNAVAILABLE("unavailable"),
        AVAILABLE("available"),
        MAYBE("maybe");

        private final String name;

        ClassName(String name) {
            this.name = name;
        }

        public static ClassName fromValue(String name) {
            for (ClassName item : ClassName.values()) {
                if (item.name.equals(name)) {
                    return item;
                }
            }
            throw new IllegalArgumentException(name);
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
