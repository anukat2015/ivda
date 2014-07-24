package sk.stuba.fiit.perconik.ivda.server;

import com.google.common.collect.ImmutableList;
import com.google.visualization.datasource.base.TypeMismatchException;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.ibm.icu.util.GregorianCalendar;

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

    public void add(String group, GregorianCalendar start, GregorianCalendar end, ClassName className, String content) throws TypeMismatchException {
        addRowFromValues(start, end, content, group, className.toString());
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
