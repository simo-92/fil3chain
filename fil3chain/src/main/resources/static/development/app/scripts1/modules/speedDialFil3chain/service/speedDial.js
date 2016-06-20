(function() {
  'use strict';
  /**
  * Config
  */
  var moduleName = 'service.speedDial.fil3chain';
  /**
  * Module
  */
  var module;
  try {
    module = angular.module(moduleName);
  } catch(err) {
    // named module does not exist, so create one
    module = angular.module(moduleName, ['state.filters.speedDial.fil3chain']);
  }

  module.factory('SpeedDialService', ['$interpolate', '$state','$q','StateSpeedDialFilter', function($interpolate, $state, $q, StateSpeedDial) {
    var SpeedDialService = {};
    var speedDialConfigs= [
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
    SpeedDialService.get = function(state){
      var deferred = $q.defer();
      var filtered = StateSpeedDial(speedDialConfigs, state);
      console.log('FabController','filtered State',state, filtered);
      if(filtered.length>0){
        deferred.resolve(filtered[0]);
      }else{
        deferred.reject('SpeedDial State not found');
      }
      return deferred.promise;
    }
    return SpeedDialService;

  }]);

})();
