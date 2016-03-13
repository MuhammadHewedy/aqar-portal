'use strict';

angular.module('myApp')

.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/search', {
		templateUrl : 'views/search/search.html',
		controller : 'SearchCtrl'
	});
} ])

.controller('SearchCtrl', [ function() {

} ]);