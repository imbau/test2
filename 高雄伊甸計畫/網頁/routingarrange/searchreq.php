<?php
    include("Mydbconnect.php");
	include("connectInfo.php");
	$arrangetime = $_GET['time'];
	$arrangedate = $_GET['date'];
	$mode = $_GET['mode'];
	$area = $_GET['area'];
	$result = array();
	$sql="SELECT *FROM userrequests WHERE arranged<=0 AND arrangedate = '".$arrangedate."' AND arrangetime = '".$arrangetime."'";
	if($mode==2)
		$sql=$sql." AND 狀態='候補'";
  
      if($area=="undefined"||$area=="-1")
	  {
	  }
	  else if($area!="")
		$sql=$sql." AND  (`上車區域`='$area' or  `下車區域`='$area' )";	
		
		$rs1 = mysql_query($sql);	
	$num_rows = mysql_num_rows($rs1);
	$i=1;
	$result['count']=$num_rows;
	
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
			$result["name".$i]=urlencode($row['姓名']);
			$result["staus".$i]=urlencode($row['狀態']);
			$result["account".$i]=urlencode($row['帳號']);
			$result["Period".$i]=urlencode($row['時段']);
			$result["time".$i]=urlencode($traveltimemin."分".$traveltimesec."秒");
			$result["cartype".$i]=urlencode($row['車種']);
			$result["startaera".$i]=urlencode($row['上車區域']);
			$result["startaddress".$i]=urlencode($row['上車地址']);
			$result["endtaera".$i]=urlencode($row['下車區域']);
			$result["endaddress".$i]=urlencode($row['下車地址']);
			$i++;
		}
	
	}	
	if($num_rows>0)	
	{
		$respJsonStr = urldecode(json_encode($result));	
		echo $respJsonStr;
	}
	else
		echo $result['count']=0;
	
?>