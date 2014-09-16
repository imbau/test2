<?php
        include("Mydbconnect.php");
        include("connectInfo.php");
		  $option = $_GET["option"];
        $arrangedate =$_GET["date"];
        $arrangetime =$_GET["time"];      
        $car_no = 0;
        $starthour =  6;
        $endhour = 24;
        $tolertime = 0;   
		$user=GetUserInfo();
		$editflag = false;   		
		if($user[0]==-1)		
		{
			echo "<meta http-equiv=REFRESH CONTENT=1;url=login.php>";	
			exit();		
		}
		if($user[0]!="root1")
		{
			$editflag=true;
			$historychecksql = "SELECT * FROM `arrange_log` WHERE `date`='$arrangedate' and  `time`='$arrangetime'";
			$historycheckresult = mysql_query($historychecksql);
			$historycheckrow =mysql_fetch_row($historycheckresult);
			if($historycheckrow[3]!=$user[1])
			{
				echo "<meta http-equiv=REFRESH CONTENT=1;url=arranger.php>";	
				exit();	
			}
		
		}
      

?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta name="generator" content="HTML Tidy for HTML5 (experimental) for Windows https://github.com/w3c/tidy-html5/tree/c63cc39" />
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <link rel="stylesheet" href="css/jquery-ui.css" />
  <link href="bootstrap.min.css" rel="stylesheet" />
  <link rel="stylesheet" href="css/bootstrap-responsive.min.css" />
  <link rel="stylesheet" href="css/bootstrap-image-gallery.min.css" />
  <link type="text/css" href="menu.css" rel="stylesheet" />
  <link rel="stylesheet" type="text/css" href="css/demo.css" />
  <link rel="stylesheet" type="text/css" href="css/style6.css" />
  <link href="css/defaultTheme.css" rel="stylesheet" media="screen" />
  <link href="css/myTheme.css" rel="stylesheet" media="screen" />
  <link rel="stylesheet" href="css/darkwash.css" media="screen" />
  <script type="text/javascript" src="js/jquery-1.8.2"></script>
  <script type="text/javascript" src="js/jquery-ui"></script>
  <script type="text/javascript" src="jquery.lettering.js"></script>
  <script type="text/javascript" src="tools.js"></script>     
  <script src="js/jquery.fixedheadertable.js"></script>
  <script src="js/demo.js"></script>
  <style type="text/css">
        .web_dialog_overlay
        {
            position: fixed;
            top: 0;
            right: 0;
            bottom: 0;
            left: 0;
            height: 100%;
            width: 100%;
            margin: 0;
            padding: 0;
            background: #000000;
            opacity: .15;
            filter: alpha(opacity=15);
            -moz-opacity: .15;
            z-index: 101;
            display: none;
        }
        .web_dialog
        {
            display: none;
            position: fixed;
            width: 190px;
            height: 350px;
            top: 50%;
            left: 70%;
            margin-left: -190px;
            margin-top: -100px;
            background-color: #ffffff;
            border: 2px solid #4169E1;
            padding: 0px;
            z-index: 102;
            font-family: Verdana;
            font-size: 10pt;
        }
        .web_dialog_title
        {
            border-bottom: solid 3px #4169E1;
            background-color: #4169E1;
            padding: 4px;
            color: White;
            font-weight:bold;
        }
        .web_dialog_title a
        {
            color: White;
            text-decoration: none;
        }
        .align_right
        {
            text-align: right;
        }
        .Level2HeadingStyle
        {
	        font-weight: bold;
	        font-size: 15pt;
	        color: #2D84A7;
	        line-height: 20px;
        }
        body { font-size: 62.5%;        
                    color:Black;     
                    background: url(images/background.jpg) no-repeat center top #252525;
                   font-family:Eras Light ITC, Helvetica, sans-serif;
				   }
		 #msg{
		 		font-size:2em;
		 		color:#000080;                                                      
              } 
                  
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
		
		#loaddiv  {
		   width:100%;
           height:350px;
		   overflow:auto;
          }
		  #loaddiv1 {
		   width:100%;
           height:700px;
		   overflow:auto;
          }
  </style>
  <script type="text/javascript">
  	var option = <?php echo $option; ?>;  
  	var arrangedate = '<?php echo $arrangedate; ?>';
    var arrangetime = '<?php echo $arrangetime; ?>';
  	var car,information,selecthour,selectcarid,selectcartype; 	
  	var lockflag=true,lockflag1=true,count=0;
	var newwin = null;
	var user = '<?php echo $editflag; ?>';	
	if(window.name != "bencalie")
	{
		location.reload();
		window.name = "bencalie";
	}
	else
	{
		window.name = "";
	}
	if(user)
	$(document).ready
	(
		function()
		{	
		$("#insertSubmit").click(function (e)
            {
                var brand = $("#Relaystationbrands input:radio:checked").val();                            
                if(brand==1)
                {     
				var answer = confirm("是否要新增頭班中繼點?");
				if(answer)
                 { 
					$("input#carid").val(selectcarid);	
					selecthour[1]=selecthour[1].substring(0,2);
					$.post("<?php echo $linkurlport;?>/WebRoutingArranger/relaypoint.view", { arrangetime: arrangetime, arrangedate: arrangedate, carid:selectcarid, orderhour:selecthour[0], ordermin:selecthour[1],mode:1}, 
                       function(response){    
						if (response.replace(/(^\s*)|(\s*$)/g, "").length ==0)
						{
							window.open ("relaypoint.php?share=0&mode=1&response=找不到中繼點&date="+arrangedate+"&time="+arrangetime +"&carid="+selectcarid,"newwindow", "top=200,left=300,location=1, status=1, scrollbars=1, width=900, height=500");
						}
						else if(response.indexOf("點選錯誤時間")!=-1)
						{
						   alert(response);
						}
						else 
						{
						window.open ("relaypoint.php?share=0&mode=1&response="+response+"&date="+arrangedate+"&time="+arrangetime+"&carid="+selectcarid,"newwindow", "top=200,left=300,location=1, status=1, scrollbars=1, width=900, height=500");
						}
					   
                       	});   
                HideRelaystationDialog();				
                lockflag1=true;    
                count=0; 
				}else
				{
					var answer1 = confirm("是否要新增尾班中繼點?");
					if(answer1)
					{  
						$("input#carid").val(selectcarid);	
						selecthour[1]=selecthour[1].substring(0,2);
					$.post("<?php echo $linkurlport;?>/WebRoutingArranger/relaypoint.view", { arrangetime: arrangetime, arrangedate: arrangedate, carid:selectcarid, orderhour:selecthour[0], ordermin:selecthour[1],mode:2}, 
						function(response){ 
						 if (response.replace(/(^\s*)|(\s*$)/g, "").length ==0)
						{
							window.open ("relaypoint.php?share=0&mode=1&response=找不到中繼點&date="+arrangedate+"&time="+arrangetime ,"newwindow", "top=200,left=300,location=1, status=1, scrollbars=1, width=900, height=500");
						}
						else if(response.indexOf("點選錯誤時間")!=-1)
						{
						   alert(response);
						}
						else
						{
							window.open ("relaypoint.php?share=0&mode=1&response="+response+"&date="+arrangedate+"&time="+arrangetime+"&carid="+selectcarid,"newwindow", "top=200,left=300,location=1, status=1, scrollbars=1, width=900, height=500");
						}
							});  
						HideRelaystationDialog();				
						lockflag1=true;    
						count=0; 
						}
						else
						{
							HideRelaystationDialog();			
						}
					}
                }
                else if(brand==3)
                {
                 $("input#carid").val(selectcarid);
                 $("input#cartype").val(selectcartype);    							 
                 $("input#timeselect").val(selecthour[0]);     
                 $("#dialog-form").dialog("open");                
                 HideRelaystationDialog();  
                 lockflag1=true;    
                 count=0;                             
                }else if(brand==2)
				{
					$("input#carid").val(selectcarid);	
					selecthour[1]=selecthour[1].substring(0,2);
					$.post("<?php echo $linkurlport;?>/WebRoutingArranger/LookupRequest.view", { arrangetime: arrangetime, arrangedate: arrangedate, carid:selectcarid, orderhour:selecthour[0], ordermin:selecthour[1],mode:0}, 
						function(response){ 
						 if (response.replace(/(^\s*)|(\s*$)/g, "").length ==0)
						{
							window.open ("relaypoint.php?share=0&mode=0&response=找不到車趟&date="+arrangedate+"&time="+arrangetime ,"newwindow", "top=200,left=300,location=1, status=1, scrollbars=1, width=900, height=500");
						}
					else if(response.indexOf("點選錯誤時間")!=-1)
						{
						   alert(response);
						}
						else
						{
							window.open ("relaypoint.php?share=0&mode=0&response="+response+"&date="+arrangedate+"&time="+arrangetime+"&carid="+selectcarid,"newwindow", "top=200,left=300,location=1, status=1, scrollbars=1, width=900, height=500");
						}
							});  
						HideRelaystationDialog();				
						lockflag1=true;    
						count=0; 
			
				}
				else if(brand==4)
				{
				   $("input#carid").val(selectcarid);
				  $("#dialog-form1").dialog("open");    
				   HideRelaystationDialog();  
					lockflag1=true;    
					count=0;               
				}
				else if(brand==5)
				{
				
                     $("input#timeselect").val(selecthour[0]);  
					 selecthour[1]=selecthour[1].substring(0,2);
					 $("input#minute").val(selecthour[1]);    
					$("input#carid").val(selectcarid);								
					 $("#dialog-form2").dialog("open");    
					HideRelaystationDialog();  
					lockflag1=true;    
					count=0;      
				}
                lockflag++;   
                
                e.preventDefault();
            });
			
          $("#btnSubmit").click(function (e)
            {
                var brand = $("#brands input:radio:checked").val();       
                if(brand=="刪除班次")
                {
                	var answer = confirm("是否刪除此預約排班?");
                	if(answer)
                     {             
                      $.post("action.php", { time: arrangetime, date: arrangedate, car:car, information:information}, 
                       function(){                                            	
                       	location.reload();
                       	});      
					 HideDialog();
                    }
                }
                else
                {
						lockflag1=true;    
						count=0;   
						selecthour[1]=selecthour[1].substring(0,2);	
						var answer = confirm("是否新增預約者?");
						if(answer)
						{ 
							$("#dialog-form").dialog("open");   	
						}	
						else
						{
							$.post("<?php echo $linkurlport;?>/WebRoutingArranger/LookupRequest.view", { arrangetime: arrangetime, arrangedate: arrangedate, carid:car, orderhour:selecthour[0], ordermin:selecthour[1],mode:1}, 
							function(response){ 
								if (response.replace(/(^\s*)|(\s*$)/g, "").length ==0)
								{
									window.open ("relaypoint.php?share=1&mode=0&response=找不到車趟&date="+arrangedate+"&time="+arrangetime ,"newwindow", "top=200,left=300,location=1, status=1, scrollbars=1, width=900, height=500");
								}
								else if(response.indexOf("點選錯誤時間")!=-1)
								{
									alert(response);
								}
								else
								{
									window.open ("relaypoint.php?share=1&mode=0&response="+response+"&date="+arrangedate+"&time="+arrangetime+"&carid="+car,"newwindow", "top=200,left=300,location=1, status=1, scrollbars=1, width=900, height=500");
								}
								});  
						}
						HideDialog();
                }
				 lockflag++;  
                e.preventDefault();	
            });
             $("td").click
                (
                        function(e)
                        {
                                if($(this).attr("class") == "empty"&&(option==0))
                                {      
                              
									if(lockflag&&count==0)
                                      {
                                      	ShowRelaystationDialog(false);  
                                      	selectcarid=$(this).parent().children(".carid").children("input").val();
                                      	selectcartype=$(this).parent().children(".cartype").children("input").val();                                      	                                    
                                      	 lockflag1=false;    
                                      	 count++;  
                                       }else if(lockflag&&count==1)
                                       {
                                         
                                      	 ShowRelaystationDialog(false);  
                                      	 lockflag1=true;    
                                      	 count=0; 
                                       }
                                         
                                        
                                }
                                else if($(this).attr("class") == "nonempty"&&(option==0))
                                {       
                                	if(lockflag1&&count==0)
                                     {
                                     ShowDialog(false);  
                                	 car=$(this).parent().children(".carid").children("input").val();
                                	 $("input#carid").val($(this).parent().children(".carid").children("input").val());
                                     $("input#cartype").val($(this).parent().children(".cartype").children("input").val());                                     
                                	 information=$(this).children("input.runnumber").val()
                                	 lockflag=false;    
                                     count++;   
                                	 }
                                	 else if(lockflag1&&count==1)
                                       {
                                         ShowDialog(false);  
                                      	 lockflag=true;    
                                      	 count=0; 
                                       }
                                }
								else if($(this).attr("class") == "worktime"&&(option==0))
                                {  
									worktime=$(this).attr("id").split("~");
									selectcarid=$(this).parent().children(".carid").children("input").val();
									$("input#carid").val(selectcarid);	
									$("input#starthr").val(worktime[1].split(":")[0]);	
									$("input#startmin").val(worktime[1].split(":")[1]);	
									$("input#endhr").val(worktime[2].split(":")[0]);	
									$("input#endmin").val(worktime[2].split(":")[1]);	
									 $("#dialog-form3").dialog("open");     
								}
                                 
                        }
                )                 		 
            $( "#dialog-form" ).dialog
					(
							{
				
							autoOpen: false,
							height: 650,
							width: 800,
							modal: true,
							buttons: 
							{
							"資料送出": function()
							{
                                //檢查資料完整性
                                var checkall = false;
                                if($("input#name").val() == "")
									{
                                        alert("請填入姓名!!");
                                        $("input#name").focus();
                                       }
                                else if($("input#account").val() == "")
									{
                                        alert("請填入帳號!!");
                                        $("input#account").focus();
									}
                                else if($("input#Disabilities").val() == "")
									{
                                        alert("請填入障別!!");
                                        $("input#Disabilities").focus();
									}
                                else if($("select#timeselect").val() =="")
									{
                                        alert("請選擇預約時間!!");
                                        $("select#timeselect").focus();
									}
                                else if($("input#minute").val() == "")
									{
                                        alert("請輸入預約時間!!");
                                        $("input#minute").focus();
									}
                                else if($("input#traveltime").val() == "")
									{
                                        alert("請輸入上下車地點的交通時間，單位為秒!!");
                                        $("input#traveltime").focus();
									}
                                else if($("input#startarea").val() == ""|| switchareaindex($("input#startarea").val())==-1)
									{
                                        alert("請輸入正確上車區域!!");
                                        $("input#startarea").focus();
									}
                                else if($("input#startadd").val() == "")
									{
                                        alert("請輸入上車地址!!");
                                        $("input#startadd").focus();
									}
									else if($("input#startRemark").val() == "")
									{
                                        alert("請輸入上車備註!!");
                                        $("input#startRemark").focus();
									}
                                else if($("input#endarea").val() == ""|| switchareaindex($("input#endarea").val())==-1)
									{
                                        alert("請輸入正確下車區域!!");
                                        $("input#endarea").focus();
									}
                                else if($("input#endadd").val() == "")
									{
                                        alert("請輸入下車地址!!");
                                        $("input#endadd").focus();
									}
									else if($("input#endRemark").val() == "")
									{
                                        alert("請輸入下車備註!!");
                                        $("input#endRemark").focus();
									}
                                else
									{
                                        var answer = confirm("確定新增這筆預約資料?");
                                        if(answer)
                                        {
                                                checkall = true;                                                 
                                        }
									}
                                    if(checkall)
									{
                                       $.post("<?php echo $linkurlport;?>/WebRoutingArranger/cararranger.view", 
                                            { 
                                                arrangedate: arrangedate,
                                                arrangetime: arrangetime,
                                                status: $("select#status").val(),
                                                name: $("input#name").val(),
                                                account: $("input#account").val(),
                                                Disabilities: $("input#Disabilities").val(),
                                                orderhour: $("input#timeselect").val(),
                                                orderminute: $("input#minute").val(),
                                                carid: $("input#carid").val(),
                                                cartype: $("input#cartype").val(),
                                                traveltime: $("input#traveltime").val(),
                                                startarea: $("input#startarea").val(),
                                                startadd: $("input#startadd").val(),
                                                endarea: $("input#endarea").val(),
                                                endadd: $("input#endadd").val(),
                                                sharing: $("select#shareselect").val(),
												startRemark: $("input#startRemark").val(),
												endRemark: $("input#endRemark").val(),
                                                tolorent:600
                                                 }, function(returnString)
                                                        {
                                                                var temp = returnString.split(",");
                                                                if(temp[0] == 0)
                                                                {
                                                                        alert(temp[1]);
                                                                }
                                                                else
                                                                {
                                                                        alert(temp[1]);																	
                                                                        location.reload();
                                                                }
                                                        });
                                                        $( this ).dialog( "close" );

                                    }
                        },
                        "取消": function()
                        {                           
                            $( this ).dialog( "close" );                          
                        }
                    },
                close: function()
                {
                        allFields.val( "" ).removeClass( "ui-state-error" );
                }
             }
        );  
 $( "#dialog-form1" ).dialog
					(
							{
				
							autoOpen: false,
							height: 500,
							width: 600,
							modal: true,
							buttons: 
							{
							"資料送出": function()
							{
                                //檢查資料完整性
                                var checkall = false;
                               if($("input#account1").val() == "")
									{
                                        alert("請填入帳號!!");
                                        $("input#account1").focus();
									}
							 else if($("select#date1").val() ==-1)
									{
                                        alert("請輸入欲支援的排班日期!!");
                                        $("select#date1").focus();
									}
                              else if($("input#startarea1").val() == "")
									{
                                        alert("請輸入正確上車區域!!");
                                        $("input#startarea1").focus();
									}
                                else if($("input#startadd1").val() == "")
									{
                                        alert("請輸入上車地址!!");
                                        $("input#startadd1").focus();
									}
                                else if($("input#endarea1").val() == "")
									{
                                        alert("請輸入正確下車區域!!");
                                        $("input#endarea1").focus();
									}
                                else if($("input#endadd1").val() == "")
									{
                                        alert("請輸入下車地址!!");
                                        $("input#endadd1").focus();
									}
                                else
									{
                                        var answer = confirm("確定新增這筆預約資料?");
                                        if(answer)
                                        {
                                                checkall = true;                                                 
                                        }
									}
                                    if(checkall)
									{
									var  Designationdatetime= $("select#date1").val().split("_");								
                                    $.post("<?php echo $linkurlport;?>/WebRoutingArranger/CrosscenterScheduling.view", 
                                     { 
                                                arrangedate: arrangedate,
                                                arrangetime: arrangetime,
												Designationdate:Designationdatetime[0],
                                                Designationtime:Designationdatetime[1],
                                                account: $("input#account1").val(),                                             
                                                carid: $("input#carid").val(),                                              
                                                startarea: $("input#startarea1").val(),
                                                startadd: $("input#startadd1").val(),
                                                endarea: $("input#endarea1").val(),
                                                endadd: $("input#endadd1").val()                                           
                                                 }, function(returnString)
                                                        {
                                                                var temp = returnString.split(",");
                                                                if(temp[0] == 0)
                                                                {
                                                                        alert(temp[1]);
                                                                }
                                                                else
                                                                {
                                                                        alert(temp[1]);																	
                                                                        location.reload();
                                                                }
                                                        });
                                                        $( this ).dialog( "close" );

                                    }
                        },
                        "取消": function()
                        {                           
                            $( this ).dialog( "close" );                          
                        }
                    },
                close: function()
                {
                        allFields.val( "" ).removeClass( "ui-state-error" );
                }
             }
        );		
		$( "#dialog-form2" ).dialog
					(
							{
				
							autoOpen: false,
							height: 400,
							width: 400,
							modal: true,
							buttons: 
							{
							"資料送出": function()
							{
                                //檢查資料完整性
                                var checkall = false;
                                if($("input#TimeLength").val() == "")
									{
                                        alert("請輸入空趟時間!!");
                                        $("input#TimeLength").focus();
									}                              
                                else
									{
                                        var answer = confirm("確定新增這筆預約資料?");
                                        if(answer)
                                        {
                                                checkall = true;                                                 
                                        }
									}
                                    if(checkall)
									{										
                                  $.post("<?php echo $linkurlport;?>/WebRoutingArranger/FalseTrip.view", 
                                     { 
                                                arrangedate: arrangedate,
                                                arrangetime: arrangetime,	
											    orderhour: $("input#timeselect").val(),
                                                orderminute: $("input#minute").val(),
                                                carid: $("input#carid").val(),     
                                                TimeLength: $("input#TimeLength").val() ,
												Meeting: $("select#Meeting").val()												
                                                 }, function(returnString)
                                                        {
                                                                var temp = returnString.split(",");
                                                                  alert(temp[1]);																	
                                                                   location.reload();
                                                        });
                                                        $( this ).dialog( "close" );

                                    }
                        },
                        "取消": function()
                        {                           
                            $( this ).dialog( "close" );                          
                        }
                    },
                close: function()
                {
                        allFields.val( "" ).removeClass( "ui-state-error" );
                }
             }
        );	
			$( "#dialog-form3" ).dialog
					(
							{
				
							autoOpen: false,
							height: 400,
							width: 400,
							modal: true,
							buttons: 
							{
							"資料送出": function()
							{
                                //檢查資料完整性
                                var checkall = false;

                                if($("input#starthr").val() == "")
									{
                                        alert("請輸入起始的小時!!");
                                        $("input#starthr").focus();
									}
								else if($("input#startmin").val() == "")
									{
                                        alert("請輸入起始的分!!");
                                        $("input#startmin").focus();
									}  
								else if($("input#endhr").val() == "")
									{
                                        alert("請輸入結束的小時!!");
                                        $("input#endhr").focus();
									}  
							   else if($("input#endmin").val() == "")
									{
                                        alert("請輸入結束的分!!");
                                        $("input#endmin").focus();
									}  
                                else
									{
                                        var answer = confirm("確定要修改司機上班時間?");
                                        if(answer)
                                        {
                                                checkall = true;                                                 
                                        }
									}
                                    if(checkall)
									{	
									
									
                                  $.post("<?php echo $linkurlport;?>/WebRoutingArranger/CorrectWorktime.view", 
                                     { 
                                                arrangedate: arrangedate,
                                                arrangetime: arrangetime,	
											    starthr: $("input#starthr").val(),
                                                startmin: $("input#startmin").val(),
												endhr: $("input#endhr").val(),
                                                endmin: $("input#endmin").val(),
                                                carid: $("input#carid").val()
                                                 }, function(returnString)
                                                        {
                                                                var temp = returnString.split(",");
                                                                  alert(temp[1]);																	
                                                                   location.reload();
                                                        });
                                                        $( this ).dialog( "close" );
                                    }
                        },
                        "取消": function()
                        {                           
                            $( this ).dialog( "close" );                          
                        }
                    },
                close: function()
                {
                        allFields.val( "" ).removeClass( "ui-state-error" );
                }
             }
        );	
        }
  ); 
  	
       function test(trow)
        {
			
			var str=trow.id.split(".");   
			selecthour=document.getElementById('table1').rows[0].cells[str[0]-7].innerHTML.split(":"); 	
        }
       function ShowDialog(modal)
        {
            $("#overlay").show();           
            $("#dialog").slideToggle(200);  
            if (modal)
            {
                $("#overlay").unbind("click");
            }
            else
            {
                $("#overlay").click(function (e)
                {
                    HideDialog();
                });
            }
        }
      function controltable()
       {
		window.open ("controltable.php?date="+arrangedate+"&time="+arrangetime,"newwindow1", "location=1, status=1, scrollbars=1, width=1200, height=750");
	  }
	     function areatable(area)
       {
		window.open ("areatable.php?option=0&date="+arrangedate+"&time="+arrangetime+"&area="+area,"areawindow", "location=1, status=1, scrollbars=1, width=1300, height=870");
	  }
	   function reqtable()
       {
	      $.get("<?php  echo $linkurlport; ?>/WebRoutingArranger/WriteExcel.view",
           {
               	arrangetime: arrangetime,
                arrangedate: arrangedate
             },function(response) 
             {
                  window.open ("reqtable.php?option=0&date="+arrangedate+"&time="+arrangetime,"areawindow", "location=1, status=1, scrollbars=1, width=1300, height=870");
			}
			);	
	
	  }
	  
	     function Notdischargedtable()
       {
	     window.open ("reqtable.php?date="+arrangedate+"&time="+arrangetime+"&mode=1","newwindow3", "location=1, status=1, scrollbars=1, width=1200, height=750");
	
	  }
	    function allreq()
       {
		window.open ("Inquiryreq.php?date="+arrangedate+"&time="+arrangetime+"&mode=1","new", "location=1, status=1, scrollbars=1, width=1200, height=750");
	  }
    function ShowRelaystationDialog(modal)
        {
            $("#overlay").show();           
            $("#insertreqdialog").slideToggle(200);  
            if (modal)
            {
                $("#overlay").unbind("click");
            }
            else
            {
                $("#overlay").click(function (e)
                {
                    HideDialog();
                });
            }
        }
       

	function HideRelaystationDialog()
        {
            $("#overlay").hide();
            $("#insertreqdialog").fadeOut(300);
        } 
     function HideDialog()
        {
            $("#overlay").hide();
            $("#dialog").fadeOut(300);
        } 		
       function Candidate()
			{				
				window.open ("arrangedtable.php?date="+arrangedate+"&time="+arrangetime ,"Candidatewindow", "location=1, status=1, scrollbars=1, width=1200, height=750");
			}
			
			function ClearMode()
			{
				window.location="finalarrangedtable.php?option=-1&date="+arrangedate+"&time="+arrangetime;				
			}
		function EditMode()
			{
				window.location="finalarrangedtable.php?option=0&date="+arrangedate+"&time="+arrangetime;				
			}
		function cleartable() {	
                    $.get("<?php  echo $linkurlport; ?>/WebRoutingArranger/clear.view",
                     {
                     	arrangetime: arrangetime,
                     	arrangedate: arrangedate, 
                     	mode: 1
                     	},function(response) 
                     	{
							window.location.href="finalarrangedtable.php?option=0&date="+arrangedate+"&time="+arrangetime ;
						});	
                }	
		function deletetable() 
		{	
                    $.get("<?php  echo $linkurlport; ?>/routingarrange/deletetable.php",
                     {
                     	arrangetime: arrangetime,
                     	arrangedate: arrangedate
                     	},function(response) 
                     	{
                     	  alert(response);
						  window.location.assign("<?php  echo $linkurlport; ?>/routingarrange/arranger.php");
						});	
               }	
		 function open_win_detail(reqnum) {	
                     window.open ("unusualreq.php?reqnum="+reqnum+"&date="+arrangedate+"&time="+arrangetime ,"unusualreq", "top=200,left=300,location=1, status=1, scrollbars=1, width=550, height=500");
		            }	
		 function open_win(reqnum,Reason) {			 				
                     window.open ("notdischarged.php?reqnum="+reqnum+"&date="+arrangedate+"&time="+arrangetime+"&Reason="+Reason,"Editar notícia", "top=100,left=300,location=1, status=1, scrollbars=1, width=400, height=300");
		          
                }	
          function deletedriver() 
		  {  
		       window.open ("drivertable.php?date="+arrangedate+"&time="+arrangetime+"&mode=1","changedriver", "top=100,left=300,location=1, status=1, scrollbars=1, width=1200, height=700");
		  }
		 function adddriver() 
		  {  
		        window.open ("drivertable.php?date="+arrangedate+"&time="+arrangetime+"&mode=0","changedriver", "top=100,left=300,location=1, status=1, scrollbars=1, width=1200, height=700");
		  }
					   
	      function check_PreProcess()
		  {
				window.open ("checkHeadandtail.php?date="+arrangedate+"&time="+arrangetime+"&mode=1"," checkwindows", "location=1, status=1, scrollbars=1, width=1200, height=750");
		}
		 function open_win_PreProcess() {
		            $.get("<?php  echo $linkurlport; ?>/WebRoutingArranger/headtailteam.view", {arrangetime: arrangetime, arrangedate: arrangedate, mode: 1},
					function(response) 
					{ 
						  alert(checkstatus(response,newwin));
					});	
				newwin=window.open('<?  echo $linkurlport;?>'+"/routingarrange/redirect.php?date="+arrangedate+"&time="+arrangetime+"&mode=2","newwindows","height=100, width=500");						
					   }
					 function tessss() {
		            $.get("<?php  echo $linkurlport; ?>/WebRoutingArranger/test11.view", {},
					function(response) 
					{ 
						 alert(checkstatus(response,newwin));
					});	
				 }
				  function test2() {
		            $.get("<?php  echo $linkurlport; ?>/WebRoutingArranger/test2.view", {},
					function(response) 
					{ 
						    alert(checkstatus(response,newwin));
					});	
				 }
		function open_win_PreRoutingArranger() {
					    $.get("<?php  echo $linkurlport; ?>/WebRoutingArranger/PreRoutingArrangertest.view", {arrangetime: arrangetime, arrangedate: arrangedate, mode: 2},
					function(response) 
					{ 	
					alert(checkstatus(response,newwin));
					});	
					newwin=window.open('<?  echo $linkurlport;?>'+"/routingarrange/redirect.php?date="+arrangedate+"&time="+arrangetime+"&mode=6","newwindows","height=100, width=500");										
                }
	    function open_win_RoutingArranger() {
					    $.get("<?php  echo $linkurlport; ?>/WebRoutingArranger/RoutingArrangertest.view", {arrangetime: arrangetime, arrangedate: arrangedate, mode: 2},
					function(response) 
					{
         					alert(checkstatus(response,newwin));
					}
						);	
					newwin=window.open('<?  echo $linkurlport;?>'+"/routingarrange/redirect.php?date="+arrangedate+"&time="+arrangetime+"&mode=1","newwindows","height=100, width=500");										
                }
                
         function open_win_Specialareatotaipei() {
					  $.get("<?php  echo $linkurlport; ?>/WebRoutingArranger/Specialareatotaipei.view", {arrangetime: arrangetime, arrangedate: arrangedate, mode: 2},
					function(response) 
					{ 	
					alert(checkstatus(response,newwin));
					}
											);	
				newwin=window.open('<?  echo $linkurlport;?>'+"/routingarrange/redirect.php?date="+arrangedate+"&time="+arrangetime+"&mode=7","newwindows","height=100, width=500");
                }   
			function relaxtime() 
			{
					  $.get("<?php  echo $linkurlport; ?>/WebRoutingArranger/SearchRelaxTime.view", {arrangetime: arrangetime, arrangedate: arrangedate},
					function(response) 
					{ 	
					   alert(response);
					    window.location.reload(); 
					   newwin=window.open('<?  echo $linkurlport;?>'+"/routingarrange/drivertable.php?date="+arrangedate+"&time="+arrangetime+"&mode=2","newwindows","height=750, width=1200");
					});	
				
               }   
				
                
		/*******************刷新頁面div*********************/
		function RefreshUpdatePanel()
			{						
			 $("#loaddiv").load(location.href + " #loaddiv");
			  $("#loaddiv1").load(location.href + " #loaddiv1");
			$("#table").load(location.href + " #table");		
			}				
		/*******************刷新頁面div*********************/		
		/*******************載入頁面div*********************/
		function loadmunuphp()
			{						
			 $("#loaddiv").load(""+ " #loaddiv");
			
			
			}				
		/*******************載入頁面div*********************/	
		
		
		function checkstatus(response,newwin)
		{
		var message="";
		 if(response.indexOf("Success")!=-1)
		{
			message="Success";
		}
		  else if(response.indexOf("-2")!=-1)
		  {
			message="已超過今日配額--請換ip重新排班";
		  }
		  else if(response.indexOf("-3")!=-1)
		  {		     
			message="要求已遭拒絕";
		  }
		  else if(response.indexOf("-4")!=-1)
		  {
		  	message="不存在的addres";		  	
		  }
		  else if(response.indexOf("-5")!=-1)
		  {
		  	message="查詢(address或latlng)遺失了";		  
		  }
		  else if(response.indexOf("-6")!=-1)
		  {
		  	message="有車子缺頭尾班";		   
		  }
		   else if(response.indexOf("-7")!=-1)
		  {
		  	message="排班發生錯誤，請按排班鈕繼續";		  
		  }
		   else if(response.indexOf("-8")!=-1)
		  {
		  	message="地址有誤，找不到地址";		  
		  }
		  newwin.close();
		  newwin=null;
		  window.location.reload(); 
		  return message;
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
function  switchareaindex( areadata)
	{
	 var area=0;
	switch(areadata)
	{	
		case "新北市汐止區":
			area=0;
		break;	
		case "新北市新店區":
			area=1;
		break;
		case "新北市中和區":
			area=2;
			break;
		case "新北市土城區":
			area=3;
			break;			
		case "新北市永和區":
			area=4;
			break;			
		case "新北市板橋區":
			area=5;
			break;
		case "新北市三峽區":
			area=6;
			break;			
		case "新北市鶯歌區":
			area=7;
			break;
		case "新北市瑞芳區":
			area=8;
			break;
		case "新北市新莊區":
			area=9;
			break;
		case "新北市深坑區":
			area=10;
			break;
		case "新北市林口區":
			area=11;
			break;
		case "新北市貢寮區":
			area=12;
			break;			
		case "新北市八里區":
			area=13;
			break;		
		case "新北市泰山區":
			area=14;
			break;
		case "新北市淡水區":
			area=15;
			break;
		case "新北市樹林區":			
			area=16;
			break;		
		case "台北市士林區":	
		case "台北市南港區":
		case "台北市大同區":	
		case "台北市大安區":			
		case "台北市中正區":			
		case "台北市內湖區":			
		case "台北市文山區":			
		case "台北市北投區":			
		case "台北市松山區":
		case "台北市萬華區":
		case "台北市信義區":
		case "台北市中山區":
			area=17;
			break;	
		case "基隆市七堵區":
		case "基隆市中正區":
		case "基隆市安樂區":
		case "基隆市仁愛區":
		case "基隆市信義區":	
		case "基隆市暖暖區":	
		case "基隆市五堵區":	
			area=18;
			break;	
		case "桃園縣中壢市":
		case "桃園縣桃園市":
		case "桃園縣龜山鄉":
		case "桃園縣八德市":
		case "桃園縣大溪鎮":
		case "桃園縣大園鄉":
		case "桃園縣楊梅市":
		case "桃園縣新屋鄉":
		case "桃園縣觀音鄉":
		case "桃園縣復興鄉":
		case "桃園縣平鎮市":
		case "桃園縣蘆竹鄉":	
		case "桃園縣龍潭鄉":	
		case "桃園市中壢區":
		case "桃園市桃園區":
		case "桃園市龜山區":
		case "桃園市八德區":
		case "桃園市大溪區":
		case "桃園市大園區":
		case "桃園市楊梅區":
		case "桃園市新屋區":
		case "桃園市觀音區":
		case "桃園市復興區":
		case "桃園市平鎮區":
		case "桃園市蘆竹區":	
		case "桃園市龍潭區":	
			area=19;
			break;	
		case "新北市烏來區":
			area=20;
			break;	
		case "新北市坪林區":
			area=21;
			break;	
		case "新北市石碇區":
			area=22;
			break;	
		case "新北市雙溪區":
			area=23;
			break;
		case "新北市五股區":
			area=24;
			break;	
		case "新北市三重區":
			area=25;
			break;	
		case "新北市蘆洲區":
			area=26;
			break;	
		case "新北市金山區":
			area=27;
			break;	
		case "新北市三芝區":
			area=28;
			break;	
		case "新北市石門區":
			area=29;
			break;	
		case "新北市萬里區":
			area=30;
			break;	
		case "新北市平溪區":
			area=31;
			break;	
		default:
			area=-1;
		    break;
			
			
		}
		return area;
	}
</script>
</head>
<body> 
  <div class="container-fluid">
  <div class="row-fluid">     
   <div class="span3">  
   <div id="msg" > 
			&nbsp;&nbsp;&nbsp;未排入班次	
			 <? 
			  $Candidate = mysql_query("SELECT no FROM userrequests WHERE 狀態='候補' AND arrangedate = '".$arrangedate."' AND arrangetime = '".$arrangetime."'");
			  $Candidatenotcount = mysql_num_rows($Candidate);
			  $normal = mysql_query("SELECT no FROM userrequests WHERE 狀態!='候補' AND arrangedate = '".$arrangedate."' AND arrangetime = '".$arrangetime."'");
			  $normalcount = mysql_num_rows($normal);			  	
			  $Share = mysql_query("SELECT * FROM travelinformationofcarsharing WHERE  date = '".$arrangedate."' AND arrangetime = '".$arrangetime."'");
			  $ShareNum = mysql_num_rows($Share);			  		 			  
			  $notdischargecount=0;
			  $Candidatenotdischargecount=0;
			  $normalnotdischarge = mysql_query("SELECT no FROM userrequests WHERE  arranged<=0 and 狀態!='候補' AND arrangedate = '".$arrangedate."' AND arrangetime = '".$arrangetime."'");
			  $Candidatenotdischarge = mysql_query("SELECT no FROM userrequests WHERE  arranged<=0 and 狀態='候補' AND arrangedate = '".$arrangedate."' AND arrangetime = '".$arrangetime."'");
			  $notdischargecount = mysql_num_rows($normalnotdischarge);	
			  $Candidatenotdischargecount = mysql_num_rows($Candidatenotdischarge);		
			  echo "<h4>&nbsp;&nbsp;&nbsp;正常班次未排入數:".$notdischargecount."/".$normalcount;
			  echo "</br>&nbsp;&nbsp;&nbsp;候補班次未排入數:".$Candidatenotdischargecount."/".$Candidatenotcount;			
		     echo "</br>&nbsp;&nbsp;&nbsp;共乘趟數:".$ShareNum."趟</h4>";					  
			  echo "</div >";		
			  ?>
	    	
			<div id="loaddiv1" >
            <ul class="nav nav-list">
         	<?         	
			echo "<table border=\"1\" cellspacing='0' style=\"overflow-y:scroll\">";				
			 echo "<tr bgcolor='#6495ED'><th >姓名</th><th>時段</th><th >旅行時間</th><th width='32%'>原因</th><th width='28%'>查看</th></tr>";
			printnotdischarge($arrangetime,$arrangedate);
			 function printnotdischarge($arrangetime,$arrangedate)
			  {			  	 
			  	 $notdischarge  = mysql_query("SELECT *FROM userrequests WHERE arranged <=0 AND arrangedate = '".$arrangedate."' AND arrangetime = '".$arrangetime."'");
	             $notdischarge_num_row = mysql_num_rows($notdischarge);	
		    	if($notdischarge_num_row>0)			
				{
					while($notdischarge_num_row = mysql_fetch_array($notdischarge))
						{
							if($notdischarge_num_row['arranged'] != -1)
								{
								if($notdischarge_num_row['抵達時間'] > 0)
								{	
									$time = $notdischarge_num_row['時段']."<br />";
									$hour = substr($time, 0, 2);
									$min  = substr($time, 2, 2);
									$timesec = $hour * 3600 + $min *60;
									$traveltime = $notdischarge_num_row['抵達時間'] - $timesec;
									$traveltimemin = (int)($traveltime / 60);
									$traveltimesec = $traveltime % 60;
									echo "<tr bgcolor='#E0FFFF'><td>".$notdischarge_num_row['姓名'].":".$notdischarge_num_row["狀態"]."</td><td>".$notdischarge_num_row['時段']."</td><td>".$traveltimemin."分".$traveltimesec."秒</td>";
								}
						if($notdischarge_num_row['arranged'] == 0)
							{
								$Reason="找不到合適車輛!!";								
								echo "<td>找不到合適車輛!!</td>";
							}	
						else if($notdischarge_num_row['arranged'] == -2)
							{
								$Reason="被刪除班次!!";								
								echo "<td>被刪除班次!!</td>";
							}
						else if($notdischarge_num_row['arranged'] ==-3)
							{
								$Reason="未排入候補排班!!";								
								echo "<td>未排入候補排班!!</td>";
							}	
							else if($notdischarge_num_row['arranged'] ==-4)
							{
								$Reason="優先區域未排入";								
								echo "<td>優先區域未排入!!</td>";
							}						 
							echo "<td><a  href='#'  onclick=open_win('".$notdischarge_num_row['識別碼']."','".$Reason."') class='btn btn-primary'>詳細資訊</a></td>";
							}
						}
						}
				echo "</table>";
			  }
				
			?>
            </ul>
          </div><!--/.well -->				  
        </div><!--/span-->
  <div class="span9">
  <iframe id="preview-frame" src="menu.php" name="preview-frame" frameborder="0"  noresize="noresize" style="width: 1150px;  height:225px; position: relative; z-index: 500;">
	</iframe>
	<!---<div id="preview" style="width: 900px;  height:170px;"></div>-->
    <div class="well1">	   
	  <?php 
	  function printstaus($progress)
	 {
	 $message="";
	   switch($progress)
	   {
	     case 1:
			$message="頭尾排班";
		 break;
		 case 2:
			$message="特殊區域到台北排班";
		 break;
		 case 3:
			$message="特殊區域排班";
		 break;
		 case 4:
			$message="一般區域排班";
		 break;
		 case 5:
			$message="候補趟次排班";
		 break;  
		 default:
			$message="";
		 break;
	   }
	 return $message;
	 }
	 if($user[0]!="root1")
	{  if($option==0)
	   {
	   echo "<div style=' width:800px;display:inline;'><h3>";
	   echo "功能按鈕:  </h3>";	  
	   echo " <a href='#' class='btn btn-success' onclick=open_win_PreProcess()>排頭尾班</a>&nbsp;";
	   // echo " <a href='#' class='btn btn-success' onclick=tessss()>test</a>&nbsp;";  
		//echo " <a href='#' class='btn btn-success' onclick=test2()>test2</a>&nbsp;";  
	   echo " <a href='#' class='btn btn-success' onclick=check_PreProcess()>檢查頭尾班</a>&nbsp;";		   	
	   echo " <a href='#' class='btn btn-success' onclick=open_win_Specialareatotaipei()>特殊區域去台北排班</a>&nbsp;";
       echo " <a href='#' class='btn btn-success' onclick=open_win_PreRoutingArranger()>特殊區域優先排班</a>&nbsp;";	
    	
    	$rs = mysql_query("SELECT * FROM progress WHERE `index`='2' and date='".$arrangedate."' and time='".$arrangetime."'");
		$rsprogress = mysql_query("SELECT * FROM progress WHERE `index`='1' and date='".$arrangedate."' and time='".$arrangetime."'");
		$rsprogress1 = mysql_fetch_array($rsprogress);		
	 	echo "<a href='#' class='btn btn-success' onclick=open_win_RoutingArranger() >一般排班</a>&nbsp;";	
		echo "<a href='#' class='btn btn-success' onclick=Candidate() >候補排班</a>&nbsp;";
		echo "<a href='#' class='btn btn-success' onclick=relaxtime() >尋找休息時間</a>&nbsp;";
		echo " <a href='#' class='btn btn-success' onclick=deletedriver()>刪除司機</a>&nbsp;";
		echo " <a href='#' class='btn btn-success' onclick=adddriver()>新增司機</a>&nbsp;";
		 echo "</div>";	
		 for($index=0;$index<40;$index++)
		 {
			echo "&nbsp;";
		 }
	   }
	   else  if($option==-1)
	   {
		echo "<a href='#' class='btn btn-success' onclick=cleartable() >清除排班紀錄</a>&nbsp;";
		echo "<a href='#' class='btn btn-success' onclick=deletetable() >刪除排班紀錄</a>&nbsp;";
	   }
	   else 
	   {
	   	   echo "<div style=' width:300px;'><h3>";
		   echo "功能按鈕:</h3>";	 
		   echo "<a href='#' class='btn btn-success' onclick=EditMode() >編輯模式</a>&nbsp;"; 
		   echo "<a href='#' class='btn btn-success' onclick=ClearMode() >清除模式</a>&nbsp;"; 
		   echo " </div>";
	   }
	   	}
	  if($option!=-1)
        {  echo "<div style=' width:1000px;'><h3>檢視按鈕: </h3> ";	
			if($user[0]=="root")
			{
				echo  "<a  href='#'   class='btn btn-inverse' onclick=areatable('土城')  >土城排班表</a>&nbsp;";
				echo  "<a  href='#'   class='btn btn-inverse'  onclick=areatable('汐止') >汐止排班表</a>&nbsp;";
				echo  "<a  href='#'   class='btn btn-inverse'  onclick=areatable('新店') >新店排班表</a>&nbsp;";		
				echo  "<a  href='#'   class='btn btn-inverse'  onclick=areatable('中和') > 中和排班表</a>&nbsp;";						
			}
			echo  "<a  href='#'   class='btn btn-inverse'  onclick=allreq() >查看所有需求</a>&nbsp;";
			echo  "<a  href='testarrangedtable1.php?option=$option&date=$arrangedate&time=$arrangetime'  class='btn btn-inverse'>個別司機查詢</a>&nbsp;";
			echo  "<a  href='#'   class='btn btn-inverse'  onclick=controltable() >列印控制總表</a>&nbsp;";
		    echo  "<a  href='#'   class='btn btn-inverse'  onclick=reqtable() >列印預約表</a>&nbsp;";
			echo  "<a  href='#'   class='btn btn-inverse'  onclick=Notdischargedtable() >列印未排入預約表</a>&nbsp;";
			echo  "<a  href='http://cars.hinet.net/FuBus/LoginTaipeiCounty.jsp'   target='newwindow'  class='btn btn-inverse' >其他報表連結</a>&nbsp;";
			
			echo "</div>";	
		}
	
	   $car_no = 0;
      $starthour =  6;
      $endhour = 24;
      $tolertime =0;
      $rs1 = mysql_query("SELECT A1.*,A2 .*	FROM arrangedtable A1, availablecars A2 WHERE A1.carid = A2.車號  AND  A2. date= '".$arrangedate."' AND A2.time = '".$arrangetime."' AND  A1. date = '" .$arrangedate."' AND A1.arrangetime = '".$arrangetime."' GROUP BY A1.`carid` ORDER BY A2.站名 ASC");
      $num_rows = mysql_num_rows($rs1);
	   $TurnoutDaters = mysql_query("SELECT  `TurnoutDate` FROM `availablecars` WHERE  `date`='".$arrangedate."' and `time`='".$arrangetime."' ORDER BY no DESC LIMIT 0, 1");	
	  $TurnoutDatersrow = mysql_fetch_array($TurnoutDaters);
	   $progress = mysql_query("SELECT  `progress` FROM `arrange_log` WHERE  `date`='".$arrangedate."' and `time`='".$arrangetime."'");	
	  $progressrow = mysql_fetch_array($progress);
	  echo ' <h3>車輛數:';
      echo $num_rows;
      echo "  台        出車時間 : 
	             ".$TurnoutDatersrow['TurnoutDate']."        目前進度按鈕:".printstaus($progressrow[0])."<br /> </h3>";
	   ?>		
		<?php    
		echo "<div style=\"width:1080px;height: 500px;\">";
    	echo "<table id=\"table1\" cellpadding=\"0\" cellspacing=\"0\" class=selectable-table>";
        echo "<thead>";
					
						echo "<tr >
								<th colspan=1>車輛編號</th>
								 <th colspan=1 >站名 </th>
								 <th colspan=1 >車種</th>
								 <th colspan=1>工作時段</th>
								 <th colspan=1  >呼號</th>";
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
					 echo "</thead>";
    			   	echo " <tbody>";
			
					 do
					{
						 $car_no++;
						echo "<tr align='center' valign='middle'  style='cursor:default;' ><td>".$car_no."</td><td>"
								.$row['站名']."</td><td class=\"carid\">".$row['cartype']."<input type=\"hidden\" value=\"".$row['carid']."\" name=\"".$row['carid']."\" /></td>";	
						$time = preg_split("/~/",$row['時段']);  
					    $time1 = preg_split("/:/",$time[0]); 
						$smin =$time1[1]+30;
						$shour =$time1[0];
						if($smin>=60)
						{
							$smin=$smin-60;
							$shour++;
						}
						if($smin == 0)
						{
							$smin = "00";
						}
						$time2 = preg_split("/:/",$time[1]);
						$emin =$time2[1]+45;
						$ehour =$time2[0];
						if($emin>=60)
						{
							$emin=$emin-60;
							$ehour++;
						}
						if($emin == 0)
						{
							$emin = "00";
						}
						$startsec=($shour*3600)+($smin*60);
						$endsec=($ehour*3600)+($emin*60);
						$startinterval =floor($startsec/$intervalsec);
						if(($endsec%$intervalsec)>0)
						$endinterval=$endsec/$intervalsec;
						else
						$endinterval =($endsec/$intervalsec)-1;
						echo "<td id=$car_no~$shour:$smin~$ehour:$emin class=\"worktime\">".$shour.":". $smin."~".$ehour.":".$emin."</td><td class=\"cartype\">(".$row['呼號'].")<input type=\"hidden\" value=\"".$row['cartype']."\" name=\"".$row['cartype']."\" /></td>";
            		//陣列初始化
					$resttime=$row['resttime1'];
					$resttime1 = explode(':', $resttime);
				
					for($i = 0; $i < $intervalcount; $i++)
					{
						if($i < $startinterval || $i >$endinterval)
						{
								$carinterval[$i] = "<td>X</td>";
						}
						else if(($i==$resttime1[0])||(($i==$resttime1[1])))
						{
								$carinterval[$i] = "<td>休息</td>";
						}			 				  
						else
						{
								$carinterval[$i] = "<td id=$i.$car_no class=\"empty\" onmousedown=test(this);TableRow.onclick(this,$i);></td>";							
						}
					}
				  for($i = 1; $i < 17; $i++)
				{
                      $fillinterval = $row["run".$i];
                      if($fillinterval != -1)
                      {
                              $information = $row["user".$i];
                              $temp2 = explode('_', $information);
                              if(count($temp2) > 1)
                              {
										$fontcolor="#EE1289";
                                      $hiddenmessage = $i."_1_".$information;
                                      $rs2 = mysql_query("SELECT 抵達時間, 姓名, 帳號, 時段, 上車區域, 上車地址, 下車區域, 下車地址 FROM userrequests WHERE 識別碼 = '".$temp2[0]."' AND arrangedate = '".$arrangedate."' AND arrangetime = '".$arrangetime."'");
                                      $rs3 = mysql_query("SELECT 抵達時間, 姓名, 帳號, 時段, 上車區域, 上車地址, 下車區域, 下車地址 FROM userrequests WHERE 識別碼 = '".$temp2[1]."' AND arrangedate = '".$arrangedate."' AND arrangetime = '".$arrangetime."'");
                                      $rs4 = mysql_query("SELECT *FROM travelinformationofcarsharing WHERE AssignSharing = '".$information."' AND date = '".$arrangedate."' AND arrangetime = '".$arrangetime."'");
                                      if($row4 = mysql_fetch_array($rs4))
                                      {
                                              $sharing = $row4['起點'];
                                              $splitsharing = explode('_', $sharing);
                                              if(strcmp($splitsharing[0], '0') == 0)
                                              {
                                                      if($row2 = mysql_fetch_array($rs2))
                                                      {      
                                                              $carinterval[$fillinterval] = "<td id=".$row['run'.$i].".$car_no onmousedown=test(this);TableRow.onclick(this,$i); class=\"nonempty\"><input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" /><font size=2><font color=\"#8B4726\">共乘:</font> <br /><font color=\"#FF0000\">時間: ".$row2['時段']."</font><br /><font color=\"#0000FF\">上車</font>: <font color=\"#8B4726\">".$row2['姓名']."</font><br /><font color=\"#8B4726\">".$row2['上車區域'].$row2['上車地址']."</font></font>";
                                                              if($row3 = mysql_fetch_array($rs3))
                                                              {
                                                                      $carinterval[$fillinterval].= "<br /><br /><font color=\"#FF0000\">時間: ".$row3['時段']."</font><br /><font color=\"#0000FF\">上車</font>:<font color=\"#8B4726\"> ".$row3['姓名']."</font><br /><font color=\"#8B4726\"> ".$row3['上車區域'].$row3['上車地址']."</font>";
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
                                                                                      $carinterval[$fillinterval].= "<br /><br /><font color=\"#339900\">下車</font>: <font color=\"#8B4726\">".$row3['下車']."</font ><br /><font color=\"#8B4726\">".$row3['下車區域'].$row3['下車地址']."</font><br /><br /><font color=\"#339900\">下車</font>:<font color=\"#8B4726\"> ".$row2['姓名']."</font><br /><font color=\"#8B4726\">".$row2['下車區域'].$row2['下車地址']."</font></td>";
                                                                              }
                                                                              else
                                                                              {
                                                                                      $carinterval[$fillinterval].= "</font></td>";
                                                                                      $j = -1;
                                                                                      for($j = $fillinterval + 1; $j < $tempInterval; $j++)
                                                                                      {
                                                                                              $carinterval[$j] = "<td id=$j.".$row['run'.$i].".$car_no  onmousedown=test(this);TableRow.onclick(this,$i);  class=\"nonempty\"><input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" />-></td>";
                                                                                      }
                                                                                      $carinterval[$j] = "<td id=$j.".$row['run'.$i].".$car_no  onmousedown=test(this);TableRow.onclick(this,$i);  class=\"nonempty\"><input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" /><font size=2><font color=\"#8B4726\">共乘: </font>".$row4['車上乘員']."<br /><br /><font color=\"#339900\">下車</font>: <font color=\"#8B4726\">".$row3['姓名']."</font><br /><font color=\"#8B4726\">".$row3['下車區域'].$row3['下車地址']."</font><br /><br /><font color=\"#339900\">下車</font>:<font color=\"#8B4726\">  ".$row2['姓名']."</font><br /><font color=\"#8B4726\">".$row2['下車區域'].$row2['下車地址']."</font></font></td>";
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
                                                                                      $carinterval[$fillinterval].= "<br /><br /><font color=\"#339900\">下車</font>:<font color=\"#8B4726\"> ".$row2['姓名']."</font><br /><font color=\"#8B4726\">".$row2['下車區域'].$row2['下車地址']."</font><br /><br /><font color=\"#339900\">下車</font>:<font color=\"#8B4726\"> ".$row3['姓名']."</font><br /><font color=\"#8B4726\">".$row3['下車區域'].$row3['下車地址']."</font></td>";
                                                                              }
                                                                              else
                                                                              {
                                                                                      $carinterval[$fillinterval].= "</font></td>";
                                                                                      $j = -1;
                                                                                      for($j = $fillinterval + 1; $j < $tempInterval; $j++)
                                                                                      {
                                                                                              $carinterval[$j] = "<td id=$j.".$row['run'.$i].".$car_no   onmousedown=test(this);TableRow.onclick(this,$i);  class=\"nonempty\"><input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" />-></td>";
                                                                                      }
                                                                                      $carinterval[$j] = "<td id=$j.".$row['run'.$i].".$car_no   onmousedown=test(this);TableRow.onclick(this,$i);  class=\"nonempty\"><input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" /><font size=2><font color=\"#8B4726\">共乘:</font><br /><font color=\"#339900\">下車</font>: <font color=\"#8B4726\">".$row2['姓名']."</font><br /><font color=\"#8B4726\">".$row2['下車區域'].$row2['下車地址']."</font><br /><br /><font color=\"#339900\">下車</font>:<font color=\"#8B4726\"> ".$row3['姓名']."</font><br /><font color=\"#8B4726\">".$row3['下車區域'].$row3['下車地址']."</font></td>";
                                                                                      //$i++;
                                                                              }
                                                                      }
                                                              }
                                                              else
                                                              {
                                                                      echo $temp2[1]."error1!!!!<br />";
                                                              }
                                                      }
                                              }
                                              else
                                              {
                                                      if($row3 = mysql_fetch_array($rs3))
                                                      {       
                                                              $carinterval[$fillinterval] = "<td id=".$row['run'.$i].".$car_no  onmousedown=test(this);TableRow.onclick(this,$i);  class=\"nonempty\"><input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" /><font size=2>共乘: ".$row4['車上乘員']."<br /><br /><font color=\"#FF0000\">時間: ".$row3['時段']."</font><br /><font color=\"#0000FF\">上車</font>: ".$temp2[1]."<br />".$row3['上車區域'].$row3['上車地址'];
                                                              if($row2 = mysql_fetch_array($rs2))
                                                              {
                                                                      $carinterval[$fillinterval].= "<br /><br /><font color=\"#FF0000\">時間: ".$row2['時段']."</font><br /><font color=\"#0000FF\">上車</font>: <font color=\"#8B4726\">".$temp2[0]."</font ><br /><font color=\"#8B4726\">".$row2['上車區域'].$row2['上車地址']."</font>";
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
                                                                                      $carinterval[$fillinterval].= "<br /><br /><font color=\"#339900\">下車</font>: <font color=\"#8B4726\">".$temp2[1]."</font>br / <font color=\"#8B4726\">".$row3['下車區域'].$row3['下車地址']."</font><br /><br /><font color=\"#339900\">下車</font>:<font color=\"#8B4726\"> ".$temp2[0]."</font><br /><font color=\"#8B4726\">".$row2['下車區域'].$row2['下車地址']."</font></td>";
                                                                              }
                                                                              else
                                                                              {
                                                                                      $carinterval[$fillinterval].= "</font></td>";
                                                                                      $j = -1;
                                                                                      for($j = $fillinterval + 1; $j < $tempInterval; $j++)
                                                                                      {
                                                                                              $carinterval[$j] = "<td id=$j.".$row['run'.$i].".$car_no  onmousedown=test(this);TableRow.onclick(this,$i);  class=\"nonempty\"><input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" />-></td>";
                                                                                      }
                                                                                      $carinterval[$j] = "<td id=$j.".$row['run'.$i].".$car_no  onmousedown=test(this);TableRow.onclick(this,$i);  class=\"nonempty\"><input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" /><font size=2>共乘: ".$row4['車上乘員']."<br /><br /><font color=\"#339900\">下車</font>:<font color=\"#8B4726\"> ".$temp2[1]."</font><br /><font color=\"#8B4726\">".$row3['下車區域'].$row3['下車地址']."</font><br /><br /><font color=\"#339900\">下車</font>:<font color=\"#8B4726\"> ".$temp2[0]."</font><br /><font color=\"#8B4726\">".$row2['下車區域'].$row2['下車地址']."</font></td>";
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
                                                                                      $carinterval[$fillinterval].= "<br /><br /><font color=\"#339900\">下車</font>: <font color=\"#8B4726\">".$temp2[0]."</font><br /><font color=\"#8B4726\">".$row2['下車區域'].$row2['下車地址']."</font><br /><br /><font color=\"#339900\">下車</font>:<font color=\"#8B4726\"> ".$temp2[1]."</font><br /><font color=\"#8B4726\">".$row3['下車區域'].$row3['下車地址']."</font></td>";
                                                                              }
                                                                              else
                                                                              {
                                                                                      $carinterval[$fillinterval].= "</font></td>";
                                                                                      $j = -1;
                                                                                      for($j = $fillinterval + 1; $j < $tempInterval; $j++)
                                                                                      {
                                                                                        $carinterval[$j] = "<td id=$j.".$row['run'.$i].".$car_no  onmousedown=test(this);TableRow.onclick(this,$i);  class=\"nonempty\"><input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" />-></td>";
                                                                                      }
                                                                                      $carinterval[$j] = "<td id=$j.".$row['run'.$i].".$car_no  onmousedown=test(this);TableRow.onclick(this,$i);  class=\"nonempty\"><input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" /><font size=2><font color=\"#8B4726\">共乘: ".$row4['車上乘員']."</font><br /><br /><font color=\"#339900\">下車</font>: <font color=\"#8B4726\">".$temp2[0]."</font ><br /><font color=\"#8B4726\">".$row2['下車區域'].$row2['下車地址']."</font><br /><br /><font color=\"#339900\">下車</font>: <font color=\"#8B4726\">".$temp2[1]."<br />".$row3['下車區域'].$row3['下車地址']."</font></font></td>";
                                                                              }
                                                                      }
                                                              }
                                                              else
                                                              {
                                                                      echo $information."error2!!!!<br />";
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
									  if($i!=1)										  
									  	{
									  		$temrs = mysql_query("SELECT 抵達時間, 姓名 ,下車區域, 下車地址 FROM userrequests WHERE 識別碼 = '".$teminformation."' AND arrangedate = '".$arrangedate."' AND arrangetime = '".$arrangetime."'");	
											$temrsrow = mysql_fetch_array($temrs);
											$temendintervel=floor(($temrsrow['抵達時間']+$tolertime)/1800);
										}					 								 
                                      $rs2 = mysql_query("SELECT 抵達時間, 姓名, 帳號,狀態, 時段, 上車區域, 上車地址, 下車區域, 下車地址 FROM userrequests WHERE 識別碼 = '".$information."' AND arrangedate = '".$arrangedate."' AND arrangetime = '".$arrangetime."'");
                                      if($row2 = mysql_fetch_array($rs2))
                                      {
                                              $add = $row2['上車地址'];
                                              $block = $row2['上車區域'];
											  $adapttime = $row2['抵達時間'] + $tolertime;
                                              $tempInterval = (int)($adapttime / $intervalsec);
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
                                              		  if($row2['狀態']=="候補")
													  $fontcolor="#EE1289";
													  else
													  $fontcolor="#000000";   
													     
                                                      $tempadd = $row2['下車地址'];
                                                      $tempblock = $row2['下車區域'];
													  
													  if(($temendintervel==$fillinterval)&&$i!=1)	
													   $carinterval[$fillinterval] = 
													   "<td id=".$row['run'.$i].".$car_no  onmousedown=test(this);TableRow.onclick(this,$i);  class=\"nonempty\">
													   <input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" />
													   <font size=2 color=$fontcolor>".$row2['狀態'].$row2['姓名']."</font><br />
													   <font color=\"#FF0000\">時間: ".$row2['時段']."</font><br />
													   <font color=\"#0000FF\">上車</font>: 
													   <font color=\"$fontcolor\">".$block.$add."<br /><br />
													   <font color=\"#FF0000\">時間: ".$arrivehour.$arrivemin."</font><br />
													   <font color=\"#339900\">下車</font>: 
													   <font color=\"$fontcolor\">".$tempblock.$tempadd."</font></td>";
													  else							  
                                                      {
                                                      	$carinterval[$fillinterval] = 
                                                      	"<td id=".$row['run'.$i].".$car_no  onmousedown=test(this);TableRow.onclick(this,$i);  class=\"nonempty\">
                                                      	<input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" />
                                                      	<font size=2 color=$fontcolor >".$row2['狀態']."-".$row2['姓名']."</font><br />
                                                      	<font color=\"FF0000\">時間: ".$row2['時段']."</font><br />
                                                      	<font color=\"#0000FF\">上車</font>: 
                                                      	<font color=\"$fontcolor\">".$block.$add."<br /><br />
                                                      	<font color=\"#FF0000\">時間: ".$arrivehour.$arrivemin."</font><br />
                                                      	<font color=\"#0000FF\">下車</font>: 
                                                      	<font color=\"$fontcolor\">".$tempblock.$tempadd."</font></td>";
													}
                                              }
                                              else
                                              {
                                              	      if($row2['狀態']=="候補")
													  $fontcolor="#EE1289";
													  else
													  $fontcolor="#000000";   
                                                      $carinterval[$fillinterval] = 
                                                      "<td id=".$row['run'.$i].".$car_no  onmousedown=test(this);TableRow.onclick(this,$i);  class=\"nonempty\">
                                                      <input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" />
                                                      <font size=2 color=\"$fontcolor\">".$row2['狀態']."-".$row2['姓名']."<br />
                                                      <font color=\"#FF0000\">時間: ".$row2['時段']."</font><br />
                                                      <font color=\"#0000FF\">上車</font>:
                                                      <font color=\"$fontcolor\">".$block.$add."</font></td>";
                                                      $j = -1;
                                                      for($j = $fillinterval + 1; $j < $tempInterval; $j++)
                                                      {
                                                       $carinterval[$j] = "<td id=$j.$car_no.".$row['run'.$i]."  onmousedown=test(this);TableRow.onclick(this,$i);  class=\"nonempty\"><input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" />-></td>";
                                                      }
                                                      $tempadd = $row2['下車地址'];
                                                      $tempblock = $row2['下車區域'];
                                                      $carinterval[$j] = 
                                                      "<td id=$j.$car_no.".$row['run'.$i]." onmousedown=test(this);TableRow.onclick(this,$i);  class=\"nonempty\">
                                                      <input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" />
                                                      <font size=2 color=\"$fontcolor\">".$row2['狀態']."-".$row2['姓名']."<br />
                                                      <font color=\"#FF0000\">時間: ".$arrivehour.$arrivemin."</font><br />
                                                      <font color=\"#0000FF\">下車</font>:
                                                      <font color=\"$fontcolor\">".$tempblock.$tempadd."</font></td>";
                                              }
                                      }
 							  $teminformation=$information;
                              }
                      }
              }
              for($i = $offset; $i < $stopinterval; $i++)
              {
                      echo $carinterval[$i];
              }				
				 echo "</tr>";
				}while($row = mysql_fetch_array($rs1));
					
					
				echo " </tbody>";
    			echo " </table>";
				
    
    		echo "<div class=\"clear\"></div>";
    	echo "</div>";
         ?>
	  <div id="dialog-form1" title="排入跨區候補資料" >       
        <form id="neworder" name="neworder">
          <fieldset>
            <legend style="color:red">所有欄位是必須的</legend>
            <table>
              <tr>
                <td class="shortcol"><label for="account1">帳號</label><input type="text" name="account1" id="account1" class=
                " shortcol ui-corner-all ui-widget-content" /></td>   
				<td><label for="date1">排班時間</label><select name="date1" id="date1">
                  <option value="-1">
                    請選擇
                  </option>
                   <?
				   $log= mysql_query("SELECT * FROM arrange_log ORDER BY no DESC");
				    while($logrow = mysql_fetch_array($log))
                   {
				   echo "<option value='".$logrow['date']."_".$logrow['time']."'>";
				   echo  $logrow['date']." ".$logrow['time'];
				    echo "</option>";
				   }
				   ?>
                </select></td>	
        </tr>
            </table>
			 <table>
              <tr>
                <td><label for="carid">車牌</label><input type="text" name="carid" id="carid" readonly="readonly" style=
                "width: 60px" /></td>
              </tr>
            </table>
            <table>
              <tr>
                <td class="shortcol"><label for="startarea1">上車區域</label><input id="startarea1" name="startarea1" type="text" class=
                " longcol ui-corner-all ui-widget-content" /></td>
                <td class="longcol"><label for="startadd1">上車地址</label><input id="startadd1" name="startadd1" type="text" class=
                " longcol ui-corner-all ui-widget-content" /></td>			
              </tr>

              <tr>
                <td class="shortcol"><label for="endarea1">下車區域</label><input id="endarea1" name="endarea1" type="text" class=
                " longcol ui-corner-all ui-widget-content" /></td>

                <td class="longcol"><label for="endadd1">下車地址</label><input id="endadd1" name="endadd1" type="text" class=
                " longcol ui-corner-all ui-widget-content" /></td>			
              </tr>
            </table>
          </fieldset>
        </form>
    
        </div>
		
		<div id="dialog-form2" title="新增空趟" >       
        <form id="neworder" name="neworder">
          <fieldset>
            <legend style="color:red">所有欄位是必須的</legend>           
			 <table>
              <tr>
                <td><label for="carid">車牌</label><input type="text" name="carid" id="carid" readonly="readonly" style=
                "width: 60px" /></td>
              </tr>
            </table>
            <table>
              <tr>
                <td class="shortcol"><label for="TimeLength">時間長度</label><input id="TimeLength" name="TimeLength" type="text" class=
                " longcol ui-corner-all ui-widget-content" /></td>
              </tr>
			   <tr>
                <td><label for="Meeting">是否為會議趟</label><select id="Meeting" name="Meeting">
                  <option value="0">
                    否
                  </option>
                  <option value="1">
                    是
                  </option>
                </select></td>
             </tr>
            </table>
          </fieldset>
        </form>
    
        </div>
	<div id="dialog-form3" title="修改工作時段" >       
        <form id="neworder" name="neworder">
          <fieldset>
            <legend style="color:red">所有欄位是必須的</legend>           
			 <table>
              <tr>
                <td><label for="carid">車牌</label><input type="text" name="carid" id="carid" readonly="readonly" style=
                "width: 60px" /></td>
              </tr>
            </table>
            <table>
              <tr>
                <td class="shortcol">
				<label for="TimeLength">工作時段</label>
				<input id="starthr" name="starthr" type="text" 
				class="longcol ui-corner-all ui-widget-content" />:
				<input id="startmin" name="startmin" type="text" 
				class="longcol ui-corner-all ui-widget-content" />~
						<input id="endhr" name="endhr" type="text" 
				class="longcol ui-corner-all ui-widget-content" />:
				<input id="endmin" name="endmin" type="text" 
				class="longcol ui-corner-all ui-widget-content" />
				</td>
              </tr>
            </table>
          </fieldset>
        </form>    
        </div>	
.	
      <div id="dialog-form" title="新增預約資料" >       
        <form id="neworder" name="neworder">
          <fieldset>
            <legend style="color:red">所有欄位是必須的</legend>

            <table>
              <tr>
                <td class="shortcol"><label for="status">狀態</label><select class="shortcol" id="status" name="statusselect">
                  <option value="1">
                    正常
                  </option>

                  <option value="0">
                    候補
                  </option>
                </select></td>

                <td class="longcol"><label for="name">姓名</label><input type="text" name="name" id="name" class=
                " longcol ui-corner-all" /></td>
              </tr>

              <tr>
                <td class="shortcol"><label for="account">帳號</label><input type="text" name="account" id="account" class=
                " shortcol ui-corner-all ui-widget-content" /></td>

                <td class="longcol"><label for="Disabilities">障別</label><input type="text" name="Disabilities" id="Disabilities" value="" class=
                " longcol ui-corner-all ui-widget-content" /></td>
              </tr>
            </table>

            <table>
              <tr>
                <td><label for="timeselect">預約 時</label><input id="timeselect" style="width:50px;" name="timeselect" type="text" class=
                "ui-widget-content ui-corner-all" /></td>
                <td><label for="minute">分</label><input id="minute" style="width:50px;" name="minute" type="text" class=
                "ui-widget-content ui-corner-all" /></td>

                <td><label for="carid">車牌</label><input type="text" name="carid" id="carid" readonly="readonly" style=
                "width: 60px" /></td>

                <td><label for="cartype">車種</label><input type="text" id="cartype" name="cartype" readonly="readonly" style=
                "width: 50px" /></td>

                <td><label for="traveltime">交通時間(秒)</label><input id="traveltime" name="traveltime" type="text" class=
                "ui-corner-all ui-widget-content" style="width: 70px;" /></td>
              </tr>
            </table>

            <table>
              <tr>
                <td class="shortcol"><label for="startarea">上車區域</label><input id="startarea" name="startarea" type="text" class=
                " longcol ui-corner-all ui-widget-content" /></td>
                <td class="longcol"><label for="startadd">上車地址</label><input id="startadd" name="startadd" type="text" class=
                " longcol ui-corner-all ui-widget-content" /></td>
				 <td class="longcol"><label for="startRemark">上車備註</label><input id="startRemark" name="startRemark" type="text" class=
                " longcol ui-corner-all ui-widget-content" /></td>
              </tr>

              <tr>
                <td class="shortcol"><label for="endarea">下車區域</label><input id="endarea" name="endarea" type="text" class=
                " longcol ui-corner-all ui-widget-content" /></td>

                <td class="longcol"><label for="endadd">下車地址</label><input id="endadd" name="endadd" type="text" class=
                " longcol ui-corner-all ui-widget-content" /></td>
				 <td class="longcol"><label for="endRemark">下車備註</label><input id="endRemark" name="endRemark" type="text" class=
                " longcol ui-corner-all ui-widget-content" /></td>
              </tr>

              <tr>
                <td><label for="share">共乘</label><select id="shareselect" name="shareselect">
                  <option value="0">
                    否
                  </option>

                  <option value="1">
                    是
                  </option>
                </select></td>
              </tr>
            </table>
          </fieldset>
        </form>
    
        </div>	
         
          <div id="insertreqdialog" class="web_dialog" >
        <table style="width: 100%; border: 0px;" cellpadding="3" cellspacing="0">
            <tbody><tr>
                <td class="web_dialog_title">選擇新增資料</td>
                <!---<td class="web_dialog_title align_right">
                    <a href="#" id="btnClose">Close</a>
                </td>--->
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td colspan="2" style="padding-left: 15px;">
                    <b>選擇新增資料 </b>
                </td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td colspan="2" style="padding-left: 15px;">
                    <div id="Relaystationbrands">
						<input id="Relaystation1" name="Relaystation" type="radio" value="1"> 選擇中繼站
						<input id="Relaystation1" name="Relaystation" type="radio" checked="checked"  value="2"> 查詢時段預約
                        <input id="Relaystation2" name="Relaystation" type="radio" value="3"> 手動排入
						<input id="Relaystation3" name="Relaystation" type="radio" value="4"> 跨中心支援候補新增   			
						<input id="Relaystation4" name="Relaystation" type="radio" value="5"> 插入會議趟次  						
                    </div>
                </td>
            </tr>
            <tr>              
            </tr>
            <tr>
                <td colspan="2" style="text-align: center;">
                    <input id="insertSubmit" type="button" class='btn btn-success' value="確定">
                </td>
            </tr>
        </tbody></table>
    </div> 
    
        <div id="dialog" class="web_dialog" >
        <table style="width: 100%; border: 0px;" cellpadding="3" cellspacing="0">
            <tbody><tr>
                <td class="web_dialog_title">選擇刪除或插入共乘</td>
                <!---<td class="web_dialog_title align_right">
                    <a href="#" id="btnClose">Close</a>
                </td>--->
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td colspan="2" style="padding-left: 15px;">
                    <b>請選擇刪除或插入共乘 </b>
                </td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td colspan="2" style="padding-left: 15px;">
                    <div id="brands">
                        <input id="brand1" name="brand" type="radio" checked="checked" value="刪除班次"> 刪除班次
                        <input id="brand2" name="brand" type="radio" value="共乘"> 指定共乘               
                    </div>
                </td>
            </tr>
            <tr>              
            </tr>
            <tr>
                <td colspan="2" style="text-align: center;">
                    <input id="btnSubmit" type="button" class='btn btn-success' value="確定">
                </td>
            </tr>
        </tbody></table>
    </div> 
    </div>
	
	 <center>
    <img src="images/logo1.png" />
     </center>
     <p align="center"><font color="#FFC125">建議解析度 1024x768 以上觀看</font></p>
	 </div >
	 </div>
	  
  </div>
 
</body>
</html>
