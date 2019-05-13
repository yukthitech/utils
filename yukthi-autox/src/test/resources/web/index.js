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

var clickCount = 0;

function onClickOfClickButton1()
{
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

