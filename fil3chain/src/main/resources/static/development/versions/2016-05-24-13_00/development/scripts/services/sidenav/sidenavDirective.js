'use strict';

/**
* @ngdoc overview
* @name testComponentApp
* @description
* # testComponentApp
*
* Main module of the application.
*/
angular
.module('blockchainApp')
.directive('sidenav',['$rootScope','$state','sidenavService', 'navbarService',function($rootScope, $state, sidenavService, navbarService) {

  return {
    restrict: 'E', // restrict by class name
    replace: true,
    templateUrl:'scripts/services/sidenav/sidenav.tpl.html',
    link: function(scope, element, attrs) {
      console.log(sidenavService,navbarService);
      navbarService.get().title= $state.current.label;

      scope.sidenavItems = sidenavService.get();
      scope.sidenavVisibility = sidenavService.getVisibility();
      
      scope.changeState = function(navItem){
        console.log(navItem);
        navbarService.get().title= navItem.label;
        $state.go(navItem.state);
      }

    }
  };
}])
