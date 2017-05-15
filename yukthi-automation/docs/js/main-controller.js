$.application.controller('mainController', function($scope){
	$scope.headerLinks = [
	    {"id": "home", "label": "Home", file: "home.html"},
	    {"id": "apiIndex", "label": "API Index", file: "api/doc-index.html"}
	];
	
	$scope.activeHeaderLink = null;
	
	$scope.changeLink = function(linkId) {
		var newLink = null;
		
		for(var i = 0; i < $scope.headerLinks.length; i++)
		{
			if($scope.headerLinks[i].id == linkId)
			{
				newLink = $scope.headerLinks[i];
				break;
			}
		}
		
		if(!newLink)
		{
			return;
		}
		
		$scope.activeHeaderLink = newLink;
		
		setTimeout(function(){ 
			$('pre code').each(function(i, block) {
				hljs.highlightBlock(block);
			});
		}, 500);
	};
	
	$scope.init = function() {
		$scope.changeLink("home");
	};
});