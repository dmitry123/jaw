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

var LogoutButton = {
    construct: function() {
        $(".logout").click(function() {
            $.get("/jaw/user/logout", {}, function(json) {
                if (!json.status) {
                    return Jaw.createMessage({
                        message: json.message
                    });
                }
                window.location.href = "/jaw";
            }, "json");
        });
    }
};

var DarkInterface = {
    update: function() {
        if (!$(".navbar").hasClass("navbar-inverse")) {
            $(".dark-interface").parent().removeClass("active");
        } else {
            $(".dark-interface").parent().addClass("active");
        }
    },
    construct: function() {
        $(".dark-interface").click(function() {
            var menu = $(".navbar");
            if (menu.hasClass("navbar-inverse")) {
                menu.removeClass("navbar-inverse");
            } else {
                menu.addClass("navbar-inverse");
            }
            DarkInterface.update();
        });
        this.update();
    }
};

var EmployeeLogout = {
    construct: function() {
        $(".employee-logout").click(function() {
            $.get("/jaw/employee/logout", {}, function(json) {
                if (!json.status) {
                    return Jaw.createMessage({
                        message: json.message
                    });
                }
                window.location.href = "/jaw/index/project";
            }, "json");
        });
    }
};

$(document).ready(function() {
    //BackgroundParallax.construct();
    LogoutButton.construct();
    EmployeeLogout.construct();
    DarkInterface.construct();
});