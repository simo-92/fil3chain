(function () {
  'use strict';

  angular
  .module('blockchainApp')
  .controller('FabWelcomeCtrl', FabCtrl);

  FabCtrl.$inject =
  ['$scope','$rootScope','$state','$q','$filter','FabService','FabStateFilter','ExactMatchFilter'];
  function FabCtrl($scope,$rootScope,$state,$q,$filter, FabService,FabStateFilter,ExactMatchFilter) {
    console.log('FabWelcomeController','FabService',FabService);

    var speedDialConfigs={
      states: ['welcome','welcome.signin','welcome.signup'],
      show:"true",
      direction:"up",
      class:"md-scale md-fab-bottom-right",
      ngClass:"{ 'md-hover-full': hover }",
      fabTrigger:{
        label:'Menu',
        icon:'icons/ic_menu_white_24px.svg',
        tooltip : {
          direction:'left',
          visible:'true',
          autohide:'false',
          label:'Menu'
        }
      },
      fabActions:[
        {
          name: "Signin",
          icon: "icons/ic_person_black_24px.svg",
          direction: "left",
          sref:"welcome.signin",
          click:function(config){
            console.log('FabController','in actionClicked this', this);
            console.log('FabController','in actionClicked state', config);
            config.go(this.sref);
          }
        },
        {
          name: "Signup",
          icon: "icons/ic_person_add_black_24px.svg",
          direction: "left",
          sref:"welcome.signup",
          click:function(config){
            console.log('FabController','in actionClicked this', this);
            console.log('FabController','in actionClicked state', config);
            config.go(this.sref);
          }
        }
      ]
    };

    $scope.speedDial = speedDialConfigs;

    $scope.actionClick = function(action){
      console.log('FabController','actionClick', action );
      if(action.click)action.click($state)
    }
  }

})();
