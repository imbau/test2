import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class DriverTable implements Cloneable
{
	public String ID;
	public int NodeNumber;
	public int StartTime;
	public int EndTime;
	public int X;
	public int Y;
	public double Lat;
	public double Lon;
	public double RestTime;
	public String RestTime1;
	public String[] TimeInterval;
	public String Car;
	public String Address;
	public String CallNum;
	public String TurnoutDate;
	public String writingerror="0";
	public int ArrangedCount;
	//public int IdleCount;
	public boolean changeRest;
	public int restInterval;
	public boolean StartArrange;//2013/1/11 增加判斷是否已排入頭班
	public boolean EndArrange;//2013/1/11 增加判斷是否已排入晚班
	public String station;//出場站點
	public int startreqtime;//頭班預約者時間
	int endreqtime;//尾班預約者時間
	public int carsize;
	public boolean Arrangedflag;
	public boolean Greedyflag;
	public boolean  PreMark=false;//事先標記有排入預約者
	public int earaWeight;
	public int halfworktime;
	public int []index={-1,-1,-1};	
	public List<String> temprelaxarry;//紀錄已處理過剩下可候選休息的區間，但要最後minfilter選取到才會寫回司機
	public List<String> relaxarry;//紀錄可候選休息的區間
	public int StartDistanceValue;//紀錄上一個乘客下車地點到當前預約者上車地點的旅行時間
	public int EndDistanceValue;//紀錄當前乘客下車地點到下一個預約者上車地點的旅行時間
	public int PreviousrequstTime;
	public int NextrequstTime;	
	public int Holiday;	//紀錄是否為假日 0為非假日 1為假日
	public int  HalfWorkTimeInterval=0;//紀錄車輛的中間時段的區間
	public int  StartTimeInterval=0;//紀錄工時起始區間
	public int  EndTimeInterval=0;//紀錄工時結束區間
	public DriverTable(int intervalCount)
	{
		StartTime = -1;
		EndTime = -1;
		X = -1;
		Y = -1;
		RestTime1 ="未選定";
		ArrangedCount = 0;
		Lat = -1;
		Lon = -1;
		ID = null;
		NodeNumber = -1;
		TimeInterval = new String[intervalCount];
		Car = null;
		Address = null;
		changeRest = false;
		restInterval = -1;
		StartArrange=false;//2013/1/11 增加判斷判斷是否已排頭班初始直
		EndArrange=false;//2013/1/11 增加判斷判斷是否已排尾班初始直
		Arrangedflag=true;
		carsize=0;
		Greedyflag=false;//判斷是否有延後15分上班如果有不允許提前收班
		earaWeight=2;	
		//IdleCount=-1;		
		relaxarry=new ArrayList<String>(12);//初始化可候選休息array
		temprelaxarry=new ArrayList<String>(relaxarry);	
		StartDistanceValue=-1;
		EndDistanceValue=-1;
		PreviousrequstTime=-1;
		NextrequstTime=-1;
		startreqtime=-1;//頭班預約者時間
		endreqtime=-1;//尾班預約者時間
		CallNum=" ";
	    TurnoutDate="";
	    Holiday=0;
	}
	public  String GetTurnoutDate()
	{
		return TurnoutDate;
	}
	public  int GetHoliday()
	{
		return Holiday;
	}
	public  int insertDrivertable(defineVariable Variable,String carFileName) throws Exception
	{
		String carInsertSQL = "insert into availablecars (站名, 呼號,telephone,drivername,車號, 班別, 車種, 時段, 地址, 場名,date,time,TurnoutDate) values ('";
		LinkInfo linkinfo = new LinkInfo();		
	    Workbook book;
		Sheet sheet;
		book = Workbook.getWorkbook(new File(linkinfo.getUploadLink() + carFileName));
		int sheetsNum = book.getNumberOfSheets();
		int index=-1;
		if(sheetsNum==1)
		{
			sheet = book.getSheet(0);
			int count = sheet.getRows();	//取得資料筆數
			String temp;
			Cell[] data;	
			String[] splitarray ;		
			for(int i = 0; i < count; i++)
			{
				data =  sheet.getRow(i);				//read whole row data
				index=i;
				try
				{
					//如果0行沒有資料視為最後一筆
					if(data[0].getContents().equals(""))
						break;
				}
				catch(Exception e)
				{
					break;
				}			
				temp = "";		
				//形成insert一筆request資料進資料庫內的SQL語法
				temp += carInsertSQL;
				for(int j = 1; j < 10; j++)
				{
					//檢查必要資訊是否有缺少填寫
					try
					{
						temp += data[j].getContents().trim() + "','";
						if(data[j].getContents().equals(""))
							Variable.CheckErrorCode(j);	
							
						
						
					}catch(Exception e)
					{
						//如果不符合格式就中斷
						Variable.CheckErrorCode(j);	
					}
					 
				}
				try
				{
					if(data[8].getContents().contains("~"))
					{
						splitarray=data[8].getContents().split("~");
						if(!(splitarray[0].contains(":")&&splitarray[1].contains(":")))
							Variable.errorcode=-14;
					//不符合時間格式
					if(checkworktime(splitarray[0],Variable))
						Variable.errorcode=-14;
					}
					else
					{	
						Variable.errorcode=-14;
					}
					
				}catch(Exception e)
				{
					Variable.errorcode=-14;
				}
				try
				{
					if(!data[11].getContents().equals(""))
						TurnoutDate=data[11].getContents().trim();
					else
					{
						Variable.errorcode=-12;
					}
				}catch(Exception e)
				{
					Variable.errorcode=-12;
				}
				try
				{
					if(!data[12].getContents().equals(""))
						Holiday=Integer.valueOf(data[12].getContents().trim());
					else
					{
						Variable.errorcode=-13;
					}
				}catch(Exception e)
				{
					Variable.errorcode=-13;
				}
				temp += data[10].getContents().trim()+"','" +Variable.date+"','"+Variable.time+ "','"+data[11].getContents().trim()+"')";	
				if(Variable.errorcode<=-9)
					break;
				try
				{
				 Variable.smt.executeUpdate(temp);
				}
				catch(SQLException e)
				{
					if(e.toString().indexOf("TurnoutDate")!=-1)				
						Variable.errorcode=-15;
					else if(e.toString().indexOf("時段")!=-1)	
						Variable.errorcode=-14;
						
				}
			}
	 }
	else
	{
		Variable.errorcode=-16;
	}
	  return index+1;
	}
	public  DriverTable readsingleDrivertable(Connection con, String arrangedate, String arrangetime,Statement smt, String carid,defineVariable Variable) throws Exception
	{
		ResultSet rs,rs2,rs3;
		Statement smt2=null;	
		int holiday=0;
		rs = smt.executeQuery("SELECT A1.*,A2 .* 	FROM arrangedtable A1, availablecars A2 WHERE A1.carid ='"+carid+"' AND A2.車號='"+carid+"' AND A2. date = '"+arrangedate +"' AND A2.time= '" +arrangetime+"' AND A1. date = '" + arrangedate + "' AND A1.arrangetime = '" + arrangetime + "' ORDER BY A1.worktime ASC");
		rs.first();
		double timeinterval =rs.getDouble("timeinterval");
		int IntervalSec = (int)(timeinterval * 3600);
		int intervalCount = (int)(24 /  timeinterval);
		rs3=Variable.smt2.executeQuery("SELECT * FROM `arrange_log` WHERE date ='"+Variable.date +"' AND time='" +Variable.time+"'");
		if(rs3.next())
			holiday=rs3.getInt("holiday");		
		DriverTable DriverNode = new DriverTable(intervalCount);
		do
		{		
			DriverNode.ID = rs.getString("carid").trim();											
			DriverNode.Car = rs.getString("cartype");
			DriverNode.Address = rs.getString("地址").trim();
			DriverNode.CallNum= rs.getString("呼號").trim();
			DriverNode.RestTime1 = rs.getString("resttime1").trim();		
			DriverNode.station=rs.getString("站名").trim();				
			DriverNode.InitialInterval(DriverNode,rs.getString("時段").trim(), timeinterval,Variable);	
			DriverNode.Holiday=holiday;
			smt2 = con.createStatement();
			//回復車輛狀態				
			for(int i = 1; i < 17; i++)
			{
				//讀取班次資料並回復Node內的timeinterval
				if(rs.getInt("run" + String.valueOf(i)) != -1&&rs.getInt("run" + String.valueOf(i)) != 0)
				{  
					String information = rs.getString("user"+ String.valueOf(i));
					String[] testnumber = information.split("_");
				
						int informationNum = Integer.valueOf(testnumber[0]);	
						rs2 = smt2.executeQuery("SELECT 抵達時間, 時段,下車區域 FROM userrequests WHERE arrangedate = '" + arrangedate + "' AND arrangetime = '" +arrangetime + "' AND 識別碼 ='" + informationNum+"'");
						if(rs2.next())
						{
							String temptime = rs2.getString("時段");
							int temphour = Integer.valueOf(temptime.substring(0, 2));	//hour = XX
							int tempmin = Integer.valueOf(temptime.substring(2, 4));		//min = YY
							int tempstartsec = temphour * 3600 + tempmin * 60;		//起點時間
							int tempendsec =rs2.getInt("抵達時間");
							int tempStartInterval = (int)(tempstartsec / IntervalSec);
							int tempEndInterval = ((tempendsec % IntervalSec)  >= 0.0 ? (int)(tempendsec / IntervalSec) : (int)(tempendsec / IntervalSec) - 1);
							for(int j = tempStartInterval; j <= tempEndInterval; j++)
							{
								DriverNode.TimeInterval[j] = information;								
							}
							DriverNode.ArrangedCount++;
							if(i==1&&informationNum!=0)
							{
								DriverNode.StartArrange=true;
								DriverNode.startreqtime=tempstartsec;	
						    }
							if(i==2&&informationNum!=0)
							{
								DriverNode.EndArrange=true;
							}
							DriverNode.endreqtime=tempstartsec;	
						}
						else
						{
							System.out.println("ERROR!!");
						}
				}
			}
		}while(rs.next());		
		return DriverNode;
		
	}
	
	public void clearNode(Connection conn, String arrangedate, String arrangetime, double restTime) throws IOException
	{
		
		int[] run = new int[16];
		Statement smt = null;	
		String insertSQL = "UPDATE arrangedtable SET ";
		String constrain = " WHERE date = '" + arrangedate + "' AND arrangetime = '" + arrangetime + "' AND carid = '" + ID + "'";
		insertSQL+="resttime="+restTime+",";		
		//run
		for(int i = 0; i < run.length - 1; i++)
		{
			insertSQL = insertSQL + "run" + String.valueOf(i+1) + "=-1" + ", user" + String.valueOf(i+1) + "=''"  + ", ";			
		}
		insertSQL = insertSQL + "run16=-1, user16='' ";			
		insertSQL += constrain;

		try
		{
			smt = conn.createStatement();		
			smt.executeUpdate(insertSQL);
			smt.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	public  int  getcarsize() 
	{
		return carsize;
	}
	
	public  LinkedList<DriverTable> readDrivertable(Connection con, String arrangedate, String arrangetime,Statement smt,defineVariable Variable,LinkedList<DriverTable> car)
	{
		ResultSet rs,rs2;
		Statement smt2=null,smt3=null;
	    int[] flag=new int[2];//判斷檢查衝趟的時段是否達到終止
		try {
			rs = smt.executeQuery("SELECT A1.*,A2 .* 	FROM arrangedtable A1, availablecars A2 WHERE A1.carid = A2.車號 AND A2. date = '"+arrangedate +"' AND A2.time= '" +arrangetime+"' AND A1. date = '" + arrangedate + "' AND A1.arrangetime = '" + arrangetime + "' GROUP BY A1.`carid` ORDER BY A1.worktime ASC");
			rs.first();
			double timeinterval =rs.getDouble("timeinterval");
			int IntervalSec = (int)(timeinterval * 3600);
			int intervalCount = (int)(24 /  timeinterval);
			do
			{	
				DriverTable DriverNode = new DriverTable(intervalCount);
				DriverNode.ID = rs.getString("carid").trim();											
				DriverNode.Car = rs.getString("cartype");			
				DriverNode.Address = rs.getString("地址").trim();
				DriverNode.RestTime1 = rs.getString("resttime1").trim();				
				DriverNode.station=rs.getString("站名").trim();	
				DriverNode.CallNum=rs.getString("呼號").trim();	
				try 
				{
				
					DriverNode.InitialInterval(DriverNode,rs.getString("時段").trim(), timeinterval,Variable);
					if(DriverNode.CallNum.indexOf("50")!=-1)
						System.out.println(DriverNode.CallNum);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				
				smt2 = con.createStatement();
				smt3= con.createStatement();
				//回復車輛狀態				
				for(int i = 1; i < 17; i++)
				{
					flag[0]=0;
					flag[1]=0;
					//讀取班次資料並回復Node內的timeinterval
					if(rs.getInt("run" + String.valueOf(i)) != -1&&rs.getInt("run" + String.valueOf(i)) != 0)
					{  
						String information = rs.getString("user"+ String.valueOf(i));
						String[] testnumber = information.split("_");
						int informationNum = Integer.valueOf(testnumber[0]);
						rs2 = smt2.executeQuery("SELECT * FROM userrequests WHERE arrangedate = '" + arrangedate + "' AND arrangetime = '" +arrangetime + "' AND 識別碼 ='" + informationNum+"'");
					 	if(rs2.next())
						{
							String temptime = rs2.getString("時段");
							int temphour = Integer.valueOf(temptime.substring(0, 2));	//hour = XX
							int tempmin = Integer.valueOf(temptime.substring(2, 4));		//min = YY
							int tempstartsec = temphour * 3600 + tempmin * 60;		//起點時間
							int tempendsec =rs2.getInt("抵達時間");
							int tempStartInterval = (int)(tempstartsec / IntervalSec);
							int tempEndInterval = (int)(tempendsec / IntervalSec);
							for(int j = tempStartInterval; j <= tempEndInterval; j++)
							{
								if(DriverNode.TimeInterval[j].indexOf("不上班")!=-1||DriverNode.TimeInterval[j].indexOf("未排班")!=-1)
									DriverNode.TimeInterval[j] = information;	
								else
								{	
									String[] reqnumber = DriverNode.TimeInterval[j].split("_");
									smt3.executeUpdate("UPDATE userrequests SET `arranged` = -2 , `Targetdrivers`='null' WHERE `識別碼` ="+informationNum+" and `arrangedate`='"+arrangedate+"' and `arrangetime`='"+arrangetime+"'");
									smt3.executeUpdate("UPDATE userrequests SET `arranged` = -2 , `Targetdrivers`='null' WHERE `識別碼` ="+reqnumber[0]+" and `arrangedate`='"+arrangedate+"' and `arrangetime`='"+arrangetime+"'");
									smt3.executeUpdate("INSERT INTO writingerror (date,time,reqindex,carcallnum) VALUES ('"+arrangedate+"','"+arrangetime+"','"+informationNum+"','"+DriverNode.CallNum+"')");								
									smt3.executeUpdate("INSERT INTO writingerror (date,time,reqindex,carcallnum) VALUES ('"+arrangedate+"','"+arrangetime+"','"+reqnumber[0]+"','"+DriverNode.CallNum+"')");
									if(reqnumber.length>2)
									{
										smt3.executeUpdate("INSERT INTO writingerror (date,time,reqindex,carcallnum) VALUES ('"+arrangedate+"','"+arrangetime+"','"+reqnumber[1]+"','"+DriverNode.CallNum+"')");
										smt3.executeUpdate("UPDATE userrequests SET `arranged` = -2 , `Targetdrivers`='null' WHERE `識別碼` ="+reqnumber[1]+" and `arrangedate`='"+arrangedate+"' and `arrangetime`='"+arrangetime+"'");
									}
									DriverNode.writingerror=DriverNode.TimeInterval[j];
									DriverNode.TimeInterval[j]="未排班";
									for(int prvindex = 1; prvindex<= 10; prvindex++)
									{
										if(DriverNode.TimeInterval[j-prvindex]==DriverNode.writingerror)
										{
											DriverNode.TimeInterval[j-prvindex]="未排班";	
											flag[0]=0;
										}				
										else
											flag[0]=1;
										if(DriverNode.TimeInterval[j+prvindex]==DriverNode.writingerror)
										{
											DriverNode.TimeInterval[j+prvindex]="未排班";
											flag[1]=0;
										}
										else
											flag[1]=1;
										if(flag[0]==1&&flag[1]==1)
											break;
									}
									
									break;
								}
							}
							DriverNode.ArrangedCount++;
							if(i==1&&informationNum!=0)
							{
								DriverNode.StartArrange=true;							
							}							
							if(i==2&&informationNum!=0)
							{
								DriverNode.EndArrange=true;
							}
							
							     
						}
						else
						{
							System.out.println("ERROR!!");
						}
					}
				}			
				car.add(DriverNode);
			}while(rs.next());	
		
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return car;
		
	}
	//頭尾班讀取車輛
	public  List<carGroup> readDrivertable(List<carGroup> car,defineVariable Variable) throws Exception
	{
		ResultSet rs,rs2,rs3;
		Statement smt2=null;	
		rs = Variable.smt.executeQuery("SELECT A1.*,A2 .* 	FROM arrangedtable A1, availablecars A2 WHERE A1.carid = A2.車號 AND A2. date = '"+Variable.date +"' AND A2.time= '" +Variable.time+"' AND A1. date = '" + Variable.date + "' AND A1.arrangetime = '" + Variable.time + "' GROUP BY A1.`carid` ORDER BY A1.worktime ASC");
		rs.first();
		double timeinterval =rs.getDouble("timeinterval");
		int IntervalSec = (int)(timeinterval * 3600);
		int intervalCount = (int)(24 /  timeinterval);
		int size=0;
		int holiday=0;
		rs3=Variable.smt2.executeQuery("SELECT * FROM `arrange_log` WHERE date ='"+Variable.date +"' AND time='" +Variable.time+"'");
		if(rs3.next())
			holiday=rs3.getInt("holiday");			
		do
		{	
			boolean[] flag={false,false};
			size++;		
			DriverTable DriverNode = new DriverTable(intervalCount);
			DriverNode.ID = rs.getString("carid").trim();											
			DriverNode.Car = rs.getString("cartype");
			DriverNode.CallNum= rs.getString("呼號").trim();
			DriverNode.Address = rs.getString("地址").trim();
			DriverNode.RestTime1 = rs.getString("resttime1").trim();				
			DriverNode.station=rs.getString("站名").trim();	
			DriverNode.InitialInterval(DriverNode,rs.getString("時段").trim(), timeinterval,Variable);	
			DriverNode.Holiday=holiday;
			smt2 = Variable.con.createStatement();
			//回復車輛狀態				
			for(int i = 1; i < 17; i++)
			{
				//讀取班次資料並回復Node內的timeinterval
				if(rs.getInt("run" + String.valueOf(i)) != -1&&rs.getInt("run" + String.valueOf(i)) != 0)
				{  
					String information = rs.getString("user"+ String.valueOf(i));
					String[] testnumber = information.split("_");
					int informationNum = Integer.valueOf(testnumber[0]);
					rs2 = smt2.executeQuery("SELECT 抵達時間, 時段,下車區域 FROM userrequests WHERE arrangedate = '" + Variable.date + "' AND arrangetime = '" +Variable.time + "' AND 識別碼 ='" + informationNum+"'");
					if(rs2.next())
					{
						String temptime = rs2.getString("時段");
						int temphour = Integer.valueOf(temptime.substring(0, 2));	//hour = XX
						int tempmin = Integer.valueOf(temptime.substring(2, 4));		//min = YY
						int tempstartsec = temphour * 3600 + tempmin * 60;		//起點時間
						int tempendsec =rs2.getInt("抵達時間");
						int tempStartInterval = (int)(tempstartsec / IntervalSec);
						int tempEndInterval = ((tempendsec % IntervalSec)  >= 0.0 ? (int)(tempendsec / IntervalSec) : (int)(tempendsec / IntervalSec) - 1);
						for(int j = tempStartInterval; j <= tempEndInterval; j++)
						{
							
							DriverNode.TimeInterval[j] = information;								
						}
						DriverNode.ArrangedCount++;
					}
					else
					{
						System.out.println("ERROR!!");
					}
				}
			}
			int starttime = DriverNode.StartTime + Variable.tolerableStartTime;//頭班時間
			int endtime =DriverNode.EndTime + Variable.tolerableEndTime;//晚班時間:出勤時間+45分			
			//計算頭班區間
			int StartInterval = (int)(starttime / Variable.IntervalSec);//上車時間在一天中的interval index
			//計算尾班區間
			int EndInterval=((endtime% Variable.IntervalSec)  > 0.0 ? (int)(endtime / Variable.IntervalSec) : (int)(endtime / Variable.IntervalSec)-1);
			//if(DriverNode.Holiday==0)
			//{
				DriverNode.HalfWorkTimeInterval=(StartInterval+EndInterval)/2;
				flag[0]=Variable.Check(StartInterval,DriverNode,0,DriverNode.HalfWorkTimeInterval);
				flag[1]=Variable.Check(EndInterval,DriverNode,1,DriverNode.HalfWorkTimeInterval);
			
				//設定有無頭尾班的旗標
				if(flag[0]==true)
					DriverNode.StartArrange=true;
				if(flag[1]==true)
					DriverNode.EndArrange=true;
			/*}
			else
			{
				//假日頭尾班手動排入
				DriverNode.StartArrange=true;
				DriverNode.EndArrange=true;				
			}*/
			
			car.get(defineVariable.switchareaindex(DriverNode.station)).addCar(DriverNode,(int)(DriverNode.StartTime)/1800);
		}while(rs.next());	
		carsize=size;
		return car;
		
	}
	//讀取車輛表
	public  List<carGroup> readDrivertable(Connection con, String arrangedate, String arrangetime,Statement smt, List<carGroup> car
			,ILF ilf,Map<Integer,RequestTable> IndexMap,defineVariable Variable,List<reqGroup> requestTable) throws Exception
	{
		ResultSet rs,rs3;	
		rs = smt.executeQuery("SELECT A1.*,A2 .* 	FROM arrangedtable A1, availablecars A2 WHERE A1.carid = A2.車號 AND A2. date = '"+arrangedate +"' AND A2.time= '" +arrangetime+"' AND A1. date = '" + arrangedate + "' AND A1.arrangetime = '" + arrangetime + "' GROUP BY A1.`carid` ORDER BY A1.worktime ASC");
		rs.first();
		double timeinterval =rs.getDouble("timeinterval");
		int IntervalSec = (int)(timeinterval * 3600);
		int intervalCount = (int)(24 /  timeinterval);
		int size=0;	
		int holiday=0;
		rs3=Variable.smt2.executeQuery("SELECT * FROM `arrange_log` WHERE date ='"+Variable.date +"' AND time='" +Variable.time+"'");
		if(rs3.next())
			holiday=rs3.getInt("holiday");	
		do
		{	size++;		
		
			DriverTable DriverNode = new DriverTable(intervalCount);
			DriverNode.ID = rs.getString("carid").trim();											
			DriverNode.Car = rs.getString("cartype");
			DriverNode.Address = rs.getString("地址").trim();
			DriverNode.CallNum= rs.getString("呼號").trim();
			DriverNode.RestTime1 = rs.getString("resttime1").trim();			
			DriverNode.station=rs.getString("站名").trim();	
			boolean[] flag={false,false};
			DriverNode.InitialInterval(DriverNode,rs.getString("時段").trim(), timeinterval,Variable);				
			DriverNode.Holiday=holiday;
			//回復車輛狀態				
			for(int i = 1; i < 17; i++)
			{
				//讀取班次資料並回復Node內的timeinterval
				if(rs.getInt("run" + String.valueOf(i)) != -1)
				{  
					String information = rs.getString("user"+ String.valueOf(i));
					String[] testnumber = information.split("_");
					RequestTable tableIndex=new RequestTable();
					if(testnumber.length>1)
					{
					  Variable.Checkreq(requestTable,testnumber[0]);
					  Variable.Checkreq(requestTable,testnumber[1]);
					}
					else if(testnumber.length==1)
					{
						Variable.Checkreq(requestTable,testnumber[0]);
					}
					tableIndex = IndexMap.get(Integer.valueOf(testnumber[0]));
					int tempstartsec =tableIndex.originalStartTime;		//起點時間
					int tempendsec =tableIndex.originalDestinationTime;
					int tempStartInterval = (int)(tempstartsec / IntervalSec);
					int tempEndInterval = ((tempendsec % IntervalSec)  >= 0.0 ? (int)(tempendsec / IntervalSec) : (int)(tempendsec / IntervalSec) - 1);
					for(int j = tempStartInterval; j <= tempEndInterval; j++)
					{
						DriverNode.TimeInterval[j] = information;								
					}
					DriverNode.ArrangedCount++;
					
				}
			}
			int starttime = DriverNode.StartTime + Variable.tolerableStartTime;//頭班時間
			int endtime =DriverNode.EndTime + Variable.tolerableEndTime;//晚班時間:出勤時間+45分	
			//設定預定頭尾班時間
			DriverNode.startreqtime=starttime;
			DriverNode.endreqtime=endtime;
			//計算頭班區間
			int StartInterval = (int)(starttime / Variable.IntervalSec);//上車時間在一天中的interval index
			//計算尾班區間
			int EndInterval=(endtime/Variable.IntervalSec);
			DriverNode.HalfWorkTimeInterval=(StartInterval+EndInterval)/2;
			flag[0]=Variable.Check(StartInterval,DriverNode,0,DriverNode.HalfWorkTimeInterval);
			flag[1]=Variable.Check(EndInterval,DriverNode,1,DriverNode.HalfWorkTimeInterval);
			if(DriverNode.Holiday==0)
			{
				//平日
				//有缺頭尾班就立即中止
				if((flag[0]==false||flag[1]==false))
				{
					Variable.errorcode=-6;
					System.out.println(DriverNode.CallNum+"無頭尾班");
					break;
				}
				
			}			
			//紀錄頭尾班時間		
			if(DriverNode.Holiday==0)
				Variable.SetHeadTailteamTime(StartInterval, EndInterval, DriverNode, IndexMap);
			else
			{
				
				DriverNode.startreqtime=0;
				DriverNode.endreqtime=0;
				Variable.SetHeadTailteamTime1(DriverNode, IndexMap);
				if(DriverNode.startreqtime==0||DriverNode.endreqtime==0)
				{
					Variable.errorcode=-6;
					System.out.println(DriverNode.CallNum+"無頭尾班");
					break;
				}
			}
			
			
			 //工時小於6小時不找休息時間
			if((DriverNode.EndTime-DriverNode.StartTime)>Variable.nonrelax)
			{
				//根據資料庫排班狀態初始化可候選休息的區間
				DriverNode=initializeRelaxarray(DriverNode,timeinterval,IndexMap,Variable,ilf,intervalCount);
			}
			
			
			//發生google map api錯誤立即中止
			if(Variable.errorcode<=-2)
				break;
			
			car.get(defineVariable.switchareaindex(DriverNode.station)).addCar(DriverNode,(int)(DriverNode.StartTime)/1800);
		}while(rs.next());	
		carsize=size;
		return car;
	}
	public DriverTable initializeRelaxarray(DriverTable DriverNode ,double interval,Map<Integer,
			RequestTable> IndexMap,defineVariable Variable,ILF ilf,int intervalCount)
	{
		double intervalSec = interval * 3600;	
		int startindex=0;
		int endindex=0;
		startindex=(int)((DriverNode.startreqtime+1800) /(int) intervalSec);//計算頭班所在區間				
		endindex=((DriverNode.endreqtime-1800) / (int)intervalSec);	//計算尾班所在區間		
		String tempreq=null;
		//將有落在休息時間的未排班區間先加到可候選休息時間array
		for(int i=startindex;i<=endindex;i++)
		{
			if(DriverNode.TimeInterval[i].indexOf("未排班")!=-1)
			{
				DriverNode.relaxarry.add(String.valueOf(i));//先將所有落在可休息時間內的休息區間加到array
			}
		}
		
		//將趟跟趟之間的未排入檢查是否可以成為休息區間
		for(int i=startindex;i<=endindex;i++)
		{
			//找到有排班的區間
			if(!(DriverNode.TimeInterval[i].indexOf("不上班")!=-1) 
					&&!(DriverNode.TimeInterval[i].indexOf("未排班")!=-1)
					&&!(DriverNode.TimeInterval[i].indexOf("休息")!=-1))
			{
				if(tempreq==null||!(tempreq.indexOf(DriverNode.TimeInterval[i])!=-1))
				{
					tempreq=DriverNode.TimeInterval[i];
					//找出當下的需求
					RequestTable tagetreq = Variable.RequestTableQuery(DriverNode.TimeInterval[i],Variable,IndexMap);
					//上一趟的預約者
					RequestTable tableindex = Variable.PreRequestTableQuery(DriverNode,i,Variable,IndexMap);	
					//找出上一班有排班的區間旅行時間					
					int [] traveltime=Variable.DistanceTime(tableindex,tagetreq,ilf,Variable,intervalSec);
					if(traveltime[1]<=-2)
					{
						Variable.errorcode=traveltime[1];
						break;
					}
					//加上容忍
					int temptraveltime=traveltime[1]+600;
					
					//如果上一趟與與當前趟其中一個遇到尖峰時刻要加上delay time
					if((tagetreq.originalStartTime>=27600&&tagetreq.originalStartTime<=31200)||(tableindex.originalDestinationTime>=27600&&tableindex.originalDestinationTime<=31200))
						temptraveltime+=900;                       //早上7:30~8:30尖峰時段再加上15分延遲
					if(tagetreq.originalStartTime>=60900&&tagetreq.originalStartTime<=67200||(tableindex.originalDestinationTime>=60900&&tableindex.originalDestinationTime<=67200))
						temptraveltime+=1200;                      //下午16:45~18:30尖峰時段再加上20分延遲
					
					
					
					//上一趟下車所剩下可扣的時間
					int tempSecond=(tableindex.originalDestinationTime-(tableindex.originalDestinationTime%60));
                    int min = (int)((tempSecond % 3600) / 60);//轉化成分
					//扣掉可用殘餘的時間
					int Residualtime=0;
					if(min>=30&&min<60)
						 Residualtime=(60-min);
					else if(min<30&&min>=0)
						Residualtime=(30-min);
					temptraveltime=temptraveltime-(Residualtime*60);					
					
					//這趟上車所剩的時間
					tempSecond=(tagetreq.originalStartTime-(tagetreq.originalStartTime%60));
					min=0;
					min = (int)((tempSecond % 3600) / 60);//轉化成分
					Residualtime=0;
					if(min==30||min==0)
						Residualtime=0;
					else if(min<30)
						Residualtime=min;
					else if(min>30)
						Residualtime=(min-30);
					
					//扣掉可用殘餘的時間
					temptraveltime=temptraveltime-(Residualtime*60);
					
					//計算要花費的格數
					int Spendtimecount=((temptraveltime % intervalSec) > 0.0 ? ((int)(temptraveltime/ intervalSec))+1 : (int)(temptraveltime/ intervalSec));

					//刪除開車所花費的格數
					for(int count=traveltime[0]+1;count<(traveltime[0]+Spendtimecount+1);count++)
					{
						if(count>=startindex&&count<=endindex)
							DriverNode.relaxarry.remove(String.valueOf(count));//刪除開車所花費的時間
					}
					
				
			}
			}
		}
		boolean found=false;
		//檢查最後一趟到最後一趟的下一趟
		for(int i=endindex;i<intervalCount;i++)
		{
			if(found)
			{
			  break;
			}
				
			if(Variable.errorcode<=-2)
				break;
			//找到有排班的區間
			if(!(DriverNode.TimeInterval[i].indexOf("不上班")!=-1) 
					&&!(DriverNode.TimeInterval[i].indexOf("未排班")!=-1)
					&&!(DriverNode.TimeInterval[i].indexOf("休息")!=-1))
			{
				//最後一趟的下一趟
				RequestTable tagetreq =  Variable.RequestTableQuery(DriverNode.TimeInterval[i],Variable,IndexMap);
				//最後一趟
				RequestTable tableindex =Variable.PreRequestTableQuery(DriverNode,i,Variable,IndexMap);
				//System.out.print("\n上一趟1"+tagetreq.Number);
				//System.out.print("\n上一趟2"+tableindex.Number);
				//找出休息時間內的最後一趟的區間旅行時間
				int [] traveltime=Variable.DistanceTime(tableindex,tagetreq,ilf,Variable,intervalSec);
				//回傳error code
				if(traveltime[1]<=-2)
				{
					Variable.errorcode=traveltime[1];
					break;
				}
				
				//加上容忍
				int temptraveltime=traveltime[1]+600;
				//如果最後一趟的下一趟與最後一趟其中一個遇到尖峰時刻要加上delay time
				if((tagetreq.originalStartTime>=27600&&tagetreq.originalStartTime<=31200)||(tableindex.originalDestinationTime>=27600&&tableindex.originalDestinationTime<=31200))
					temptraveltime+=900;                       //早上7:30~8:30尖峰時段再加上15分延遲
				if(tagetreq.originalStartTime>=60900&&tagetreq.originalStartTime<=67200||(tableindex.originalDestinationTime>=60900&&tableindex.originalDestinationTime<=67200))
					temptraveltime+=1200;                      //下午16:45~18:30尖峰時段再加上20分延遲
				
				//上一趟下車所剩下可扣的時間
				int tempSecond=(tableindex.originalDestinationTime-(tableindex.originalDestinationTime%60));
                int min = (int)((tempSecond % 3600) / 60);//轉化成分
			    //扣掉可用殘餘的時間
				int Residualtime=0;
				if(min>=30&&min<60)
					 Residualtime=(60-min);
				else if(min<30&&min>=0)
					Residualtime=(30-min);
				temptraveltime=temptraveltime-(Residualtime*60);
				
				//最後一趟的下一趟上車所剩的時間
				tempSecond=(tagetreq.originalStartTime-(tagetreq.originalStartTime%60));
				min = (int)((tempSecond % 3600) / 60);//轉化成分
				Residualtime=0;
				if(min==30||min==0)
					Residualtime=0;
				else if(min<30)
					Residualtime=min;
				else if(min>30)
					Residualtime=(min-30);
				//扣掉可用殘餘的時間
				temptraveltime=temptraveltime-(Residualtime*60);
				int Spendtimecount=((temptraveltime % intervalSec) > 0.0 ? ((int)(temptraveltime/ intervalSec))+1 : (int)(temptraveltime/ intervalSec));
				//刪除開車所花費的格數
				for(int count=traveltime[0]+1;count<(traveltime[0]+Spendtimecount+1);count++)
				{
					if(count>=startindex&&count<=endindex)
						DriverNode.relaxarry.remove(String.valueOf(count));//刪除開車所花費的時間
				}
				found=true;
			}
		}
		
		//初始化temprelaxarry
		if(Variable.errorcode>-2)
			DriverNode.temprelaxarry=new ArrayList<String>(DriverNode.relaxarry);	
		return DriverNode;
	}
	public void UpdateNode(defineVariable Variable, String restTime,DriverTable node) 
	{
		List<Integer> RunList = new ArrayList<Integer>();			
		String insertSQL = "UPDATE arrangedtable SET ";
		String constrain = " WHERE date = '" + Variable.date + "' AND arrangetime = '" + Variable.time  + "' AND carid = '" + ID + "'";
		insertSQL+="resttime1='"+restTime+"'";
		//紀錄前一個user代號
		String TempUserindex="null";	
		//儲存run
		for(int index =0; index <node.TimeInterval.length; index++)
		{
			if(!(node.TimeInterval[index].indexOf("不上班")!=-1)&&!(node.TimeInterval[index].indexOf("未排班")!=-1))
			{
				if(TempUserindex=="null"||!(TempUserindex.equals(node.TimeInterval[index])))
					RunList.add(index);
				TempUserindex=node.TimeInterval[index];				
			}
		}	
	
		for(int index=0;index<16;index++)	 
		{
			if(index<RunList.size())
			{
				insertSQL+=" ,run"+(index+1)+"='"+RunList.get(index)+"', user"+(index+1)+"='"+node.TimeInterval[(int) RunList.get(index)]+"'";			
			}else
			{
				insertSQL+=" ,run"+(index+1)+"='-1', user"+(index+1)+"=' '";			
			}
		}
		
		insertSQL += constrain;
		try
		{
			Variable.smt2.executeUpdate(insertSQL);		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
		
	}
	public void PrintNode(Connection conn, String date, String time, String tableName)
	{
		int runs = 0;
		String recentUser = "";
		int[] run = new int[16];
		Statement smt = null;
		String information = "車號: " + ID;		
		String insertSQL = "insert into "+tableName+" (carid, timeinterval, resttime,resttime1, date, arrangetime, worktime, cartype, run1, run2, run3, run4, run5, run6, run7, run8, run9, run10, run11, run12, run13, run14, run15, run16, user1, user2, user3, user4, user5, user6, user7, user8, user9, user10, user11, user12, user13, user14, user15, user16) values ('";
		for(int i = 0; i < run.length; i++)
		{
			run[i] = -1;
		}
		for(int i = 0; i < TimeInterval.length; i++)
		{
			if(TimeInterval[i].equals("不上班") || TimeInterval[i].equals("休息"))
			{
				information = information + ", 不";
			}
			else if(TimeInterval[i].equals("未排班"))
			{
				information = information + ", 未";
			}
			else
			{
				information = information + ", " + TimeInterval[i];
				if(!recentUser.contentEquals(TimeInterval[i]) )
				{
					recentUser = TimeInterval[i];
					run[runs++] = i;
				}
			}
		}
		//車牌
		insertSQL = insertSQL + ID + "',";
		//interval
		insertSQL = insertSQL + (double)24/TimeInterval.length + ",";
		//resttime
		insertSQL = insertSQL + RestTime + ",'";
		//resttime1
		insertSQL = insertSQL + RestTime1 + "', '";
		//日期
		insertSQL = insertSQL + date + "', '";
		//時間
		insertSQL = insertSQL + time +"', ";
		//工作時段
		insertSQL = insertSQL + StartTime + ",'";
		//車種
		insertSQL = insertSQL + Car + "'";
		//run
		for(int i = 0; i < run.length; i++)
		{
			insertSQL = insertSQL + "," + run[i];
		}
		insertSQL = insertSQL + ",'";
		//user	
		for(int i = 0; i < run.length - 1; i++)
		{
			if(run[i] == -1)
			{
				insertSQL = insertSQL + " " + "','";
			}
			else
			{
				insertSQL = insertSQL + TimeInterval[run[i]] + "','";
			}
		}
		if(run[run.length-1] == -1)
		{
			insertSQL = insertSQL + " ')";
		}
		else
		{
			insertSQL  = insertSQL + TimeInterval[run[run.length-1]] + "')";
		}
		try
		{
			smt = conn.createStatement();
			smt.executeUpdate(insertSQL);
			smt.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public void InitialInterval(DriverTable node,String time, double interval,defineVariable Variable) throws Exception
	{
		int startInterval, endInterval;
		double intervalSec;		
		int startSec=0;
		int endSec=0;
		ResultSet rs;	
		rs = Variable.smt2.executeQuery("SELECT * FROM `carbarn` WHERE `車場地址`='"+node.Address+"'");
		if(rs.next())
		{
			node.Lat=Double.parseDouble(rs.getString("Lat").trim());
			node.Lon=Double.parseDouble(rs.getString("Lon").trim());
		}
		String[] worktime=time.split("~");	
		String[] onwork=worktime[0].split(":");
		String[] offwork=worktime[1].split(":");
		startSec=(Integer.parseInt(onwork[0])*3600)+(Integer.parseInt(onwork[1])*60);//上班時間轉換秒數
	    endSec =(Integer.parseInt(offwork[0])*3600)+(Integer.parseInt(offwork[1])*60);//下班時間轉換秒數;	
	    intervalSec = interval * 3600;
		
		for(int i = 0; i < node.TimeInterval.length; i++)
		{
			//-2代表該時段不上班
			node.TimeInterval[i] = "不上班".trim();
		}		
		node.StartTime = startSec;
		node.EndTime = endSec;		
		node.halfworktime=(int)(((startSec+1800)+(endSec+2700))/2);
		startInterval = (int)((startSec+1800) / intervalSec);
		endInterval = (int)((endSec+2700) / intervalSec);
		
		for(int i = startInterval; i <= endInterval; i++)
		{
			//上班時間但尚未排班
			node.TimeInterval[i] = "未排班".trim();
		}
		//處理休息時間
		/*for(int i = startInterval + (int)(restInterval/interval); i < startInterval + (int)(restInterval/interval) + (int)(1/interval); i++)
		{
			node.TimeInterval[i] = "休息";
		}*/
	}
	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch(CloneNotSupportedException e)
		{
			System.out.println(e);
			return null;
		}
	}
	public LinkedList<DriverTable> filterDriverTable(List<carGroup> DriverList,int carsize,int TimeInterval,int mode,defineVariable Variable) 
	   {
			boolean AddFlag=false;
			LinkedList <DriverTable> OriginDriverTable = new LinkedList<DriverTable>();
			 for(int areaindex=0;areaindex<Variable.areanum;areaindex++)
			 { 				
				 for(int timeindex=0;timeindex<Variable.intervalnum;timeindex++)
				 { 
					 for(int index=0;index<DriverList.get(areaindex).getCar(timeindex).size();index++)
					 {
						 AddFlag=false;
						switch(mode)
						{
						case 0://夜車
							 AddFlag=true;
							break;
						case 1://土城
							if(DriverList.get(areaindex).getCar(timeindex).get(index).station.indexOf("土城")!=-1)
								 AddFlag=true;
							break;
						case 2://汐止
							if(DriverList.get(areaindex).getCar(timeindex).get(index).station.indexOf("汐止")!=-1)
								 AddFlag=true;
							break;
						case 3://中和
							if(DriverList.get(areaindex).getCar(timeindex).get(index).station.indexOf("中和")!=-1)
								 AddFlag=true;
							break;
						case 4://新店
							if(DriverList.get(areaindex).getCar(timeindex).get(index).station.indexOf("新店")!=-1)
								 AddFlag=true;
							break;
						 default: //預設全部
							 AddFlag=true;
						}
						//如果尋找的時間間格落在工時以內就加進去 
						 if(TimeInterval>DriverList.get(areaindex).getCar(timeindex).get(index).StartTimeInterval
						 &&TimeInterval<DriverList.get(areaindex).getCar(timeindex).get(index).EndTimeInterval)
							 if(DriverList.get(areaindex).getCar(timeindex).get(index).TimeInterval[TimeInterval].indexOf("未排班")!=-1)
								if(AddFlag)
									OriginDriverTable.add(DriverList.get(areaindex).getCar(timeindex).get(index));
					  }
				}
			}
		return OriginDriverTable;
		}
	//讀取某個司機表放置List讓filter可使用 carindex:要單獨抽出來的場站
	public LinkedList <DriverTable> getareafilterDriverTable(int carindex,List<carGroup> DriverList,defineVariable Variable)
	 {
		LinkedList <DriverTable> OriginDriverTable = new LinkedList<DriverTable>();
		if(carindex<=Variable.areanum)//目前只有4個站
		{
			for(int timeindex=0;timeindex<Variable.intervalnum;timeindex++)
			{ 
				for(int index=0;index<DriverList.get(carindex).getCar(timeindex).size();index++)
				{		
						OriginDriverTable.add(DriverList.get(carindex).getCar(timeindex).get(index));
				}
			 }
		 }
			return OriginDriverTable;
		}
	//讀取4大金釵
		public LinkedList <DriverTable> getfFournobileDriverTable(List<carGroup> DriverList,defineVariable Variable) 
	    {
			
			LinkedList <DriverTable> OriginDriverTable = new LinkedList<DriverTable>();
			for(int Zhonghespecialcarindex=0;Zhonghespecialcarindex<Variable.Zhonghespecialcar.size();Zhonghespecialcarindex++)
			{
				for(int area=0;area<Variable.areanum;area++)
				 { 				
					 for(int timeindex=0;timeindex<40;timeindex++)
					 { 
						 for(int index=0;index<DriverList.get(area).getCar(timeindex).size();index++)
						 {
							 if(Variable.Zhonghespecialcar.get(Zhonghespecialcarindex).indexOf(DriverList.get(area).getCar(timeindex).get(index).ID)!=-1)
							 {
								 OriginDriverTable.add(DriverList.get(area).getCar(timeindex).get(index)); 
							 } 
						 }
					}
				}
	}
			
		
		return OriginDriverTable;
		}
	//讀取司機表放置List讓filter可使用
	public List <DriverTable> getfilterDriverTable(List<carGroup> DriverList,defineVariable Variable) throws IOException
    {
		
		List<DriverTable> filterDriverTable= new ArrayList<DriverTable>(300);
		
		for(int j=0;j<Variable.areanum;j++)
		 { 				
			 for(int l=0;l<40;l++)
			 { 
				 for(int k=0;k<DriverList.get(j).getCar(l).size();k++)
				 {
				    filterDriverTable.add(DriverList.get(j).getCar(l).get(k));
				  
				 }
			}
		}
	
	return filterDriverTable;
	}
	//取得夜班車
		public List <DriverTable> getnightcarDriverTable(List<carGroup> DriverList,defineVariable Variable) throws IOException
	    {
			
			List<DriverTable> filterDriverTable= new ArrayList<DriverTable>(300);
			
			for(int j=0;j<Variable.areanum;j++)
			 { 				
				 for(int l=0;l<40;l++)
				 { 
					 for(int k=0;k<DriverList.get(j).getCar(l).size();k++)
					 {
						 if(DriverList.get(j).getCar(l).get(k).StartTime>43200)
						 //將原始司機加入filterlist
						 { 
							 filterDriverTable.add(DriverList.get(j).getCar(l).get(k));
					     }
					  
					 }
				}
			}
		
		return filterDriverTable;
		}
		public  Map<String, DriverTable> GetcarIndexMap(Map<String, DriverTable> carIndexMap,List<carGroup> car,defineVariable Variable )
		{  
			//紀錄司機修改位置
			for(int areaindex=0;areaindex<Variable.areanum;areaindex++)
			{ 
				for(int timeindex=0;timeindex<Variable.intervalnum;timeindex++)
				{ 
					for(int index=0;index<car.get(areaindex).getCar(timeindex).size();index++)
					{
					   car.get(areaindex).getCar(timeindex).get(index).index[0]=areaindex;
					   car.get(areaindex).getCar(timeindex).get(index).index[1]=timeindex;
					   car.get(areaindex).getCar(timeindex).get(index).index[2]=index;
					   carIndexMap.put(car.get(areaindex).getCar(timeindex).get(index).ID,car.get(areaindex).getCar(timeindex).get(index));					   
					}
				}
			}
		    return carIndexMap;
		}
	    //根據傳入的最適合排入車輛的driverNumber，更動完整的車輛表
		//DriverList為原始司機表 TargetDriver:要排入的司機 tableName:要寫入的資料表名稱
		public  void ModifyOriginDriverTable(defineVariable Variable,RequestTable Reqtable,double interval,DriverTable TargetDriver,String tableName, Map<Integer,RequestTable> indexmap,List<carGroup> car)
		{
			if(Reqtable.DestinationAddress.indexOf("新北市汐止區建成路152號")!=-1)
				System.out.println("111");
			double	IntervalSec = interval * 3600;//將interval的時間由小時為單位轉成由秒為單位
			int	StartInterval = (int)( (Reqtable.originalStartTime) / IntervalSec);//上車時間在一天中的interval index
			//下車時間在一天中的interval index
			int	EndInterval = (Reqtable.originalDestinationTime / (int) IntervalSec);
			TargetDriver=car.get(TargetDriver.index[0]).getCar(TargetDriver.index[1]).get(TargetDriver.index[2]);
			if(Reqtable.AssignSharing!=-1)
			{
				//當AssignSharing不等於-1時，代表欲排入預約為共乘排班，AssignSharing的值代表的是已經排入這輛車輛的預約index
				RequestTable AssignSharingreq=indexmap.get(Reqtable.AssignSharing);
				String intervalString=new String();				
				if(UpdateSharingData(Reqtable,AssignSharingreq,Variable))
				{
					intervalString = Reqtable.Number+ "_"+ Reqtable.AssignSharing;
				}else
				{
					UpdateSharingData(AssignSharingreq,Reqtable,Variable);
					intervalString = Reqtable.AssignSharing+"_"+Reqtable.Number;
				}
				//修改車輛的Time Interval
				for(int i = StartInterval; i <= EndInterval; i++)
				{
					TargetDriver.TimeInterval[i]= intervalString;					
				}				
				//更新司機可用的休息區間
				 //工時小於4小時不找休息時間
				if((TargetDriver.EndTime-TargetDriver.StartTime)>14400)
					TargetDriver.relaxarry=new ArrayList<String>(TargetDriver.temprelaxarry);	
				
				 Reqtable.Targetdrivers=TargetDriver.ID;
				 AssignSharingreq.Targetdrivers=TargetDriver.ID;
				 try
				{
					 //更新當前預約者與共乘者的排班狀態
					 String sql = "UPDATE "+tableName+" SET arranged = 1,Targetdrivers='"+Reqtable.Targetdrivers+"' WHERE 識別碼 = '" + Reqtable.Number + "' AND arrangedate = '" + Variable.date + "' AND arrangetime = '" + Variable.time + "'";
					 Variable.smt.executeUpdate(sql);	
					 sql = "UPDATE "+tableName+" SET arranged = 1,Targetdrivers='"+AssignSharingreq.Targetdrivers+"' WHERE 識別碼 = '" + AssignSharingreq.Number + "' AND arrangedate = '" + Variable.date + "' AND arrangetime = '" + Variable.time + "'";
					 Variable.smt.executeUpdate(sql);
			    }
				catch(Exception e)
				{
						
				}
			
			}
			else
			{
			    //將當前預約者寫入司機班表
				for(int i = StartInterval; i <= EndInterval; i++)
				{
					TargetDriver.TimeInterval[i]=String.valueOf(Reqtable.Number);	
				}
				//更新司機可用的休息區間 小於4小時不找休息時間
				if((TargetDriver.EndTime-TargetDriver.StartTime)>14400)
					TargetDriver.relaxarry=new ArrayList<String>(TargetDriver.temprelaxarry);	
				Reqtable.Targetdrivers=TargetDriver.ID;
				try
				{
					//更新當前預約者的排班狀態
					String sql = "UPDATE "+tableName+" SET arranged = 1,Targetdrivers='"+Reqtable.Targetdrivers+"' WHERE 識別碼 = '" + Reqtable.Number + "' AND arrangedate = '" + Variable.date + "' AND arrangetime = '" + Variable.time + "'";
					Variable.smt.executeUpdate(sql);		
				}
				catch(Exception e)
				{
				
				}
			}
			//更新司機班表
			UpdateNode(Variable,TargetDriver.RestTime1,TargetDriver);						 
			
		}
		private static boolean UpdateSharingData(RequestTable req1,RequestTable req2,defineVariable variable) 
		{	
			String reqinfo=null;
			int arrivetime=0;
			String[] Order=new String[2];
			reqinfo=String.valueOf(req1.Number)+"_"+String.valueOf(req2.Number);				
			ResultSet rs;
			int UpdateFlag=0;
			try
			{
				rs = variable.smt2.executeQuery("SELECT AssignSharing FROM travelinformationofcarsharing WHERE date = '" + variable.date + "' AND arrangetime = '" 
						+ variable.time + "' AND `AssignSharing`='"+reqinfo+"'");	
				if(rs.next())
				{
					if(req1.DestinationTime>req2.DestinationTime)
					{
						arrivetime=req1.DestinationTime;
						Order[0]="1_1";
						Order[1]="1_0";	
					}else
					{
						arrivetime=req2.DestinationTime;
						Order[0]="1_1";
						Order[1]="0_1";	
					}
					String sql = "UPDATE travelinformationofcarsharing SET `arrivetime`="+arrivetime+
						", `中繼點1`='"+Order[0]+"', `中繼點2`='"+Order[1]+"' WHERE `date`= '" + variable.date + "' AND arrangetime = '" + variable.time+"' AND `AssignSharing`='"+reqinfo+"'";
			    	variable.smt.executeUpdate(sql);		
			    	UpdateFlag=1;					
				}else
				{
					UpdateFlag=0;					
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				
			}
			if(UpdateFlag==1)
				return true;
			else
				return false;
		}
		public boolean checkworktime(String time,defineVariable Variable)
		{
			String[] timesplitarray=time.split(":");
			boolean result=false;
			 try			 
			 {
				 if(( Integer.valueOf(timesplitarray[0])<23&& Integer.valueOf(timesplitarray[0])>0)&&( Integer.valueOf(timesplitarray[1])<60&& Integer.valueOf(timesplitarray[1])>=0))
					 result=false;
				 else
					 result = true; 
			 }
			 catch (Exception ex)
			 {
				 Variable.errorcode=14;
				 return true;
		       }
			  return result;
		}
		/*public DriverTable SetHeadTailTime(int Interval,DriverTable Driver,int mode)
		{
			boolean[] CheckStuas={false,false};
			if(mode==0)
			{
				//頭班
				for(int index=Interval;index<=Interval+3;index++)
				{
					if(!(Driver.TimeInterval[index].indexOf("不上班")!=-1)
						&&!(Driver.TimeInterval[index].indexOf("未排班")!=-1)
						&&!(Driver.TimeInterval[index].indexOf("休息")!=-1))
					{
						if(index==Interval)
							CheckStuas[0]=true;
						else
							CheckStuas[1]=true;
						break;
					}
					else
					{
						if(index==Interval)
							CheckStuas[0]=false;
						else
							CheckStuas[1]=false;
					}
				}
			}
			else
			{
				//尾班
				for(int index=Interval;index>=Interval-3;index--)
				{
					if(!(Driver.TimeInterval[index].indexOf("不上班")!=-1)
						&&!(Driver.TimeInterval[index].indexOf("未排班")!=-1)
						&&!(Driver.TimeInterval[index].indexOf("休息")!=-1))
					{
						if(index==Interval)
							CheckStuas[0]=true;
						else
							CheckStuas[1]=true;
						break;
					}
					else
					{
						if(index==Interval)
							CheckStuas[0]=false;
						else
							CheckStuas[1]=false;
					}					
				}
			}
			return Driver;		
		}	*/
}
