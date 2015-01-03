var BackgroundParallax = {
    construct: function() {
        var center = {
            x: $(window).width() / 2,
            y: $(window).height() / 2
        };
        var god = $(".god-button");
        var background = $(".background");
        var godCenter = {
            x: parseInt(god.css("left")),
            y: parseInt(god.css("top"))
        };
        $(document).mousemove(function(e) {
            var offset = {
                x: -(e.pageX - center.x) / 75,
                y: -(e.pageY - center.y) / 75
            };
            background.css("background-position", offset.x + "px " + offset.y + "px");
            god.css("left", godCenter.x + offset.x + "px")
                .css("top", godCenter.y + offset.y + "px");
        });
    }
};

$(document).ready(function() {
    //BackgroundParallax.construct();
});