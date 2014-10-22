acdtimeline.setWindow({
    start: st2,
    end:   et2
});

function checkIntersection(start, end, item) {
    if (item.end) {
        // Time range object // NH use getLeft and getRight here
        return (start <= item.start && item.end <= end);
    } else {
        // Point object
        return (start <= item.start && item.start <= end);
    }
};

/**
 * Get the id's of the currently visible items.
 * @returns {Array} The ids of the visible items
 */
ItemSet.prototype.getVisibleItems2 = function(supplier) {
  var range = this.body.range.getRange();
  var left  = this.body.util.toScreen(range.start);
  var right = this.body.util.toScreen(range.end);

  var ids = [];
  for (var groupId in this.groups) {
    if (this.groups.hasOwnProperty(groupId)) {
      var group = this.groups[groupId];
      var rawVisibleItems = group.visibleItems;

      // filter the "raw" set with visibleItems into a set which is really
      // visible by pixels
      for (var i = 0; i < rawVisibleItems.length; i++) {
        var item = rawVisibleItems[i];
        // TODO: also check whether visible vertically
        if ((item.left < right) && (item.left + item.width > left)) {
          ids.push(item.id);
        }
      }
    }
  }

  return ids;
};

/**
 * Find all elements within the start and end range
 * If no element is found, returns an empty array
 * @param start time
 * @param end time
 * @return Array itemsInRange
 */
function filterItemsByInterval(items, start, end, supplier) {
    if (items) {
        var i = 0;
        while (i < items.length) {
            var item = items[i];
            if (checkIntersection(start, end, item)) {
                if (!supplier(i, item)) {
                    continue; // preskoc iteraciu
                }
            }
            i++;
        }
    }
};

/**
 * Delete all items in range scope
 */
links.Timeline.prototype.deleteItems = function (start, end) {
    var timeline = this;
    var count = 0;
    filterItemsByInterval(this.items, start, end, function (index, item) {
        // dataSet.remove(id);
        timeline.deleteItem(index, true);
        count++;
        return false;
    });
    return count;
};

/**
 * Set a new value for the visible range int the timeline.
 * Set start undefined to include everything from the earliest date to end.
 * Set end undefined to include everything from start to the last date.
 * Example usage:
 *    myTimeline.setVisibleChartRange(new Date("2010-08-22"),
 *                                    new Date("2010-09-13"));
 * @param {Date}   start     The start date for the timeline. optional
 * @param {Date}   end       The end date for the timeline. optional
 * @param {boolean} redraw   Optional. If true (default) the Timeline is
 *                           directly redrawn
 */
links.Timeline.prototype.setVisibleChartRange = function (start, end, redraw) {
    var range = {};
    if (!start || !end) {
        // retrieve the date range of the items
        range = this.getDataRange(true);
    }

    if (!start) {
        if (end) {
            if (range.min && range.min.valueOf() < end.valueOf()) {
                // start of the data
                start = range.min;
            }
            else {
                // 7 days before the end
                start = new Date(end.valueOf());
                start.setDate(start.getDate() - 7);
            }
        }
        else {
            // default of 3 days ago
            start = new Date();
            start.setDate(start.getDate() - 3);
        }
    }

    if (!end) {
        if (range.max) {
            // end of the data
            end = range.max;
        }
        else {
            // 7 days after start
            end = new Date(start.valueOf());
            end.setDate(end.getDate() + 7);
        }
    }

    // prevent start Date <= end Date
    if (end <= start) {
        end = new Date(start.valueOf());
        end.setDate(end.getDate() + 7);
    }

    // limit to the allowed range (don't let this do by applyRange,
    // because that method will try to maintain the interval (end-start)
    var min = this.options.min ? this.options.min : undefined; // date
    if (min != undefined && start.valueOf() < min.valueOf()) {
        start = new Date(min.valueOf()); // date
    }
    var max = this.options.max ? this.options.max : undefined; // date
    if (max != undefined && end.valueOf() > max.valueOf()) {
        end = new Date(max.valueOf()); // date
    }

    this.applyRange(start, end);

    if (redraw == undefined || redraw == true) {
        this.render({
            animate: false
        });  // TODO: optimize, no reflow needed
    }
    else {
        this.recalcConversion();
    }
};
//  this.range.setRange(middle - interval / 2, middle + interval / 2, animate);
