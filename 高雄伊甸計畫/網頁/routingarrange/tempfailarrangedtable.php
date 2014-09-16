<?php
	include("Mydbconnect.php");
	include("connectInfo.php");
	$arrangetime = $_GET['time'];
	$arrangedate = $_GET['date'];

?><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>可調適性動態排班系統 </title> 	
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
<style type="text/css">
        body { font-size: 75%; 	
			   color:SaddleBrown;	  
			   background: url(images/background.jpg) no-repeat center top #252525;
	           font-family:Eras Light ITC, Helvetica, sans-serif;
			   }	    
</style>


<script type="text/javascript">
$(document).ready
(
	function()
	{
		$("input#adjust").click
		(
			function()
			{
				<?php
					mysql_query("DELETE FROM userrequests WHERE arrangedate ='".$arrangedate."' AND arrangetime = '".$arrangetime."'");
					mysql_query("DELETE FROM arrangedtable WHERE date = '".$arrangedate."' AND arrangetime = '".$arrangetime."'");
					mysql_query("INSERT INTO userrequests (識別碼, arrangedate, arrangetime, 抵達時間, 狀態, 共乘意願, 姓名, 帳號, 電話, 時段, 上車區域, 上車地址, sLat, sLon, sX, sY, 下車區域, 下車地址, eLat, eLon, eX, eY, 訂車時間, 車種, arranged) SELECT 識別碼, arrangedate, arrangetime, 抵達時間, 狀態, 共乘意願, 姓名, 帳號, 電話, 時段, 上車區域, 上車地址, sLat, sLon, sX, sY, 下車區域, 下車地址, eLat, eLon, eX, eY, 訂車時間, 車種, arranged FROM tempuserrequests WHERE arrangedate ='".$arrangedate."' AND arrangetime = '".$arrangetime."'");
					mysql_query("INSERT INTO arrangedtable (carid, timeinterval, resttime, date, arrangetime, worktime, cartype, run1, run2, run3, run4, run5, run6, run7, run8, run9, run10, run11, run12, user1, user2, user3, user4, user5, user6, user7, user8, user9, user10, user11, user12) SELECT carid, timeinterval, resttime, date, arrangetime, worktime, cartype, run1, run2, run3, run4, run5, run6, run7, run8, run9, run10, run11, run12, user1, user2, user3, user4, user5, user6, user7, user8, user9, user10, user11, user12 FROM temparrangetable WHERE date = '".$arrangedate."' AND arrangetime = '".$arrangetime."'");
				?>
				//alert("暫時沒有沒有功能!!");
				window.location = 'testarrangedtable.php?option=-1&arrangedate=<?php echo $arrangedate; ?>&arrangetime=<?php echo $arrangetime; ?>';
			}
		);
	}
);
$(function() {
					$("#letter-container h2 a").lettering();
				});
		$(function(){
		// 幫 #menu li 加上 hover 事件
		$('#menu>li').hover(function(){
			// 先找到 li 中的子選單
			var _this = $(this),
				_subnav = _this.children('ul');
			
			// 變更目前母選項的背景顏色
			// 同時顯示子選單(如果有的話)
			_this.css('backgroundColor', '#06c').siblings().css('backgroundColor', '');
			_subnav.css('display', 'block');
		} , function(){
			// 同時隱藏子選單(如果有的話)
			// 也可以把整句拆成上面的寫法
			$(this).children('ul').css('display', 'none');
		});
		
		// 取消超連結的虛線框
		$('a').focus(function(){
			this.blur();
		});
	});

</script>
</head>

<body>
<body>
  <div class="container">
   <div id="letter-container" class="letter-container">		
					 <div class="page-header">
					 <img src="images/logo.png" width="80px" /><h2><a>可調適性動態排班系統</a></h2>
					 <ul id="menu">
					<li>
						<a href="#" onclick="window.open('map.html','newwindows','height=500, width=700');" >連絡我們</a>							
					</li>
		             <li>
						<a href="#">關於我們</a>
						<ul>
							<li><a href="http://www.ycswf.org.tw/" TARGET="_blank" >育成社會福利基金會</a></li>
							<li><a href="http://web.ncku.edu.tw/bin/home.php"  TARGET="_blank">成功大學</a></li>
							<li><a href="http://www.eden.org.tw/"  TARGET="_blank">伊甸社會福利基金會</a></li>	
							<li><a href="http://www.iii.org.tw/"   TARGET="_blank">資策會</a></li>
						</ul>
					</li>					
					<li><a href="<?php  echo $linkurlport; ?>/routingarrange/arranger.php">首頁</a></li>					
					</ul>
					</div >
				   </div >
				   <div class="well1">
<p><input name="return" type="button" onclick="history.back()" value="回上一頁"/></p>
<center><h1>排班結果</h1></center>
<center>兩地點間的預估交通時間:  <input name="input" type="button" onclick="location.href='inputerror.php'" value="輸入" /></center>

<?php
$rs1 = mysql_query("SELECT *FROM userrequests WHERE arranged != 1 AND arrangedate = '".$arrangedate."' AND arrangetime = '".$arrangetime."'");
echo "<center><table border=\"1\" cellspacing='0' style='font-size:15px;'>";
echo "<tr><th>姓名</th><th>狀態</th><th>帳號</th><th>時段</th><th>旅行時間</th><th>指定車種</th><th>上車區域</th><th>上車地址</th><th>下車區域</th><th>下車地址</th><th>原因</th></tr>";
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
		echo "<tr><td >".$row['姓名']."</td><td>".$row['狀態']."</td><td >".$row['帳號']."</td><td>".$row['時段']."</td><td>".$traveltimemin."分".$traveltimesec."秒</td><td>".$row['車種']."</td><td>".$row['上車區域']."</td><td>".$row['上車地址']."</td><td>".$row['下車區域']."</td><td>".$row['下車地址']."</td>";
	}
	else
	{
		echo "<tr><td >".$row['姓名']."</td><td>".$row['狀態']."</td><td>".$row['帳號']."</td><td>".$row['時段']."</td><td>error</td><td>".$row['車種']."</td><td>".$row['上車區域']."</td><td>".$row['上車地址']."</td><td>".$row['下車區域']."</td><td>".$row['下車地址']."</td>";
	}
	if(strcmp($row['狀態'], "候補") != 0)
	{
		if($row['arranged'] == 0)
		{
			echo "<td>找不到合適車輛!!</td></tr>";
		}
		else
		{
			echo "<td>地址格式可能有錯誤!!</td></tr>";
		}
	}
	else
	{
		echo "<td>候補預約</td></tr>"	;
	}
}

echo "</table></center>"
?>
<center>接受調整休息時間後的排班:  <input id="adjust" type="button" value="接受" /></center>
</div>
</div >
   <center>
	<img src="images/logo1.png"  />		
	</center>
	<p align=center><font color=#FFC125>建議解析度 1024x768 以上觀看</font></p>
</body>
</html>