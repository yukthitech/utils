var testLogApp = angular.module('testLogApp', []);

testLogApp.controller('testLogAppController', function($scope){
	
	$scope.fetchJsonObjects = function(){
		
		var urlStr = (window.location).search;
		
		urlStr = "logs/" + urlStr.slice(1, urlStr.length) + ".js";
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
		
		try
		{
			$scope.$apply();
		}catch(ex)
		{}
	};
	
});