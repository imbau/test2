<?php
include("Mydbconnect.php");
$option = $_GET["option"];
if($option==0)
{
	$rs = mysql_query("SELECT * FROM `progress` WHERE `index`=0");
	if($row = mysql_fetch_array($rs))
	{
	 $arrangedate =$row['date'];
	 $arrangetime =$row['time'];
	 $option=3;	
	}
}
else 
	{
		$arrangedate =$_GET['date'];
		$arrangetime =$_GET['time'];
	}

$rs = mysql_query("SELECT * FROM progress WHERE `index`='".$option."' and `date`='".$arrangedate."' and `time`='".$arrangetime."'");
if($row = mysql_fetch_array($rs))
	{
	  echo $row['percent'];
	}

?>