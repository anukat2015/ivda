/**
 * Created by Seky on 3. 12. 2014.
 */

GoogleChartComponent = function () {
    DiagramComponent.call();
};
GoogleChartComponent.prototype = new DiagramComponent();

GoogleChartComponent.prototype.init = function (attributes, manager) {
    DiagramComponent.prototype.init.call(this, attributes, manager);
    // TODO: Options sa moze nacitat tu vypracovat a ulozit
    this.updateData();
};

GoogleChartComponent.prototype.destroy = function () {
    if (this.diagram != undefined) {
        this.diagram.clearChart();
    }
    DiagramComponent.prototype.destroy.call(this);
};

// ------------- Web Duration
WebDurationComp = function () {
    GoogleChartComponent.call();
    this.title = "Duration of visit";
    this.name = "webDuration";
    this.grouped = true;
};
WebDurationComp.prototype = new GoogleChartComponent();

WebDurationComp.prototype.setData = function (name) {
    // Activity at web pages
    var gdata = new google.visualization.DataTable();
    gdata.addColumn('string', 'Web domain');
    gdata.addColumn('number', 'Duration');
    data.forEach(function (key) {
        gdata.addRow([key.content, key.y]);
    });
    var options = {
        title: 'Duration of visit | Per domain',
        height: 500,
        vAxis: {title: this.attributes.granularity}
    };

    this.diagram = new google.visualization.ColumnChart(this.getDiagramElement());
    this.diagram.draw(gdata, options);
};


// ------------- BrowserVsRewrittenCodeCom
BrowserVsRewrittenCodeCom = function () {
    GoogleChartComponent.call();
    this.title = "Browser activity duration vs Written code";
    this.name = "browserVsRewrittenCode";
    this.grouped = false;
};
BrowserVsRewrittenCodeCom.prototype = new GoogleChartComponent();

BrowserVsRewrittenCodeCom.prototype.setData = function (name) {
    // https://google-developers.appspot.com/chart/interactive/docs/gallery/scatterchart
    var gdata = new google.visualization.DataTable();
    gdata.addColumn('number', 'Browser activity duration');
    gdata.addColumn('number', 'Written code');
    data.forEach(function (key) {
        gdata.addRow([parseInt(key.content), key.y]);
    });

    var options = {
        title: 'Browser activity duration vs Written code',
        hAxis: {title: 'Browser activity duration'},
        vAxis: {title: 'Written code'},
        //legend: 'none',
        height: 500
    };

    this.diagram = new google.visualization.ScatterChart(this.getDiagramElement());
    this.diagram.draw(data, options);
};


// ------------- Files modifications, in counts
FilesModificationCom = function () {
    GoogleChartComponent.call();
    this.title = "Files modifications, in counts";
    this.name = "fileModifications";
    this.grouped = false;
};
FilesModificationCom.prototype = new GoogleChartComponent();

FilesModificationCom.prototype.setData = function (name) {
    var gdata, options;

    // Files modifications
    var gdata = new google.visualization.DataTable();
    gdata.addColumn('string', 'Path');
    gdata.addColumn('number', 'Edits');
    data.forEach(function (key) {
        gdata.addRow([key.content, key.y]);
    });
    var options = {
        title: 'Files modifications, in counts'
    };
    this.diagram = new google.visualization.Histogram(this.getDiagramElement());
    this.diagram.draw(data, options);
};


// ------------- Files modifications, in counts
DomainVisitCom = function () {
    GoogleChartComponent.call();
    this.title = "Domain visit, in counts";
    this.name = "domainVisits";
    this.grouped = false;
};
DomainVisitCom.prototype = new GoogleChartComponent();

DomainVisitCom.prototype.setData = function (name) {
    var gdata, options;

    // Files modifications
    var gdata = new google.visualization.DataTable();
    gdata.addColumn('string', 'Domain');
    gdata.addColumn('number', 'Visit');
    data.forEach(function (key) {
        gdata.addRow([key.content, key.y]);
    });
    var options = {
        title: 'Domain visit, in counts'
    };
    this.diagram = new google.visualization.Histogram(this.getDiagramElement());
    this.diagram.draw(data, options);
};

