var Jaw = Jaw || {};

(function(Jaw) {

    "use strict";

    /**
     * Construct table header with table's properties
     * @param table {Table} - Parent table component
     * @constructor
     */
    var Header = function(table) {
        this.table = function() {
            return table;
        };
        Jaw.Component.call(this);
    };

    Jaw.extend(Header, Jaw.Component);

    /**
     * That method will invoke table's property method, cuz
     * header doesn't have property object
     * @param key {string} - Key
     * @param [value] {*|null|undefined} - Value
     * @returns {*} - New or old value
     */
    Header.prototype.property = function(key, value) {
        return this.table().property.apply(this.table(), arguments);
    };

    /**
     * Render table's header
     * @returns {jQuery} - jQuery selector with header
     */
    Header.prototype.render = function() {
        var me = this;
        var header = this.property("header");
        var tr = $("<tr></tr>", {
            style: "background-color: #ddd;"
        });
        for (var i in header) {
            var style = header[i].name == "#" ?
                "width: 30px;" +
                "text-align: center;" +
                "cursor: pointer;" : "cursor: pointer;";
            $("<td></td>", {
                html: "<b>" + header[i].name + "</b>",
                style: style
            }).click(function() {
                me.table().order($(this).data("id"));
                me.table().update();
            }).data("id", header[i].id)
                .appendTo(tr)
        }
        return tr.append(
            $("<td><b>Действия<b></td>")
        );
    };

    /**
     * Construct modal component for table
     * @param table {Table} - Table instance
     * @param modal {jQuery} - Modal selector
     * @constructor
     */
    var Modal = function(table, modal) {
        this.table = function() {
            return table;
        };
        Jaw.Component.call(this, {}, {}, modal);
    };

    Jaw.extend(Modal, Jaw.Component);

    /**
     * That method will invoke table's property method, cuz
     * header doesn't have property object
     * @param key {string} - Key
     * @param [value] {*|null|undefined} - Value
     * @returns {*} - New or old value
     */
    Modal.prototype.property = function(key, value) {
        return this.table().property.apply(this.table(), arguments);
    };

    /**
     * Render input button for modal row
     * @param heading {{}} - Current heading from header
     * @param row {{}} - Current row object
     * @param name {string} - Row's name
     * @param key {string} - Row's key
     * @returns {jQuery} - Rendered selector
     */
    Modal.prototype.renderInput = function(heading, row, name, key) {
        var temporary = {}, input, k;
        var success = function(json) {
            if (!json.status) {
                return Jaw.createMessage({
                    message: json.message
                });
            }
            for (var j in json.rows) {
                var display = json.display.split(
                    json.separator
                );
                var text = "";
                for (k in display) {
                    if (!display[k]) {
                        continue;
                    }
                    text += json.rows[j][display[k]];
                    if (k != display.length - 1) {
                        text += json.separator;
                    }
                }
                var value = -1;
                for (k in json.rows[j]) {
                    if (k.endsWith(".id")) {
                        value = json.rows[j][k];
                        break;
                    }
                }
                temporary[json.key].append(
                    $("<option></option>", {
                        text: text,
                        value: value
                    })
                ).val(
                    temporary[json.key].data("value") || -1
                );
            }
        };
        if (key != "id" && key.endsWith("_id")) {
            input = $("<select></select>", {
                class: "form-control",
                value: row,
                id: heading.id.replace(".", "_")
            }).append(
                $("<option></option>", {
                    text: "Нет",
                    value: -1
                })
            );
            input.data("value", row);
            temporary[key] = input;
            var serialized = "";
            var display = heading.display;
            var separator = heading.separator || ",";
            if ($.isArray(display)) {
                for (var j in display) {
                    serialized += display[j] + separator;
                }
            } else {
                serialized = display;
            }
            $.get(heading.url, {
                action: "all",
                key: key,
                display: serialized || key,
                separator: separator
            }, function(data) {
                success($.parseJSON(data));
            });
        } else {
            input = $("<input>", {
                type: heading.type || "text",
                placeholder: name,
                class: "form-control",
                id: heading.id.replace(".", "_"),
                value: heading.type != "password" ? row : ""
            });
        }
        return input;
    };

    /**
     * Render modal window form and display it
     * @param row {{}} - Current row
     * @param [identifiable] {bool} - Don't allow data change
     * @param [editable] {bool} - Can user edit fields
     */
    Modal.prototype.render = function(row, identifiable, editable) {
        var header = this.property("header");
        var head = function(id) {
            for (var i in header) {
                if (header[i].id == id) {
                    return header[i];
                }
            }
            return null;
        };
        var form = this.selector().find(".jaw-table-form");
        form.empty();
        var stack = [];
        for (var k in row) {
            var h = head(k);
            if (!h) {
                continue;
            }
            if (h.id.endsWith(".id") && !identifiable) {
                continue;
            }
            var name = h.name == "#" ? "Идентификатор" : h.name;
            if (h.type == "password") {
                continue;
            }
            var input = this.renderInput(h, row[k], name,
                k.substr(k.indexOf(".") + 1)
            );
            if (!editable || h.id.endsWith(".id")) {
                input.attr("disabled", "disabled");
            }
            var group = $("<div></div>", {
                class: "form-group required"
            }).append(
                $("<label></label>", {
                    class: "col-md-offset-1 col-md-4 control-label",
                    text: name
                })
            ).append(
                $("<div></div>", {
                    class: "col-md-6"
                }).append(input)
            ).append(
                $("<br>")
            );
            stack.push(group);
        }
        for (var i in stack) {
            form.append(stack[stack.length - i - 1]).append($("<br>"));
        }
        this.selector().modal();
    };

    /**
     * Construct reference modal window with information
     * about current row
     * @param table {Table} - Component's table
     * @param modal {jQuery} - Modal selector
     * @constructor
     */
    var Reference = function(table, modal) {
        this.table = function() {
            return table;
        };
        Jaw.Component.call(this, {}, {}, modal);
    };

    Jaw.extend(Reference, Jaw.Component);

    /**
     * That method will invoke table's property method, cuz
     * header doesn't have property object
     * @param key {string} - Key
     * @param [value] {*|null|undefined} - Value
     * @returns {*} - New or old value
     */
    Reference.prototype.property = function(key, value) {
        return this.table().property.apply(this.table(), arguments);
    };

    /**
     * Render references for reference modal window
     * @param id {String} - Row's identifier
     * @param data {{}} - Row's data
     * @returns {jQuery} - Rendered selector
     */
    Reference.prototype.renderReferences = function(id, data) {
        var name = id;
        var property;
        for (var k in this.property("reference")) {
            property = this.property("reference")[k];
            if (property.id == id) {
                name = property.name || id;
                break;
            }
        }
        var selector = $("<div></div>", {
        }).append(
            $("<div></div>", {
                html: "<b>" + name + "</b>"
            })
        );
        var block = $("<div></div>", {
        });
        for (var i in data) {
            var text = "";
            for (var j in property["display"]) {
                text += data[i][property["display"][j]];
                if (j != property["display"].length - 1) {
                    text += property["separator"];
                }
            }
            block.append(
                $("<div></div>", {
                    text: text
                })
            );
        }
        return selector.append(block);
    };

    /**
     * Render elements references modal window
     */
    Reference.prototype.render = function(row) {
        var k = this.property("table") + ".id";
        var id = row[k];
        if (!id) {
            throw new Error("Table/buildReference() : \"Can't find key for row\"");
        }
        this.selector().find(".jaw-table-form").empty().append(
            $("<div></div>", {
                style: "width: 100%; text-align: center;"
            }).append(
                $("<img>", {
                    src: "/jaw/images/ajax-loader.gif"
                })
            )
        );
        this.selector().modal();
        var me = this;
        $.get(this.property("url"), {
            action: "reference",
            id: id,
            alias: k
        }, function (data) {
            var json = $.parseJSON(data);
            if (!json.status) {
                return Jaw.createMessage({
                    message: json.message
                });
            }
            var references = json["reference"];
            var has = false;
            var s = me.selector().find(".jaw-table-form").empty();
            for (var k in references) {
                if (references[k].length) {
                    has = true;
                }
                s.append(
                    me.renderReferences(
                        k, references[k]
                    )
                );
            }
            if (!has) {
                me.selector().find(".jaw-table-form").empty().append(
                    $("<div></div>", {
                        style: "text-align: center; font-size: 20px",
                        html: "<b>Данный элемент не имеет зависимостей</b>"
                    })
                );
            }
        });
    };

    /**
     * construct table body with table's properties
     * @param table {Table} - Parent table component
     * @constructor
     */
    var Body = function(table) {
        var me = this;
        this.table = function() {
            return table;
        };
        Jaw.Component.call(this);
        // Create component for edit modal window
        this._modalEdit = new Modal(this.table(),
            $("#modal-jaw-table-edit")
        );
        this._modalEdit.selector().find("#jaw-table-save").click(function() {
            var attributes = {};
            var form = me._modalEdit.selector().find(".jaw-table-form");
            for (var h in me.property("header")) {
                var heading = me.property("header")[h];
                var s = form.find("#" + heading.id.replace(".", "_"));
                if (!s.val().length) {
                    s.parents(".form-group").addClass("has-error");
                    return true;
                }
                attributes[heading.id.substr(heading.id.indexOf(".") + 1)] = s.val();
            }
            var button = $(this).button("loading");
            $.get(me.property("url") + "?action=update", attributes, function(data) {
                var json = $.parseJSON(data);
                if (!json.status) {
                    button.button("reset");
                    return Jaw.createMessage({
                        message: json.message
                    });
                }
                button.button("reset");
                me._modalEdit.selector().modal("hide");
                me.table().update();
            });
        });
        // Create component for delete modal window
        this._modalDelete = new Modal(this.table(),
            $("#modal-jaw-table-delete")
        );
        this._modalDelete.selector().find("#jaw-table-delete").click(function() {
            var button = $(this).button("loading");
            $.get(me.property("url") + "?action=delete", {
                id: me.property("active").data("row")[me.property("table") + ".id"]
            }, function(data) {
                var json = $.parseJSON(data);
                if (!json.status) {
                    button.button("reset");
                    return Jaw.createMessage({
                        message: json.message
                    });
                }
                button.button("reset");
                me._modalDelete.selector().modal("hide");
                me.table().update();
            });
        });
        // Create component for references modal window
        this._modalReference = new Reference(this.table(),
            $("#modal-jaw-table-reference")
        );
    };

    Jaw.extend(Body, Jaw.Component);

    /**
     * That method will invoke table's property method, cuz
     * header doesn't have property object
     * @param key {string} - Key
     * @param [value] {*|null|undefined} - Value
     * @returns {*} - New or old value
     */
    Body.prototype.property = function(key, value) {
        return this.table().property.apply(this.table(), arguments);
    };

    /**
     * Render actions for each row
     * @param row {{}} - Current row
     * @returns {jQuery}
     */
    Body.prototype.renderAction = function(row) {
        var me = this;
        var container = $("<div></div>", {
            style: "text-align: center"
        });
        var style = "cursor: pointer; padding-right: 5px; padding-left: 5px;";
        container.append(
            $("<span></span>", {
                class: "glyphicon glyphicon-link",
                style: style
            }).click(function() {
                me._modalReference.render(row);
            })
        );
        container.append(
            $("<span></span>", {
                class: "glyphicon glyphicon-pencil",
                style: style
            }).click(function() {
                me._modalEdit.render(row, true, true);
            })
        );
        container.append(
            $("<span></span>", {
                class: "glyphicon glyphicon-remove",
                style: style
            }).click(function() {
                me._modalDelete.render(row, true, false);
            })
        );
        return container;
    };

    /**
     * Render table's body
     * @returns {jQuery|Array}
     */
    Body.prototype.render = function() {
        var me = this;
        var header = this.property("header");
        var result = [];
        for (var k in this.property("data")) {
            var body = $("<tr></tr>", {
                class: "default unselectable body"
            });
            var global = "id";
            var data = this.property("data")[k];
            body.data("row", data).dblclick(function() {
                me._modalReference.render(
                    $(this).data("row")
                );
            });
            for (var i in header) {
                var id = header[i].display || header[i].id;
                var c;
                if ($.isArray(id)) {
                    var separator = header[i].separator || ", ";
                    var html = "";
                    for (var j in id) {
                        html += data[id[j]] + (j != id.length - 1 ? separator : "");
                        if (id[j].endsWith(".id")) {
                            global = id[j];
                        }
                    }
                    c = $("<td></td>", {
                        html: html,
                        style: header[i].style
                    });
                } else {
                    c = $("<td></td>", {
                        html: data[id],
                        style: header[i].style
                    });
                    if (id.endsWith(".id")) {
                        global = id;
                    }
                }
                if (header[i].href) {
                    var href = header[i].href;
                    for (var d in data) {
                        href = href.replace(d, data[d]);
                    }
                }
                body.append(c);
            }
            body.append(
                $("<td></td>", {
                    style: "width: 100px;"
                }).append(
                    this.renderAction(data)
                )
            ).click(function() {
                    history.pushState(null, null, window.location.hash +
                        "?id=" + $(this).data("id")
                    );
                    if (me.property("active")) {
                        me.property("active").removeClass("info");
                    }
                    me.property("active", $(this).addClass("info"));
                }
            ).data("id", data[global]);
            result.push(body);
        }
        if (!this.property("data") || !this.property("data").length) {
            return $("<tr></tr>", {
                class: "default"
            }).append(
                $("<td></td>", {
                    colspan: header.length + 1,
                    style: "font-size: 20px; text-align: center;",
                    html: "<b>Отсутсвуют данные для отображения</b>"
                })
            );
        }
        return result;
    };

    /**
     * Construct table footer with parent table component
     * @param table {Table} - Parent table component
     * @constructor
     */
    var Footer = function(table) {
        var me = this;
        this.table = function() {
            return table;
        };
        Jaw.Component.call(this);
        this._modalInsert = new Modal(this.table(),
            $("#modal-jaw-table-add")
        );
        this._modalInsert.selector().find("#jaw-table-add").click(function() {
            var head = me.property("header");
            var attributes = {};
            var form = me._modalInsert.selector().find(".jaw-table-form");
            var errors = false;
            for (var h in head) {
                var s = form.find("#" + head[h].id.replace(".", "_"));
                if (!s.length) {
                    continue;
                }
                if (!s.val().length) {
                    s.parents(".form-group").addClass("has-error");
                    errors = true;
                }
                attributes[head[h].id.substr(head[h].id.indexOf(".") + 1)] = s.val();
            }
            if (errors) {
                return true;
            }
            var button = $(this).button("loading");
            $.get(me.property("url"), $.extend(attributes, {
                action: "add"
            }), function(data) {
                var json = $.parseJSON(data);
                if (!json.status) {
                    button.button("reset");
                    return Jaw.createMessage({
                        message: json.message
                    });
                }
                button.button("reset");
                me._modalInsert.selector().modal("hide");
                me.table().update();
            });
        });
    };

    Jaw.extend(Footer, Jaw.Component);

    /**
     * That method will invoke table's property method, cuz
     * header doesn't have property object
     * @param key {string} - Key
     * @param [value] {*|null|undefined} - Value
     * @returns {*} - New or old value
     */
    Footer.prototype.property = function(key, value) {
        return this.table().property.apply(this.table(), arguments);
    };

    /**
     * Render container for queries based on where clause
     * @returns {jQuery}
     */
    Footer.prototype.renderQuery = function() {
        var me = this;
        var input = $("<input>", {
            type: "text",
            placeholder: "Условие поиска",
            class: "form-control",
            id: "query",
            style: "width: 350px; float: left;",
            value: this.table().property("where")
        }).keydown(function(e) {
            if (e.keyCode == 13) {
                $(this).parent().find("button").trigger("click");
            }
        });
        var button = $("<button></button>", {
            text: "Отправить",
            class: "btn btn-primary",
            style: "margin-left: 10px;"
        }).click(function() {
            var value = me.selector().find("#query").val();
            if (value) {
                me.property("where", value);
            } else {
                me.property("where", null);
            }
            me.update();
        });
        return $("<td></td>", {
            style: "width: 550px",
            class: "query"
        }).append(input).append(button)
    };

    /**
     * Render select item for limit
     * @returns {jQuery}
     */
    Footer.prototype.renderLimit = function() {
        var me = this;
        var select = $("<select></select>", {
            class: "form-control",
            style: "width: auto"
        }).change(function() {
            me.table().limit(+$(this).val());
            me.table().update();
        });
        for (var i in this.property("limit")) {
            select.append($("<option></option>", {
                text: this.property("limit")[i],
                value: this.property("limit")[i]
            }));
        }
        select.val(me.table().limit());
        return $("<td></td>", {
            style: "width: auto",
            class: "select"
        }).append(select)
    };

    /**
     * Render container with pager, which will show current page
     * and will allow user to set necessary page
     * @returns {jQuery}
     */
    Footer.prototype.renderPager = function() {
        var me = this;
        return $("<td></td>", {
            style: "width: 200px; vertical-align: middle",
            valign: "middle",
            class: "pager"
        }).append(
            $("<span></span>", {
                style: "float: left; line-height: 30px; margin-right: 5px; cursor: pointer",
                class: "glyphicon glyphicon-chevron-left"
            }).click(function() {
                var value = +$(this).parent().children(".page").val().split("/")[0];
                if (value <= 1) {
                    return true;
                }
                $(this).parent().children(".page").val(
                    (value - 1) + "/" + me.property("pages")
                );
                me.property("page", value - 1);
                me.table().update();
            })
        ).append(
            $("<input>", {
                style: "width: 60px; float: left; text-align: center",
                type: "text",
                class: "form-control page",
                disabled: "disabled",
                value: "0/0"
            })
        ).append(
            $("<span></span>", {
                style: "float: left; line-height: 30px; margin-left: 5px; cursor: pointer",
                class: "glyphicon glyphicon-chevron-right"
            }).click(function() {
                var value = +$(this).parent().children(".page").val().split("/")[0];
                if (me.property("pages") && value >= +me.property("pages")) {
                    return true;
                }
                $(this).parent().children(".page").val(
                    (value + 1) + "/" + me.property("pages")
                );
                me.property("page", value + 1);
                me.table().update();
            })
        )
    };

    /**
     * Render adder button to insert new items in table
     * @returns {jQuery}
     */
    Footer.prototype.renderAdder = function() {
        var me = this;
        return $("<td></td>", {
            style: "text-align: center; width: 30px;",
            class: "adder"
        }).append(
            $("<span></span>", {
                class: "glyphicon glyphicon-plus add",
                style: "font-size: 20px; line-height: 25px; cursor: pointer;"
            }).click(function() {
                var row = {};
                for (var k in me.property("header")) {
                    row[me.property("header")[k].id] = "";
                }
                me._modalInsert.render(row, false, true);
            })
        )
    };

    /**
     * Render refresh button, which will resend request on
     * server to update current data
     * @returns {*|jQuery}
     */
    Footer.prototype.renderRefresher = function() {
        var me = this;
        return $("<td></td>", {
            style: "text-align: right; width: 30px;",
            class: "refresher"
        }).append(
            $("<span></span>", {
                class: "glyphicon glyphicon-refresh refresh",
                style: "font-size: 20px; line-height: 25px; cursor: pointer;"
            }).click(function() {
                me.update();
            })
        );
    };

    /**
     * Render table's footer with all upper stuff
     * @returns {jQuery}
     */
    Footer.prototype.render = function() {
        var header = this.property("header");
        var tr = $("<tr></tr>", {
            colspan: header.length + 1
        }).append(
            this.renderQuery()
        ).append(
            this.renderPager()
        ).append(
            this.renderLimit()
        ).append(
            this.renderAdder()
        ).append(
            this.renderRefresher()
        );
        return $("<div></div>", {
            class: "panel-footer"
        }).append(
            $("<table></table>", {
                style: "width: 100%"
            }).append(tr)
        );
    };

    /**
     * Construct table component with properties
     * @param properties
     * @constructor
     */
    var Table = function(properties) {
        // Construct super component
        Jaw.Component.call(this, properties, {
            limit: [5, 10, 25, 50, 100],
            page: 1
        }, true);
        // Construct children components
        this._header = new Header(this);
        this._body = new Body(this);
        this._footer = new Footer(this);
        // Render table component
        this.selector(this.render());
    };

    Jaw.extend(Table, Jaw.Component);

    /**
     * Set table's order method, if key will be repeated twicly, then
     * it will add 'desc' suffix to name
     * @param order {string} - Order key
     * @returns {*}
     */
    Table.prototype.order = function(order) {
        if (arguments.length > 0) {
            if (this.property("order") == order) {
                this.property("order", order + " desc");
            } else {
                this.property("order", order);
            }
        }
        return this.property("order");
    };

    /**
     * Render table with header, body and footer
     * @returns {jQuery}
     */
    Table.prototype.render = function() {
        // Check for header and url existence
        if (!this.property("header") || !this.property("url")) {
            return $("<div></div>", {
                html: "<b>Error: Table hasn't been initialized</b>",
                style: "font-size: 20px"
            });
        }
        // Remove old children components selectors
        this._header.selector().remove();
        this._body.selector().remove();
        this._footer.selector().remove();
        // Render table with children components
        return $("<div></div>", {
            class: "panel panel-default"
        }).append(
            $("<table></table>", {
                class: "table table-striped table-bordered"
            }).append(
                this._header.selector(
                    this._header.render()
                )
            ).append(
                this._body.selector(
                    this._body.render()
                )
            )
        ).append(
            this._footer.selector(
                this._footer.render()
            )
        );
    };

    /**
     * Set table's limit, if limit hasn't been set, then it will
     * return first default
     * @param [limit] - Limit to set
     * @returns {Number}
     */
    Table.prototype.limit = function(limit) {
        if (arguments.length > 0) {
            this._limit = limit;
        }
        return this._limit || this.property("limit")[0];
    };

    /**
     * Update current table
     * @returns {boolean}
     */
    Table.prototype.update = function() {
        var me = this;
        // Don't update without URL
        if (!this.property("url")) {
            return false;
        }
        // If selector array (body), then change it to first element
        if ($.isArray(this._body.selector())) {
            this._body.selector(
                this._body.selector()[0]
            );
        }
        // Replace selector with loading image
        this._body.selector().replaceWith(
            $("<td></td>", {
                colspan: this.property("header").length + 1
            }).append(
                $("<img>", {
                    class: "col-md-offset-6",
                    src: "/jaw/images/ajax-loader.gif",
                    style: "padding: 10px"
                })
            )
        );
        // Remove old body's rows
        this.selector().find(".body").remove();
        // Send request to fetch new rows
        $.get(this.property("url"), {
            action: "fetch",
            order: me.order(),
            page: me.property("page") || 1,
            limit: me.limit(),
            where: me.property("where")
        }, function(data) {
            // Parse server's response
            var json = $.parseJSON(data);
            // If we have false status then we got an error
            if (!json.status) {
                // Restore table to previous state
                me.update();
                // Display error message
                return Jaw.createMessage({
                    message: json.message
                });
            }
            // Initialize table
            me.property("length", json["length"]);
            me.property("data", json["table"]);
            me.property("pages", json["pages"]);
            // Update table with super method
            Jaw.Component.prototype.update.call(me);
            // Change pager's page
            me._footer.selector().find(".page").val(
                (+me.property("page") || 1) + "/" + me.property("pages")
            );
        });
    };

    /**
     * Create table element with properties
     * @param selector {string} - Node's selector, like id or class
     * @param properties {{}} - Component's properties
     */
    Jaw.createTable = function(selector, properties) {
        Jaw.create(new Table(properties), selector);
    };

    $(document).ready(function() {
        var parameters = window.location.href.substr(
            window.location.href.lastIndexOf("?") + 1
        ).split("&");
        for (var p in parameters) {
            var center = parameters[p].indexOf("=");
            if (parameters[p].substr(0, center) == "id") {
                console.log(+parameters[p].substr(center + 1));
            }
        }
    });

})(Jaw);