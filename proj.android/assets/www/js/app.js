var myApp = angular.module('myApp',
  ['ng-iscroll', 'ui.bootstrap','ajoslin.mobile-navigate','angular-gestures', 'weizoo.nova'])
.config(function($routeProvider) {
  $routeProvider.when("/", {
    templateUrl: "inc/home.html"
  })
  /*.when("/1", {
    templateUrl: "inc/page1.html"
  })
  .when("/2", {
    templateUrl: "inc/page2.html"
  })*/
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
.controller('MainCtrl', function($scope/*, $navigate*/) {

  // Defines additional options such as onScrollEnd and other runtime settings
  // exposed by iScroll can be defined per id attribute

  $scope.$parent.myScrollOptions = {
      snap: false,
      checkDOMChanges: true,
      onScrollEnd: function ()
      {
          //alert('finshed scrolling');
      }
  };

  // expose refreshiScroll() function for ng-onclick or other meth
  $scope.refreshiScroll = function ()
  {
      $scope.$parent.myScroll['wrapper'].refresh();
  };

  /*var i = 0, guard = false;

  $scope.nav = {
    next: function(){
      if(guard){
        return;
      }
      if(i < 2){
        $navigate.go('/' + ++i);
        guard = true;
        setTimeout(function(){
          guard = false;
        }, 300);
      }
    },
    back: function(){
      if(guard){
        return;
      }
      if(i > 0){ 
        $navigate.back();
        i--;
        guard = true;
        setTimeout(function(){
          guard = false;
        }, 300);
      }
    }
  }*/
})
.controller('AccordionDemoCtrl', function($scope, $http, $nova){
  /*function poll(){
    $http.get('http://db1.scg.bjt.qihoo.net/PlutoTest/poll?id=test').success(function(data){
      console.log(data);
      //poll();
    }).error(function(){
      //poll();
    });
  }
  poll();*/

  console.log($nova.WebSocket);

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
