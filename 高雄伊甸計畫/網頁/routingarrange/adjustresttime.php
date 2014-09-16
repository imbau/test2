<?php
	include("Mydbconnect.php");
	include("connectInfo.php");
	
	$arrangedate = $_GET["date"];
	$arrangetime = $_GET["time"];

	set_time_limit(0);
	$car_no = 0;
	$starthour =  6;
	$endhour = 24;
	$tolertime = 600;
	$rs1 = mysql_query("SELECT * FROM temparrangetable WHERE date = '".$arrangedate."' AND arrangetime = '".$arrangetime."' ORDER BY no");
	$num_rows = mysql_num_rows($rs1);

?>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
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
        body { font-size: 62.5%; 	
			   color:SaddleBrown;	  
			   background: url(images/background.jpg) no-repeat center top #252525;
	           font-family:Eras Light ITC, Helvetica, sans-serif;
			   }		
</style>


<script type="text/javascript">
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
					</ul></div >
				   </div >
 <div class="well1">
<div id="page">
	<h1>調整休息時間後，排班結果</h1>
	<p>車輛總數: <?php echo $num_rows;?>台<br />
	   排班日期: <?php echo $arrangedate; ?><br />
	   排班時間: <?php echo $arrangetime; ?></p>
	<input onclick="location.href='arranger.php'" type="button" value="排班頁面" class="btn btn-primary ">&nbsp
	<input onclick="location.href='tempfailarrangedtable.php?date=<?php echo $arrangedate;?>&time=<?php echo $arrangetime;?>'" type="button" value="未排入預約" class="btn btn-warning ">
<?php
echo "<div style=\"width:2000px;overflow:auto;\">";
echo "<table border=\"1\" cellspacing='0'>";
echo "<tr style=\"width:30px;\"><th>車輛編號</th><th>日期</th><th>車牌號碼</th><th>工作時段</th><th>車種</th>";
$row = mysql_fetch_array($rs1);
$interval = $row['timeinterval'];
$number = $starthour -1;
$divi = (int)(($endhour - $starthour)/$interval);
$temp = (int)(1/$interval);
//表格標題
for($i = 0; $i < $divi; $i++)
{
	if($i % $temp == 0)
	{
		$number++;		
		echo "<th>".$number.":00</th>";
	}
	else
	{
		echo "<th>".$number.":".(int)(60/$temp*($i%$temp))."</th>";
	}
}
echo "</tr>";

$offset = (int)($starthour/$interval);
$stopinterval = (int)($endhour/$interval);
$intervalsec = $interval * 3600;
$intervalcount = 24 / $interval;


do
{
	$car_no++;
	echo "<tr align='center' valign='middle' bgcolor='#fafafa' style='cursor:default' onMouseOver=\"this.style.backgroundColor='#DEF8FA'\" onMouseOut=\"this.style.backgroundColor='#FFFFFF'\"><td>".$car_no."</td><td>"
	.$row['date']."</td><td class=\"carid\">".$row['carid']."<input type=\"hidden\" value=\"".$row['carid']."\" name=\"".$row['carid']."\" /></td>";
	$time = $row['worktime'];
	$shour = (int)($time / 3600);
	$smin = ($time % 3600) / 60;
	if($smin == 0)
	{
		$smin = "00";
	}
	$ehour = (int)(($time + 33300) / 3600);
	$emin = (($time + 33300) % 3600) / 60;
	if($emin == 0)
	{
		$emin = "00";
	}
	echo "<td>".$shour.":".$smin."~".$ehour.":".$emin."</td><td class=\"cartype\">".$row['cartype']."<input type=\"hidden\" value=\"".$row['cartype']."\" name=\"".$row['cartype']."\" /></td>";
	$startinterval = (int)($time / ($interval * 3600));
	$endinterval = (($time + 33300) % $intervalsec)  > 0.0 ? ((int)($time + 33300) / $intervalsec) : ((int)(($time + 33300)/ $intervalsec) - 1);
	//陣列初始化
	for($i = 0; $i < $intervalcount; $i++)
	{
		if($row['resttime'] == 4)
		{
			if($i <= $startinterval || $i > $endinterval)
			{
				$carinterval[$i] = "<td>X</td>";
			}
			else if($i > ($startinterval + ($row['resttime'] / $interval)) && $i <= ($startinterval + ($row['resttime'] / $interval) + $temp))
			{
				$carinterval[$i] = "<td>X</td>";
			}
			else
			{
				$carinterval[$i] = "<td class=\"empty\"></td>";
			}

		}
		else
		{
			if($i <= $startinterval || $i > $endinterval)
			{
				$carinterval[$i] = "<td>X</td>";
			}
			else if($i >= ($startinterval + ($row['resttime'] / $interval)) && $i < ($startinterval + ($row['resttime'] / $interval) + $temp))
			{
				$carinterval[$i] = "<td>X</td>";
			}
			else
			{
				$carinterval[$i] = "<td class=\"empty\"></td>";
			}
		}
	}
	for($i = 1; $i < 13; $i++)
	{
		$fillinterval = $row["run".$i];
		if($fillinterval != -1)
		{
			$information = $row["user".$i];
			$temp2 = explode('_', $information);
			if(count($temp2) > 1)
			{
				$hiddenmessage = $i."_1_".$information;
				$rs2 = mysql_query("SELECT 抵達時間, 姓名, 帳號, 時段, 上車區域, 上車地址, 下車區域, 下車地址 FROM tempuserrequests WHERE 識別碼 = '".$temp2[0]."' AND arrangedate = '".$arrangedate."' AND arrangetime = '".$arrangetime."'");
				$rs3 = mysql_query("SELECT 抵達時間, 姓名, 帳號, 時段, 上車區域, 上車地址, 下車區域, 下車地址 FROM tempuserrequests WHERE 識別碼 = '".$temp2[1]."' AND arrangedate = '".$arrangedate."' AND arrangetime = '".$arrangetime."'");
				$rs4 = mysql_query("SELECT *FROM travelinformationofcarsharing WHERE 預約表格欄位 = '".$information."' AND 日期 = '".$arrangedate."' AND arrangetime = '".$arrangetime."'");
				if($row4 = mysql_fetch_array($rs4))
				{
					$sharing = $row4['起點'];
					$splitsharing = explode('_', $sharing);
					if(strcmp($splitsharing[0], '0') == 0)
					{
						//echo $information."<br />";
						//echo $temp2[0]."<br />";
						//echo $splitsharing[0]."<br />";
						if($row2 = mysql_fetch_array($rs2))
						{	
							
							$carinterval[$fillinterval] = "<td class=\"nonempty\"><input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" /><font size=2>共乘: ".$row4['車上乘員']."<br /><br /><font color=\"#FF0000\">時間: ".$row2['時段']."</font><br /><font color=\"#0000FF\">上車</font>: ".$temp2[0]."<br />".$row2['上車區域'].$row2['上車地址'];
							if($row3 = mysql_fetch_array($rs3))
							{
								$carinterval[$fillinterval].= "<br /><br /><font color=\"#FF0000\">時間: ".$row3['時段']."</font><br /><font color=\"#0000FF\">上車</font>: ".$temp2[1]."<br />".$row3['上車區域'].$row3['上車地址'];
								$sharing2 = $row4['終點'];
								$splitsharing2 = explode('_', $sharing2);
								if(strcmp($splitsharing2[0], '0') == 0)
								{
									$adapttime1 = $row2['抵達時間'] + $tolertime;
									$tempInterval = (int)($adapttime1 / $intervalsec);
									$arrivehour1 =  (int)($adapttime1 / 3600);
									$arrivemin1 = (int)(($adapttime1 % 3600) / 60);
									if($arrivehour1 < 10)
									{
										$arrivehour1 = "0".$arrivehour1;
									}
									if($arrivemin1 < 10)
									{
										$arrivemin1 = "0".$arrivemin1;
									}
									$adapttime2 = $row3['抵達時間'] + $tolertime;
//									$tempInterval2 = (int)($adapttime / $intervalsec) - $offset;
									$arrivehour2 =  (int)($adapttime2 / 3600);
									$arrivemin2 = (int)(($adapttime2 % 3600) / 60);
									if($arrivehour2 < 10)
									{
										$arrivehour2 = "0".$arrivehour2;
									}
									if($arrivemin2 < 10)
									{
										$arrivemin2 = "0".$arrivemin2;
									}
									if($tempInterval == $fillinterval)
									{
										$carinterval[$fillinterval].= "<br /><br /><font color=\"#339900\">下車</font>: ".$temp2[1]."br /".$row3['下車區域'].$row3['下車地址']."<br /><br /><font color=\"#339900\">下車</font>: ".$temp2[0]."<br />".$row2['下車區域'].$row2['下車地址']."</font></td>";
									}
									else
									{
										$carinterval[$fillinterval].= "</font></td>";
										$j = -1;
										for($j = $fillinterval + 1; $j < $tempInterval; $j++)
										{
											$carinterval[$j] = "<td class=\"nonempty\"><input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" />-></td>";
										}
										$carinterval[$j] = "<td class=\"nonempty\"><input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" /><font size=2>共乘: ".$row4['車上乘員']."<br /><br /><font color=\"#339900\">下車</font>: ".$temp2[1]."<br />".$row3['下車區域'].$row3['下車地址']."<br /><br /><font color=\"#339900\">下車</font>:  ".$temp2[0]."<br />".$row2['下車區域'].$row2['下車地址']."</font></td>";
										//$i++;
									}
								}
								else
								{
									$adapttime1 = $row3['抵達時間'] + $tolertime;
									$tempInterval = (int)($adapttime1 / $intervalsec) - $offset;
									$arrivehour1 =  (int)($adapttime1 / 3600);
									$arrivemin1 = (int)(($adapttime1 % 3600) / 60);
									if($arrivehour1 < 10)
									{
										$arrivehour1 = "0".$arrivehour1;
									}
									if($arrivemin1 < 10)
									{
										$arrivemin1 = "0".$arrivemin1;
									}
									$adapttime2 = $row2['抵達時間'] + $tolertime;
//									$tempInterval2 = (int)($adapttime / $intervalsec) - $offset;
									$arrivehour2 =  (int)($adapttime2 / 3600);
									$arrivemin2 = (int)(($adapttime2 % 3600) / 60);
									if($arrivehour2 < 10)
									{
										$arrivehour2 = "0".$arrivehour2;
									}
									if($arrivemin2 < 10)
									{
										$arrivemin2 = "0".$arrivemin2;
									}
									if($tempInterval == $fillinterval)
									{
										$carinterval[$fillinterval].= "<br /><br /><font color=\"#339900\">下車</font>: ".$temp2[0]."<br />".$row2['下車區域'].$row2['下車地址']."<br /><br /><font color=\"#339900\">下車</font>: ".$temp2[1]."<br />".$row3['下車區域'].$row3['下車地址']."</font></td>";
									}
									else
									{
										$carinterval[$fillinterval].= "</font></td>";
										$j = -1;
										for($j = $fillinterval + 1; $j < $tempInterval; $j++)
										{
											$carinterval[$j] = "<td class=\"nonempty\"><input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" />-></td>";
										}
										$carinterval[$j] = "<td class=\"nonempty\"><input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" /><font size=2>共乘: ".$row4['車上乘員']."<br /><br /><font color=\"#339900\">下車</font>: ".$temp2[0]."<br />".$row2['下車區域'].$row2['下車地址']."<br /><br /><font color=\"#339900\">下車</font>: ".$temp2[1]."<br />".$row3['下車區域'].$row3['下車地址']."</font></td>";
										//$i++;
									}
								}
							}
							else
							{
								echo $information."error!!!!<br />";
							}
						}
					}
					else
					{
						if($row3 = mysql_fetch_array($rs3))
						{	
							$carinterval[$fillinterval] = "<td class=\"nonempty\"><input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" /><font size=2>共乘: ".$row4['車上乘員']."<br /><br /><font color=\"#FF0000\">時間: ".$row3['時段']."</font><br /><font color=\"#0000FF\">上車</font>: ".$temp2[1]."<br />".$row3['上車區域'].$row3['上車地址'];
							if($row2 = mysql_fetch_array($rs2))
							{
								$carinterval[$fillinterval].= "<br /><br /><font color=\"#FF0000\">時間: ".$row2['時段']."</font><br /><font color=\"#0000FF\">上車</font>: ".$temp2[0]."<br />".$row2['上車區域'].$row2['上車地址'];
								$sharing2 = $row4['終點'];
								$splitsharing2 = explode('_', $sharing2);
								if(strcmp($splitsharing2[0], '0') == 0)
								{
									$adapttime1 = $row2['抵達時間'] + $tolertime;
									$tempInterval = (int)($adapttime1 / $intervalsec) - $offset;
									$arrivehour1 =  (int)($adapttime1 / 3600);
									$arrivemin1 = (int)(($adapttime1 % 3600) / 60);
									if($arrivehour1 < 10)
									{
										$arrivehour1 = "0".$arrivehour1;
									}
									if($arrivemin1 < 10)
									{
										$arrivemin1 = "0".$arrivemin1;
									}
									$adapttime2 = $row3['抵達時間'] + $tolertime;
//									$tempInterval2 = (int)($adapttime / $intervalsec) - $offset;
									$arrivehour2 =  (int)($adapttime2 / 3600);
									$arrivemin2 = (int)(($adapttime2 % 3600) / 60);
									if($arrivehour2 < 10)
									{
										$arrivehour2 = "0".$arrivehour2;
									}
									if($arrivemin2 < 10)
									{
										$arrivemin2 = "0".$arrivemin2;
									}
									if($tempInterval == $fillinterval)
									{
										$carinterval[$fillinterval].= "<br /><br /><font color=\"#339900\">下車</font>: ".$temp2[1]."br /".$row3['下車區域'].$row3['下車地址']."<br /><br /><font color=\"#339900\">下車</font>: ".$temp2[0]."<br />".$row2['下車區域'].$row2['下車地址']."</font></td>";
									}
									else
									{
										$carinterval[$fillinterval].= "</font></td>";
										$j = -1;
										for($j = $fillinterval + 1; $j < $tempInterval; $j++)
										{
											$carinterval[$j] = "<td class=\"nonempty\"><input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" />-></td>";
										}
										$carinterval[$j] = "<td class=\"nonempty\"><input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" /><font size=2>共乘: ".$row4['車上乘員']."<br /><br /><font color=\"#339900\">下車</font>: ".$temp2[1]."<br />".$row3['下車區域'].$row3['下車地址']."<br /><br /><font color=\"#339900\">下車</font>: ".$temp2[0]."<br />".$row2['下車區域'].$row2['下車地址']."</font></td>";
										//$i++;
									}
								}
								else
								{
									$adapttime1 = $row3['抵達時間'] + $tolertime;
									$tempInterval = (int)($adapttime1 / $intervalsec) - $offset;
									$arrivehour1 =  (int)($adapttime1 / 3600);
									$arrivemin1 = (int)(($adapttime1 % 3600) / 60);
									if($arrivehour1 < 10)
									{
										$arrivehour1 = "0".$arrivehour1;
									}
									if($arrivemin1 < 10)
									{
										$arrivemin1 = "0".$arrivemin1;
									}
									$adapttime2 = $row2['抵達時間'] + $tolertime;
		//							$tempInterval2 = (int)($adapttime / $intervalsec) - $offset;
									$arrivehour2 =  (int)($adapttime2 / 3600);
									$arrivemin2 = (int)(($adapttime2 % 3600) / 60);
									if($arrivehour2 < 10)
									{
										$arrivehour2 = "0".$arrivehour2;
									}
									if($arrivemin2 < 10)
									{
										$arrivemin2 = "0".$arrivemin2;
									}
									if($tempInterval == $fillinterval)
									{
										$carinterval[$fillinterval].= "<br /><br /><font color=\"#339900\">下車</font>: ".$temp2[0]."<br />".$row2['下車區域'].$row2['下車地址']."<br /><br /><font color=\"#339900\">下車</font>: ".$temp2[1]."<br />".$row3['下車區域'].$row3['下車地址']."</font></td>";
									}
									else
									{
										$carinterval[$fillinterval].= "</font></td>";
										$j = -1;
										for($j = $fillinterval + 1; $j < $tempInterval; $j++)
										{
											$carinterval[$j] = "<td class=\"nonempty\"><input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" />-></td>";
										}
										$carinterval[$j] = "<td class=\"nonempty\"><input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" /><font size=2>共乘: ".$row4['車上乘員']."<br /><br /><font color=\"#339900\">下車</font>: ".$temp2[0]."<br />".$row2['下車區域'].$row2['下車地址']."<br /><br /><font color=\"#339900\">下車</font>: ".$temp2[1]."<br />".$row3['下車區域'].$row3['下車地址']."</font></td>";
										//$i++;
									}
								}
							}
							else
							{
								echo $information."error!!!!<br />";
							}
						}
					}
				}
				else
				{
					echo "<td>".$information."</td>";
				}
			}
			else
			{
				$hiddenmessage = $i."_0_".$information;
				$rs2 = mysql_query("SELECT 抵達時間, 姓名, 帳號, 時段, 上車區域, 上車地址, 下車區域, 下車地址 FROM tempuserrequests WHERE 識別碼 = '".$information."' AND arrangedate = '".$arrangedate."' AND arrangetime = '".$arrangetime."'");
				if($row2 = mysql_fetch_array($rs2))
				{
					$add = $row2['上車地址'];
					$block = $row2['上車區域'];
					$adapttime = $row2['抵達時間'] + $tolertime;
					$tempInterval = (int)($adapttime / $intervalsec);
					//echo $tempInterval."<br />";
					//echo $recentinterval."<br />";
					//echo $offset;
					//die;
					
					$arrivehour =  (int)($adapttime / 3600);
					$arrivemin = (int)(($adapttime % 3600) / 60);
					if($arrivehour < 10)
					{
						$arrivehour = "0".$arrivehour;
					}
					if($arrivemin < 10)
					{
						$arrivemin = "0".$arrivemin;
					}
					if($tempInterval == $fillinterval)
					{
						$tempadd = $row2['下車地址'];
						$tempblock = $row2['下車區域'];
						$carinterval[$fillinterval] = "<td class=\"nonempty\"><input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" /><font size=2>".$information."<br /><font color=\"#FF0000\">時間: ".$row2['時段']."</font><br /><font color=\"#0000FF\">上車</font>: ".$block.$add."<br /><br /><font color=\"#FF0000\">時間: ".$arrivehour.$arrivemin."</font><br /><font color=\"#339900\">下車</font>: ".$tempblock.$tempadd."</font></td>";
						//echo "<td><font size=2>".$information."<br /><font color=\"#FF0000\">時間: ".$row2['時段']."</font><br /><font color=\"#0000FF\">上車</font>: ".$block.$add."<br /><br /><font color=\"#FF0000\">時間: ".$arrivehour.$arrivemin."</font><br /><font color=\"#339900\">下車</font>: ".$tempblock.$tempadd."</font></td>";
					}
					else
					{
						$carinterval[$fillinterval] = "<td class=\"nonempty\"><input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" /><font size=2>".$information."<br /><font color=\"#FF0000\">時間: ".$row2['時段']."</font><br /><font color=\"#0000FF\">上車</font>: ".$block.$add."</font></td>";
						$j = -1;
						for($j = $fillinterval + 1; $j < $tempInterval; $j++)
						{
							$carinterval[$j] = "<td class=\"nonempty\"><input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" />-></td>";
						}
						$tempadd = $row2['下車地址'];
						$tempblock = $row2['下車區域'];
						$carinterval[$j] = "<td class=\"nonempty\"><input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" /><font size=2>".$information."<br /><font color=\"#FF0000\">時間: ".$arrivehour.$arrivemin."</font><br /><font color=\"#339900\">下車</font>: ".$tempblock.$tempadd."</font></td>";
						//$i++;
					}
				}
			}
		}
		//else
		//{
	//		break;
	//	}
	}
	for($i = $offset; $i < $stopinterval; $i++)
	{
		echo $carinterval[$i];
	}
	echo "</tr>";
	
}while($row = mysql_fetch_array($rs1));
/*while($row = mysql_fetch_array($rs1))
{
	echo $row['carid']."<br />";
	//echo $row['上車地址']."<br />";
}*/
//mysql_close($con);
echo "</table>";
echo "</div>";
?>
</div>
</div>
    <center>
	<img src="images/logo1.png"  />		
	</center>
	<p align=center><font color=#FFC125>建議解析度 1024x768 以上觀看</font></p>
</body>

</html>
