function Globals() {
    this.table = new google.visualization.Table(document.getElementById('datatable'));
    this.charts = new ChartPanel();
    this.timeline = new Timeline();
    this.graph = new GraphPanel();
    this.loader = new ChunksLoader();
    this.preloader = new Preloader();
    this.toolbar = new Toolbar();
    this.service = new IvdaService();
    this.wasInit = false;

    this.timeline.init();


    this.init = function (start, end, developers) {
        this.toolbar.init(developers, this.loader.checkDeveloper);
        this.toolbar.setTime(start, end);
        this.timeline.panel.draw();
        this.timeline.panel.setVisibleChartRange(start, end);
        this.wasInit = true;
    };

    this.redraw = function () {
        if (!this.wasInit) return;
        this.timeline.panel.redraw();
        this.charts.redraw();
        this.graph.redraw();
    };

    this.toggleProcesses = function () {
        this.graph.showProcesses = !this.graph.showProcesses;
        this.graph.redraw();
    };
}

function onReSize() {
    if (gGlobals && gGlobals.wasInit) {
        gGlobals.redraw();
    }
}

function onLoad() {
    gGlobals = new Globals();
    //var end = new Date();
    var end = new Date("2014-08-06T12:00:00.000");
    var start = new Date(end.getTime() - 2 * 24 * 60 * 60 * 1000); // posledne 2 dni

    // Stiahni mena vyvojarov
    $.ajax({
        dataType: "json",
        url: gGlobals.service.getDevelopersURL(),
        cache: false,
        success: function (developers, textStatus, jqXHR) {
            gGlobals.init(start, end, developers);
        }
    });
}
