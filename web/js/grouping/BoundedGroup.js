/**
 * Created by Seky on 22. 10. 2014.
 */

function BoundedGroup() {
    this.firstEvent;  // potrebne a urcenie zaciatocnej pozicie
    this.lastEvent;  // potrebne pre meranie podla casu
    this.inGroup;

    this.init = function (actual) {  // alias createNewGroup
        this.inGroup = 0;
        this.firstEvent = actual;
    };

    this.add2Group = function (event) {
        this.lastEvent = event;
        this.inGroup++;
    };

    this.getLastEvent = function () {
        return this.lastEvent;
    }

    this.getFirstEvent = function () {
        return this.firstEvent;
    }

    this.isEmpty = function () {
        return this.getFirstEvent() == null;
    }

    this.size = function () {
        return this.inGroup;
    }

    this.getTimeInterval = function() {
        return diffItemsTime(this.getFirstEvent(), this.getLastEvent());
    };
}
