var ErrorMessage = {
	activate: function() {
		if (parseInt($(".error-message").css("left")) < 0) {
			this.open();
		} else {
			this.close();
		}
	},
	open: function() {
		if (parseInt($(".error-message").css("left")) < 0) {
			$(".error-message").animate({
				"left": "10px"
			}, "slow");
		}
	},
	close: function() {
		if (parseInt($(".error-message").css("left")) > 0) {
			$(".error-message").animate({
				"left": "-" + parseInt($(".error-message").css("width")) + "px"
			}, "slow");
		}
	},
	post: function(message) {
		if (parseInt($(".error-message").css("left")) > 0 && message == $(".error-message .message").text()) {
			return false;
		}
		$(".error-message .message").html(message);
		$(".error-message").css("left", "-" + (message.length * 10) + "px");
		this.open();
		return true;
	},
	construct: function() {
		$(".error-message").click(function() {
			ErrorMessage.close();
		});
	}
};

$(document).ready(function() {
	ErrorMessage.construct();
});