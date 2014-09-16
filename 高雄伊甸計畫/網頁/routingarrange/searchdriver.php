<?php
    include("Mydbconnect.php");
	include("connectInfo.php");
	$arrangetime = $_GET['time'];
	$arrangedate = $_GET['date'];	
	$area = $_GET['area'];
	$mode = $_GET['mode'];
	$result = array();
	if($mode==1)
		$sql="SELECT * FROM `availablecars`  WHERE date = '".$arrangedate."' AND time = '".$arrangetime."'";  
	else if($mode==0)
		$sql="SELECT * FROM `car`  WHERE 1";  
	else if($mode==2)
	{
		$sql="SELECT * FROM `arrangedtable`  WHERE (resttime1='未選定' or resttime1='null:null')AND date = '".$arrangedate."' AND arrangetime = '".$arrangetime."'"; 
	}
	
	 if($mode<2)
    {
		if($area=="undefined"||$area=="-1")
		{
		}
		else if($area!=""&&$mode==1)
		{
			$sql=$sql." AND 站名='$area'";	
		}
		else
		{
			$sql=$sql." AND station='$area'";	
		}
			$result=driverarray($sql);
	}
	else if($mode==2)
	{
		$index=1;
		$rs1 = mysql_query($sql);	
		$sql="SELECT * FROM `availablecars`  WHERE date = '".$arrangedate."' AND time = '".$arrangetime."'";  
		while($row = mysql_fetch_array($rs1))
		{
		  if($index==1)	
			{	
				$sql=$sql."and (車號='".$row[ 'carid']."'";	
			}
			else
			{
				$sql=$sql."or 車號='".$row[ 'carid']."'";	
			}
			$index++;
		}
			$sql=$sql.")";	
		$result=driverarray($sql);
	
	}

   function driverarray($sql)	
   {
		$result = array();
		$rs1 = mysql_query($sql);	
		$num_rows = mysql_num_rows($rs1);
		$i=1;
		$result['count']=$num_rows;
		while($row = mysql_fetch_array($rs1))
		{
				$result["no".$i]=urlencode($row[0]);
				$result["station".$i]=urlencode($row[1]);
				$result["callnumber".$i]=urlencode($row[2]);
				$result["telephone".$i]=urlencode($row[3]);
				$result["drivername".$i]=urlencode($row[4]);
				$result["carid".$i]=urlencode($row[5]);
				$result["shift".$i]=urlencode($row[6]);
				$result["cartype".$i]=urlencode($row[7]);
				$result["worktime".$i]=urlencode($row[8]);
				$result["address".$i]=urlencode($row[9]);
				$result["parkname".$i]=urlencode($row[10]);
				$i++;	
		}	
	return $result;
   }

	
	if( $result['count']>0)	
	{
		$respJsonStr = urldecode(json_encode($result));	
		echo $respJsonStr;
	}
	else
		echo $result['count']=0;
	
?>