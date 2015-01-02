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
                    return Jaw.createMessage({
                        message: json.message
                    });
                }
                $("#modal-register").modal("hide");
                $.post("/jaw/user/login", {
                    login: login,
                    password: password
                }).done(function(data) {
                    var json = $.parseJSON(data);
                    if (!json.status) {
                        return Jaw.createMessage({
                            message: json.message
                        });
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
                    return Jaw.createMessage({
                        message: json.message
                    });
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

$(document).ready(function() {
    FormLogin.construct();
    ModalRegister.construct();
});