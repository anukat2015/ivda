package sk.stuba.fiit.perconik.ivda.server.grouping;

import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.server.grouping.group.Group;

/**
 * Created by Seky on 21. 8. 2014.
 * <p/>
 * Interaface urcuje sposob spravania ako sa skupiny maju rozdelovat.
 */
public interface IDividing {
    // Kontrola ci sa prvok moze vlozit do skupiny, ak nie posledny prvok sa do skupiny prida a skupina konci
    boolean canDivide(Group group, EventDto event);

    // Ktore udalosti ignorujeme uplne?
    boolean canIgnore(EventDto event);

    // Kontrola ci sa prvok moze vlozit do skupiny, ak nie posledny prvok sa do skupiny neprida a skupina konci
    boolean canPush(Group g, EventDto event);
}
