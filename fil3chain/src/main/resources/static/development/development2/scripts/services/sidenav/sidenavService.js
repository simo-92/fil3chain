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
.service('sidenavService',['$q','$filter', function sidenav($q,$filter) {
  var visible = false;
  var items = [{
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
  }];
  function getSidenavItems() {
    console.log($filter('filter')(items, { state: 'dash' }));
    return items;
  }
  function setSidenavItems(sidenavItems){
    items = sidenavItems;
    return items;
  }
  function getVisibility(){
    return this.visible;
  }
  function show(){
    this.visible = true;
  }
  function hide(){
    this.visible = false;
  }
  function toggle(){
    this.visible = !this.visible;
  }
  return {
    get:getSidenavItems,
    post:setSidenavItems,
    getVisibility : getVisibility,
    show: show,
    hide: hide,
    toggle : toggle
  }
}]);
