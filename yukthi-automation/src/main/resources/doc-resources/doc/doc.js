$.application.controller('docController', function($scope){
	
	$scope.indexedData = {
		"plugins": {},
		"steps": {},
		"validations": {}
	};
	
	$scope.nodeSelected = function(event, data) {
		console.log(data);
		
		if(!data.selected || data.selected.length == 0)
		{
			return;
		}
		
		var selectedId = data.selected[0];
		var usIdx = selectedId.indexOf("_");
		
		var type = selectedId.substr(0, usIdx);
		selectedId = selectedId.substr(usIdx + 1);

		var dollarIdx = selectedId.indexOf("$");
		var objId = dollarIdx > 0 ? selectedId.substr(0, dollarIdx) : selectedId;
		
		console.log("Selected id: " + objId);
		if(type == "step")
		{
			$scope.selectedItem = {"type": "step", "data": $scope.indexedData.steps[objId]};
		}
		else if(type == "validation")
		{
			$scope.selectedItem = {"type": "validation", "data": $scope.indexedData.validations[objId]};
		}
		else
		{
			$scope.selectedItem = {"type": "plugin", "data": $scope.indexedData.plugins[objId]};
		}
		
		try
		{
			$scope.$digest();
		}catch(ex)
		{
			console.log(ex);
		}
	};
	
	$scope.load = function() {
		var pluginsNode = {"id": "plugins", "text": "Plugins", "state": {"opened": true}, children: []};
		var stepsNode = {"id": "steps", "text": "Steps", "state": {"opened": true}, children: []};
		var validationsNode = {"id": "validations", "text": "Validations", "state": {"opened": true}, children: []};
		
		var treeData = [pluginsNode, stepsNode, validationsNode];
		
		//add place holder for steps and validation within plugin
		$scope.indexedData.plugins["<default>"] = {"name": "&lt;default&gt;", "steps": [], "validations": []};
		
		for(var plugin of docData.plugins)
		{
			$scope.indexedData.plugins[plugin.name] = plugin;
			plugin.steps = [];
			plugin.validations = [];
		}
		
		//create nodes for steps
		var stepNode = null;
		
		for(var step of docData.steps)
		{
			$scope.indexedData.steps[step.name] = step;
			
			if(step.requiredPlugins && step.requiredPlugins.length > 0)
			{
				for(var pluginName of step.requiredPlugins)
				{
					$scope.indexedData.plugins[pluginName].steps.push(step);
				}
			}
			else
			{
				$scope.indexedData.plugins["<default>"].steps.push(step);
			}
			
			stepNode = {"id": "step_" + step.name, "text": step.name};
			stepsNode.children.push(stepNode);
		}
		
		//create nodes for validations
		var validationNode = null;
		
		for(var validation of docData.validations)
		{
			$scope.indexedData.validations[validation.name] = validation;
			
			if(validation.requiredPlugins && validation.requiredPlugins.length > 0)
			{
				for(var pluginName of validation.requiredPlugins)
				{
					$scope.indexedData.plugins[pluginName].validations.push(validation);
				}
			}
			else
			{
				$scope.indexedData.plugins["<default>"].validations.push(validation);
			}
			
			validationNode = {"id": "validation_" + validation.name, "text": validation.name};
			validationsNode.children.push(validationNode);
		}
		
		//create nodes for plugins
		var addPlugin = function(pluginName) {
			var plugin = $scope.indexedData.plugins[pluginName];
			
			var pluginNode = {"id": "plugin_" + plugin.name, "text": plugin.name, "state": {"opened": false}, children: []};
			
			pluginsNode.children.push(pluginNode);
			
			for(var step of plugin.steps)
			{
				stepNode = {"id": "step_" + step.name + "$" + plugin.name, "text": step.name};
				pluginNode.children.push(stepNode);
			}

			for(var validation of plugin.validations)
			{
				validationNode = {"id": "validation_" + validation.name + "$" + plugin.name, "text": validation.name};
				pluginNode.children.push(validationNode);
			}
		};

		addPlugin("<default>");
		
		for(var plugin of docData.plugins)
		{
			addPlugin(plugin.name);
		}

		$('#dataTree').jstree({ 'core' : { 'data' : treeData} });
		$('#dataTree').on("select_node.jstree", $scope.nodeSelected);
	};
});