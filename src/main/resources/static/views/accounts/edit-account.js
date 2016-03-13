'use strict';

// http://www.sitepoint.com/creating-crud-app-minutes-angulars-resource/

angular.module('myApp')

.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/accounts/edit/:id', {
		templateUrl : 'views/accounts/create-account.html',
		controller : 'EditAccountCtrl'
	});
} ])

.controller('EditAccountCtrl', [ '$scope', 'Account', '$rootScope', '$routeParams', '$location', function($scope, Account, $rootScope, $routeParams, $location) {

	Account.get({
		id : $routeParams.id
	}, function(account) {
		$scope.account = account;
	})

	$scope.saveAccount = function() {
		$scope.account.$update(function() {
			$rootScope.success();
			$location.path('/accounts');
		}, function(error) {
			$rootScope.error(error.data.message);
		})
	};

} ]);