package sk.stuba.fiit.perconik.ivda.server.process;

import com.gratex.perconik.services.ast.rcs.ChangesetDto;
import com.gratex.perconik.services.ast.rcs.FileVersionDto;
import com.gratex.perconik.services.ast.rcs.RcsProjectDto;
import com.gratex.perconik.services.ast.rcs.RcsServerDto;
import org.apache.commons.lang.builder.ToStringBuilder;
import sk.stuba.fiit.perconik.ivda.astrcs.AstRcsWcfService;
import sk.stuba.fiit.perconik.ivda.server.FileVersionsUtil;
import sk.stuba.fiit.perconik.ivda.server.MyDataTable;
import sk.stuba.fiit.perconik.ivda.uaca.client.EventsRequest;
import sk.stuba.fiit.perconik.uaca.dto.EventDto;
import sk.stuba.fiit.perconik.uaca.dto.ide.IdeCheckinEventDto;

import java.util.List;

/**
 * Created by Seky on 15. 8. 2014.
 */
public class ProcessChangesets extends ProcessEventsToDataTable {
    public ProcessChangesets(EventsRequest request) {
        super(request);
    }

    @Override
    protected void proccessItem(EventDto event) {
        if (!(event instanceof IdeCheckinEventDto)) return;
        String action = event.getActionName();
        IdeCheckinEventDto cevent = (IdeCheckinEventDto) event;

        sk.stuba.fiit.perconik.uaca.dto.ide.RcsServerDto rcsServer = cevent.getRcsServer();
        if (rcsServer == null) { // tzv ide o lokalny subor bez riadenia verzii
            LOGGER.info("rcsServer empty");
            return;
        }

        String changesetIdInRcs = cevent.getChangesetIdInRcs();
        if (changesetIdInRcs.isEmpty() || changesetIdInRcs.compareTo("0") == 0) { // changeset - teda commit id nenajdeny
            LOGGER.info("changesetIdInRcs empty");
            return;
        }

        try {
            LOGGER.info("-----------------");
            RcsServerDto server = AstRcsWcfService.getInstance().getNearestRcsServerDto(rcsServer.getUrl());
            LOGGER.info(ToStringBuilder.reflectionToString(server));
            RcsProjectDto project = AstRcsWcfService.getInstance().getRcsProjectDto(server);
            LOGGER.info(ToStringBuilder.reflectionToString(project));
            ChangesetDto changeset = AstRcsWcfService.getInstance().getChangesetDto(changesetIdInRcs, project);
            LOGGER.info(ToStringBuilder.reflectionToString(changeset));
            List<FileVersionDto> fileVersion = AstRcsWcfService.getInstance().getFileVersionsDto(changeset, project);

            for (FileVersionDto file : fileVersion) {
                FileVersionsUtil.save(file);
            }
            LOGGER.info("-----------------");
        } catch (AstRcsWcfService.NotFoundException e) {
            LOGGER.info("Chybaju nejake udaje:" + e.getMessage());
            return;
        } catch (Exception e) {
            LOGGER.error("proccessItem", e);
            return;
        }

        String description = action
                + "<span class=\"more\"><pre>"
                + cevent + "<br/>"
                + "</pre></span>";

        dataTable.add(event.getUser(), event.getTimestamp(), MyDataTable.ClassName.AVAILABLE, "IdeCheckinEventDto", description);
    }

}
