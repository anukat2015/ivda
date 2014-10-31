/**
 * Created by Seky on 7. 10. 2014.
 */
function Preloader() {
    this.loader = $('#loader');
    this.loaderText = $('#loader-text');
    this.tasks = 0;
    this.finishedTasks = 0;

    this.loader.hide();
    this.loaderText.hide();

    this.updateStatus = function () {
        var text = "Pending requests</br>" + this.finishedTasks + "/" + this.tasks;
        this.loaderText.html(text);
    };

    this.start = function () {
        if (this.pendingTasks() == 0) {
            return;
        }
        this.updateStatus();
        this.loader.show();
        this.loaderText.show();
        this.task();
    };

    this.task = function () {
        var instance = this;
        setTimeout(function () {
            var tasks = instance.pendingTasks();
            if (tasks > 0) {
                instance.updateStatus();
                instance.task();
            } else {
                instance.loader.hide();
                instance.loaderText.hide();
            }
        }, 200);
    };

    this.pendingTasks = function () {
        return this.loader.tasks - this.loader.finishedTasks;
    };
}
