package sk.stuba.fiit.perconik.ivda.server.process;

import com.google.common.io.Files;
import com.google.visualization.datasource.base.TypeMismatchException;
import com.gratex.perconik.services.ast.rcs.ChangesetDto;
import com.gratex.perconik.services.ast.rcs.FileVersionDto;
import com.gratex.perconik.services.ast.rcs.RcsProjectDto;
import com.gratex.perconik.services.ast.rcs.RcsServerDto;
import org.apache.commons.lang.builder.ToStringBuilder;
import sk.stuba.fiit.perconik.ivda.astrcs.AstRcsWcfService;
import sk.stuba.fiit.perconik.ivda.server.MyDataTable;
import sk.stuba.fiit.perconik.ivda.uaca.client.EventsRequest;
import sk.stuba.fiit.perconik.ivda.uaca.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.uaca.dto.ide.IdeCheckinEventDto;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by Seky on 15. 8. 2014.
 */
public class ProcessChangesets extends ProcessEventsToDataTable {
    private final static File cacheFolder = new File("C:/cache/");

    public ProcessChangesets(EventsRequest request) {
        super(request);
    }

    @Override
    protected void proccessItem(EventDto event) throws TypeMismatchException {
        if (!(event instanceof IdeCheckinEventDto)) return;
        String action = event.getActionName();
        IdeCheckinEventDto cevent = (IdeCheckinEventDto) event;

        sk.stuba.fiit.perconik.ivda.uaca.dto.ide.RcsServerDto rcsServer = cevent.getRcsServer();
        if (rcsServer == null) { // tzv ide o lokalny subor bez riadenia verzii
            logger.info("rcsServer empty");
            return;
        }

        String changesetIdInRcs = cevent.getChangesetIdInRcs();
        if (changesetIdInRcs.isEmpty() || changesetIdInRcs.compareTo("0") == 0) { // changeset - teda commit id nenajdeny
            logger.info("changesetIdInRcs empty");
            return;
        }

        String name;
        File cacheFile;
        String content;
        Integer id;

        try {
            logger.info("-----------------");
            RcsServerDto server = AstRcsWcfService.getInstance().getRcsServerDto(rcsServer.getUrl());
            logger.info(ToStringBuilder.reflectionToString(server));
            RcsProjectDto project = AstRcsWcfService.getInstance().getRcsProjectDto(server);
            logger.info(ToStringBuilder.reflectionToString(project));
            ChangesetDto changeset = AstRcsWcfService.getInstance().getChangesetDto(changesetIdInRcs, project);
            logger.info(ToStringBuilder.reflectionToString(changeset));
            List<FileVersionDto> fileVersion = AstRcsWcfService.getInstance().getFileVersionsDto(changeset, project);

            for (FileVersionDto file : fileVersion) {
                logger.info(ToStringBuilder.reflectionToString(file));
                id = file.getId();
                name = Files.getNameWithoutExtension(file.getUrl().getValue()) + id;
                cacheFile = new File(cacheFolder, name);
                logger.info("Ulozene do cache:" + cacheFile);
                content = AstRcsWcfService.getInstance().getFileContent(id);
                Files.write(content, cacheFile, Charset.defaultCharset());
            }
            logger.info("-----------------");
        } catch (Exception e) {
            logger.info("proccessItem", e);
        }

        String description = action
                + "<span class=\"more\"><pre>"
                + cevent + "<br/>"
                + "</pre></span>";

        dataTable.add(event.getUser(), event.getTimestamp(), MyDataTable.ClassName.AVAILABLE, "IdeCheckinEventDto", description);
    }
}
