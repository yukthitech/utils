var testLogApp = angular.module('testLogApp', []);

testLogApp.controller('testLogAppController', function($scope){
	
	$scope.filterLevel = "Level";
	
	$scope.fetchJsonObjects = function(){
		
		var urlStr = (window.location).search;
		
		urlStr = "../output/logs/" + urlStr.slice(1, urlStr.length) + ".js";
		$scope.importScript(urlStr, $scope.displayLogs);
	};
	
	$scope.importScript = function (sSrc, fOnload){
		
		  var oScript = document.createElement("script");
		  oScript.type = "text\/javascript";
		  
		  if(fOnload) 
		  { 
			 oScript.onload = fOnload; 
		  }
		  
		 var logScript = document.getElementById('logScript');
			
		 logScript.parentNode.insertBefore(oScript, logScript);
		 oScript.src = sSrc;
	};
	
	$scope.displayLogs = function(){
		
		$scope.testLogs = logData;
		
		$scope.messages = $scope.testLogs.messages;
		
		for(var i = 0 ; i < $scope.messages.length ; i++)
		{
			var obj = $scope.messages[i];
			obj.display = true;
		}
		
		try
		{
			$scope.$apply();
		}catch(ex)
		{}
	};
	
	/**
	 * Gets invoked on change of level.
	 */
	$scope.onChangeLevel = function(data){
		
		$scope.filterLevel= data;
		
		for(var i = 0 ; i < $scope.messages.length ; i++)
		{
			var obj = $scope.messages[i];
			
			obj.display = $scope.filterLevel.includes(obj.logLevel);
		}
		
	};
	
});