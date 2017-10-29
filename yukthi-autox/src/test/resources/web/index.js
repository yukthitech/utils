function changeStatusTo(status)
{
	var fld = document.getElementById("status");
	fld.value = status;
	
	var div = document.getElementById("testLayer");
	div.style.visibility = 'visible';
}