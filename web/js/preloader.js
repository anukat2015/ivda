/**
 * Created by Seky on 7. 10. 2014.
 */
function Preloader() {
    this.loader = $('#loader');
    this.loaderText = $('#loader-text');

    this.updateStatus = function () {
        var text = "Pending requests</br>" + gGlobals.loader.finishedTasks + "/" + gGlobals.loader.tasks;
        this.loaderText.html(text);
    }

    this.start = function () {
        this.updateStatus();
        this.loader.show();
        this.loaderText.show();
        this.task();
    }

    this.task = function () {
        var instance = this;
        setTimeout(function () {
            var tasks = gGlobals.loader.pendingTasks();
            if (tasks > 0) {
                instance.updateStatus();
                instance.task();
            } else {
                instance.loader.hide();
                instance.loaderText.hide();
            }
        }, 200);
    }
}