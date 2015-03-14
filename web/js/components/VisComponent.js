/**
 * Created by Seky on 3. 12. 2014.
 */

VisComponent = function () {
    DiagramComponent.call();
    this.groups = ["MONTH", "DAY", "HOUR", "MINUTE"];
};

VisComponent.prototype = new DiagramComponent();

VisComponent.prototype._buildDom = function () {
    DiagramComponent.prototype._buildDom.call(this);
    this.dom.addClass("mytimeline");
};

VisComponent.prototype.setRange = function (range) {
    this.diagram.setOptions(range);
    this.attributes.range = range;
    this._updateDescription();
};

VisComponent.prototype.setData = function (data) {
    this.diagram.setItems(data);
    this.diagram.fit();
    this.diagram.redraw();
};

VisComponent.prototype.destroy = function () {
    this.diagram.destroy();
    DiagramComponent.prototype.destroy.call(this);
};

VisComponent.prototype._prepareDiagram = function () {
    var instance = this;
    this.diagram.on('rangechanged', function (properties) {
        instance.onMove(properties);
    });
    this.setRange(this.attributes.range);
};

VisComponent.prototype.init = function (attributes, manager) {
    DiagramComponent.prototype.init.call(this, attributes, manager);
};
