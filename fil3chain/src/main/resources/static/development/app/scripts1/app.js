'use strict';

/**
* @ngdoc overview
* @name blockchain
* @description
* # blockchain
*
* Main module of the application.
*/
angular
.module('blockchainApp', [
  'chart.js',
  'ngAnimate',
  'ngAria',
  'ngCookies',
  'ngMaterial',
  'ngMessages',
  'ngResource',
  'ngResource',
  'ngRoute',
  'ui.router',
  'fil3chain',
  'angularUtils.service.Sha256'
])
.config(function($stateProvider, $urlRouterProvider) {


  $stateProvider
  $stateProvider
  .state('welcome', {
    url: '/welcome',
    views: {
      "master": {
        templateUrl: 'views/welcome.html'
      }
    }
  })
  .state('welcome.signin', {
    url: "/signin",
    views: {
      "content@welcome": {
        templateUrl: 'views/welcome.signin.html',
        controller: 'signinCtrl'
      }
    }
  })
  .state('welcome.signup', {
    url: "/signup",
    views: {
      "content@welcome": {
        templateUrl: 'views/welcome.signup.html',
        controller:'signupCtrl'
      }
    }
  })
/*
  .state('app', {
    abstract: true,
    url: '/app/:userId',
    resolve:{
      userId: ['$stateParams',function($stateParams){
        return $stateParams.userId;
      }]
    },
    views: {
      "master": {
        templateUrl: 'views/app.master/application.html',
        controller: 'applicationCtrl'
      },
      "sidenav@app": {
        templateUrl: 'views/app.sidenav/sidenav.html',
        controller: 'SidenavAppCtrl'
      },
      "navbar@app": {
        templateUrl: 'views/app.navbar/navbar.html',
        controller: 'NavbarAppCtrl'
      }
    }
  })
  .state('app.wallet', {
    url: '/wallet',
    views: {
      'navbar@app':{
        templateUrl: 'views/app.wallet.filters/filters.html',
        controller: 'filtersWalletCtrl'
      }
    }
  })
  .state('app.dashboard', {
    url: '/dashboard',
    views: {
      "content@app": {
        templateUrl: 'views/app.dashboard/dashboard.html'
      },
      "filters@app.dashboard": {
        templateUrl: 'views/app.dashboard.filters/filters.tpl.html',
        controller: 'filtersAppDashboardCtrl'
      }
    },
    label:'Dashboard'
  })
  .state('app.copyright', {
    url: '/copyright',
    templateUrl: 'views/copyright/copyright.html',
    controller: 'copyrightCtrl',
    label: 'Copyright',
    data: {
      displayName: 'Copyright'
    }
  })
  /*
  .state('app.wallet', {
    url: '/wallet',
    templateUrl: 'views/wallet/wallet.html',
    controller: 'copyrightCtrl'
  })
*/

  $urlRouterProvider.otherwise("/welcome");

})
.run(['$rootScope',function ($rootScope) {
  $rootScope.menu = [
    {
      link : '',
      title: 'Dashboard',
      icon: 'dashboard'
    },
    {
      link : '',
      title: 'Friends',
      icon: 'group'
    },
    {
      link : '',
      title: 'Messages',
      icon: 'message'
    }
  ];
}]);
/*
.run(['$rootScope','UserService','contextService','sidenavService',function($rootScope,UserService,contextService,sidenavService){
  console.log('Run function');
  console.log('UserService',UserService);
  UserService.Create({name:'ivan18',surname:'ivan18',email:'ivan18@ivan18.it',username:'ivan18',password:'ivan18'});
  console.log('ContextService',contextService);
  contextService.post('welcomeFab',{
    actions:[
      { name: "Signin", icon: "icons/ic_person_black_24px.svg", direction: "left",sref:"welcome.signin" },
      { name: "Signup", icon: "icons/ic_person_add_black_24px.svg", direction: "left",sref:"welcome.signup" }
    ]
  });
  contextService.post('sidenav',[{
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
  }
]);
contextService.post('navbar',{
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
    click:function(ev){console.log(ev)}
  }]
});
console.log('Context',contextService.get());
console.log($rootScope)
}])
.factory('context', ['$rootScope',function($rootScope) {
  $rootScope.context = {};
  return   $rootScope.context;
}])
.service('contextService',['context', function(context) {

  this.get = function(name){
    if(name) return context[name];
    return context;
  };
  this.post=function(name, model){
    return context[name]=model;
  };
}])
.directive('myTabs', function() {
  return {
    restrict: 'E',
    transclude: true,
    scope: {},
    controller: ['$scope', function($scope) {
      var panes = $scope.panes = [];

      $scope.select = function(pane) {
        angular.forEach(panes, function(pane) {
          pane.selected = false;
        });
        pane.selected = true;
      };

      this.addPane = function(pane) {
        if (panes.length === 0) {
          $scope.select(pane);
        }
        panes.push(pane);
      };
    }],
    templateUrl: 'views/my-tabs.html'
  };
})
.directive('myPane', function() {
  return {
    require: '^^myTabs',
    restrict: 'E',
    transclude: true,
    scope: {
      title: '@'
    },
    link: function(scope, element, attrs, tabsCtrl) {
      tabsCtrl.addPane(scope);
    },
    templateUrl: 'views/my-pane.html'
  };
});
*/
