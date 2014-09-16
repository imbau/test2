<?php
	include("Mydbconnect.php");
	include("connectInfo.php");

$count = $_POST['count'];
$arrangedate = $_POST['arrangedate'];
$arrangetime = $_POST['arrangetime'];


//$_POST['request'.$i]
for($i = 1; $i <= $count ; $i++)
{
	if($_POST['traveltime'.$i] == "")
	{
		continue;	
	}
	//echo "SELECT 識別碼, 上車區域, 上車地址, 下車區域, 下車地址, 時段 FROM userrequests WHERE 識別碼 = ".$_POST['request'.$i]." AND arrangedate = '". $arrangedate."' AND arrangetime = '". $arrangetime."'";
	$rs1 = mysql_query("SELECT 識別碼, 上車區域, 上車地址, 下車區域, 下車地址, 時段 FROM tempuserrequests WHERE 識別碼 = ".$_POST['request'.$i]." AND arrangedate = '". $arrangedate."' AND arrangetime = '". $arrangetime."'");
	if($row = mysql_fetch_array($rs1))
	{
		//echo $row['識別碼']."<br />";
		$time = $row['時段']."<br />";
		$hour = substr($time, 0, 2);
		$min  = substr($time, 2, 2);
		$sec = $hour*3600 + $min*60;
		$traveltime = $_POST['traveltime'.$i];
		mysql_query("UPDATE tempuserrequests SET 抵達時間 = ".($sec + $traveltime)." WHERE 識別碼 = ".$_POST['request'.$i]." AND arrangedate = '". $arrangedate."' AND arrangetime = '". $arrangetime."'");
		$startaddress = $row['上車區域'].$row['上車地址'];
		$endaddress = $row['下車區域'].$row['下車地址'];
		$rs2 = mysql_query("SELECT 識別碼, 預約時間 FROM traveltime WHERE 上車地址 = '".$startaddress."' AND 下車地址 ='".$endaddress."'");
		while($row2 = mysql_fetch_array($rs2))
		{
			//echo $row2['識別碼'];
			if((int)($row2['預約時間'] / 3600) == (int)($sec / 3600))
			{
				mysql_query("UPDATE traveltime SET 原始交通時間 = ".$traveltime.", 修正交通時間 = ".$traveltime." WHERE 識別碼 = ".$row2['識別碼']);
				break;
			}
		}
		//	echo $sec."<br />";
		//	echo $_POST['traveltime1'];
	}
}
//判斷是否還有有問題的地址
$rs = mysql_query("SELECT 識別碼, 上車區域, 上車地址, 下車區域, 下車地址 FROM tempuserrequests WHERE 抵達時間 = -1");
if(mysql_fetch_array($rs))
{
	echo "remain";
}
else
{
	echo "clear";
}


?>
