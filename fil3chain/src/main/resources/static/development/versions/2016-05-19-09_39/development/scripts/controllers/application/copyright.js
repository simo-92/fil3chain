'use strict';

/**
 * @ngdoc function
 * @name blockchain.controller:copyrightCtrl
 * @description
 * # copyrightCtrl
 * Controller of the blockchain
 */
angular.module('blockchainApp')
  .controller('copyrightCtrl',['$scope','sha256Service', function ($scope,sha256Service) {
    console.log('copyrightCtrl');
    console.log(sha256Service);
    $scope.messaggio="";
    $scope.sha = function(messaggio){
      console.log(sha256Service.hash(messaggio));
    }


  }]);
