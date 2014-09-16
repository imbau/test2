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
  <title>候補名單</title>
  <link rel="stylesheet" href="http://code.jquery.com/ui/1.9.0/themes/base/jquery-ui.css" />
  <link href="bootstrap.min.css" rel="stylesheet" />
  <link rel="stylesheet" href="http://blueimp.github.com/cdn/css/bootstrap-responsive.min.css" />
  <link rel="stylesheet" href="http://blueimp.github.com/Bootstrap-Image-Gallery/css/bootstrap-image-gallery.min.css" />
   <link rel="stylesheet" href="css/darkwash.css" media="screen" />
  <script type="text/javascript" src="http://code.jquery.com/jquery-1.8.2.js"></script>
  <script type="text/javascript" src="http://code.jquery.com/ui/1.9.0/jquery-ui.js"></script>
 
  <style type="text/css">
        body {
        	font-size: 90%;
        	color:#551A8B;
        	background: url(images/carrbg.jpg) no-repeat ;
        	background-attachment: fixed;
        	font-family:Eras Light ITC, Helvetica, sans-serif;
        	font-weight: bold;
                }        

  </style>
  <script type="text/javascript">
var phase = false;

$.ajaxSetup ({
    // Disable caching of AJAX responses */
    cache: false
});
var arrangedate = '<?php echo $arrangedate; ?>';
var arrangetime = '<?php echo $arrangetime; ?>';
$(document).ready
(
	function()
	{
		$("input#Update").click
		(
			function()
			{
				var count = <?php echo $num_rows; ?>;
				var data = {};				
				data['count'] = count;
				for(var i = 1; i <= count; i++)
				{
					data['traveltime'+i] = $("input#traveltime"+i).val();
					data['request'+i] = $("input#request"+i).val();					
				}
				data['arrangedate'] = arrangedate;
				data['arrangetime'] = arrangetime;
				$.post("updatedatabase.php", data, function()
				{
					location.reload();
     			});
     			
			}
			
		);

	}
);	
function Candidate()
			{		
			$.get("<?php  echo $linkurlport; ?>/WebRoutingArranger/RoutingArrangertest.view", {arrangetime: arrangetime, arrangedate: arrangedate, mode: 1},
					function(response) 
					{  
					window.location.reload(); 
				});		
			window.open('<?  echo $linkurlport;?>'+"/routingarrange/redirect.php?date="+arrangedate+"&time="+arrangetime+"&mode=4", 'Test', config='height=100,width=500');
		   	window.close();								
			}

  </script>
</head>
<body>
  <div class="container">    
    <form>
      <center>
        <h1>候補名單</h1>		        
        <a href='#' class='btn btn-primary' onclick=Candidate() >候補排班</a>
     	<input type="button" name="update" value="更新旅行時間" id="Update" class="btn btn-primary start" />
     	<input type="reset" name="reset" value="清除輸入資料" class="btn btn-primary start" />	     	
      </center>    
     <?php
     $count = 1;
     echo "<center><table border=\"1\" cellspacing='0'>";
     echo "<tr><th>流水號</th><th>姓名</th><th>狀態</th><th>帳號</th><th>時段</th><th>旅行時間</th><th>指定車種</th><th>上車區域</th><th>上車地址</th><th>下車區域</th><th>下車地址</th><th>原因</th><th>修改交通時間(以秒為單位)</th></tr>";
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
</form>
    </div>
</body>
</html>
