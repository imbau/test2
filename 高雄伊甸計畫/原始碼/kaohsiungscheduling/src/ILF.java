
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class ILF {
	private ResultSet rs = null;
	public int LastNumber;
	private List<TravelTimeStruct> traveltime = null;
	GoogleMapsAPI gmsapi = null;	
	private Connection con;
	private Statement smt,smt1;
	private defineVariable Variable;
	private Map<String,templatlon> templatlon= null;
	public ILF(Connection conn,defineVariable variable)
	{
		Variable=variable;
		con = conn;
		try
		{
			smt = con.createStatement();				
			smt1= con.createStatement();	
			//修正的經緯度
			rs = smt.executeQuery("SELECT * FROM `templatlon` WHERE 1");
			templatlon = new HashMap<String, templatlon>();	
			while(rs.next())
			{		
				templatlon TempNode= new templatlon();
				TempNode.No = rs.getInt("no");
				TempNode.Address = rs.getString("address").trim();
				TempNode.Latitude = Double.valueOf(rs.getString("latitude"));
				TempNode.Longitude = Double.valueOf(rs.getString("longitude"));	
				templatlon.put(TempNode.Address,TempNode);			
			}
			
			//旅行時間歷史紀錄
			rs = smt.executeQuery("SELECT * FROM traveltime");
			traveltime = new ArrayList<TravelTimeStruct>();			
			while(rs.next())
			{			
				TravelTimeStruct tempNode = new TravelTimeStruct();
				tempNode.No = rs.getInt("識別碼");
				tempNode.StartAddress = rs.getString("上車地址").trim();
				tempNode.StartLon = Double.valueOf(rs.getString("上車地址經度"));
				tempNode.StartLat = Double.valueOf(rs.getString("上車地址緯度"));					
				tempNode.EndAddress = rs.getString("下車地址").trim();
				tempNode.EndLon = Double.valueOf(rs.getString("下車地址經度"));
				tempNode.EndLat = Double.valueOf(rs.getString("下車地址緯度"));
				tempNode.OriginTravelTime = rs.getInt("原始交通時間");				
				traveltime.add(tempNode);
			}
			LastNumber = traveltime.size();
			System.out.println("旅行時間資料數量: "+ LastNumber);
			rs.close();
			rs=null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		gmsapi =  new GoogleMapsAPI(Variable);					//Google maps api object
	
	}
	//釋放記憶體
	public void Freememory() throws InterruptedException, IOException
	{
		traveltime=null;
		System.gc();		
	}
	//找尋歷史資料，輸入參數為input: 起訖點[緯度][經度][緯度][經度]，XY:起訖點[X][Y][X][Y]，起訖點[地址][地址]，time:時間(單位為秒)
	public int SearchHistory(double[] input, String[] address, int time) throws InterruptedException, IOException
	{
		double[] StartMinMaxLatLon=new double[4];
		double[] EndMinMaxLatLon=new double[4];
		address[0]=address[0].trim();
		address[1]=address[1].trim();	
		//回傳結果
		int result = -1;
		//起點終點地址相同，回傳旅行時間0
		if(address[0].equals(address[1]))
		{
			return 0;
		}
		//取得起點200公尺最大與最小經緯度
		StartMinMaxLatLon=GetAround(input[0],input[1],150);
		//取得終點200公尺最大與最小經緯度
		EndMinMaxLatLon=GetAround(input[2],input[3],150);
		for(int i = 0; i < LastNumber; i++)
		{
			//判斷是否符合範圍
			if(CheckAccordAround(traveltime.get(i),StartMinMaxLatLon,EndMinMaxLatLon))
			{	if(traveltime.get(i).OriginTravelTime >= 0)
				{
					//取得可用旅行時間
					result = traveltime.get(i).OriginTravelTime;	
					return result;
				}
				else
				{
					result=SearchGooglemapapi(address,input,traveltime.get(i).No);
				    return result;
				}
			}else
			{   //如果經緯度找不到回歸字串比對
				if((traveltime.get(i).StartAddress.trim().equals(address[0].trim())&& traveltime.get(i).EndAddress.trim().equals(address[1].trim())) || (traveltime.get(i).StartAddress.trim().equals(address[1].trim()) && traveltime.get(i).EndAddress.trim().equals(address[0].trim())))
				{
					if(traveltime.get(i).OriginTravelTime >= 0)
					{
						//取得可用旅行時間
						result = traveltime.get(i).OriginTravelTime;	
						return result;
					}
				}
			}
		}
		//歷史資料找不到找google
		if(result >= 0)
		{
			return result;
		}
		else
		{
			result=SearchGooglemapapi(address,input,-1);
			return result;
		}
	}
	//找尋歷史經緯度
	public double[]  SearchLatLonHistory(String address) 
	{
		 double[] ReturnValue = {-1.0, -1.0};
		//查詢歷史經緯度訊息		
		if(templatlon.get(address.trim())!=null)
		{
			templatlon TempNode= new templatlon();
			TempNode=templatlon.get(address.trim());
			ReturnValue[0]  = TempNode.Latitude;
			ReturnValue[1]  =TempNode.Longitude;				
		}
		else
		{
			//查詢不到歷史經緯度就去查google		
			ReturnValue=gmsapi.GeocodingAPI(address.trim());
			if(ReturnValue[0]!=-1||ReturnValue[1]!=-1)
				AddTempLatLon(address.trim(),ReturnValue);
		}
		return ReturnValue;
	}
	//找尋歷史資料，參數為時間字串，起點地址字串，終點地址字串
	public int SearchHistory(RequestTable reqtable,defineVariable variable) throws Exception 
	{
		boolean isfound = false;
		int result = -1;
	    double[] ReturnValue = {-1.0, -1.0};
	    int traveltimeindex=-1;
		try
		{
			//起始點經緯度
			ReturnValue=SearchLatLonHistory(reqtable.OriginAddress.trim());
			//取出經緯度
			reqtable.OriginLat = ReturnValue[0];
			reqtable.OriginLon = ReturnValue[1];		
			if(reqtable.OriginLat ==-1||reqtable.OriginLon ==-1)
				variable.errorcode=-17;
			}catch(Exception e)
			{
				variable.errorcode=-17;
				//e.printStackTrace();
			}
		//起始點經緯度
		try
		{
			ReturnValue=SearchLatLonHistory(reqtable.DestinationAddress.trim());
			reqtable.DestinationLat = ReturnValue[0];
			reqtable.DestinationLon =ReturnValue[1];	
			if(reqtable.DestinationLat ==-1||reqtable.DestinationLon ==-1)
				variable.errorcode=-17;
		}catch(Exception e)
		{
			variable.errorcode=-17;
			//e.printStackTrace();
		}
		for(int i = 0 ; i < LastNumber; i++)
		{			
			if((traveltime.get(i).StartAddress.trim().equals(reqtable.OriginAddress.trim())&& traveltime.get(i).EndAddress.trim().equals(reqtable.DestinationAddress.trim())) || (traveltime.get(i).StartAddress.trim().equals(reqtable.DestinationAddress.trim()) && traveltime.get(i).EndAddress.trim().equals(reqtable.OriginAddress.trim())))
			{
				//資料庫內的交通時間是可用的
				if(traveltime.get(i).OriginTravelTime >= 0)
				{
					reqtable.TravelTime = traveltime.get(i).OriginTravelTime;
					reqtable.DestinationTime = reqtable.TravelTime + reqtable.OriginTime;
					isfound = true;	//停止找尋
					traveltimeindex= traveltime.get(i).No;
					break;
				}
				else
				{
					String[] travelTimeResult = gmsapi.DirectionAPI(reqtable.OriginAddress, reqtable.DestinationAddress);
					if(travelTimeResult[0].equals("success"))
					{
						reqtable.TravelTime = Integer.valueOf(travelTimeResult[5]);
						reqtable.DestinationTime = reqtable.TravelTime + reqtable.OriginTime;
						try
						{
							smt.executeUpdate("update traveltime set 原始交通時間 = " + travelTimeResult[5] + ", 修正交通時間 = " + reqtable.TravelTime + " where 識別碼 = " + traveltime.get(i).No);
							traveltimeindex= traveltime.get(i).No;
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
						isfound = true;//停止搜尋
						break;
					}
					else
					{
						int errorcode=0;
						switch(travelTimeResult[1])
						{
						//已超過今日配額
						case "OVER_QUERY_LIMIT":
						{
							//在執行一次判斷是否是真的配額用完
							//先延遲1秒
							Thread.sleep(1000);
							travelTimeResult = gmsapi.DirectionAPI(reqtable.OriginAddress, reqtable.DestinationAddress);
							if(travelTimeResult[0].equals("success"))
							{
								reqtable.TravelTime = Integer.valueOf(travelTimeResult[5]);								
								reqtable.DestinationTime = reqtable.TravelTime + reqtable.OriginTime;
								try
								{
									smt.executeUpdate("update traveltime set 原始交通時間 = " + travelTimeResult[5] + ", 修正交通時間 = " + reqtable.TravelTime + " where 識別碼 = " + traveltime.get(i).No);
									traveltimeindex= traveltime.get(i).No;
								}
								catch(Exception e)
								{
									e.printStackTrace();
								}
								isfound = true;//停止搜尋
								break;
							}else
							{
								//如果還是OVER_QUERY_LIMIT就是配額用完
								errorcode=-2;
							}
						}
						break;
						//要求已遭拒絕
						case "REQUEST_DENIED":
							errorcode=-3;
							break;
						//不存在的address
						case "ZERO_RESULTS":
							errorcode=-4;
							break;
						//查詢(address或latlng)遺失了
						case "INVALID_REQUEST":
							errorcode=-5;
							break;	
						}		
						if(errorcode<-2)
						{
							reqtable.TravelTime = errorcode;//travelTimeResult[0];//dti.TimeInterval(hour, min, travelTimeResult[0]);
							reqtable.DestinationTime =errorcode;//reqtable.TravelTime + reqtable.OriginTime;
						}
						isfound = true;//停止搜尋
						break;
					}
				}
			}			
		}
		if(!isfound)
		{
			if(result == -1||(reqtable.OriginLat==-1.0))
			{
				String[] travelTimeResult = gmsapi.DirectionAPI(reqtable.OriginAddress, reqtable.DestinationAddress);
				if(travelTimeResult[0].equals("success"))
				{
					reqtable.TravelTime = Integer.valueOf(travelTimeResult[5]);//dti.TimeInterval(hour, min, travelTimeResult[0]);
					reqtable.DestinationTime = reqtable.TravelTime + reqtable.OriginTime;		
					traveltimeindex=AddEntity(reqtable.TravelTime, reqtable.OriginAddress, reqtable.DestinationAddress, reqtable); ;
				}else
				{
					int errorcode=0;
					switch(travelTimeResult[1])
					{
					//已超過今日配額
					case "OVER_QUERY_LIMIT":
					{
						//在執行一次判斷是否是真的配額用完
						//先延遲1秒
						Thread.sleep(1000);
						travelTimeResult = gmsapi.DirectionAPI(reqtable.OriginAddress, reqtable.DestinationAddress);
						if(travelTimeResult[0].equals("success"))
						{
							reqtable.TravelTime = Integer.valueOf(travelTimeResult[5]);//dti.TimeInterval(hour, min, travelTimeResult[0]);
							reqtable.DestinationTime = reqtable.TravelTime + reqtable.OriginTime;
							traveltimeindex=AddEntity(reqtable.TravelTime, reqtable.OriginAddress, reqtable.DestinationAddress, reqtable);
						}else
						{
							//如果還是OVER_QUERY_LIMIT就是配額用完
							errorcode=-2;
						}
					}
					break;
					//要求已遭拒絕
					case "REQUEST_DENIED":
						errorcode=-3;
						break;
					//不存在的address
					case "ZERO_RESULTS":
						errorcode=-4;
						break;
					//查詢(address或latlng)遺失了
					case "INVALID_REQUEST":
						errorcode=-5;
						break;	
					}		
					if(errorcode<=-2)
					{
						reqtable.TravelTime = errorcode;//travelTimeResult[0];//dti.TimeInterval(hour, min, travelTimeResult[0]);
						reqtable.DestinationTime =errorcode;//reqtable.TravelTime + reqtable.OriginTime;
					}
				}
			}
		}		
		return traveltimeindex;
	}		
	public void AddTempLatLon(String address,double[] latlonvalue)
	{
		templatlon TempNode= new templatlon();
		try
		{	
			//更新到資料庫
			String sqlQuery = "INSERT INTO `templatlon`(`address`, `latitude`, `longitude`) VALUES ('";
			sqlQuery  = sqlQuery +  address.trim()+ "'," + latlonvalue[0] + "," + latlonvalue[1]+ ")";
			smt.executeUpdate(sqlQuery);
			
			//更新traveltime舊的旅行時間
			sqlQuery = "UPDATE `traveltime` SET `上車地址緯度`="+String.valueOf(latlonvalue[0])+",`上車地址經度`="+String.valueOf(latlonvalue[1])+" WHERE `上車地址`='"+address.trim()+"'";
			smt.executeUpdate(sqlQuery);
		
			sqlQuery = "UPDATE `traveltime` SET `下車地址緯度`="+String.valueOf(latlonvalue[0])+",`下車地址經度`="+String.valueOf(latlonvalue[1])+" WHERE `下車地址`='"+address.trim()+"'";
			
			smt.executeUpdate(sqlQuery);
			ResultSet rs = null;
			rs = smt1.executeQuery("SELECT *  FROM templatlon WHERE `address` = '" + address.trim() +"'");
			
			//更新到map記憶體
			if(rs.next())
			{	
				TempNode.No = rs.getInt("no");
			}
			TempNode.Address = address.trim();
			TempNode.Latitude = latlonvalue[0];
			TempNode.Longitude = latlonvalue[1]	;
			templatlon.put(TempNode.Address,TempNode);	
			
		}
		catch(Exception e)
		{
			System.out.println("發生錯誤");
			e.printStackTrace();		
		}	
	}
	
	//排班時的新增旅行資料
	public void AddEntity(int[] inputtraveltime, double[] LatLon, String[] address)
	{
		try
		{
			String sqlQuery = "insert into traveltime (上車地址, 上車地址經度, 上車地址緯度, 下車地址, 下車地址經度, 下車地址緯度, 原始交通時間) values ('";
			sqlQuery  = sqlQuery + address[0] + "','" + String.valueOf(LatLon[1]) + "','" + String.valueOf(LatLon[0]) + "','";
			sqlQuery = sqlQuery + address[1] + "','" + String.valueOf(LatLon[3]) + "','" + String.valueOf(LatLon[2]) + "',";
			sqlQuery = sqlQuery + inputtraveltime[0]+")";			
			smt.executeUpdate(sqlQuery);
			ResultSet rs = null;
			rs = smt.executeQuery("SELECT `識別碼` ,  `上車地址` ,  `上車地址經度` ,  `上車地址緯度` ,  `下車地址` ,  `下車地址經度` ,  `下車地址緯度` ,  `原始交通時間`   FROM traveltime WHERE 上車地址 = '" + address[0] + "' AND 下車地址 = '" + address[1] + "'");
			if(rs.next())
			{		
				TravelTimeStruct tempNode = new TravelTimeStruct();				
				tempNode.No = rs.getInt("識別碼");
				tempNode.StartAddress = rs.getString("上車地址");
				tempNode.StartLon = Double.valueOf(rs.getString("上車地址經度"));
				tempNode.StartLat = Double.valueOf(rs.getString("上車地址緯度"));
				tempNode.EndAddress = rs.getString("下車地址");
				tempNode.EndLon = Double.valueOf(rs.getString("下車地址經度"));
				tempNode.EndLat = Double.valueOf(rs.getString("下車地址緯度"));
				tempNode.OriginTravelTime = rs.getInt("原始交通時間");				
				traveltime.add(tempNode);
				LastNumber++;				
			}
			rs.close();
			rs = null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("1 add data error!!! From " + address[0] + " to " + address[1]);
		}		
	}
	
	//匯入資料庫時的新增旅行資料 
	public int AddEntity(int inputtraveltime, String origin, String destination, RequestTable reqtable)	
	{
		int No=0;
		try
		{
			String sqlQuery = "insert into traveltime (上車地址, 上車地址經度, 上車地址緯度, 下車地址, 下車地址經度, 下車地址緯度, 原始交通時間) values ('";
			sqlQuery  = sqlQuery + reqtable.OriginAddress + "','" + String.valueOf(reqtable.OriginLon) + "','" + String.valueOf(reqtable.OriginLat) + "','";
			sqlQuery = sqlQuery + reqtable.DestinationAddress + "','" + String.valueOf(reqtable.DestinationLon) + "','" + String.valueOf(reqtable.DestinationLat) + "',";
			sqlQuery = sqlQuery + inputtraveltime + ")";
			smt.executeUpdate(sqlQuery);
			ResultSet rs = null;
			rs = smt.executeQuery("SELECT `識別碼` ,  `上車地址` ,  `上車地址經度` ,  `上車地址緯度` ,  `下車地址` ,  `下車地址經度` ,  `下車地址緯度` ,  `原始交通時間`  FROM traveltime WHERE 上車地址 = '" + reqtable.OriginAddress + "' AND 下車地址 = '" + reqtable.DestinationAddress + "'");
			if(rs.next())
			{				
				TravelTimeStruct tempNode = new TravelTimeStruct();
				tempNode.No = rs.getInt("識別碼");
				tempNode.StartAddress = rs.getString("上車地址");
				tempNode.StartLon = Double.valueOf(rs.getString("上車地址經度"));
				tempNode.StartLat = Double.valueOf(rs.getString("上車地址緯度"));
				tempNode.EndAddress = rs.getString("下車地址");
				tempNode.EndLon = Double.valueOf(rs.getString("下車地址經度"));
				tempNode.EndLat = Double.valueOf(rs.getString("下車地址緯度"));
				tempNode.OriginTravelTime = rs.getInt("原始交通時間");			
				traveltime.add(tempNode);
				LastNumber++;
				No=tempNode.No;
			}
			rs.close();
			rs = null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("2 add data error!!! From " + origin + " to " + destination);
		}	
		return No;
	}
	//取得經緯度範圍
	//[最小緯度][最大緯度][最小經度][最大經度]
	public double[] GetAround(double latitude, double longitude, int raidusMile) 
	{
		double degree = (24901 * 1609) / 360.0;
		double dpmLat = 1 / degree;
		double radiusLat = dpmLat * raidusMile;
		double minLat = latitude - radiusLat;//最小緯度
	    double maxLat = latitude + radiusLat;//最大緯度
	    double mpdLng = degree * Math.cos(latitude * (3.14159265 / 180));
	    double dpmLng = 1 / mpdLng;
	    double radiusLng = dpmLng * raidusMile;
	    double minLng = longitude - radiusLng;//最小經度
	    double maxLng = longitude + radiusLng;//最大經度
	    return new double[]{minLat, maxLat,minLng, maxLng};	
	 }
	//判斷是否符合經緯度範圍
	public boolean CheckAccordAround(TravelTimeStruct Node,double[] StartMinMaxLatLon,double[] EndMinMaxLatLon) 
	{
		//緯度是否落在範圍內
		if(Node.StartLat>=StartMinMaxLatLon[0]&&Node.StartLat<=StartMinMaxLatLon[1])
		{
			//判斷經度是否落在範圍
			if(Node.StartLon>=StartMinMaxLatLon[2]&&Node.StartLon<=StartMinMaxLatLon[3])
			{
				//判斷緯度是否落在範圍
				if(Node.EndLat>=EndMinMaxLatLon[0]&&Node.EndLat<=EndMinMaxLatLon[1])
				{
					//判斷經度是否落在範圍
					if(Node.EndLon>=EndMinMaxLatLon[2]&&Node.EndLon<=EndMinMaxLatLon[3])
					{
						return true;
				    }
					else
					{
						return false;
					}
			    }
				else
				{
					return false;
				}
			}
			else
			{
				return false;
			}
		}else if(Node.EndLat>=StartMinMaxLatLon[0]&&Node.EndLat<=StartMinMaxLatLon[1])
		{
			//判斷經度是否落在範圍
			if(Node.EndLon>=StartMinMaxLatLon[2]&&Node.EndLon<=StartMinMaxLatLon[3])
			{
				//判斷緯度是否落在範圍
				if(Node.StartLat>=EndMinMaxLatLon[0]&&Node.StartLat<=EndMinMaxLatLon[1])
				{
					//判斷經度是否落在範圍
					if(Node.StartLon>=EndMinMaxLatLon[2]&&Node.StartLon<=EndMinMaxLatLon[3])
					{
						return true;
					}
					else
					{
						return false;
					}
				}
				else
				{
					return false;
				}
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	public int SearchGooglemapapi(String[] address,double[] input,int traveltimenum) 
	{
		int result=-1;
		//資料庫內存放的旅行時間資料是錯誤的嘗試透過google map取得可用旅行時間
		String[] travelTimeResult;
		try {
			travelTimeResult = gmsapi.DirectionAPI(address[0].trim(), address[1].trim());
			//google map api 成功取回可用旅行時間			
			if(travelTimeResult[0].equals("success"))
			{
				result = Integer.valueOf(travelTimeResult[5]);
				//如果原本資料庫有更新資料庫資料
				if(traveltimenum>=0)
				{
					try
					{
						smt.executeUpdate("update traveltime set 原始交通時間 = " + result +" where 識別碼 = " + traveltimenum);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					//資料庫沒有資料新增一筆
					int[] timeinput = {result, result,-1};
					AddEntity(timeinput, input, address);
				}
				return result;
			}else
			{
				switch(travelTimeResult[1])
				{
					//已超過今日配額
					case "OVER_QUERY_LIMIT":
						//在執行一次判斷是否是真的配額用完
						//先延遲1秒
						Thread.sleep(2000);
						travelTimeResult = gmsapi.DirectionAPI(address[0].trim(), address[1].trim());
						if(travelTimeResult[0].equals("success"))
						{
							if(traveltimenum>=0)
							{
								try
								{
									smt.executeUpdate("update traveltime set 原始交通時間 = " + result +" where 識別碼 = " + traveltimenum);
								}
								catch(Exception e)
								{
									e.printStackTrace();
								}
							}
							else
							{
								//資料庫沒有資料新增一筆
								int[] timeinput = {result, result,-1};
								AddEntity(timeinput, input, address);
							}
							return result;
						}else
						{
							//如果還是OVER_QUERY_LIMIT就是配額用完
							result=-2;
							System.out.println("123"+"OVER_QUERY_LIMIT");
						}
						break;
					//要求已遭拒絕
					case "REQUEST_DENIED":
						result=-3;
						System.out.println("REQUEST_DENIED");
						break;
						//不存在的address
					case "ZERO_RESULTS":
						result=-4;
						System.out.println("ZERO_RESULTS");
						break;
						//查詢(address或latlng)遺失了
					case "INVALID_REQUEST":
						result=-5;
						System.out.println("INVALID_REQUEST");
						break;	
					case "NOT_FOUND":
						result=-1;
						System.out.println("NOT_FOUND");
						break;	
						
				}		
			}
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			SearchGooglemapapi(address,input,traveltimenum);
		
		}		
		return result;
  }
}
