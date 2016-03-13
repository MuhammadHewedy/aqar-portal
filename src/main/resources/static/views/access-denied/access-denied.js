'use strict';

angular.module('myApp')

.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/access-denied', {
		templateUrl : 'views/access-denied/access-denied.html',
		controller : 'AccessDeniedCtrl'
	});
} ])

.controller('AccessDeniedCtrl', [ function() {

} ]);