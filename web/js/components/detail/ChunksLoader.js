/**
 * Trieda sa stara o nacitavanie eventov zo sluzby.
 * Eventy su priradenie do chunkov, skupiny, ktora je cela naraz spravovana.
 * Eventy v skupine su teda naraz stiahnute, vytvorene a vymazane.
 * @constructor
 */
ChunksLoader = function () {
    this.parent = undefined;
    this.CHUNK_SIZE = (60 * 1000 * 60 * 24); // cely den
    this.actualMin = undefined;
    this.actualMax = undefined;
};

/**
 * Nacitaj data pre cely casovy interval
 * @param start
 * @param end
 * @param finishCallback
 */
ChunksLoader.prototype.loadRange = function (component, start, end) {
    this.parent = component;
    // Hoci pouzivatel nam povedal ze sa mame pozriet na tu danu oblast, my to zaokruhlime - zoberiem zo sirsej perspektivy
    // Statistiky sa mu vypocitaju na zaklade prvkov, ktore su viditelne
    this.actualMin = start.floor(this.CHUNK_SIZE);
    this.actualMax = end.ceil(this.CHUNK_SIZE);

    var chunks = this.chunksCount(this.actualMax, this.actualMin);
    this.loadChunks(this.actualMin, chunks);
};

/**
 * Vymaz eventy z timelinu a repoziatara na zaklade casu
 * @param start
 * @param end
 */
ChunksLoader.prototype.deleteByTime = function (start, end) {
    //console.log("deleteItems " + new Date(start).toString() + " " + new Date(end).toString());
    // Vymaz eventy z Timelinu
    var changed = this.parent.diagram.deleteItems(start, end);
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
    chunked = this.chunksCount(start.floor(this.CHUNK_SIZE), this.actualMin);
    newMin = new Date(this.actualMin.getTime() + this.CHUNK_SIZE * chunked);
    if (chunked !== 0) { // Nepohli sme sa o zanedbatelny kusok
        if (newMin.getTime() > this.actualMin.getTime()) {
            // Pohli sme sa doprava o minimalne chunk, cize zmaz stare udaje
            this.deleteByTime(this.actualMin, newMin);
        } else {
            this.loadChunks(newMin, chunked);
        }
        this.actualMin = newMin;
    }

    // Vypocitaj offset pre pravu stranu
    chunked = this.chunksCount(end.ceil(this.CHUNK_SIZE), this.actualMax);
    newMax = new Date(this.actualMax.getTime() + this.CHUNK_SIZE * chunked);
    if (chunked !== 0) {
        if (newMax.getTime() < this.actualMax.getTime()) {
            // Pohli sme sa dolava o minimalne chunk, cize zmaz stare udaje
            this.deleteByTime(newMax, this.actualMax);
        } else {
            this.loadChunks(this.actualMax, chunked);
        }
        this.actualMax = newMax;
    }

    console.log("Nove hranice " + this.actualMin.toString() + " " + this.actualMax.toString());
};

/**
 * Nacitaj urcity pocet chunkov od pociatocneho datumu
 * @param min pociatocny datum
 * @param chunks  pocet chunkov
 */
ChunksLoader.prototype.loadChunks = function (min, chunks) {
    var end;
    var count = chunks > 0 ? chunks : chunks * -1;
    var temp = min.getTime();
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
    var instance = this;
    gGlobals.service.getTimeline(new Date(start), new Date(end), this.parent.attributes.developer, function (data) {
        // Pozor: Odpoved mohla prist asynchronne a mohla nejaku predbehnut ;)
        // Alebo prisla neskoro a hranice uz su zmenene ..
        // To nevadi ,... lebo timeline sa nepozera na poradie v array len na datumy
        //console.log("addItems " + data.getNumberOfRows());
        instance.acceptData(data);
    });
};

ChunksLoader.prototype.chunksCount = function (max, min) {
    return Math.floor((max.getTime() - min.getTime()) / this.CHUNK_SIZE);
};

/**
 * Data prichadzaju vo forme zoznamu eventov pre developera.
 * tzv pride serializovana List<TimelineEvent>
 * @param events
 */
ChunksLoader.prototype.acceptData = function (events) {
    if(this.parent.diagram == undefined) {
        return;
    }
    gGlobals.service._convertDates(events);
    this.parent.diagram.addItems(events, true);
    this.parent.diagram.redraw();
    this.parent.charts.redraw();
};
