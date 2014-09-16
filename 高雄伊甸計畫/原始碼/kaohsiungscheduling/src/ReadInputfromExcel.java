//用來讀取Input excel並存入資料庫中


import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class ReadInputfromExcel
{
	private Workbook book;
	private Sheet sheet;
	private Connection con = null;
	private String date = null;
	private String time = null;
	private defineVariable Variable;
	public ReadInputfromExcel(defineVariable variable)
	{
		con = variable.con;
		date=variable.date;
		time =variable.time;
		Variable=variable;
	}
	
		
	public Cell[] ReadTable(int row)
	{
		Cell[] data = sheet.getRow(row);				//read whole row data
		return data;															//return data

	}
	
	public void inicartable()								//初始化排班表
	{
		LinkedList<DriverTable> OriginDriverTable =new LinkedList<DriverTable>();
		double TimeUnit =0.5;
		int RestTimeInterval =-5;
		OriginDriverTable =buildDriverTable(TimeUnit, RestTimeInterval);	
		for(int i = 0; i < OriginDriverTable.size(); i++)
		{		
			OriginDriverTable.get(i).PrintNode(con,date, time, "arrangedtable");
		}
		
	}
	//讀取request預約資料表的excel檔，並將他寫入資料庫中，資料表名稱為"預約總表"
	public RequestTable[] ReadOrderTable()
	{
		
		try
		{
			Statement smt = con.createStatement();
			ResultSet rs = null;
			rs = smt.executeQuery("SELECT * FROM userrequests WHERE arrangedate = '" + date + "' AND arrangetime = '" + time + "'");
			rs.last();
			int count = rs.getRow();					
			RequestTable[] reqtable = new RequestTable[count];
			int i = 0;
			rs.first();
			do
			{
				reqtable[i] = buildRequestNode(rs);
				reqtable[i].Number =Integer.valueOf(rs.getString("識別碼"));
				i++;
			}while(rs.next());
			smt.close();
			return reqtable;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	//將必要資訊填入
	public RequestTable buildRequestNode(ResultSet rs)
	{
		//String TempAddress = null;
		int hour = 0, min = 0;
		String time;
		RequestTable reqNode = new RequestTable();
		//ILF smartSearch = new ILF(con);
		try
		{
			//候補與否
			if(rs.getString("狀態").equals("候補"))
			{
				reqNode.Status = 2;
			}
			else
			{
				reqNode.Status = 1;
			}
			//共乘資訊
			if(rs.getString("共乘意願").equals("否"))
			{
				reqNode.Share = false;
			}
			else
			{
				reqNode.Share = true;
			}	
			//上車地址
			reqNode.OriginAddress = rs.getString("上車區域") + rs.getString("上車地址");
			//下車地址
			reqNode.DestinationAddress = rs.getString("下車區域") + rs.getString("下車地址");
			//帳號
			reqNode.RequestNumber = rs.getString("帳號");
			//預約時間，起點經度緯度XY座標，終點經度緯度XY座標，抵達時間，旅行時間資訊
			//smartSearch.SearchHistory(data[9].getContents(), data[10].getContents() + data[11].getContents(), data[12].getContents() + data[13].getContents(), reqNode);
			reqNode.OriginLat = rs.getDouble("sLat");
			reqNode.OriginLon = rs.getDouble("sLon");
			reqNode.DestinationLat = rs.getDouble("eLat");
			reqNode.DestinationLon = rs.getDouble("eLon");
			time = rs.getString("時段");
			hour = Integer.valueOf(time.substring(0, 2));	//hour = XX
			min = Integer.valueOf(time.substring(2, 4));		//min = YY
			reqNode.OriginTime = hour * 3600 + min * 60;		//起點時間
			reqNode.DestinationTime = rs.getInt("抵達時間");
			if(reqNode.DestinationTime == -1)
			{
				reqNode.TravelTime = -1;
			}
			else
			{
				reqNode.TravelTime = reqNode.DestinationTime - reqNode.OriginTime;
			}
			
			reqNode.Car = rs.getString("車種");
			return reqNode;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	//計算資料數量，並回傳數值
	public int DataCount(String tableName)								//get data count
	{
		try
		{
			book = Workbook.getWorkbook(new File(tableName));
			sheet = book.getSheet(0);
		}
		catch(Exception ex)
		{
			System.out.print("Open xls file error!!\n");
			System.out.print(ex);
		}
		int rows = 0; 
		rows = sheet.getRows();	//取得資料筆數
		return rows;
	}
	public DriverTable buildDriverNode(double interval, int RestTimeInterval,ResultSet rs)
	{
		DriverTable Drivernode= new DriverTable((int)(24/interval));
		ResultSet rs2 = null;
		Statement smt = null, smt2 = null;
		try
		{
			smt = con.createStatement();
			smt2 = con.createStatement();
			//建立所有車輛的linkedlist
			int nodeNumber = 0;		
			rs.first();
			String address = rs.getString("address");
			//初始化TimeInterval
			String[] tempTime = rs.getString("worktime").split("~");
			InitialInterval(Drivernode, tempTime[0], tempTime[1], interval, RestTimeInterval);
			//填入車輛識別
			Drivernode.ID = rs.getString("carid");
			Drivernode.CallNum = rs.getString("callnumber");			
			Drivernode.NodeNumber = nodeNumber++;
			Drivernode.Car = rs.getString("cartype");
			Drivernode.Address = address;
			Drivernode.RestTime = RestTimeInterval;
			rs2 = smt.executeQuery("select *from carbarn where 車場地址 = '" + address + "'");
			if(rs2.next())
			{
				Drivernode.Lat = Double.valueOf(rs2.getString("Lat"));
				Drivernode.Lon = Double.valueOf(rs2.getString("Lon"));
				Drivernode.X = Integer.valueOf(rs2.getString("X"));
				Drivernode.Y = Integer.valueOf(rs2.getString("Y"));					
			}
			else
			{
				String temp = "insert into carbarn (車場地址, Lat, Lon, X, Y,場名) values ('";
				GoogleMapsAPI gmAPI = new GoogleMapsAPI(Variable);
				double[] result = gmAPI.GeocodingAPI(address);
				Drivernode.Y = (int)((result[0] - 24) * 110754.8256 + 2655032.3);			//coordinate transform
				Drivernode.X = (int)((result[1] - 121) * 101745.445 + 250000);
				Drivernode.Lat = result[0];
				Drivernode.Lon = result[1];
				temp = temp + address + "','" + String.valueOf(Drivernode.Lat) + "','" + String.valueOf(Drivernode.Lon) + "','" + String.valueOf(Drivernode.X) + "','" + String.valueOf(Drivernode.Y) + "','"+" "+"')";
				smt2.executeUpdate(temp);
			}
			
			rs.close();
		}
		catch(Exception e)
		{
			
			return null;
		}
		return Drivernode;
		
	}
	//從資料庫讀取司機車輛資料
	public LinkedList<DriverTable> buildDriverTable(double interval, int RestTimeInterval)
	{
		LinkedList<DriverTable> driverTable = new LinkedList<DriverTable>();
		ResultSet rs = null, rs2 = null;
		Statement smt = null, smt2 = null;
		try
		{
			
			smt = con.createStatement();
			smt2 = con.createStatement();
			//從資料庫query 可用車輛表
			rs = smt.executeQuery("select 地址, 車種, 時段, 車號 from availablecars WHERE  date='"+date+"' and time='"+time+"' ORDER BY `no` ASC");
			//建立所有車輛的linkedlist
			int nodeNumber = 0;
			
			while(rs.next())
			{
				DriverTable node = new DriverTable((int)(24/interval));
				String address = rs.getString("地址");
				//初始化TimeInterval
				
				String[] tempTime = rs.getString("時段").split("~");
				InitialInterval(node, tempTime[0], tempTime[1], interval, RestTimeInterval);
				
				//填入車輛識別
				node.ID = rs.getString("車號");			
				node.NodeNumber = nodeNumber++;
				node.Car = rs.getString("車種");
				node.Address = address;
				node.RestTime = RestTimeInterval;
				
				rs2 = smt2.executeQuery("select *from carbarn where 車場地址 = '" + address + "'");
				if(rs2.next())
				{
					//System.out.println("find result");
					node.Lat = Double.valueOf(rs2.getString("Lat"));
					node.Lon = Double.valueOf(rs2.getString("Lon"));
					node.X = Integer.valueOf(rs2.getString("X"));
					node.Y = Integer.valueOf(rs2.getString("Y"));
					
				}
				else
				{
					String temp = "insert into carbarn (車場地址, Lat, Lon, X, Y,場名) values ('";
					//System.out.println(j++);
					GoogleMapsAPI gmAPI = new GoogleMapsAPI(Variable);
					double[] result = gmAPI.GeocodingAPI(address);
					node.Y = (int)((result[0] - 24) * 110754.8256 + 2655032.3);			//coordinate transform
					node.X = (int)((result[1] - 121) * 101745.445 + 250000);
					node.Lat = result[0];
					node.Lon = result[1];
					temp = temp + address + "','" + String.valueOf(node.Lat) + "','" + String.valueOf(node.Lon) + "','" + String.valueOf(node.X) + "','" + String.valueOf(node.Y) + "','"+" "+"')";
					//System.out.println(temp);
					smt2.executeUpdate(temp);
					//Thread.sleep(100);
					
				}
				//rs2.close();
				driverTable.add(node);
			}
			rs.close();
			return driverTable;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		
	}
	
	public void InitialInterval(DriverTable node, String startString, String endString, double interval, int restInterval)
	{
		int startInterval, endInterval;
		double intervalSec;
		int startSec, endSec;
		intervalSec = interval * 3600;
		
		for(int i = 0; i < node.TimeInterval.length; i++)
		{
			//-2代表該時段不上班
			node.TimeInterval[i] = "不上班";
		}
		//處理上班時段
		String[] startTime = startString.split(":");
		String[] endTime = endString.split(":");
		startSec = (Integer.valueOf(startTime[0]) * 3600 + Integer.valueOf(startTime[1]) * 60);
		endSec =  (Integer.valueOf(endTime[0]) * 3600 + Integer.valueOf(endTime[1]) * 60);
		
		node.StartTime = startSec;
		node.EndTime = endSec;
		
		startInterval = (int)(startSec / intervalSec)+1;
		endInterval = ((endSec % intervalSec)  > 0.0 ? (int)(endSec / intervalSec) : (int)(endSec / intervalSec)-1);
		for(int i = startInterval; i <= endInterval; i++)
		{
			//上班時間但尚未排班
			node.TimeInterval[i] = "未排班";
		}
		/*
		//處理休息時間
		for(int i = startInterval + (int)(restInterval/interval); i < startInterval + (int)(restInterval/interval) + (int)(1/interval); i++)
		{
			node.TimeInterval[i] = "不上班";
		}*/
	}

	
	public void reset()
	{
		book.close();
	}
	
}
