var testAutomationApp = angular.module('testAutomationApp', []);

testAutomationApp.controller('testAutomationAppController', function($scope){
	
	$scope.status = "Status";
	
	/**
	 * Gets invoked on init.
	 */
	$scope.fetchObjects = function(){
		
		$scope.testResults = reportData;
		
		$scope.testSuiteResults = $scope.testResults.testSuiteResults;
		
		for(var i = 0 ; i < $scope.testSuiteResults.length ; i++)
		{
			var obj = $scope.testSuiteResults[i];
			obj.display = true;
		}
		 
		 $scope.suiteNameToObj = {};
		 var testResultsArr = [];
		 for(var i = 0 ; i < $scope.testSuiteResults.length ; i++)
		 {
			 var testCaseResults = $scope.testSuiteResults[i].testCaseResults;
			 for(var j = 0 ; j < testCaseResults.length ; j++)
			 {
				 var testCaseObj = testCaseResults[j];
				 testCaseObj.display = true;
			 }
			 
			 var obj = {"suiteName" : $scope.testSuiteResults[i].suiteName, "testCaseResults" : testCaseResults};
			 obj.display = true;
			 
			 $scope.suiteNameToObj[obj.suiteName] = obj;
			 testResultsArr.push(obj);
		 }
		 
		$scope.testResultsArr = testResultsArr;
	};
	
	/**
	 * On change of status.
	 */
	$scope.onChangeStatus = function(data){
		
		$scope.status = data;
	};
	
	/**
	 * Gets  invoked on type for filter test suite.
	 */
	$scope.filterTestSuite = function(event){
		
		var searchString = $scope.searchTestSuiteName.toLowerCase();
		
		for(var i = 0 ; i < $scope.testSuiteResults.length ; i++)
		{
			var obj = $scope.testSuiteResults[i];
			obj.display = obj.suiteName.toLowerCase().includes(searchString);
			
			$scope.suiteNameToObj[obj.suiteName].display = obj.display;
		}
		
	};
	
	/**
	 * Gets invoked on type for filter test case.
	 */
	$scope.filterTestCase = function(event){
		
		var searchString = $scope.searchTestCaseName.toLowerCase();
		
		for(var i = 0 ; i < $scope.testSuiteResults.length ; i++)
		 {
			 var testCaseResults = $scope.testSuiteResults[i].testCaseResults;
			 
			 for(var j = 0 ; j < testCaseResults.length ; j++)
			 {
				 var testCaseObj = testCaseResults[j];
				 testCaseObj.display = testCaseObj.testCaseName.toLowerCase().includes(searchString);;
			 }
		 }
	};
	
});