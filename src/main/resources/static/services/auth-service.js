'use strict';

angular.module('myApp').factory('AuthService', [ '$http', '$q', 'Base64Service', function($http, $q, Base64Service) {

	return {
		getLoggedInUser : function() {
			var deferred = $q.defer();

			$http.get('/api/auth/loggedInUser').then(function(response) {
				deferred.resolve(response.data);
			}, function(error) {
				deferred.reject(error.status);
			});
			return deferred.promise;
		},
		login : function(credentials) {
			var auth = 'Basic ' + Base64Service.encode(credentials.username + ':' + credentials.password);
			var deferred = $q.defer();

			$http({
				method : 'POST',
				url : '/api/auth/login',
				headers : {
					'Authorization' : auth
				}
			}).then(function(response) {
				deferred.resolve(response.data);
			}, function(error) {
				deferred.reject(error.data.message);
			})
			return deferred.promise;
		},
		logout : function() {
			var deferred = $q.defer();

			$http.get('/api/auth/logout').then(function(response) {
				deferred.resolve();
			});
			return deferred.promise;
		}
	}

} ]);