'use strict';

/**
* @ngdoc overview
* @name blockchain
* @description
* # blockchain
*
* Main module of the application.
*/
angular
.module('blockchain', [
  'ngAnimate',
  'ngAria',
  'ngMaterial',
  'ngMessages',
  'ngResource',
  'ngResource',
  'ngRoute'
])
.config(function ($routeProvider) {
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
});
