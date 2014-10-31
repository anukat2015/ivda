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
        return !(group.getLastEvent().content === actual.content);
    };

    this.divideForWebTabSpendTime = function (group, actual) {
        var first, url1, url2;
        first = group.getFirstEvent();
        url1 = first.metadata.link;
        if (url1 == undefined) {
            return false; // nejde o Web, ignorujeme, rozdeli to nieco dalsie
        }
        url1 = new URL(url1).hostname;
        url2 = actual.metadata.link;
        if (url2 == undefined) {
            return true; // druhy prvok je iny typ
        }
        url2 = new URL(url2).hostname;
        return group.inGroup > 1 && url != url2; // tzv prida event na zaciatok a na konci bude druhy event
    };

    this.canIgnore = function (event) {
        return false;
    };
}
