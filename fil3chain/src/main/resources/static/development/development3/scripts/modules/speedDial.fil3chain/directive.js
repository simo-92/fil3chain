(function() {
  'use strict';
  /**
  * Config
  */
  var moduleName = 'directive.speedDial.fil3chain';
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
      'service.speedDial.fil3chain'
    ]);
  }
  module.directive('speedDial', SpeedDialDirective);

  SpeedDialDirective.$inject = ['$log', 'SpeedDialConfig','SpeedDialService'];

  function SpeedDialDirective($log, SpeedDialConfig, SpeedDialService) {
    $log.debug('SpeedDialDirective');
    return{
      restrict:'E',
      templateUrl:'scripts/modules/speedDial.fil3chain/speedDial.tpl.html',
      scope:{},
      compile: function(element, attributes){
        return {
          pre: function(scope, element, attributes, controller, transcludeFn){
            $log.debug('speedDial','directive pre',element);
            scope.speedDial = SpeedDialService.get(scope);
            $log.debug( scope.speedDial );
          },
          post: function(scope, element, attributes, controller, transcludeFn){
            $log.debug('speedDial','directive post',scope);
            //function changeSuccessListener(event, toState, toParams, fromState, fromParams){
              //$log.info('speedDial','Link','$stateChangeSuccess',event, toState, toParams, fromState, fromParams);
               SpeedDialService.set('welcome')
            //}
            //scope.$on('$stateChangeSuccess',changeSuccessListener);
        }
      }
    },
    controller:function(){
      console.log(this);
    }
  }
  //End return
};
//End Directive
})();
