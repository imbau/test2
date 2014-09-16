<?php
	include("Mydbconnect.php");
	include("connectInfo.php");
/*	$carid = $_GET['car'];
	$datainformation = $_GET['information'];
	$arrangedate = $_GET['date'];
	$arrangetime = $_GET['time'];*/
	$carid = $_POST['car'];
	$datainformation = $_POST['information'];
	$arrangedate = $_POST['date'];
	$arrangetime = $_POST['time'];
	$splitdata = explode('_', $datainformation);	
	$carreq=array("-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1");
	$carrun=array("-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1");
	$mysqlcartable = mysql_query("SELECT * FROM arrangedtable WHERE date = '".$arrangedate."' AND carid='$carid' AND arrangetime = '".$arrangetime."' AND `carid`='".$carid."'");  
	$mysqlcartablerow=mysql_fetch_array($mysqlcartable);	
	$userindex=0;
	for($runindex=1;$runindex<17;$runindex++)
	{	
	
		if($mysqlcartablerow['run'.$runindex]!=-1)
		{
		
			$carrun[$userindex]=$mysqlcartablerow['run'.$runindex];
			$carreq[$userindex]=$mysqlcartablerow['user'.$runindex];
			$userindex++;
		}
   }
   
     if(count($splitdata) > 3)
	 {
	 $deleteinformation=$splitdata[2]."_".$splitdata[3];
	 	mysql_query("UPDATE userrequests SET arranged = -2 ,Targetdrivers='null' WHERE 識別碼 = ".$splitdata[2]." AND arrangedate = '".$arrangedate."' AND arrangetime = '".$arrangetime."'");
		mysql_query("UPDATE userrequests SET arranged = -2 ,Targetdrivers='null' WHERE 識別碼 = ".$splitdata[3]." AND arrangedate = '".$arrangedate."' AND arrangetime = '".$arrangetime."'");
	 }
	 else
	 {
	  $deleteinformation=$splitdata[2];
	 	mysql_query("UPDATE userrequests SET arranged = -2 ,Targetdrivers='null' WHERE 識別碼 = ".$splitdata[2]." AND arrangedate = '".$arrangedate."' AND arrangetime = '".$arrangetime."'");
	 }
	
   for($runindex=0;$runindex<17;$runindex++)
	{	
	   mysql_query("UPDATE arrangedtable SET run".$userindex."='-1' ,user".$userindex."=' ' WHERE  `date` = '".$arrangedate."' AND `arrangetime` = '".$arrangetime."'  AND `carid`='".$carid."'");
		if($deleteinformation==$carreq[$runindex])
	     { 
			 $carreq[$runindex]="-1";
			 $carrun[$runindex]="-1";
		 }
	 }
	$userindex=1;
  for($runindex=0;$runindex<17;$runindex++)
	{	
	   if( $carrun[$runindex]!=-1)
	   {
			mysql_query("UPDATE arrangedtable SET run".$userindex."='".$carrun[$runindex]."' ,user".$userindex."='".$carreq[$runindex]."' WHERE  `date` = '".$arrangedate."' AND `arrangetime` = '".$arrangetime."'  AND `carid`='".$carid."'");
			 $userindex++;
	   }
   }
   	
   /*
	$rs=mysql_query("SELECT Normal FROM `notdischarged` WHERE date = '".$arrangedate."' AND time = '".$arrangetime."'");
	$rs1=mysql_fetch_array($rs);	
	$rs1['Normal']++;
	mysql_query("UPDATE notdischarged SET Normal =".$rs1['Normal']." WHERE date = '".$arrangedate."' AND time = '".$arrangetime."'");	*/
?>

