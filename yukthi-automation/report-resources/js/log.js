$.application.controller('testLogAppController', function($scope){
	
	$scope.filterLevel = "ALL";
	$scope.levels = ["ALL", "DEBUG", "ERROR", "INFO", "TRACE"];
	
	/**
	 * Gets invoked on init.
	 */
	$scope.fetchJsonObjects = function(){
		
		var urlStr = (window.location).search;
		
		urlStr = "../output/logs/" + urlStr.slice(1, urlStr.length) + ".js";
		$scope.importScript(urlStr, $scope.displayLogs);
	};
	
	/**
	 * Import required js file.
	 */
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
	
	/**
	 * Display all the messages
	 */
	$scope.displayLogs = function(){
		
		//logData global variable present in included js file.
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
		
		$scope.highlightCode();
	};
	
	$scope.highlightCode = function() {
		$("pre code").unwrap();
		$("code").wrap('<pre style="padding: 4px; margin: 0.5em; display: inline-block;"></pre>');
		
		$('pre code').each(function(i, block) {
			hljs.highlightBlock(block);
		});	
	};
	
	$scope.highlightFilter = function() {
		
		$scope.highlightCode();
		
		return function() {
			return true;
		};
	};
	
	/**
	 * Gets invoked on change of level.
	 */
	$scope.onChangeLevel = function(data){
		$scope.filterLevel = data;
	};
	
	$scope.filterLogs = function() {
		var filterFunc = function(item) {
			
			if($scope.filterLevel != "ALL")
			{
				if($scope.filterLevel != item.logLevel)
				{
					$scope.highlightCode();
					return false;
				}
			}
			
			var res = true;
			
			if($scope.searchByMessage && $scope.searchByMessage.length > 0)
			{
				var searchByMessage = $scope.searchByMessage.toLowerCase().trim();
				var filters = searchByMessage.split(/\s*\|\s*/);
				res = false;

				for(var i = 0; i < filters.length; i++)
				{
					if(filters[i].length <= 0)
					{
						continue;
					}
					
					if(item.message.toLowerCase().indexOf(filters[i]) >= 0)
					{
						res = true;
						break;
					}
				}
			}
			
			//$scope.highlightCode();
			return res;
		};
		
		return filterFunc;
	};
	
	/**
	 * Replace the message.
	 */
	$scope.replaceMessage = function(message){
		
		if(!message)
		{
			return "";
		}
		
		var result = message.replace(/\n/g, "<BR/>");
		result = result.replace(/\t/g, "&nbsp;&nbsp;&nbsp;&nbsp;");
		
		return result;
		
	};
	
});