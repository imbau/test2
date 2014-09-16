<?php
	include("Mydbconnect.php");
	include("connectInfo.php");

	$count = $_POST['count'];
	$arrangedate = $_POST['arrangedate'];
	$arrangetime = $_POST['arrangetime'];  
 	$mode = $_POST['mode'];   
	$String="";
	for($index= 1; $index <= $count ; $index++)
	{
		$rs= mysql_query("SELECT * FROM `userrequests` WHERE 識別碼 = ".$_POST['request'.$index]." AND arrangedate = ' $arrangedate' AND arrangetime = '$arrangetime'");
		if($index==1)
			$String="SELECT * FROM `userrequests` WHERE 識別碼 = ".$_POST['request'.$index]." AND arrangedate = ' $arrangedate' AND arrangetime = '$arrangetime'";
		if($row = mysql_fetch_array($rs))
		{   
		   if($mode==0)
				mysql_query("UPDATE userrequests SET 上車區域='".$_POST['startarea'.$index]."', 上車地址 = '".$_POST['start'.$index]."', GETONRemark='".$_POST['startmark'.$index]."',下車區域='".$_POST['endarea'.$index]."',下車地址='".$_POST['end'.$index]."' ,OffCarRemark='".$_POST['endmark'.$index]."' WHERE 識別碼 = ".$_POST['request'.$index]." AND arrangedate = '". $arrangedate."' AND arrangetime = '". $arrangetime."'");
			else
				mysql_query("UPDATE userrequests SET 車種='".$_POST['smallcar'.$index]."' , 上車區域='".$_POST['startarea'.$index]."', 上車地址 = '".$_POST['start'.$index]."',下車區域='".$_POST['endarea'.$index]."',下車地址='".$_POST['end'.$index]."' WHERE 識別碼 = ".$_POST['request'.$index]." AND arrangedate = '". $arrangedate."' AND arrangetime = '". $arrangetime."'");
			
			if($mode!=0)
     		{
				$startaddress = $row['上車區域'].$row['上車地址'];
				$endaddress = $row['下車區域'].$row['下車地址'];
				$updatestartaddress = $_POST['startarea'.$index].$_POST['start'.$index];
				$updateendaddress = $_POST['endarea'.$index].$_POST['end'.$index];
				mysql_query("UPDATE traveltime SET 上車地址 = '".$updatestartaddress."', 下車地址='".$updateendaddress."' WHERE 上車地址 = '".$startaddress."' AND 下車地址 ='".$endaddress."'");
		  }
		}
	}
	echo $_POST['smallcar1'];
?>