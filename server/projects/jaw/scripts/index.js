var ModalRegister = {
    highlight: function(selector, strict) {
        if (!$(selector).val().length || strict !== undefined) {
            $(selector).parent().find(".glyphicon").addClass(
                "highlight-error"
            ).removeClass(
                "highlight-ok"
            );
            return false;
        } else {
            $(selector).parent().find(".glyphicon").addClass(
                "highlight-ok"
            ).removeClass(
                "highlight-error"
            );
            return true;
        }
    },
    check: function(strict) {
        var result = this.highlight("#modal-register .register-login") &
            this.highlight("#modal-register .register-password") &
            this.highlight("#modal-register .register-repeat") &
            this.highlight("#modal-register .register-email");
        if ($("#modal-register .register-password").val() != $("#modal-register .register-repeat").val()) {
            ModalRegister.highlight("#modal-register .register-password", false);
            ModalRegister.highlight("#modal-register .register-repeat", false);
        } else {
            return result;
        }
        return false;
    },
    reset: function() {
        $("#modal-register .glyphicon").removeClass(
            "highlight-ok"
        ).removeClass(
            "highlight-error"
        );
    },
    construct: function() {
        $("#modal-register input").keyup(function(e) {
            ModalRegister.check();
        });
        $("#modal-register .btn-primary").click(function() {
            if (!ModalRegister.check()) {
                return true;
            }
            var login = $("#modal-register .register-login").val();
            var password = $("#modal-register .register-password").val();
            var email = $("#modal-register .register-email").val();
            $.post("/jaw/user/register", {
                login: login,
                password: password,
                email: email
            }).done(function(data) {
                var json = $.parseJSON(data);
                if (!json.status && json.message) {
                    return ErrorMessage.post(json.message);
                }
                $("#modal-register").modal("hide");
                $.post("/jaw/user/login", {
                    login: login,
                    password: password
                }).done(function(data) {
                    var json = $.parseJSON(data);
                    if (!json.status) {
                        return ErrorMessage.post(json.message);
                    }
                    window.location.href = "/jaw/index/project";
                });
            });
        });
        $("#modal-register input").append(
            $("<span></span>", {
                class: "glyphicon glyphicon-exclamation-sign highlight-error",
                style: "position: absolute"
            })
        );
    }
};

var FormLogin = {
    check: function(strict) {
        var result = ModalRegister.highlight(".login-login") &
            ModalRegister.highlight(".login-password");
        return result;
    },
    construct: function() {
        $("#login input").keyup(function(e) {
            FormLogin.check();
        });
        $("#login .login-submit").click(function() {
            if (!FormLogin.check()) {
                return true;
            }
            $.post("/jaw/user/login", {
                login: $("#login .login-login").val(),
                password: $("#login .login-password").val()
            }).done(function(data) {
                var json = $.parseJSON(data);
                if (!json.status) {
                    return ErrorMessage.post(json.message);
                }
                window.location.href = "/jaw/index/project";
            });
        });
        $("#login .login-register").click(function() {
            $("#modal-register").modal({
                backdrop: 'static',
                keyboard: false
            });
            ModalRegister.reset();
        });
    }
};

var BackgroundParallax = {
    construct: function() {
        var center = {
            x: $(window).width() / 2,
            y: $(window).height() / 2
        };
        $(document).mousemove(function(e) {
            var offset = {
                x: -(e.pageX - center.x) / 50,
                y: -(e.pageY - center.y) / 50
            };
            $(document.body).css("background-position", offset.x + "px " + offset.y + "px");
        });
    }
};

var ProjectForm = {
    construct: function() {
        $(".logo-form .index-project-exit").click(function() {
            window.location.href = "/jaw/user/logout";
        });
    }
};

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
            var STRING_LIMIT = 30;
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
                        groupString += ", ";
                    }
                }
                var string = employees[i]["product.name"] + "/" +
                    employees[i]["employee.surname"] + " " + employees[i]["employee.name"] + "/";
                string += groupString;
                if (string.length > STRING_LIMIT) {
                    string = string.substr(0, STRING_LIMIT) + "...";
                }
                list.append(
                    $("<li></li>", {
                        role: "presentation",
                        class: "index-project-employee",
                        id: "join-project",
                        style: "text-align: left"
                    }).append(
                        $("<a></a>", {
                            href: "#",
                            text: string
                        })
                    ).click(function() {
                            IndexProjectEmployee.activate($(this), function() {
                                console.log(this._active.data("instance"));
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
        this.update();
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
        var company = $("#modal-create-company .company-name");
        var surname = $("#modal-create-company .director-surname");
        var name = $("#modal-create-company .director-name");
        var patronymic = $("#modal-create-company .director-patronymic");
        $("#modal-create-company .create-company").click(function() {
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
                    return ErrorMessage.post(json.message);
                }
                window.location.href = "/jaw/index/project";
            });
        });
    }
};

var ModalCreateProject = {
    construct: function() {
        var modal = $("#modal-create-project");
        modal.find(".create-project-companies").change(function() {
            var companyID = $(this).val();
            $("#modal-create-project .create-project-employees").empty().append(
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
                    return ErrorMessage.post(json.message);
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
                    return ErrorMessage.post(json.message);
                }
                //window.location.href = "/jaw/index/project";
                modal.modal("hide");
                IndexProjectEmployee.update();
            });
        });
    }
};

var GodButton = {
    construct: function() {
        $(".god-button").click(function() {
            window.location.href = "/jaw/admin/view";
        });
    }
};

$(document).ready(function() {
    IndexProjectEmployee.construct();
    ModalCreateCompany.construct();
    ModalCreateProject.construct();
    GodButton.construct();
    FormLogin.construct();
    ModalRegister.construct();
    ProjectForm.construct();
});