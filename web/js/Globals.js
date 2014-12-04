function Globals() {
    //this.table = new google.visualization.Table(document.getElementById('datatable'));
    this.preloader = new Preloader();
    this.toolbar = new Toolbar();
    this.service = new IvdaService();
    this.graphs = new DiagramManager();
    this.wasInit = false;

    this.init = function () {
        this.toolbar.init();
        this.wasInit = true;
    };

    this.redraw = function () {
        if (!this.wasInit) return;
        this.graphs.redraw();
    };
}

function onReSize() {
    if (gGlobals && gGlobals.wasInit) {
        gGlobals.redraw();
    }
}

function onLoad() {
    gGlobals = new Globals();
    gGlobals.init();
}
