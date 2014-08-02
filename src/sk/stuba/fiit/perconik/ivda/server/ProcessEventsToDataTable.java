package sk.stuba.fiit.perconik.ivda.server;

import com.google.visualization.datasource.base.TypeMismatchException;
import com.gratex.perconik.services.AstRcsWcfSvc;
import com.gratex.perconik.services.IAstRcsWcfSvc;
import com.gratex.perconik.services.ast.rcs.GetUserRequest;
import com.gratex.perconik.services.ast.rcs.GetUserResponse;
import com.ibm.icu.util.GregorianCalendar;
import com.ibm.icu.util.TimeZone;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.NtlmAuthenticator;
import sk.stuba.fiit.perconik.ivda.client.DownloadAll;
import sk.stuba.fiit.perconik.ivda.client.EventsRequest;
import sk.stuba.fiit.perconik.ivda.client.EventsResponse;
import sk.stuba.fiit.perconik.ivda.client.PagedResponse;
import sk.stuba.fiit.perconik.ivda.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.dto.ide.IdeCodeEventRequest;
import sk.stuba.fiit.perconik.ivda.dto.ide.IdeDocumentDto;
import sk.stuba.fiit.perconik.ivda.dto.ide.RcsServerDto;

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
        test();
        start();
    }

    public void test() {
        java.net.Authenticator.setDefault(new NtlmAuthenticator("steltecia\\PublicServices", "FiitSvc123."));
        AstRcsWcfSvc tester = new AstRcsWcfSvc();
        IAstRcsWcfSvc service = tester.getPort(IAstRcsWcfSvc.class);

        GetUserRequest req = new GetUserRequest();
        req.setUserId(1);
        GetUserResponse resposne = service.getUser(req);
        logger.info("U " + resposne.toString());
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

        RcsServerDto rcsServer = dokument.getRcsServer();
        if (rcsServer == null) { // tzv ide o lokalny subor bez riadeniaverzii
            logger.info("Lokalny subor");
            return;
        }
        String fragment = rcsServer.getTypeUri().getFragment();

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

