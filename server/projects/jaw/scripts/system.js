var RequestButton = {
    construct: function() {
        var button = $("#btn-request")
            .data("toggle", "popover")
            .data("trigger", "click")
            .data("placement", "bottom")
            .data("container", "body")
            .data("html", true)
            .data("content", $("#request-table").html())
            .attr("title", "Запросы");
        if (+button.children(".badge").html() > 0) {
            button.popover("hide");
        }
    }
};

var RequestModal = {
    show: function(id) {
        var modal = $("#modal-request-info");
        var body = modal.find(".modal-body");
        body.empty().append(
            $("<img>", {
                class: "col-md-offset-6",
                src: "/jaw/images/ajax-loader.gif"
            })
        ).append("<br>");
        var put = function(label, text) {
            var item = $("<div></div>").append(
                $("<div></div>", {
                    html: "<b>" + label + "</b>"
                })
            );
            if ($.isArray(text)) {
                for (var i in text) {
                    item.append($("<div></div>", {
                        text: text[i]
                    }));
                }
            } else {
                item.append($("<div></div>", {
                    text: text
                }));
            }
            body.append(item);
        };
        $.get("/jaw/request/getInfo", {
            id: id
        }, function(data) {
            var json = $.parseJSON(data);
            if (!json.status) {
                return Jaw.createMessage({
                    message: json.message
                });
            }
            body.empty();
            var request = json["request"];
            put("Компания", request["company.name"]);
            put("Сотрудник", [
                request["employee.surname"] + " " + request["employee.name"] + " " + request["employee.patronymic"],
                request["employee.join_date"]
            ]);
            put("Тип запроса", request["privilege.name"]);
            if (request["product.id"]) {
                put("Разработка", [
                    request["product.name"],
                    request["product.created"]
                ]);
            }
            put("Пользователь", [
                request["users.login"],
                request["users.email"]
            ]);
            put("Текс запроса", request["request.message"]);
        });
        this.id = id;
        modal.modal();
    },
    ok: function(id) {
        id = id || this.id;
        $.get("/jaw/request/accept", {
            id: id
        }, function(data) {
            var json = $.parseJSON(data);
            if (!json.status) {
                return Jaw.createMessage({
                    message: json.message
                });
            }
            RequestModal.cleanup(id);
        });
    },
    cancel: function(id) {
        id = id || this.id;
        $.get("/jaw/request/cancel", {
            id: id
        }, function(data) {
            var json = $.parseJSON(data);
            if (!json.status) {
                return Jaw.createMessage({
                    message: json.message
                });
            }
            RequestModal.cleanup(id);
        });
    },
    cleanup: function(id) {
        var btn = $("#btn-request");
        var request = $(".popover a#request-" + id);
        request.parents("tr").remove();
        var val = +btn.find("span.badge").text();
        btn.find("span.badge").text(val - 1);
        if (val <= 1) {
            btn.popover("destroy");
        }
    },
    construct: function() {
        $("body")
            .on("click", ".request", function() {
              var id = $(this).attr("id");
                RequestModal.show(
                    +id.substr(id.lastIndexOf("-") + 1)
                );
            })
            .on("click", ".request-ok", function() {
                var id = $(this).attr("id");
                RequestModal.ok(
                    +id.substr(id.lastIndexOf("-") + 1)
                );
            })
            .on("click", ".request-cancel", function() {
                var id = $(this).attr("id");
                RequestModal.cancel(
                    +id.substr(id.lastIndexOf("-") + 1)
                );
            });
    },
    id: 0
};

$(document).ready(function() {
    RequestButton.construct();
    RequestModal.construct();
});