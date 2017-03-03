 
var testMonitorApp = angular.module("testMonitorApp", []);

testMonitorApp.controller("testMonitorController", function($scope){

	/**
	 * Create dummy records
	 */
	$scope.createDummyRecords = function(){
		
		$scope.testCaseName = "testCaseName";
		$scope.monitorName = "monitorName";
		
		$scope.contentLines = [];
		
		for(var i = 0 ; i < 10 ; i++ )
		{
			$scope.contentLines.push(i + "A");
		}
	};
	
});