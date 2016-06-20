(function () {
  'use strict';

  angular
  .module('blockchainApp')
  .controller('FabAppCtrl', FabCtrl);

  FabCtrl.$inject =
  ['$scope','$rootScope','$state','$q','$filter','FabService','FabStateFilter','ExactMatchFilter'];
  function FabCtrl($scope,$rootScope,$state,$q,$filter, FabService,FabStateFilter,ExactMatchFilter) {
    console.log('FabAppController','FabService',FabService);

    var speedDialConfigs=[
      {
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
      },
      {
        states: ['app.dashboard1'],
        show:"true",
        direction:"up",
        class:"md-scale md-fab-bottom-right",
        ngClass:"{ 'md-hover-full': hover }",
        mouseenter:"fab.open=true",
        mouseleave:"fab.open=false",
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
      }
    ];
    console.log('FabController','initial State',$state );
    // console.log('FabController','filtered State',ExactMatchFilter(speedDialConfigs, { states: $state.current.name }));
    var filtered = FabStateFilter(speedDialConfigs, $state.current.name);
    console.log('FabController','filtered State', filtered);
    if(filtered.length>0){

      FabService.setSpeedDial(FabStateFilter(speedDialConfigs, $state.current.name)[0])
      console.log('FabController','after set speedDial', FabService.getSpeedDial() );

      $scope.speedDial = FabService.getSpeedDial()

    }else{
      $scope.speedDial = { show: false};
    }
    /*
    FabService.getSpeedDial()
    .then(function(success){
    console.log('Success Get speedDial',success);
  },function(error){
  console.log('Error Get speedDial',error);
})
*/

$scope.actionClick = function(action){
  console.log('FabController','actionClick', action );
  if(action.click)action.click($state)
}
}

})();
