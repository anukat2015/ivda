package sk.stuba.fiit.perconik.ivda.server.process;

import com.gratex.perconik.services.ast.rcs.ChangesetDto;
import com.gratex.perconik.services.ast.rcs.FileVersionDto;
import com.gratex.perconik.services.ast.rcs.RcsProjectDto;
import com.gratex.perconik.services.ast.rcs.RcsServerDto;
import sk.stuba.fiit.perconik.ivda.astrcs.AstRcsWcfService;
import sk.stuba.fiit.perconik.ivda.server.FileVersionsUtil;
import sk.stuba.fiit.perconik.ivda.server.MyDataTable;
import sk.stuba.fiit.perconik.ivda.uaca.client.EventsRequest;
import sk.stuba.fiit.perconik.uaca.dto.EventDto;
import sk.stuba.fiit.perconik.uaca.dto.ide.IdeCodeEventDto;
import sk.stuba.fiit.perconik.uaca.dto.ide.IdeDocumentDto;

/**
 * Created by Seky on 7. 8. 2014.
 */
public class ProcessFileVersions extends ProcessEventsToDataTable {
    public ProcessFileVersions(EventsRequest request) {
        super(request);
    }

    @Override
    protected void proccessItem(EventDto event) {
        if (!(event instanceof IdeCodeEventDto)) return;
        if (!event.getEventTypeUri().toString().contains("code/pastefromweb")) return;
        String action = event.getActionName();

        IdeCodeEventDto cevent = (IdeCodeEventDto) event;
        IdeDocumentDto dokument = cevent.getDocument();
        if (cevent.getStartColumnIndex() != null) {
            LOGGER.info("ZAUJIMAVE getStartColumnIndex nieje null");
        }
        if (cevent.getEndColumnIndex() != null) {
            LOGGER.info("ZAUJIMAVE getEndColumnIndex nieje null");
        }
        if (dokument.getBranch() != null) {
            LOGGER.info("ZAUJIMAVE getBranch nieje null");
        }

        sk.stuba.fiit.perconik.uaca.dto.ide.RcsServerDto rcsServer = dokument.getRcsServer();
        if (rcsServer == null) { // tzv ide o lokalny subor bez riadenia verzii
            LOGGER.info("Lokalny subor");
            return;
        }

        String changesetIdInRcs = dokument.getChangesetIdInRcs();
        if (changesetIdInRcs.isEmpty() || changesetIdInRcs.compareTo("0") == 0) { // changeset - teda commit id nenajdeny
            LOGGER.info("changesetIdInRcs empty");
            return;
        }

        try {
            LOGGER.info("Skopiroval:" + cevent.getText());
            RcsServerDto server = AstRcsWcfService.getInstance().getRcsServerDto(dokument.getRcsServer().getUrl());
            RcsProjectDto project = AstRcsWcfService.getInstance().getRcsProjectDto(server);
            ChangesetDto changeset = AstRcsWcfService.getInstance().getChangesetDto(dokument.getChangesetIdInRcs(), project);
            FileVersionDto fileVersion = AstRcsWcfService.getInstance().getFileVersionDto(changeset, dokument.getServerPath(), project);
            FileVersionsUtil.save(fileVersion);
            // List<ChangesetDto> vysledok = AstRcsWcfService.getChangeset(fileVersion.getEntityId());
        } catch (Exception e) {
            LOGGER.info("proccessItem", e);
        }

        String description = action
                + "<span class=\"more\"><pre>"
                + cevent.getText() + "<br/>"
                + cevent.getStartRowIndex() + "," + cevent.getEndRowIndex() + "<br/>"
                + dokument.getChangesetIdInRcs() + "<br/>"
                + dokument.getServerPath() + "<br/>"
                + rcsServer + "<br/>"
                + "</pre></span>";


        dataTable.add(event.getUser(), event.getTimestamp(), MyDataTable.ClassName.AVAILABLE, "pastefromweb", description);
    }
}
