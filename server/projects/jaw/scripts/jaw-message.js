var Jaw = Jaw || {};

(function(Jaw) {

    "use strict";

    /**
     * Construct message as component to render
     * Properties: {
     *  delay {int} - Close timeout
     *  open {function} - Event on after open
     *  close {function} - Event on after close
     *  type {string} - Bootstrap type (danger, warning, info, success)
     *  message {string} - Message to display,
     *  sign {string} - Bootstrap sign (ok, question, info, exclamation, warning, plus, minus, remove)
     * }
     * @param properties {{}} - Properties
     * @constructor
     */
    var Message = function(properties) {
        Jaw.Component.call(this, properties, {
            type: "danger",
            message: "Not-Initialized",
            sign: "info",
            delay: 5000
        });
    };

    Jaw.extend(Message, Jaw.Component);

    /**
     * Render message component
     * @returns {jQuery}
     */
    Message.prototype.render = function() {
        return $("<div></div>", {
            class: "alert " + ("alert-" + this.property("type")) + " jaw-message-wrapper",
            role: "alert"
        }).append(
            $("<span></span>", {
                class: "glyphicon glyphicon-" + this.property("sign") + "-sign",
                style: "margin-right: 10px"
            })
        ).append(
            $("<span></span>", {
                class: "jaw-message",
                text: this.property("message")
            })
        );
    };

    /**
     * Activate message component, it will add click event
     * and animate message opening from left edge
     */
    Message.prototype.activate = function() {
        var me = this;
        this.selector().click(function() {
            me.destroy();
        }).css("left", (-this.selector().width() * 2) + "px");
        this.open();
    };

    /**
     * Open message (animate from left edge)
     * @param [after] {function|null|undefined} - Callback after open
     */
    Message.prototype.open = function(after) {
        var me = this;
        if (parseInt(this.selector().css("left")) < 0) {
            this.selector().animate({
                "left": "5px"
            }, "slow", null, function() {
                if (me.property("open")) {
                    me.property("open").call(me);
                }
                if (after) {
                    after(me);
                }
            });
            setTimeout(function() {
                me.close();
            }, this.property("delay"));
        }
    };

    /**
     * Close message component, if it hasn't been opened yet
     * @param [after] {function|null|undefined} - Callback after close
     */
    Message.prototype.close = function(after) {
        var me = this;
        if (parseInt(this.selector().css("left")) > 0) {
            this.selector().animate({
                "left": "-" + parseInt(this.selector().css("width")) + "px"
            }, "slow", null, function() {
                if (me.property("close")) {
                    me.property("close").call(me);
                }
                if (after) {
                    after(me);
                }
                Collection.destroy(me);
            });
        }
    };

    /**
     * Overridden destroy method, it will close current component (move
     * to left edge) and invoke super destroy method
     */
    Message.prototype.destroy = function() {
        this.close(function(me) {
            Jaw.Component.prototype.destroy.call(me);
        });
    };

    /**
     * Collection is a singleton, which stores active messages and
     * will put new message after previous (with new top offset)
     * @type {{create: Function, destroy: Function, _components: Array}}
     */
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

    /**
     * Create new message instance with some properties
     * @param properties {{}} - Message component's properties
     */
    Jaw.createMessage = function(properties) {
        Collection.create(properties);
    };

})(Jaw);