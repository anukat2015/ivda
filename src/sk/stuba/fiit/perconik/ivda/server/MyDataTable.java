package sk.stuba.fiit.perconik.ivda.server;

import com.google.common.collect.ImmutableList;
import com.google.visualization.datasource.base.TypeMismatchException;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.ibm.icu.util.GregorianCalendar;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Seky on 22. 7. 2014.
 * Trieda MyDataTable definuje stlpce, ktore pouzivame v klientovi.
 */
public final class MyDataTable extends DataTable implements Serializable {
    /**
     * Definovane stlpce.
     */
    private static final List<ColumnDescription> columnDescriptions = ImmutableList.copyOf(
            new ColumnDescription[]{
                    new ColumnDescription("start", ValueType.DATETIME, "Start date"),
                    new ColumnDescription("end", ValueType.DATETIME, "End date"),
                    new ColumnDescription("content", ValueType.TEXT, "Content"),
                    new ColumnDescription("group", ValueType.TEXT, "Group"),
                    new ColumnDescription("className", ValueType.TEXT, "ClassName"),
                    new ColumnDescription("description", ValueType.TEXT, "Description")
            });

    public MyDataTable() {
        super();
        addColumns(columnDescriptions);
    }

    /**
     * Pridaj riadok.
     *
     * @param group
     * @param start
     * @param className
     * @param content
     * @throws TypeMismatchException
     */
    public void add(String group, GregorianCalendar start, ClassName className, String content) throws TypeMismatchException {
        addRowFromValues(start, null, content, group, className.toString(), null);
    }

    /**
     * Pridaj riadok.
     *
     * @param group
     * @param start
     * @param className
     * @param content
     * @param description
     * @throws TypeMismatchException
     */
    public void add(String group, GregorianCalendar start, ClassName className, String content, String description) throws TypeMismatchException {
        addRowFromValues(start, null, content, group, className.toString(), description);
    }

    /**
     * Pridaj riadok.
     *
     * @param group
     * @param start
     * @param end
     * @param className
     * @param content
     * @throws TypeMismatchException
     */
    public void add(String group, GregorianCalendar start, GregorianCalendar end, ClassName className, String content) throws TypeMismatchException {
        addRowFromValues(start, end, content, group, className.toString(), null);
    }

    /**
     * Definovane CSS styli v klientovi.
     */
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
