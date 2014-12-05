package sk.stuba.fiit.perconik.ivda.server.processevents;

import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.util.lang.TimeGranularity;
import sk.stuba.fiit.perconik.ivda.util.histogram.Histogram;
import sk.stuba.fiit.perconik.ivda.util.histogram.HistogramBySiblings;
import sk.stuba.fiit.perconik.ivda.util.lang.ProcessIterator;

import java.util.Date;

/**
 * Created by Seky on 1. 11. 2014.
 */
public class CountEventsHistogram extends ProcessIterator<EventDto> {
    private final Histogram<Date> histogram = new HistogramBySiblings<>();
    private final TimeGranularity granularity;

    public CountEventsHistogram(TimeGranularity granularity) {
        this.granularity = granularity;
    }

    @Override
    protected void proccessItem(EventDto event) {
        Date date = granularity.roundDate(event.getTimestamp());
        histogram.map(date);
    }

    public Histogram<Date> getHistogram() {
        return histogram;
    }

    public TimeGranularity getGranularity() {
        return granularity;
    }
}
