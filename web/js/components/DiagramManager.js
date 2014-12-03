/**
 * Created by Seky on 3. 12. 2014.
 */

DiagramManager = function () {
    this.lastId = 1;
    this.dom = $("#graphs");
    this.components = {};
    this.visibleComponents = {};
    this.registerComponents();
    this.dom.sortable();
};

DiagramManager.prototype.redraw = function () {
    var com;
    var instance = this;
    Object.keys(this.visibleComponents).forEach(function (key) {
        com = instance.visibleComponents[key];
        com.redraw();
    });
};

DiagramManager.prototype.getGraphs = function () {
    var graphs, com;
    var instance = this;
    Object.keys(this.components).forEach(function (key) {
        com = instance.components[key];
        graphs.push({id: key, name: com.getTitle()});
    });
    return graphs;
};

DiagramManager.prototype.create = function (featureName, attributes) {
    // Pridaj do zoznamu viditelnych
    if (!this.components.hasOwnProperty(featureName)) {
        throw new Error("component nenajdeny");
    }
    var com = jQuery.extend(true, {}, this.components[id]);
    com.id = this.lastID;
    this.visibleComponents[com.id] = com;
    this.lastID++;

    // Zobraz to na webe
    com.init(attributes, this);
    this.dom.append(com.dom);
};

DiagramManager.prototype.destroy = function (id) {
    if (!this.visibleComponents.hasOwnProperty(id)) {
        throw new Error("component nenajdeny");
    }
    var com = this.visibleComponents[id];
    com.destroy();
    com.dom.remove();
    delete this.visibleComponents[id];
};

DiagramManager.prototype.register = function (component) {
    this.components[component.getName()] = (component);
};

DiagramManager.prototype.dragStart = function (item) {
    gGlobals.toolbar.showTrash();
};

DiagramManager.prototype.dragStop = function (item) {
    gGlobals.toolbar.hideTrash();
};

DiagramManager.prototype.registerComponents = function () {
    this.register(new CountEventsCom());
    this.register(new CountEventsDividedCom());
    this.register(new CodeChangesCom());
    this.register(new ActivityDynamicCom());
    this.register(new ActivityDetailCom());
    this.register(new FilesModificationCom());
    this.register(new DomainVisitCom());
    this.register(new WebDurationComp());
    this.register(new BrowserVsRewrittenCodeCom());
};
