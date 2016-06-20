(function () {
  'use strict';

  angular
  .module('blockchainApp')
  .factory('FabService', FabService);

  FabService.$inject = ['$http', '$timeout','$q'];
  function FabService($http, $timeout, $q) {
    var service = {};
    var speedDial={};

    service.setSpeedDial = SetSpeedDial;
    service.getSpeedDial = GetSpeedDial;

    service.setSpeedDialFabTrigger = SetSpeedDialFabTrigger;
    service.getSpeedDialFabTrigger = GetSpeedDialFabTrigger;

    service.setSpeedDialFabActions = SetSpeedDialFabActions;
    service.getSpeedDialFabActions = GetSpeedDialFabActions;
    return service;

    function GetSpeedDial(){
      return speedDial;
    }
    function SetSpeedDial(speedDialConfig){
      speedDial = speedDialConfig;
    }
    function SetSpeedDialFabTrigger(fabTrigger){
      var deferred = $q.defer();
      speedDial.fabTrigger = fabTrigger;
      deferred.resolve(GetSpeedDialFabTrigger());
      return deferred.promise;
    }
    function GetSpeedDialFabTrigger(){
      var deferred = $q.defer();
      if(!speedDial.fabTrigger){
        deferred.reject('SpeedDial FabTrigger not found');
      }else {
        deferred.resolve(speedDial.fabTrigger);
      }
      return deferred.promise;
    }



    function SetSpeedDialFabActions(fabActions){
      var deferred = $q.defer();
      speedDialfabActions = fabActions;
      deferred.resolve(GetSpeedDialFabActions());
      return deferred.promise;
    }
    function GetSpeedDialFabActions(){
      var deferred = $q.defer();
      if(!speedDial.fabActions){
        deferred.reject('SpeedDial FabActions not founds');
      }else {
        deferred.resolve(speedDial.fabActions);
      }
      return deferred.promise;
    }

    function addSpeedDialFabAction(action){

    }
    function removeSpeedDialFabAction(action){

    }
    function updateSpeedDialFabAction(action){

    }

    return service;
  }


})();
