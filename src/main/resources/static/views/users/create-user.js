'use strict';

// http://www.sitepoint.com/creating-crud-app-minutes-angulars-resource/

angular.module('myApp')

.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/users/create', {
		templateUrl : 'views/users/create-user.html',
		controller : 'CreateUserCtrl'
	});
} ])

.controller('CreateUserCtrl', [ '$scope', 'User', 'Account', '$rootScope', function($scope, User, Account, $rootScope) {

	$scope.user = new User();
	$scope.saveUser = function() {
		$scope.user.$save(function() {
			$rootScope.success();
			$scope.user = new User();
			$scope.form.$setPristine();
		}, function(error) {
			$rootScope.error(error.data.message);
		})
	}

	Account.query({
		'enabled' : 'true',
		size : '100000000'
	}, function(data) {
		$scope.accounts = data.content;
	})

} ]);