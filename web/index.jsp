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
    <link rel="stylesheet" type="text/css" href="libs/jquery-ui/jquery-ui.min.css"/>
    <link rel="stylesheet" type="text/css" href="styles/loader.css"/>
    <link rel="stylesheet" type="text/css" href="styles/timeline.css"/>
    <link rel="stylesheet" type="text/css" href="styles/style.css"/>

    <script type="text/javascript" src="https://code.jquery.com/jquery-1.11.1.min.js"></script>
    <script type="text/javascript" src="https://www.google.com/jsapi"></script>

    <script type="text/javascript" src="libs/jquery-ui/jquery-ui.min.js"></script>
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

    <script type="text/javascript" src="js/components/detail/ChartPanel.js"></script>
    <script type="text/javascript" src="js/components/detail/ChunksLoader.js"></script>
    <script type="text/javascript" src="js/components/detail/DetailComponent.js"></script>
    <script type="text/javascript" src="js/components/DiagramComponent.js"></script>
    <script type="text/javascript" src="js/components/GoogleChartComponent.js"></script>
    <script type="text/javascript" src="js/components/VisComponent.js"></script>
    <script type="text/javascript" src="js/components/DiagramManager.js"></script>

    <script type="text/javascript" src="js/util.js"></script>
    <script type="text/javascript" src="js/Preloader.js"></script>
    <script type="text/javascript" src="js/GraphData.js"></script>
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
    <input type="text" id="t-developer" value="" placeholder="Pick some developer..."/>
    <label for="t-startDate">Start time:</label>
    <input type="text" id="t-startDate" value=""/>
    <label for="t-endDate">End time:</label>
    <input type="text" id="t-endDate" value=""/>
    <input type="button" id="t-currentTime" value="Current time"/>
    <input type="text" id="t-feature" value="" placeholder="Choose tracked feature"/>
    <input type="text" id="t-granularity" value="Choose granularity"/>
    <input type="radio" id="t-lockMovement" value="1" checked>
    <label for="t-lockMovement">Lock movement</label>
    <input type="submit" id="t-submit" value="Create">
</div>
<div id="trash" style="display: none"> </div>
<div id="graphs"> </div>
<div id="loader-wrapper">
    <div id="loader"></div>
</div>
<div id="loader-text"></div>

</body>
</html>


