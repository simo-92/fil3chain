'use strict';

/**
* @ngdoc function
* @name blockchain.controller:signinCtrl
* @description
* # signinCtrl
* Controller of the blockchain
*/

angular.module('blockchainApp')
.controller('signinCtrl', ['$scope',function ($scope) {
  console.log('signinCtrl');
  function signin(){
    console.log('signin clicked from signinCtrl');
  };
  $scope.signin= signin;
  console.log('signinCtrl','scope',$scope);
}]);
