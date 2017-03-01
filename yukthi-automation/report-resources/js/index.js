var testAutomationApp = angular.module('testAutomationApp', []);

testAutomationApp.controller('testAutomationAppController', function($scope){
	
	$scope.fetchJsonObjects = function(){
		
		$.ajax({ dataType: "json", url: "test-results.json",
			 success: function (data, textStatus) {
				 
				 $scope.testResults = data;
				
				 var testSuiteResults = $scope.testResults.testSuiteResults;
				 
				 var testResultsArr = [];
				 for(var i = 0 ; i < testSuiteResults.length ; i++)
				 {
					 var obj = {"suiteName" : testSuiteResults[i].suiteName, "testCaseResults" : testSuiteResults[i].testCaseResults};
					 
					 testResultsArr.push(obj);
				 }
				 
				 $scope.testResultsArr = testResultsArr;
				 
				 try
				 {
					 $scope.$apply();
				 }catch(ex)
				 {}
			 }
		});
		
	};
});