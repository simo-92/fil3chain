'use strict';

/**
 * @ngdoc function
 * @name testComponentApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the testComponentApp
 */
angular.module('testComponentApp')
.controller('MainCtrl', function ($scope,$http,$location) {
	$scope.resource = function() {
		$http.get('/resource/').success(function(data) {
			$scope.greeting = data;
		});		
	}	


	$scope.logout = function() {

		$http.post('logout', {}).success(function() {
			$scope.authenticated = false;
			$location.path("/");
		}).error(function(data) {
			$scope.authenticated = false;
		});
	}

	function authenticate(credentials, callback) {
		console.log('Authenticate',credentials, btoa(credentials.password))
		var headers = credentials ? {authorization : "Basic "
			+ btoa(credentials.username + ":" + credentials.password)
		} : {};

		$http.get('user', {headers : headers}).success(function(data) {
			if (data.name) {
				$scope.authenticated = true;
			} else {
				$scope.authenticated = false;
			}
			callback && callback();
		}).error(function() {
			$scope.authenticated = false;
			callback && callback();
		});

	}

	//authenticate();
	$scope.user = {};
	$scope.submit = function(user) {
		console.log(user)

		authenticate(user, function() {
			if ($scope.authenticated) {
				$location.path("/");
				$scope.error = false;
			} else {
				$location.path("/login");
				$scope.error = true;
			}
		});
	};
});

