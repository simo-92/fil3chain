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
  module.factory('SpeedDial', SpeedDialService);

  SpeedDialService.$inject = ['$log','SpeedDialConfig','StateSpeedDialFilter',];
  function SpeedDialService($log, SpeedDialConfig, StateSpeedDialFilter) {
    var SpeedDialService = {};

    $log.info('SpeedDialService',SpeedDialConfig, StateSpeedDialFilter)
    SpeedDialService.get = function(state){
      $log.debug('SpeedDialService','get','state',state);
      var filtered;
      if(!state)state='default';
      filtered = StateSpeedDialFilter(SpeedDialConfig, state)[0];

      if(!filtered)filtered = StateSpeedDialFilter(SpeedDialConfig, 'default')[0];
      console.log('uuuuuuuuuuu',filtered);
      return filtered

}
return SpeedDialService;
};

})();
