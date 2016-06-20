(function() {
  'use strict';
  /**
  * Config
  */
  var moduleName = 'config.speedDial.fil3chain';
  /**
  * Module
  */
  var module;
  try {
    module = angular.module(moduleName);
  } catch(err) {
    // named module does not exist, so create one
    module = angular.module(moduleName, []);
  }
  module.factory('SpeedDialConfig', SpeedDialConfigs);

  SpeedDialConfigs.$inject = ['$log'];

  function SpeedDialConfigs($log) {
    $log.debug('SpeedDialConfigs');
    return[{
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
    }
  ]
};

})();
