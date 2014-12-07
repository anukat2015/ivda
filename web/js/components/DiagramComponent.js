/**
 * Created by Seky on 3. 12. 2014.
 */

DiagramComponent = function () {
    this.id = -1;
    this.title = "Diagram component";
    this.name = "diagram";
    this.attributes = {
        developer: "Unknown",
        granularity: "Unknown",
        range: undefined
    };
    this.asynTask = undefined;
    this.dom = undefined;
    this.diagram = undefined;
    this.manager = undefined;
    this.groups = ["PER_VALUE"];
};

DiagramComponent.prototype.setRange = function (range) {
};

// Call on move
DiagramComponent.prototype.onMove = function (range) {
    this.manager.onMove(this, range);
    this.attributes.range = range;
    this._updateDescription();
};

DiagramComponent.prototype.getName = function () {
    return this.name;   // vrat nazov s ktorym sa prgramovo pracuje
};

DiagramComponent.prototype.getDiagramElement = function () {
    var elements = this.dom.find(".diagram");
    return elements[0];
};

DiagramComponent.prototype.getTitle = function () {
    return this.title;  // Vrat popis pre pouzivatela
};


DiagramComponent.prototype.init = function (attributes, manager) {
    this.attributes = attributes;
    this.manager = manager;
    this._buildDom();
};

DiagramComponent.prototype._buildDom = function () {
    var div = '<div class="component" id="c-' + this.id + '"></div>';
    this.dom = $(div);
    this.dom.append(this._buildHtml());
    this._updateDescription();
};

DiagramComponent.prototype.destroy = function () {
    // uzivatel znicil data
    this.dom.remove();
    this.dom = undefined;
    this.attributes = undefined;
    this.diagram = undefined;
};

DiagramComponent.prototype.updateData = function () {
    var instance = this;
    gGlobals.service.getData(this.name, this.attributes, function (data) {
        if (instance.attributes == undefined) {
            // Diagram bol zniceny
        } else {
            instance.setData(data);
        }
    });
};

DiagramComponent.prototype.setData = function (data) {

};

DiagramComponent.prototype.draw = function () {
    if (this.diagram == undefined) {
        return;
    }
    this.diagram.redraw();
};

DiagramComponent.prototype._updateDescription = function () {
    var dateFormat = 'd.m.Y H:i';
    var html = "(" + this.attributes.range.start.dateFormat(dateFormat) + " - " + this.attributes.range.end.dateFormat(dateFormat) + ")";
    this.dom.find(".description .time").html(html);
};

DiagramComponent.prototype._buildHeader = function () {
    return '<h1>' + this.getTitle() + '</h1>' +
        '<div class="description">' +
        '   <span class="developer">' + this.attributes.developer + '</span>' +
        '   <span class="time"></span>' +
        '</div>' +
        '<div class="clear"></div> ';
};

DiagramComponent.prototype._buildHtml = function () {
    return this._buildHeader() + '<div class="diagram graph-' + this.getName() + '"></div>';
};

DiagramComponent.prototype.redraw = function () {
    if (this.asynTask) {
        clearTimeout(this.asynTask);
        delete this.asynTask;
    }
    var instance = this;
    this.asynTask = setTimeout(function () {
        // Run asynchronous task
        instance.draw();
    }, 1000);
};
