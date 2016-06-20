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
      'config.service.speedDial.fil3chain',
      'service.speedDial.fil3chain',
      'state.filters.speedDial.fil3chain'
    ]);
  }
  module.factory('SpeedDialService', SpeedDialService);

  SpeedDialService.$inject = ['$log','$state','$q','$rootScope','StateSpeedDialFilter','SpeedDialConfigService'];

  function SpeedDialService($log, $state, $q,$rootScope,  StateSpeedDial, SpeedDialConfigService) {
    $log.debug('SpeedDialService');
    var SpeedDialService={};
    var config={}

    SpeedDialService.get = function(){
        return config;
    }

    SpeedDialService.set = function(state){
      if(state){
        config = StateSpeedDial(SpeedDialConfigService, state)[0];
      }else{
        config = StateSpeedDial(SpeedDialConfigService, 'default')[0];
      }
      console.log('ooooooooooo',SpeedDialService.config);
      return config;
    }
    return SpeedDialService;

  };

})();
