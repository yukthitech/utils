function changeStatusTo(status)
{
	var fld = document.getElementById("status");
	fld.value = status;
	
	var div = document.getElementById("testLayer");
	div.style.visibility = 'visible';
}

function setHiddenValue()
{
	var fld = document.getElementById("status");
	var hiddenFld = document.getElementById("hiddenFld");
	
	fld.value = hiddenFld.value;
}