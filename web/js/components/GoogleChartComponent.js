/**
 * Created by Seky on 3. 12. 2014.
 */

GoogleChartComponent = function () {
    DiagramComponent.call();
};
GoogleChartComponent.prototype = new DiagramComponent();

GoogleChartComponent.prototype.init = function(attributes, manager) {
    DiagramComponent.prototype.init(attributes, manager);
    // TODO: Options sa moze nacitat tu vypracovat a ulozit
    this.updateData();
};



// ------------- Web Duration
WebDurationComp = function () {
    GoogleChartComponent.call();
    this.title  = "Duration of visit";
    this.name = "webDuration";
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
        title: 'Duration of visit | Minutes per domain',
        height: 600,
        vAxis: {title: 'Minutes'}
    };

    this.diagram = new google.visualization.ColumnChart(this.getDiagramElement());
    this.diagram.draw(gdata, options);
};




// ------------- BrowserVsRewrittenCodeCom
BrowserVsRewrittenCodeCom = function () {
    GoogleChartComponent.call();
    this.title  = "Browser vs Rewritten code";
    this.name = "browserVsRewrittenCode";
};
BrowserVsRewrittenCodeCom.prototype = new GoogleChartComponent();

BrowserVsRewrittenCodeCom.prototype.setData = function (name) {
    // https://google-developers.appspot.com/chart/interactive/docs/gallery/scatterchart
    var data = google.visualization.arrayToDataTable([
        ['Age', 'Weight'],
        [ 8, 12],
        [ 4, 5.5],
        [ 11, 14],
        [ 4, 5],
        [ 3, 3.5],
        [ 6.5, 7]
    ]);

    var options = {
        title: 'Age vs. Weight comparison',
        hAxis: {title: 'Age', minValue: 0, maxValue: 15},
        vAxis: {title: 'Weight', minValue: 0, maxValue: 15},
        legend: 'none'
    };

    this.diagram = new google.visualization.ScatterChart(this.getDiagramElement());
    this.diagram.draw(data, options);
};


// ------------- Files modifications, in counts
FilesModificationCom = function () {
    GoogleChartComponent.call();
    this.title  = "Files modifications, in counts";
    this.name = "fileModifications";
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
    this.title  = "Domain visit, in counts";
    this.name = "domainVisits";
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

