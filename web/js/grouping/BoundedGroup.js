/**
 * Created by Seky on 22. 10. 2014.
 */

function BoundedGroup() {
    this.firstEvent;  // potrebne a urcenie zaciatocnej pozicie
    this.lastEvent;  // potrebne pre meranie podla casu
    this.inGroup;

    this.init = function (actual) {  // alias createNewGroup
        inGroup = 0;
        firstEvent = actual;
    };

    this.add2Group = function (event) {
        lastEvent = event;
        inGroup++;
    };

    this.getLastEvent = function () {
        return lastEvent;
    }

    this.getFirstEvent = function () {
        return firstEvent;
    }

    this.isEmpty = function () {
        return this.getFirstEvent() == null;
    }

    this.size = function () {
        return inGroup;
    }

    this.getTimeInterval = function() {
        return diffItemsTime(this.getFirstEvent(), this.lastEvent());
    };
}
