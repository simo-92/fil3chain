'use strict';

/**
* @ngdoc function
* @name blockchain.controller:MainCtrl
* @description
* # MainCtrl
* Controller of the blockchain
*/
angular.module('blockchainApp')
.controller('MainCtrl', function ( $scope ) {
   $scope.title = 'My App Title';
  var imagePath = 'http://i1.wp.com/www.yaabot.com/wp-content/uploads/2015/12/yaabot_brain_1.jpg?resize=759%2C500';
  $scope.todos = [];
  for (var i = 0; i < 15; i++) {
    $scope.todos.push({
      face: imagePath,
      what: "Brunch this weekend?",
      who: "Min Li Chan",
      notes: "I'll be in your neighborhood doing errands."
    });
  }
});
