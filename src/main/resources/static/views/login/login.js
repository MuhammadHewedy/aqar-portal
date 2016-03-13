'use strict';

angular.module('myApp')

.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/login', {
		templateUrl : 'views/login/login.html',
		controller : 'LoginCtrl'
	});
} ])

.controller('LoginCtrl', [ '$scope', '$rootScope', '$location', 'AuthService', function($scope, $rootScope, $location, AuthService) {
	$scope.credentials = {
		username : '',
		password : '',
		error : ''
	};

	$scope.login = function() {
		AuthService.login($scope.credentials).then(function(user) {
			$rootScope.currentUser = user;
			$location.path('/');
		}, function(message) {
			$rootScope.currentUser = null;
			$scope.credentials.error = message;
			console.log('error: ' + message);
		});
	}
} ]);