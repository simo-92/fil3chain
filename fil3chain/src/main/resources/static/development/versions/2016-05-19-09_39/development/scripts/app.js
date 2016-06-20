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
  'angularUtils.service.Sha256'
])
.config(function($stateProvider, $urlRouterProvider) {


  $stateProvider
  .state('welcome', {
    url: '/welcome',
    templateUrl: 'views/welcome/welcome.html',
    controller:'welcomeCtrl'
  })
  .state('welcome.signin', {
    url: "/signin",
    templateUrl: 'views/welcome/partials/signin.html',
    controller: 'signinCtrl'
  })
  .state('welcome.signup', {
    url: "/signup",
    templateUrl: 'views/welcome/partials/signup.html',
    controller:'signupCtrl'
  })

  .state('app', {
    abstract: true,
    url: '/app',
    templateUrl: 'views/application/application.html',
    controller: 'applicationCtrl'
  })
  .state('app.dashboard', {
    url: '/dashboard',
    controller: 'applicationCtrl',
    views: {
      'signin': {
        templateUrl: 'views/welcome/partials/signin.html'
      },
      'signup': {
        templateUrl: 'views/welcome/partials/signup.html'
      }
    },
    templateUrl: 'views/application/partials/dashboard.html',
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
  .state('app.wallet', {
    url: '/wallet',
    templateUrl: 'views/wallet/wallet.html',
    controller: 'copyrightCtrl'
  })

  $urlRouterProvider.otherwise("/welcome");

})
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
})
.directive('apsUploadFile', ['sha256Service',function (sha256Service) {
  var directive = {
    restrict: 'E',
    //template: '<input id="fileInput" type="file" class="ng-hide"> <md-button id="uploadButton" class="md-raised md-primary" aria-label="attach_file">    Choose file </md-button><md-input-container  md-no-float>    <input id="textInput" ng-model="fileName" type="text" placeholder="No file chosen" ng-readonly="true"></md-input-container>',
    template:'<input type="file"></input>',
    link: apsUploadFileLink
  };
  return directive;



  function apsUploadFileLink(scope, element, attrs) {
    console.log('element ',element);
    element.on('change',function(event){
      console.log('Input changed',event);
      console.log(element);
      var files = event.target.files; // FileList object
      console.log('Files in input',files);
      var file = event.target.files[0];
      var reader = new FileReader();
      // Closure to capture the file information.
      reader.onload = (function(theFile) {
        return function(e) {
          console.log('Reader OnLoad',e,theFile);
          //console.log('Blob', e.currentTarget.result);
          var sha = sha256Service.hash(e.currentTarget.result);
          console.log('Sha256', sha);
        }
      })(file);
      // Read in the image file as a data URL.
     reader.readAsDataURL(file);

    })
    /*
    var input = angular.element((element[0]).querySelector('#fileInput'));
    var button =  angular.element((element[0]).querySelector('#uploadButton'));
    var textInput =  angular.element((element[0]).querySelector('#textInput'));

    if (input.length && button.length && textInput.length) {
    button.on('click',function(e) {
    input.click();
  });
  textInput.on('click',function(e) {
  input.click();
});
}

input.on('change', function(e) {
var files = e.target.files;
if (files[0]) {
scope.fileName = files[0].name;
} else {
scope.fileName = null;
}
scope.$apply();
});
*/
};

}])
