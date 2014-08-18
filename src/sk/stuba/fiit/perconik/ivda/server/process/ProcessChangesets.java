package sk.stuba.fiit.perconik.ivda.server.process;

import com.gratex.perconik.services.ast.rcs.ChangesetDto;
import com.gratex.perconik.services.ast.rcs.FileVersionDto;
import com.gratex.perconik.services.ast.rcs.RcsProjectDto;
import com.gratex.perconik.services.ast.rcs.RcsServerDto;
import difflib.Chunk;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
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
    private static final String ZAUJIMAVY_SUBOR = "sk.stuba.fiit.perconik.eclipse/src/sk/stuba/fiit/perconik/eclipse/jdt/core/JavaElementEventType.java";

    public ProcessChangesets(EventsRequest request) {
        super(request);
    }

    private static String chunk2String(Chunk chunk) {
        List<?> lines = chunk.getLines();
        StringBuilder builder = new StringBuilder(2048);
        builder.append("position:\t").append(chunk.getPosition()).append("\tlines:\t").append(lines.size()).append('\n');
        for (Object o : lines) {
            builder.append(o).append('\n');
        }
        return builder.toString();
    }

    /**
     * Prechadzaj vsetky commity.
     * Filtruj len subory 'sk.stuba.fiit.perconik.eclipse/src/sk/stuba/fiit/perconik/eclipse/jdt/core/JavaElementEventType.java' zatial.
     * Ukaz zmenu tohto suboru od jeho poslednej verzie.
     * Ked v aktualonom commite nedoslo k zmene tochto suboru. Commit vypis z patricnou poznamkou.
     *
     * @param event
     */
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
            RcsServerDto server = AstRcsWcfService.getInstance().getNearestRcsServerDto(rcsServer.getUrl());
            RcsProjectDto project = AstRcsWcfService.getInstance().getRcsProjectDto(server);
            ChangesetDto changeset = AstRcsWcfService.getInstance().getChangesetDto(changesetIdInRcs, project);
            List<FileVersionDto> fileVersion = AstRcsWcfService.getInstance().getChangedFiles(changeset);

            for (FileVersionDto file : fileVersion) {
                if (file.getUrl().getValue().equals(ZAUJIMAVY_SUBOR)) {
                    LOGGER.info(FileVersionsUtil.getName(file));
                    LOGGER.info(changesetIdInRcs);
                    najdenySubor(file);

                    String description = action
                            + "<span class=\"more\"><pre>"
                            + cevent + "<br/>"
                            + "</pre></span>";

                    dataTable.add(event.getUser(), event.getTimestamp(), MyDataTable.ClassName.AVAILABLE, "IdeCheckinEventDto", description);
                    return; // ignoruj ostatne
                }
            }

        } catch (AstRcsWcfService.NotFoundException e) {
            LOGGER.info("Chybaju nejake udaje:" + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("proccessItem", e);
        }
    }

    /**
     * Dany subor sme nasli, zachyt ID, vypis hodnotu
     *
     * @param file
     */
    private void najdenySubor(FileVersionDto file) {  // TODO: implementovat do REST sluzby
        try {
            List<String> aktualneVerzia = FileVersionsUtil.getContent(file);
            List<String> staraVerzia = FileVersionsUtil.getContentAncestor(file);

            Patch patch = DiffUtils.diff(staraVerzia, aktualneVerzia);
            Integer additions = 0;
            Integer deletions = 0;
            for (Delta delta : patch.getDeltas()) {
                LOGGER.info("Diff\t" + delta.getType());
                switch (delta.getType()) {
                    case INSERT: {
                        additions += delta.getRevised().getLines().size();
                        LOGGER.info(chunk2String(delta.getRevised()));
                        break;
                    }
                    case DELETE: {
                        deletions += delta.getOriginal().getLines().size();
                        LOGGER.info(chunk2String(delta.getOriginal()));
                        break;
                    }
                    case CHANGE: {
                        int changed = delta.getOriginal().getLines().size() + delta.getRevised().getLines().size();
                        additions += changed;
                        deletions += changed;
                        LOGGER.info(chunk2String(delta.getOriginal()));
                        LOGGER.info(chunk2String(delta.getRevised()));
                        break;
                    }
                }
            }
            LOGGER.info("additions\t" + additions + "\tdeletions\t" + deletions);

        } catch (AstRcsWcfService.NotFoundException e) {
            LOGGER.warn("Nemozem stiahnut subor, lebo:" + e.getMessage());
        }
    }
}
