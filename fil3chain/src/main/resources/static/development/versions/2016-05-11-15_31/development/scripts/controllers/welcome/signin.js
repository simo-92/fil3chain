'use strict';

/**
* @ngdoc function
* @name blockchain.controller:AboutCtrl
* @description
* # AboutCtrl
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
  /*
  var p2 = Promise.resolve(fabService.get());
  p2.then(function(fab) {
      console.log('Fab',fab);
      fab.scope.click = 'Ivan'
  }, function(e) {
    console.log(e); // TypeError: Throwing
  });
  */
}]);
