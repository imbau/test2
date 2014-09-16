<?php
        include("Mydbconnect.php");
        include("connectInfo.php");
        $count = 0;
        $date = $_GET['date'];
        $time = $_GET['time'];
        //$count = 0;
        $rs1 = mysql_query("SELECT * FROM userrequests WHERE 抵達時間 != -1 AND arrangedate = '".$date."' AND arrangetime = '".$time."'");
        $num_rows = mysql_num_rows($rs1);
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta name="generator" content="HTML Tidy for HTML5 (experimental) for Windows https://github.com/w3c/tidy-html5/tree/c63cc39" />
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <meta name="viewport" content="width=device-width" />
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
  </style>
  <script src="jquery-1.8.1.min.js"></script>
  <script type="text/javascript" src="jquery.lettering.js"></script>
  <script type="text/javascript" src="menu.js"></script>
  <script type="text/javascript" src="script.js"></script>
  
  <script type="text/javascript">
  $(document).ready
  (
        function()
        {
                $("input#update").click
                (
                        function()
                        {
                                
                                var count = <?php echo $num_rows; ?>;
                                var data = {};
								var arrangetime1,arrangetime1 ;
                                data['count'] = count;
								var addressdata ={};
							
                                for(var i = 1; i <= count; i++)
                                {
									
                                        data['traveltime'+i] = $("input#traveltime"+i).val();
                                        data['request'+i] = $("input#request"+i).val();	
                                }								
								for(var i = 1; i <= count; i++)
                                {		
								        var smallcar=document.getElementById('smallcar'+i); 
                                        addressdata['request'+i] = $("input#request"+i).val();
										addressdata['start'+i] = $("input#start"+i).val();
										addressdata['end'+i] = $("input#end"+i).val();
										addressdata['startarea'+i] = $("input#startarea"+i).val();
										addressdata['endarea'+i] = $("input#endarea"+i).val();
									    if(smallcar.checked) 
										 { 
											addressdata['smallcar'+i] ="小車";													
										 }
										 else
										 {
											addressdata['smallcar'+i] ="";	
										 }
                                }								
                                data['arrangedate'] = '<?php echo $date; ?>';
                                data['arrangetime'] = '<?php echo $time; ?>';							
								arrangetime1=data['arrangetime'];
								arrangedate1=data['arrangedate'];
								addressdata['arrangedate'] = '<?php echo $date; ?>';
                                addressdata['arrangetime'] = '<?php echo $time; ?>';
							    addressdata['count'] = count;			
								addressdata['mode'] = 1;										
								 $.post("updaterequestadd.php",addressdata,
									function(response) 
								   {    
								   });	
                                $.post("updatedatabase.php", data, function(data)
                                { 
                                if(data.trim() == "remain")
                                {
                                        var answer = confirm("仍有未輸入的交通時間，是否仍要排班?");
                                                if (answer)
                                                {
                                                 window.location ="finalarrangedtable.php?option=0"+"&date="+arrangedate1+"&time="+arrangetime1;
                                                }
                                                else
                                                {
                                                        location.reload();
                                                }
                                        }
                                        else if(data.trim() == "clear")
                                        {
										   window.location ="finalarrangedtable.php?option=0"+"&date="+arrangedate1+"&time="+arrangetime1;
                                        }
                        });
                        }
                )
        }
  );  
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
      <center>
        <h1>預約者起終點間交通時間列表:</h1>
      </center><br />

      <center>
        <h2>檢查完旅行時間請按排班</h2>
      </center>
      <center>
        <input type="button" id="back" value="回上一頁" onclick="history.back(-1)" class="btn btn-inverse" />&nbsp;&nbsp;
        <input type="button" id="update" value="排班" class="btn btn-primary start" /> 
		<input type="reset" name="reset" value=
          "清除旅行時間" class="btn btn-success" />
      </center><br />

      <form name="updateform" id="updateform">
        <center>
          <table border="1" cellspacing='0'>
            <tr>
			  <th>姓名</th>
              <th>上車區域</th>
              <th>上車地址</th>
              <th>下車區域</th>
              <th>下車地址</th>
              <th style='width:50px'>預估旅行時間(秒)</th>
              <th>取得旅行時間(秒)</th>
			  <th>小車</th> 
            </tr>
			<?php
            while($row = mysql_fetch_array($rs1))
            {         
                    $time = $row['時段'];
                    $hour = substr($time, 0, 2);
                    $min  = substr($time, 2, 2);
                    $timesec = $hour * 3600 + $min *60;
                    $traveltime = $row['抵達時間'] - $timesec;
                    $traveltimemin = (int)($traveltime / 60);
                    $traveltimesec = $traveltime % 60;
                    $count++;
                    echo "<tr  valign='middle' bgcolor='#fafafa' style='cursor:default' onMouseOver=\"this.style.backgroundColor='#DEF8FA'\" onMouseOut=\"this.style.backgroundColor='#FFFFFF'\">";
				       echo "<td style='width:50px'>".$row['姓名']."</td>";
					echo "<td>
									<input type=\"text\" id=\"startarea".$count."\" value=\"".$row['上車區域']."\"  style='width:90px'/>
							</td>";
                    echo "<td>
									<input type=\"text\" id=\"start".$count."\" value=\"".$row['上車地址']."\" style='width:250px'/>
							</td>";
					 echo "<td>
									<input type=\"text\" id=\"endarea".$count."\" value=\"".$row['下車區域']."\"style='width:90px' />
							</td>";
                    echo "<td>
									<input type=\"text\" id=\"end".$count."\" value=\"".$row['下車地址']."\" style='width:250px'/>
							</td>";
									
                    echo "<td><input type=\"hidden\" id=\"request".$count."\" value=\"".$row['識別碼']."\" />
									<input type=\"text\" id=\"traveltime".$count."\" style='width:50px'/>
							</td>
							<td  style='width:50px'>".$traveltimemin."分".$traveltimesec."秒</td>";
						
					if($row['車種']=="小車")		
						echo "	<td>	<input type=\"checkbox\" id=\"smallcar".$count."\"   value=1 checked=\"checked\"/></td>";
					else
						echo "	<td>	<input type=\"checkbox\" id=\"smallcar".$count."\"  value=0 /></td>";
					echo	 "</tr>";                    
            }
            ?>
          </table>
        </center><br />       
      </form>
    </div>

    <center>
      <img src="images/logo1.png" />
    </center>
  </div>

  <p align="center"><font color="#FFC125">建議解析度 1024x768 以上觀看</font></p>
</body>
</html>
