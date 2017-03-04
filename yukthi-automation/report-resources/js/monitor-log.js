 
var monitorApp = angular.module("monitorApp", []);

monitorApp.controller("monitorController", function($scope){

	/**
	 * Create dummy records
	 */
	$scope.initContent = function(){
		
		$scope.testCaseName = $("#testCaseName").text();
		$scope.monitorName = $("#monitorName").text();
		
		var content = $("#logContentContainer").text();
		var contentArr = content.split("\n")
		
		var contentLines = [];
		for(var i = 0 ; i < contentArr.length ; i++)
		{
			var line = contentArr[i];
			
			// replace
			line = $scope.replaceAll(line, "\s+", "\u00A0");
			line = $scope.replaceAll(line, "\t", "\u00A0\u00A0\u00A0\u00A0");
			line = line.trim();
			
			if(line.length > 0)
			{
				var obj = {"id" : (i + 1), "line" : line};
				contentLines.push(obj);
			}
		}
		
		$scope.contentLines = contentLines;
	};
	
	/**
	 * Replace all.
	 */
	$scope.replaceAll = function(data, target, replacement){
		
		  return data.split(target).join(replacement);
	};
	
	/**
	 * Goto
	 */
	$scope.goToLine = function(id){
		
		$scope.searchLog = "";
		
		var elem = $("#" + id);
		 
		$('body').animate({ scrollTop: elem.scrollHeight});
	};
	
});