/**
 * Trieda sa stara o nacitavanie eventov zo sluzby.
 * Eventy su priradenie do chunkov, skupiny, ktora je cela naraz spravovana.
 * Eventy v skupine su teda naraz stiahnute, vytvorene a vymazane.
 * @constructor
 */
ChunksLoader = function () {
    this.CHUNK_SIZE = (60 * 1000 * 60 * 24); // cely den

    this.actualMin = undefined;
    this.actualMax = undefined;
    this.finisherCounts = -1;
    this.finisherCallback = undefined;
    this.developer = undefined;
};

/**
 * Nacitaj data pre cely casovy interval
 * @param start
 * @param end
 * @param finishCallback
 */
ChunksLoader.prototype.loadRange = function (start, end, finishCallback) {
    // Hoci pouzivatel nam povedal ze sa mame pozriet na tu danu oblast, my to zaokruhlime - zoberiem zo sirsej perspektivy
    // Statistiky sa mu vypocitaju na zaklade prvkov, ktore su viditelne
    this.actualMin = this.chunkRound2Left(start);
    this.actualMax = this.chunkRound2Right(end);

    var chunks = this.chunksCount(this.actualMax, this.actualMin);
    this.finisherCallback = finishCallback;
    this.finisherCounts = chunks;
    this.loadChunks(this.actualMin, chunks);
    gGlobals.preloader.start();
};

/**
 * Vymaz eventy z timelinu a repoziatara na zaklade casu
 * @param start
 * @param end
 */
ChunksLoader.prototype.deleteByTime = function (start, end) {
    //console.log("deleteItems " + new Date(start).toString() + " " + new Date(end).toString());
    // Vymaz eventy z Timelinu
    var changed = gGlobals.timeline.deleteItems(start, end);
    //console.log("deleted " + changed);
};

/**
 * Handler, ktory zachycuje pohyb casovej osi
 * @param start
 * @param end
 */
ChunksLoader.prototype.onRangeChanged = function (start, end) {
    var chunked, newMin, newMax;

    // Vypocitaj offset pre lavu stranu
    chunked = this.chunksCount(this.chunkRound2Left(start), this.actualMin);
    newMin = this.actualMin + this.CHUNK_SIZE * chunked;
    if (chunked !== 0) { // Nepohli sme sa o zanedbatelny kusok
        if (newMin > this.actualMin) {
            // Pohli sme sa doprava o minimalne chunk, cize zmaz stare udaje
            this.deleteByTime(this.actualMin, newMin);
        } else {
            this.loadChunks(newMin, chunked);
        }
        this.actualMin = newMin;
    }

    // Vypocitaj offset pre pravu stranu
    chunked = this.chunksCount(this.chunkRound2Right(end), this.actualMax);
    newMax = this.actualMax + this.CHUNK_SIZE * chunked;
    if (chunked !== 0) {
        if (newMax < this.actualMax) {
            // Pohli sme sa dolava o minimalne chunk, cize zmaz stare udaje
            this.deleteByTime(newMax, this.actualMax);
        } else {
            this.loadChunks(this.actualMax, chunked);
        }
        this.actualMax = newMax;
    }

    console.log("Nove hranice " + new Date(this.actualMin).toString() + " " + new Date(this.actualMax).toString());
};

/**
 * Nacitaj urcity pocet chunkov od pociatocneho datumu
 * @param min pociatocny datum
 * @param chunks  pocet chunkov
 */
ChunksLoader.prototype.loadChunks = function (min, chunks) {
    var end;
    var count = chunks > 0 ? chunks : chunks * -1;
    var temp = min;
    gGlobals.preloader.tasks += count;
    for (var i = 0; i < count; i++) {
        end = temp + this.CHUNK_SIZE;
        this.loadChunk(temp, end);
        temp = end;
    }
};

/**
 * Stiahni eventy pre jeden chunk.
 * Chunk je definovany casovym intervalom.
 * @param start
 * @param end
 */
ChunksLoader.prototype.loadChunk = function (start, end) {
    //console.log("loadChunk " + new Date(start).toString() + " " + new Date(end).toString());
    var url = gGlobals.getTimelineServiceURL(new Date(start), new Date(end));
    //console.log(url);
    var instance = this;
    $.ajax({
        dataType: "json",
        url: url,
        error: function (jqXHR, textStatus, errorThrown) {
            gGlobals.alertError("Server response status:" + textStatus);
        },
        success: function (data, textStatus, jqXHR) {
            // Pozor: Odpoved mohla prist asynchronne a mohla nejaku predbehnut ;)
            // Alebo prisla neskoro a hranice uz su zmenene ..
            // To nevadi ,... lebo timeline sa nepozera na poradie v array len na datumy
            //console.log("addItems " + data.getNumberOfRows());
            instance.acceptData(data);
        }
    }).always(function () {
        gGlobals.preloader.finishedTasks++;
        instance.finisherCounts--;
        if (instance.finisherCounts === 0) {
            instance.finisherCallback();
        }
    });
};


/**
 * Prichadzajuce eventy zo sluzby je potrebne spracovat.
 * @param events
 */
ChunksLoader.prototype.prepareEvents = function (events) {
    var item;
    for (var i = 0; i < events.length; i++) {
        item = events[i];
        if (item.start != null) {
            item.start = new Date(parseInt(item.start) + gGlobals.timezoneOffset);
        }
        if (item.end != null) {
            item.end = new Date(parseInt(item.end) + gGlobals.timezoneOffset);
        }
    }
};

/**
 * Data prichadzaju vo forme zoznamu eventov pre developera.
 * tzv pride serializovana List<TimelineEvent>
 * @param events
 */
ChunksLoader.prototype.acceptData = function (events) {
    this.prepareEvents(events);
    gGlobals.timeline.addItems(events, true);
    gGlobals.redraw();
};

ChunksLoader.prototype.chunkRound2Left = function (date) {
    return ( Math.floor(date.getTime() / this.CHUNK_SIZE) * this.CHUNK_SIZE);
};

ChunksLoader.prototype.chunkRound2Right = function (date) {
    return ( Math.ceil(date.getTime() / this.CHUNK_SIZE) * this.CHUNK_SIZE);
};

ChunksLoader.prototype.chunksCount = function (max, min) {
    return Math.floor((max - min) / this.CHUNK_SIZE);
};

/**
 * Handler, ktory zachytava zmenu developerov.
 * Reaguj na zmenu developerov.
 */
ChunksLoader.prototype.checkDevelopers = function () {
    var actual = gGlobals.getDeveloper();
    if (actual === this.developer) {
        return;
    }

    this.developer = actual;
    gGlobals.timeline.deleteAllItems();
    gGlobals.redraw();

    var range = gGlobals.timeline.getVisibleChartRange();
    this.loadRange(range.start, range.end, function () {
        console.log("finished loadRange");
        gGlobals.redraw();
        gGlobals.timeline.render({
            animate: false
        });
    });
};
