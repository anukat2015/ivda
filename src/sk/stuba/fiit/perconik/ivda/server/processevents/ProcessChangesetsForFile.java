package sk.stuba.fiit.perconik.ivda.server.processevents;

import com.google.common.collect.ImmutableMap;
import com.gratex.perconik.services.ast.rcs.ChangesetDto;
import com.gratex.perconik.services.ast.rcs.FileVersionDto;
import com.gratex.perconik.services.ast.rcs.RcsProjectDto;
import com.gratex.perconik.services.ast.rcs.RcsServerDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.ide.IdeCheckinEventDto;
import sk.stuba.fiit.perconik.ivda.astrcs.AstRcsWcfService;
import sk.stuba.fiit.perconik.ivda.server.EventsUtil;
import sk.stuba.fiit.perconik.ivda.server.servlets.TimelineEvent;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.List;

/**
 * Created by Seky on 15. 8. 2014.
 * <p/>
 * Vypis file verziu specifickeho suboru.
 */
@NotThreadSafe
public final class ProcessChangesetsForFile extends ProcessEvents2TimelineEvents {
    private static final String ZAUJIMAVY_SUBOR = "sk.stuba.fiit.perconik.eclipse/src/sk/stuba/fiit/perconik/eclipse/jdt/core/JavaElementEventType.java";

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
        IdeCheckinEventDto cevent = (IdeCheckinEventDto) event;

        sk.stuba.fiit.perconik.ivda.activity.dto.ide.RcsServerDto rcsServer = cevent.getRcsServer();
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
            RcsProjectDto project = AstRcsWcfService.getInstance().getRcsProjectDto(server, cevent.getSolutionName());
            ChangesetDto changeset = AstRcsWcfService.getInstance().getChangesetDto(changesetIdInRcs, project);
            List<FileVersionDto> fileVersion = AstRcsWcfService.getInstance().getChangedFiles(changeset);

            for (FileVersionDto file : fileVersion) {
                if (file.getUrl().getValue().equals(ZAUJIMAVY_SUBOR)) {
                    LOGGER.info(changesetIdInRcs);
                    TimelineEvent e = new TimelineEvent(
                            event.getTimestamp(),
                            event.getUser(),
                            EventsUtil.event2Classname(event),
                            "IdeCheckinEventDto",
                            null,
                            ImmutableMap.of(
                                    "uid", event.getEventId()
                            )
                    );
                    add(e);
                    return; // ignoruj ostatne
                }
            }

        } catch (AstRcsWcfService.NotFoundException e) {
            LOGGER.info("Chybaju nejake udaje:" + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("proccessItem", e);
        }
    }
}
