<?php
	include("Mydbconnect.php");
	$count = 0;
	$date = $_GET['date'];
	$time = $_GET['time'];
	//$count = 0;
	
	$rs1 = mysql_query("SELECT 識別碼, 上車區域, 上車地址, 下車區域, 下車地址 FROM userrequests WHERE 抵達時間 = -1 AND arrangedate = '".$date."' AND arrangetime = '".$time."'");
	$num_rows = mysql_num_rows($rs1);
	


?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
   <head>
        <meta HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
        <title>可調適性動態排班系統 </title> 		
        <meta name="viewport" content="width=device-width">
        <link href="bootstrap.min.css" rel="stylesheet" />
        <link rel="stylesheet" href="http://blueimp.github.com/cdn/css/bootstrap-responsive.min.css">
        <link rel="stylesheet" href="http://blueimp.github.com/Bootstrap-Image-Gallery/css/bootstrap-image-gallery.min.css">
        <link type="text/css" href="menu.css" rel="stylesheet" />
        <link rel="stylesheet" type="text/css" href="css/demo.css" />
        <link rel="stylesheet" type="text/css" href="css/style6.css" />		
		<link rel="stylesheet" href="css/darkwash.css" media="screen" />
         <style type="text/css">
                  body {
					color:SaddleBrown;	 				   
	                font-size:0.825em;
	                background: url(images/background.jpg) no-repeat center top #252525;
	                font-family:Eras Light ITC, Helvetica, sans-serif;
                    }					
					 #msg{
					 font-size:3em;
					 color:SaddleBrown;	                    				 
					 }					
				div#menu { margin:10px auto; }
					 					 </style>
		 <script src="jquery-1.8.1.min.js"></script>
		 <script type="text/javascript" src="jquery.lettering.js"></script>
		  <script type="text/javascript" src="menu.js"></script>
		  <script type="text/javascript" src="script.js"></script>

		
		<script>
		$(document).ready
		(
		function()
		{
		$("input#update").click
		(
			function()
			{
				//alert("tees");
				var count = <?php echo $num_rows; ?>;
//				alert("1234test"+count);
				var data = {};
				//var i = 1;
				//data['traveltime'+i] = $("input#traveltime"+i).val();
				//alert(data['traveltime1']+ " " + $("input#traveltime"+i).val());
				data['count'] = count;
				for(var i = 1; i <= count; i++)
				{
					data['traveltime'+i] = $("input#traveltime"+i).val();
					data['request'+i] = $("input#request"+i).val();
				}
				data['arrangedate'] = '<?php echo $date; ?>';
				data['arrangetime'] = '<?php echo $time; ?>';
				$.post("updatedatabase.php", data, function(data)
				{
     				//alert(data);
     				//var temp = data.split(" ");
     				if(data.trim() == "remain")
     				{
     					var answer = confirm("仍有未輸入的交通時間，是否仍要排班?");
						if (answer)
						{
							window.location = 'redirect.php';
							//alert("remaintest");
						}
						else
						{
							location.reload();
						}
					}
					else if(data.trim() == "clear")
					{
						window.location = 'redirect.php';
						//alert("cleartest");
					}
     			});
			}
		)
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
<div class="container">  	   
<form method="post" action="updatedatabase.php" name="updateform">
<center><table border="1" cellspacing='0'>
<tr><th>上車區域</th><th>上車地址</th><th>下車區域</th><th>下車地址</th><th>預估旅行時間(秒)</th></tr>
<?php

while($row = mysql_fetch_array($rs1))
{
//	echo "上車地址: ".$row['上車區域'].$row['上車地址'].", 下車地址: ".$row['下車區域'].$row['下車地址']."<input type=\"hidden\" name=\"".$count."\" value=\"".$row['識別碼']."\" /><input type=\"text\" name=\"traveltime".$count."\" /><br />";
	$time = $row['時段'];
	$hour = substr($time, 0, 2);
	$min  = substr($time, 2, 2);
	$timesec = $hour * 3600 + $min *60;
	$traveltime = $row['抵達時間'] - $timesec;
	$traveltimemin = (int)($traveltime / 60);
	$traveltimesec = $traveltime % 60;
	$count++;
	echo "<tr  valign='middle' bgcolor='#fafafa' style='cursor:default' onMouseOver=\"this.style.backgroundColor='#DEF8FA'\" onMouseOut=\"this.style.backgroundColor='#FFFFFF'\"><td>".$row['上車區域']."</td>";
	echo "<td>".$row['上車地址']."</td>";
	echo "<td>".$row['下車區域']."</td>";
	echo "<td>".$row['下車地址']."</td>";
	echo "<td><input type=\"hidden\" id=\"request".$count."\" value=\"".$row['識別碼']."\" /><input type=\"text\" id=\"traveltime".$count."\" /></td><td>".$traveltimemin."分".$traveltimesec."秒</td></tr>";
}

?>
</table></center>

<input type="hidden" value="<?php echo $count;?>" name="count" />
<center><input type="submit" name="update" value="Update" />
<input type="reset" name="reset" value="Reset" /></center>
</form>
		</div></div>
		</div>	  
 <center>
	<img src="images/logo1.png"  />		
</center>
<p align=center><font color=#FFC125>建議解析度 1024x768 以上觀看</font></p>
</body>
</html>