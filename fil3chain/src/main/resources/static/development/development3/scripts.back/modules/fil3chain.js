(function() {
  'use strict';
  /**
  * Config
  */
  var moduleName = 'fil3chain';
  /**
  * Module
  */
  var module;
  try {
    module = angular.module(moduleName);
  } catch(err) {
    // named module does not exist, so create one
    module = angular.module(moduleName, [
      'user.fil3chain',
      'authentication.fil3chain',
      'speedDial.fil3chain'
    ]);
  }

})();
