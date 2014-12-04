/**
 * Created by Seky on 3. 12. 2014.
 */
DetailComponent = function () {
    DiagramComponent.call();
    this.loader = new ChunksLoader(this);
    this.charts = new ChartPanel(this);
    this.title = "Action visualization";
    this.name = "actions";
};
DetailComponent.prototype = new DiagramComponent();

DetailComponent.prototype.init = function (atts, manager) {
    DiagramComponent.prototype.init.call(this, atts, manager);
    this.loader.init();
    this.charts.loadRange(atts.range.start, atts.range.end);
    this._createTimeline();
    this._registerQTip();
    this.diagram.draw();
    this.diagram.setVisibleChartRange(atts.range.start, atts.range.end);
    this.updateData();
};

DetailComponent.prototype._onRangeChanged = function () {
    if(this.diagram == undefined) {
        return;
    }
    var range = this.diagram.getVisibleChartRange();
    this.loader.onRangeChanged(this.attributes.developer, range.start, range.end);
    this.charts.redraw();
};

DetailComponent.prototype._createTimeline = function () {
    // Nastavenia timeline
    var windowHeight = $(window).height();
    var options = {
        width: "100%",
        height: "94%",
        layout: "circle",
        style: 'circle',
        axisOnTop: true,
        editable: false,
        showNavigation: true,
        zoomMin: 1000 * 60 * 1,             // 5 min
        zoomMax: 1000 * 60 * 60 * 24 * 7,     // tyzden
        showMajorLabels: true,
        //stackEvents: true,
        cluster: true,
        groupMinHeight: windowHeight * 0.1,
        groupWidth: '20px'
    };

    // Instantiate our timeline object.
    this.diagram = new links.Timeline(this.getDiagramElement(), options);
    google.visualization.events.addListener(this.diagram, 'rangechanged', this._onRangeChanged);
};

DetailComponent.prototype._onItemMouseClick = function (api, item) {
    console.log(item.metadata.id);
    var html = 'Loading...';
    if (item.group.content == "Web") {
        html = '<a href="' + item.content + '">' + item.content + '</a>';
    } else if (item.group.content == "Ide") {  // ide
        html = 'Changed lines: ' + item.metadata.y + '</br>'
            + 'Changed in future: ' + item.metadata.changedInFuture + '</br>'
            + 'Changed in past: ' + item.metadata.changedInHistory + '</br>'
            + 'Path: ' + item.content + '</br>'
            + 'Text: </br><pre>' + item.metadata.text + '</pre></br>';
    }
    return html;
};

DetailComponent.prototype.destroy = function () {
    this.diagram.deleteAllItems();
    DiagramComponent.prototype.destroy.call(this);
};

DetailComponent.prototype._toggleMetric = function (value) {
    var value;
    var prototyp = links.Timeline.Item.prototype;
    if (value === 1) {
        value = prototyp.computeSizeByChangesInFuture;
    } else if (value === 2) {
        value = prototyp.computeSizeByChangedLines;
    } else {
        value = prototyp.computeSizeConstant;
    }
    prototyp.computeSize = value;
    this.diagram.redraw();
};

DetailComponent.prototype._registerQTip = function () {
    var instance = this;
    this.dom.on("mouseenter", '.timeline-event-circle', function ($e) {
        $(this).qtip({
            overwrite: false,
            hide: 'unfocus',
            show: 'click',
            content: {
                text: function (event, api) {
                    var item = instance.panel.getSelected();
                    if (item === undefined) {
                        return 'Not selected entity.';
                    } else if (item.isCluster) {
                        return "Toto je skupina udalosti.";
                    } else if (item.metadata == undefined) {
                        return 'Ziadne data.';
                    }
                    return instance._onItemMouseClick(api, item);
                }
            }
        });
    });
};

DetailComponent.prototype.setRange = function (range) {
    this.diagram.setVisibleChartRange(range.start, range.end);
    this.attributes.range = range;
    this._updateDescription();
};

DetailComponent.prototype._buildDom = function () {
    return this._buildHeader() + '<div> \
     <div class="leftBar">     \
        <div class="mytimeline diagram graph-' + this.getName() + '"></div>  \
    </div>      \
        <div class="rightBar">  \
            <div class="charts">  \
                <div class="pieChart1"></div>  \
                <div class="pieChart2"></div> \
                <div class="pieChart3"></div>  \
             </div>   \
            <div class="legenda">    \
                <div class="title posun">Nezobrazuju sa ziadne aktivity.</div>  \
                <div class="title posun">Preto si vyber developera alebo sa posun na casovej osi.</div>  \
                <div class="title">Snaz sa identifikovat tieto aktivity:</div> \
                <table width="100%" align="center">  \
                    <tr> \
                        <td>  \
                            <div class="timeline-event timeline-event-circle cWeb">  \
                                <div class="timeline-event-content fixed-size">Web</div>  \
                            </div>   \
                        </td> \
                        <td>  \
                            <p>Udalost vo webovom prehliadaci.</p> \
                        </td>  \
                    </tr>  \
                    <tr>   \
                        <td> \
                            <div class="timeline-event timeline-event-circle cIde"> \
                                <div class="timeline-event-content fixed-size">Ide</div>  \
                            </div>  \
                        </td> \
                        <td> \
                            <p>Udalost vo vyvojom prostredi.</p> \
                        </td> \
                    </tr> \
                    <tr>   \
                        <td>  \
                            <div class="timeline-event timeline-event-circle cUnkown">  \
                                <div class="timeline-event-content fixed-size">Unknown</div>  \
                            </div> \
                        </td>  \
                        <td> \
                            <p>Neznama udalost.</p>  \
                        </td>  \
                    </tr> \
                    <tr>   \
                        <td> \
                            <div class="timeline-event timeline-event-circle timeline-event-cluster ui-widget-header"> \
                                <div class="timeline-event-content fixed-size">46</div> \
                            </div>  \
                        </td>  \
                        <td>  \
                            <p>Aktivita reprezentuje skupinu udalosti.</p>  \
                        </td> \
                    </tr> \
                </table> \
            </div>  \
        </div> \
        <div class="clear"></div>  \
    </div>';
};
