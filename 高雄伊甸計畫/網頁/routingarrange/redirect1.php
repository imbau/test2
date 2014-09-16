<?php
include("connectInfo.php");
$arrangedate =$_GET["date"];
$arrangetime =$_GET["time"];  
$mode =$_GET["mode"]; 
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta name="generator" content="HTML Tidy for HTML5 (experimental) for Windows https://github.com/w3c/tidy-html5/tree/c63cc39" />
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <title>進度表</title>
  <meta name="viewport" content="width=device-width" />
  <link href="jquery-ui-1.9.0.custom/css/ui-lightness/jquery-ui-1.9.0.custom.css" rel="stylesheet" />
  <link href="bootstrap.min.css" rel="stylesheet" />
  <link rel="stylesheet" href="http://blueimp.github.com/cdn/css/bootstrap-responsive.min.css" />
  <link rel="stylesheet" href="http://blueimp.github.com/Bootstrap-Image-Gallery/css/bootstrap-image-gallery.min.css" />
  <link type="text/css" href="menu.css" rel="stylesheet" />
  <link rel="stylesheet" type="text/css" href="css/demo.css" />
  <link rel="stylesheet" type="text/css" href="css/style6.css" />
  <link rel="stylesheet" href="css/darkwash.css" media="screen" />
  <style type="text/css">
                   body {
                              color:SaddleBrown;                                         
							  font-size:0.825em;
							  background: url(images/background.jpg) no-repeat center top #252525;
							  font-family:Eras Light ITC, Helvetica, sans-serif;
                           }      
               
                   .ui-progressbar .ui-progressbar-value { background-image: url(images/progress-10.gif); }
  </style>
  <script src="jquery-1.8.1.min.js"></script>
  <script type="text/javascript" src="jquery.lettering.js"></script>
  <script type="text/javascript" src="menu.js"></script>
  <script type="text/javascript" src="script.js"></script>
  <script src="jquery-ui-1.9.0.custom.js"></script>
   <script  src="tools.js">  </script>  
<script>
  var begin;
  var finished = 0; 
  var arrangedate = '<?php echo $arrangedate; ?>';
  var arrangetime = '<?php echo $arrangetime; ?>';	
  var mode = '<?php echo $mode; ?>';	
  var str=null;  
  
	switch(mode)
  	{
  		case '4':
  		str="候補班完成";  		
  		break;
  	}
   	
  function showPercentage()
  {
	$.get("progress.php?option="+mode+"&date="+arrangedate+"&time="+arrangetime, {"option": mode},function(response)
	{	  
	    $("span#ppercent").text(response + " %");
		 test(parseInt(response));     
		if(response == 100)
		{
			window.clearInterval(begin);					
            alert(str);
			window.close(); 			
		}
		
   	});
  }  
  begin = window.setInterval(showPercentage, 1000);
  </script>
</head>

<body >  
    	<p><h2>進度: <span id="ppercent">0%</span></h2></p>
	    <div id="progressbar"></div>  
</body>
</html>
