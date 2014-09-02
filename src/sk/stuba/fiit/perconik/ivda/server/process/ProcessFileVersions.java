package sk.stuba.fiit.perconik.ivda.server.process;

import com.google.common.base.Strings;
import com.gratex.perconik.services.ast.rcs.ChangesetDto;
import com.gratex.perconik.services.ast.rcs.FileVersionDto;
import com.gratex.perconik.services.ast.rcs.RcsProjectDto;
import com.gratex.perconik.services.ast.rcs.RcsServerDto;
import sk.stuba.fiit.perconik.ivda.astrcs.AstRcsWcfService;
import sk.stuba.fiit.perconik.ivda.server.EventsUtil;
import sk.stuba.fiit.perconik.uaca.dto.EventDto;
import sk.stuba.fiit.perconik.uaca.dto.ide.IdeCodeEventDto;
import sk.stuba.fiit.perconik.uaca.dto.ide.IdeDocumentDto;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * Created by Seky on 7. 8. 2014.
 * <p>
 * Stiahni file verziu suboru ktory pride v evente..
 */
@NotThreadSafe
public final class ProcessFileVersions extends ProcessEventsToDataTable {
    @Override
    protected void proccessItem(EventDto event) {
        if (!(event instanceof IdeCodeEventDto)) return;
        //if (!event.getEventTypeUri().toString().contains("code/pastefromweb")) return;

        IdeCodeEventDto cevent = (IdeCodeEventDto) event;
        IdeDocumentDto dokument = cevent.getDocument();
        if (cevent.getStartColumnIndex() != null) {
            LOGGER.warn("ZAUJIMAVE getStartColumnIndex nieje null");
        }
        if (cevent.getEndColumnIndex() != null) {
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
            LOGGER.info("Skopiroval:\\n" + cevent.getText());
            RcsServerDto server = AstRcsWcfService.getInstance().getNearestRcsServerDto(rcsServer.getUrl());
            RcsProjectDto project = AstRcsWcfService.getInstance().getRcsProjectDto(server);
            ChangesetDto changeset = AstRcsWcfService.getInstance().getChangesetDto(dokument.getChangesetIdInRcs(), project);
            FileVersionDto fileVersion = AstRcsWcfService.getInstance().getFileVersionDto(changeset, dokument.getServerPath(), project);
            int size = EventsUtil.codeWritten(cevent.getText());
            if (size > 0) {
                dataTable.addEvent(event, new FileInfo(fileVersion));
            } else {
                LOGGER.warn("Prazdne riadky!");
            }
        } catch (AstRcsWcfService.NotFoundException e) {
            LOGGER.error("Chybaju nejake udaje:" + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("proccessItem", e);
        }
    }

    public static class FileInfo {
        String path;
        Integer id;
        Integer ancestor;

        public FileInfo(FileVersionDto file) {
            path = file.getUrl().getValue();
            id = file.getId();
            ancestor = file.getAncestor1Id().getValue();
        }
    }
}
