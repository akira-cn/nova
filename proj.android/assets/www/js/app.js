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
.controller('PlutoCtrl', function($scope, $http, $nova, $q){

  var data = localStorage.getItem("items");

  var groups = null;

  if(data){
    try{
      groups = JSON.parse(data);
    }catch(ex){

    }
  }

  if(!groups){
    groups = [{title: new Date().getTime(),
       content: "这是一条测试消息",
       type: "info",
       state: "normal"
    }]; 
  }

  $scope.$watch(function(scope) {
    return JSON.stringify(scope.groups);
  }, function(newValue, oldValue){
    //console.log(newValue, oldValue);
    localStorage.setItem('items', newValue);
  });

  $scope.oneAtATime = true;

  $scope.groups = groups;

  function changeState(group, newState){
    var oldState = group.state;
    group.state = newState;

    if(group.id){
      push(group.state, group.id).then(
        null, function(){
          alert('数据同步失败，请检查网络设置');
          group.state = oldState;
        }
      );
    }    
  }
  
  $scope.undo = function(group){
    changeState(group, "normal");
  }

  $scope.doing = function(group){
    changeState(group, "doing");
  }

  $scope.done = function(group){
    changeState(group, "done");
  }

  $scope.remove = function(group){
    var idx = $scope.groups.indexOf(group);
    $scope.groups.splice(idx, 1);
  }

  if($nova){
    $scope.deviceId = $nova.PhoneState.telephony.deviceId;

    function push(cmd, dataId){
      var deferred = $q.defer();

      $http(
        {
          timeout:5000, 
          method: 'POST', 
          url: 'http://db1.scg.bjt.qihoo.net:8360/PlutoTest/push?id=' + $scope.deviceId,
          data: {cmd:cmd, data:dataId, from:$scope.deviceId},
        })
        .success(function(data){
          deferred.resolve();
        })
        .error(function(){
          deferred.reject();
        });

      return deferred.promise;
    }

    function poll(){
      $http.get('http://db1.scg.bjt.qihoo.net/PlutoTest/poll?id=' + $scope.deviceId 
        + '&r=' + Math.random()).success(function(data){
        if(data.cmd && data.data){
          switch(data.cmd){
            case "info":
            case "warn":
            case "err":
              var date = new Date(data.time || null);

              $scope.groups.unshift({
                  title: date.getTime(), 
                  content: data.data, 
                  type: data.cmd, 
                  state: "normal",
                  id: data.id});
              
              $nova.Ringtone.play($nova.Ringtone.TYPE_NOTIFICATION);
              $nova.Notifications.postNotification("QAlarm: " + data.data.substring(0, 20), 
                "QAlarm <" + data.cmd + ">", data.data);
              break;
            case "normal":
            case "doing":
            case "done":
            case "del":
              var id = data.data;
              var found = $scope.groups.filter(function(msg){
                return msg.id === id;
              });
              if(found.length > 0){
                if(data.cmd === "del"){
                  var idx = $scope.groups.indexOf(found[0]);
                  $scope.groups.splice(idx, 1);
                }else{
                  console.log(found[0]);
                  found[0].state = data.cmd;
                }
              }
              $nova.Ringtone.play($nova.Ringtone.TYPE_NOTIFICATION);
              $nova.Notifications.postNotification(
                "QAlarm: " + data.from + "更改了消息状态", 
                "QAlarm <" + data.cmd + ">", 
                "QAlarm: " + data.from + "更改了消息状态"
              );
              break;
          }
        }
        poll();
      }).error(function(){
        poll();
      });
    }
    poll();
  }
});
