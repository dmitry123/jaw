var Jaw = Jaw || {};

(function(Jaw) {

    "use strict";

    var Body = function(form) {
        Jaw.Sub.call(this, form);
    };

    Jaw.extend(Body, Jaw.Sub);

    Body.prototype.render = function() {
        var body = $("<div></div>", {
            class: "modal-body"
        }).append(
            $("<div></div>", {
                class: "row"
            })
        );
        var fields = this.property("fields");
        for (var i in fields) {
            if (!fields[i].id) {
                continue;
            }
            var input;
            var type = fields[i].type || "text";
            if (type == "textarea") {
                input = $("<textarea></textarea>", {
                    class: "form-control"
                }).css("max-width", "100%");
            } else if (type == "dropdown") {
                input = $("<select></select>", {
                    class: "form-control"
                }).append(
                    $("<option></option>", {
                        value: -1,
                        text: "Нет"
                    })
                );
                var value = fields[i].value || [];
                for (var j in value) {
                    input.append(
                        $("<option></option>", {
                            value: value[j],
                            text: value[j]
                        })
                    );
                }
            } else {
                input = $("<input>", {
                    class: "form-control " + fields[i].class || "",
                    type: type,
                    placeholder: fields[i].name || fields[i].id
                });
            }
            var required = "required";
            if (input.hasClass("required")) {
                required = "required";
            }
            $("<div></div>", {
                class: "form-group " + required
            }).append(
                $("<label></label>", {
                    class: "col-md-offset-1 col-md-3 control-label",
                    text: fields[i].name || fields[i].id
                })
            ).append(
                $("<div></div>", {
                    class: "col-md-7"
                }).append(input)
            ).append(
                "<br><br>"
            ).appendTo(body.find(".row"));
        }
        return body;
    };

    var Footer = function(form) {
        Jaw.Sub.call(this, form);
    };

    Jaw.extend(Footer, Jaw.Sub);

    Footer.prototype.render = function() {
        var footer = $("<div></div>", {
            class: "modal-footer"
        }).append(
            $("<button></button>", {
                type: "button",
                class: "btn btn-default",
                text: "Закрыть"
            }).attr("data-dismiss", "modal")
        );
        var buttons = this.property("buttons");
        for (var i in buttons) {
            footer.append(
                $("<button></button>", {
                    id: buttons[i].id || "",
                    class: buttons[i].class || "btn btn-default",
                    text: buttons[i].text || ""
                })
            );
        }
        return footer;
    };

    var Form = function(properties) {
        // Invoke super constructor
        Jaw.Component.call(this, properties, {
            title: "[Jaw/Form/Title]"
        }, true);
        // Initialize sub-components
        this._body = new Body(this);
        this._footer = new Footer(this);
        // Render component
        this.selector(this.render());
    };

    Jaw.extend(Form, Jaw.Component);

    /**
     * Render form component
     */
    Form.prototype.render = function() {
        var header = $("<div></div>", {
            class: "modal-header"
        }).append(
            $("<button></button>", {
                type: "button",
                class: "close",
                html: "&times;"
            }).data("dismiss", "modal")
                .attr("aria-hidden", "true")
        ).append(
            $("<h4></h4>", {
                class: "modal-title",
                html: this.property("title")
            })
        );
        return $("<div></div>", {
            class: "modal fade",
            id: this.property("id")
        }).append(
            $("<div></div>", {
                class: "modal-dialog modal-content"
            }).append(header).append(
                this._body.selector(this._body.render())
            ).append(
                this._footer.selector(this._footer.render())
            )
        );
    };

    Form.prototype.update = function() {
        var me = this;
        // Don't update without URL or form
        if (!this.property("url") || !this.property("form")) {
            return false;
        }
        // Replace selector with loading image
        this._body.selector().empty().append(
            $("<div></div>", {
                style: "width: 100%; text-align: center;"
            }).append(
                $("<img>", {
                    src: "/jaw/images/ajax-loader.gif"
                })
            )
        );
        $.get(this.property("url"), {
            form: this.property("form")
        }, function(json) {
            if (!json.status) {
                return Jaw.createMessage({
                    message: json.message
                });
            }
            console.log(json);
        }, "json");
    };

    /**
     * Create new form and render to DOM
     * @param selector {HTMLElement|string} - jQuery's selector
     * @param properties {{}} - Component's properties
     */
    Jaw.createForm = function(selector, properties) {
        return Jaw.create(new Form(properties), selector);
    };

})(Jaw);