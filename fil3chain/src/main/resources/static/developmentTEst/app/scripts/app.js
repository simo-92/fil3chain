'use strict';

/**
 * @ngdoc overview
 * @name testComponentApp
 * @description
 * # testComponentApp
 *
 * Main module of the application.
 */
angular
  .module('testComponentApp', [
    'ngAnimate',
    'ngCookies',
    'ngResource',
    'ngRoute'
  ])
  .config(function ($routeProvider,$httpProvider) {
    $routeProvider
      .when('/', {
        templateUrl: 'views/main.html',
        controller: 'MainCtrl',
        controllerAs: 'main'
      })
      .when('/about', {
        templateUrl: 'views/about.html',
        controller: 'AboutCtrl',
        controllerAs: 'about'
      })
      .otherwise({
        redirectTo: '/'
      });
    $httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';
    $httpProvider.interceptors.push('myInterceptor');


  })
  .run(function run( $http, $cookies ){
	  //$http.defaults.headers.common["X-AUTH-TOKEN"] = $cookies['AUTH-TOKEN'];
	  $cookies.remove('XSRF-TOKEN');
	})
  .factory('myInterceptor', ['$q','$cookies',  function($q,$cookies) {  
    var requestInterceptor = {
        request: function(config) {
            var deferred = $q.defer();
            console.log(config.headers);
            console.log($cookies.get('XSRF-TOKEN'));
            config.headers['XSRF-TOKEN']=$cookies.get('XSRF-TOKEN');
            deferred.resolve(config);
            /*
            someAsyncService.doAsyncOperation().then(function() {
               console.log(config);
                deferred.resolve(config);
            }, function() {
                // Asynchronous operation failed, modify config accordingly
                deferred.resolve(config);
            });
            */
            return deferred.promise;
        }
    };

    return requestInterceptor;
}]);
