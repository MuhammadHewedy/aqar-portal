'use strict';

// http://www.sitepoint.com/creating-crud-app-minutes-angulars-resource/

angular.module('myApp')

.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/accounts/create', {
		templateUrl : 'views/accounts/create-account.html',
		controller : 'CreateAccountCtrl'
	});
} ])

.controller('CreateAccountCtrl', [ '$scope', 'Account', '$rootScope', function($scope, Account, $rootScope) {

	$scope.account = new Account();

	$scope.saveAccount = function() {
		$scope.account.$save(function() {
			$rootScope.success();
			$scope.account = new Account();
			$scope.form.$setPristine();
		}, function(error) {
			$rootScope.error(error.data.message);
		})
	};

} ]);