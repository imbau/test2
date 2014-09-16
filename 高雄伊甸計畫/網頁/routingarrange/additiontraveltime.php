<?php
	include("Mydbconnect.php");
	include("connectInfo.php");
	$count = 0;
	$date = $_GET['date'];
	$time = $_GET['time'];
	//$count = 0;
	$rs1 = mysql_query("SELECT 識別碼, 時段, 上車區域, 上車地址, 下車區域, 下車地址, 抵達時間 FROM userrequests WHERE 抵達時間 != -1 AND arrangedate = '".$date."' AND arrangetime = '".$time."'");
	$num_rows = mysql_num_rows($rs1);

?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript" src="http://code.jquery.com/jquery-1.8.2.js"></script>
<script type="text/javascript">
$(document).ready
(
	function()
	{
		$("input#update").click
		(
			function()
			{
				//alert("tees");
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
				$.post("additionupdatedatabase.php", data, function(data)
				{
     				//alert(data);
     				//var temp = data.split(" ");
     				if(data.trim() == "remain")
     				{
     					var answer = confirm("仍有未輸入的交通時間，是否仍要排班?");
						if (answer)
						{
							window.location = 'additionredirect.php';
							//alert("remaintest");
						}
						else
						{
							location.reload();
						}
					}
					else if(data.trim() == "clear")
					{
						window.location = 'additionredirect.php';
						//alert("cleartest");
					}
     			});
			}
		)
	}
);
</script>

<title>Travel Time</title>
</head>

<body>
<center><h1>兩地點間交通時間列表</h1></center>
<center><input type="button" id="back" value="回上一頁" onclick="history.back(-1)" /></center>
<form name="updateform">
<center><table border="1" cellspacing='0'>
<tr><th>上車區域</th><th>上車地址</th><th>下車區域</th><th>下車地址</th><th>預估旅行時間(秒)</th><th>取得旅行時間(秒)</th></tr>
<?php

while($row = mysql_fetch_array($rs1))
{
//	echo "上車地址: ".$row['上車區域'].$row['上車地址'].", 下車地址: ".$row['下車區域'].$row['下車地址']."<input type=\"hidden\" name=\"".$count."\" value=\"".$row['識別碼']."\" /><input type=\"text\" name=\"traveltime".$count."\" /><br />";
	$time = $row['時段'];
	$hour = substr($time, 0, 2);
	$min  = substr($time, 2, 2);
	$timesec = $hour * 3600 + $min *60;
	$traveltime = $row['抵達時間'] - $timesec;
	$traveltimemin = (int)($traveltime / 60);
	$traveltimesec = $traveltime % 60;
	$count++;
	echo "<tr  valign='middle' bgcolor='#fafafa' style='cursor:default' onMouseOver=\"this.style.backgroundColor='#DEF8FA'\" onMouseOut=\"this.style.backgroundColor='#FFFFFF'\"><td>".$row['上車區域']."</td>";
	echo "<td>".$row['上車地址']."</td>";
	echo "<td>".$row['下車區域']."</td>";
	echo "<td>".$row['下車地址']."</td>";
	echo "<td><input type=\"hidden\" id=\"request".$count."\" value=\"".$row['識別碼']."\" /><input type=\"text\" id=\"traveltime".$count."\" /></td><td>".$traveltimemin."分".$traveltimesec."秒</td></tr>";
	
}


?>
</table></center>

<center><input type="button" id="update" value="Update" />
<input type="reset" name="reset" value="Reset" /></center>
</form>
</body>
</html>