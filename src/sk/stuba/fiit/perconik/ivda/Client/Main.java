package sk.stuba.fiit.perconik.ivda.Client;

import com.gratex.perconik.useractivity.app.dto.EventDto;

import java.net.URI;

/**
 * Created by Seky on 22. 7. 2014.
 */
public class Main {
    public static void main(String[] args) {
        URI link = new EventsRequest().setParameters(null).getURI();

        DownloadAll<EventDto> download = new DownloadAll<EventDto>(link, EventsResponse.class) {
            int counter = 0;

            @Override
            protected boolean downloaded(PagedResponse<EventDto> response) {
                counter++;
                if (counter == 3) return false;
                System.out.println();
                System.out.println(response.getResultSet().toString());
                return true;
            }
        };
    }

}
