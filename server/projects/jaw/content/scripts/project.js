var IndexProjectEmployee = {
    activate: function(selector, handler) {
        $(".index-project-start")
            .removeClass("active");
        if (this._active) {
            this._active.removeClass("active");
        }
        this._active = selector;
        if (handler) {
            this._handler = handler;
        }
        selector.addClass("active");
    },
    update: function() {
        var list = $(".index-project-list");
        list.empty().append(
            $("<li></li>", {
                role: "presentation"
            }).append(
                $("<img>", {
                    src: "/jaw/images/ajax-loader.gif"
                })
            )
        );
        $.post("/jaw/index/getEmployeeProjects", {}, function(data) {
            var STRING_LIMIT = 10;
            var json = $.parseJSON(data);
            if (!json.status) {
                return true;
            }
            var employees = json["employees"];
            list.empty();
            for (var i in employees) {
                var groups = $.parseJSON(employees[i]["groups"]);
                var groupString = "";
                for (var j in groups) {
                    groupString += groups[j];
                    if (j != groups.length - 1) {
                        groupString += ",";
                    }
                }
                if (groupString.length > STRING_LIMIT) {
                    groupString = groupString.substr(0, STRING_LIMIT) + "...";
                }
                var name = employees[i]["employee.surname"] + " " +
                    employees[i]["employee.name"].charAt(0).toUpperCase() + "." +
                    employees[i]["employee.patronymic"].charAt(0).toUpperCase();
                var table = $("<table></table>", {
                    style: "width: 100%"
                }).append(
                    $("<thead></thead>").append(
                        $("<tr></tr>").append(
                            $("<td></td>", {
                                style: "text-align: left; width: 50%",
                                html: "<b>" + employees[i]["product.name"] + "</b>"
                            })
                        ).append(
                            $("<td></td>", {
                                style: "text-align: right",
                                text: name
                            })
                        )
                    )
                );
                list.append(
                    $("<li></li>", {
                        role: "presentation",
                        class: "index-project-employee",
                        style: "text-align: left"
                    }).append(
                        $("<a></a>", {
                            href: "javascript:void(0)"
                        }).append(table)
                    ).click(function() {
                            IndexProjectEmployee.activate($(this), function() {
                                var employee = this._active.data("instance");
                                $(".index-project-start").button("loading");
                                $.get("/jaw/employee/login", {
                                    id: employee["employee.id"]
                                }, function(data) {
                                    var json = $.parseJSON(data);
                                    if (!json.status) {
                                        return Jaw.createMessage({
                                            message: json.message
                                        });
                                    }
                                    window.location.href = "/jaw/system/view";
                                });
                            });
                        }).data("instance", employees[i])
                );
            }
            if (!employees.length) {
                list.append(
                    $("<li></li>", {
                        role: "presentation"
                    }).append(
                        $("<b>Вы не подключены ни к одному проекту</b>")
                    )
                );
            }
        });
    },
    construct: function() {
        var me = this;
        $("#create-company").click(function() {
            IndexProjectEmployee.activate($(this), function() {
                $("#modal-create-company").modal({
                    backdrop: 'static',
                    keyboard: false
                });
            });
        });
        $("#join-company").click(function() {
            IndexProjectEmployee.activate($(this), function() {
                $("#modal-join-company").modal({
                    backdrop: 'static',
                    keyboard: false
                });
            });
        });
        $("#create-project").click(function() {
            IndexProjectEmployee.activate($(this), function() {
                $("#modal-create-project").modal({
                    backdrop: 'static',
                    keyboard: false
                });
            });
        });
        $("#join-project").click(function() {
            IndexProjectEmployee.activate($(this), function() {
                $("#modal-join-project").modal({
                    backdrop: 'static',
                    keyboard: false
                });
            });
        });
        $(".index-project-start").click(function() {
            if (me._handler) {
                me._handler();
            }
        });
        $(".logo-form").find(".glyphicon-refresh").click(function() {
            IndexProjectEmployee.update();
        });
        $(".index-project-employee").click(function() {
            var li = $(this);
            IndexProjectEmployee.activate(li, function() {
                var employee = this._active.data("instance");
                $(".index-project-start").button("loading");
                $.get("/jaw/employee/login", {
                    id: li.data("employee") || employee["employee.id"]
                }, function(data) {
                    var json = $.parseJSON(data);
                    $(".index-project-start").button("reset");
                    if (!json.status) {
                        return Jaw.createMessage({
                            message: json.message
                        });
                    }
                    window.location.href = "/jaw/system";
                });
            });
        });
    },
    _active: null,
    _handler: null
};

var ModalCreateCompany = {
    test: function(selector) {
        if (selector.val().length) {
            return true;
        }
        selector.parent().addClass("has-error");
        return false;
    },
    construct: function() {
        var modal = $("#modal-create-company");
        var company = modal.find(".company-name");
        var surname = modal.find(".director-surname");
        var name = modal.find(".director-name");
        var patronymic = modal.find(".director-patronymic");
        modal.find(".create-company").click(function() {
            var result = ModalCreateCompany.test(company) &
                ModalCreateCompany.test(surname) &
                ModalCreateCompany.test(name) &
                ModalCreateCompany.test(patronymic);
            if (!result) {
                return true;
            }
            $.post("/jaw/company/register", {
                company: company.val(),
                name: name.val(),
                surname: surname.val(),
                patronymic: patronymic.val()
            }, function(data) {
                var json = $.parseJSON(data);
                if (!json.status) {
                    return Jaw.createMessage({
                        message: json.message
                    });
                }
                window.location.href = "/jaw";
            });
        });
    }
};

var ModalCreateProject = {
    construct: function() {
        var modal = $("#modal-create-project");
        modal.find(".create-project-companies").change(function() {
            var companyID = $(this).val();
            $("#modal-create-project").find(".create-project-employees").empty().append(
                $("<option></option>", {
                    value: -1,
                    text: "Нет"
                })
            );
            if (companyID < 0) {
                return true;
            }
            modal.find(".ajax-loader").css("visibility", "visible");
            modal.find(".create-project-employees").attr("disabled", "disabled");
            $.post("/jaw/company/getCompanyEmployees", {
                company_id: companyID
            }, function(data) {
                var json = $.parseJSON(data);
                if (!json.status) {
                    return Jaw.createMessage({
                        message: json.message
                    });
                }
                var employees = $.parseJSON(json["employees"]);
                for (var i in employees) {
                    modal.find(".create-project-employees").append(
                        $("<option></option>", {
                            value: employees[i]["id"],
                            text: employees[i]["surname"] + " " + employees[i]["name"]
                        })
                    );
                }
                modal.find(".ajax-loader").css("visibility", "hidden");
                modal.find(".create-project-employees").removeAttr("disabled");
            });
        });
        var name = modal.find(".company-name");
        var companies = modal.find(".create-project-companies");
        var employees = modal.find(".create-project-employees");
        modal.find(".create-project").click(function() {
            var result = ModalCreateCompany.test(name);
            if (companies.val() == -1 || employees.val() == -1 || !result) {
                return true;
            }
            $.post("/jaw/project/register", {
                name: name.val(),
                leader_id: employees.val(),
                company_id: companies.val()
            }, function(data) {
                var json = $.parseJSON(data);
                if (!json.status) {
                    return Jaw.createMessage({
                        message: json.message
                    });
                }
                modal.modal("hide");
                IndexProjectEmployee.update();
            });
        });
    }
};

var ModalJoinCompany = {
    construct: function() {
        var modal = $("#modal-join-company");
        modal.on("show.bs.modal", function() {
            modal.find(".form-group").removeClass("has-error");
        });
        var company = modal.find("#company");
        $(this).parents(".form-group").find(".ajax-loader").css("visibility", "visible");
        modal.find("input").attr("disabled", "disabled");
        $.get("/jaw/company/getRows", {}, function(data) {
            var json = $.parseJSON(data);
            if (!json.status) {
                return Jaw.createMessage({
                    message: json.message
                });
            }
            modal.find("input").removeAttr("disabled");
            var companies = json["companies"];
            for (var i in companies) {
                company.append(
                    $("<option></option>", {
                        value: companies[i]["id"],
                        text: companies[i]["name"]
                    })
                );
            }
        });
        company.change(function() {
            var value = $(this).val();
            modal.find("#receiver").empty().append(
                $("<option></option>", {
                    value: -1,
                    text: "Нет"
                })
            );
            if (value < 0) {
                return true;
            }
            $(this).parents(".form-group").find(".ajax-loader").css("visibility", "visible");
            modal.find("#receiver").attr("disabled", "disabled");
            $.get("/jaw/company/getAcceptors", {
                companyID: value
            }, function(data) {
                var json = $.parseJSON(data);
                if (!json.status) {
                    return Jaw.createMessage({
                        message: json.message
                    });
                }
                company.parents(".form-group").find(".ajax-loader").css("visibility", "hidden");
                modal.find("#receiver").removeAttr("disabled");
                var employees = json["employees"];
                var receiver = modal.find("#receiver");
                var first = null;
                for (var i in employees) {
                    receiver.append(
                        $("<option></option>", {
                            value: employees[i]["id"],
                            text: employees[i]["name"]
                        })
                    );
                    if (!first) {
                        first = employees[i]["id"];
                    }
                }
                if (first) {
                    receiver.val(first);
                }
            });
        });
        modal.find("#submit").click(function() {
            var valid = true;
            modal.find(".form-group input, .form-group select, .form-group textarea").each(function(i, item) {
                if (!$(item).val() || +$(item).val() < 0) {
                    $(item).parents(".form-group").addClass("has-error");
                    valid = false;
                } else {
                    $(item).parents(".form-group").removeClass("has-error");
                }
            });
            if (!valid) {
                return true;
            }
            $.get("/jaw/request/joinCompany", {
                surname: modal.find("#surname").val(),
                name: modal.find("#name").val(),
                patronymic: modal.find("#patronymic").val(),
                receiverID: modal.find("#receiver").val(),
                message: modal.find("#message").val()
            }, function(data) {
                var json = $.parseJSON(data);
                if (!json.status) {
                    return Jaw.createMessage({
                        message: json.message
                    });
                }
                Jaw.createMessage({
                    message: json.message,
                    type: "success",
                    sign: "ok"
                });
                modal.modal("hide");
            });
        });
    }
};

var ModalJoinProject = {
    construct: function() {
        var modal = $("#modal-join-project");
        modal.on("show.bs.modal", function() {
            modal.find(".form-group").removeClass("has-error");
        });
        var company = modal.find("#company");
        var project = modal.find("#project");
        var sender = modal.find("#sender");
        var leader = modal.find("#leader");
        $(this).parents(".form-group").find(".ajax-loader").css("visibility", "visible");
        modal.find("input").attr("disabled", "disabled");
        $.get("/jaw/company/getCompanies", {}, function(data) {
            var json = $.parseJSON(data);
            if (!json.status) {
                return Jaw.createMessage({
                    message: json.message
                });
            }
            modal.find("input").removeAttr("disabled");
            var companies = json["companies"];
            for (var i in companies) {
                company.append(
                    $("<option></option>", {
                        value: companies[i]["id"],
                        text: companies[i]["name"]
                    })
                );
            }
        });
        company.change(function() {
            var value = $(this).val();
            project.empty().append(
                $("<option></option>", {
                    value: -1,
                    text: "Нет"
                })
            );
            leader.empty().append(
                $("<option></option>", {
                    value: -1,
                    text: "Нет"
                })
            );
            if (value < 0) {
                return true;
            }
            $(this).parents(".form-group").find(".ajax-loader").css("visibility", "visible");
            project.attr("disabled", "disabled");
            $.get("/jaw/company/getProjectsAndEmployees", {
                id: value
            }, function(data) {
                var json = $.parseJSON(data);
                company.parents(".form-group").find(".ajax-loader").css("visibility", "hidden");
                if (!json.status) {
                    company.val(-1);
                    return Jaw.createMessage({
                        message: json.message
                    });
                }
                project.removeAttr("disabled");
                var projects = json["projects"];
                var i;
                for (i in projects) {
                    project.append(
                        $("<option></option>", {
                            value: projects[i]["id"],
                            text: projects[i]["name"]
                        })
                    );
                }
                var employees = json["employees"];
                for (i in employees) {
                    sender.append(
                        $("<option></option>", {
                            value: employees[i]["id"],
                            text: employees[i]["name"]
                        })
                    );
                }
            });
        });
        project.change(function() {
            var value = $(this).val();
            leader.empty().append(
                $("<option></option>", {
                    value: -1,
                    text: "Нет"
                })
            );
            if (value < 0) {
                return true;
            }
            $(this).parents(".form-group").find(".ajax-loader").css("visibility", "visible");
            leader.attr("disabled", "disabled");
            $.get("/jaw/project/getAcceptors", {
                id: value
            }, function(data) {
                var json = $.parseJSON(data);
                project.parents(".form-group").find(".ajax-loader").css("visibility", "hidden");
                leader.removeAttr("disabled");
                if (!json.status) {
                    project.val(-1);
                    return Jaw.createMessage({
                        message: json.message
                    });
                }
                var employees = json["employees"];
                var first = null;
                for (var i in employees) {
                    leader.append(
                        $("<option></option>", {
                            value: employees[i]["id"],
                            text: employees[i]["name"]
                        })
                    );
                    if (!first) {
                        first = employees[i]["id"];
                    }
                }
                if (first) {
                    leader.val(first);
                }
            });
        });
        modal.find("#submit").click(function() {
            var valid = true;
            modal.find(".form-group input, .form-group select").each(function(i, item) {
                if (!$(item).val() || +$(item).val() < 0) {
                    $(item).parents(".form-group").addClass("has-error");
                    valid = false;
                } else {
                    $(item).parents(".form-group").removeClass("has-error");
                }
            });
            if (!valid) {
                return true;
            }
            $.get("/jaw/request/joinProduct", {
                receiverID: leader.val(),
                senderID: sender.val(),
                message: modal.find("#message").val(),
                productID: project.val()
            }, function(data) {
                var json = $.parseJSON(data);
                if (!json.status) {
                    return Jaw.createMessage({
                        message: json.message
                    });
                }
                Jaw.createMessage({
                    message: json.message,
                    type: "success",
                    sign: "ok"
                });
                modal.modal("hide");
            });
        });
    }
};

$(document).ready(function() {
    IndexProjectEmployee.construct();
    ModalCreateCompany.construct();
    ModalCreateProject.construct();
    ModalJoinCompany.construct();
    ModalJoinProject.construct();
});