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
            this.group = this.createGroup(item);
            this.group.add2Group(item);
            return;
        }

        if(this.dividing.canDivide(this.group, item)) {
            this.foundEndOfGroup(this.group);
            this.group = this.createGroup(item);
        }
        this.group.add2Group(item);
    };

    this.createGroup = function(item) {
        var group =  new BoundedGroup();
        group.init(item);
        return group;
    };

    this.foundEndOfGroup = function(group) {
        this.groups.push(group);
    }

    this.finish = function() {
        if (this.group == null || this.group.isEmpty()) {
            return; // ked ziadny prvok nebol v odpovedi
        }

        this.foundEndOfGroup(this.group);
    };
}
