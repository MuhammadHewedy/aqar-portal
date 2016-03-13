'use strict';

// http://www.sitepoint.com/creating-crud-app-minutes-angulars-resource/

angular.module('myApp').factory('User', [ '$resource', function($resource) {

	return $resource('/api/users/:id', {
		id : '@id'
	}, {
		update : {
			method : 'PUT'
		},
		query : {
			isArray : false,
			params : {
				page : '@page'
			}
		}
	});

} ]);