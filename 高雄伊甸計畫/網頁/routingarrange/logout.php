<?php session_start(); ?>
<!--上方語法為啟用session，此語法要放在網頁最前方-->
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<?php
//連接資料庫
//只要此頁面上有用到連接MySQL就要include它
include("Mydbconnect.php");
include("connectInfo.php");
mysql_query("DELETE FROM onlineuser WHERE `IP`='$myip'"); 
echo '<meta http-equiv=REFRESH CONTENT=1;url=login.php>';		
?>