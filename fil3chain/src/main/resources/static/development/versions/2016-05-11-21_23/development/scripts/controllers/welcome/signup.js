'use strict';

/**
* @ngdoc function
* @name blockchain.controller:signupCtrl
* @description
* # signupCtrl
* Controller of the blockchain
*/
angular.module('blockchainApp')
.controller('signupCtrl', ['$scope',function ($scope) {
  console.log('signupCtrl');
  $scope.signup = function(){
    console.log('click by signupCtrl');
  }
}]);
