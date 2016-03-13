'use strict';

// http://www.sitepoint.com/creating-crud-app-minutes-angulars-resource/

angular.module('myApp').factory('Account', [ '$resource', function($resource) {

	return $resource('/api/accounts/:id', {
		id : '@id'
	}, {
		update : {
			method : 'PUT'
		},
		query : {
			isArray : false,
			params : {
				page : '@page',
				size : '@size'
			}
		}
	});

} ]);