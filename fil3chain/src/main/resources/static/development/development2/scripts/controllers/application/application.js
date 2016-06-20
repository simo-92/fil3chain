'use strict';

/**
* @ngdoc function
* @name blockchain.controller:applicationCtrl
* @description
* # applicationCtrl
* Controller of the blockchain
*/
angular.module('blockchainApp')
.controller('applicationCtrl',['$scope','$state','sidenavService','navbarService','sha256Service','userId' ,function ($scope,$state,sidenavService,navbarService,sha256Service,userId) {
  console.log('ApplicationCtrl',userId);
  if(!userId){
    $state.go('welcome');
  }
  /*
  console.log(sha256Service.hash('ok sono ivan'));
  $scope.sidenavItems = contextService.get('sidenav');
  $scope.navbar = contextService.get('navbar');


  console.log($scope.navbar,$state, navbarService);
  $scope.navbarClick = function(action){
    console.log(action);
  }

  for (var key in p) {
    if (p.hasOwnProperty(key)) {
      alert(key + " -> " + p[key]);
    }
  }



  $scope.title = $state.current.label;
  console.log('Title',$state.current.label);

  if(  $scope.navbar.links.length>0){
    $scope.selectedIndex = 0;
    $scope.$watch('selectedIndex', function(current, old) {
      console.log($scope.navbar.links[current]);
      $state.go($scope.navbar.links[current].state)
    })
  }
  */

  //sidenavService.show();
}]);
