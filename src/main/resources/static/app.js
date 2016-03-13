'use strict';

// Declare app level module which depends on views, and components
angular.module('myApp', [ 'ngRoute', 'ngResource', 'ui.bootstrap', 'ngMessages', 'pascalprecht.translate' ])

.config([ '$routeProvider', '$locationProvider', '$httpProvider', '$translateProvider', function($routeProvider, $locationProvider, $httpProvider, $translateProvider) {

/*	$routeProvider.otherwise({
		redirectTo : '/'
	});*/

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

} ]).factory('response_interceptor', [ '$q', '$location', function($q, $location) {
	return {
		'responseError' : function(rejection) {
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
} ]);
