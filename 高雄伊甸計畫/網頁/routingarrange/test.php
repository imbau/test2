<?php
	include("Mydbconnect.php");
	include("connectInfo.php");
	$option = $_GET["option"];
	$carid = $_GET["carid"];	
	$arrangedate = "";
	$arrangetime = "";
	set_time_limit(0);
	$arrangedate =$_GET["date"];
	$arrangetime =$_GET["time"];
	
	$car_no = 0;
	$rs1 = mysql_query("SELECT * FROM arrangedtable WHERE date = '".$arrangedate."' AND carid='$carid' AND arrangetime = '".$arrangetime."' ORDER BY no");  
	$num_rows = mysql_num_rows($rs1);   
	$carclass = mysql_query("SELECT 班別 FROM availablecars WHERE date = '".$arrangedate."' AND 車號='$carid' AND time = '".$arrangetime."' ORDER BY no");  
    $carclassrow = mysql_fetch_array($carclass);	
?>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<?php
$row = mysql_fetch_array($rs1);	
$interval = $row['timeinterval'];
$starthour =  6;
$endhour = 24;
$tolertime = 600;
$intervalsec = $interval * 3600;
$Carclass=$carclassrow['班別'];
echo "<center> <h3><table border=\"1\" cellspacing='0'>";
echo "<tr bgcolor='#fafafa' ><td  width='5%'>站名</td><td width='10%'></td><td width='8%'>車牌號碼</td><td width='8%'>$carid</td><td width='5%'>駕駛</td><td width='8%'></td><td width='15%'>班別</td><th width='5%'>$Carclass</td><td width='5%'>日期</td><td width='10%'>$arrangedate</td>";
echo "<tr bgcolor='#fafafa' ><th colspan=4  align='left'>出發前公里數:<br>加油:";
for($i=0;$i<15;$i++)  echo  "&nbsp";
echo "公升<th colspan=4  align='left'>時間:<br>加油時間: <th colspan=4  align='left'>改派:<br>登錯:";
echo "<tr bgcolor='#fafafa'><th colspan=4  align='left'>回站後總公里數:<br>行駛公里數: <br>總出車時間: <th colspan=2  align='left'>時間:<br>一般趟次: <br>實收總金額:<th colspan=4  align='left'>共乘趟次:    <br>總載客人數: <br>取消趟次: ";
for($i=0;$i<20;$i++)  echo  "&nbsp";
echo  "爽約趟次:";
echo "<tr bgcolor='#fafafa'><th align='left'>趟次<th  colspan=1 align='left'>姓名/帳號<th align='left'>起點時間<th colspan=2 align='left'>起點及障別<th align='left'>抵達時間<th colspan=1  align='left'>目的地點<th align='left'>人數<th   colspan=1 align='left'>公里數<th  align='left'>金額";

for($i=1;$i<9;$i++)  
{
$information = $row["user".$i];
$temp2 = explode('_', $information);
if($information!=''&&$information!=' ')
{
if(count($temp2) > 1)
{
$rs2 = mysql_query("SELECT 抵達時間, 姓名, 帳號,電話,時段, 上車區域, 上車地址, 下車區域, 下車地址 FROM userrequests WHERE 識別碼 ='".$temp2[0]."' AND arrangedate = '".$arrangedate."' AND arrangetime = '".$arrangetime."'");
$rs3 = mysql_query("SELECT 抵達時間, 姓名, 帳號,電話,時段, 上車區域, 上車地址, 下車區域, 下車地址 FROM userrequests WHERE 識別碼 = '".$temp2[1]."'  AND arrangedate = '".$arrangedate."' AND arrangetime = '".$arrangetime."'");
$row1 = mysql_fetch_array($rs2);	
$row2 = mysql_fetch_array($rs3);	
$adapttime1 = $row1['抵達時間'] + $tolertime;
$tempInterval = (int)($adapttime1 / $intervalsec);
$arrivehour1 =  (int)($adapttime1 / 3600);
$arrivemin1 = (int)(($adapttime1 % 3600) / 60);
$adapttime2 = $row2['抵達時間'] + $tolertime;
$tempInterval1 = (int)($adapttime2 / $intervalsec);
$arrivehour11 =  (int)($adapttime2 / 3600);
$arrivemin11 = (int)(($adapttime2% 3600) / 60);
echo "<tr bgcolor='#fafafa'><th align='left'> $i<th colspan=1 align='left'>共乘:<br>".$row1['姓名']."/".$row1['帳號']."<br>".$row2['姓名']."/".$row2['帳號']."<th  align='left'>".$row1['時段']."<br>".$row2['時段']."";
echo "<th colspan=2  align='left'>(".$row1['電話'].")-".$row1['上車區域']."".$row1['上車地址']."<br>(".$row2['電話'].")-".$row2['上車區域']."".$row2['上車地址']."<th align='left'>$arrivehour1:$arrivemin1<br>$arrivehour11:$arrivemin11<th colspan=1  align='left'>".$row1['下車區域']."".$row1['下車地址']."<br>".$row2['下車區域']."".$row2['下車地址']."<th align='left'><th   colspan=1 align='left'><th  align='left'>"; 
}
else{
	$rs2 = mysql_query("SELECT 抵達時間, 姓名, 帳號,電話,時段, 上車區域, 上車地址, 下車區域, 下車地址 FROM userrequests WHERE 識別碼 = '$information' AND arrangedate = '".$arrangedate."' AND arrangetime = '".$arrangetime."'");
	$row1 = mysql_fetch_array($rs2);	
	$adapttime1 = $row1['抵達時間'] + $tolertime;
	$tempInterval = (int)($adapttime1 / $intervalsec);
	$arrivehour1 =  (int)($adapttime1 / 3600);
	$arrivemin1 = (int)(($adapttime1 % 3600) / 60);
	if($arrivehour1<10)
		$arrivehour1="0".$arrivehour1;
	if($arrivemin1<10)
		$arrivemin1="0".$arrivemin1;
	echo "<tr bgcolor='#fafafa'><th align='left'> $i<th colspan=1 align='left'>".$row1['姓名']."/".$row1['帳號']."<th  align='left'>".$row1['時段']."";
	echo "<th colspan=2  align='left'>(".$row1['電話'].")-".$row1['上車區域']."".$row1['上車地址']."<th align='left'>$arrivehour1$arrivemin1<th colspan=1  align='left'>".$row1['下車區域']."".$row1['下車地址']."<th align='left'><th   colspan=1 align='left'><th  align='left'>"; 
}
}
 }
echo "<tr bgcolor='#fafafa'><th align='left'>覆核<th colspan=1 align='left'><th align='left'>調度員<th colspan=2 align='left'><th  align='left'>排班員<th colspan=2 align='left'><th align='left'>駕駛員<th colspan=2 align='left'>";

echo "</table></center>";
$worktime=$row['worktime'];
$resttime=$row['resttime']*3600;
$resttime=$resttime+$worktime;
$resttimehr=floor($resttime/3600);
$resttimemin = (int)(($resttime % 3600) / 60);
$resttimehr1=$resttimehr+1;
 
 if($row['resttime']<0)
 {echo "<center>休息時間:時間未排定</center> ";}
 else
 {echo "<center>休息時間:$resttimehr:$resttimemin~$resttimehr1:$resttimemin</center> ";}
?>