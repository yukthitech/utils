var testLogApp = angular.module('testLogApp', []);

testLogApp.controller('testLogAppController', function($scope){
	
	$scope.fetchJsonObjects = function(){
		
		var urlStr = (window.location).search;
		
		urlStr = urlStr.slice(1, urlStr.length) + ".json";
		
		$.ajax({ dataType: "json", url: urlStr,
			 success: function (data, textStatus) {
				 
				 $scope.testLogs = data;
				
				 console.log($scope.testLogs);
				 try
				 {
					 $scope.$apply();
				 }catch(ex)
				 {}
			 }
		});
		
	};
});