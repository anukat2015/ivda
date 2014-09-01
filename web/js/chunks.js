function Chunks() {
    this.actualMin = undefined;
    this.actualMax = undefined;
    this.chunkSize = (20 * 1000 * 60); // tzv 30min/20/10 teda kde zvysok 60%30= 0
    this.showError = true;
    this.finisherCounts = -1;
    this.finisherCallback = undefined;

    this.loadRange = function (start, end, finishcallback) {
        // Hoci pouzivatel nam povedal ze sa mame pozriet na tu danu oblast, my to zaokruhlime - zoberiem zo sirsej perspektivy
        // Statistiky sa mu vypocitaju na zaklade prvkov, ktore su viditelne
        this.actualMin = this.chunkRound2Left(start);
        this.actualMax = this.chunkRound2Right(end);

        var chunks = this.chunksCount(this.actualMax, this.actualMin);
        this.finisherCallback = finishcallback;
        this.finisherCounts = chunks;
        this.loadChunks(this.actualMin, chunks);
    };

    this.onRangeChanged = function (start, end) {
        var chunked, newMin, newMax;

        // Vypocitaj offset pre lavu stranu
        chunked = this.chunksCount(this.chunkRound2Left(start), this.actualMin);
        newMin = this.actualMin + this.chunkSize * chunked;
        if (chunked > 0) { // Nepohli sme sa o zanedbatelny kusok
            if (newMin > this.actualMin) {
                // Pohli sme sa doprava o minimalne chunk, cize zmaz stare udaje
                console.log("deleteItems " + new Date(this.actualMin).toString() + " " + new Date(newMin).toString());
                gGlobals.timeline.deleteItems(this.actualMin, newMin);
            } else {
                this.loadChunks(newMin, this.actualMin);
            }
        }

        // Vypocitaj offset pre pravu stranu
        chunked = this.chunksCount(this.chunkRound2Right(end), this.actualMax);
        newMax = this.actualMax + this.chunkSize * chunked;
        if (chunked > 0) {
            if (newMax < this.actualMax) {
                // Pohli sme sa dolava o minimalne chunk, cize zmaz stare udaje
                console.log("deleteItems " + new Date(this.actualMax).toString() + " " + new Date(newMax).toString());
                gGlobals.timeline.deleteItems(this.actualMax, newMax);
            } else {
                this.loadChunks(newMax, this.actualMax);
            }
        }

        this.actualMin = newMin;
        this.actualMax = newMax;
        console.log("Nove hranice " + new Date(this.actualMin).toString() + " " + new Date(this.actualMax).toString());
    };

    this.loadChunks = function (min, chunks) {
        var temp = min;
        for (var i = 0; i < chunks; i++) {
            var end = temp + this.chunkSize;
            this.loadChunk(temp, end);
            temp = end;
        }
        return temp;
    };

    this.loadChunk = function (start, end) {
        console.log("loadChunk " + new Date(start).toString() + " " + new Date(end).toString());
        var url = gGlobals.getServiceURL(new Date(start), new Date(end));
        console.log(url);
        var query = new google.visualization.Query(url);
        var chunks = this;
        query.send(function (response) {
            if (response.isError()) {
                var msg = "Error in query: " + response.getMessage() + " " + response.getDetailedMessage();
                console.log(msg);
                if (chunks.showError) {
                    alert(msg);
                    chunks.showError = false;
                }
            } else {
                chunks.addData(response.getDataTable());
            }

            this.finisherCounts--;
            if(this.finisherCounts == 0) {
                chunks.finisherCallback();
            }
        });
    };

    this.addData = function (data) {
        // Pozor: Odpoved mohla prist asynchronne a mohla nejaku predbehnut ;)
        // Alebo prisla neskoro a hranice uz su zmenene ..
        // To nevadi ,... lebo timeline sa nepozera na poradie v array len na datumy
        gGlobals.timeline.addItems(data, true);
    };

    this.chunkRound2Left = function (date) {
        return ( Math.floor(date.getTime() / this.chunkSize) * this.chunkSize); // zaokruhli na 00:20, 00:40, 00:00
    };

    this.chunkRound2Right = function (date) {
        return ( Math.ceil(date.getTime() / this.chunkSize) * this.chunkSize);
    };

    this.chunksCount = function(max, min) {
        offset = this.chunkRound2Left(start) - this.actualMin;
        return Math.floor((max - min) / this.chunkSize);
    };
}