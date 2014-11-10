/**
 * Created by Seky on 22. 10. 2014.
 */

function DivideByTimeAndType() {
    /**
     * tzv. raz za 6 min sa musi poslat udaj o procesoch, vtedy sa da chapat, ze je na pocitaci ale eventy sa nezachytavaju
     * napriklad pzoera film
     */
    this.ONLINE_MIN_INTERVAL = 1000 * 60 * 6;

    this.canDivide = function (group, actual) {
        return this.divideByTime(group, actual) || this.divideForWebTabSpendTime(group, actual) || this.divideByType(group, actual);
    };

    this.divideByTime = function (group, actual) {
        return diffItemsTime(group.getLastEvent(), actual) > this.ONLINE_MIN_INTERVAL;
    };

    this.divideByType = function (group, actual) {
        return !(group.getLastEvent().group.content === actual.group.content);
    };

    this.divideForWebTabSpendTime = function (group, actual) {
        var first, url1, url2;
        first = group.getFirstEvent();
        if (first.group.content != "Web") {
            return false; // nejde o Web, ignorujeme, rozdeli to nieco dalsie
        }
        if (actual.group.content != "Web") {
            return true; // druhy prvok je iny typ
        }
        url1 = new URL(first.content).hostname;
        url2 = new URL(actual.content).hostname;
        return group.inGroup > 1 && url1 != url2; // tzv prida event na zaciatok a na konci bude druhy event
    };

    this.canIgnore = function (event) {
        return false;
    };
}
