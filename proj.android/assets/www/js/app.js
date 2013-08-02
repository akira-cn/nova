var myApp = angular.module('myApp',['ui.bootstrap','ajoslin.mobile-navigate'])
.config(function($routeProvider) {
  $routeProvider.when("/", {
    templateUrl: "inc/home.html"
  })
  .when("/1", {
    templateUrl: "inc/page1.html"
  })
  .when("/2", {
    templateUrl: "inc/page2.html"
  })
  .otherwise({
    redirectTo: "/"
  });
})
.run(function($route, $http, $templateCache) {
  angular.forEach($route.routes, function(r) {
    if (r.templateUrl) { 
      $http.get(r.templateUrl, {cache: $templateCache});
    }
  });
})
.controller('MainCtrl', function($scope, $navigate) {
  $scope.$navigate = $navigate;
})
.controller('AccordionDemoCtrl', function($scope){
  $scope.oneAtATime = true;

  $scope.groups = [
    {
      title: "Dynamic Group Header - 1",
      content: "Dynamic Group Body - 1"
    },
    {
      title: "Dynamic Group Header - 2",
      content: "Dynamic Group Body - 2"
    }
  ];

  $scope.items = ['Item 1', 'Item 2', 'Item 3'];

  $scope.addItem = function() {
    var newItemNo = $scope.items.length + 1;
    $scope.items.push('Item ' + newItemNo);
  };  
})
.controller('AlertDemoCtrl', function($scope){
  $scope.alerts = [
    { type: 'error', msg: 'Oh snap! Change a few things up and try submitting again.' }, 
    { type: 'success', msg: 'Well done! You successfully read this important alert message.' }
  ];

  $scope.addAlert = function() {
    $scope.alerts.push({msg: "Another alert!"});
  };

  $scope.closeAlert = function(index) {
    $scope.alerts.splice(index, 1);
  };
})
.controller('ButtonsCtrl', function($scope){
  $scope.singleModel = 1;

  $scope.radioModel = 'Middle';

  $scope.checkModel = {
    left: false,
    middle: true,
    right: false
  };
});

/*
function CarouselDemoCtrl($scope) {
  $scope.myInterval = 5000;
  var slides = $scope.slides = [];
  $scope.addSlide = function() {
    var newWidth = 200 + ((slides.length + (25 * slides.length)) % 150);
    slides.push({
      image: 'http://placekitten.com/' + newWidth + '/200',
      text: ['More','Extra','Lots of','Surplus'][slides.length % 4] + ' ' +
        ['Cats', 'Kittys', 'Felines', 'Cutes'][slides.length % 4]
    });
  };
  for (var i=0; i<4; i++) {
    $scope.addSlide();
  }
}

function CollapseDemoCtrl($scope) {
  $scope.isCollapsed = false;
}

var DatepickerDemoCtrl = function ($scope) {
  $scope.today = function() {
    $scope.dt = new Date();
  };
  $scope.today();

  $scope.showWeeks = true;
  $scope.toggleWeeks = function () {
    $scope.showWeeks = ! $scope.showWeeks;
  };

  $scope.clear = function () {
    $scope.dt = null;
  };

  // Disable weekend selection
  $scope.disabled = function(date, mode) {
    return ( mode === 'day' && ( date.getDay() === 0 || date.getDay() === 6 ) );
  };

  $scope.toggleMin = function() {
    $scope.minDate = ( $scope.minDate ) ? null : new Date();
  };
  $scope.toggleMin();
};

function DialogDemoCtrl($scope, $dialog){

  // Inlined template for demo
  var t = '<div class="modal-header">'+
          '<h3>This is the title</h3>'+
          '</div>'+
          '<div class="modal-body">'+
          '<p>Enter a value to pass to <code>close</code> as the result: <input ng-model="result" /></p>'+
          '</div>'+
          '<div class="modal-footer">'+
          '<button ng-click="close(result)" class="btn btn-primary" >Close</button>'+
          '</div>';

  $scope.opts = {
    backdrop: true,
    keyboard: true,
    backdropClick: true,
    template:  t, // OR: templateUrl: 'path/to/view.html',
    controller: 'TestDialogController'
  };

  $scope.openDialog = function(){
    var d = $dialog.dialog($scope.opts);
    d.open().then(function(result){
      if(result)
      {
        alert('dialog closed with result: ' + result);
      }
    });
  };

  $scope.openMessageBox = function(){
    var title = 'This is a message box';
    var msg = 'This is the content of the message box';
    var btns = [{result:'cancel', label: 'Cancel'}, {result:'ok', label: 'OK', cssClass: 'btn-primary'}];

    $dialog.messageBox(title, msg, btns)
      .open()
      .then(function(result){
        alert('dialog closed with result: ' + result);
    });
  };
}

// the dialog is injected in the specified controller
function TestDialogController($scope, dialog){
  $scope.close = function(result){
    dialog.close(result);
  };
}*/