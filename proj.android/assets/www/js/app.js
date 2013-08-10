var myApp = angular.module('myApp',
  ['ng-iscroll', 'ui.bootstrap','ajoslin.mobile-navigate','angular-gestures', 'weizoo.nova'])
.config(function($routeProvider) {
  $routeProvider.when("/", {
    templateUrl: "inc/home.html"
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
.controller('MainCtrl', function($scope/*, $navigate*/) {

})
.controller('PlutoCtrl', function($scope, $http, $nova){
  //console.log($nova.WebSocket);
  //alert(JSON.stringify($nova.PhoneState.connectivity));

  $scope.oneAtATime = true;

  $scope.groups = [
    {title: new Date().getTime(),
     content: "这是一条测试消息",
     type: "info",
     state: "normal"
    },
  ];

  $scope.done = function(group){
    group.state = group.state ==  "done" ? "normal" : "done";
  }

  $scope.remove = function(group){
    var idx = $scope.groups.indexOf(group);
    $scope.groups.splice(idx, 1);
  }

  if($nova){
    $scope.deviceId = $nova.PhoneState.telephony.deviceId;

    function poll(){
      $http.get('http://db1.scg.bjt.qihoo.net/PlutoTest/poll?id=' + $scope.deviceId 
        + '&r=' + Math.random()).success(function(data){
        if(data.cmd && data.data){
          var date = new Date(data.time || null);
          $scope.groups.unshift({
              title: date.getTime(), 
              content: data.data, 
              type: data.cmd, state: "normal"});
          $nova.Ringtone.play($nova.Ringtone.TYPE_NOTIFICATION);
          $nova.Notifications.postNotification("QAlarm: " + data.data.substring(0, 20), 
            "QAlarm <" + data.cmd + ">", data.data);
        }
        poll();
      }).error(function(){
        poll();
      });
    }
    poll();
  }
});
