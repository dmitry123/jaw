
var Jaw = Jaw || {
    };

(function(Jaw) {

    "use strict";

    /*
      ___ _____ _ _____ ___
     / __|_   _/_\_   _| __|
     \__ \ | |/ _ \| | | _|
     |___/ |_/_/ \_\_| |___|

     */

    var State = function(machine, name, index) {
        this._machine = machine;
        this._name = name;
        this._index = index;
    };

    State.prototype.load = function() {
        throw new Error("State/load() - Not implemented");
    };

    State.prototype.unload = function() {
        throw new Error("State/unload() - Not implemented");
    };

    State.prototype.machine = function() {
        return this._machine;
    };

    State.prototype.name = function(name) {
        if (arguments.length > 0) {
            this._name = name;
        }
        return this._name;
    };

    State.prototype.index = function(index) {
        if (arguments.length > 0) {
            this._index = index;
        }
        return this._index;
    };

    /*
      __  __   _   ___ _  _ ___ _  _ ___
     |  \/  | /_\ / __| || |_ _| \| | __|
     | |\/| |/ _ \ (__| __ || || .` | _|
     |_|  |_/_/ \_\___|_||_|___|_|\_|___|

     */

    var Machine = function() {
        this._states = [];
    };

    Machine.prototype.add = function(state) {
        if (state in this._states) {
            throw new Error("Machine/add() - State already in that machine");
        }
        state.index(this._states.length);
        this._states.push(state);
    };

    Machine.prototype.drop = function(state) {
        if (!(state in this._states)) {
            throw new Error("Machine/add() - Unresolved state");
        }
    };

    /*
      _____ _   ___ _    ___
     |_   _/_\ | _ ) |  | __|
       | |/ _ \| _ \ |__| _|
       |_/_/ \_\___/____|___|

     */

    var Table = function(selector, properties) {
        this._widget = $(selector);
        this._properties = properties;
        this._selector = this.render();
    };

    Table.prototype.property = function(field, value) {
        if (arguments.length > 1) {
            this._properties[field] = value;
        }
        if (this._properties === undefined) {
            throw new Error("Table/property() : \"Undeclared field (" + field + ")\"");
        }
        return this._properties[field];
    };

    Table.prototype.widget = function(widget) {
        if (arguments.length > 0) {
            this._widget = widget;
        }
        return this._widget;
    };

    Table.prototype.has = function(field) {
        return this._properties[field] !== undefined;
    };

    Table.prototype.selector = function(selector) {
        if (arguments.length > 0) {
            this._selector = selector;
        }
        return this._selector;
    };

    Table.prototype.action = function() {
        var container = $("<div></div>", {
            style: "text-align: center"
        });
        var style = "cursor: pointer; padding-right: 5px; padding-left: 5px;";
        container.append(
            $("<span></span>", {
                class: "glyphicon glyphicon-pencil",
                style: style
            })
        );
        container.append(
            $("<span></span>", {
                class: "glyphicon glyphicon-remove",
                style: style
            })
        );
        return container;
    };

    Table.prototype.update = function() {
        var me = this;
        if (!this.has("url")) {
            return false;
        }
        this.widget().empty().append(
            $("<img>", {
                class: "col-md-offset-6",
                src: "/jaw/images/ajax-loader.gif"
            })
        );
        $.get(this.property("url"), {
            action: "fetch"
        }, function(data) {
            var json = $.parseJSON(data);
            if (!json.status) {
                return ErrorMessage.post(json.message);
            }
            me.property("data", json["table"]);
            me.widget().empty().append(
                me.selector(me.render())
            );
        });
    };

    Table.prototype.render = function() {
        var i;
        if (!this.has("header") || !this.has("url")) {
            return $("<div></div>", {
                html: "<b>Error: Table hasn't been initialized</b>",
                style: "font-size: 20px"
            });
        }
        var header = $("<tr></tr>", {
            style: "background-color: lightgray"
        });
        var head = this.property("header");
        for (i in head) {
            header.append($("<td></td>", {
                html: "<b>" + head[i].name + "</b>",
                style: head[i].name == "#" ?
                    "width: 30px; text-align: center;" : ""
            }));
        }
        header.append($("<td><b>Действия<b></td>", {
        }));
        var table = $("<table></table>", {
            class: "table table-striped table-bordered"
        }).append(
            $("<tbody></tbody>").append(header)
        );
        if (!this.has("data")) {
            return table;
        }
        for (var k in this.property("data")) {
            var body = $("<tr></tr>", {
                class: "default"
            });
            var data = this.property("data")[k];
            for (i in head) {
                var id = head[i].id;
                var c;
                if ($.isArray(id)) {
                    var separator = head[i].separator || ", ";
                    var html = "";
                    for (var j in id) {
                        html += data[id[j]] + (j != id.length - 1 ? separator : "");
                    }
                    c = $("<td></td>", {
                        html: html,
                        style: head[i].style
                    });
                } else {
                    c = $("<td></td>", {
                        html: data[id],
                        style: head[i].style
                    });
                }
                if (head[i].href) {
                    var href = head[i].href;
                    for (var d in data) {
                        href = href.replace(d, data[d]);
                    }
                    c = $("<button></button>", {
                        class: "btn btn-link",
                        href: href
                    }).append(c);
                }
                body.append(c);
            }
            body.append(
                $("<td></td>", {
                    style: "width: 100px;"
                }).append(
                    this.action()
                )
            );
            table.append(body);
        }
        return table;
    };

    /*
      ___ ___  ___  ___ ___ ___ _______   __        __  __   _   _  _   _   ___ ___ ___
     | _ \ _ \/ _ \| _ \ __| _ \_   _\ \ / /  ___  |  \/  | /_\ | \| | /_\ / __| __| _ \
     |  _/   / (_) |  _/ _||   / | |  \ V /  |___| | |\/| |/ _ \| .` |/ _ \ (_ | _||   /
     |_| |_|_\\___/|_| |___|_|_\ |_|   |_|         |_|  |_/_/ \_\_|\_/_/ \_\___|___|_|_\

     */

    var PropertyManager = function() {
        this._properties = [];
    };

    PropertyManager.prototype.put = function(field, value) {
        this._properties[field] = value;
    };

    PropertyManager.prototype.has = function(field) {
        return this._properties[field] != undefined;
    };

    PropertyManager.prototype.get = function(field) {
        return this._properties[field];
    };

    /*
      ___ ___ _  _  ___ _    ___ _____ ___  _  _
     / __|_ _| \| |/ __| |  | __|_   _/ _ \| \| |
     \__ \| || .` | (_ | |__| _|  | || (_) | .` |
     |___/___|_|\_|\___|____|___| |_| \___/|_|\_|

     */

    var propertyManager = new PropertyManager();
    var siteMachine = new Machine();

    /*
         _  ___      __
      _ | |/_\ \    / /
     | || / _ \ \/\/ /
     \__/_/ \_\_/\_/

     */

    Jaw.createTable = function(selector, properties) {
        var parent = $(selector);
        var table = new Table(parent, properties);
        parent.data("jaw-table", table).append(
            table.selector()
        );
        table.update();
    };

    Jaw.getPropertyManager = function() {
        return propertyManager;
    };

    Jaw.getMachine = function() {
        return siteMachine;
    };

    Jaw.setProperty = function(properties) {
        for (var k in properties) {
            propertyManager.put(k, properties[k]);
        }
    };

    Jaw.getProperty = function(path) {
        var _get = function(path, object) {
            if (path.indexOf(".") < 0) {
                return object ? object[path] || null : null;
            }
            if (typeof object != "object") {
                return object;
            }
            var i = path.indexOf(".");
            return object ? _get(path.substr(i + 1),
                object[path.substr(0, i)]
            ) || null : null;
        };
        return _get(path, propertyManager._properties);
    };

    Jaw.getBaseUrl = function() {
        return propertyManager.get("project-name");
    };

    Jaw.getController = function() {
        return propertyManager.get("controller");
    };

    Jaw.getAction = function() {
        return propertyManager.get("action");
    };

    Jaw.extend = function(child, parent) {
        var F = function() {};
        F.prototype = parent.prototype;
        child.prototype=  new F();
        child.prototype.constructor = child;
        child.superclass = parent.prototype;
    };

})(Jaw);