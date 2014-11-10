/**
 * Created by Seky on 10. 11. 2014.
 */

Date.prototype.floor = function (chunkSize) {
    return ( Math.floor(this.getTime() / chunkSize) * chunkSize);
};

Date.prototype.ceil = function (chunkSize) {
    return ( Math.ceil(this.getTime() / chunkSize) * chunkSize);
};

