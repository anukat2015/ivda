/**
 * Created by Seky on 14. 3. 2015.
 */

TimelineComponent = function () {
    VisComponent.call();
};

TimelineComponent.prototype = new VisComponent();

TimelineComponent.prototype._createTimeline = function () {
    var options = {
        width: "100%",
        height: "700px",
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

TimelineComponent.prototype._registerNavigationBar = function () {
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

// ----- Activity detail
ActivityDetailCom = function () {
    TimelineComponent.call();
    this.title = "Detail of activities per enviroment";
    this.name = "activityDetail";
    this.groups = ["PER_VALUE"];
};
ActivityDetailCom.prototype = new TimelineComponent();

ActivityDetailCom.prototype.init = function (attributes, manager) {
    VisComponent.prototype.init.call(this, attributes, manager);
    this._createTimeline();
    var info = new GraphData();
    info.createGroup2('Web', 'Activity in Web browser');
    info.createGroup2('Ide', 'Activity in IDE enviroment');
    this.diagram.setGroups(info.groups);
    this.updateData();
};


// ----- Processes detail
ProcessesDetailCom = function () {
    TimelineComponent.call();
    this.title = "Detail of runned OS processes";
    this.name = "processDetail";
    this.groups = ["PER_VALUE"];
};
ProcessesDetailCom.prototype = new TimelineComponent();

ProcessesDetailCom.prototype.init = function (attributes, manager) {
    VisComponent.prototype.init.call(this, attributes, manager);
    this._createTimeline();
    var info = new GraphData();
    info.createGroup2('processes', 'List of process');
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
