<?php
	//echo $_GET['option'];
	//echo $_GET['status'];
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<meta content="text/html; charset=utf-8" http-equiv="Content-Type" />
<script type="text/javascript" src="http://code.jquery.com/jquery-1.8.2.js"></script>
<script type="text/javascript">
$(document).ready
(
	//	alert("test");
	function()
	{
		//alert("testtt");
		var option = <?php echo $_GET['option']; ?>;
		var status = <?php echo $_GET['status']; ?>;
		if(status == 0)
		{
			alert("此新增排班無法排入此輛車!!!");
			window.location = 'testarrangedtable.php?option='+option;
		}
		else if(status == 1)
		{
		
		}
		else if(status == 2)
		{
		
		}
	}
);
</script>
<title>ResultPage</title>
</head>

<body>

</body>

</html>
