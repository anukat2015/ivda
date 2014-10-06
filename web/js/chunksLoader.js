/**
 * Trieda sa stara o nacitavanie eventov zo sluzby.
 * Eventy su priradenie do chunkov, skupiny, ktora je cela naraz spravovana.
 * Eventy v skupine su teda naraz stiahnute, vytvorene a vymazane.
 * @constructor
 */
ChunksLoader = function () {
    this.actualMin = undefined;
    this.actualMax = undefined;
    this.chunkSize = (60 * 1000 * 60); // jedna cela hodina
    this.showError = true;
    this.finisherCounts = -1;
    this.finisherCallback = undefined;
    this.tasks = 0;
    this.finishedTasks = 0;
    this.developers = [];
    this.groupRepositore = {}; // docasne ulozisko eventov pre danu skupinu, ked skupina sa nema zobrazit, udaje su presunute sem
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
    this.developers = gGlobals.getDevelopers();
    this.actualMin = this.chunkRound2Left(start);
    this.actualMax = this.chunkRound2Right(end);

    var chunks = this.chunksCount(this.actualMax, this.actualMin);
    this.finisherCallback = finishCallback;
    this.finisherCounts = chunks;
    this.loadChunks(this.actualMin, chunks);
};

/**
 * Pomocna metoda, ktora zisti ci mame zapnuteho daneho vyvojara
 * @param developer
 */
ChunksLoader.prototype.containsDeveloper = function (developer) {
    return this.developers.indexOf(developer) > -1;
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

    // Vymaz aj eventy z repozitara
    var instance = this;
    Object.keys(this.groupRepositore).forEach(function (group) {
        var events = instance.groupRepositore[group];
        filterItemsByInterval(events, start, end, function supplier(index, item) {
            events.splice(index, 1);
            return false;
        });
    });
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
    newMin = this.actualMin + this.chunkSize * chunked;
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
    newMax = this.actualMax + this.chunkSize * chunked;
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
    this.tasks += count;
    for (var i = 0; i < count; i++) {
        end = temp + this.chunkSize;
        this.loadChunk(temp, end);
        temp = end;
    }
};

/**
 * Data sa nepodarilo stiahnut
 * @param error
 */
ChunksLoader.prototype.alertError = function (error) {
    var msg = "Error in request: " + error;
    console.log(msg);
    if (this.showError) {
        alert(msg);
        this.showError = false;
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
    var url = gGlobals.getServiceURL(new Date(start), new Date(end));
    //console.log(url);
    var instance = this;
    $.ajax({
        dataType: "json",
        url: url,
        cache: false,
        error: function (jqXHR, textStatus, errorThrown) {
            instance.alertError("I get error:" + textStatus);
        },
        success: function (data, textStatus, jqXHR) {
            // Pozor: Odpoved mohla prist asynchronne a mohla nejaku predbehnut ;)
            // Alebo prisla neskoro a hranice uz su zmenene ..
            // To nevadi ,... lebo timeline sa nepozera na poradie v array len na datumy
            //console.log("addItems " + data.getNumberOfRows());
            if (data.status != "ok") {
                instance.alertError("I get error:" + data.status);
            } else {
                instance.acceptData(data.groups);
            }
        }
    }).always(function () {
        instance.finishedTasks++;
        instance.finisherCounts--;
        if (instance.finisherCounts === 0) {
            instance.finisherCallback();
        }
    });
};


ChunksLoader.prototype.pendingTasks = function () {
    return this.tasks - this.finishedTasks;
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
            item.start = new Date(parseInt(item.start));
        }
        if (item.end != null) {
            item.end = new Date(parseInt(item.end));
        }
    }
};

/**
 * Data prichadzaju vo forme zoznamu eventov pre developera.
 * tzv pride serializovana Map<String, List<TimelineEvent>>
 * @param events
 */
ChunksLoader.prototype.acceptData = function (groups) {
    var instance = this;
    var shouldReload = false;
    Object.keys(groups).forEach(function (group) {
        // Spracuj nove eventy
        var events = groups[group];
        instance.prepareEvents(events);

        // Developera mame zapnuteho, pridaj ho do timelinu
        if (instance.containsDeveloper(group)) {
            gGlobals.timeline.addItems(events, true);
            shouldReload = true;
        } else {
            // Developer je vypnuty, pridaj ho do repozitara
            if (!instance.groupRepositore.hasOwnProperty(group)) {  // presun vsetky prvky
                instance.groupRepositore[group] = events;
            } else {
                instance.groupRepositore[group] = instance.groupRepositore[group].concat(events);
            }
        }
    });

    if (shouldReload) {
        // Zmenili sa date, ktoire uzivatel ma vidiet
        gGlobals.redraw();
    }
};

ChunksLoader.prototype.chunkRound2Left = function (date) {
    return ( Math.floor(date.getTime() / this.chunkSize) * this.chunkSize);
};

ChunksLoader.prototype.chunkRound2Right = function (date) {
    return ( Math.ceil(date.getTime() / this.chunkSize) * this.chunkSize);
};

ChunksLoader.prototype.chunksCount = function (max, min) {
    return Math.floor((max - min) / this.chunkSize);
};

/**
 * Handler, ktory zachytava zmenu developerov.
 * Reaguj na zmenu developerov.
 */
ChunksLoader.prototype.checkDevelopers = function () {
    var actual = gGlobals.getDevelopers();
    var i, developer;
    var shouldRefresh = false;
    for (i = 0; i < actual.length; i++) {
        developer = actual[i];
        if (!this.containsDeveloper(developer)) {
            // Developera musime pridat ...
            //gGlobals.timeline.getGroup(developer); o to sa postara uz redraw
            if (this.groupRepositore.hasOwnProperty(developer)) {
                gGlobals.timeline.addItems(this.groupRepositore[developer], true);
                delete this.groupRepositore[developer];
            }
            shouldRefresh = true;
        }
    }
    for (i = 0; i < this.developers.length; i++) {
        developer = this.developers[i];
        if (actual.indexOf(developer) == -1) {
            // Presun eventy z timelinu do nasho repozitara
            // Ktore treba presunut? vsetky ktore patria do skupiny
            // Developera musime zmazat ...
            var deletedItems = gGlobals.timeline.deleteGroup(developer);
            if (this.groupRepositore.hasOwnProperty(developer)) {
                console.log("repozitar uz obsahuje skupinu, nieco je zle");
            }
            this.groupRepositore[developer] = deletedItems;
            shouldRefresh = true;
        }
    }

    if (shouldRefresh) {
        gGlobals.redraw();
    }
    this.developers = actual;
};