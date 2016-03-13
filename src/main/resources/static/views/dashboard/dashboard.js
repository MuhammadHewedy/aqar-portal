'use strict';

angular.module('myApp')

.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/dashboard', {
		templateUrl : 'views/dashboard/dashboard.html',
		controller : 'DashboardCtrl'
	});
} ])

.controller('DashboardCtrl', [ function() {

} ]);