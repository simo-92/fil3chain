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


  $stateProvider
  .state('welcome', {
    url: '/welcome',
    templateUrl: 'views/welcome/welcome.html',
    controller:'welcomeCtrl'
  })
  .state('welcome.signin', {
    url: "/signin",
    templateUrl: 'views/welcome/signin.html',
    controller: 'signinCtrl'
  })
  .state('welcome.signup', {
    url: "/signup",
    templateUrl: 'views/welcome/signup.html',
    controller:'signupCtrl'
  })

  .state('app', {
    abstract: true,
    url: '/app',
    templateUrl: 'views/application.html',
    controller: 'applicationCtrl'
  })
  .state('app.dashboard', {
    url: '/dashboard',
    controller: 'applicationCtrl',
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
  .state('app.copyright', {
    url: '/copyright',
    templateUrl: 'views/copyright/copyright.html',
    controller: 'copyrightCtrl'
  })
  .state('app.wallet', {
    url: '/wallet',
    templateUrl: 'views/wallet/wallet.html',
    controller: 'copyrightCtrl'
  })

  $urlRouterProvider.otherwise("/welcome");

})
.run(['$rootScope','contextService',function($rootScope,contextService){
  console.log('Run function');
  console.log('ContextService',contextService);
  contextService.post('user',{name:'String',surname:'String',username:'String',password:'String'});
  contextService.post('page',{state:'home',link:'/home',models:['user']});
  contextService.post('welcomeFab',{
    actions:[
      { name: "Signin", icon: "icons/ic_person_black_24px.svg", direction: "left",sref:"welcome.signin" },
      { name: "Signup", icon: "icons/ic_person_add_black_24px.svg", direction: "left",sref:"welcome.signup" }
    ]
  });
  console.log('Context',contextService.get());
  //$rootScope.
}])
.factory('context', ['$rootScope',function($rootScope) {
  $rootScope.context = {};
  return   $rootScope.context;
}])
.service('contextService',['context', function(context) {

  this.get = function(name){
    if(name) return context[name];
    return context;
  };
  this.post=function(name, model){
    return context[name]=model;
  };
}])
.directive('apsUploadFile', apsUploadFile);

function apsUploadFile() {
  var directive = {
    restrict: 'E',
    template: '<input id="fileInput" type="file" class="ng-hide"> <md-button id="uploadButton" class="md-raised md-primary" aria-label="attach_file">    Choose file </md-button><md-input-container  md-no-float>    <input id="textInput" ng-model="fileName" type="text" placeholder="No file chosen" ng-readonly="true"></md-input-container>',
    link: apsUploadFileLink
  };
  return directive;
}

function apsUploadFileLink(scope, element, attrs) {
  var input = angular.element((element[0]).querySelector('#fileInput'));
  var button =  angular.element((element[0]).querySelector('#uploadButton'));
  var textInput =  angular.element((element[0]).querySelector('#textInput'));

  if (input.length && button.length && textInput.length) {
    button.on('click',function(e) {
      input.click();
    });
    textInput.on('click',function(e) {
      input.click();
    });
  }

  input.on('change', function(e) {
    var files = e.target.files;
    if (files[0]) {
      scope.fileName = files[0].name;
    } else {
      scope.fileName = null;
    }
    scope.$apply();
  });
};
