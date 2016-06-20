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
  var moduleName = 'directive.navbar.fil3chain';
  /**
  * Module
  */
  var module;
  try {
    module = angular.module(moduleName);
  } catch(err) {
    // named module does not exist, so create one
    module = angular.module(moduleName, [
      'ui.router',
      'service.navbar.fil3chain',
      'state.filters.navbar.fil3chain'
    ]);
  }
  module.directive('navbar', NavbarDirective);

  NavbarDirective.$inject = [
    '$log',
    '$compile',
    '$http',
    '$state',
    'Navbar',
    'StateNavbarFilter',
    'TabNavbarFilter'
  ];
  function NavbarDirective($log, $compile, $http, $state, Navbar,StateNavbarFilter,TabNavbarFilter) {
    var templateSrc = 'scripts/modules/navbar.fil3chain/templates/navbar.html';
    return{
      restrict:'E',
      scope:{},
      compile: function compile(tElement, tAttrs, transclude) {
        return {
          pre: function preLink(scope, iElement, iAttrs, controller) {
            $log.debug('NavbarDirective','pre',$state.current.name);
            scope.navbar = Navbar.get($state.current.name);
            $log.debug('NavbarDirective','pre',Navbar.get($state.current.name));

            scope.actionClick = function(index,action){
              $log.info('navbar','action','click',action);
              $state.go(action.state),
              selectedIndex = index;
            }
            function setIndexTab(links,stateName){
              var x = TabNavbarFilter(links,stateName)
              console.log('link trovati',x);
              return x;
            }
            scope.setIndexTab = setIndexTab;
            //scope.selectedIndex = 0;
            scope.selectedIndex = setIndexTab(scope.navbar.links,$state.current.name);
            /*
            scope.$watch('selectedIndex', function(current, old) {
            $log.info('navbar','selectedIndex',scope.navbar,current, old);

            $state.go(scope.navbar.links[current].state);
          })
          */
        },
        post: function postLink(scope, iElement, iAttrs, controller) {
          $log.debug('NavbarDirective','post');
          $http.get(templateSrc)
          .then(function(template){
            $log.debug('NavbarDirective','post','template load success');
            var compiled = $compile(template.data)(scope);
            angular.element(iElement).replaceWith(compiled);

            var watched=0;

            function changeSuccessListener(event, toState, toParams, fromState, fromParams){
              //$log.info('NavbarDirective','$stateChangeSuccess',event, toState, toParams, fromState, fromParams);
              scope.navbar = Navbar.get($state.current.name);
              $log.info('NavbarDirective','$stateChangeSuccess',scope.selectedIndex)
              scope.selectedIndex = scope.setIndexTab(scope.navbar.links,$state.current.name);
          }
          scope.$on('$stateChangeSuccess', changeSuccessListener);

        },function(error){
          $log.error('NavbarDirective','post','template load error',error);
        })
      }
    }
  }
};
};
})();
