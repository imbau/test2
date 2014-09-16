<?php
	include("Mydbconnect.php");
	include("connectInfo.php");
	
	$date = $_GET["date"];
	$user=GetUserInfo();
	$rs = mysql_query("SELECT * FROM arrange_log WHERE TurnoutDate = '".$date."' and `company`='".$user[1]."' ORDER BY no DESC");
	$retString = "";
	while($row = mysql_fetch_array($rs))
	{
		$retString .= $row['no'].",".$row['date'].",".$row['time']." ";
	}
	echo $retString;
?>
