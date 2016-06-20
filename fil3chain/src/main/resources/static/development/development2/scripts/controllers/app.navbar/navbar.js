(function () {
  'use strict';

  angular
  .module('blockchainApp')
  .controller('NavbarAppCtrl', NavbarCtrl);

  NavbarCtrl.$inject =
  ['$scope','$http', '$state', '$rootScope', '$timeout','$q', 'UserService','navbarService'];
  function NavbarCtrl($scope,$http, $state, $rootScope, $timeout, $q, UserService, UserValidatorService) {

    $scope.filters = [
      {
        name: "Wallet",
        icon: "icons/ic_person_black_24px.svg",
        sref:"wallet",
        click:function(config){
          console.log('filtersAppDashboardCtrl','in actionClicked this', this);
          console.log('filtersAppDashboardCtrl','in actionClicked state', config);
          config.go(this.sref);
        }
      },
      {
        name: "Blockchain",
        icon: "icons/ic_person_add_black_24px.svg",
        sref:"blockchain",
        click:function(config){
          console.log('filtersAppDashboardCtrl','in actionClicked this', this);
          console.log('filtersAppDashboardCtrl','in actionClicked state', config);
          config.go(this.sref);
        }
      }
    ];

    console.log('STATE',$state,$scope.filters);
    $scope.title = $state.current.label
  };
}

)();
