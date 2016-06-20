'use strict';

/**
* @ngdoc function
* @name blockchain.controller:signinCtrl
* @description
* # signinCtrl
* Controller of the blockchain
*/

angular.module('blockchainApp')
.controller('signinCtrl', ['$scope','$mdToast','$state','AuthenticationService',function ($scope,$mdToast,$state,AuthenticationService) {
  console.log('signinCtrl');
  $scope.user={};
  $scope.signin = function(user){
    console.log('signin clicked from signinCtrl',$scope.user);
    AuthenticationService.signin($scope.user)
    .then(function(user){
      console.log('Signin','result',user);
      AuthenticationService.SetCredentials(user.username,user.password);
      $state.go('app.dashboard');
    },function(message){
      $mdToast.show(
        $mdToast.simple()
        .textContent(message)
        .position('fil')
        //.position($scope.getToastPosition())
        .hideDelay(5000)
      );
    })
  };;
  //console.log('signinCtrl','scope',$scope);
}]);
