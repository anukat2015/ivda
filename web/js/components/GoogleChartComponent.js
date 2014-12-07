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
    this.title = "Dlzka navstivenych webov";
    this.name = "webDuration";
    this.groups = ["DAY", "HOUR", "MINUTE"];
};
WebDurationComp.prototype = new GoogleChartComponent();

WebDurationComp.prototype.setData = function (data) {
    // Activity at web pages
    var gdata = new google.visualization.DataTable();
    gdata.addColumn('string', 'Domena');
    gdata.addColumn('number', 'Dlzka');
    data.forEach(function (key) {
        gdata.addRow([key.content, key.y]);
    });
    var options = {
        height: 500,
        vAxis: {title: this.attributes.granularity + "S"},
        legend: { position: 'none' } ,
        explorer: {
            maxZoomOut:2,
            keepInBounds: true
        }
    };

    this.diagram = new google.visualization.ColumnChart(this.getDiagramElement());
    this.diagram.draw(gdata, options);
};


// ------------- BrowserVsRewrittenCodeCom
BrowserVsRewrittenCodeCom = function () {
    GoogleChartComponent.call();
    this.title = "Aktivita v prehliadaci voci naslednemu napisanemu kodu";
    this.name = "browserVsRewrittenCode";
    this.groups = ["HOUR", "MINUTE"];
};
BrowserVsRewrittenCodeCom.prototype = new GoogleChartComponent();

BrowserVsRewrittenCodeCom.prototype.setData = function (data) {
    // https://google-developers.appspot.com/chart/interactive/docs/gallery/scatterchart
    var gdata = new google.visualization.DataTable();
    gdata.addColumn('number', 'Aktivita v prehliadaci');
    gdata.addColumn('number', 'Aktivita v prehliadaci v minutach, Zmeny v zdrojovom kode podla LOC metriky');
    data.forEach(function (key) {
        gdata.addRow([parseInt(key.content), key.y]);
    });

    var options = {
        hAxis: {title: 'Aktivita v prehliadaci v ' + this.attributes.granularity + "S"},
        vAxis: {title: 'Napisany kod v LOC metrike'},
        height: 500,
        legend: { position: 'none' } ,
        explorer: {
            maxZoomOut:2,
            keepInBounds: true
        }
    };

    this.diagram = new google.visualization.ScatterChart(this.getDiagramElement());
    this.diagram.draw(gdata, options);
};


// ------------- Files modifications, in counts
FilesModificationCom = function () {
    GoogleChartComponent.call();
    this.title = "Modifikovane subory";
    this.name = "fileModifications";
};
FilesModificationCom.prototype = new GoogleChartComponent();

FilesModificationCom.prototype.setData = function (data) {
    // Files modifications
    var gdata = new google.visualization.DataTable();
    gdata.addColumn('string', 'Path');
    gdata.addColumn('number', 'Pocet modifikacii');
    data.forEach(function (key) {
        gdata.addRow([key.content, key.y]);
    });
    var options = {
        vAxis: {title: 'Pocet modifikacii suboru'},
        legend: { position: 'none' } ,
        explorer: {
            maxZoomOut:2,
            keepInBounds: true
        }
    };
    this.diagram = new google.visualization.ColumnChart(this.getDiagramElement());
    this.diagram.draw(gdata, options);
};


// ------------- Files modifications, in counts
DomainVisitCom = function () {
    GoogleChartComponent.call();
    this.title = "Navstivene domeny";
    this.name = "domainVisits";
};
DomainVisitCom.prototype = new GoogleChartComponent();

DomainVisitCom.prototype.setData = function (data) {
    // Files modifications
    var gdata = new google.visualization.DataTable();
    gdata.addColumn('string', 'Domena');
    gdata.addColumn('number', 'Pocet navstev');
    data.forEach(function (key) {
        gdata.addRow([key.content, key.y]);
    });
    var options = {
        vAxis: {title: 'Pocet navstev'},
        hAxis: {title: 'Domena navstivena kliknutim, zadanim url, vybranim z oblubenych, ...'},
        legend: { position: 'none' } ,
        explorer: {
            maxZoomOut:2,
            keepInBounds: true
        }
    };
    this.diagram = new google.visualization.ColumnChart(this.getDiagramElement());
    this.diagram.draw(gdata, options);
};

