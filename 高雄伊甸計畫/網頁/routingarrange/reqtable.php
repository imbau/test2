<?php
	include("Mydbconnect.php");	
	require('chinese-unicode.php');
	$option = $_GET["option"];
	$carid = $_GET["carid"];	
	$mode = $_GET["mode"];	
	$arrangedate = "";
	$arrangetime = "";
	set_time_limit(0);
	$arrangedate =$_GET["date"];
	$arrangetime =$_GET["time"];	
    $filename = mysql_query("SELECT Reqtable FROM `arrange_log` WHERE  `date`='".$arrangedate."' and `time`='".$arrangetime."' ");	
	$file = mysql_fetch_array($filename);
	$url=$linkurlport."download/"; //路徑位置
//	echo $linkurlport;
	header("Content-type:application");
	header("Content-Disposition: attachment; filename=".$file['Reqtable']);
	readfile($url.str_replace("@","",$file['Reqtable']));	
	exit(0);
	
?>
