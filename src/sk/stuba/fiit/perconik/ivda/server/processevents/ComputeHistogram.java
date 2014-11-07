package sk.stuba.fiit.perconik.ivda.server.processevents;

import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.util.histogram.Histogram;
import sk.stuba.fiit.perconik.ivda.util.histogram.HistogramByHashTable;

/**
 * Created by Seky on 1. 11. 2014.
 */
public final class ComputeHistogram extends ProcessEvents {
    private final Histogram<String> histogram = new HistogramByHashTable<>();

    @Override
    protected void proccessItem(EventDto event) {
        /*Calendar c = Calendar.getInstance();
        c.setTime(event.getTimestamp());
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        hDni.map(c.getTime());
        */
        histogram.map(event.getUser());
    }

    public Histogram<String> getHistogram() {
        return histogram;
    }
}
