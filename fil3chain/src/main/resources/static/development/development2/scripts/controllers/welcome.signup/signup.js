'use strict';

/**
* @ngdoc function
* @name blockchain.controller:signupCtrl
* @description
* # signupCtrl
* Controller of the blockchain
*/
angular.module('blockchainApp')
.controller('signupCtrl', ['$scope','$mdToast','$state','AuthenticationService','UserValidatorService',function ($scope,$mdToast,$state,AuthenticationService,UserValidatorService) {
  console.log('signupCtrl',AuthenticationService);
  $scope.signup = function(user){
    console.log('click by signupCtrl',user);
    
    AuthenticationService.signup(user)
    .then(function(user){
      console.log('signupCtrl','signup',user);
      $state.go('app.dashboard');
    },function(response){

      $mdToast.show(
        $mdToast.simple()
        .textContent(response)
        .position('fil')
        //.position($scope.getToastPosition())
        .hideDelay(5000)
      );
    })
  }
}]);
