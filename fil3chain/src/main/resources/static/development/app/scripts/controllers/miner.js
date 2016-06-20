'use strict';

/**
* @ngdoc function
* @name blockchain.controller:signinCtrl
* @description
* # signinCtrl
* Controller of the blockchain
*/
angular.module('blockchainApp')
.controller('minerCtrl',function($scope, $mdDialog, $mdMedia){
  console.log('minerCtrl');
  var start = false;
  var buttonMinerStart={
    icon:'icons/ic_play_arrow_white_24px.svg',
    label:'Start miner'
  };
  var buttonMinerStop={
    icon:'icons/ic_stop_white_24px.svg',
    label:'Stop miner'
  };

  $scope.buttonMiner = buttonMinerStart;
  function minerButtonClick(event){
    console.log('minerCtrl',start);
    //Qui si deve avviare il metodo di mining
    if(!start) {
      showAlert(event);
      $scope.buttonMiner = buttonMinerStop;
    }
    else {
      $scope.buttonMiner = buttonMinerStart;
    }
    start=!start;
    return;
  }
  $scope.minerButtonClick = minerButtonClick;
  function showAlert(ev) {
    var useFullScreen = ($mdMedia('sm') || $mdMedia('xs')) ;// && $scope.customFullscreen;

    $mdDialog.show({
      controller: 'minerDialogCtrl',
      templateUrl: 'views/miner.dialog.html',
      parent: angular.element(document.body),
      targetEvent: ev,
      clickOutsideToClose:true,
      fullscreen: useFullScreen
    })
    .then(function(answer) {
      $scope.status = 'You said the information was "' + answer + '".';
    }, function() {
      $scope.status = 'You cancelled the dialog.';
    });
  };
    function showAlert1(ev) {
      // Appending dialog to document.body to cover sidenav in docs app
      // Modal dialogs should fully cover application
      // to prevent interaction outside of dialog
      $mdDialog.show(
        $mdDialog.alert()
        .parent(angular.element(document.querySelector('body')))
        .clickOutsideToClose(true)
        .title('This is an alert title')
        .textContent('You can specify some description text in here.')
        .ariaLabel('Alert Dialog Demo')
        .ok('Got it!')
        .targetEvent(ev)
      );
    };

  });
