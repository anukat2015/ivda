<html>
<head>
    <title>Interactive visualization of developer‘s actions</title>

    <!-- for mobile devices like android and iphone -->
    <meta content="True" name="HandheldFriendly"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0"/>
    <meta name="Author" content="Lukas Sekerak"/>
    <meta name="robots" content="noindex"/>

    <script type="text/javascript" src="libs/vis/dist/vis.js"></script>
    <link href="libs/vis/dist/vis.css" rel="stylesheet" type="text/css"/>

    <link rel="shortcut icon" type="image/x-icon" href="logo.png"/>
    <link rel="stylesheet" type="text/css" href="libs/timeline-2.8.0/timeline.css"/>
    <link rel="stylesheet" type="text/css" href="libs/qtip2/jquery.qtip.css"/>
    <link rel="stylesheet" type="text/css" href="libs/datetimepicker/jquery.datetimepicker.css"/>
    <link rel="stylesheet" type="text/css" href="libs/selectize.js/dist/css/selectize.default.css"/>
    <link rel="stylesheet" type="text/css" href="styles/loader.css"/>
    <link rel="stylesheet" type="text/css" href="styles/timeline.css"/>
    <link rel="stylesheet" type="text/css" href="styles/style.css"/>

    <script type="text/javascript" src="https://code.jquery.com/jquery-1.11.0.min.js"></script>
    <script type="text/javascript" src="https://www.google.com/jsapi"></script>

    <script type="text/javascript" src="libs/qtip2/jquery.qtip.js"></script>
    <script type="text/javascript" src="libs/datetimepicker/jquery.datetimepicker.js"></script>
    <script type="text/javascript" src="libs/selectize.js/dist/js/standalone/selectize.js"></script>

    <script type="text/javascript" src="libs/timeline-2.8.0/timeline.js"></script>
    <script type="text/javascript" src="libs/timeline-2.8.0/parts/Item.js"></script>
    <script type="text/javascript" src="libs/timeline-2.8.0/parts/ItemRange.js"></script>
    <script type="text/javascript" src="libs/timeline-2.8.0/parts/ItemFloatingRange.js"></script>
    <script type="text/javascript" src="libs/timeline-2.8.0/parts/ItemDot.js"></script>
    <script type="text/javascript" src="libs/timeline-2.8.0/parts/ItemMetrics.js"></script>
    <script type="text/javascript" src="libs/timeline-2.8.0/parts/ItemCircle.js"></script>
    <script type="text/javascript" src="libs/timeline-2.8.0/parts/ItemChart.js"></script>
    <script type="text/javascript" src="libs/timeline-2.8.0/parts/ItemBox.js"></script>
    <script type="text/javascript" src="libs/timeline-2.8.0/parts/ClusterGenerator.js"></script>
    <script type="text/javascript" src="libs/timeline-2.8.0/parts/StepDate.js"></script>
    <script type="text/javascript" src="libs/timeline-2.8.0/parts/StepDate.js"></script>

    <script type="text/javascript" src="js/grouping/BoundedGroup.js"></script>
    <script type="text/javascript" src="js/grouping/DivideByTimeAndType.js"></script>
    <script type="text/javascript" src="js/grouping/ProcessAsGroup.js"></script>

    <script type="text/javascript" src="js/Util.js"></script>
    <script type="text/javascript" src="js/Preloader.js"></script>
    <script type="text/javascript" src="js/Timeline.js"></script>
    <script type="text/javascript" src="js/ChartPanel.js"></script>
    <script type="text/javascript" src="js/GraphData.js"></script>
    <script type="text/javascript" src="js/GraphPanel.js"></script>
    <script type="text/javascript" src="js/ChunksLoader.js"></script>
    <script type="text/javascript" src="js/Toolbar.js"></script>
    <script type="text/javascript" src="js/IvdaService.js"></script>
    <script type="text/javascript" src="js/Globals.js"></script>

    <script type="text/javascript">
        var gGlobals = undefined;
        google.load("visualization", "1", {packages: ["corechart", 'table']});
        google.setOnLoadCallback(onLoad);
    </script>
</head>

<body onresize="onReSize();">
<div id="toolbar">
    Start time: <input type="text" id="startDate" value=""/>
    End time: <input type="text" id="endDate" value=""/>
    <input type="button" id="setRange" value="Set" onclick="onSetTime();"/>
    <input type="button" id="setCurrentTime" value="Current time" onclick="onSetCurrentTime();"/>
    <input type="text" id="select-links" class="demo-default" value="" placeholder="Pick some developer..."/>
</div>

<div id="leftBar">
    <div class="mytimeline" id="mytimeline"></div>
</div>
<div id="rightBar">
    <div id="pieChart1"></div>
    <div id="pieChart2"></div>
    <div id="pieChart3"></div>
    <div id="legenda">
        <div class="title posun">Nezobrazuju sa ziadne aktivity.</div>
        <div class="title posun">Preto si vyber developera alebo sa posun na casovej osi.</div>
        <div class="title">Snaz sa identifikovat tieto aktivity:</div>
        <table width="100%" align="center">
            <tr>
                <td>
                    <div class="timeline-event timeline-event-circle cWeb">
                        <div class="timeline-event-content fixed-size">Web</div>
                    </div>
                </td>
                <td>
                    <p>Udalost vo webovom prehliadaci.</p>
                </td>
            </tr>
            <tr>
                <td>
                    <div class="timeline-event timeline-event-circle cIde">
                        <div class="timeline-event-content fixed-size">Ide</div>
                    </div>
                </td>
                <td>
                    <p>Udalost vo vyvojom prostredi.</p>
                </td>
            </tr>
            <tr>
                <td>
                    <div class="timeline-event timeline-event-circle cUnkown">
                        <div class="timeline-event-content fixed-size">Unknown</div>
                    </div>
                </td>
                <td>
                    <p>Neznama udalost.</p>
                </td>
            </tr>
            <tr>
                <td>
                    <div class="timeline-event timeline-event-circle timeline-event-cluster ui-widget-header">
                        <div class="timeline-event-content fixed-size">46</div>
                    </div>
                </td>
                <td>
                    <p>Aktivita reprezentuje skupinu udalosti.</p>
                </td>
            </tr>
        </table>
    </div>
</div>
<div class="clear"></div>
<div id="datatable"></div>

<div>
    <h1>Count of events | Per day</h1>

    <div class="mytimeline" id="graph-countEventsPerDay"></div>

    <h1>Count of events | Per hour</h1>

    <div class="mytimeline" id="graph-countEventsPerHour"></div>

    <h1>Changes of source codes | LOC Per File | Global</h1>

    <div class="mytimeline" id="graph-locChangesGlobal"></div>

    <h1>Changes of source codes | LOC Per File | Local</h1>

    <div class="mytimeline" id="graph-locChangesLocal"></div>

    <h1>Activity dynamic-range-histogram</h1>

    <div class="mytimeline" id="graph-activityHistogram"></div>

    <h1>Activities from local data | Duration of activity</h1>

    <div id="graph-activities"></div>

    <h1>Files modifications | Counts per file</h1>

    <div id="graph-fileModifications"></div>

    <h1>Domain visits | Count per domain</h1>

    <div id="graph-domainVisits"></div>
</div>
<div id="loader-wrapper">
    <div id="loader"></div>
</div>
<div id="loader-text"></div>

</body>
</html>
