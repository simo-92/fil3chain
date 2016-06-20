(function () {
  'use strict';

  angular
  .module('blockchainApp')
  .factory('navbar', NavbarCtrl);

  NavbarCtrl.$inject =
  ['$scope','$http', '$cookies', '$rootScope', '$timeout','$q', 'UserService','UserValidatorService'];
  function NavbarCtrl($scope,$http, $cookies, $rootScope, $timeout, $q, UserService, UserValidatorService) {

    $scope.changeState = ChangeState;


    function ChangeState(toState){
      console.log($cookies.get('globals'))
      console.log('Change state',toState);
    }

    function Signup(user) {
      var deferred = $q.defer();
      var promise = UserValidatorService.validateUserSignup(user);
      promise
      .then(function(user){return  UserService.Create(user)})
      .then(function(user){
        deferred.resolve(user)
      }
      ,function(message){
        deferred.reject(message);
      })
      return deferred.promise;
    }
  }


})();
