/**
 * @constructor links.Timeline.ItemChart
 * @extends links.Timeline.Item
 * @param {Object} data       Object containing parameters start, end
 *                            content, group, type, className, editable.
 * @param {Object} [options]  Options to set initial property values
 *                                {Number} top
 *                                {Number} left
 *                                {Number} width
 *                                {Number} height
 */
links.Timeline.ItemChart = function (data, options) {
    links.Timeline.Item.call(this, data, options);
};

links.Timeline.ItemChart.prototype = new links.Timeline.Item();

/**
 * Reflow the Item: retrieve its actual size from the DOM
 * @return {boolean} resized    returns true if the axis is resized
 * @override
 */
links.Timeline.ItemChart.prototype.reflow = function () {
    var dom = this.dom,
        contentHeight = dom.content.offsetHeight,
        resized = (
            (this.contentHeight != contentHeight)
            );

    this.contentHeight = contentHeight;
    return resized;
};

/**
 * Select the item
 * @override
 */
links.Timeline.ItemChart.prototype.select = function () {
    var dom = this.dom;
    links.Timeline.addClassName(dom, 'timeline-event-selected ui-state-active');
};

/**
 * Unselect the item
 * @override
 */
links.Timeline.ItemChart.prototype.unselect = function () {
    var dom = this.dom;
    links.Timeline.removeClassName(dom, 'timeline-event-selected ui-state-active');
};

/**
 * Creates the DOM for the item, depending on its type
 * @return {Element | undefined}
 * @override
 */
links.Timeline.ItemChart.prototype.createDOM = function () {
    // background box
    var divBox = document.createElement("DIV");
    divBox.style.position = "absolute";

    // contents box, right from the dot
    var divContent = document.createElement("DIV");
    divContent.className = "timeline-event-content";
    divBox.appendChild(divContent);
    divBox.content = divContent;

    // Pridaj chart
    var changedLines = parseInt(this.metadata);
    var metric = (changedLines + 1) * 30;
    var radius = metric + "px";
    divContent.style.width = radius;
    divContent.style.height = radius;
    divContent.style.lineHeight = radius;

    var data = google.visualization.arrayToDataTable([
        ['Changes', 'LOC'],
        ['Changed lines', changedLines]
    ]);

    var options = {
        legend: 'none',
        // pieSliceText: 'label', TODO: zapni az ked je velky
        backgroundColor: 'transparent',
        chartArea: {width: '90%', height: '90%', left: '0%', top: '0%'},
        width: metric,
        height: metric
    };
    var chart = new google.visualization.PieChart(divContent);
    chart.draw(data, options);

    this.dom = divBox;
    this.updateDOM();
    return divBox;
};

/**
 * Append the items DOM to the given HTML container. If items DOM does not yet
 * exist, it will be created first.
 * @param {Element} container
 * @override
 */
links.Timeline.ItemChart.prototype.showDOM = function (container) {
    var dom = this.dom;
    if (!dom) {
        dom = this.createDOM();
    }

    if (dom.parentNode != container) {
        if (dom.parentNode) {
            // container changed. remove it from old container first
            this.hideDOM();
        }

        // append to container
        container.appendChild(dom);
        this.rendered = true;
    }
};

/**
 * Remove the items DOM from the current HTML container
 * @override
 */
links.Timeline.ItemChart.prototype.hideDOM = function () {
    var dom = this.dom;
    if (dom) {
        if (dom.parentNode) {
            dom.parentNode.removeChild(dom);
        }
        this.rendered = false;
    }
};

/**
 * Update the DOM of the item. This will update the content and the classes
 * of the item
 * @override
 */
links.Timeline.ItemChart.prototype.updateDOM = function () {
    if (this.dom) {
        var divBox = this.dom;

        // update contents
        divBox.firstChild.innerHTML = this.content;

        // update classes
        divBox.className = "timeline-event-chart";

        if (this.isCluster) {
            links.Timeline.addClassName(divBox, 'timeline-event-cluster ui-widget-header');
        }

        // add item specific class name when provided
        if (this.className) {
            links.Timeline.addClassName(divBox, this.className);
        }
    }
};

/**
 * Reposition the item, recalculate its left, top, and width, using the current
 * range of the timeline and the timeline options. *
 * @param {links.Timeline} timeline
 * @override
 */
links.Timeline.ItemChart.prototype.updatePosition = function (timeline) {
    var dom = this.dom;
    if (dom) {
        var left = timeline.timeToScreen(this.start);

        dom.style.top = this.top + "px";
        dom.style.left = (left - this.contentHeight / 2) + "px";

        //dom.content.style.marginLeft = (1.5 * this.dotWidth) + "px";
        //dom.content.style.marginTop = (0.5 * this.dotWidth) + "px";
    }
};

/**
 * Check if the item is visible in the timeline, and not part of a cluster.
 * @param {Date} start
 * @param {Date} end
 * @return {boolean} visible
 * @override
 */
links.Timeline.ItemChart.prototype.isVisible = function (start, end) {
    if (this.cluster) {
        return false;   // TODO: ked je to cluster scitaj deti a zobraz jeden velky kruh
    }

    return (this.start > start)
        && (this.start < end);
};

/**
 * Reposition the item
 * @param {Number} left
 * @param {Number} right
 * @override
 */
links.Timeline.ItemChart.prototype.setPosition = function (left, right) {
    var dom = this.dom;

    dom.style.left = (left - this.contentHeight / 2) + "px";

    if (this.group) {
        this.top = this.group.top;
        dom.style.top = this.top + 'px';
    }
};

/**
 * Calculate the left position of the item
 * @param {links.Timeline} timeline
 * @return {Number} left
 * @override
 */
links.Timeline.ItemChart.prototype.getLeft = function (timeline) {
    return timeline.timeToScreen(this.start);
};

/**
 * Calculate the right position of the item
 * @param {links.Timeline} timeline
 * @return {Number} right
 * @override
 */
links.Timeline.ItemChart.prototype.getRight = function (timeline) {
    return timeline.timeToScreen(this.start) + this.width;
};
