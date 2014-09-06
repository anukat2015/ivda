package sk.stuba.fiit.perconik.ivda.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.GregorianCalendar;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.uaca.dto.EventDto;

import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Seky on 22. 7. 2014.
 * Trieda MyDataTable definuje stlpce, ktore pouzivame v klientovi.
 */
@NotThreadSafe
public final class MyDataTable extends DataTable implements Serializable {
    protected static final Logger LOGGER = Logger.getLogger(MyDataTable.class.getName());
    private static final long serialVersionUID = 2235473137254607516L;
    /**
     * Definovane stlpce.
     */
    private static final List<ColumnDescription> COLUMN_DESCRIPTIONS = ImmutableList.copyOf(
            new ColumnDescription[]{
                    new ColumnDescription("start", ValueType.DATETIME, "Start date"),
                    new ColumnDescription("end", ValueType.DATETIME, "End date"),
                    new ColumnDescription("content", ValueType.TEXT, "Content"),
                    new ColumnDescription("group", ValueType.TEXT, "Group"),
                    new ColumnDescription("className", ValueType.TEXT, "ClassName"),
                    new ColumnDescription("metadata", ValueType.TEXT, "Metadata")
            });
    private static ObjectMapper mapper = new ObjectMapper();

    public MyDataTable() {
        super();
        addColumns(COLUMN_DESCRIPTIONS);
    }

    /**
     * Datatable vyzaduje GMT time zone, ale server v response potom odosle datum bez time zone teda prideme o cast datumu
     *
     * @param time
     */
    private static GregorianCalendar rollTheTime(@Nullable GregorianCalendar time) {
        if (time != null) {
            time = (GregorianCalendar) time.clone(); // GregorianCalendar asi nie je immutable
            time.roll(Calendar.HOUR, true); // Java bug http://www.programering.com/a/MTM2ATNwATk.html
            time.roll(Calendar.HOUR, true); // klasicky roll sa sprava tiez inac a add() sa sprava tiez inac
        }
        return time;
    }

    @SuppressWarnings("MethodWithTooManyParameters")
    public void add(String group,
                    GregorianCalendar start,
                    @Nullable GregorianCalendar end,
                    ClassName className,
                    @Nullable String content,
                    @Nullable Object metadata
    ) {
        // Uloz vysledok
        try {
            String json = mapper.writeValueAsString(metadata);
            addRowFromValues(rollTheTime(start), rollTheTime(end), content, Developers.blackoutName(group), className.toString(), json);
        } catch (Exception e) {
            LOGGER.error("TypeMismatchException error at MyDataTable.", e);
        }
    }

    public void addEvent(EventDto event, Object metadata) {
        add(event.getUser(), event.getTimestamp(), null, EventsUtil.event2Classname(event), EventsUtil.event2name(event), metadata);
    }

    /**
     * Definovane CSS styli v klientovi.
     */
    public enum ClassName {
        UNAVAILABLE("unavailable"),
        AVAILABLE("available"),
        MAYBE("maybe");

        private final String name;

        ClassName(String type) {
            name = type;
        }

        public static ClassName fromValue(String value) {
            for (ClassName item : ClassName.values()) {
                if (item.name.equals(value)) {
                    return item;
                }
            }
            throw new IllegalArgumentException(value);
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
