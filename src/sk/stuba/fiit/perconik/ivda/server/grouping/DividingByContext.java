package sk.stuba.fiit.perconik.ivda.server.grouping;

import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.ide.IdeCodeEventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.ide.IdeDocumentDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.web.WebEventDto;
import sk.stuba.fiit.perconik.ivda.server.grouping.group.Group;

/**
 * Created by Seky on 6. 3. 2015.
 * Chceme delenie klasicke podla prostredia ale aj podla kontextu.
 * V zmysle kontextu sa berie pre Ide praca so suborom. Pre Web sa berie ejdna navstivena stranka.
 */
public final class DividingByContext extends DividingByEnviroment {
    @Override
    public boolean canDivide(Group group, EventDto actual) {
        return dividePerWebTab(group, actual) || dividePerIdeFile(group, actual) || divideByEnviroment(group, actual);
    }

    /**
     * Delenie podla jednotlivych tabov.
     * @param group
     * @param actual
     * @return
     */
    protected static boolean dividePerWebTab(Group group, EventDto actual) {
        EventDto first = group.getFirstEvent();
        if (!(first instanceof WebEventDto)) {
            return false; // nejde o Web, ignorujeme, rozdeli to nieco dalsie
        }
        if (!(actual instanceof WebEventDto)) {
            return true; // druhy prvok je iny typ
        }
        String url1 = ((WebEventDto) first).getUrlDomain();
        String url2 = ((WebEventDto) actual).getUrlDomain();
        if (url1 == null || url2 == null) {
            return true;
        }
        // url1 ponechaj take ake je, ked je ine ako url2 rozdeli to samo, co je pravdepodobne, inak mohol byt aj na rovnakej neznamen stranke
        return !url1.equals(url2); // tzv prida event na zaciatok a na konci bude druhy event
    }

    /**
     * Delenie podla suborov
     * @param group
     * @param actual
     * @return
     */
    protected static boolean dividePerIdeFile(Group group, EventDto actual) {
        EventDto first = group.getFirstEvent();
        if (!(first instanceof IdeCodeEventDto)) {
            return false; // nejde o Web, ignorujeme, rozdeli to nieco dalsie
        }
        if (!(actual instanceof IdeCodeEventDto)) {
            return true; // druhy prvok je iny typ
        }
        IdeDocumentDto a = ((IdeCodeEventDto) first).getDocument();
        IdeDocumentDto b = ((IdeCodeEventDto) actual).getDocument();
        if (a == null || b == null) {
            return true;
        }
        String url1 = a.getLocalPath();
        String url2 = b.getLocalPath();
        if (url1 == null || url2 == null) {
            return true;
        }
        // url1 ponechaj take ake je, ked je ine ako url2 rozdeli to samo, co je pravdepodobne, inak mohol byt aj na rovnakej neznamen stranke
        return !url1.equals(url2); // tzv prida event na zaciatok a na konci bude druhy event
    }
}
