package sk.stuba.fiit.perconik.ivda.server.processevents;

import sk.stuba.fiit.perconik.ivda.server.grouping.group.Group;
import sk.stuba.fiit.perconik.ivda.util.lang.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Seky on 5. 12. 2014.
 */
public abstract class BrowserVsWrittenCode extends CreateBaseActivities {
    // Threshold urcuje budovanie skupin aktivit
    // Ma byt urcite vacsi ako THRESHOLD pre budovanie aktivit
    private static long TIME_THRESHOLD = TimeUnit.MINUTES.toMillis(10);

    protected List<WebGroup> webActivities = new ArrayList<>(24);
    protected List<IdeGroup> ideActivities = new ArrayList<>(24);

    // pre kazdy web pocitame dlzku aktivity
    // ukladaj weby do kedy nepride ide
    // vyber weby za posledny threshold
    // potom tomuto IDE co prislo spocitaj riadky, ak prislo dalsie ide spocitaj riadky
    // ak prisiel web tak predchadzajucemu IDE prirad Web

    // tzv (WEBWEBWEBWEBWEBWEB)(IDEIDEIDEIDE)
    // Kazda dalsia aktivita sa musi spustit d thresholdu.
    // Ked sa prve Ide enspsuti do thresholdu. Skupina sa rusi.
    // Ked sa druhe a dalsie Ide nespsuti do thresholdu, konci sa so skupinou.
    // Ked dalsia Web udalost sa nespusti do thresholdu. Stara aktivita sa zrusi a buduje sa nova.
    @Override
    protected void foundEndOfGroup(Group group) {
        Date start, end;
        if (group instanceof WebGroup) {
            // Nemame ziadnu Ide aktivitu ulozenu
            if (ideActivities.isEmpty()) {
                if (webActivities.isEmpty()) {
                    // Nemame nic ulozene, tak to len pridaj
                    webActivities.add(((WebGroup) group));
                } else {
                    // Zacala sa dalsia Web aktivita do thresholdu?
                    start = group.getFirstEvent().getTimestamp();
                    end = endOfLastWebActivity();
                    if (DateUtils.diff(end, start) < TIME_THRESHOLD) {
                        // ano, zacala
                        webActivities.add(((WebGroup) group));
                    } else {
                        // nezacala, nastav tuto skupinu ako poslednu
                        webActivities.clear();
                        webActivities.add(((WebGroup) group));
                    }
                }
            } else {
                // Nejake Ide udalosti sme uz mali ulozene a prisiel Web
                _foundActivitiesRelation();
                webActivities.add(((WebGroup) group));  // pridaj do novej skupiny
            }
        } else if (group instanceof IdeGroup) {
            // Prislo Ide
            if (webActivities.isEmpty()) {
                // Nemame ide k comu priradit, preskoc Ide
            } else {
                // Zacala sa dalsia Ide aktivita do thresholdu?
                start = group.getFirstEvent().getTimestamp();
                if (ideActivities.isEmpty()) {
                    // ked nemame Ide tak to meriame z posledneho Webu
                    end = endOfLastWebActivity();
                    if (DateUtils.diff(end, start) < TIME_THRESHOLD) {
                        // ano zacala
                        ideActivities.add(((IdeGroup) group));
                    }
                } else {
                    // inak to meriame od posledneho Ide
                    end = endOfLastIdeActivity();
                    if (DateUtils.diff(end, start) < TIME_THRESHOLD) {
                        // ano zacala
                        ideActivities.add(((IdeGroup) group));
                    } else {
                        // nezacala, odrez toto ide
                        _foundActivitiesRelation();
                    }
                }
            }
        }
    }

    protected Date endOfLastWebActivity() {
        return webActivities.get(webActivities.size() - 1).getLastEvent().getTimestamp();
    }

    protected Date endOfLastIdeActivity() {
        return ideActivities.get(ideActivities.size() - 1).getLastEvent().getTimestamp();
    }

    protected long getTimeDuration() {
        // Pre web mame viacere aktivity ulozene v skupine, medzi skupinami vyvojar mohol byt neaktivny alebo mohol pracovat na niecom inom
        // napriklad mohol byt offline, mohol sa ist poradit, mohol pri v explorery a neico hladal
        // Preto scitame celkovy cas ako sucet aktivit a nie zaciatok a koniec
        long time = 0;
        for (WebGroup g : webActivities) {
            time += g.getTimeDiff();
        }
        return time;
    }

    protected int getWrittenCode() {
        // Pozerame sa na zachytene aktivity a na to kolko riadkov bolo prepisanych pocas ich trvania
        int loc = 0;
        for (IdeGroup g : ideActivities) {
            loc += g.getLoc();
        }
        return loc;
    }

    private void _foundActivitiesRelation() {
        foundActivitiesRelation();

        // Vycisti ulozene aktivity
        webActivities.clear();
        ideActivities.clear();
    }

    protected abstract void foundActivitiesRelation();
}
