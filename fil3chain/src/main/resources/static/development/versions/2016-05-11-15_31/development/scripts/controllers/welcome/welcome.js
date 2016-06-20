'use strict';

/**
* @ngdoc function
* @name blockchain.controller:AboutCtrl
* @description
* # AboutCtrl
* Controller of the blockchain
*/
angular.module('blockchainApp')

.controller('welcomeCtrl', ['$scope','$rootScope','$urlRouter','$location','$state','$mdDialog', '$timeout','fabService',function ($scope,$rootScope,$urlRouter,$location,$state,$mdDialog, $timeout,fabService) {
  console.log('welcomeCtrl');
  console.log('welcomeCtrl','scope',$scope);

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

  $scope.items = [
    { name: "Signin", icon: "images/ic_power_settings_new_black_24px.svg", direction: "left",sref:"welcome.signin" },
    { name: "Signup", icon: "images/ic_power_settings_new_black_24px.svg", direction: "left",sref:"welcome.signup" }
  ];

  $scope.openDialog = function($event, item) {
    // Show the dialog
    $mdDialog.show({
      clickOutsideToClose: true,
      controller: function($mdDialog) {
        // Save the clicked item
        this.item = item;

        // Setup some handlers
        this.close = function() {
          $mdDialog.cancel();
        };
        this.submit = function() {
          $mdDialog.hide();
        };
      },
      controllerAs: 'dialog',
      templateUrl: 'dialog.html',
      targetEvent: $event
    });
  }
  //availableModes = ['md-fling', 'md-scale'];
  $scope.fab={
    isOpen: false,
    selectedDirection:'up',
    selectedMode : 'md-fling'
  }

  // var fabActions ={
  //   signin:,
  //   signup
  // }
  function signin(){
    console.log('signin Function');
  }
  function signup(){
    console.log('signup Function');
  }
  function initFab(){
    console.log('initFab Function');
  };
  var fabActionsConfig={
    '/signin': signin,
    '/signup':  signup,
    '/welcome': initFab
  }
  var state = $state.current;
  $scope.$watch('state', function() {
    console.log('hey, path'+state+'has changed!');
    console.log(fabActionsConfig,state);
    $scope.fabClick = fabActionsConfig[state.url];
  });

  function fabClick(path){
    console.log('Fab clicked from signinCtrl');
    console.log($location.path());
  };
  $scope.fabClick = fabClick;


  $rootScope.$on('$stateChangeStart', function (event, toState,   toParams
    , fromState, fromParams)
    {
      console.log($location.path(),$state.current);

      console.log(event)
      console.log(toState)
      console.log('-----',fabActionsConfig,toState.url);
      $scope.fabClick = fabActionsConfig[toState.url];

      console.log(toParams)
      console.log(fromState)
      console.log(fromParams)
      console.log(toState)

    });
    //console.log(fabService.get());.

  }]);
