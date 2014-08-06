package sk.stuba.fiit.perconik.ivda.server;

import com.google.visualization.datasource.base.TypeMismatchException;
import com.gratex.perconik.services.ast.rcs.ChangesetDto;
import com.ibm.icu.util.GregorianCalendar;
import com.ibm.icu.util.TimeZone;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.AstRcsWcfService;
import sk.stuba.fiit.perconik.ivda.client.DownloadAll;
import sk.stuba.fiit.perconik.ivda.client.EventsRequest;
import sk.stuba.fiit.perconik.ivda.client.EventsResponse;
import sk.stuba.fiit.perconik.ivda.client.PagedResponse;
import sk.stuba.fiit.perconik.ivda.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.dto.ide.IdeCodeEventRequest;
import sk.stuba.fiit.perconik.ivda.dto.ide.IdeDocumentDto;

/**
 * Created by Seky on 22. 7. 2014.
 */
public class ProcessEventsToDataTable extends DownloadAll<EventDto> {
    private static final Logger logger = Logger.getLogger(ProcessEventsToDataTable.class.getName());
    private int counter = 0;
    private MyDataTable dataTable;
    private EventsRequest request;

    public ProcessEventsToDataTable(EventsRequest request) {
        super(EventsResponse.class);
        this.request = request;
        dataTable = new MyDataTable();
        start();
    }


    @Override
    protected boolean downloaded(PagedResponse<EventDto> response) {
        counter++;
        //if (counter == 5) return false;

        try {
            for (EventDto event : response.getResultSet()) {
                proccessItem(event);
            }
        } catch (TypeMismatchException e) {
            logger.error("Type mismatch", e);
        }
        return true;   // chceme dalej stahovat
    }


    private void proccessItem(EventDto event) throws TypeMismatchException {
        if (!(event instanceof IdeCodeEventRequest)) return;
        if (!event.getEventTypeUri().toString().contains("code/pastefromweb")) return;
        String action = event.getActionName();
        GregorianCalendar timestamp = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        timestamp.setTime(event.getTimestamp().toGregorianCalendar().getTime());

        IdeCodeEventRequest cevent = (IdeCodeEventRequest) event;
        IdeDocumentDto dokument = cevent.getDocument();
        if (cevent.getStartColumnIndex() != null) {
            logger.info("ZAUJIMAVE getStartColumnIndex nieje null");
        }
        if (cevent.getEndColumnIndex() != null) {
            logger.info("ZAUJIMAVE getEndColumnIndex nieje null");
        }
        if (dokument.getBranch() != null) {
            logger.info("ZAUJIMAVE getBranch nieje null");
        }

        sk.stuba.fiit.perconik.ivda.dto.ide.RcsServerDto rcsServer = dokument.getRcsServer();
        if (rcsServer == null) { // tzv ide o lokalny subor bez riadenia verzii
            logger.info("Lokalny subor");
            return;
        }
        String fragment = rcsServer.getTypeUri().getFragment();

        ChangesetDto changeset = AstRcsWcfService.getChangesetDto(dokument);


        // teraz je otazka co chceme sledovat, povodny kod?
        // alebo chceme sledovat kod ktory sa uz upravil a poslal na server?

        /*
        DOLEZITA JE KONECNOST
        tzv uzivatel skopiroval kod, prepisal ho ....... potom ale mohol skopirovat kod znovu a prepisat ho a to je ako keby zmena nad novym skopirovanim
        cize prve hladanie moze ist az po udalost kedy bolo opat pouzite kopirovanie nad danou entitnou
        */

        // Ak vývojár často používa prehliadač (resp. portál) pri písaní svojho kódu, ako často je potom tento kód prepisovaný?
        // - Pozerat sa na ako casto pouziva prehliadac ?
        // Platí táto tendencia na všetky jeho kódy? ... filter podla suborov, respektive pohlad len na subory
        // Platí to u každého iného vývojára rovnako?" ... vyberat podla vyvojara
        // + zamerat sa na filtre podla obdobia aktivit
        // Granularita na zaklade casu ... cize pozriem na jednu hodinu, co napisal za hodinu a co bolo v tomto kode prepisane ... blbost

        String description = action
                + "<span class=\"more\"><pre>"
                + cevent.getText() + "<br/>"
                + cevent.getStartRowIndex() + "," + cevent.getEndRowIndex() + "<br/>"
                + dokument.getChangesetIdInRcs() + "<br/>"
                + dokument.getServerPath() + "<br/>"
                + rcsServer + "<br/>"
                + "</pre></span>";


        dataTable.add(event.getUser(), timestamp, MyDataTable.ClassName.AVAILABLE, description);
    }

    public MyDataTable getDataTable() {
        return dataTable;
    }

    private void start() {
        downloadedNonRecursive(request.getURI());
    }
};

