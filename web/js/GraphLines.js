/**
 * Created by Seky on 22. 10. 2014.
 */
function GraphLines() {
    this.lines = {
        changedLines: {
            'label': 'Changed lines',
            'data': []
        },
        changedInFuture: {
            'label': 'Changed in future',
            'data': []
        }
    };

    this.addPoint = function (type, date, value) {
        this.lines[type].data.push({
            'date': date,
            'value': value
        });
    };
}
