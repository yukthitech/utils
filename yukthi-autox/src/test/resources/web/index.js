function changeStatusTo(status)
{
	console.log("Changing status to: ", status);
	
	var fld = document.getElementById("status");
	fld.value = status;
	
	var div = document.getElementById("testLayer");
	div.style.visibility = 'visible';
}

function setHiddenValue()
{
	console.log("Setting hidden field value...");
	
	var fld = document.getElementById("status");
	var hiddenFld = document.getElementById("hiddenFld");
	
	fld.value = hiddenFld.value;
}

var clickCount = 0;

function onClickOfClickButton1()
{
	console.log("Buttong is clicked. Current click count: ", clickCount);
	if(clickCount < 2)
	{
		clickCount ++;
		return;
	}
	
	setTimeout(function(){
		var div = document.getElementById("clickButton1Res");
		div.style.visibility = 'visible';
	}, 1600);
}

