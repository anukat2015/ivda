/**
 * Created by Seky on 1. 11. 2014.
 */

links.Timeline.Item.prototype.computeSizeByChangedLines = function () {
    var radius = 12;
    if (this.group.content == "Ide") {
        if (this.metadata.y != undefined) {
            radius = Math.max(radius, this.metadata.y);
        }
    }
    return radius;
};

links.Timeline.Item.prototype.computeSizeByChangesInFuture = function () {
    var radius = 12;
    if (this.group.content == "Ide") {
        if (this.metadata.changedInFuture != undefined) {
            radius = Math.max(radius, this.metadata.changedInFuture);
        }
    }
    return radius;
};

links.Timeline.Item.prototype.computeSizeConstant = function () {
    var radius = 12;
    return radius;
};

links.Timeline.Item.prototype.computeSize = links.Timeline.Item.prototype.computeSizeConstant;
