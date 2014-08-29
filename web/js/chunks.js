function Chunks() {
    this.actualMin = undefined;
    this.actualMax = undefined;
    this.chunkSize = (20 * 1000 * 60); // tzv 30min/20/10 teda kde zvysok 60%30= 0

    this.loadRange = function (start, end) {
        // Hoci pouzivatel nam povedal ze sa mame pozriet na tu danu oblast, my to zaokruhlime - zoberiem zo sirsej perspektivy
        // Statistiky sa mu vypocitaju na zaklade prvkov, ktore su viditelne
        this.actualMin = this.chunkRound2Left(start);
        this.actualMax = this.chunkRound2Right(end);
        this.loadChunks(this.actualMin, this.actualMax);
    }

    this.onRangeChanged = function (start, end) {
        var chunked, offset;
        var newMin, newMax;

        // Vypocitaj offset pre lavu stranu
        offset = this.chunkRound2Left(start) - this.actualMin;
        chunked = Math.floor(offset / this.chunkSize);
        newMin = this.actualMin + this.chunkSize * chunked;
        if (chunked > 0) { // Nepohli sme sa o zanedbatelny kusok
            if (newMin > this.actualMin) {
                // Pohli sme sa doprava o minimalne chunk, cize zmaz stare udaje
                gGlobals.timeline.removeItems(this.actualMin, newMin, true);
            } else {
                this.loadChunks(newMin, this.actualMin);
            }
        }

        // Vypocitaj offset pre pravu stranu
        offset = this.chunkRound2Right(end) - this.actualMax;
        chunked = Math.floor(offset / this.chunkSize);
        newMax = this.actualMax + this.chunkSize * chunked;
        if (chunked > 0) {
            if (newMax < this.actualMax) {
                // Pohli sme sa dolava o minimalne chunk, cize zmaz stare udaje
                gGlobals.timeline.removeItems(this.actualMax, newMax, true);
            } else {
                this.loadChunks(newMax, this.actualMax);
            }
        }

        this.actualMin = newMin;
        this.actualMin = newMax;
    }

    this.loadChunks = function (min, max) {
        for (var temp = min; temp < max;) {
            var end = temp + this.chunkSize;
            this.loadChunk(temp, end);
            temp = end;
        }
        return end;
    }

    this.loadChunk = function (start, end) {
        var url = this.getServiceURL(new Date(start), new Date(end));
        var query = new google.visualization.Query(url);
        query.send(this.handleChunk);
    }

    this.handleChunk = function (response) {
        if (response.isError()) {
            alert('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
            return;
        }
        var data = response.getDataTable();
        // Pozor: Odpoved mohla prist asynchronne a mohla nejaku predbehnut ;)
        // Alebo prisla neskoro a hranice uz su zmenene ..
        gGlobals.timeline.addItems(data, true);
    }

    this.chunkRound2Left = function (date) {
        return ( Math.floor(date.getTime() / this.chunkSize) * this.chunkSize); // zaokruhli na 00:20, 00:40, 00:00
    }

    this.chunkRound2Right = function (date) {
        return ( Math.ceil(date.getTime() / this.chunkSize) * this.chunkSize);
    }
}