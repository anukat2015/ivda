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
        return this.divideByTime(group, actual) || this.divideByType(group, actual);
    };

    this.divideByTime = function (group, actual) {
        return diffItemsTime(group.getLastEvent(), actual) > this.ONLINE_MIN_INTERVAL;
    };

    this.divideByType = function (group, actual) {
        return !(group.getLastEvent().content === actual.content);
    };

    this.canIgnore = function (event) {
        return false;
    };
}
