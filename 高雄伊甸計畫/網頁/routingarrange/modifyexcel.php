<?php 
	include("connectInfo.php");
	$filename = $_GET["filename"];
	?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
        <meta HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=big5">
        <title>�i�վA�ʰʺA�ƯZ�t�� </title> 		
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
		$("#������").handsontable({
									rows: 20,
									cols: 7,
								   //rowHeaders: true,
									colHeaders: true,
									minSpareCols: 1, //always keep at least 1 spare col at the right
									minSpareRows: 1 //always keep at least 1 spare row at the bottom
									});
		$("#������").handsontable("loadData", data); 
									}

//-->
		$(function(){
		// �� #menu li �[�W hover �ƥ�
		$('#menu>li').hover(function(){
			// ����� li �����l���
			var _this = $(this),
				_subnav = _this.children('ul');
			
			// �ܧ�ثe���ﶵ���I���C��
			// �P����ܤl���(�p�G������)
			_this.css('backgroundColor', '#06c').siblings().css('backgroundColor', '');
			_subnav.css('display', 'block');
		} , function(){
			// �P�����äl���(�p�G������)
			// �]�i�H���y��W�����g�k
			$(this).children('ul').css('display', 'none');
		});
		
		// �����W�s������u��
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
					 <img src="images/logo.png" width="80px" /><h2><a>�i�վA�ʰʺA�ƯZ�t��</a></h2>
					 <ul id="menu">
					<li>
						<a href="#" onclick="window.open('map.html','newwindows','height=500, width=700');" >�s���ڭ�</a>							
					</li>
		             <li>
						<a href="#">����ڭ�</a>
						<ul>
							<li><a href="http://www.ycswf.org.tw/" TARGET="_blank" >�|�����|�֧Q����|</a></li>
							<li><a href="http://web.ncku.edu.tw/bin/home.php"  TARGET="_blank">���\�j��</a></li>
							<li><a href="http://www.eden.org.tw/"  TARGET="_blank">��l���|�֧Q����|</a></li>	
							<li><a href="http://www.iii.org.tw/"   TARGET="_blank">�굦�|</a></li>
						</ul>
					</li>					
					<li><a href="<?php  echo $linkurlport; ?>/routingarrange/fileupload1.php">����</a></li>					
					</ul></div >
				   </div >
	 <div class="well1">
		<div class="container" style="width:1000px; height:400px;">	 
		<!-----
		  <button type="button" name="save" class="btn btn-success">       
                    <span><h3><font face="�з���">�ק�s��</font></h3></span>
                </button> 	  ---->
		<center>
		<div id="������" class="dataTable" style="width: 700px; height:600px; overflow: auto"></div>
		</center>
   

			  
		</div>
	   </div>
	     <center>
		<img src="images/logo1.png"  />		
		</center>
	   
	   </div>	 
</body>
</html>