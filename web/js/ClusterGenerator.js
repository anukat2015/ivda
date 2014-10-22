/** ------------------------------------------------------------------------ **/

/**
 * @constructor links.Timeline.ClusterGenerator
 * Generator which creates clusters of items, based on the visible range in
 * the Timeline. There is a set of cluster levels which is cached.
 * @param {links.Timeline} timeline
 */
links.Timeline.ClusterGenerator = function (timeline) {
    this.timeline = timeline;
    this.clear();
};

/**
 * Clear all cached clusters and data, and initialize all variables
 */
links.Timeline.ClusterGenerator.prototype.clear = function () {
    // cache containing created clusters for each cluster level
    this.items = [];
    this.groups = {};
    this.clearCache();
};

/**
 * Clear the cached clusters
 */
links.Timeline.ClusterGenerator.prototype.clearCache = function () {
    // cache containing created clusters for each cluster level
    this.cache = {};
    this.cacheLevel = -1;
    this.cache[this.cacheLevel] = [];
};

/**
 * Set the items to be clustered.
 * This will clear cached clusters.
 * @param {Item[]} items
 * @param {Object} [options]  Available options:
 *                            {boolean} applyOnChangedLevel
 *                                If true (default), the changed data is applied
 *                                as soon the cluster level changes. If false,
 *                                The changed data is applied immediately
 */
links.Timeline.ClusterGenerator.prototype.setData = function (items, options) {
    this.items = items || [];
    this.dataChanged = true;
    this.applyOnChangedLevel = true;
    if (options && options.applyOnChangedLevel) {
        this.applyOnChangedLevel = options.applyOnChangedLevel;
    }
    // console.log('clustergenerator setData applyOnChangedLevel=' + this.applyOnChangedLevel); // TODO: cleanup
};

/**
 * Update the current data set: clear cache, and recalculate the clustering for
 * the current level
 */
links.Timeline.ClusterGenerator.prototype.updateData = function () {
    this.dataChanged = true;
    this.applyOnChangedLevel = false;
};

/**
 * Filter the items per group.
 * @private
 */
links.Timeline.ClusterGenerator.prototype.filterData = function () {
    // filter per group
    var items = this.items || [];
    var groups = {};
    this.groups = groups;

    // split the items per group
    items.forEach(function (item) {
        // put the item in the correct group
        var groupName = item.group ? item.group.content : '';
        var group = groups[groupName];
        if (!group) {
            group = [];
            groups[groupName] = group;
        }
        group.push(item);

        // calculate the center of the item
        if (item.start) {
            if (item.end) {
                // range
                item.center = (item.start.valueOf() + item.end.valueOf()) / 2;
            }
            else {
                // box, dot
                item.center = item.start.valueOf();
            }
        }
    });

    // sort the items per group
    for (var groupName in groups) {
        if (groups.hasOwnProperty(groupName)) {
            groups[groupName].sort(function (a, b) {
                return (a.center - b.center);
            });
        }
    }

    this.dataChanged = false;
};

/**
 * Cluster the events which are too close together
 * @param {Number} scale     The scale of the current window,
 *                           defined as (windowWidth / (endDate - startDate))
 * @return {Item[]} clusters
 */
links.Timeline.ClusterGenerator.prototype.getClusters = function (scale) {
    var level = -1,
        granularity = 2, // TODO: what granularity is needed for the cluster levels?
        timeWindow = 0,  // milliseconds
        maxItems = 5;    // TODO: do not hard code maxItems

    if (scale > 0) {
        level = Math.round(Math.log(100 / scale) / Math.log(granularity));
        timeWindow = Math.pow(granularity, level);
    }

    // clear the cache when and re-filter the data when needed.
    if (this.dataChanged) {
        var levelChanged = (level != this.cacheLevel);
        var applyDataNow = this.applyOnChangedLevel ? levelChanged : true;
        if (applyDataNow) {
            // TODO: currently drawn clusters should be removed! mark them as invisible?
            this.clearCache();
            this.filterData();
            // console.log('clustergenerator: cache cleared...'); // TODO: cleanup
        }
    }

    this.cacheLevel = level;
    var clusters = this.cache[level];
    if (!clusters) {
        // console.log('clustergenerator: create cluster level ' + level); // TODO: cleanup
        clusters = [];

        // TODO: spit this method, it is too large
        for (var groupName in this.groups) {
            if (this.groups.hasOwnProperty(groupName)) {
                var items = this.groups[groupName];
                var iMax = items.length;
                var i = 0;
                while (i < iMax) {
                    // find all items around current item, within the timeWindow
                    var item = items[i];
                    var neighbors = 1;  // start at 1, to include itself)

                    // loop through items left from the current item
                    var j = i - 1;
                    while (j >= 0 && (item.center - items[j].center) < timeWindow / 2) {
                        if (!items[j].cluster) {
                            neighbors++;
                        }
                        j--;
                    }

                    // loop through items right from the current item
                    var k = i + 1;
                    while (k < items.length && (items[k].center - item.center) < timeWindow / 2) {
                        neighbors++;
                        k++;
                    }

                    // loop through the created clusters
                    var l = clusters.length - 1;
                    while (l >= 0 && (item.center - clusters[l].center) < timeWindow / 2) {
                        if (item.group == clusters[l].group) {
                            neighbors++;
                        }
                        l--;
                    }

                    // aggregate until the number of items is within maxItems
                    if (neighbors > maxItems) {
                        // too busy in this window.
                        var num = neighbors - maxItems + 1;
                        clusters.push(this.createCluster(items, item, num, i));
                        i += num;
                    }
                    else {
                        delete item.cluster;
                        i += 1;
                    }
                }
            }
        }

        this.cache[level] = clusters;
    }

    return clusters;
};


links.Timeline.ClusterGenerator.prototype.createCluster = function (items, item, num, i) {
    // append the items to the cluster, and calculate the average start for the cluster
    var clusterItems = [];
    var avg = undefined;  // number. average of all start dates
    var min = undefined;  // number. minimum of all start dates
    var max = undefined;  // number. maximum of all start and end dates
    var containsRanges = false;
    var metadata = {
        changedLines: 0
    };

    var count = 0;
    var m = i;
    var met;
    while (clusterItems.length < num && m < items.length) {
        var p = items[m];
        clusterItems.push(p);
        var start = p.start.valueOf();
        var end = p.end ? p.end.valueOf() : p.start.valueOf();
        containsRanges = containsRanges || (p instanceof links.Timeline.ItemRange || p instanceof links.Timeline.ItemFloatingRange);

        // Metadata stats
        met = p.metadata;
        if (met != undefined) {
            if (met.changedLines != undefined) {
                metadata.changedLines += met.changedLines;
            }
        }

        // Stats
        if (count) {
            // calculate new average (use fractions to prevent overflow)
            avg = (count / (count + 1)) * avg + (1 / (count + 1)) * p.center;
        }
        else {
            avg = p.center;
        }
        min = (min != undefined) ? Math.min(min, start) : start;
        max = (max != undefined) ? Math.max(max, end) : end;
        count++;
        m++;
    }


    // Compute metadata
    var title = 'Cluster containing ' + clusterItems.length +
        ' events. Zoom in to see the individual events.';
    var content = '<div title="' + title + '">' + clusterItems.length + ' events</div>';
    var group = item.group ? item.group.content : undefined;
    var cluster;

    if (containsRanges) {
        // boxes and/or ranges
        cluster = this.timeline.createItem({
            'start': new Date(min),
            'end': new Date(max),
            'content': content,
            'group': group,
            'metadata': metadata
        });
    }
    else {
        // boxes only
        cluster = this.timeline.createItem({
            'start': new Date(avg),
            'content': content,
            'group': group,
            'metadata': metadata
        });
    }
    cluster.isCluster = true;
    cluster.items = clusterItems;
    cluster.items.forEach(function (item) {
        item.cluster = cluster;
    });
    return cluster;
}
;
