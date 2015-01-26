var Jaw = Jaw || {};

(function(Jaw) {

    "use strict";

    var Form = function(properties, selector) {
        var me = this;
        Jaw.Component.call(this, properties, {}, selector);
        if (this.modal()) {
            this.modal().on("show.bs.modal", function() {
                me.reset();
            });
            this.modal().find(".btn-primary").click(function() {
                me.submit();
            });
        }
    };

    Jaw.extend(Form, Jaw.Component);

    Form.prototype.render = function() {
        /* That method will be never raised, i hope */
    };

    Form.prototype.before = function() {
        this.selector().find(".form-group")
            .css("opacity", "0.25");
        this.selector().append(
            $("<img>", {
                src: "/jaw/images/ajax-loader.gif",
                style: "position: absolute; left: 50%; top: 45%"
            })
        );
        if (this.modal()) {
            this.modal().find(".btn").prop("disabled", true);
        }
    };

    Form.prototype.after = function() {
        this.selector().find(".form-group")
            .css("opacity", "1");
        this.selector().find("img").remove();
        if (this.modal()) {
            this.modal().find(".btn").prop("disabled", false);
        }
    };

    Form.prototype.update = function() {
        var me = this;
        if (!this.property("url").length) {
            return Jaw.createMessage({
                message: "Невозможно обновить форму, т.к. отсутсвует URL для обновления"
            });
        }
        this.before();
        var url = this.property("url");
        $.get(url.substring(0, url.lastIndexOf("/") + 1) + "getForm", {
            form: this.property("form"),
            model: this.selector().serialize(),
            id: this.property("id")
        }, function(json) {
            if (!json.status) {
                return Jaw.createMessage({
                    message: json.message
                });
            }
            var s = $(json.component);
            me.selector().replaceWith(s);
            for (var i in json.model) {
                if (s.find("#" + i).val(json.model[i]) == null) {
                    s.find("#" + i).val(-1);
                }
            }
            me.selector(s);
            me.after();
        }, "json");
    };

    Form.prototype.reset = function() {
        this.selector().find(".form-group input, .form-group textarea").val("");
        this.selector().find(".form-group select").val("-1");
    };

    Form.prototype.submit = function() {
        var me = this;
        if (!this.property("url").length) {
            return Jaw.createMessage({
                message: "Невозможно отправить форму, т.к. отсутсвует URL для обновления"
            });
        }
        this.selector().find(".form-group").removeClass("has-error");
        this.before();
        $.post(this.property("url"), {
            form: this.property("form"),
            model: this.selector().serialize(),
            id: this.property("id")
        }, function(json) {
            if (!json.status) {
                if (!json["validation"]) {
                    for (var i in json["errors"]) {
                        me.selector().find("#" + json["errors"][i]["id"]).parents(".form-group")
                            .addClass("has-error");
                    }
                }
                return Jaw.createMessage({
                    message: json.message
                });
            }
            return Jaw.createMessage({
                message: json.message,
                type: "success",
                sign: "ok"
            });
        }, "json");
        this.after();
    };

    Form.prototype.modal = function() {
        var modals = this.selector().parents(".modal");
        if (!modals.length) {
            return null;
        }
        return $(modals[0]);
    };

    /**
     * Create new form and render to DOM
     * @param selector {HTMLElement|string} - jQuery's selector
     * @param properties {{}} - Component's properties
     */
    Jaw.createForm = function(selector, properties) {
        return Jaw.create(new Form(properties, $(selector)), selector, true);
    };

    $(document).ready(function() {
        $(".jaw-form").each(function(i, item) {
            Jaw.createForm(item, {
                url: $(item).attr("action"),
                form: $(item).data("form"),
                id: $(item).attr("id")
            });
            var modal = $(item).parents(".modal");
            if (!modal.length) {
                return void(0);
            }
            var refresh = modal.find(".refresh");
            if (!refresh.length) {
                return void(0);
            }
            refresh.removeClass("hidden").click(function() {
                $(this).parents(".modal").find(".jaw-form").data("jaw").update();
            });
        });
    });

})(Jaw);