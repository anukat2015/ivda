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

VisComponent.prototype._createTimeline = function () {
    var options = {
        width: "100%",
        height: "300px",
        stack: true,
        margin: {
            item: 10, // minimal margin between items
            axis: 5   // minimal margin between items and the axis
        },
        orientation: 'top'
    };

    this.diagram = new vis.Timeline(this.getDiagramElement(), new vis.DataSet(), options);
    this._prepareDiagram();
};

VisComponent.prototype._registerNavigationBar = function () {
    this.dom.find(".vis.timeline .center > .content").append('   \
        <div class="timeline-navigation ui-widget ui-state-highlight ui-corner-all"  \
    style="position: absolute;right: 7px;top: 53%;">                   \
        <div class="timeline-navigation-zoom-in" title="Zoom in"><span class="ui-icon ui-icon-circle-zoomin"></span>  \
        </div>                                                                                                 \
        <div class="timeline-navigation-zoom-out" title="Zoom out"><span class="ui-icon ui-icon-circle-zoomout"></span>  \
        </div>    \
        <div class="timeline-navigation-move-left" title="Move left"><span  \
        class="ui-icon ui-icon-circle-arrow-w"></span></div>   \
        <div class="timeline-navigation-move-right" title="Move right"><span \
        class="ui-icon ui-icon-circle-arrow-e"></span></div>    \
    </div>');

};

VisComponent.prototype._createDynamicHistogram = function (items, groups, overlap) {
    var options = {
        width: "100%",
        height: "300px",
        legend: {
            right: {position: 'top-right'},
            left: {position: 'top-left'}
        },
        style: 'bar',
        barChart: {
            handleOverlap: overlap,
            width: 1,
            align: 'center'
        }, // align: left, center, right
        drawPoints: true,
        dataAxis: {
            icons: false,
            popisY: "Pocet"
        },
        orientation: 'top'
    };

    this.diagram = new vis.Graph2d(this.getDiagramElement(), items, options, groups);
    this._prepareDiagram();
};

VisComponent.prototype.init = function (attributes, manager) {
    DiagramComponent.prototype.init.call(this, attributes, manager);
};


// ----- Pocet eventov
CountEventsCom = function () {
    VisComponent.call();
    this.title = "Pocet eventov histogram";
    this.name = "countEvents";
};
CountEventsCom.prototype = new VisComponent();

CountEventsCom.prototype.init = function (attributes, manager) {
    VisComponent.prototype.init.call(this, attributes, manager);
    this._createDynamicHistogram(new vis.DataSet(), new vis.DataSet(), "overlap");
    var info = new GraphData();
    info.createGroup2('events', 'Count of actions');
    this.diagram.setGroups(info.groups);
    this.updateData();
};

// ----- Pocet eventov divided
CountEventsDividedCom = function () {
    VisComponent.call();
    this.title = "Pocet eventov rozdelenych do skupin";
    this.name = "countEventsDivided";
};
CountEventsDividedCom.prototype = new VisComponent();

CountEventsDividedCom.prototype.init = function (attributes, manager) {
    VisComponent.prototype.init.call(this, attributes, manager);
    this._createDynamicHistogram(new vis.DataSet(), new vis.DataSet(), "stack");
    var info = new GraphData();
    info.createGroup2('Web', 'Pocet akcii v prehliadaci');
    info.createGroup2('Ide', 'Pocet akcii v ide prostredi');
    info.createGroup2('Mix', 'Pocet inych akcii');
    this.diagram.setGroups(info.groups);
    this.updateData();
};


// ----- Changes of source codes
CodeChangesCom = function () {
    VisComponent.call();
    this.title = "Zmeny v zdrojovom kode";
    this.name = "locChanges";
    this.groups = ["MONTH", "DAY", "HOUR", "MINUTE", "PER_VALUE"];
};
CodeChangesCom.prototype = new VisComponent();

CodeChangesCom.prototype.init = function (attributes, manager) {
    VisComponent.prototype.init.call(this, attributes, manager);
    this._createDynamicHistogram(new vis.DataSet(), new vis.DataSet(), "overlap");
    var info = new GraphData();
    info.createGroup2("changedLines", "Zmeny v zdrojovom kode podla LOC metriky");
    this.diagram.setGroups(info.groups);
    this.updateData();
};


// ----- Activity time grouped
ActivityTimeGroupedCom = function () {
    VisComponent.call();
    this.title = "Zoskupene trvanie pre aktivity";
    this.name = "activityTimeGrouped";
    this.groups = ["MONTH", "DAY", "HOUR"];
};
ActivityTimeGroupedCom.prototype = new VisComponent();

ActivityTimeGroupedCom.prototype.init = function (attributes, manager) {
    VisComponent.prototype.init.call(this, attributes, manager);
    this._createDynamicHistogram(new vis.DataSet(), new vis.DataSet(), "stack");
    var info = new GraphData();
    info.createGroup2('Web', 'Dlzka aktivity vo webovom prehliadaci v minutach');
    info.createGroup2('Ide', 'Dlzka aktivity v ide prostredi v minutach');
    this.diagram.setGroups(info.groups);
    this.updateData();
};

// ----- Activity time grouped
ActivityLocDomainVisitsGrouped = function () {
    VisComponent.call();
    this.title = "Vybrane a zoskupene crty aktivit";
    this.name = "activityLocDomainVisitsGrouped";
    this.groups = ["MONTH", "DAY", "HOUR"];
};
ActivityLocDomainVisitsGrouped.prototype = new VisComponent();

ActivityLocDomainVisitsGrouped.prototype.init = function (attributes, manager) {
    VisComponent.prototype.init.call(this, attributes, manager);
    this._createDynamicHistogram(new vis.DataSet(), new vis.DataSet(), "stack");
    var info = new GraphData();
    info.createGroup2('Web', 'Pocet navstivenych domen pocas aktivit v prehliadaci');
    info.createGroup2('Ide', 'Zmeny v zdrojovom kode podla LOC metriky pocas aktivit v ide');
    this.diagram.setGroups(info.groups);
    this.updateData();
};


// ----- Activity Unique domains vs Changed LOC
ActivityDynamicCom = function () {
    VisComponent.call();
    this.title = "Vybrane crty aktivit";
    this.name = "activityHistogram";
    this.groups = ["PER_VALUE"];
};
ActivityDynamicCom.prototype = new VisComponent();

ActivityDynamicCom.prototype.init = function (attributes, manager) {
    VisComponent.prototype.init.call(this, attributes, manager);
    this._createDynamicHistogram(new vis.DataSet(), new vis.DataSet(), "overlap");
    var info = new GraphData();
    info.createGroup2('Web', 'Pocet navstivenych domen pocas aktivity v prehliadaci, jeden stlpec je aktivita');
    info.createGroup2('Ide', 'Zmeny v zdrojovom kode podla LOC metriky pocas aktivity v ide, jeden stlpec je aktivita');
    this.diagram.setGroups(info.groups);
    this.updateData();
};

// ----- Activity detail
ActivityDetailCom = function () {
    VisComponent.call();
    this.title = "Detailu aktivit";
    this.name = "activityDetail";
    this.groups = ["PER_VALUE"];
};
ActivityDetailCom.prototype = new VisComponent();

ActivityDetailCom.prototype.init = function (attributes, manager) {
    VisComponent.prototype.init.call(this, attributes, manager);
    this._createTimeline();
    var info = new GraphData();
    info.createGroup2('Web', 'Aktivita v prehliadaci');
    info.createGroup2('Ide', 'Aktivita v ide prostredi');
    this.diagram.setGroups(info.groups);
    this.updateData();
};


// ----- Processes detail
ProcessesDetailCom = function () {
    VisComponent.call();
    this.title = "Detail spustenych procesov";
    this.name = "processDetail";
    this.groups = ["PER_VALUE"];
};
ProcessesDetailCom.prototype = new VisComponent();

ProcessesDetailCom.prototype.init = function (attributes, manager) {
    VisComponent.prototype.init.call(this, attributes, manager);
    this._createTimeline();
    var info = new GraphData();
    info.createGroup2('processes', 'Zoznam procesov');
    this.diagram.setGroups(info.groups);
    this.updateData();
};

ProcessesDetailCom.prototype.updateData = function () {
    var instance = this;
    gGlobals.service.getProcesses(this.attributes, function (data) {
        if (instance.attributes == undefined) {
            // Diagram bol zniceny
        } else {
            var x = instance.fixData(data);
            instance.setData(x);
        }
    });
};

ProcessesDetailCom.prototype.fixData = function (processes) {
    var items = new vis.DataSet();
    var service = gGlobals.service;
    for (var i = 0; i < processes.length; i++) {
        var process = processes[i];
        items.add({
            start: service._convertDate(process.start),
            end: service._convertDate(process.end),
            content: process.name,
            group: "processes"
        });
    }
    return items;
};
