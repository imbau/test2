<?php
include("connectInfo.php");
include("Mydbconnect.php");

$allowedExts = array("xls");
$extention = end(explode(".", $_FILES["rerequestfile"]["name"]));
$reqfile = -1;
$avairecarfile = -1;
echo $_POST['hiddenreorderdate']." ".$_POST['hiddenarrangetime'];
$rs1 = mysql_query("SELECT no FROM userrequests WHERE arrangedate = '".$_POST['hiddenreorderdate']."' AND arrangetime = '".$_POST['hiddenarrangetime']."'");
$num_rows = mysql_num_rows($rs1);
echo $number_rows;

if($_FILES["rerequestfile"]["error"] > 0)
{
        //echo "Return Code: ".$_FILES["rerequestfile"]["error"]."<br/>";
}
else
{
      //  echo "Request Upload: " . $_FILES["reqeustfile"]["name"] . "<br />";
       // echo "Type: " . $_FILES["rerequestfile"]["type"] . "<br />";
      //  echo "Size: " . ($_FILES["rerequestfile"]["size"] / 1024) . " Kb<br />";
       // echo "Temp file: " . $_FILES["rerequestfile"]["tmp_name"] . "<br />";
        
        $reqfile = 1;
        
        if (file_exists("upload/" . $_FILES["rerequestfile"]["name"]))
        {
                
                //echo $_FILES["myfile"]["name"] . " already exists. ";
                //chmod("upload/".$_FILES["recarfile"]["name"], 0777);
                unlink("upload/".$_FILES["rerequestfile"]["name"]);
                move_uploaded_file($_FILES["rerequestfile"]["tmp_name"], "upload/" . $_FILES["rerequestfile"]["name"]);
        }       
        else
        {
                move_uploaded_file($_FILES["rerequestfile"]["tmp_name"], "upload/" . $_FILES["rerequestfile"]["name"]);
                echo "Stored in: " . "upload/" . $_FILES["myfile"]["name"];
        }
       
}

if($_FILES["recarfile"]["error"] > 0)
{
        echo "Return Code: ".$_FILES["recarfile"]["error"]."<br />";
}
else
{
        $avairecarfile = 1;
        
        echo "<br />Car Upload: " . $_FILES["recarfile"]["name"] . "<br />";
        echo "Type: " . $_FILES["recarfile"]["type"] . "<br />";
        echo "Size: " . ($_FILES["recarfile"]["size"] / 1024) . " Kb<br />";
        echo "Temp file: " . $_FILES["recarfile"]["tmp_name"] . "<br />";
        
        if(file_exists("upload/".$_FILES["recarfile"]["name"]))
        {       
                //chmod("upload/".$_FILES["recarfile"]["name"], 0777);
                unlink("upload/".$_FILES["recarfile"]["name"]);
                move_uploaded_file($_FILES["recarfile"]["tmp_name"], "upload/".$_FILES["recarfile"]["name"]);
        }
        else
        {
                move_uploaded_file($_FILES["recarfile"]["tmp_name"], "upload/".$_FILES["recarfile"]["name"]);
        }
}
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta name="generator" content="HTML Tidy for HTML5 (experimental) for Windows https://github.com/w3c/tidy-html5/tree/c63cc39" />
  <meta http-equiv="Content-Type" content="text/html; charset=big5" />

  <title>�i�վA�ʰʺA�ƯZ�t��</title>
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
  <script src="jquery-1.8.1.min.js"> </script>
  <script type="text/javascript" src="jquery.lettering.js"></script>
  <script type="text/javascript" src="menu.js"></script>
  <script type="text/javascript" src="script.js"></script>
  <script src="jquery-ui-1.9.0.custom.js"></script>
  <script type="text/javascript">
  var begin;
  var start = true;
  $(document).ready
  (
        function()
        {
                $("input#arrange_button").click
                (
                        function()
                        {
                                //window.open("fileuploadprogress.php");
                                //alert("123");
                                var mode = <?php echo $_POST['mode']; ?>;
                                if(start)
                                {
                                        start = false;
                                        var arrangetime = '<?php echo $_POST['hiddenarrangetime']; ?>';
                                        var arrangedate = '<?php echo $_POST['hiddenreorderdate']; ?>';
                                        //alert(arrangetime + " " + arrangedate);
                                        begin = window.setInterval(showPercentage, 2000);
                                        if(mode == 1)
                                        {
                                                $.post("<?php echo $linkurlport; ?>/WebRoutingArranger/ReadExcelAddition.view", {rerequestfilename: $("input#rerequestfilename").val(), recarfilename: $("input#recarfilename").val(), arrangetime: arrangetime, arrangedate: arrangedate},function(response)
                                                {
                                                        //alert(response);
                                                        var temp = response.split(" ");
                                                        //alert(temp[0]);
                                                        if(temp[0] == "error")
                                                        {
                                                                window.location="additioninputerror.php?date="+temp[1]+"&time="+temp[2];
                                                        }
                                                        else if(temp[0] == "success")
                                                        {
                                                                window.location="additiontraveltime.php?date="+temp[1]+"&time="+temp[2];
                                                        }

                                                
                                                });
                                        }
                                        else if(mode == 2)
                                        {
                                                var last_number = <?php echo $num_rows; ?>;
                                                $.post("<?php echo $linkurlport; ?>/WebRoutingArranger/ReadExcel.view", {rerequestfilename: $("input#rerequestfilename").val(), recarfilename: $("input#recarfilename").val(), arrangetime: arrangetime, arrangedate: arrangedate, mode: 2, last_number: (last_number+1)},function(response)
                                                {
                                                        //alert(response);
                                                        var temp = response.split(" ");
                                                        //alert(temp[0]);
                                                        if(temp[0] == "error")
                                                        {
                                                                 window.location="additioninputerror.php?date="+temp[1]+"&time="+temp[2];
                                                        }
                                                        else if(temp[0] == "success")
                                                        {
                                                              window.location="additiontraveltime.php?date="+temp[1]+"&time="+temp[2];
                                                        }

                                                
                                                });

                                        }

                                }

                        }
                );
        }
  );

  function showPercentage()
  {
        $.get("progress.php", {option: 3},function(response)
        {
                $("span#databaseprogress").text(response + "%");
        });
        
  }
  $(function() {
                       $("#letter-container h2 a").lettering();
                   });
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

<body>
  <div class="container">
      <div id="letter-container" class="letter-container">
      <div class="page-header">	  
        <img src="images/logo.png" width="80px" />
		
        <h2><a>�i�վA�ʰʺA�ƯZ�t��</a></h2>

        <ul id="menu">
          <li>
         <a href="#" onclick="window.open('map.html','newwindows','height=500, width=700');" >�s���ڭ�</a>	
          </li>
          <li> <a href="#">����ڭ�</a>
		  <ul>
              <li>
                <a href="http://www.ycswf.org.tw/" target="_blank">�|�����|�֧Q����|</a>
              </li>
              <li>
                <a href="http://web.ncku.edu.tw/bin/home.php" target="_blank">���\�j��</a>
              </li>
              <li>
                <a href="http://www.eden.org.tw/" target="_blank">��l���|�֧Q����|</a>
              </li>
              <li>
                <a href="http://www.iii.org.tw/" target="_blank">�굦�|</a>
              </li>
            </ul>
          </li>
          <li>
           		<a href="<?php  echo $linkurlport; ?>/routingarrange/arranger.php">����</a>
          </li>
        </ul>
		
      </div>
    </div>

    <div class="well1">
   
      <h3><input type="button" name="arrange" value="�ɮ׶פJ��Ʈw" id="arrange_button" class="btn btn-primary start" />
      <button type="button" onclick="window.open(&#39;modifyexcel.php&#39;);" name="cartable" class=
      "btn btn-success"><span><font face="�з���">�˵�������</font></span></button></h3><br />

      <p>�פJ��Ʈw�i��: <span id="databaseprogress">0%</span></p>
      <div id="progressbar"></div>
    </div>
  </div>

  <center>
    <img src="images/logo1.png" />
  </center>

  <p align="center"><font color="#FFC125">��ĳ�ѪR�� 1024x768 �H�W�[��</font></p>
</body>
</html>
