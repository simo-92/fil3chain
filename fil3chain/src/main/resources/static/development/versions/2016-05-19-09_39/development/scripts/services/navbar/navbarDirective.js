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
.directive('navbar',['$rootScope','$state','$filter','ExactMatchFilter','navbarService', function($rootScope, $state, $filter,ExactMatchFilter,navbarService) {

  return {
    restrict: 'E', // restrict by class name
    scope: {},
    templateUrl:'scripts/services/navbar/navbar.tpl.html',
    link: function(scope, element, attrs) {
      console.log('NavbarDirective linked',navbarService,$state);
      scope.navbar = navbarService.get();
      // scope.title = '$state.current.label';
      console.log('Title',$state.current.label);
      /*
      $rootScope.$on('$stateChangeStart',
      function(event, toState, toParams, fromState, fromParams){
        // do something
        console.log('to state',toState.label);
        scope.title = toState.label;
        scope.apply;
      })
      */
      $rootScope.$on('$stateChangeSuccess',
      function(event, toState, toParams, fromState, fromParams){
        // do something
        console.log(toState.name);
        console.log('Filter',ExactMatchFilter(navbarService.get().links, { state: toState.name }))
        var checkedState = ExactMatchFilter(navbarService.get().links, { state: toState.name });
        if(checkedState.length===1){
          scope.navbar.title = checkedState[0].label;
          $state.go(checkedState[0].state);
        }
      })

      if( scope.navbar.links.length>0){
        scope.selectedIndex = 0;
        scope.$watch('selectedIndex', function(current, old) {
          $state.go(scope.navbar.links[current].state);
        })
      }
    }
  };
}])
