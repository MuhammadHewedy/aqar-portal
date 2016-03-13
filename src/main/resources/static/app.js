'use strict';

// Declare app level module which depends on views, and components
angular.module('myApp', [ 'ngRoute', 'ngResource', 'ui.bootstrap', 'ngMessages', 'pascalprecht.translate' ])

.config([ '$routeProvider', '$locationProvider', '$httpProvider', '$translateProvider', function($routeProvider, $locationProvider, $httpProvider, $translateProvider) {

	$routeProvider.otherwise({
		redirectTo : '/dashboard'
	});

	$locationProvider.html5Mode(true);

	$httpProvider.defaults.headers.common = {
		'X-Requested-With' : 'XMLHttpRequest'
	};

	$httpProvider.interceptors.push('response_interceptor');

	$translateProvider.preferredLanguage('en');
	$translateProvider.useSanitizeValueStrategy('escape');
	$translateProvider.useStaticFilesLoader({
		prefix : '/languages/',
		suffix : '.json'
	});

} ]).controller('MainCtrl', [ '$scope', '$location', '$rootScope', 'AuthService', function($scope, $location, $rootScope, AuthService) {

	$scope.logout = function() {
		AuthService.logout().then(function() {
			$location.path("/");
			$rootScope.currentUser = null;
		})
	}

} ]).factory('response_interceptor', [ '$q', '$location', function($q, $location) {
	return {
		'responseError' : function(rejection) {
			if (rejection.status == 401 && $location.path() != '/login') {
				$location.path('/login');
			}
			if (rejection.status == 403) {
				$location.path('/access-denied');
			}
			if (rejection.data.message == undefined) {
				rejection.data = {}
				if (rejection.status == 500) {
					rejection.data.message = 'internal.server.error';
				} else if (rejection.status == 400) {
					rejection.data.message = 'bad.request';
				}
			}
			return $q.reject(rejection);
		}
	};
} ]).run([ '$rootScope', '$location', '$route', 'AuthService', function($rootScope, $location, $route, AuthService) {

	AuthService.getLoggedInUser().then(function(user) {
		$rootScope.currentUser = user;
		console.log('user returned from server: ', user);
		$location.url($rootScope.getQueryParam('next'));
	}, function(status) {
		$rootScope.currentUser = null;
		console.log('No loggedIn user info, redirect to login view; status: ' + status);
		$location.path('/login');
	});

	$rootScope.$on('$routeChangeStart', function(event, next, current) {
		console.log($location);

		if ($rootScope.currentUser == null && $location.path() != '/logout') {
			console.log('User is null, always be redirected to login view');
			$location.path('/login');
		}
	});

} ]);
