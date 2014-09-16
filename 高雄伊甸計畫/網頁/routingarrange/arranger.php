<?php
        include("Mydbconnect.php");
        include("connectInfo.php");
		$user=GetUserInfo();
		if($user[0]=="root1")
		{
		  echo "<meta http-equiv=REFRESH CONTENT=1;url=/newroutingarrange/arranger.php>";	
		}else
		{
			date_default_timezone_set("Asia/Taipei");
			$rs1 = mysql_query("SELECT * FROM arrange_log WHERE date = '".date("Y-m-d")."' ORDER BY no DESC");
			$rs2 = mysql_query("SELECT * FROM arrange_log ORDER BY no DESC ");
		}
		$title = mysql_query("SELECT * FROM title WHERE 1");
		$titlerow=mysql_fetch_array($title);
?>
<!DOCTYPE HTML>
<html>
  <head>
    <meta name="generator"
    content="HTML Tidy for HTML5 (experimental) for Windows https://github.com/w3c/tidy-html5/tree/c63cc39" />
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta name="viewport" content="width=device-width" />
    <link href="bootstrap.min.css" rel="stylesheet" />
    <link rel="stylesheet" href="http://blueimp.github.com/cdn/css/bootstrap-responsive.min.css" />
    <link rel="stylesheet" href="http://blueimp.github.com/Bootstrap-Image-Gallery/css/bootstrap-image-gallery.min.css" />
    <link type="text/css" href="menu.css" rel="stylesheet" />
    <link rel="stylesheet" type="text/css" href="css/demo.css" />
    <link rel="stylesheet" type="text/css" href="css/style6.css" />
    <link rel="stylesheet" href="css/darkwash.css" media="screen" />
    <link rel="stylesheet" href="http://code.jquery.com/ui/1.9.0/themes/base/jquery-ui.css" />
    <style type="text/css">
                   body {
                        color:#FFFFFF;
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
    <script type="text/javascript" src="http://code.jquery.com/jquery-1.8.2.js"></script>
    <script type="text/javascript" src="jquery.lettering.js"></script>
    <script type="text/javascript" src="http://code.jquery.com/ui/1.9.0/jquery-ui.js"></script>	
	 <script  src="tools.js">  </script>  
<script >  
if(window.name != "bencalie")
	{
		location.reload();
		window.name = "bencalie";
	}
	else
	{
		window.name = "";
	}
	 /****************上傳dialog************************/
$(document).ready
  (
 function()
        {            
        $("div#rearrange_dialog").dialog
                (
                        {
                    autoOpen: false,
                    height: 420,
                    width: 380,
                    modal: true,
                    close: function()
                {
                        allFields.val( "" ).removeClass( "ui-state-error" );
                }
                }
        );   
        $("input#partialarrange").click
        (
                function()
                {
                        
                        $("span#arrangetime").text($(this).parent().children("span.time").text());
                        $("input#hiddenarrangetime").val($(this).parent().children("span.time").text());
                        $("span#reorderdate").text($(this).parent().children("span.date").text());
                        $("input#hiddenreorderdate").val($(this).parent().children("span.date").text());
                        $("input#mode").val(1);
                        $("div#rearrange_dialog").dialog("open");
                }
        );        
        $("input#rearrange").click
        (
                function()
                {
                        $("span#arrangetime").text($(this).parent().children("span.time").text());
                        $("input#hiddenarrangetime").val($(this).parent().children("span.time").text());
                        $("span#reorderdate").text($(this).parent().children("span.date").text());
                        $("input#hiddenreorderdate").val($(this).parent().children("span.date").text());
                        $("input#mode").val(2);
                        $("div#rearrange_dialog").dialog("open");

                }
        ); 
        }
  );
 /****************上傳dialog************************/
  

/******************部分排班*************************/	
function scriptbutton1() {
   $("span#arrangetime").text($(this).parent().children("span.time").text());
        $("input#hiddenarrangetime").val($(this).parent().children("span.time").text());
        $("span#reorderdate").text($(this).parent().children("span.date").text());
        $("input#hiddenreorderdate").val($(this).parent().children("span.date").text());
        $("input#mode").val(1);
        $("div#rearrange_dialog").dialog("open");


}
/***************************************************/

/**********************重新排班*****************************/
  function scriptbutton2()
  {
        $("span#arrangetime").text($(this).parent().children("span.time").text());
        $("input#hiddenarrangetime").val($(this).parent().children("span.time").text());
        $("span#reorderdate").text($(this).parent().children("span.date").text());
        $("input#hiddenreorderdate").val($(this).parent().children("span.date").text());
        $("input#mode").val(2);
        $("div#rearrange_dialog").dialog("open");
  }
 /***************************************************/  

/*********************歷史資料查詢******************************/
 function CallFinalTable(no,arrangedate,arrangetime)
  {
	 	   window.open("finalarrangedtable.php?option=" + no+"&date="+arrangedate+"&time="+arrangetime);
  }
  /*********************候補未排入******************************/
 function CallFinalTable1(no,arrangedate,arrangetime)
  {
        window.open("Inquiryreq.php?option=" + no+"&date="+arrangedate+"&time="+arrangetime+"&mode=2","Candidatewindow", "location=1, status=1, scrollbars=1, width=1200, height=750");
  }
 
 
/***************************************************/
 /*********************指定查詢******************************/
  function LookUpDateTable()
  {
  
        var xmlhttp;     	
        if (window.XMLHttpRequest)
        {// code for IE7+, Firefox, Chrome, Opera, Safari
                xmlhttp=new XMLHttpRequest();
        }
        else
        {// code for IE6, IE5
                xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
        }
        xmlhttp.onreadystatechange=function()
        {
                if (xmlhttp.readyState==4 && xmlhttp.status==200)
        {
                        //alert(xmlhttp.responseText);
                        var i;
                        var table = document.getElementById("asigndate");
                        while(table.rows.length > 1)
                        {
                                table.deleteRow(table.rows.length - 1);
                        }
                        var temp = xmlhttp.responseText.split(" ");

                        for(i = 0; i < temp.length - 1; i++)
                        {
                                var temp2 = temp[i].split(",");
                                var tObj = table.tBodies[0];
                                var row = document.createElement("tr");
                                var cell = document.createElement("td");
							    
                                cell.innerHTML = "<center>" + (i+1) + "<\/center>";
                                row.appendChild(cell);
                                cell = document.createElement("td");
                                tempdate = temp2[1];
                                temptime = temp2[2];	
								var no = temp2[0] .trim();
								cell.innerHTML = "<center><span class=\"date\">"+  datetext.value + "<\/span>  <input type='button'  class='btn btn-success' value='查看' onclick=CallFinalTable('"+ no+"','"+tempdate+"','"+temptime+ "')><\/center>";                             
                                row.appendChild(cell);
                                tObj.appendChild(row);
                                
                        }
                }
        }
        xmlhttp.open("GET", "asigndate.php?date=" + datetext.value, true);
        xmlhttp.send();
  }
  
 /***************************************************/
/*******************載入頁面div*********************/
		function loadmunuphp()
			{		
			 $("#preview").load("menu.php?user=root");
			}		
/*******************載入頁面div*********************/	
$(function(){
	loadmunuphp();
});
</script>   
 </head>
  <body onbeforeunload="alert('Good Bye')">
    <div class="container">
	<!------<iframe id="preview-frame" src="menu.php" name="preview-frame" frameborder="0"  noresize="noresize" style="width: 1150px;  height:170px; position: relative; z-index: 500;">
	</iframe>---->
	<div id="preview" style="width: 1150px;  height:200px;"></div>
      <div class="well1">
        <div class="container" style="width:1000px; height:600px;">
          <!-- The file upload form used as target for the file upload widget -->
          <form id="fileupload" action="fileupload.php" method="post" enctype="multipart/form-data" style="width:1000px">
            <!-- The fileupload-buttonbar contains buttons to add/delete files and start/cancel the upload -->
            <div class="row">
              <div class="span11" style="width:1000px;">
			  
                <div id='msg' style="width:1000px">
                <img src="images/ch1.png" /> 
                <img src="images/ch2.png" hspace="160px" />
                <br />
                <!-- The fileinput-button span is used to style the file input field as button --> 
                <img src="images/UploadFoldericon.png" /> 
                <input type="file" name="requestfile" />&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; 
                <img src="images/Uploadicon.png" /> 
                <input type="file" name="carfile" />&#160;&#160;&#160;&#160; 
                <button type="submit" class="btn btn-primary start">
                  <h3>
                    <span>
                      <font face="標楷體">開始上傳</font>
                    </span>
                  </h3>
                </button> 
                <button type="reset" class="btn btn-warning cancel">
                  <h3>
                    <span>
                      <font face="標楷體">取消</font>
                    </span>
                  </h3>
                </button>
				</div>
				
                <div class="tab-container">
                  <div id="c1">
                  <a href="#c1" title="關於">
                    <h3>關於</h3>
                  </a> 
                  <!-- This is your actual tab and the content is below it !-->
                  <div class="tab-content">
                    <!-- tab-container > div > div in the CSS !-->
                    <h3>無障礙交通：復康巴士</h3>
                    <p>伊甸結合無障礙交通環境專家藍武王教授一同評估推展復康巴士計畫，引介華航捐助台北市政府三輛大型復康巴士，市政府同時成立小型復康巴士車隊，1989年起，伊甸接受政府委託代辦「復康巴士」營運業務，推出定點公車與小型呼叫服務，並協助公共運輸無障礙設施檢測，提供身心障礙者交通便利性。
					</p>
                  </div>
				  </div>
				  
                  <div id="c2">
                  <a href="#c2" title="指定日期">指定日期 
                  <input type="text" id="datetext" style="height: 10px; width: 100px;" /> 
                  <input type="button" class='btn btn-info' value="查詢" style="width: 60px" onclick="LookUpDateTable()" />
				  </a> 
                  <!-- This is your actual tab and the content is below it !-->
                  <div class="tab-content">
                    <!-- tab-container > div > div in the CSS !-->
                    <table width="100%" border="5" cellspacing='0' font="" size="+5" id="asigndate">
                      <tr>
                        <th width="5%">No.</th>
                        <th>Date</th>
                      </tr>
                    </table>
                  </div>
				  </div>   
				  <div id="c4">
                  <?php    
					echo "<a href='#c4' title='候補未排入區'>";
					echo "<h3>$titlerow[2]</h3></a> ";					
				 ?>    
                  <!-- This is your actual tab and the content is below it !-->
                  <div class="tab-content">
                    <!-- tab-container > div > div in the CSS !-->
                    <table width="100%" border="5" cellspacing='0' font="" size="+5">
                      <tr>
                        <th width="5%">No.</th>
                        <th width="10%">排班單位</th>
						<th width="30%">出車時間</th>
					<?php
                                   $i = 1;
                                   while($row2 = mysql_fetch_array($rs2))
                                     {
									     $TurnoutDaters = mysql_query("SELECT  `TurnoutDate` FROM `availablecars` WHERE  `date`='".$row2['date']."' and `time`='".$row2['time']."' ORDER BY no DESC LIMIT 0, 1");	
										  $TurnoutDatersrow = mysql_fetch_array($TurnoutDaters);
                                          echo "<tr align='center' valign='middle' >
                                            <td>".$i."</td>
											<td>
											<span class=\"date\">".$row2['company']."</span> 
											</td>
                                             <td>
                                             <span class=\"date\">".$TurnoutDatersrow['TurnoutDate']."</span> 
											<input type='button'  class='btn btn-success' value='查看' onclick=CallFinalTable1('".$row2['no']."','".$row2['date']."','".$row2['time']."') > 																						
                                              </td>
                                              </tr>";
                                                $i++;
                                          }
										 
                          ?>
                      </tr>
                    </table>
                  </div>
				  </div>
				   <div id="c3">
				   	<?php    
					echo "<a href='#c3' title='歷史訊息'>";
					echo "<h3>$titlerow[3]</h3></a> ";					
					?>      
                  <!-- This is your actual tab and the content is below it !-->
                  <div class="tab-content">
                    <!-- tab-container > div > div in the CSS !-->
                    <table width="100%" border="5" cellspacing='0' font="" size="+5">
                      <tr>
                        <th width="5%">NO.</th>
						<th width="10%">排班單位</th>
						<th width="30%">出班時間</th>
						<?php
                                                        $i = 1;
														if($user[0]=="root1")
														{											
															$rs2 = mysql_query("SELECT * FROM arrange_log WHERE 1 ORDER BY no DESC");		
														}	
														else	
														{									
															$rs2 = mysql_query("SELECT * FROM arrange_log WHERE `company`='".$user[1]."'ORDER BY no DESC LIMIT 0, 15");		
														}														
                                                        while($row2 = mysql_fetch_array($rs2))
                                                        {
															    $TurnoutDaters = mysql_query("SELECT  `TurnoutDate` FROM `availablecars` WHERE  `date`='".$row2['date']."' and `time`='".$row2['time']."' ORDER BY no DESC LIMIT 0, 1");	
																$TurnoutDatersrow = mysql_fetch_array($TurnoutDaters);
                                                                echo "<tr align='center' valign='middle' >
                                                                         <td>".$i."</td>
																		 <td>
																		<span class=\"date\">".$row2['company']."</span> 
																		</td>
                                                                         <td>
                                                                         <span class=\"date\">".$TurnoutDatersrow['TurnoutDate']."</span> 
                                                                         <input type='button'  class='btn btn-success' value='查看' onclick=CallFinalTable('".$row2['no']."','".$row2['date']."','".$row2['time']."') >   																																				 
                                                                         </td>
                                                                         </tr>";
                                                                         $i++;
                                                        }
                          ?>
                      </tr>
                    </table>
                  </div>
				  </div>
				    <div id="c5">
					<?php    
					echo "<a href='#c5' title='公佈欄'>";
					echo "<h3>$titlerow[4]</h3></a> ";					
					?>       
                  <!-- This is your actual tab and the content is below it !-->
                  <div class="tab-content">
                    <!-- tab-container > div > div in the CSS !-->
                    <table  id='table' width="100%" border="5" cellspacing='0' font="" size="+5">
                      <tr>
						<th width="10%">排班時間</th>
						<th width="30%">訊息</th>
						<?php
						   readmessage();
							function readmessage() 
							{ 
								               $message = mysql_query("SELECT * FROM `bulletin` WHERE 1");
											   while($messagerow = mysql_fetch_array($message))
                                                             {
                                                                echo "<tr align='center' valign='middle' >
																		 <td>
																		<span class=\"date\">".$messagerow['date']."  ".$messagerow['time']."</span> 
																		</td>
                                                                         <td>
                                                                         <span class=\"date\">".$messagerow['message']."</span>                                                                  
                                                                         </td>
                                                                         </tr>";
                                                                         $i++;
                                                              }
							} 
										
											   
                          ?>
                      </tr>
                    </table>
                  </div>
				  </div>
                </div>
              </div>
            </div>
          </form>
        </div>
      </div>
      <div id="rearrange_dialog" title="檔案上傳">
        <form name="rearrange_upload" enctype="multipart/form-data" method="post" action="additionfileupload.php">
        <table>
          <tr>
            <td>
            <span>排班時間:</span> 
            <span id="arrangetime"></span>
			</td>
          </tr>
          <tr>
            <td>
            <span>預約日期:</span> 
            <span id="reorderdate"></span>
			</td>
          </tr>
          <tr>
            <td>
              <label for="requestfile">預約表上傳</label>
              <input type="file" name="rerequestfile" size="20" maxlength="20" />
            </td>
          </tr>
          <tr>
            <td>
              <label for="carfile">車輛表上傳</label>
              <input type="file" name="recarfile" size="20" maxlength="20" />
            </td>
          </tr>
          <tr>
            <td>
              <font color="#FF0000">*不能使用中文檔名</font>
            </td>
          </tr>
        </table>
        <table>
          <tr>
            <td>
              <input type="submit" value="上傳" />
            </td>
            <td>
              <input type="reset" value="清除" />
            </td>
          </tr>
        </table>
        <input type="hidden" id="hiddenarrangetime" name="hiddenarrangetime" /> 
        <input type="hidden" id="hiddenreorderdate" name="hiddenreorderdate" /> 
        <input type="hidden" id="mode" name="mode" />
		</form>
      </div>
      <center>
        <img src="images/logo1.png" />
      </center>
    </div>

    <p align="center">
      <font color="#FFC125">建議解析度 1024x768 以上觀看</font>
    </p>
  </body>
</html>