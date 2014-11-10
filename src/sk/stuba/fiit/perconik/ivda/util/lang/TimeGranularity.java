package sk.stuba.fiit.perconik.ivda.util.lang;

import org.apache.commons.lang.time.DateUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Seky on 9. 11. 2014.
 */
public enum TimeGranularity {
    WEEK(Calendar.WEEK_OF_MONTH),
    DAY(Calendar.DATE),
    HOUR(Calendar.HOUR),
    MINUTE(Calendar.MINUTE),
    PER_VALUE(0) {
        public Date roundDate(Date in) {
            return in;
        }

        public Date increment(Date in) {
            return in;
        }
    };

    private int type;

    TimeGranularity(int type) {
        this.type = type;
    }

    public Date roundDate(Date in) {
        return DateUtils.truncate(in, type);
    }

    public Date increment(Date in) {
        return DateUtils.add(in, type, 1);
    }
}
