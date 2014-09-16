<?php
	include("Mydbconnect.php");
	include("connectInfo.php");
	$arrangedate = $_GET['arrangedate'];
	$arrangetime = $_GET['arrangetime'];
	/***deletable****/
	mysql_query("DELETE FROM  arrangedtable  WHERE `date`='".$arrangedate."' AND `arrangetime`='".$arrangetime."';");
	mysql_query("DELETE FROM  arrange_log  WHERE `date`='".$arrangedate."' AND `time`='".$arrangetime."';");
	mysql_query("DELETE FROM  progress  WHERE `date`='".$arrangedate."' AND `time`='".$arrangetime."' AND  `index`!=0;");
	mysql_query("DELETE FROM  notdischarged  WHERE `date`='".$arrangedate."' AND `time`='".$arrangetime."';");
	mysql_query("DELETE FROM  userrequests  WHERE `arrangedate`='".$arrangedate."' AND `arrangetime`='".$arrangetime."';");
	mysql_query("DELETE FROM  travelinformationofcarsharing  WHERE `date`='".$arrangedate."' AND `arrangetime`='".$arrangetime."';");
	/***********最佳化表格**********************/
	mysql_query("OPTIMIZE TABLE  `arrangedtable`;");
	mysql_query("OPTIMIZE TABLE  `arrange_log`;");
	mysql_query("OPTIMIZE TABLE  `progress`;");
	mysql_query("OPTIMIZE TABLE  `notdischarged`;");
	mysql_query("OPTIMIZE TABLE  `userrequests`;");
	mysql_query("OPTIMIZE TABLE  `travelinformationofcarsharing`;");
    echo "刪除成功";
	
?>

