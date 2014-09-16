//�Ψ�Ū��Input excel�æs�J��Ʈw��


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
	
	public void inicartable()								//��l�ƱƯZ��
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
	//Ū��request�w����ƪ�excel�ɡA�ñN�L�g�J��Ʈw���A��ƪ�W�٬�"�w���`��"
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
				reqtable[i].Number =Integer.valueOf(rs.getString("�ѧO�X"));
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
	
	//�N���n��T��J
	public RequestTable buildRequestNode(ResultSet rs)
	{
		//String TempAddress = null;
		int hour = 0, min = 0;
		String time;
		RequestTable reqNode = new RequestTable();
		//ILF smartSearch = new ILF(con);
		try
		{
			//�ԸɻP�_
			if(rs.getString("���A").equals("�Ը�"))
			{
				reqNode.Status = 2;
			}
			else
			{
				reqNode.Status = 1;
			}
			//�@����T
			if(rs.getString("�@���N�@").equals("�_"))
			{
				reqNode.Share = false;
			}
			else
			{
				reqNode.Share = true;
			}	
			//�W���a�}
			reqNode.OriginAddress = rs.getString("�W���ϰ�") + rs.getString("�W���a�}");
			//�U���a�}
			reqNode.DestinationAddress = rs.getString("�U���ϰ�") + rs.getString("�U���a�}");
			//�b��
			reqNode.RequestNumber = rs.getString("�b��");
			//�w���ɶ��A�_�I�g�׽n��XY�y�СA���I�g�׽n��XY�y�СA��F�ɶ��A�Ȧ�ɶ���T
			//smartSearch.SearchHistory(data[9].getContents(), data[10].getContents() + data[11].getContents(), data[12].getContents() + data[13].getContents(), reqNode);
			reqNode.OriginLat = rs.getDouble("sLat");
			reqNode.OriginLon = rs.getDouble("sLon");
			reqNode.DestinationLat = rs.getDouble("eLat");
			reqNode.DestinationLon = rs.getDouble("eLon");
			time = rs.getString("�ɬq");
			hour = Integer.valueOf(time.substring(0, 2));	//hour = XX
			min = Integer.valueOf(time.substring(2, 4));		//min = YY
			reqNode.OriginTime = hour * 3600 + min * 60;		//�_�I�ɶ�
			reqNode.DestinationTime = rs.getInt("��F�ɶ�");
			if(reqNode.DestinationTime == -1)
			{
				reqNode.TravelTime = -1;
			}
			else
			{
				reqNode.TravelTime = reqNode.DestinationTime - reqNode.OriginTime;
			}
			
			reqNode.Car = rs.getString("����");
			return reqNode;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	//�p���Ƽƶq�A�æ^�Ǽƭ�
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
		rows = sheet.getRows();	//���o��Ƶ���
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
			//�إߩҦ�������linkedlist
			int nodeNumber = 0;		
			rs.first();
			String address = rs.getString("address");
			//��l��TimeInterval
			String[] tempTime = rs.getString("worktime").split("~");
			InitialInterval(Drivernode, tempTime[0], tempTime[1], interval, RestTimeInterval);
			//��J�����ѧO
			Drivernode.ID = rs.getString("carid");
			Drivernode.CallNum = rs.getString("callnumber");			
			Drivernode.NodeNumber = nodeNumber++;
			Drivernode.Car = rs.getString("cartype");
			Drivernode.Address = address;
			Drivernode.RestTime = RestTimeInterval;
			rs2 = smt.executeQuery("select *from carbarn where �����a�} = '" + address + "'");
			if(rs2.next())
			{
				Drivernode.Lat = Double.valueOf(rs2.getString("Lat"));
				Drivernode.Lon = Double.valueOf(rs2.getString("Lon"));
				Drivernode.X = Integer.valueOf(rs2.getString("X"));
				Drivernode.Y = Integer.valueOf(rs2.getString("Y"));					
			}
			else
			{
				String temp = "insert into carbarn (�����a�}, Lat, Lon, X, Y,���W) values ('";
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
	//�q��ƮwŪ���q���������
	public LinkedList<DriverTable> buildDriverTable(double interval, int RestTimeInterval)
	{
		LinkedList<DriverTable> driverTable = new LinkedList<DriverTable>();
		ResultSet rs = null, rs2 = null;
		Statement smt = null, smt2 = null;
		try
		{
			
			smt = con.createStatement();
			smt2 = con.createStatement();
			//�q��Ʈwquery �i�Ψ�����
			rs = smt.executeQuery("select �a�}, ����, �ɬq, ���� from availablecars WHERE  date='"+date+"' and time='"+time+"' ORDER BY `no` ASC");
			//�إߩҦ�������linkedlist
			int nodeNumber = 0;
			
			while(rs.next())
			{
				DriverTable node = new DriverTable((int)(24/interval));
				String address = rs.getString("�a�}");
				//��l��TimeInterval
				
				String[] tempTime = rs.getString("�ɬq").split("~");
				InitialInterval(node, tempTime[0], tempTime[1], interval, RestTimeInterval);
				
				//��J�����ѧO
				node.ID = rs.getString("����");			
				node.NodeNumber = nodeNumber++;
				node.Car = rs.getString("����");
				node.Address = address;
				node.RestTime = RestTimeInterval;
				
				rs2 = smt2.executeQuery("select *from carbarn where �����a�} = '" + address + "'");
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
					String temp = "insert into carbarn (�����a�}, Lat, Lon, X, Y,���W) values ('";
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
			//-2�N��Ӯɬq���W�Z
			node.TimeInterval[i] = "���W�Z";
		}
		//�B�z�W�Z�ɬq
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
			//�W�Z�ɶ����|���ƯZ
			node.TimeInterval[i] = "���ƯZ";
		}
		/*
		//�B�z�𮧮ɶ�
		for(int i = startInterval + (int)(restInterval/interval); i < startInterval + (int)(restInterval/interval) + (int)(1/interval); i++)
		{
			node.TimeInterval[i] = "���W�Z";
		}*/
	}

	
	public void reset()
	{
		book.close();
	}
	
}
