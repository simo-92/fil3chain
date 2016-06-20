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
.module('blockchainApp', [
  'ngAnimate',
  'ngAria',
  'ngMaterial',
  'ngMessages',
  'ngResource',
  'ngResource',
  'ngRoute',
  'ui.router'
])
.config(function($stateProvider, $urlRouterProvider) {
  $urlRouterProvider.otherwise("/welcome");

  $stateProvider
  .state('welcome', {
    url: '/welcome',
    templateUrl: 'views/welcome/welcome.html',
    controller:'welcomeCtrl'
    //templateUrl: 'views/welcome/welcome.html'
  })
  .state('welcome.signin', {
    url: "/signin",
    templateUrl: 'views/welcome/signin.html',
    controller: 'signinCtrl'
  })
  .state('welcome.signup', {
    url: "/signup",
    templateUrl: 'views/welcome/signup.html'
  })



  .state('app', {
    abstract: true,
    url: '/app',
    templateUrl: 'views/application.html'
  })

  /* WELCOME ROUTING */
  .state('app.welcome', {

    url: '/welcome',
    views: {
      'signin': {
        templateUrl: 'views/welcome/signin.html'
      },
      'signup': {
        templateUrl: 'views/welcome/signup.html'
      }
    },
    templateUrl: 'views/welcome/welcome.html'
  })

  .state('app.dashboard', {
    url: '/welcome1',
    views: {
      'signin': {
        templateUrl: 'views/welcome/signin.html'
      },
      'signup': {
        templateUrl: 'views/welcome/signup.html'
      }
    }
  })

})
.run(['fabService',function(fabService){
  console.log('Run function');
  console.log(fabService.get());
  //fabService.post(document.querySelector('md-fab-speed-dial'));
}])

.service('fabService', function() {
  var fabElement;
  var scope;

  function get(){
    return {
      elemet:fabElement,
      scope:scope
    };
  }
  function post(newFabElement,newScope){
    fabElement = newFabElement;
    scope= newScope;
  }
  function getScope(){
    return scope;
  }
  return{
    get:get,
    getScope: getScope,
    post:post
  }
})
.directive('fab', ['fabService',function factory(fabService) {
  return{
    restrict: 'A',
    compile: function(tElem, tAttrs){
      return{
        pre:function(scope, iElement, iAttrs){
          console.log('Pre');
        },
        post:function(scope, iElement, iAttrs){
          console.log('Post');
          fabService.post(iElement,scope);
        }
      }
    }
    /*
    link:function(scope,element,attrs){
      console.log(scope,element,attrs);
      fabService.post(element,scope);
      console.log(fabService.get());
    }
    */
  }
}]);
/*
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
*/
