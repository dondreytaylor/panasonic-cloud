'use strict';


// Declare app level module which depends on filters, and services
angular.module('emitapp', [
	'ngAnimate',
	'ngRoute',
	'emitapp.directives',
	'emitapp.controllers',
])

.config(['$locationProvider', '$interpolateProvider', '$routeProvider', function($locationProvider, $interpolateProvider, $routeProvider)
{
	$routeProvider
		.when('/', { 
			templateUrl: "/view/login"
		})
		.when('/simulator/oximeter', { 
			templateUrl: "/view/simulator/oximeter"
		})
		.when('/forgot', { 
			templateUrl: "/view/forgot"
		})
		.when('/dashboard', { 
			templateUrl: "/view/dashboard/index"
		})
		.when('/dashboard/vitals', { 
			templateUrl: "/view/dashboard/vitals"
		})
		.when('/dashboard/alerts', { 
			templateUrl: "/view/dashboard/alerts"
		})
		.when('/dashboard/profile', { 
			templateUrl: "/view/dashboard/profile"
		})
		.when('/dashboard/profile/update', { 
			templateUrl: "/view/profile/update"
		})
		.when('/dashboard/profile/guardian', { 
			templateUrl: "/view/profile/guardian"
		})
		.when('/dashboard/profile/guardianlist', { 
			templateUrl: "/view/profile/guardianlist"
		})
		.when('/dashboard/profile/physician', { 
			templateUrl: "/view/profile/physician"
		})
		.when('/dashboard/profile/physicianlist', { 
			templateUrl: "/view/profile/physicianlist"
		})
		.when('/dashboard/profile/password', { 
			templateUrl: "/view/profile/password"
		})
		.when('/dashboard/contacts', { 
			templateUrl: "/view/dashboard/contacts"
		})


	$interpolateProvider.startSymbol('[[').endSymbol(']]');
	$locationProvider.html5Mode(true);
}])

.run(['$rootScope', '$location', function($rootScope, $location) {
	$rootScope.redirect = function(path) { 
		window.location.href = path;
	}; 

	$rootScope.path = function(path) { 
		$location.path(path);
	};
}])
