'use strict';

angular.module('myApp')

.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/view1', {
		templateUrl : 'views/view1/view1.html',
		controller : 'View1Ctrl'
	});
} ])

.controller('View1Ctrl', [ function() {

} ]);