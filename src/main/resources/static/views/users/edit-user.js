'use strict';

// http://www.sitepoint.com/creating-crud-app-minutes-angulars-resource/

angular.module('myApp')

.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/users/edit/:id', {
		templateUrl : 'views/users/create-user.html',
		controller : 'EditUserCtrl'
	});
} ])

.controller('EditUserCtrl', [ '$scope', '$rootScope', '$routeParams', 'User', 'Account', '$location', function($scope, $rootScope, $routeParams, User, Account, $location) {

	User.get({
		id : $routeParams.id
	}, function(user) {
		$scope.user = user;
		console.log(user);
	})

	$scope.user = new User();
	$scope.saveUser = function() {
		$scope.user.$update(function() {
			$rootScope.success();
			$location.path('/accounts');
		}, function(error) {
			$rootScope.error(error.data.message);
		})
	}

	Account.query({
		size : 2000,
		'enabled' : 'true'
	}, function(accounts) {
		$scope.accounts = accounts.content;
	})

} ]);