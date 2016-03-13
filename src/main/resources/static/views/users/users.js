'use strict';

// http://www.sitepoint.com/creating-crud-app-minutes-angulars-resource/

angular.module('myApp')

.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/users', {
		templateUrl : 'views/users/users.html',
		controller : 'UsersCtrl'
	});
} ])

.controller('UsersCtrl', [ '$scope', 'User', function($scope, User, Account) {

	$scope.currentPage = 1;

	$scope.goToPage = function() {
		User.query({
			page : $scope.currentPage - 1
		}, function(users) {
			$scope.users = users;
		});
	}

	// ---- list
	$scope.users = User.query();

} ]);