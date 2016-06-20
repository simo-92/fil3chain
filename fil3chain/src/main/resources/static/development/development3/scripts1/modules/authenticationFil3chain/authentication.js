(function() {
  'use strict';
  /**
  * Config
  */
  var moduleName = 'authentication.fil3chain';
  /**
  * Module
  */
  var module;
  try {
    module = angular.module(moduleName);
  } catch(err) {
    // named module does not exist, so create one
    module = angular.module(moduleName, ['service.authentication.fil3chain']);
  }

})();
