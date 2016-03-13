'use strict';

// http://www.sitepoint.com/creating-crud-app-minutes-angulars-resource/

angular.module('myApp')

.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/accounts', {
		redirectTo : '/accounts/page/1'
	}).when('/accounts/page/:page', {
		templateUrl : 'views/accounts/accounts.html',
		controller : 'ListAccountCtrl'
	});
} ])

.controller('ListAccountCtrl', [ '$scope', '$routeParams', '$location', 'Account', function($scope, $routeParams, $location, Account) {

	$scope.currentPage = $routeParams.page;

	$scope.goToPage = function() {
		$location.path('/accounts/page/' + $scope.currentPage);
	}

	$scope.getData = function() {
		Account.query({
			page : $routeParams.page - 1
		}, function(accounts) {
			$scope.accounts = accounts;
		});
	}();
} ]);