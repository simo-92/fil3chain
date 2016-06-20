(function () {
  'use strict';

  angular
  .module('blockchainApp')
  .controller('SidenavAppCtrl', SidenavCtrl);

  SidenavCtrl.$inject =
  ['$scope','$http', '$state', '$rootScope', '$timeout','$q', 'UserService','UserValidatorService'];
  function SidenavCtrl($scope,$http, $state, $rootScope, $timeout, $q, UserService, UserValidatorService) {

    var sidenav=[
      {
        state : 'app.dashboard',
        label: 'Dashboard',
        icon: 'icons/ic_dashboard_black_24px.svg',
        click: function(config){
          config.go(this.state)
        }
      },
      {
        state : 'app.copyright',
        label: 'Copyright',
        icon: 'icons/ic_copyright_black_24px.svg',
        click: function(config){
          config.go(this.state)
        }
      },
      {
        state : 'app.statistics',
        label: 'Statistics',
        icon: 'icons/graph_black.svg',
        click: function(config){
          config.go(this.state)
        }
      },
      {
        state : 'app.wallet',
        label: 'Wallet',
        icon: 'icons/user_black.svg',
        click: function(config){
          config.go(this.state)
        }
      }
    ];

    $scope.sidenavItems = sidenav;
    $scope.sidenavVisibility = true;
    $scope.click = function(sidenavItem){
      sidenavItem.click($state);
    }
  }


})();
