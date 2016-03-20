'use strict';

// http://www.sitepoint.com/creating-crud-app-minutes-angulars-resource/

angular.module('myApp').factory('Apartment', [ '$resource', function($resource) {

	return $resource('/api/:id', {
		id : '@id'
	}, {
		query : {
			isArray : false,
			params : {
				size : 200000,
				page : '@page'
			}
		}
	});

} ]);