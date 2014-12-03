/**
 * Created by Seky on 3. 12. 2014.
 */

DiagramComponent = function () {
    this.id = -1;
    this.title = "Diagram component";
    this.name =  "diagram";
    this.attributes = {
        developer: "Unknown",
        granularity: "Unknown",
        range: undefined
    };
    this.asynTask = undefined;
    this.dom = undefined;
    this.diagram = undefined;
};

DiagramComponent.prototype.setRange = function (range) { };

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


DiagramComponent.prototype.init = function(attributes, manager) {
    this.attributes = attributes;
    this._buildDom(manager);
};

DiagramComponent.prototype._buildDom = function(manager) {
    var instance;
    var div = '<div id="c-' + this.id + '"></div>';
    this.dom = $(div);
    this.dom.append(this.buildHtml());
    this.dom.draggable({
        drag: function() {
            manager.dragStart(instance);
        },
        stop: function() {
            manager.dragStop(instance);
        }
    });
};

DiagramComponent.prototype.destroy = function () {
    // uzivatel znicil data
    this.dom = undefined;
    this.attributes = undefined;
};

DiagramComponent.prototype.updateData = function () {
    var instance;
    gGlobals.service.getData(this.name, this.attributes, function (data) {
        instance.setData(data);
    });
};

DiagramComponent.prototype.setData = function (data) {

};

DiagramComponent.prototype.draw = function() {
    if(this.diagram == undefined) {
        return;
    }
    this.diagram.redraw();
};

DiagramComponent.prototype._buildHtml = function () {
    return '<h1>' + this.getTitle() + '</h1> \
            <div class="diagram graph-' + this.getName() + '"></div>';
};

GraphPanel.prototype.redraw = function () {
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
