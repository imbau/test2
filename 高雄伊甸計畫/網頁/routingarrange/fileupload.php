<?php
			include("connectInfo.php");
			include("Mydbconnect.php");
			echo $_POST['hiddenreorderdate']." ".$_POST['hiddenarrangetime'];
			$rs1 = mysql_query("SELECT no FROM userrequests WHERE arrangedate = '".$_POST['hiddenreorderdate']."' AND arrangetime = '".$_POST['hiddenarrangetime']."'");
			$num_rows = mysql_num_rows($rs1);
			date_default_timezone_set("Asia/Taipei");
			$ArrangerTime = date("H:i:s");
			$ArrangerDate =date("Y-m-d");	
			for($smtProgress = 1; $smtProgress <= 8; $smtProgress++)
				if($smtProgress!=3)
					mysql_query("INSERT INTO progress (`percent`,`date`,`time`,`index`)values(0,'".$ArrangerDate ."','".$ArrangerTime ."',".$smtProgress.")");	
				else
					mysql_query("INSERT INTO progress (`percent`,`date`,`time`,`index`)values(0,'".$ArrangerDate ."','".$ArrangerTime ."',".$smtProgress.")");			
        ?>
		
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta name="generator" content="HTML Tidy for HTML5 (experimental) for Windows https://github.com/w3c/tidy-html5/tree/c63cc39" />
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <meta name="viewport" content="width=device-width" />
  <link href="jquery-ui-1.9.0.custom/css/ui-lightness/jquery-ui-1.9.0.custom.css" rel="stylesheet" />
  <link href="bootstrap.min.css" rel="stylesheet" />
  <link rel="stylesheet" href="http://blueimp.github.com/cdn/css/bootstrap-responsive.min.css" />
  <link rel="stylesheet" href="http://blueimp.github.com/Bootstrap-Image-Gallery/css/bootstrap-image-gallery.min.css" />
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
                 .ui-progressbar .ui-progressbar-value { background-image: url(images/pbar-ani.gif); }
  </style>
  <script src="jquery-1.8.1.min.js"></script>
  <script type="text/javascript" src="jquery.lettering.js"></script>
  <script type="text/javascript" src="menu.js"></script>
  <script type="text/javascript" src="script.js"></script>
  <script src="jquery-ui-1.9.0.custom.js"></script>
  <script  src="tools.js">  </script>  
  <script type="text/javascript">
  var begin;
  var start = true,start1= true;
  var arrangedate = '<?php echo $ArrangerDate; ?>';
  var arrangetime = '<?php echo $ArrangerTime; ?>';
  var count=0;
  $(document).ready
  (
        function()
        {			
				/*****載入頁面div**********/	
				loadmunuphp();
				/********畫按鈕*************/		
				drawbutton("修改地址",1);	
        }
  );     
  /**********************************************************************/
  /**************************新增button**************************************/
   function drawbutton(buttonname,mode)
   { 
   	 buttonarea=document.getElementById('buttonarea');
   	 var cell=document.createElement('input');
   	 cell.type = 'button';
	 cell.setAttribute('id','Importing');	//給它id
	 cell.value=buttonname;
	 cell.className="btn btn-primary start";
	 cell.onclick=function(){Importing(mode);};	
	 buttonarea.appendChild(cell);	
   
   }
   /**************************移除button**************************************/
   function removebutton(name) 
   {
     var d=document.getElementById('buttonarea');
 	 var olddiv = document.getElementById(name);
 	   d.removeChild(olddiv);
   }
  /************************預約表與車輛表匯入資料庫或尋找旅行時間*************************/
   function Importing (mode)
   {
		if(start)
		{
			start = false; 		
            $.post("<?php  echo $linkurlport; ?>/WebRoutingArranger/ReadExcel.view", {arrangetime: arrangetime, arrangedate: arrangedate,requestfilename: $("input#requestfilename").val(), carfilename: $("input#carfilename").val(), mode: mode},function(response)
            {
                 var temp = response.split(",");        
				start=true;	 
                 if(temp[0] == "success")
                 {
					if(mode==1)
						{
							window.open ("traveltime1.php?date="+temp[1]+"&time="+temp[2],"Editar notícia", "location=1, status=1, scrollbars=1, width=1200, height=750");	
							removebutton('Importing');			
							drawbutton("查詢旅行時間",2);				
						}
					else			
						 window.location="traveltime.php?date="+temp[1]+"&time="+temp[2];
                 }                                     
				else if(temp[0] == "-2")
                {
					alert("已超過今日配額");
                     window.location="arranger.php";
                 }
				else  if(temp[0] == "-3")
                 {
					 alert("google api 要求已遭拒絕");
                     window.location="arranger.php";
                  }
				 else  if(temp[0] == "-4")
                 {
					alert("不存在的addres");
                    window.location="arranger.php";
                  }
				  else   if(temp[0] == "-5")
                  {
					  alert("查詢(address或latlng)遺失了");
                       window.location="arranger.php";
                   }
					else   if(temp[0] == "-6")
                   {
						 alert("預約表 第"+temp[3]+"行時間格式有誤");
                         window.location="arranger.php";
                    }	
					else   if(temp[0] == "-7")
                    {
						 alert("預約表  第"+temp[3]+"行政區域格式有誤");
                         window.location="arranger.php";
                     }
					else   if(temp[0] == "-8")
                    {
						 alert("預約表  第"+temp[3]+"欄位格式有誤");
                         window.location="arranger.php";
                     }
					else   if(temp[0] == "-9")
                   {
						alert("車輛表 第"+temp[3]+"缺少呼號!!");
                        window.location="arranger.php";
                    }
					else   if(temp[0] == "-10")
                    {
						 alert("車輛表 第"+temp[3]+"缺少工作時段!!");
                         window.location="arranger.php";
                     }
					 else   if(temp[0] == "-11")
                     {
						   alert("車輛表 第"+temp[3]+"缺少車廠地址!!");
                           window.location="arranger.php";
                      }
					else   if(temp[0] == "-12")
                      {
						   alert("車輛表 第"+temp[3]+"缺少出班日期!!");
						window.location="arranger.php";
                     }
				  else   if(temp[0] == "-13")
                    {
						alert("車輛表 第"+temp[3]+"缺少平日或假日的標記!!");
                       window.location="arranger.php";
                   }
                 });
                 if(mode==2)
                	setTimeout('window.setInterval(showPercentage, 500)',500); 
			}  
	}
/*******************showPercentage*********************/
  function showPercentage()
  {    	  
  	
  	$.get("progress.php?", {"option": 3,"date":arrangedate,"time":arrangetime},
	function(response)
    {       
        progres=response;
		if(response==0)
		{
			$("span#databaseprogress").text("資料庫準備中，請稍後。");
			test(parseInt(0));   
	    }
	    else  if(response>0&&response<100)
        {
			$("span#databaseprogress").text(progres + "%");
            test(parseInt(response));   
	    } 
     });
  }
 /*******************showPercentage*********************/  
 
  function open_win_detail(filename) {	                   
                     window.open ("modifyexcel.php?filename="+filename);		        
                }	
	/*******************載入頁面div*********************/
function loadmunuphp()
			{		
			 $("#preview").load("menu.php");
			}				



  </script>
</head>

<body>
  <div class="container">
<div id="preview" style="width: 1150px;  height:200px;"></div>
  <div class="well1">
      <?php     
      $allowedExts = array("xls");
	  $tag_arr =explode(".", $_FILES["requestfile"]["name"]);
      $extention = end($tag_arr);
      $reqfile = -1;
      $avaicarfile = -1;

      if($_FILES["requestfile"]["error"] > 0)
      {
	   echo "上傳檔案錯誤或尚未上傳";
          
      }
      else
      {
              echo "預約表檔名: " . $_FILES["requestfile"]["name"] . "<br />";			
              echo "檔案大小: " . ($_FILES["requestfile"]["size"] / 1024) . " Kb<br />";
              
              $reqfile = 1;
              
              if (file_exists("upload/" . $_FILES["requestfile"]["name"]))
              {
                      
                      unlink("upload/".$_FILES["requestfile"]["name"]);
                      move_uploaded_file($_FILES["requestfile"]["tmp_name"], "upload/" . $_FILES["requestfile"]["name"]);
              }       
              else
              {
                      move_uploaded_file($_FILES["requestfile"]["tmp_name"], "upload/" . $_FILES["requestfile"]["name"]);
                      echo "Stored in: " . "upload/" . $_FILES["myfile"]["name"];
              }       
      }

      if($_FILES["carfile"]["error"] > 0)
      {
              
      }
      else
      {
              $avaicarfile = 1;       
              echo "車輛表檔名: " . $_FILES["carfile"]["name"] . "<br />";
              echo "檔案大小: " . ($_FILES["carfile"]["size"] / 1024) . " Kb<br />";
              
              if(file_exists("upload/".$_FILES["carfile"]["name"]))
              {       
                      unlink("upload/".$_FILES["carfile"]["name"]);
                      move_uploaded_file($_FILES["carfile"]["tmp_name"], "upload/".$_FILES["carfile"]["name"]);
              }
              else
              {
                      move_uploaded_file($_FILES["carfile"]["tmp_name"], "upload/".$_FILES["carfile"]["name"]);
              }
      }
      ?>
	  <?php  
                      if($reqfile != -1)
                      {
                      echo "<p>預約表檔名: <input type=\"text\" readonly=\"readonly\" id=\"requestfilename\" value=\"".$_FILES["requestfile"]["name"]."\" /></p>";
                              if($avaicarfile != -1)
                              {
                              echo "<p>車輛表檔名: <input type=\"text\" readonly=\"readonly\" id=\"carfilename\" value=\"".$_FILES["carfile"]["name"]."\" /></p>";   
                              }
                              else
                              {
                              echo "<p>車輛表檔名: <input type=\"text\" readonly=\"readonly\" id=\"carfilename\" value=\"no\" /></p>";
                              }
                      }
					  
         ?>
  
	<div  id="buttonarea">	
	</div> 
	  <p>查詢進度: <span id="databaseprogress"></span></p>

      <div id="progressbar"></div>
    </div>
  </div>

  <center>
    <img src="images/logo1.png" />
  </center>

  <p align="center"><font color="#FFC125">建議解析度 1024x768 以上觀看</font></p>
</body>
</html>
