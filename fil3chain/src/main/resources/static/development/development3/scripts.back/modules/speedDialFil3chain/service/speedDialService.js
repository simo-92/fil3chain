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
    module = angular.module(moduleName, ['config.service.speedDial.fil3chain','state.filters.speedDial.fil3chain']);
  }

  module.factory('SpeedDialService', ['$interpolate', '$state','$q','SpeedDialConfigService','StateSpeedDialFilter',

  function($interpolate, $state, $q, SpeedDialConfigService, StateSpeedDial) {
    console.log("SSSSSSSSSSSSS",SpeedDialConfigService);
    var SpeedDialService = {};
    var SpeedDial = SpeedDialConfigService
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
