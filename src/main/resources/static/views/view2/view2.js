'use strict';

angular.module('myApp')

.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/view2', {
		templateUrl : 'views/view2/view2.html',
		controller : 'View2Ctrl'
	});
} ])

.controller('View2Ctrl', [ function() {

} ]);