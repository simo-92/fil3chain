'use strict';

/**
* @ngdoc function
* @name blockchain.controller:AboutCtrl
* @description
* # AboutCtrl
* Controller of the blockchain
*/
angular.module('blockchainApp')

.controller('welcomeCtrl', ['$scope','$rootScope','$urlRouter','$location','$state','$mdDialog', '$timeout',function ($scope,$rootScope,$urlRouter,$location,$state,$mdDialog, $timeout) {
  console.log('welcomeCtrl');
  console.log('welcomeCtrl','scope',$scope);
  $scope.fab={};
  $scope.items = [
    { name: "Signin", icon: "images/ic_power_settings_new_black_24px.svg", direction: "left",sref:"welcome.signin" },
    { name: "Signup", icon: "images/ic_power_settings_new_black_24px.svg", direction: "left",sref:"welcome.signup" }
  ];

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
