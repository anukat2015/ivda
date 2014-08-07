package sk.stuba.fiit.perconik.ivda.server.process;

import com.google.common.io.Files;
import com.google.visualization.datasource.base.TypeMismatchException;
import com.gratex.perconik.services.ast.rcs.ChangesetDto;
import com.gratex.perconik.services.ast.rcs.FileVersionDto;
import com.gratex.perconik.services.ast.rcs.RcsProjectDto;
import com.gratex.perconik.services.ast.rcs.RcsServerDto;
import com.ibm.icu.util.GregorianCalendar;
import com.ibm.icu.util.TimeZone;
import sk.stuba.fiit.perconik.ivda.astrcs.AstRcsWcfService;
import sk.stuba.fiit.perconik.ivda.server.MyDataTable;
import sk.stuba.fiit.perconik.ivda.uaca.client.EventsRequest;
import sk.stuba.fiit.perconik.ivda.uaca.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.uaca.dto.ide.IdeCodeEventRequest;
import sk.stuba.fiit.perconik.ivda.uaca.dto.ide.IdeDocumentDto;

import java.io.File;
import java.nio.charset.Charset;

/**
 * Created by Seky on 7. 8. 2014.
 */
public class ProcessFileVersions extends ProcessEventsToDataTable {
    private final static File cacheFolder = new File("C:/cache/");

    public ProcessFileVersions(EventsRequest request) {
        super(request);
    }

    protected void proccessItem(EventDto event) throws TypeMismatchException {
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

        sk.stuba.fiit.perconik.ivda.uaca.dto.ide.RcsServerDto rcsServer = dokument.getRcsServer();
        if (rcsServer == null) { // tzv ide o lokalny subor bez riadenia verzii
            logger.info("Lokalny subor");
            return;
        }
        String fragment = rcsServer.getTypeUri().getFragment();

        String changesetIdInRcs = dokument.getChangesetIdInRcs();
        if (changesetIdInRcs.isEmpty() || changesetIdInRcs.compareTo("0") == 0) { // changeset - teda commit id nenajdeny
            return;
        }

        try {
            logger.info(cevent.getText());
            RcsServerDto server = AstRcsWcfService.getRcsServerDto(dokument.getRcsServer().getUrl());
            RcsProjectDto project = AstRcsWcfService.getRcsProjectDto(server);
            ChangesetDto changeset = AstRcsWcfService.getChangesetDto(dokument.getChangesetIdInRcs(), project);
            FileVersionDto fileVersion = AstRcsWcfService.getFileVersionDto(changeset, dokument.getServerPath(), project);

            String name;
            File cacheFile;
            String content;
            Integer id;

            id = fileVersion.getId();
            name = Files.getNameWithoutExtension(dokument.getLocalPath()) + id;
            cacheFile = new File(cacheFolder, name);
            logger.info(cacheFile);
            content = AstRcsWcfService.getFileContent(id);
            Files.write(content, cacheFile, Charset.defaultCharset());
            // List<ChangesetDto> vysledok = AstRcsWcfService.getChangeset(fileVersion.getEntityId());
        } catch (Exception e) {
            logger.info("proccessItem", e);
        }


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
}
