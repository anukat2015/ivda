/**
 * @constructor links.Timeline.ItemFloatingRange
 * @extends links.Timeline.Item
 * @param {Object} data       Object containing parameters start, end
 *                            content, group, type, className, editable.
 * @param {Object} [options]  Options to set initial property values
 *                                {Number} top
 *                                {Number} left
 *                                {Number} width
 *                                {Number} height
 */
links.Timeline.ItemFloatingRange = function (data, options) {
    links.Timeline.Item.call(this, data, options);
};

links.Timeline.ItemFloatingRange.prototype = new links.Timeline.Item();

/**
 * Select the item
 * @override
 */
links.Timeline.ItemFloatingRange.prototype.select = function () {
    var dom = this.dom;
    links.Timeline.addClassName(dom, 'timeline-event-selected ui-state-active');
};

/**
 * Unselect the item
 * @override
 */
links.Timeline.ItemFloatingRange.prototype.unselect = function () {
    var dom = this.dom;
    links.Timeline.removeClassName(dom, 'timeline-event-selected ui-state-active');
};

/**
 * Creates the DOM for the item, depending on its type
 * @return {Element | undefined}
 * @override
 */
links.Timeline.ItemFloatingRange.prototype.createDOM = function () {
    // background box
    var divBox = document.createElement("DIV");
    divBox.style.position = "absolute";

    // contents box
    var divContent = document.createElement("DIV");
    divContent.className = "timeline-event-content";
    divBox.appendChild(divContent);

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
links.Timeline.ItemFloatingRange.prototype.showDOM = function (container) {
    var dom = this.dom;
    if (!dom) {
        dom = this.createDOM();
    }

    if (dom.parentNode != container) {
        if (dom.parentNode) {
            // container changed. remove the item from the old container
            this.hideDOM();
        }

        // append to the new container
        container.appendChild(dom);
        this.rendered = true;
    }
};

/**
 * Remove the items DOM from the current HTML container
 * The DOM will be kept in memory
 * @override
 */
links.Timeline.ItemFloatingRange.prototype.hideDOM = function () {
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
links.Timeline.ItemFloatingRange.prototype.updateDOM = function () {
    var divBox = this.dom;
    if (divBox) {
        // update contents
        divBox.firstChild.innerHTML = this.content;

        // update class
        divBox.className = "timeline-event timeline-event-range ui-widget ui-state-default";

        if (this.isCluster) {
            links.Timeline.addClassName(divBox, 'timeline-event-cluster ui-widget-header');
        }

        // add item specific class name when provided
        if (this.className) {
            links.Timeline.addClassName(divBox, this.className);
        }

        // TODO: apply selected className?
    }
};

/**
 * Reposition the item, recalculate its left, top, and width, using the current
 * range of the timeline and the timeline options. *
 * @param {links.Timeline} timeline
 * @override
 */
links.Timeline.ItemFloatingRange.prototype.updatePosition = function (timeline) {
    var dom = this.dom;
    if (dom) {
        var contentWidth = timeline.size.contentWidth,
            left = this.getLeft(timeline), // NH use getLeft
            right = this.getRight(timeline); // NH use getRight;

        // limit the width of the this, as browsers cannot draw very wide divs
        if (left < -contentWidth) {
            left = -contentWidth;
        }
        if (right > 2 * contentWidth) {
            right = 2 * contentWidth;
        }

        dom.style.top = this.top + "px";
        dom.style.left = left + "px";
        //dom.style.width = Math.max(right - left - 2 * this.borderWidth, 1) + "px"; // TODO: borderWidth
        dom.style.width = Math.max(right - left, 1) + "px";
    }
};

/**
 * Check if the item is visible in the timeline, and not part of a cluster
 * @param {Number} start
 * @param {Number} end
 * @return {boolean} visible
 * @override
 */
links.Timeline.ItemFloatingRange.prototype.isVisible = function (start, end) {
    if (this.cluster) {
        return false;
    }

    // NH check for no end value
    if (this.end && this.start) {
        return (this.end > start)
            && (this.start < end);
    } else if (this.start) {
        return (this.start < end);
    } else if (this.end) {
        return (this.end > start);
    } else {
        return true;
    }
};

/**
 * Reposition the item
 * @param {Number} left
 * @param {Number} right
 * @override
 */
links.Timeline.ItemFloatingRange.prototype.setPosition = function (left, right) {
    var dom = this.dom;

    dom.style.left = left + 'px';
    dom.style.width = (right - left) + 'px';

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
links.Timeline.ItemFloatingRange.prototype.getLeft = function (timeline) {
    // NH check for no start value
    if (this.start) {
        return timeline.timeToScreen(this.start);
    } else {
        return 0;
    }
};

/**
 * Calculate the right position of the item
 * @param {links.Timeline} timeline
 * @return {Number} right
 * @override
 */
links.Timeline.ItemFloatingRange.prototype.getRight = function (timeline) {
    // NH check for no end value
    if (this.end) {
        return timeline.timeToScreen(this.end);
    } else {
        return timeline.size.contentWidth;
    }
};

/**
 * Calculate the width of the item
 * @param {links.Timeline} timeline
 * @return {Number} width
 * @override
 */
links.Timeline.ItemFloatingRange.prototype.getWidth = function (timeline) {
    return this.getRight(timeline) - this.getLeft(timeline);
};