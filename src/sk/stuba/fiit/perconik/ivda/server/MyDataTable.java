package sk.stuba.fiit.perconik.ivda.server;

import com.google.common.collect.ImmutableList;
import com.google.visualization.datasource.base.TypeMismatchException;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.ibm.icu.util.GregorianCalendar;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * Udaje dodavene do group sa nahradzuju inym retazcom.
     * Ochrana sukromia.
     */
    private final Map<String, String> replaceGroup;
    private char alphabetCurrent = 'A';

    public MyDataTable() {
        super();
        replaceGroup = new HashMap<>();
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
        add(group, start, null, className, content, null);
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
        add(group, start, null, className, content, description);
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
        add(group, start, end, className, content, null);
    }

    public void add(String group, GregorianCalendar start, GregorianCalendar end, ClassName className, String content, String description) throws TypeMismatchException {

        // Datatable vyzaduje GMT time zone, ale server v response potom odosle datum bez time zone teda prideme o cast datumu
        if (start != null) {
            start = (GregorianCalendar) start.clone(); // GregorianCalendar asi nie je immutable
            start.roll(GregorianCalendar.HOUR, true); // Java bug http://www.programering.com/a/MTM2ATNwATk.html
            start.roll(GregorianCalendar.HOUR, true); // .add() sa sprava inac
        }
        if (end != null) {
            end = (GregorianCalendar) end.clone();  // klasicky roll sa sprava tiez inac
            end.roll(GregorianCalendar.HOUR, true);  // https://groups.google.com/forum/#!topic/comp.lang.java.programmer/dcQnxrVhSYo
            end.roll(GregorianCalendar.HOUR, true);
        }

        // Nahrad skupinu
        if (group != null) {
            String groupnew = replaceGroup.get(group);
            if (groupnew == null) {
                groupnew = "" + alphabetCurrent;
                replaceGroup.put(group, groupnew);
                alphabetCurrent++;
            }
            group = groupnew;
        }

        // Uloz vysledok
        addRowFromValues(start, end, content, group, className.toString(), description);
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
