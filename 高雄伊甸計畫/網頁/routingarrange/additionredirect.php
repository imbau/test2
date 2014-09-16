<?php
include("connectInfo.php");

echo "<script>";
echo "window.open('".$linkurlport."/WebRoutingArranger/AdditionRoutingArranger.view');";
echo "</script>";
?>
<script>
var begin;
//var finished = 0;
function showPercentage()
{
	var xmlhttp;
	//if (str.length==0)
  	//{ 
  	//	document.getElementById("percent").innerHTML="";
  	//	return;
  	//}
	if (window.XMLHttpRequest)
  	{// code for IE7+, Firefox, Chrome, Opera, Safari
  		xmlhttp=new XMLHttpRequest();
  	}
	else
  	{// code for IE6, IE5
  		xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
  	}
	xmlhttp.onreadystatechange=function()
  	{
  		if (xmlhttp.readyState==4 && xmlhttp.status==200)
    	{
			if(xmlhttp.responseText == 100)
			{
				clearInterval(begin);
				document.getElementById("percent").innerHTML=xmlhttp.responseText + " %";
				window.location = 'finalarrangedtable.php?option=0';
			}
			else
			{
				document.getElementById("percent").innerHTML=xmlhttp.responseText + " %";
			}
			
			
    	}
 	}
	xmlhttp.open("GET", "progress.php?option=1", true);
	xmlhttp.send();
}

begin = window.setInterval(showPercentage, 5000);
</script>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" >
<title>Process Progress</title>
</head>

<body>
<p>Process percentage: <span id="percent">0%</span></p>
</body>
</html>