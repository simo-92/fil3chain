(function () {
  'use strict';

  angular
  .module('blockchainApp')
  .controller('filtersAppDashboardCtrl', FiltersCtrl);

  FiltersCtrl.$inject =
  ['$scope','$http', '$cookies', '$rootScope', '$timeout','$q', 'UserService','navbarService'];
  function FiltersCtrl($scope,$http, $cookies, $rootScope, $timeout, $q, UserService, UserValidatorService) {

    $scope.filters = [
      {
        label: "Wallet",
        icon: "icons/ic_person_black_24px.svg",
        sref:"wallet",
        click:function(config){
          console.log('filtersAppDashboardCtrl','in actionClicked this', this);
          console.log('filtersAppDashboardCtrl','in actionClicked state', config);
          config.go(this.sref);
        }
      },
      {
        label: "Signup",
        icon: "icons/ic_person_add_black_24px.svg",
        sref:"welcome.signup",
        click:function(config){
          console.log('filtersAppDashboardCtrl','in actionClicked this', this);
          console.log('filtersAppDashboardCtrl','in actionClicked state', config);
          config.go(this.sref);
        }
      }
    ];


  }


})();
