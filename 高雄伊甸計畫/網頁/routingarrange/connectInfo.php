<?php
$ip = getenv("SERVER_ADDR");    
$myip = $_SERVER['REMOTE_ADDR'];		
$linkurl = "http://$ip";
$linkurlport = "http://$ip";
$folder = "C:/AppServ/www"; 


function GetUserInfo() 
{ 
	$UserInfo=array("-1","-1");
	$sql = "SELECT * FROM onlineuser where `IP`='".$_SERVER['REMOTE_ADDR']."'";	
	$result = mysql_query($sql);		
	if(mysql_num_rows($result)>0)		
	{
		$User = mysql_fetch_array($result);	
		$UserInfo[0]=$User[1];
		$UserInfo[1]=$User[4];
	}
	  return $UserInfo; 
} 
?>

