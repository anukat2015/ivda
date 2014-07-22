package sk.stuba.fiit.perconik.ivda.server;

import com.gratex.perconik.useractivity.app.dto.EventDto;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.Client.DownloadAll;
import sk.stuba.fiit.perconik.ivda.Client.EventsRequest;
import sk.stuba.fiit.perconik.ivda.Client.EventsResponse;
import sk.stuba.fiit.perconik.ivda.Client.PagedResponse;

import java.net.URI;

/**
 * Created by Seky on 22. 7. 2014.
 */
public class ProcessEventsToDataTable extends DownloadAll<EventDto> {
    private static final Logger logger = Logger.getLogger(ProcessEventsToDataTable.class.getName());

    private int counter = 0;

    public ProcessEventsToDataTable(URI uri, Class<?> aClass) {
        super(new EventsRequest().setParameters(null).getURI(), EventsResponse.class);
    }

    @Override
    protected boolean downloaded(PagedResponse<EventDto> response) {
        counter++;
        if (counter == 3) return false;
        logger.info(response.getResultSet().toString());
        return false;
    }
};

