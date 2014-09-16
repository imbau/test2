<?php 
	include("connectInfo.php");
	$filename = $_GET["filename"];
	?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
        <meta HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=big5">
        <title>可調適性動態排班系統 </title> 		
        <meta name="viewport" content="width=device-width">
        <link href="bootstrap.min.css" rel="stylesheet" />
        <link rel="stylesheet" href="http://blueimp.github.com/cdn/css/bootstrap-responsive.min.css">
        <link rel="stylesheet" href="http://blueimp.github.com/Bootstrap-Image-Gallery/css/bootstrap-image-gallery.min.css">
        <link type="text/css" href="menu.css" rel="stylesheet" />
        <link rel="stylesheet" type="text/css" href="css/demo.css" />
        <link rel="stylesheet" type="text/css" href="css/style6.css" />	
   <link rel="stylesheet" media="screen" href="jquery.handsontable.css">			
         <style type="text/css">
                   body {
				  	color:#8B3626;
	                font-size:0.825em;
	                background: url(images/background.jpg) no-repeat center top #252525;
	                font-family:Arial, Helvetica, sans-serif;
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
		 <script src="demo/js/json2.min.js"></script> 
		 <script src="jquery.handsontable.js"></script>
		
		<script>
		$(function() {
					$("#letter-container h2 a").lettering();
				});
<!--

		$(document).ready(
		function(){
						var url;
						var filename = '<?php echo $filename; ?>';						
						url="phpexcel.php?filename="+filename;							
                         $.getJSON(url, function(data){
                                    loadExamples(data);
                             })
							 .error(function() { 
							        alert("error"); 
							});
                              }
				    );
					
		function loadExamples(data) {
		$("#車輛表").handsontable({
									rows: 20,
									cols: 7,
								   //rowHeaders: true,
									colHeaders: true,
									minSpareCols: 1, //always keep at least 1 spare col at the right
									minSpareRows: 1 //always keep at least 1 spare row at the bottom
									});
		$("#車輛表").handsontable("loadData", data); 
									}

//-->
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
 <body >     
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
					<li><a href="<?php  echo $linkurlport; ?>/routingarrange/fileupload1.php">首頁</a></li>					
					</ul></div >
				   </div >
	 <div class="well1">
		<div class="container" style="width:1000px; height:400px;">	 
		<!-----
		  <button type="button" name="save" class="btn btn-success">       
                    <span><h3><font face="標楷體">修改存檔</font></h3></span>
                </button> 	  ---->
		<center>
		<div id="車輛表" class="dataTable" style="width: 700px; height:600px; overflow: auto"></div>
		</center>
   

			  
		</div>
	   </div>
	     <center>
		<img src="images/logo1.png"  />		
		</center>
	   
	   </div>	 
</body>
</html>