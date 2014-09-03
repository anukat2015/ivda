package sk.stuba.fiit.perconik.ivda.server.process;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.gratex.perconik.services.ast.rcs.ChangesetDto;
import com.gratex.perconik.services.ast.rcs.FileVersionDto;
import com.gratex.perconik.services.ast.rcs.RcsProjectDto;
import com.gratex.perconik.services.ast.rcs.RcsServerDto;
import sk.stuba.fiit.perconik.ivda.astrcs.AstRcsWcfService;
import sk.stuba.fiit.perconik.ivda.server.Catalog;
import sk.stuba.fiit.perconik.ivda.server.EventsUtil;
import sk.stuba.fiit.perconik.uaca.dto.EventDto;
import sk.stuba.fiit.perconik.uaca.dto.ide.IdeCodeEventDto;
import sk.stuba.fiit.perconik.uaca.dto.ide.IdeDocumentDto;
import sk.stuba.fiit.perconik.uaca.dto.web.WebNavigateEventDto;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * Created by Seky on 7. 8. 2014.
 * <p>
 * Stiahni file verziu suboru ktory pride v evente..
 */
@NotThreadSafe
public final class ProcessFileVersions extends ProcessEventsToDataTable {
    private final Catalog developerLinks;

    public ProcessFileVersions() {
        developerLinks = Catalog.Processes.BANNED.getList();
    }

    @Override
    protected void proccessItem(EventDto event) {
        if (event instanceof IdeCodeEventDto) {
            ideEvent((IdeCodeEventDto) event);
            return;
        }

        if (event instanceof WebNavigateEventDto) {
            webEvent((WebNavigateEventDto) event);
            return;
        }
    }

    private void webEvent(WebNavigateEventDto event) {
        String link = event.getUrl();
        //if (!developerLinks.contains(link)) {
         //   return;
        //}
        //int changedLines = 20;
        //if (changedLines > 0) {
            dataTable.addEvent(event, ImmutableMap.of(
                    "uid", event.getEventId(),
                    "ajax", event
            ));
        //}
    }

    private void ideEvent(IdeCodeEventDto event) {
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

        sk.stuba.fiit.perconik.uaca.dto.ide.RcsServerDto rcsServer = dokument.getRcsServer();
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
            LOGGER.info("Skopiroval:\\n" + event.getText());
            RcsServerDto server = AstRcsWcfService.getInstance().getNearestRcsServerDto(rcsServer.getUrl());
            RcsProjectDto project = AstRcsWcfService.getInstance().getRcsProjectDto(server);
            ChangesetDto changeset = AstRcsWcfService.getInstance().getChangesetDto(dokument.getChangesetIdInRcs(), project);
            FileVersionDto fileVersion = AstRcsWcfService.getInstance().getFileVersionDto(changeset, dokument.getServerPath(), project);
            int changedLines = EventsUtil.codeWritten(event.getText());
            if (changedLines > 0) {
                Integer ancestor = fileVersion.getAncestor1Id().getValue();
                dataTable.addEvent(event, ImmutableMap.of(
                        "uid", event.getEventId(),
                        "path", fileVersion.getUrl().getValue(),
                        "id", fileVersion.getId(),
                        "ancestor", ancestor == null ? 0 : ancestor,
                        "changedLines", changedLines
                ));
            } else {
                LOGGER.warn("Prazdne riadky!");
            }
        } catch (AstRcsWcfService.NotFoundException e) {
            LOGGER.error("Chybaju nejake udaje:" + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("proccessItem", e);
        }
    }
}
