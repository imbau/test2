<?php
        include("Mydbconnect.php");
        include("connectInfo.php"); 
		$user=GetUserInfo();	
		$title = mysql_query("SELECT * FROM title WHERE 1");
		$titlerow=mysql_fetch_array($title);
		$menutile=$titlerow[1];
		if($user[0]==-1)		
		{
			echo "<meta http-equiv=REFRESH CONTENT=1;url=login.php>";	
			exit();		
		}
		
		?>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta name="generator" content="HTML Tidy for HTML5 (experimental) for Windows https://github.com/w3c/tidy-html5/tree/c63cc39" />
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <?php echo "<title>$menutile</title>"?>
  <link rel="stylesheet" href="css/jquery-ui.css" />
  <link href="bootstrap.min.css" rel="stylesheet" />
  <link rel="stylesheet" href="css/bootstrap-responsive.min.css" />
  <link rel="stylesheet" href="css/bootstrap-image-gallery.min.css" />
  <link type="text/css" href="menu.css" rel="stylesheet" />
  <link rel="stylesheet" type="text/css" href="css/demo.css" />
  <link rel="stylesheet" type="text/css" href="css/style6.css" />
  <link rel="stylesheet" href="css/darkwash.css" media="screen" />
  <script type="text/javascript" src="js/jquery-1.8.2.js"></script>
  <script type="text/javascript" src="js/jquery.bgiframe-2.1.2.js"></script>
  <script type="text/javascript" src="js/jquery-ui.js"></script>
  <script type="text/javascript" src="jquery.lettering.js"></script>
  <script type="text/javascript" src="tools.js"></script>     
   <style type="text/css">
                   body {
                        color:SaddleBrown;
                        font-size:0.825em;
                        background: url(images/background.jpg) no-repeat center top #252525;
                        font-family:Eras Light ITC, Helvetica, sans-serif;
                    }                                                                        
</style>
  <body> 
   <div id="letter-container" class="letter-container">
      <div class="page-header">
        <!--<img src="images/logo.png" width="80px" />--->
        <h2><a><?  echo $menutile;?></a></h2>
		<h1><a>
		<?  
		for($x=0;$x<50;$x++)
			echo "&nbsp;";
		echo $user[1];
		?></a></h1>
        <ul id="menu">
		  <li>
          <a href="<?php  echo $linkurlport; ?>/routingarrange/logout.php"  target="_top" >登出</a>		       	            		     
          </li>
          <li> <a href="#" onclick="window.open('map.html','newwindows','height=500, width=700');">連絡我們</a></li>
          <li>
            <a href="#">關於我們</a>
            <ul>
              <li> <a href="http://www.ycswf.org.tw/" target="_blank">育成社會福利基金會</a></li>
              <li><a href="http://web.ncku.edu.tw/bin/home.php" target="_blank">成功大學</a></li>
              <li><a href="http://www.eden.org.tw/" target="_blank">伊甸社會福利基金會</a></li>
              <li><a href="http://www.iii.org.tw/" target="_blank">資策會</a></li>
            </ul>
          </li>
          <li>
          <a href="<?php  echo $linkurlport."/routingarrange/arranger.php"?>"  target="_blank">首頁</a>		   
          </li>
        </ul>
      </div>
    </div>
	</body>
</html>