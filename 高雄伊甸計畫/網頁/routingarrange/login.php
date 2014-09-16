<?php
       include("Mydbconnect.php");
       include("connectInfo.php");	   
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>登入</title>
<script type="text/javascript" src="js/jquery.js"></script>
<script type="text/javascript" src="js/bootstrap.js"></script>
<script type="text/javascript" src="js/dynamic-menu.js"></script>
 
<link href="css/dynamic-menu.css" rel="stylesheet" type="text/css" />
<link href="css/bootstrap.css" rel="stylesheet" type="text/css" />
<link href="css/kyict.css" rel="stylesheet" type="text/css" />
<style type="text/css">
     
</style>
</head>

<body>
<div id="content">
 <br/><br/> <br/><br/> <br/><br/> <br/><br/> <br/><br/>
 <div class="login-box" style="margin:0 auto 0 auto">
  <h4 align="center">請登入</h4>
   <br/>
  <form action="connect.php" method="post" class="form-horizontal" style="margin-left: -70px">
  <div class="control-group">
    <label class="control-label" for="inputEmail"><strong>帳號</strong></label>
    <div class="controls">
      <input name="identity" type="text" id="identity" >
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="inputPassword"><strong>密碼</strong></label>
    <div class="controls">
      <input name="password" type="password" id="password" >
    </div>
  </div>
  <div class="control-group">
    <div class="controls">
      <label class="checkbox">
       <!---- <input name="remember" id="remember" type="checkbox"> 保持登入狀態
        <a href="yy">忘記密碼</a>--->
      </label>
       <br/>
      <button type="submit" class="btn btn-warning">登入</button>
    </div>
   </div>
  </form>
 </div>
</div><!-- /content -->
</body>
</html>
