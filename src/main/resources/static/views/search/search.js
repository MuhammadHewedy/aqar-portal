'use strict';

angular.module('myApp')

.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/search', {
		templateUrl : 'views/search/search.html',
		controller : 'SearchCtrl'
	});
} ])

.controller('SearchCtrl', [ '$scope', 'Apartment', function($scope, Apartment) {

	Apartment.query(function(apartments) {
		$scope.apartments = apartments.content;
		console.log($scope.apartments);
	});
} ]);