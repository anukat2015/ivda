package sk.stuba.fiit.perconik.ivda.server;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Seky on 9. 11. 2014.
 */
public enum TimeGranularity {
    WEEK(1000 * 60 * 60 * 24 * 7) {
        protected void _roundDate(Calendar c) {
            c.set(Calendar.DAY_OF_WEEK, 0);
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
        }
    },
    DAY(1000 * 60 * 60 * 24) {
        protected void _roundDate(Calendar c) {
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
        }
    },
    HOUR(1000 * 60 * 60) {
        protected void _roundDate(Calendar c) {
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
        }
    },
    MINUTE(1000 * 60) {
        protected void _roundDate(Calendar c) {
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
        }
    },
    PER_VALUE(0) {
        protected void _roundDate(Calendar c) {
        }
    };

    private long threshold;

    TimeGranularity(long threshold) {
        this.threshold = threshold;
    }

    protected abstract void _roundDate(Calendar c);

    public Date roundDate(Date in) {
        Calendar c = Calendar.getInstance();
        c.setTime(in);
        _roundDate(c);
        return c.getTime();
    }

    public Date addThreshold(Date in) {
        return new Date(in.getTime() + threshold);
    }
}
