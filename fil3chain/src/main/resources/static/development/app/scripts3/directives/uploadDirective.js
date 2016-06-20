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
.directive('upload',['$q','$mdToast','sha256Service',function ($q,$mdToast,sha256Service) {
  var directive = {
    restrict: 'C',
    /*template: '<input id="fileInput" type="file" class="ng-hide">
    <md-button id="uploadButton" class="md-raised md-primary" aria-label="attach_file">
    Choose file
    </md-button>
    <md-input-container  md-no-float>
    <input id="textInput" ng-model="fileName" type="text" placeholder="No file chosen" ng-readonly="true">
    </md-input-container>',*/
    template: buildFormTemplate,
    link: apsUploadFileLink
  };
  return directive;

  function buildFormTemplate(){
    return(
      '<input type="file" class="ng-hide">'+
      '<md-button id="uploadButton" class="md-raised md-primary" aria-label="attach_file">'+
      'Choose file'+
      '</md-button>'+
      '</input>'
    );
  }
  function checkFileReader(){
    var deferred = $q.defer();
    // Check for the various File API support.
    if (window.File && window.FileReader && window.FileList && window.Blob) {
      //if supported resolve
      deferred.resolve();
    } else {
      deferred.reject('The File APIs are not fully supported by your browser.');
    }
    return deferred.promise;
  }

  function apsUploadFileLink(scope, element, attrs) {
    console.log('element ',element);
    var reader;
    var inputWrapper;
    var innerButton;
    scope.toggleProgress = function(){
      console.log('Toggle',scope.progressMode);
      if(!scope.progressMode || scope.progressMode===''){
        scope.progressMode='indeterminate';
        scope.$apply();
      }else {
        scope.progressMode='';
      }
    }

    checkFileReader()
    .then(function(){

      inputWrapper = element[0].children[0];
      innerButton = element[0].children[1];
      angular.element(innerButton).on('click',function(){
        inputWrapper.click();
      })
      element.on('change',function(event){

        console.log('Input changed',event);
        //var files = event.target.files;
        var file = event.target.files[0];
        if(file){
          reader = new FileReader();
        scope.toggleProgress();
        console.log('File',file);

        // Closure to capture the file information.
        reader.onload = (function(theFile) {
          return function(e) {
            console.log('Reader OnLoad',e,theFile);
            //console.log('Blob', e.currentTarget.result);
            sha256Service.hash(e.currentTarget.result)
            .then(function(sha){
              console.log('Sha256', sha);
              scope.file = theFile;
              scope.file.sha256 = sha;
              console.log('scope',scope);
              scope.toggleProgress();
              //scope.$apply();
            });

          }
        })(file);

        // Read in the image file as a data URL.
        reader.readAsDataURL(file);
      }
      });
    },function(message){
      $mdToast.show(
        $mdToast.simple()
        .textContent(message)
        .position('fil')
        //.position($scope.getToastPosition())
        .hideDelay(5000)
      );
    });
    /*
    var inputWrapper = element[0].children[0];
    var innerButton = element[0].children[1];

    angular.element(innerButton).on('click',function(){
    inputWrapper.click();
  })

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
*/
};

}])
