<?php
	include("Mydbconnect.php");
	include("connectInfo.php");
	$option = $_GET["option"];
	$carid = $_GET["carid"];	
	$arrangedate = "";
	$arrangetime = "";
	set_time_limit(0);
	$arrangedate =$_GET["date"];
	$arrangetime =$_GET["time"];
	
	$car_no = 0;
	$rs1 = mysql_query("SELECT * FROM arrangedtable WHERE date = '".$arrangedate."' AND carid='$carid' AND arrangetime = '".$arrangetime."' ORDER BY no");  
	$num_rows = mysql_num_rows($rs1);   
	$carclass = mysql_query("SELECT 站名,班別,drivername FROM availablecars WHERE date = '".$arrangedate."' AND 車號='$carid' AND time = '".$arrangetime."' ORDER BY no");  
    $carclassrow = mysql_fetch_array($carclass);
	$row = mysql_fetch_array($rs1);		
	for($i=1;$i<9;$i++)  
	{
		$information = $row["user".$i];
		$temp2 = explode('_', $information);
		if($information!=''&&$information!=' ')
		{
			if(count($temp2) > 1)
			{
					
			}
			else
			{
			$rs2 = mysql_query("SELECT * FROM userrequests WHERE 識別碼 = '$information' AND arrangedate = '".$arrangedate."' AND arrangetime = '".$arrangetime."'");
			$row1 = mysql_fetch_array($rs2);
			$time = $row1['時段'];
			$hour = substr($time, 0, 2);
            $min  = substr($time, 2, 2);
			$timesec = $hour * 3600 + $min *60;
			$traveltime = $row1['抵達時間'] - $timesec;
			$traveltimemin = (int)($traveltime / 60);
            $traveltimesec = $traveltime % 60;
			$porint[$i-1][0]=$row1['sLat'];
			$porint[$i-1][1]=$row1['sLon'];
			$traveltime1[$i-1][0]=$traveltimemin;
			$traveltime1[$i-1][1]=$traveltimesec;
			$countpoint=$i;
			}
		}
	 }
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>復康巴士第一營運中心排班系統 </title> 	
<link rel="stylesheet" href="http://code.jquery.com/ui/1.9.0/themes/base/jquery-ui.css" />
<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false&language=zh-TW"></script> 
<script type="text/javascript" src="http://code.jquery.com/jquery-1.8.2.js"></script>
<style type="text/css">
		html { height: 100% }
		body { height: 100%; margin: 0px; padding: 0px }	   
		#map_canvas { height: 100% }
		#msg{
		font-size:2em;
		color:SaddleBrown;	                    				 
			 }		
</style>
 <script type="text/javascript">
	
	var start;
	var count = '<?php echo $countpoint;?>';	
	var waypoints = new Array(16);
	var traveltime=new Array;	
	 for (var i = 0; i <= count; i++)	
	 		traveltime[i]=new Array;
	var start=new google.maps.LatLng("<?echo $porint[0][0]?>","<?echo $porint[0][1]?>");
	traveltime[0][0]="<?echo $traveltime1[0][0]?>";
	traveltime[0][1]="<?echo $traveltime1[0][1]?>";
	var count1=0;
	<?
	for ($i = 1; $i < $countpoint-1; $i++)		
		{
		?>
		waypoints[count1]=new google.maps.LatLng("<?echo $porint[$i][0]?>","<?echo $porint[$i][1]?>");
		traveltime[count1+1][0]="<?echo $traveltime1[$i][0]?>";
		traveltime[count1+1][1]="<?echo $traveltime1[$i][1]?>";	
		count1++;
		<?
		}
	?>
	var directionsService = new google.maps.DirectionsService();	
    var map;
	var end=new google.maps.LatLng("<?echo $porint[$countpoint-1][0]?>","<?echo $porint[$countpoint-1][1]?>");	
	traveltime[count1+2][0]="<?echo $traveltime1[$countpoint-1][0]?>";
	traveltime[count1+2][1]="<?echo $traveltime1[$countpoint-1][1]?>";		
/************************
   google map 初始化
************************/
function initialize() 
	{
        //規畫路徑呈現選項   
        var markerOptions1=new google.maps.Marker({
            visible: false//取消google原生marker
        })   
		var rendererOptions = 
		{
			strokeColor :"#000000",//路徑顏色
			strokeOpacity: 1.0,
			suppressMarkers: true
        };
 
        directionsDisplay = new google.maps.DirectionsRenderer({polylineOptions: rendererOptions,markerOptions:markerOptions1});
        var startPoint = new google.maps.LatLng(25.05848, 121.554879);			
        var myOptions = {
                zoom: 16,
                mapTypeId: google.maps.MapTypeId.ROADMAP,            
                center: start
                
        }
        
        map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);  
		marked(start,'頭班:'+traveltime[0][0]+"分"+traveltime[0][1]+"秒");	
		calcRoute();		
		marked(end,'尾班:'+traveltime[count1+2][0]+"分"+traveltime[count1+2][1]+"秒");	 
		directionsDisplay.setMap(map);
	   
	  
	}
/************************
      路徑規劃
************************/

function calcRoute()
	{	
	
		if (!waypoints) return;
        //經過地點
        var waypts = [];
		for (var i = 0; i <count1; i++)
		{
		 marked(waypoints[i],"第"+(i+2).toString()+"趟:"+traveltime[i+1][0]+"分"+traveltime[i+1][1]+"秒");			
			waypts.push
			({
				location: waypoints[i],
				stopover: true
			});
        } 
        //規畫路徑請求
        var request = 
		{
			origin: start,
			destination: end,
			waypoints: waypts,
			travelMode: google.maps.DirectionsTravelMode.DRIVING
        };
         
        directionsService.route(request, function(response, status)
		{
			//規畫路徑回傳結果
			if (status == google.maps.DirectionsStatus.OK)
			{				
				directionsDisplay.setDirections(response);
			}
        });
	}
/************************
      Marker
************************/

function marked(addr,markstr)
	{
		var marker = new google.maps.Marker({
                position: addr, //經緯度
                title: markstr, //顯示文字
                icon: createMarkerIcon(markstr, {
                    bgColor:"orangered"
                }),
                map: map //指定要放置的地圖對象
            });
	}
/************************
新增在地圖上指定座標描點
************************/
function createMarker(lat, Lng,contentString) 
	{
		var MapLatLng=new google.maps.LatLng(lat,Lng);
		var marker = new google.maps.Marker
		({
			position: MapLatLng, 
			map: map,
			clickable : true						
		});
		markersArray.push(marker);
		google.maps.event.addListener(marker, 'click', function() 
		{
			infowindow.setContent(contentString); 
			infowindow.open(map,marker);
		});
		//勾選後立即顯示其訊息
		google.maps.event.trigger(marker,"click");
	}
				
/************************
刪除在地圖上指定座標描點
************************/
function DeleteMarker(CheckVal)
	{
		for(var i=0;i<markersArray.length;i++)
			{
			//將所取消選取的座標與存在於地圖內陣列座標做分析
				if($.trim(markersArray[i]["position"])==$.trim(CheckVal))
					{
						markersArray[i].setMap(null);
						markersArray[i].length=0;
					}
			}
	}

/************************
createMarkerIcon
************************/
function createMarkerIcon(text, opt)
	{
		//定義預設參數
		var defaultOptions = 
		{
			fontStyle: "normal", //normal, bold, italic
			fontName: "Arial",
			fontSize: 12, //以Pixel為單位
			bgColor: "darkblue",
			fgColor: "white",
			padding: 4,
			arrowHeight: 6 //下方尖角高度
		};
		options = $.extend(defaultOptions, opt);
		//建立Canvas
		var canvas = document.createElement("canvas"),
		context = canvas.getContext("2d");
		//評估文字尺寸
		var font = options.fontStyle + " " + options.fontSize + "px " + 
					options.fontName;
		context.font = font;
		var metrics = context.measureText(text);
		//文字大小加上padding作為外部尺寸
		var w = metrics.width + options.padding * 2;
		//高度以Font的大小為準
		var h = options.fontSize + options.padding * 2 +
				options.arrowHeight;
		canvas.width = w;
		canvas.height = h;
		//邊框及背景
		context.beginPath();
		context.rect(0, 0, w, h - options.arrowHeight);
		context.fillStyle = options.bgColor;
		context.fill();
		//畫出下方尖角
		context.beginPath();
		var x = w / 2, y = h, arwSz = options.arrowHeight;
		context.moveTo(x, y);
		context.lineTo(x - arwSz, y - arwSz);
		context.lineTo(x + arwSz, y - arwSz);
		context.lineTo(x, y);
		context.fill();
		//印出文字
		context.textAlign = "center";
		context.fillStyle = options.fgColor;
		context.font = font;
		context.fillText(text,	w / 2,(h - options.arrowHeight) / 2 + options.padding);
		//傳回DataURI字串
		return canvas.toDataURL();
	}
	</script>
  </head>

	<body onload="initialize();">	
   <div id ='msg'><font face="標楷體">
  <? echo $carid."旅行地圖";	?>
   </font>
   </div>
		<div id="map_canvas" style="width:100%; height:94%"></div>
	</body>
</html>
