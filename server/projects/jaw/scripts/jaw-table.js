var Jaw = Jaw || {};

(function(Jaw) {

    "use strict";

    var Widget = function(selector, properties) {
        Jaw.Widget.call(this, selector, properties);
        this._table = new Table(selector, properties);
    };

    Jaw.extend(Widget, Jaw.Widget);

    Widget.prototype.table = function(table) {
        if (arguments.length > 0) {
            this._table = table;
        }
        return this._table;
    };

    Widget.prototype.construct = function() {
        var me = this;
        var i;
        if (!this.has("header") || !this.has("url")) {
            return $("<div></div>", {
                html: "<b>Error: Table hasn't been initialized</b>",
                style: "font-size: 20px"
            });
        }
        var header = $("<tr></tr>", {
            style: "background-color: #ddd;"
        });
        var head = this.property("header");
        for (i in head) {
            $("<td></td>", {
                html: "<b>" + head[i].name + "</b>",
                style: head[i].name == "#" ? "width: 30px; text-align: center; cursor: pointer;"
                    : "cursor: pointer;"
            }).click(function() {
                me.order($(this).data("id"));
                me.update();
            }).data("id", head[i].id)
                .appendTo(header);
        }
        header.append($("<td><b>Действия<b></td>"));
        var table = $("<table></table>", {
            class: "table table-striped table-bordered"
        }).append(header);
        if (!this.has("data")) {
            return table;
        }
        var query = $("<input>", {
            type: "text",
            placeholder: "Условие поиска",
            class: "form-control",
            id: "query",
            style: "width: 350px; float: left;",
            value: this.where()
        }).keydown(function(e) {
            if (e.keyCode == 13) {
                button.trigger("click");
            }
        });
        var button = $("<button></button>", {
            text: "Отправить",
            class: "btn btn-primary",
            style: "margin-left: 10px;"
        }).click(function() {
            var value = footer.find("#query").val();
            if (value) {
                me.where(value);
            } else {
                me.where(null);
            }
            me.update();
        });
        var select = $("<select></select>", {
            class: "form-control",
            style: "width: auto"
        }).change(function() {
            me.limit(+$(this).val());
            me.update();
        });
        for (i in this.property("limit")) {
            select.append($("<option></option>", {
                text: this.property("limit")[i],
                value: this.property("limit")[i]
            }));
        }
        select.val(me.limit());
        var footer = $("<table></table>", {
            style: "width: 100%"
        }).append(
            $("<tr></tr>", {
                colspan: head.length + 1
            }).append(
                $("<td></td>", {
                    style: "width: 550px"
                }).append(query).append(button)
            ).append(
                $("<td></td>", {
                    style: "width: 200px; vertical-align: middle",
                    valign: "middle"
                }).append(
                    $("<span></span>", {
                        style: "float: left; line-height: 30px; margin-right: 5px; cursor: pointer",
                        class: "glyphicon glyphicon-chevron-left"
                    }).click(function() {
                        var value = +$(this).parent().children("#page").val().split("/")[0];
                        if (value <= 1) {
                            return true;
                        }
                        $(this).parent().children("#page").val(
                            (value - 1) + "/" + me.property("pages")
                        );
                        me.page(value - 1);
                        me.update();
                    })
                ).append(
                    $("<input>", {
                        style: "width: 60px; float: left; text-align: center",
                        type: "text",
                        class: "form-control",
                        disabled: "disabled",
                        value: "1/0",
                        id: "page"
                    })
                ).append(
                    $("<span></span>", {
                        style: "float: left; line-height: 30px; margin-left: 5px; cursor: pointer",
                        class: "glyphicon glyphicon-chevron-right"
                    }).click(function() {
                        var value = +$(this).parent().children("#page").val().split("/")[0];
                        if (me.property("pages") && value >= +me.property("pages")) {
                            return true;
                        }
                        $(this).parent().children("#page").val(
                            (value + 1) + "/" + me.property("pages")
                        );
                        me.page(value + 1);
                        me.update();
                    })
                )
            ).append(
                $("<td></td>", {
                    style: "width: auto"
                }).append(select)
            ).append(
                $("<td></td>", {
                    style: "text-align: center; width: 30px;"
                }).append(
                    $("<span></span>", {
                        class: "glyphicon glyphicon-plus",
                        style: "font-size: 20px; line-height: 25px; cursor: pointer;"
                    }).click(function() {
                        var row = {};
                        for (var k in head) {
                            row[head[k].id] = "";
                        }
                        me.build($("#modal-jaw-table-add"), row, false, true);
                    })
                )
            ).append(
                $("<td></td>", {
                    style: "text-align: right; width: 30px;"
                }).append(
                    $("<span></span>", {
                        class: "glyphicon glyphicon-refresh",
                        style: "font-size: 20px; line-height: 25px; cursor: pointer;"
                    }).click(function() {
                        me.update();
                    })
                )
            )
        );
        return $("<div></div>", {
            class: "panel panel-default"
        }).append(table).append(
            $("<div></div>", {
                class: "panel-footer"
            }).append(footer)
        );
    };

    Widget.prototype.render = function() {
    };

    /*
      _____ _   ___ _    ___
     |_   _/_\ | _ ) |  | __|
       | |/ _ \| _ \ |__| _|
       |_/_/ \_\___/____|___|

     */

    var Table = function(selector, properties) {
        this._widget = selector;
        this._properties = properties;
        this._selector = this.render();
        this._active = undefined;
        this._order = undefined;
        this._page = undefined;
        this._where = undefined;
        this._clicked = undefined;
        this._limit = undefined;
        this.property("limit", this.property("limit") || [
            10, 25, 50, 100
        ]);
        var me = this;
        var head = this.property("header");
        var editModal = $("#modal-jaw-table-edit");
        var deleteModal = $("#modal-jaw-table-delete");
        var addModal = $("#modal-jaw-table-add");
        editModal
            .find("#jaw-table-save").click(function() {
                var attributes = {};
                var form = editModal.find(".jaw-table-form");
                for (var h in head) {
                    var s = form.find("#" + head[h].id.replace(".", "_"));
                    if (!s.val().length) {
                        s.parents(".form-group").addClass("has-error");
                        return true;
                    }
                    attributes[head[h].id.substr(head[h].id.indexOf(".") + 1)] = s.val();
                }
                var button = $(this).button("loading");
                $.get(me.property("url") + "?action=update", attributes, function(data) {
                    var json = $.parseJSON(data);
                    if (!json.status) {
                        button.button("reset");
                        return ErrorMessage.post(json.message);
                    }
                    button.button("reset");
                    editModal.modal("hide");
                    me.update();
                });
            });
        deleteModal
            .find("#jaw-table-delete").click(function() {
                var button = $(this).button("loading");
                $.get(me.property("url") + "?action=delete", {
                    id: me.active()[me.property("table") + ".id"]
                }, function(data) {
                    var json = $.parseJSON(data);
                    if (!json.status) {
                        button.button("reset");
                        return ErrorMessage.post(json.message);
                    }
                    button.button("reset");
                    deleteModal.modal("hide");
                    me.update();
                });
            });
        addModal
            .find("#jaw-table-add").click(function() {
                var attributes = {};
                var form = addModal.find(".jaw-table-form");
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
                $.get(me.property("url") + "?action=add", attributes, function(data) {
                    var json = $.parseJSON(data);
                    if (!json.status) {
                        button.button("reset");
                        return ErrorMessage.post(json.message);
                    }
                    button.button("reset");
                    addModal.modal("hide");
                    me.update();
                });
            });
    };

    Table.prototype.active = function(active) {
        if (arguments.length > 0) {
            this._active = active;
        }
        return this._active;
    };

    Table.prototype.clicked = function(clicked) {
        if (arguments.length > 0) {
            this._clicked = clicked;
        }
        return this._clicked;
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

    Table.prototype.order = function(order) {
        if (arguments.length > 0) {
            if (this._order && this._order == order) {
                this._order += " desc";
            } else {
                this._order = order;
            }
        }
        return this._order;
    };

    Table.prototype.limit = function(limit) {
        if (arguments.length > 0) {
            this._limit = limit;
        }
        return this._limit;
    };

    Table.prototype.page = function(page) {
        if (arguments.length > 0) {
            this._page = page;
        }
        return this._page;
    };

    Table.prototype.where = function(where) {
        if (arguments.length > 0) {
            this._where = where;
        }
        return this._where;
    };

    Table.prototype.selector = function(selector) {
        if (arguments.length > 0) {
            this._selector = selector;
        }
        return this._selector;
    };

    Table.prototype.build = function(modal, row, readonly) {
        var header = this.property("header");
        var head = function(id) {
            for (var i in header) {
                if (header[i].id == id) {
                    return header[i];
                }
            }
            return null;
        };
        var me = this;
        var form = modal.find(".jaw-table-form");
        form.empty();
        var stack = [];
        var temporary = {};
        for (var k in row) {
            if (!head(k) || head(k).id.endsWith(".id") && !head(k).type) {
                continue;
            }
            var name = head(k).name == "#" ? "Идентификатор" : head(k).name;
            if (readonly && head(k).type == "password") {
                continue;
            }
            var key = k.substr(k.indexOf(".") + 1);
            var input;
            if (key != "id" && key.endsWith("_id")) {
                input = $("<select></select>", {
                    class: "form-control",
                    value: row[k],
                    id: head(k).id.replace(".", "_")
                }).append(
                    $("<option></option>", {
                        text: "Нет",
                        value: -1
                    })
                );
                input.data("value", row[k]);
                temporary[key] = input;
                var serialized = "";
                var display = head(k).display;
                var separator = head(k).separator || ",";
                if ($.isArray(display)) {
                    for (var j in display) {
                        serialized += display[j] + separator;
                    }
                } else {
                    serialized = display;
                }
                $.get(head(k).url, {
                    action: "all",
                    key: key,
                    display: serialized || key,
                    separator: separator
                }, function(data) {
                    var json = $.parseJSON(data);
                    if (!json.status) {
                        return ErrorMessage.post(json.message);
                    }
                    for (var j in json.rows) {
                        var display = json.display.split(
                            json.separator
                        );
                        var text = "";
                        for (var k in display) {
                            if (!display[k]) {
                                continue;
                            }
                            text += json.rows[j][display[k]];
                            if (k != display.length - 1) {
                                text += json.separator;
                            }
                        }
                        var value = -1;
                        for (var k in json.rows[j]) {
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
                });
            } else {
                input = $("<input>", {
                    type: head(k).type || "text",
                    placeholder: name,
                    class: "form-control",
                    id: head(k).id.replace(".", "_"),
                    value: head(k).type != "password" ? row[k] : ""
                });
            }
            if (readonly || head(k).id.endsWith(".id") && !head(k).type) {
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
        modal.modal();
        this.active(row);
    };

    Table.prototype.renderReferences = function(id, data) {
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

    Table.prototype.buildReference = function(modal, row) {
        var k = this.property("table") + ".id";
        var id = row[k];
        if (!id) {
            return ErrorMessage.post("Table/buildReference() : \"Can't find key for row\"");
        }
        modal.find(".jaw-table-form").empty().append(
            $("<div></div>", {
                style: "width: 100%; text-align: center;"
            }).append(
                $("<img>", {
                    src: "/jaw/images/ajax-loader.gif"
                })
            )
        );
        modal.modal();
        var me = this;
        $.get(this.property("url"), {
            action: "reference",
            id: id,
            alias: k
        }, function (data) {
            var json = $.parseJSON(data);
            if (!json.status) {
                return ErrorMessage.post(json.message);
            }
            var references = json["reference"];
            var has = false;
            var s = modal.find(".jaw-table-form").empty();
            for (var k in references) {
                if (references[k].length) {
                    has = true;
                }
                s.append(
                    me.renderReferences(k, references[k])
                );
            }
            if (!has) {
                modal.find(".jaw-table-form").empty().append(
                    $("<div></div>", {
                        style: "text-align: center; font-size: 20px",
                        html: "<b>Данный элемент не имеет зависимостей</b>"
                    })
                );
            }
        });
    };

    Table.prototype.action = function(row) {
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
                me.buildReference($("#modal-jaw-table-reference"), row);
            })
        );
        container.append(
            $("<span></span>", {
                class: "glyphicon glyphicon-pencil",
                style: style
            }).click(function() {
                me.build($("#modal-jaw-table-edit"), row);
            })
        );
        container.append(
            $("<span></span>", {
                class: "glyphicon glyphicon-remove",
                style: style
            }).click(function() {
                me.build($("#modal-jaw-table-delete"), row, true);
            })
        );
        return container;
    };

    Table.prototype.update = function(strict) {
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
            action: "fetch",
            order: me.order(),
            page: me.page() || 1,
            limit: this.limit() || this.property("limit")[0],
            where: me.where()
        }, function(data) {
            var json = $.parseJSON(data);
            if (!json.status) {
                me.widget().empty().append(
                    me.selector(me.render())
                );
                return ErrorMessage.post(json.message);
            }
            me.property("length", json["length"]);
            me.property("data", json["table"]);
            me.property("pages", json["pages"]);
            me.widget().empty().append(
                me.selector(me.render())
            );
            me.widget().find("#page").val(
                (+me.page() || 1) + "/" + me.property("pages")
            );
        });
    };

    Table.prototype.render = function() {
        var me = this;
        var i;
        if (!this.has("header") || !this.has("url")) {
            return $("<div></div>", {
                html: "<b>Error: Table hasn't been initialized</b>",
                style: "font-size: 20px"
            });
        }
        var header = $("<tr></tr>", {
            style: "background-color: #ddd;"
        });
        var head = this.property("header");
        for (i in head) {
            $("<td></td>", {
                html: "<b>" + head[i].name + "</b>",
                style: head[i].name == "#" ? "width: 30px; text-align: center; cursor: pointer;"
                    : "cursor: pointer;"
            }).click(function() {
                me.order($(this).data("id"));
                me.update();
            }).data("id", head[i].id)
                .appendTo(header);
        }
        header.append($("<td><b>Действия<b></td>"));
        var table = $("<table></table>", {
            class: "table table-striped table-bordered"
        }).append(header);
        if (!this.has("data")) {
            return table;
        }
        for (var k in this.property("data")) {
            var body = $("<tr></tr>", {
                class: "default unselectable"
            });
            var global = "id";
            var data = this.property("data")[k];
            body.data("row", data).dblclick(function() {
                me.buildReference($("#modal-jaw-table-reference"),
                    $(this).data("row")
                );
            });
            for (i in head) {
                var id = head[i].display || head[i].id;
                var c;
                if ($.isArray(id)) {
                    var separator = head[i].separator || ", ";
                    var html = "";
                    for (var j in id) {
                        html += data[id[j]] + (j != id.length - 1 ? separator : "");
                        if (id[j].endsWith(".id")) {
                            global = id[j];
                        }
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
                    if (id.endsWith(".id")) {
                        global = id;
                    }
                }
                if (head[i].href) {
                    var href = head[i].href;
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
                    this.action(data)
                )
            ).click(function() {
                    history.pushState(null, null, window.location.hash +
                        "?id=" + $(this).data("id")
                    );
                    if (me.clicked()) {
                        me.clicked().removeClass("info");
                    }
                    me.clicked($(this).addClass("info"));
                }
            ).data("id", data[global]);
            table.append(body);
        }
        if (!this.property("data").length) {
            table.append($("<tr></tr>", {
                class: "default"
            }).append(
                $("<td></td>", {
                    colspan: head.length + 1,
                    style: "font-size: 20px; text-align: center;",
                    html: "<b>Отсутсвуют данные для отображения</b>"
                })
            ));
        }
        var query = $("<input>", {
            type: "text",
            placeholder: "Условие поиска",
            class: "form-control",
            id: "query",
            style: "width: 350px; float: left;",
            value: this.where()
        }).keydown(function(e) {
            if (e.keyCode == 13) {
                button.trigger("click");
            }
        });
        var button = $("<button></button>", {
            text: "Отправить",
            class: "btn btn-primary",
            style: "margin-left: 10px;"
        }).click(function() {
            var value = footer.find("#query").val();
            if (value) {
                me.where(value);
            } else {
                me.where(null);
            }
            me.update();
        });
        var select = $("<select></select>", {
            class: "form-control",
            style: "width: auto"
        }).change(function() {
            me.limit(+$(this).val());
            me.update();
        });
        for (i in this.property("limit")) {
            select.append($("<option></option>", {
                text: this.property("limit")[i],
                value: this.property("limit")[i]
            }));
        }
        select.val(me.limit());
        var footer = $("<table></table>", {
            style: "width: 100%"
        }).append(
            $("<tr></tr>", {
                colspan: head.length + 1
            }).append(
                $("<td></td>", {
                    style: "width: 550px"
                }).append(query).append(button)
            ).append(
                $("<td></td>", {
                    style: "width: 200px; vertical-align: middle",
                    valign: "middle"
                }).append(
                    $("<span></span>", {
                        style: "float: left; line-height: 30px; margin-right: 5px; cursor: pointer",
                        class: "glyphicon glyphicon-chevron-left"
                    }).click(function() {
                        var value = +$(this).parent().children("#page").val().split("/")[0];
                        if (value <= 1) {
                            return true;
                        }
                        $(this).parent().children("#page").val(
                            (value - 1) + "/" + me.property("pages")
                        );
                        me.page(value - 1);
                        me.update();
                    })
                ).append(
                    $("<input>", {
                        style: "width: 60px; float: left; text-align: center",
                        type: "text",
                        class: "form-control",
                        disabled: "disabled",
                        value: "1/0",
                        id: "page"
                    })
                ).append(
                    $("<span></span>", {
                        style: "float: left; line-height: 30px; margin-left: 5px; cursor: pointer",
                        class: "glyphicon glyphicon-chevron-right"
                    }).click(function() {
                        var value = +$(this).parent().children("#page").val().split("/")[0];
                        if (me.property("pages") && value >= +me.property("pages")) {
                            return true;
                        }
                        $(this).parent().children("#page").val(
                            (value + 1) + "/" + me.property("pages")
                        );
                        me.page(value + 1);
                        me.update();
                    })
                )
            ).append(
                $("<td></td>", {
                    style: "width: auto"
                }).append(select)
            ).append(
                $("<td></td>", {
                    style: "text-align: center; width: 30px;"
                }).append(
                    $("<span></span>", {
                        class: "glyphicon glyphicon-plus",
                        style: "font-size: 20px; line-height: 25px; cursor: pointer;"
                    }).click(function() {
                        var row = {};
                        for (var k in head) {
                            row[head[k].id] = "";
                        }
                        me.build($("#modal-jaw-table-add"), row, false, true);
                    })
                )
            ).append(
                $("<td></td>", {
                    style: "text-align: right; width: 30px;"
                }).append(
                    $("<span></span>", {
                        class: "glyphicon glyphicon-refresh",
                        style: "font-size: 20px; line-height: 25px; cursor: pointer;"
                    }).click(function() {
                        me.update();
                    })
                )
            )
        );
        return $("<div></div>", {
            class: "panel panel-default"
        }).append(table).append(
            $("<div></div>", {
                class: "panel-footer"
            }).append(footer)
        );
    };

    Jaw.createTable = function(selector, properties) {
        var parent = $(selector);
        var table = new Table(parent, properties);
        parent.data("jaw-table", table).append(
            table.selector()
        );
        table.update();
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