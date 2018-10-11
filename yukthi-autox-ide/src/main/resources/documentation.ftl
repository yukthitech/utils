<style>

.doc_heading
{
	font-weight: bold;
	background: #DCDCDC;
	text-align: center !important;
}

.doc_mainHeading
{
	font-weight: bold;
	background: #8585ad;
	color: white;
	text-align: center !important;
}

.doc_sideHeading
{
	font-weight: bold;
	background: #DCDCDC;
	text-align: left;
}

.tdMandatory
{
	font-weight: bold;
	color:red;
}

.tdMandatory::before
{
	content: "*"
}

.doc_table
{
	width: 100%;
	margin: 1em;
}

.doc_table td
{
	padding: 0.5em;
	text-align: left;
	border: 1px solid black;
}

.stripped td::even
{
	background: red;
}
</style>
<div>
	<div style="position: absolute; bottom: 0px; left: 25em; right: 0px; top: 0px; overflow: auto;">
		<div style="padding: 1em; width: 95%; padding-top: 0px;">
		</div>
		
		<div style="width: 95%;">
			<table class="doc_table" style="border:1">
				<tr>
					<td colspan="2" class="doc_mainHeading">
						Basic Information of ${type}
					</td>
				</tr>
				<tr>
					<td class="doc_sideHeading">Name </td>
					<td>${node.name}</td>
				</tr>
				<tr>
					<td class="doc_sideHeading">Description </td>
					<td>${node.description}</td>
				</tr>
				<tr>
					<td class="doc_sideHeading">Java Type </td>
					<td>${node.javaType}</td>
				</tr>
				<#if type=='step'>
				<tr>
					<td class="doc_sideHeading">Required Plugins</td>
					<td>
						<#list node.requiredPlugins as requiredPlugins >
						<div style="display: inline;">
							${requiredPlugins} &nbsp; &nbsp;<span ng-if="!$last"> </span> 
						</div>
						</#list>
					</td>
				</tr>
				</#if>
			</table>

			<table class="doc_table stripped" style="border:1">
				<tr>
					<td colspan="3"  class="doc_mainHeading">
						Parameters
					</td>
				</tr>
				<tr>
					<td class="doc_heading"> Name </td>
					<td class="doc_heading"> Type </td>
					<td class="doc_heading"> Description </td>
				</tr>
				<#list node.params as param>
					<tr>
					<td>${param.name}</td>
					<td>${param.type}</td>
					<td>
						<#if param.sourceType == "RESOURCE">
							<b><i>This is a <a href="#apiIndex$general_resourceParamType">Resource Param.</a><BR/></i></b>
						</#if>
						<#if param.sourceType == "OBJECT">
							<b><i>This is a <a href="#apiIndex$general_objectParamType">Object Param.</a></i></b>
						</#if>
						<#if param.sourceType == "UI_LOCATOR">
							<b><i>This is a <a href="#apiIndex$general_uiLocators">UI locator param.</a></i></b>
						</#if>						
						
						${param.description}
					</td>
				</tr>
					
				</#list>
				<tr>
				<#assign i = node.params?size>
				<#if i==0>
					<td colspan="3" style="font-weight: bold; ">
						<i>No parameters available.</i>
					</td>
				</#if>
				</tr>
			</table>
			<#if type="plugin">
			<table class="doc_table stripped" style="border:1">
				<tr>
					<td colspan="4"  class="doc_mainHeading">
						Plugin Command Line Arguments
					</td>
				</tr>
				<tr>
					<td class="doc_heading"> Short Name </td>
					<td class="doc_heading"> Long Name </td>
					<td class="doc_heading"> Type </td>
					<td class="doc_heading"> Description </td>
				</tr>
				<#list node.cliArguments as clia>
				<tr>
					<td ng-class="param.mandatory ? 'tdMandatory' : 'tdNormal' ">${clia.shortName}</td>
					<td ng-class="param.mandatory ? 'tdMandatory' : 'tdNormal' ">${clia.longName}</td>
					<td>${clia.type}</td>
					<td>${clia.description}</td>
				</tr>
				</#list>
				<tr>
					<#assign i = node.cliArguments?size>
					<#if i==0>
					<td colspan="4" style="font-weight: bold; ">
						<i>No plugin specific command-line-arguments available.</i>
					</td>
					</#if>
				</tr>
			</table>
			</#if>
		</div>
	</div>		
</div>