'use strict';

/**
 * @ngdoc function
 * @name blockchain.controller:applicationCtrl
 * @description
 * # applicationCtrl
 * Controller of the blockchain
 */
angular.module('blockchainApp')
  .controller('applicationCtrl',['$scope', function ($scope) {
    $scope.menu = [
      {
        state : 'app.dashboard',
        label: 'Dashboard',
        icon: 'icons/ic_dashboard_black_24px.svg'
      },
      {
        state : 'app.copyright',
        label: 'Copyright',
        icon: 'icons/ic_copyright_black_24px.svg'
      },
      {
        state : 'app.statistics',
        label: 'Statistics',
        icon: 'icons/graph_black.svg'
      },
      {
        state : 'app.wallet',
        label: 'Wallet',
        icon: 'icons/user_black.svg'
      }
    ];
  
  }]);
