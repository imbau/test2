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
	$carclass = mysql_query("SELECT * FROM availablecars WHERE date = '".$arrangedate."' AND 車號='$carid' AND time = '".$arrangetime."' ORDER BY no");  
    $carclassrow = mysql_fetch_array($carclass);	
?>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="stylesheet" href="http://code.jquery.com/ui/1.9.0/themes/base/jquery-ui.css" />
<link href="bootstrap.min.css" rel="stylesheet" />
<link rel="stylesheet" href="http://blueimp.github.com/cdn/css/bootstrap-responsive.min.css">
<link rel="stylesheet" href="http://blueimp.github.com/Bootstrap-Image-Gallery/css/bootstrap-image-gallery.min.css">
<link type="text/css" href="menu.css" rel="stylesheet" />
<link rel="stylesheet" type="text/css" href="css/demo.css" />
<link rel="stylesheet" type="text/css" href="css/style6.css" />		
<link rel="stylesheet" href="css/darkwash.css" media="screen" />
<script type="text/javascript" src="http://code.jquery.com/jquery-1.8.2.js"></script>
<script type="text/javascript" src="/resources/demos/external/jquery.bgiframe-2.1.2.js"></script>
<script type="text/javascript" src="http://code.jquery.com/ui/1.9.0/jquery-ui.js"></script>
		 <script type="text/javascript" src="jquery.lettering.js"></script>
		  <script type="text/javascript" src="menu.js"></script>
		  <script type="text/javascript" src="script.js"></script>
		    <script  src="tools.js">  </script>  
<style type="text/css">
        body { font-size: 62.5%; 	
			   color:SaddleBrown;	  
			   background: url(images/background.jpg) no-repeat center top #252525;
	           font-family:Eras Light ITC, Helvetica, sans-serif;}
        label, input { display:block; }
        input.text { margin-bottom:12px; width:95%; padding: .4em; }
        fieldset { padding:0; border:0; margin-top:25px; }
        h1 { font-size: 3.0em; margin: .6em 0; }
        div#users-contain { width: 350px; margin: 20px 0; }
        div#users-contain table { margin: 1em 0; border-collapse: collapse; width: 100%; }
        div#users-contain table td, div#users-contain table th { border: 1px solid #eee; padding: .6em 10px; text-align: left; }
        .ui-dialog .ui-state-error { padding: .3em; }
        .validateTips { border: 1px solid transparent; padding: 0.3em; }
        .shortcol{ width:80px;}
        .longcol{ width:240px;}

</style>
  <script type="text/javascript">
		var arrangedate = '<?php echo $arrangedate; ?>';
        var arrangetime = '<?php echo $arrangetime; ?>';
        var carid = '<?php echo $carid; ?>';
       function pdfcardetail()
			{				
				window.open ("cardetailpdf.php?date="+arrangedate+"&time="+arrangetime+"&carid="+carid ,"Editar notícia", "location=1, status=1, scrollbars=1, width=1200, height=750");
			}
	  function googlemap()
			{				
				window.open ("googlemap.php?date="+arrangedate+"&time="+arrangetime+"&carid="+carid ,"Editar notícia", "location=1, status=1, scrollbars=1, width=1200, height=750");
			}
			/*******************載入頁面div*********************/
function loadmunuphp()
			{		
			 $("#preview").load("menu.php");
			}				
/*******************載入頁面div*********************/	

$(function(){
	loadmunuphp();
});
		</script>
</head>

<body>
  <div class="container">
	 <div id="preview" style="width: 1150px;  height:200px;"></div>
<div class="well1">
<?php
$row = mysql_fetch_array($rs1);	
$interval = $row['timeinterval'];
$starthour =  6;
$endhour = 24;
$tolertime = 600;
$intervalsec = $interval * 3600;
$Carclass=$carclassrow['班別'];
$station=$carclassrow['站名'];
$drivername=$carclassrow['drivername'];

echo "<h2>$carid 出勤總表 <a href='#' class='btn btn-success' onClick=pdfcardetail()>列印班表</a>&nbsp;<a href='#' class='btn btn-success' onclick=googlemap()>googlemap</a><br>";
echo "<center> <h3><table border=\"1\" cellspacing='0'>";
echo "<tr bgcolor='#fafafa' ><td  width='5%'>站名</td><td width='10%'>$station</td><td width='8%'>車牌號碼</td><td width='8%'>$carid(".$carclassrow['呼號'].")</td><td width='5%'>駕駛</td><td width='8%'>$drivername</td><td width='15%'>班別</td><th width='5%'>$Carclass</td><td width='5%'>日期</td><td width='10%'>".$carclassrow['TurnoutDate']."</td>";
echo "<tr bgcolor='#fafafa' ><th colspan=4  align='left'>出發前公里數:<br>加油:";
for($i=0;$i<15;$i++)  echo  "&nbsp";
echo "公升<th colspan=4  align='left'>時間:<br>加油金額: <th colspan=4  align='left'>改派:<br>登錯:";
echo "<tr bgcolor='#fafafa'><th colspan=4  align='left'>回站後總公里數:<br>行駛公里數: <br>總出車時數: <th colspan=2  align='left'>時間:<br>一般趟次: <br>實收總金額:<th colspan=4  align='left'>共乘趟次:    <br>總載客人數: <br>取消趟次: ";
for($i=0;$i<20;$i++)  echo  "&nbsp";
echo  "爽約趟次:";
echo "<tr bgcolor='#fafafa'><th align='left'>趟次<th  colspan=1 align='left'>姓名/帳號<th align='left'>起點時間<th colspan=2 align='left'>起點及障別<th align='left'>抵達時間<th colspan=1  align='left'>目的地點<th align='left'>人數<th   colspan=1 align='left'>公里數<th  align='left'>金額";

for($i=1;$i<=18;$i++)  
{
if($row["run".$i]!=-1)
{
$information = $row["user".$i];
$temp2 = explode('_', $information);
if($information!=''&&$information!=' ')
{
if(count($temp2) > 1)
	{
	$rs2 = mysql_query("SELECT * FROM userrequests WHERE 識別碼 ='".$temp2[0]."' AND arrangedate = '".$arrangedate."' AND arrangetime = '".$arrangetime."'");
	$rs3 = mysql_query("SELECT * FROM userrequests WHERE 識別碼 = '".$temp2[1]."'  AND arrangedate = '".$arrangedate."' AND arrangetime = '".$arrangetime."'");
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
	echo "<th colspan=2  align='left'>(".$row1['障別'].$row1['telephone'].")-".$row1['上車區域']."".$row1['上車地址']."(".$row1['GETONRemark'].")<br>(".$row2['障別'].$row2['telephone'].")-".$row2['上車區域']."".$row2['上車地址']."(".$row2['GETONRemark'].")<th align='left'>$arrivehour1$arrivemin1<br>$arrivehour11$arrivemin11<th colspan=1  align='left'>".$row1['下車區域']."".$row1['下車地址']."(".$row1['OffCarRemark'].")<br>".$row2['下車區域']."".$row2['下車地址']."(".$row2['OffCarRemark'].")<th align='left'><th   colspan=1 align='left'><th  align='left'>"; 
	}
else
	{
	$rs2 = mysql_query("SELECT * FROM userrequests WHERE 識別碼 = '$information' AND arrangedate = '".$arrangedate."' AND arrangetime = '".$arrangetime."'");
	$row1 = mysql_fetch_array($rs2);
	$porint[$i][0]=$row1['sLat'];
	$porint[$i][1]=$row1['sLon'];
	$porint[$i][2]=$row1['eLat'];
	$porint[$i][3]=$row1['eLon'];
	$countpoint=$i;
	$adapttime1 = $row1['抵達時間'] + $tolertime;
	$tempInterval = (int)($adapttime1 / $intervalsec);
	$arrivehour1 =  (int)($adapttime1 / 3600);
	$arrivemin1 = (int)(($adapttime1 % 3600) / 60);
	if($arrivehour1<10)
		$arrivehour1="0".$arrivehour1;
	if($arrivemin1<10)
		$arrivemin1="0".$arrivemin1;
	if($row1['狀態']=="候補")
		$str2="候補/";
		else
	    $str2="";
	echo "<tr bgcolor='#fafafa'><th align='left'> $i<th colspan=1 align='left'>".$str2.$row1['姓名']."/".$row1['帳號']."<th  align='left'>".$row1['時段']."";
	echo "<th colspan=2  align='left'>(".$row1['障別'].$row1['telephone'].")-".$row1['上車區域']."".$row1['上車地址']."(".$row1['GETONRemark'].")<th align='left'>$arrivehour1$arrivemin1<th colspan=1  align='left'>".$row1['下車區域']."".$row1['下車地址']."(".$row1['OffCarRemark'].")<th align='left'><th   colspan=1 align='left'><th  align='left'>"; 
	}
}
}
 }
echo "<tr bgcolor='#fafafa'><th align='left'>覆核<th colspan=1 align='left'><th align='left'>調度員<th colspan=2 align='left'><th  align='left'>排班員<th colspan=2 align='left'><th align='left'>駕駛員<th colspan=2 align='left'>";

echo "</table></center>";

$worktime=$row['worktime'];
$resttime=$row['resttime1'];
if($resttime!="未選定")
{
$resttime1 = explode(':', $resttime);
$resthour=floor($resttime1[0]/2);
if($resttime1[0]%2==0)
	$restmin="00";
	else
	$restmin="30";
	
$resttime1[0]=$resttime1[0]+1;	
$resthour1=floor($resttime1[0]/2);
if($resttime1[0]%2==0)
	$restmin1="00";
	else
	$restmin1="30";	
	
$resthour2=floor($resttime1[1]/2);
if($resttime1[1]%2==0)
	$restmin2="00";
	else
	$restmin2="30";
	
$resttime1[1]=$resttime1[1]+1;	
$resthour3=floor($resttime1[1]/2);
if($resttime1[1]%2==0)
	$restmin3="00";
	else
	$restmin3="30";
$restttime=$resthour.':'.$restmin.'~'.$resthour1.':'.$restmin1.' 與 '.$resthour2.':'.$restmin2.'~'.$resthour3.':'.$restmin3;
}
else
{
	$restttime=$resttime;
}

echo "<center>休息時間:$restttime</center> ";
?>
</p>
		</div >
 </div>
    <center>
	<img src="images/logo1.png"  />		
	</center>
	<p align=center><font color=#FFC125>建議解析度 1024x768 以上觀看</font></p>
	</body>
</html>
