var Jaw = Jaw || {};

(function(Jaw) {

    "use strict";

    /**
     * Construct message as component to render
     * Properties: {
     *  delay {int} - Close timeout
     *  open {function} - Event on after open
     *  close {function} - Event on after close
     *  type {string} - Bootstrap type (danger, warning, info, default, success, primary)
     *  message {string} - Message to display
     * }
     * @param properties {{}} - Properties
     * @constructor
     */
    var Message = function(properties) {
        Jaw.Component.call(this, properties);
    };

    Jaw.extend(Message, Jaw.Component);

    Message.prototype.render = function() {
        var type = "alert-" + (this.property("type"));
        return $("<div></div>", {
            class: "alert " + type + " jaw-message-wrapper",
            role: "alert"
        }).append(
            $("<span></span>", {
                class: "glyphicon glyphicon-exclamation-sign"
            })
        ).append(
            $("<span></span>", {
                class: "jaw-message",
                text: this.property("message") || "Message Not Initialized"
            })
        );
    };

    Message.prototype.activate = function() {
        var me = this;
        this.selector().click(function() {
            me.destroy();
        }).css("left", (-this.selector().width() * 2) + "px");
        this.open();
    };

    Message.prototype.open = function(after) {
        var me = this;
        if (parseInt(this.selector().css("left")) < 0) {
            this.selector().animate({
                "left": "5px"
            }, "slow", null, function() {
                if (me.property("open")) {
                    me.property("open")(me);
                }
                if (after) {
                    after(me);
                }
            });
            setTimeout(function() {
                me.close();
            }, this.property("delay") || 2000);
        }
    };

    Message.prototype.close = function(after) {
        var me = this;
        if (parseInt(this.selector().css("left")) > 0) {
            this.selector().animate({
                "left": "-" + parseInt(this.selector().css("width")) + "px"
            }, "slow", null, function() {
                if (me.property("close")) {
                    me.property("close")(me);
                }
                if (after) {
                    after(me);
                }
                Collection.destroy(me);
            });
        }
    };

    Message.prototype.destroy = function() {
        this.close(function(me) {
            Jaw.Component.prototype.destroy.call(me);
        });
    };

    var Collection = {
        create: function(properties) {
            var message = new Message(properties);
            Jaw.create(message, document.body);
            var last = null;
            if (this._components.length > 0) {
                last = this._components[this._components.length - 1];
            }
            message.selector().css("top", (
                last ? parseInt(message.selector().css("top")) + 2 * (last.selector().height() + 10) * this._components.length : 5
            ) + "px");
            this._components.push(message);
        },
        destroy: function(component) {
            for (var i in this._components) {
                if (this._components[i] == component) {
                    this._components.splice(i, 1);
                    break;
                }
            }
        },
        _components: []
    };

    Jaw.createMessage = function(properties) {
        Collection.create(properties);
    };

    $(document).ready(function() {
        Jaw.createMessage({
            message: "Hello, World 1",
            type: "info"
        });
        setTimeout(function() {
            Jaw.createMessage({
                message: "Hello, World 2",
                type: "danger"
            });
        }, 250);
        setTimeout(function() {
            Jaw.createMessage({
                message: "Hello, World 3",
                type: "success"
            });
        }, 500);
        setTimeout(function() {
            Jaw.createMessage({
                message: "Hello, World 1",
                type: "info"
            });
            setTimeout(function() {
                Jaw.createMessage({
                    message: "Hello, World 2",
                    type: "danger"
                });
            }, 250);
            setTimeout(function() {
                Jaw.createMessage({
                    message: "Hello, World 3",
                    type: "success"
                });
            }, 500);
        }, 3500);
    });

})(Jaw);