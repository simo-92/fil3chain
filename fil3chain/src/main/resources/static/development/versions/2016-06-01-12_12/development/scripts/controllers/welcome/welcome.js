'use strict';

/**
* @ngdoc function
* @name blockchain.controller:AboutCtrl
* @description
* # AboutCtrl
* Controller of the blockchain
*/
angular.module('blockchainApp')

.controller('welcomeCtrl', ['$scope','$rootScope','$urlRouter','$location','$state','$mdDialog', '$timeout','contextService',function ($scope,$rootScope,$urlRouter,$location,$state,$mdDialog, $timeout,contextService) {
  console.log('welcomeCtrl');
  console.log('welcomeCtrl','scope',$scope);
  console.log(contextService.get('welcomeFab'));
  $scope.fab = contextService.get('welcomeFab');

  $scope.hidden = false;
  $scope.isOpen = false;
  $scope.hover = false;

  // On opening, add a delayed property which shows tooltips after the speed dial has opened
  // so that they have the proper position; if closing, immediately hide the tooltips
  $scope.$watch('demo.isOpen', function(isOpen) {
    if (isOpen) {
      $timeout(function() {
        $scope.tooltipVisible = $scope.isOpen;
      }, 600);
    } else {
      $scope.tooltipVisible = $scope.isOpen;
    }
  });


  }]);
