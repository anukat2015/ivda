package sk.stuba.fiit.perconik.ivda.server.experimental;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.gratex.perconik.services.ast.rcs.ChangesetDto;
import com.gratex.perconik.services.ast.rcs.FileVersionDto;
import com.gratex.perconik.services.ast.rcs.RcsProjectDto;
import com.gratex.perconik.services.ast.rcs.RcsServerDto;
import difflib.Delta;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.activity.client.ActivityService;
import sk.stuba.fiit.perconik.ivda.activity.client.EventsRequest;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.ide.IdeCodeEventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.ide.IdeDocumentDto;
import sk.stuba.fiit.perconik.ivda.astrcs.AstRcsWcfService;
import sk.stuba.fiit.perconik.ivda.util.Diff;
import sk.stuba.fiit.perconik.ivda.util.GZIP;

import javax.annotation.concurrent.NotThreadSafe;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.File;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.List;

@NotThreadSafe
public final class TryBuildHistory {
    private static final Logger LOGGER = Logger.getLogger(TryBuildHistory.class.getName());

    private IdeCodeEventDto event;
    private RcsServerDto server;
    private RcsProjectDto project;
    private ChangesetDto changeset;
    private ChangesetDto successorChangeset;
    private FileVersionDto fileVersion;

    private void eventDownload(String id) {
        if (Strings.isNullOrEmpty(id)) {
            throw new WebApplicationException(
                    Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                            .entity("sid query parameter is empt")
                            .build()
            );
        }

        EventDto event = ActivityService.getInstance().getEvent(id);
        if (event == null) {
            throw new WebApplicationException(
                    Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                            .entity("Event not found")
                            .build()
            );
        }

        if (!(event instanceof IdeCodeEventDto)) {
            return;
        }

        this.event = (IdeCodeEventDto) event;
    }

    public void build() throws Exception {
        String uid = "4f0f7134-ceb8-4ca3-84f1-c40eb16b39e8";
        eventDownload(uid);
        getMoreInformation();
        String path = fileVersion.getUrl().getValue();
        Integer id = fileVersion.getId();
        Integer ancestor = fileVersion.getAncestor1Id().getValue();
        successorChangeset = AstRcsWcfService.getInstance().getChangesetSuccessor(changeset, fileVersion);
        FileVersionDto successor = AstRcsWcfService.getInstance().getFileVersionSuccessor(successorChangeset, fileVersion);

        // Stiahni vsetky 3 verzie
        //List<String> staraVerzia = AstRcsWcfService.getInstance().getContent(path, ancestor); // subor mohol mt v minulosti inu cestu
        List<String> aktualneVerzia = AstRcsWcfService.getInstance().getContent(path, id);
        List<String> buducaVerzia = AstRcsWcfService.getInstance().getContent(successor.getUrl().getValue(), successor.getId());
        //List<Delta> deltasA = Diff.getDiff(aktualneVerzia, staraVerzia);
        List<Delta> deltasB = Diff.getDiff(buducaVerzia, aktualneVerzia);

        // Co dalje ked mame 3 verzie?
        // zisti datum o verzie A - B - C
        // stiahni aktivity mezi datumamy
        getActivities();
        // vyhladaj vsetky Ide eventy
        // pozri sa na to co prepisoval a skladaj subor
        LOGGER.info("koniec");
    }

    private static final File tempFile = new File("C:\\events.gzip");

    private void getActivities() {
        EventsRequest request = new EventsRequest();
        Date start = changeset.getTimeStamp().toGregorianCalendar().getTime();
        Date end = successorChangeset.getTimeStamp().toGregorianCalendar().getTime();
        request.setEventTypeUri(event); // tzv chceme rovnaky typ vytiahnut
        request.setTime(start, end);

        ImmutableList<EventDto> events = null;
        /*
        try {
            events = ActivityService.getInstance().getEvents(request);
            GZIP.serialize(events, tempFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        */

        try {
            events = (ImmutableList<EventDto>) GZIP.deserialize(tempFile);
        } catch (Exception e) {
            e.printStackTrace();
        }


        for (EventDto e : events) {
            if ((e instanceof IdeCodeEventDto)) {
                processIdeBlock((IdeCodeEventDto) e);
            }
        }
    }

    private void processIdeBlock(IdeCodeEventDto e) {
        IdeDocumentDto dokument = e.getDocument();
        sk.stuba.fiit.perconik.ivda.activity.dto.ide.RcsServerDto rcsServer = dokument.getRcsServer();
        if (rcsServer == null) { // tzv ide o lokalny subor bez riadenia verzii
            return;
        }

        String changesetIdInRcs = dokument.getChangesetIdInRcs();
        if (Strings.isNullOrEmpty(changesetIdInRcs) || changesetIdInRcs.compareTo("0") == 0) { // changeset - teda commit id nenajdeny
            return;
        }

        if (!event.getDocument().getServerPath().equals(dokument.getServerPath())) { // TODO: toto nemusi sediet, dokument moihol casom zmenit nazov
            return;
        }

        // ide o rovnaky subor, teda akcia bola vykonana nad tym suborom
        //LOGGER.info(e.getStartRowIndex() + " " + e.getEndRowIndex());
        LOGGER.info(e);
    }

    private void getMoreInformation() {
        IdeDocumentDto dokument = event.getDocument();
        if (event.getStartColumnIndex() != null) {
            LOGGER.warn("ZAUJIMAVE getStartColumnIndex nieje null");
        }
        if (event.getEndColumnIndex() != null) {
            LOGGER.warn("ZAUJIMAVE getEndColumnIndex nieje null");
        }
        if (dokument.getBranch() != null) {
            LOGGER.warn("ZAUJIMAVE getBranch nieje null");
        }

        sk.stuba.fiit.perconik.ivda.activity.dto.ide.RcsServerDto rcsServer = dokument.getRcsServer();
        if (rcsServer == null) { // tzv ide o lokalny subor bez riadenia verzii
            LOGGER.info("Lokalny subor");
            return;
        }

        String changesetIdInRcs = dokument.getChangesetIdInRcs();
        if (Strings.isNullOrEmpty(changesetIdInRcs) || changesetIdInRcs.compareTo("0") == 0) { // changeset - teda commit id nenajdeny
            LOGGER.info("changesetIdInRcs empty");
            return;
        }

        try {
            server = AstRcsWcfService.getInstance().getNearestRcsServerDto(rcsServer.getUrl());
            project = AstRcsWcfService.getInstance().getRcsProjectDto(server);
            changeset = AstRcsWcfService.getInstance().getChangesetDto(dokument.getChangesetIdInRcs(), project);
            fileVersion = AstRcsWcfService.getInstance().getFileVersionDto(changeset, dokument.getServerPath(), project);
        } catch (AstRcsWcfService.NotFoundException e) {
            LOGGER.error("Chybaju nejake udaje:" + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("proccessItem", e);
        }
    }
}
