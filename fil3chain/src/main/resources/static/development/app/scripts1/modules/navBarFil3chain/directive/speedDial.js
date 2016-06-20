/**
* uiBreadcrumbs automatic breadcrumbs directive for AngularJS & Angular ui-router.
*
* https://github.com/michaelbromley/angularUtils/tree/master/src/directives/uiBreadcrumbs
*
* Copyright 2014 Michael Bromley <michael@michaelbromley.co.uk>'+
*/


(function() {

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
    module = angular.module(moduleName, ['ui.router','service.speedDial.fil3chain']);
  }

  module.directive('speedDial', ['$state','$q','$log','$compile','SpeedDialService', function( $state, $q, $log, $compile, SpeedDialService) {

    var template=
    '<md-fab-speed-dial ng-show="{{speedDial.show}}" md-direction="{{speedDial.direction}}" md-open="isOpen" class="{{speedDial.class}}" ng-class="{{speedDial.ngClass}}" ng-mouseenter="isOpen=true" ng-mouseleave="isOpen=false">'+
    '<md-fab-trigger>'+
    '<md-button aria-label="menu" class="md-fab">'+
    '<md-icon md-svg-src="icons/ic_menu_white_24px.svg" aria-label="menu"></md-icon>'+
    '</md-button>'+
    '</md-fab-trigger>'+
    '<md-fab-actions ng-show="{{speedDial.fabActions.length>0}}">'+
    '<div ng-repeat="action in speedDial.fabActions">'+
    '<md-button ng-click="actionClick(action)" aria-label="{{action.name}}" class="md-fab md-raised md-mini">'+
    '<md-tooltip  md-direction="{{action.direction}}" md-visible="tooltipVisible" md-autohide="false">'+
    '{{action.name}}'+
    '</md-tooltip>'+
    '<md-icon md-svg-src="{{action.icon}}" aria-label="{{action.name}}"></md-icon>'+
    '</md-button>'+
    '</div>'+
    '</md-fab-actions>'+
    '</md-fab-speed-dial>';

    return {
      restrict: 'E',
      link: function( scope, element, attributes){
        $log.log("speedDial",'Linked');

        function speedDialSuccessCallback(speedDial){
          $log.debug('speedDial','speedDialSuccessCallback',speedDial);
          scope.speedDial = speedDial;
          var content = $compile(template)(scope);
          element.append(content);
        };
        function speedDialErrorCallback(message){
          $log.debug('speedDial','speedDialErrorCallback',message);
          if(scope.speedDial)scope.speedDial.show = false;
        }
        SpeedDialService.get($state.current.name)
        .then(speedDialSuccessCallback, speedDialErrorCallback);
      },

      controller:['$scope','$rootScope','$state',function($scope,$rootScope,$state){
        $log.log("speedDial",SpeedDialService);

        function changeSuccessListener(event, toState, toParams, fromState, fromParams){
          $log.info('speedDial','$stateChangeSuccess',event, toState, toParams, fromState, fromParams);

          function speedDialSuccessCallback(speedDial){
            $log.debug('speedDial','speedDialSuccessCallback',speedDial);
            $scope.speedDial = speedDial;
          };
          function speedDialErrorCallback(message){
            $log.debug('speedDial','speedDialErrorCallback',message);
            if($scope.speedDial)$scope.speedDial.show = false;
          }

          SpeedDialService.get(toState.name)
          .then(speedDialSuccessCallback, speedDialErrorCallback);
        }
        $rootScope.$on('$stateChangeSuccess', changeSuccessListener);

        $scope.actionClick = function(action){
          $log.info('speedDial','action','click',action);
          action.click($state);
        }
      }]
    };
  }]);
})();


/*
      compile: function(element, attributes){
        return {
          pre: function(scope, element, attributes, controller, transcludeFn){
            $log.debug('speedDial','directive pre',element);
            $log.debug( SpeedDialService.get($state.current.name));
            function speedDialSuccessCallback(speedDial){
              $log.debug('speedDial',speedDial);
              scope.speedDial = speedDial;
            };
            function speedDialErrorCallback(message){
              $log.debug('speedDial',message);
            }
            SpeedDialService.get($state.current.name)
            .then(speedDialSuccessCallback, speedDialErrorCallback);
          },
          post: function(scope, element, attributes, controller, transcludeFn){
            $log.debug('speedDial','directive post',element,SpeedDialService);
            scope.$watch('speedDial',
              function handleFooChange( newValue, oldValue ) {
                console.log( "vm.fooCount:", oldValue,newValue );
                var x = $compile(template)(scope);
                element.replaceWith(x);
              }
            );
          }
        }
      },
*/
