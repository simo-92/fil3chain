(function() {
  'use strict';
  /**
  * Config
  */
  var moduleName = 'config.sidenav.fil3chain';
  /**
  * Module
  */
  var module;
  try {
    module = angular.module(moduleName);
  } catch(err) {
    // named module does not exist, so create one
    module = angular.module(moduleName, []);
  }

  module.factory('SidenavConfig', SidenavServiceConfig);

  SidenavServiceConfig.$inject = [];
  function SidenavServiceConfig() {
    return[
      {
        states : [
          'app.wallet',
          'app.fil3chain',
          'app.wallet',
          'app.wallet.profile',
          'app.wallet.transactions',
          'app.wallet.statistics'
        ],
        links:[{
          state : 'app.dashboard',
          label: 'Dashboard',
          icon: 'icons/ic_dashboard_black_24px.svg'
        },
        {
          state : 'app.copyright',
          label: 'Copyright',
          icon: 'icons/ic_copyright_black_24px.svg'
        },
        {
          state : 'app.statistics',
          label: 'Statistics',
          icon: 'icons/graph_black.svg'
        },
        {
          state : 'app.wallet.profile',
          label: 'Wallet',
          icon: 'icons/user_black.svg'
        }]
      },
      {
        states : ['default'],
        links: []
      }
    ];
  };


})();
