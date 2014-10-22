/**
 * Created by Seky on 22. 10. 2014.
 */ 7

function ProcessAsGroup() {
    this.dividing = new DivideByTimeAndType();
    this.group = null;
    this.groups = new Array();

    this.processItem = function(item) {
        if(this.dividing.canIgnore(item)) {
            return;
        }

        if(this.group == null) {
            this.group = this.createGroup();
            this.group.add2Group(item);
            return;
        }

        if(this.dividing.canDivide(this.group, item)) {
            this.foundEndOfGroup(this.group);
            this.group = this.createGroup();
        }
        this.group.add2Group(item);
    };

    this.createGroup = function() {
        return new BoundedGroup();
    };

    this.foundEndOfGroup = function(group) {
        var first = group.getFirstEvent();
        var last = group.gettLastEvent();
        this.groups.push(group);
    }
}
