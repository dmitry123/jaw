var Jaw = Jaw || {};

(function(Jaw) {

    "use strict";

    /*
       ___ ___  __  __ __  __  ___  _  _
      / __/ _ \|  \/  |  \/  |/ _ \| \| |
     | (_| (_) | |\/| | |\/| | (_) | .` |
      \___\___/|_|  |_|_|  |_|\___/|_|\_|

     */

    Jaw.extend = function(child, parent) {
        var F = function() {};
        F.prototype = parent.prototype;
        child.prototype=  new F();
        child.prototype.constructor = child;
        child.superclass = parent.prototype;
    };

    /*
       ___ ___  __  __ ___  ___  _  _ ___ _  _ _____
      / __/ _ \|  \/  | _ \/ _ \| \| | __| \| |_   _|
     | (_| (_) | |\/| |  _/ (_) | .` | _|| .` | | |
      \___\___/|_|  |_|_|  \___/|_|\_|___|_|\_| |_|

     */

    var Component = function(properties, selector) {
        this._properties = properties || {};
        this._selector = selector || this.render();
    };

    Component.prototype.render = function() {
        throw new Error("Component/render() : Not-Implemented");
    };

    Component.prototype.activate = function() {
        throw new Error("Component/activate() : Not-Implemented");
    };

    Component.prototype.selector = function(selector) {
        if (arguments.length > 0) {
            this._selector = selector;
        }
        return this._selector;
    };

    Component.prototype.property = function(key, value) {
        if (arguments.length > 1) {
            this._properties[key] = value;
        }
        return this._properties[key];
    };

    Component.prototype.destroy = function() {
        this.selector().remove();
    };

    Component.prototype.update = function() {
        var parent = this.selector().parent();
        this.selector().remove();
        this.selector(
            this.render()
        ).appendTo(parent);
        this.activate();
    };

    Jaw.Component = Component;

    /*
     __      _____ ___   ___ ___ _____
     \ \    / /_ _|   \ / __| __|_   _|
      \ \/\/ / | || |) | (_ | _|  | |
       \_/\_/ |___|___/ \___|___| |_|

     */

    var Widget = function(selector, properties) {
        Component.call(this, selector.append(this.construct()), properties);
    };

    Jaw.extend(Widget, Component);

    Widget.prototype.construct = function() {
        throw new Error("Widget/construct() : Not-Implemented");
    };

    Widget.prototype.render = function() {
        throw new Error("Widget/render() : Not-Implemented");
    };

    Widget.prototype.update = function() {
        this.selector().find(".jaw-component").empty()
            .append(this.render());
    };

    Jaw.Widget = Widget;

    /**
     * Create new component's instance and render to DOM
     * @param component - Component's instance
     * @param selector - Parent's selector
     */

    Jaw.create = function(component, selector) {
        $(selector).data("jaw", component).append(
            component.selector()
        );
        component.update();
    };

    String.prototype.endsWith = function(suffix) {
        return this.indexOf(suffix, this.length - suffix.length) !== -1;
    };

})(Jaw);