package sk.stuba.fiit.perconik.ivda.util.lang;

import org.apache.commons.lang.time.DateUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Seky on 9. 11. 2014.
 */
public enum TimeGranularity {
    MONTH(Calendar.MONTH, 1000 * 60 * 60 * 24 * 30),
    DAY(Calendar.DAY_OF_MONTH, 1000 * 60 * 60 * 24),
    HOUR(Calendar.HOUR, 1000 * 60 * 60),
    MINUTE(Calendar.MINUTE, 1000 * 60),
    PER_VALUE(0, 1) {
        public Date roundDate(Date in) {
            return in;
        }

        public Date increment(Date in) {
            return in;
        }
    };

    private int type;
    private long size;

    TimeGranularity(int type, long size) {
        this.type = type;
        this.size = size;
    }

    public Date roundDate(Date in) {
        return DateUtils.truncate(in, type);
    }

    public Date increment(Date in) {
        return DateUtils.add(in, type, 1);
    }

    public long millis() {
        return size;
    }
}
