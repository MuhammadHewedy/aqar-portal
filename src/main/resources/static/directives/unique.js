// http://blog.brunoscopelliti.com/form-validation-the-angularjs-way/

'use strict';

/**
 * 4 patterns:
 * 
 * <input unique />
 * 
 * <input unique="object.property" />
 * 
 * <input uniqueId="{{object.id}}" />
 * 
 * <input unique="object.property" uniqueId="{{object.id}}" />
 * 
 * **Where**:
 * 
 * object : should match java entity object name
 * 
 * property : should match java entity property name
 * 
 * id : should match java entity id property
 * 
 * 
 * **Notes**:
 * 
 * use the first form if scope object, field and id fields match of java entity,
 * you case override the value you need with the corresponding pattern.
 */

angular.module('myApp').directive('unique', [ '$http', function($http) {
	return {
		require : "ngModel",
		restrict : 'A',
		link : function(scope, elem, attrs, ctrl) {

			scope.$watch(attrs.ngModel, function() {

				ctrl.$setValidity('unique', true);

				var ngModelValue = attrs.unique;
				if (!ngModelValue) {
					ngModelValue = attrs.ngModel;
				}

				var idValue = attrs.uniqueId;
				if (!idValue) {
					var obj = scope[ngModelValue.split('.')[0]];
					if (obj) {
						idValue = scope[ngModelValue.split('.')[0]].id;
					}
				}

				$http({
					method : 'GET',
					url : 'api/unique',
					params : {
						name : ngModelValue,
						value : elem.val(),
						ignore : idValue
					}
				}).then(function(response) {
					console.log('unique check returned success, ' + response.status);
					ctrl.$setValidity('unique', true);
				}, function(error) {
					console.log('unique check returned error, ' + error.status)
					ctrl.$setValidity('unique', false);
				});

			});
		}
	}
} ]);