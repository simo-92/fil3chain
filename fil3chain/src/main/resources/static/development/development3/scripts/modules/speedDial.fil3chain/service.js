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
    module = angular.module(moduleName, [
      'config.speedDial.fil3chain',
      'state.filters.speedDial.fil3chain'
    ]);
  }
  module.factory('SpeedDialService', SpeedDialService);

  SpeedDialService.$inject = [
    '$log',
    'SpeedDialConfig',
    'StateSpeedDialFilter'
  ];

  function SpeedDialService($log, SpeedDialConfig, StateSpeedDialFilter) {
    $log.debug('SpeedDialService');
    var config = {};

    return{
      get:function(){
        $log.debug('SpeedDialService','set',scope)
        return config;
      },
      set : function(state){
        if(!state){
          //config = StateSpeedDialFilter(SpeedDialConfig, 'default')[0];
        }else{
          $log.debug('SpeedDialService','set')
          config = StateSpeedDialFilter(SpeedDialConfig, state)[0];
        }
        $log.debug('SpeedDialService','set','StateSpeedDialFilter',config)
      }
    }

  };
})();
