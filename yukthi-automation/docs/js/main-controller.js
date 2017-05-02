$.application.controller('mainController', function($scope){
	$scope.headerLinks = [
	    {"id": "home", "label": "Home", file: "home.html"},
	    {"id": "internals", "label": "Internals", file: "internals.html"},
	    {"id": "customizations", "label": "Customizations", file: "customizations.html"},
	    {"id": "apiIndex", "label": "API Index", file: "doc-index.html"}
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
	};
	
	$scope.init = function() {
		$scope.changeLink("home");
	};
});