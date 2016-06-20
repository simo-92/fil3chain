'use strict';

/**
* @ngdoc overview
* @name testComponentApp
* @description
* # testComponentApp
*
* Main module of the application.
*/
angular
.module('blockchainApp')
.service('fabService',['$q','$filter', function navbar($q,$filter) {
  var config={
    title:'',
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
      state : 'app.wallet',
      label: 'Wallet',
      icon: 'icons/user_black.svg'
    }],
    actions:[{
      label: 'Search',
      icon: 'icons/ic_search_white_24px.svg',
      click:function(scope){
        if(!scope.showSearch){
          scope.showSearch=true
        }else{
          scope.showSearch = !scope.showSearch
        }
      }
    }]
  };

  function getSidenavItems() {
    //console.log($filter('filter')(config, { state: 'dash' }));
    return config;
  }
  function setSidenavItems(sidenavItems){
    config = sidenavItems;
    return config;
  }
  return {
    get:getSidenavItems,
    post:setSidenavItems
  }
}]);
