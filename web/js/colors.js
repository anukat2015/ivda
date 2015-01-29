/**
 * Created by Seky on 29. 1. 2015.
 */


var COLORS_TYPE = {
    BLUE: "#3366cc",
    RED: "#dc3912",
    ORANGE: "#ff9900",
    GREEN: "#109618",
    FIALOVA: "#990099",
    SLABO_MODRA: "#0099c6"
};

var CATALOG = {
    Processes: {
        DEFAULT: COLORS_TYPE.BLUE,
        BANNED: COLORS_TYPE.RED,
        COMMUNICATION: COLORS_TYPE.GREEN,
        TYPICAL: COLORS_TYPE.FIALOVA,
        NODEVELOPER: COLORS_TYPE.RED,
        MUSIC: COLORS_TYPE.SLABO_MODRA
    },
    Web: {
        DEFAULT: COLORS_TYPE.BLUE,
        WORK: COLORS_TYPE.GREEN,
        PERSONAL: COLORS_TYPE.RED,
        UNKNOWN: COLORS_TYPE.SLABO_MODRA
    }
}

function CatalogGetColor(type, name) {
    var part = CATALOG[type];
    if( part.hasOwnProperty(name) ) {
        return part[name];
    }
    return return part["DEFAULT"];
}

