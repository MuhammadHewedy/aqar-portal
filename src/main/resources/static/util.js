'use strict';

angular.module('myApp').run([ '$rootScope', function($rootScope) {

	$rootScope.getQueryParam = function(name) {
		var match = RegExp('[?&]' + name + '=([^&]*)').exec(window.location.search);
		return match && decodeURIComponent(match[1].replace(/\+/g, ' '));
	}

	$rootScope.alerts = [];

	$rootScope.success = function() {
		addAlert('operation.done.successfully', 'success', 2000);
	}

	$rootScope.info = function(msg) {
		addAlert(msg, 'success', 2000);
	};

	$rootScope.error = function(msg) {
		addAlert(msg, 'danger', 10000);
	};

	$rootScope.closeAlert = function(index) {
		$rootScope.alerts.splice(index, 1);
	};
	// ---
	function addAlert(msg, type, timeout) {
		$rootScope.alerts.push({
			msg : msg,
			type : type,
			timeout : timeout
		});
	}

} ]);
