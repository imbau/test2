<?php
    include("Mydbconnect.php");
	include("connectInfo.php");
	$arrangetime = $_GET['time'];
	$arrangedate = $_GET['date'];
	$rs1 = mysql_query("SELECT *FROM userrequests WHERE 狀態='候補' AND arrangedate = '".$arrangedate."' AND arrangetime = '".$arrangetime."'");
	$num_rows = mysql_num_rows($rs1);

?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta name="generator" content="HTML Tidy for HTML5 (experimental) for Windows https://github.com/w3c/tidy-html5/tree/c63cc39" />
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />  
  <title>可調適性動態排班系統</title>
  <link rel="stylesheet" href="http://code.jquery.com/ui/1.9.0/themes/base/jquery-ui.css" />
  <link href="bootstrap.min.css" rel="stylesheet" />
  <link rel="stylesheet" href="http://blueimp.github.com/cdn/css/bootstrap-responsive.min.css" />
  <link rel="stylesheet" href="http://blueimp.github.com/Bootstrap-Image-Gallery/css/bootstrap-image-gallery.min.css" />
  <link type="text/css" href="menu.css" rel="stylesheet" />
  <link rel="stylesheet" type="text/css" href="css/demo.css" />
  <link rel="stylesheet" type="text/css" href="css/style6.css" />
  <link rel="stylesheet" href="css/darkwash.css" media="screen" />
  <script type="text/javascript" src="http://code.jquery.com/jquery-1.8.2.js"></script>
  <script type="text/javascript" src="/resources/demos/external/jquery.bgiframe-2.1.2.js"></script>
  <script type="text/javascript" src="http://code.jquery.com/ui/1.9.0/jquery-ui.js"></script>
  <script type="text/javascript" src="jquery.lettering.js"></script>
  <script  src="tools.js">  </script>  
  <style type="text/css">
        body {
        	font-size: 75%;
        	color:SaddleBrown;
        	background: url(images/background.jpg) no-repeat center top #252525;
        	font-family:Eras Light ITC, Helvetica, sans-serif;
                }        

  </style>
  <script type="text/javascript">
var phase = false;

$.ajaxSetup ({
    // Disable caching of AJAX responses */
    cache: false
});

$(document).ready
(
	function()
	{
		$("input#Update").click
		(
			function()
			{
				var count = <?php echo $num_rows; ?>;
//				alert("1234test"+count);
				var data = {};
				//var i = 1;
				//data['traveltime'+i] = $("input#traveltime"+i).val();
				//alert(data['traveltime1']+ " " + $("input#traveltime"+i).val());
				data['count'] = count;
				for(var i = 1; i <= count; i++)
				{
					data['traveltime'+i] = $("input#traveltime"+i).val();
					data['request'+i] = $("input#request"+i).val();
				}
				data['arrangedate'] = '<?php echo $date; ?>';
				data['arrangetime'] = '<?php echo $time; ?>';
				$.post("updatedatabase.php", data, function()
				{
					location.reload();
     			});
     			//alert('<?php echo $num_rows; ?>');
			}
			
		);

	}
);

  </script>
</head>
<body>
  <div class="container">
   	<iframe id="preview-frame" src="menu.php" name="preview-frame" frameborder="0" noresize="noresize" style="width: 1150px;  height:160px;">
	</iframe>
    <div class="well1">
      <center>
        <h1>候補名單</h1>
		<input name="return" type="button" onclick="history.back()" value="回上一頁" class="btn btn-primary start"/>&nbsp &nbsp			
      </center>    
     <?php
     $count = 1;
     echo "<center><table border=\"1\" cellspacing='0'>";
     echo "<tr><th>流水號</th><th>姓名</th><th>狀態</th><th>帳號</th><th>時段</th><th>旅行時間</th><th>指定車種</th><th>上車區域</th><th>上車地址</th><th>下車區域</th><th>下車地址</th><th>原因</th><th>修改交通時間</th></tr>";
     while($row = mysql_fetch_array($rs1))
     {
     	if($row['抵達時間'] > 0)
     	{
     	$time = $row['時段']."<br />";
		$hour = substr($time, 0, 2);
		$min  = substr($time, 2, 2);
		$timesec = $hour * 3600 + $min *60;
		$traveltime = $row['抵達時間'] - $timesec;
		$traveltimemin = (int)($traveltime / 60);
		$traveltimesec = $traveltime % 60;
		echo "<tr><td>".$count."</td><td>".$row['姓名']."</td><td>".$row['狀態']."</td><td>".$row['帳號']."</td><td>".$row['時段']."</td><td>".$traveltimemin."分".$traveltimesec."秒</td><td>".$row['車種']."</td><td>".$row['上車區域']."</td><td>".$row['上車地址']."</td><td>".$row['下車區域']."</td><td>".$row['下車地址']."</td>";
	}
	else
	{
		echo "<tr><td>".$count."</td><td>".$row['姓名']."</td><td>".$row['狀態']."</td><td>".$row['帳號']."</td><td>".$row['時段']."</td><td>error</td><td>".$row['車種']."</td><td>".$row['上車區域']."</td><td>".$row['上車地址']."</td><td>".$row['下車區域']."</td><td>".$row['下車地址']."</td>";
	}
	if(strcmp($row['狀態'], "候補") == 0)	
	{
		echo "<td>候補預約</td>"	;
	}
	$count++;
	echo "<td><input type=\"hidden\" id=\"request".$count."\" value=\"".$row['識別碼']."\" /><input type=\"text\" id=\"traveltime".$count."\" /></td></tr>";

}
echo "</table></center>";
?>
<center>
	<input type="button" name="update" value="Update" id="Update" />
	<input type="reset" name="reset" value="Reset" />
	</center>
    </div>
  </div>

  <center>
    <img src="images/logo1.png" />
  </center>

  <p align="center"><font color="#FFC125">建議解析度 1024x768 以上觀看</font></p>
</body>
</html>
