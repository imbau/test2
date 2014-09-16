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
	public boolean StartArrange;//2013/1/11 �W�[�P�_�O�_�w�ƤJ�Y�Z
	public boolean EndArrange;//2013/1/11 �W�[�P�_�O�_�w�ƤJ�߯Z
	public String station;//�X�����I
	public int startreqtime;//�Y�Z�w���̮ɶ�
	int endreqtime;//���Z�w���̮ɶ�
	public int carsize;
	public boolean Arrangedflag;
	public boolean Greedyflag;
	public boolean  PreMark=false;//�ƥ��аO���ƤJ�w����
	public int earaWeight;
	public int halfworktime;
	public int []index={-1,-1,-1};	
	public List<String> temprelaxarry;//�����w�B�z�L�ѤU�i�Կ�𮧪��϶��A���n�̫�minfilter�����~�|�g�^�q��
	public List<String> relaxarry;//�����i�Կ�𮧪��϶�
	public int StartDistanceValue;//�����W�@�ӭ��ȤU���a�I���e�w���̤W���a�I���Ȧ�ɶ�
	public int EndDistanceValue;//������e���ȤU���a�I��U�@�ӹw���̤W���a�I���Ȧ�ɶ�
	public int PreviousrequstTime;
	public int NextrequstTime;	
	public int Holiday;	//�����O�_������ 0���D���� 1������
	public int  HalfWorkTimeInterval=0;//���������������ɬq���϶�
	public int  StartTimeInterval=0;//�����u�ɰ_�l�϶�
	public int  EndTimeInterval=0;//�����u�ɵ����϶�
	public DriverTable(int intervalCount)
	{
		StartTime = -1;
		EndTime = -1;
		X = -1;
		Y = -1;
		RestTime1 ="����w";
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
		StartArrange=false;//2013/1/11 �W�[�P�_�P�_�O�_�w���Y�Z��l��
		EndArrange=false;//2013/1/11 �W�[�P�_�P�_�O�_�w�Ƨ��Z��l��
		Arrangedflag=true;
		carsize=0;
		Greedyflag=false;//�P�_�O�_������15���W�Z�p�G�������\���e���Z
		earaWeight=2;	
		//IdleCount=-1;		
		relaxarry=new ArrayList<String>(12);//��l�ƥi�Կ��array
		temprelaxarry=new ArrayList<String>(relaxarry);	
		StartDistanceValue=-1;
		EndDistanceValue=-1;
		PreviousrequstTime=-1;
		NextrequstTime=-1;
		startreqtime=-1;//�Y�Z�w���̮ɶ�
		endreqtime=-1;//���Z�w���̮ɶ�
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
		String carInsertSQL = "insert into availablecars (���W, �I��,telephone,drivername,����, �Z�O, ����, �ɬq, �a�}, ���W,date,time,TurnoutDate) values ('";
		LinkInfo linkinfo = new LinkInfo();		
	    Workbook book;
		Sheet sheet;
		book = Workbook.getWorkbook(new File(linkinfo.getUploadLink() + carFileName));
		int sheetsNum = book.getNumberOfSheets();
		int index=-1;
		if(sheetsNum==1)
		{
			sheet = book.getSheet(0);
			int count = sheet.getRows();	//���o��Ƶ���
			String temp;
			Cell[] data;	
			String[] splitarray ;		
			for(int i = 0; i < count; i++)
			{
				data =  sheet.getRow(i);				//read whole row data
				index=i;
				try
				{
					//�p�G0��S����Ƶ����̫�@��
					if(data[0].getContents().equals(""))
						break;
				}
				catch(Exception e)
				{
					break;
				}			
				temp = "";		
				//�Φ�insert�@��request��ƶi��Ʈw����SQL�y�k
				temp += carInsertSQL;
				for(int j = 1; j < 10; j++)
				{
					//�ˬd���n��T�O�_���ʤֶ�g
					try
					{
						temp += data[j].getContents().trim() + "','";
						if(data[j].getContents().equals(""))
							Variable.CheckErrorCode(j);	
							
						
						
					}catch(Exception e)
					{
						//�p�G���ŦX�榡�N���_
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
					//���ŦX�ɶ��榡
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
					else if(e.toString().indexOf("�ɬq")!=-1)	
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
		rs = smt.executeQuery("SELECT A1.*,A2 .* 	FROM arrangedtable A1, availablecars A2 WHERE A1.carid ='"+carid+"' AND A2.����='"+carid+"' AND A2. date = '"+arrangedate +"' AND A2.time= '" +arrangetime+"' AND A1. date = '" + arrangedate + "' AND A1.arrangetime = '" + arrangetime + "' ORDER BY A1.worktime ASC");
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
			DriverNode.Address = rs.getString("�a�}").trim();
			DriverNode.CallNum= rs.getString("�I��").trim();
			DriverNode.RestTime1 = rs.getString("resttime1").trim();		
			DriverNode.station=rs.getString("���W").trim();				
			DriverNode.InitialInterval(DriverNode,rs.getString("�ɬq").trim(), timeinterval,Variable);	
			DriverNode.Holiday=holiday;
			smt2 = con.createStatement();
			//�^�_�������A				
			for(int i = 1; i < 17; i++)
			{
				//Ū���Z����ƨæ^�_Node����timeinterval
				if(rs.getInt("run" + String.valueOf(i)) != -1&&rs.getInt("run" + String.valueOf(i)) != 0)
				{  
					String information = rs.getString("user"+ String.valueOf(i));
					String[] testnumber = information.split("_");
				
						int informationNum = Integer.valueOf(testnumber[0]);	
						rs2 = smt2.executeQuery("SELECT ��F�ɶ�, �ɬq,�U���ϰ� FROM userrequests WHERE arrangedate = '" + arrangedate + "' AND arrangetime = '" +arrangetime + "' AND �ѧO�X ='" + informationNum+"'");
						if(rs2.next())
						{
							String temptime = rs2.getString("�ɬq");
							int temphour = Integer.valueOf(temptime.substring(0, 2));	//hour = XX
							int tempmin = Integer.valueOf(temptime.substring(2, 4));		//min = YY
							int tempstartsec = temphour * 3600 + tempmin * 60;		//�_�I�ɶ�
							int tempendsec =rs2.getInt("��F�ɶ�");
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
	    int[] flag=new int[2];//�P�_�ˬd�Ľ몺�ɬq�O�_�F��פ�
		try {
			rs = smt.executeQuery("SELECT A1.*,A2 .* 	FROM arrangedtable A1, availablecars A2 WHERE A1.carid = A2.���� AND A2. date = '"+arrangedate +"' AND A2.time= '" +arrangetime+"' AND A1. date = '" + arrangedate + "' AND A1.arrangetime = '" + arrangetime + "' GROUP BY A1.`carid` ORDER BY A1.worktime ASC");
			rs.first();
			double timeinterval =rs.getDouble("timeinterval");
			int IntervalSec = (int)(timeinterval * 3600);
			int intervalCount = (int)(24 /  timeinterval);
			do
			{	
				DriverTable DriverNode = new DriverTable(intervalCount);
				DriverNode.ID = rs.getString("carid").trim();											
				DriverNode.Car = rs.getString("cartype");			
				DriverNode.Address = rs.getString("�a�}").trim();
				DriverNode.RestTime1 = rs.getString("resttime1").trim();				
				DriverNode.station=rs.getString("���W").trim();	
				DriverNode.CallNum=rs.getString("�I��").trim();	
				try 
				{
				
					DriverNode.InitialInterval(DriverNode,rs.getString("�ɬq").trim(), timeinterval,Variable);
					if(DriverNode.CallNum.indexOf("50")!=-1)
						System.out.println(DriverNode.CallNum);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				
				smt2 = con.createStatement();
				smt3= con.createStatement();
				//�^�_�������A				
				for(int i = 1; i < 17; i++)
				{
					flag[0]=0;
					flag[1]=0;
					//Ū���Z����ƨæ^�_Node����timeinterval
					if(rs.getInt("run" + String.valueOf(i)) != -1&&rs.getInt("run" + String.valueOf(i)) != 0)
					{  
						String information = rs.getString("user"+ String.valueOf(i));
						String[] testnumber = information.split("_");
						int informationNum = Integer.valueOf(testnumber[0]);
						rs2 = smt2.executeQuery("SELECT * FROM userrequests WHERE arrangedate = '" + arrangedate + "' AND arrangetime = '" +arrangetime + "' AND �ѧO�X ='" + informationNum+"'");
					 	if(rs2.next())
						{
							String temptime = rs2.getString("�ɬq");
							int temphour = Integer.valueOf(temptime.substring(0, 2));	//hour = XX
							int tempmin = Integer.valueOf(temptime.substring(2, 4));		//min = YY
							int tempstartsec = temphour * 3600 + tempmin * 60;		//�_�I�ɶ�
							int tempendsec =rs2.getInt("��F�ɶ�");
							int tempStartInterval = (int)(tempstartsec / IntervalSec);
							int tempEndInterval = (int)(tempendsec / IntervalSec);
							for(int j = tempStartInterval; j <= tempEndInterval; j++)
							{
								if(DriverNode.TimeInterval[j].indexOf("���W�Z")!=-1||DriverNode.TimeInterval[j].indexOf("���ƯZ")!=-1)
									DriverNode.TimeInterval[j] = information;	
								else
								{	
									String[] reqnumber = DriverNode.TimeInterval[j].split("_");
									smt3.executeUpdate("UPDATE userrequests SET `arranged` = -2 , `Targetdrivers`='null' WHERE `�ѧO�X` ="+informationNum+" and `arrangedate`='"+arrangedate+"' and `arrangetime`='"+arrangetime+"'");
									smt3.executeUpdate("UPDATE userrequests SET `arranged` = -2 , `Targetdrivers`='null' WHERE `�ѧO�X` ="+reqnumber[0]+" and `arrangedate`='"+arrangedate+"' and `arrangetime`='"+arrangetime+"'");
									smt3.executeUpdate("INSERT INTO writingerror (date,time,reqindex,carcallnum) VALUES ('"+arrangedate+"','"+arrangetime+"','"+informationNum+"','"+DriverNode.CallNum+"')");								
									smt3.executeUpdate("INSERT INTO writingerror (date,time,reqindex,carcallnum) VALUES ('"+arrangedate+"','"+arrangetime+"','"+reqnumber[0]+"','"+DriverNode.CallNum+"')");
									if(reqnumber.length>2)
									{
										smt3.executeUpdate("INSERT INTO writingerror (date,time,reqindex,carcallnum) VALUES ('"+arrangedate+"','"+arrangetime+"','"+reqnumber[1]+"','"+DriverNode.CallNum+"')");
										smt3.executeUpdate("UPDATE userrequests SET `arranged` = -2 , `Targetdrivers`='null' WHERE `�ѧO�X` ="+reqnumber[1]+" and `arrangedate`='"+arrangedate+"' and `arrangetime`='"+arrangetime+"'");
									}
									DriverNode.writingerror=DriverNode.TimeInterval[j];
									DriverNode.TimeInterval[j]="���ƯZ";
									for(int prvindex = 1; prvindex<= 10; prvindex++)
									{
										if(DriverNode.TimeInterval[j-prvindex]==DriverNode.writingerror)
										{
											DriverNode.TimeInterval[j-prvindex]="���ƯZ";	
											flag[0]=0;
										}				
										else
											flag[0]=1;
										if(DriverNode.TimeInterval[j+prvindex]==DriverNode.writingerror)
										{
											DriverNode.TimeInterval[j+prvindex]="���ƯZ";
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
	//�Y���ZŪ������
	public  List<carGroup> readDrivertable(List<carGroup> car,defineVariable Variable) throws Exception
	{
		ResultSet rs,rs2,rs3;
		Statement smt2=null;	
		rs = Variable.smt.executeQuery("SELECT A1.*,A2 .* 	FROM arrangedtable A1, availablecars A2 WHERE A1.carid = A2.���� AND A2. date = '"+Variable.date +"' AND A2.time= '" +Variable.time+"' AND A1. date = '" + Variable.date + "' AND A1.arrangetime = '" + Variable.time + "' GROUP BY A1.`carid` ORDER BY A1.worktime ASC");
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
			DriverNode.CallNum= rs.getString("�I��").trim();
			DriverNode.Address = rs.getString("�a�}").trim();
			DriverNode.RestTime1 = rs.getString("resttime1").trim();				
			DriverNode.station=rs.getString("���W").trim();	
			DriverNode.InitialInterval(DriverNode,rs.getString("�ɬq").trim(), timeinterval,Variable);	
			DriverNode.Holiday=holiday;
			smt2 = Variable.con.createStatement();
			//�^�_�������A				
			for(int i = 1; i < 17; i++)
			{
				//Ū���Z����ƨæ^�_Node����timeinterval
				if(rs.getInt("run" + String.valueOf(i)) != -1&&rs.getInt("run" + String.valueOf(i)) != 0)
				{  
					String information = rs.getString("user"+ String.valueOf(i));
					String[] testnumber = information.split("_");
					int informationNum = Integer.valueOf(testnumber[0]);
					rs2 = smt2.executeQuery("SELECT ��F�ɶ�, �ɬq,�U���ϰ� FROM userrequests WHERE arrangedate = '" + Variable.date + "' AND arrangetime = '" +Variable.time + "' AND �ѧO�X ='" + informationNum+"'");
					if(rs2.next())
					{
						String temptime = rs2.getString("�ɬq");
						int temphour = Integer.valueOf(temptime.substring(0, 2));	//hour = XX
						int tempmin = Integer.valueOf(temptime.substring(2, 4));		//min = YY
						int tempstartsec = temphour * 3600 + tempmin * 60;		//�_�I�ɶ�
						int tempendsec =rs2.getInt("��F�ɶ�");
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
			int starttime = DriverNode.StartTime + Variable.tolerableStartTime;//�Y�Z�ɶ�
			int endtime =DriverNode.EndTime + Variable.tolerableEndTime;//�߯Z�ɶ�:�X�Ԯɶ�+45��			
			//�p���Y�Z�϶�
			int StartInterval = (int)(starttime / Variable.IntervalSec);//�W���ɶ��b�@�Ѥ���interval index
			//�p����Z�϶�
			int EndInterval=((endtime% Variable.IntervalSec)  > 0.0 ? (int)(endtime / Variable.IntervalSec) : (int)(endtime / Variable.IntervalSec)-1);
			//if(DriverNode.Holiday==0)
			//{
				DriverNode.HalfWorkTimeInterval=(StartInterval+EndInterval)/2;
				flag[0]=Variable.Check(StartInterval,DriverNode,0,DriverNode.HalfWorkTimeInterval);
				flag[1]=Variable.Check(EndInterval,DriverNode,1,DriverNode.HalfWorkTimeInterval);
			
				//�]�w���L�Y���Z���X��
				if(flag[0]==true)
					DriverNode.StartArrange=true;
				if(flag[1]==true)
					DriverNode.EndArrange=true;
			/*}
			else
			{
				//�����Y���Z��ʱƤJ
				DriverNode.StartArrange=true;
				DriverNode.EndArrange=true;				
			}*/
			
			car.get(defineVariable.switchareaindex(DriverNode.station)).addCar(DriverNode,(int)(DriverNode.StartTime)/1800);
		}while(rs.next());	
		carsize=size;
		return car;
		
	}
	//Ū��������
	public  List<carGroup> readDrivertable(Connection con, String arrangedate, String arrangetime,Statement smt, List<carGroup> car
			,ILF ilf,Map<Integer,RequestTable> IndexMap,defineVariable Variable,List<reqGroup> requestTable) throws Exception
	{
		ResultSet rs,rs3;	
		rs = smt.executeQuery("SELECT A1.*,A2 .* 	FROM arrangedtable A1, availablecars A2 WHERE A1.carid = A2.���� AND A2. date = '"+arrangedate +"' AND A2.time= '" +arrangetime+"' AND A1. date = '" + arrangedate + "' AND A1.arrangetime = '" + arrangetime + "' GROUP BY A1.`carid` ORDER BY A1.worktime ASC");
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
			DriverNode.Address = rs.getString("�a�}").trim();
			DriverNode.CallNum= rs.getString("�I��").trim();
			DriverNode.RestTime1 = rs.getString("resttime1").trim();			
			DriverNode.station=rs.getString("���W").trim();	
			boolean[] flag={false,false};
			DriverNode.InitialInterval(DriverNode,rs.getString("�ɬq").trim(), timeinterval,Variable);				
			DriverNode.Holiday=holiday;
			//�^�_�������A				
			for(int i = 1; i < 17; i++)
			{
				//Ū���Z����ƨæ^�_Node����timeinterval
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
					int tempstartsec =tableIndex.originalStartTime;		//�_�I�ɶ�
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
			int starttime = DriverNode.StartTime + Variable.tolerableStartTime;//�Y�Z�ɶ�
			int endtime =DriverNode.EndTime + Variable.tolerableEndTime;//�߯Z�ɶ�:�X�Ԯɶ�+45��	
			//�]�w�w�w�Y���Z�ɶ�
			DriverNode.startreqtime=starttime;
			DriverNode.endreqtime=endtime;
			//�p���Y�Z�϶�
			int StartInterval = (int)(starttime / Variable.IntervalSec);//�W���ɶ��b�@�Ѥ���interval index
			//�p����Z�϶�
			int EndInterval=(endtime/Variable.IntervalSec);
			DriverNode.HalfWorkTimeInterval=(StartInterval+EndInterval)/2;
			flag[0]=Variable.Check(StartInterval,DriverNode,0,DriverNode.HalfWorkTimeInterval);
			flag[1]=Variable.Check(EndInterval,DriverNode,1,DriverNode.HalfWorkTimeInterval);
			if(DriverNode.Holiday==0)
			{
				//����
				//�����Y���Z�N�ߧY����
				if((flag[0]==false||flag[1]==false))
				{
					Variable.errorcode=-6;
					System.out.println(DriverNode.CallNum+"�L�Y���Z");
					break;
				}
				
			}			
			//�����Y���Z�ɶ�		
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
					System.out.println(DriverNode.CallNum+"�L�Y���Z");
					break;
				}
			}
			
			
			 //�u�ɤp��6�p�ɤ���𮧮ɶ�
			if((DriverNode.EndTime-DriverNode.StartTime)>Variable.nonrelax)
			{
				//�ھڸ�Ʈw�ƯZ���A��l�ƥi�Կ�𮧪��϶�
				DriverNode=initializeRelaxarray(DriverNode,timeinterval,IndexMap,Variable,ilf,intervalCount);
			}
			
			
			//�o��google map api���~�ߧY����
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
		startindex=(int)((DriverNode.startreqtime+1800) /(int) intervalSec);//�p���Y�Z�Ҧb�϶�				
		endindex=((DriverNode.endreqtime-1800) / (int)intervalSec);	//�p����Z�Ҧb�϶�		
		String tempreq=null;
		//�N�����b�𮧮ɶ������ƯZ�϶����[��i�Կ�𮧮ɶ�array
		for(int i=startindex;i<=endindex;i++)
		{
			if(DriverNode.TimeInterval[i].indexOf("���ƯZ")!=-1)
			{
				DriverNode.relaxarry.add(String.valueOf(i));//���N�Ҧ����b�i�𮧮ɶ������𮧰϶��[��array
			}
		}
		
		//�N���뤧�������ƤJ�ˬd�O�_�i�H�����𮧰϶�
		for(int i=startindex;i<=endindex;i++)
		{
			//��즳�ƯZ���϶�
			if(!(DriverNode.TimeInterval[i].indexOf("���W�Z")!=-1) 
					&&!(DriverNode.TimeInterval[i].indexOf("���ƯZ")!=-1)
					&&!(DriverNode.TimeInterval[i].indexOf("��")!=-1))
			{
				if(tempreq==null||!(tempreq.indexOf(DriverNode.TimeInterval[i])!=-1))
				{
					tempreq=DriverNode.TimeInterval[i];
					//��X��U���ݨD
					RequestTable tagetreq = Variable.RequestTableQuery(DriverNode.TimeInterval[i],Variable,IndexMap);
					//�W�@�몺�w����
					RequestTable tableindex = Variable.PreRequestTableQuery(DriverNode,i,Variable,IndexMap);	
					//��X�W�@�Z���ƯZ���϶��Ȧ�ɶ�					
					int [] traveltime=Variable.DistanceTime(tableindex,tagetreq,ilf,Variable,intervalSec);
					if(traveltime[1]<=-2)
					{
						Variable.errorcode=traveltime[1];
						break;
					}
					//�[�W�e��
					int temptraveltime=traveltime[1]+600;
					
					//�p�G�W�@��P�P��e��䤤�@�ӹJ��y�p�ɨ�n�[�Wdelay time
					if((tagetreq.originalStartTime>=27600&&tagetreq.originalStartTime<=31200)||(tableindex.originalDestinationTime>=27600&&tableindex.originalDestinationTime<=31200))
						temptraveltime+=900;                       //���W7:30~8:30�y�p�ɬq�A�[�W15������
					if(tagetreq.originalStartTime>=60900&&tagetreq.originalStartTime<=67200||(tableindex.originalDestinationTime>=60900&&tableindex.originalDestinationTime<=67200))
						temptraveltime+=1200;                      //�U��16:45~18:30�y�p�ɬq�A�[�W20������
					
					
					
					//�W�@��U���ҳѤU�i�����ɶ�
					int tempSecond=(tableindex.originalDestinationTime-(tableindex.originalDestinationTime%60));
                    int min = (int)((tempSecond % 3600) / 60);//��Ʀ���
					//�����i�δݾl���ɶ�
					int Residualtime=0;
					if(min>=30&&min<60)
						 Residualtime=(60-min);
					else if(min<30&&min>=0)
						Residualtime=(30-min);
					temptraveltime=temptraveltime-(Residualtime*60);					
					
					//�o��W���ҳѪ��ɶ�
					tempSecond=(tagetreq.originalStartTime-(tagetreq.originalStartTime%60));
					min=0;
					min = (int)((tempSecond % 3600) / 60);//��Ʀ���
					Residualtime=0;
					if(min==30||min==0)
						Residualtime=0;
					else if(min<30)
						Residualtime=min;
					else if(min>30)
						Residualtime=(min-30);
					
					//�����i�δݾl���ɶ�
					temptraveltime=temptraveltime-(Residualtime*60);
					
					//�p��n��O�����
					int Spendtimecount=((temptraveltime % intervalSec) > 0.0 ? ((int)(temptraveltime/ intervalSec))+1 : (int)(temptraveltime/ intervalSec));

					//�R���}���Ҫ�O�����
					for(int count=traveltime[0]+1;count<(traveltime[0]+Spendtimecount+1);count++)
					{
						if(count>=startindex&&count<=endindex)
							DriverNode.relaxarry.remove(String.valueOf(count));//�R���}���Ҫ�O���ɶ�
					}
					
				
			}
			}
		}
		boolean found=false;
		//�ˬd�̫�@���̫�@�몺�U�@��
		for(int i=endindex;i<intervalCount;i++)
		{
			if(found)
			{
			  break;
			}
				
			if(Variable.errorcode<=-2)
				break;
			//��즳�ƯZ���϶�
			if(!(DriverNode.TimeInterval[i].indexOf("���W�Z")!=-1) 
					&&!(DriverNode.TimeInterval[i].indexOf("���ƯZ")!=-1)
					&&!(DriverNode.TimeInterval[i].indexOf("��")!=-1))
			{
				//�̫�@�몺�U�@��
				RequestTable tagetreq =  Variable.RequestTableQuery(DriverNode.TimeInterval[i],Variable,IndexMap);
				//�̫�@��
				RequestTable tableindex =Variable.PreRequestTableQuery(DriverNode,i,Variable,IndexMap);
				//System.out.print("\n�W�@��1"+tagetreq.Number);
				//System.out.print("\n�W�@��2"+tableindex.Number);
				//��X�𮧮ɶ������̫�@�몺�϶��Ȧ�ɶ�
				int [] traveltime=Variable.DistanceTime(tableindex,tagetreq,ilf,Variable,intervalSec);
				//�^��error code
				if(traveltime[1]<=-2)
				{
					Variable.errorcode=traveltime[1];
					break;
				}
				
				//�[�W�e��
				int temptraveltime=traveltime[1]+600;
				//�p�G�̫�@�몺�U�@��P�̫�@��䤤�@�ӹJ��y�p�ɨ�n�[�Wdelay time
				if((tagetreq.originalStartTime>=27600&&tagetreq.originalStartTime<=31200)||(tableindex.originalDestinationTime>=27600&&tableindex.originalDestinationTime<=31200))
					temptraveltime+=900;                       //���W7:30~8:30�y�p�ɬq�A�[�W15������
				if(tagetreq.originalStartTime>=60900&&tagetreq.originalStartTime<=67200||(tableindex.originalDestinationTime>=60900&&tableindex.originalDestinationTime<=67200))
					temptraveltime+=1200;                      //�U��16:45~18:30�y�p�ɬq�A�[�W20������
				
				//�W�@��U���ҳѤU�i�����ɶ�
				int tempSecond=(tableindex.originalDestinationTime-(tableindex.originalDestinationTime%60));
                int min = (int)((tempSecond % 3600) / 60);//��Ʀ���
			    //�����i�δݾl���ɶ�
				int Residualtime=0;
				if(min>=30&&min<60)
					 Residualtime=(60-min);
				else if(min<30&&min>=0)
					Residualtime=(30-min);
				temptraveltime=temptraveltime-(Residualtime*60);
				
				//�̫�@�몺�U�@��W���ҳѪ��ɶ�
				tempSecond=(tagetreq.originalStartTime-(tagetreq.originalStartTime%60));
				min = (int)((tempSecond % 3600) / 60);//��Ʀ���
				Residualtime=0;
				if(min==30||min==0)
					Residualtime=0;
				else if(min<30)
					Residualtime=min;
				else if(min>30)
					Residualtime=(min-30);
				//�����i�δݾl���ɶ�
				temptraveltime=temptraveltime-(Residualtime*60);
				int Spendtimecount=((temptraveltime % intervalSec) > 0.0 ? ((int)(temptraveltime/ intervalSec))+1 : (int)(temptraveltime/ intervalSec));
				//�R���}���Ҫ�O�����
				for(int count=traveltime[0]+1;count<(traveltime[0]+Spendtimecount+1);count++)
				{
					if(count>=startindex&&count<=endindex)
						DriverNode.relaxarry.remove(String.valueOf(count));//�R���}���Ҫ�O���ɶ�
				}
				found=true;
			}
		}
		
		//��l��temprelaxarry
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
		//�����e�@��user�N��
		String TempUserindex="null";	
		//�x�srun
		for(int index =0; index <node.TimeInterval.length; index++)
		{
			if(!(node.TimeInterval[index].indexOf("���W�Z")!=-1)&&!(node.TimeInterval[index].indexOf("���ƯZ")!=-1))
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
		String information = "����: " + ID;		
		String insertSQL = "insert into "+tableName+" (carid, timeinterval, resttime,resttime1, date, arrangetime, worktime, cartype, run1, run2, run3, run4, run5, run6, run7, run8, run9, run10, run11, run12, run13, run14, run15, run16, user1, user2, user3, user4, user5, user6, user7, user8, user9, user10, user11, user12, user13, user14, user15, user16) values ('";
		for(int i = 0; i < run.length; i++)
		{
			run[i] = -1;
		}
		for(int i = 0; i < TimeInterval.length; i++)
		{
			if(TimeInterval[i].equals("���W�Z") || TimeInterval[i].equals("��"))
			{
				information = information + ", ��";
			}
			else if(TimeInterval[i].equals("���ƯZ"))
			{
				information = information + ", ��";
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
		//���P
		insertSQL = insertSQL + ID + "',";
		//interval
		insertSQL = insertSQL + (double)24/TimeInterval.length + ",";
		//resttime
		insertSQL = insertSQL + RestTime + ",'";
		//resttime1
		insertSQL = insertSQL + RestTime1 + "', '";
		//���
		insertSQL = insertSQL + date + "', '";
		//�ɶ�
		insertSQL = insertSQL + time +"', ";
		//�u�@�ɬq
		insertSQL = insertSQL + StartTime + ",'";
		//����
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
		rs = Variable.smt2.executeQuery("SELECT * FROM `carbarn` WHERE `�����a�}`='"+node.Address+"'");
		if(rs.next())
		{
			node.Lat=Double.parseDouble(rs.getString("Lat").trim());
			node.Lon=Double.parseDouble(rs.getString("Lon").trim());
		}
		String[] worktime=time.split("~");	
		String[] onwork=worktime[0].split(":");
		String[] offwork=worktime[1].split(":");
		startSec=(Integer.parseInt(onwork[0])*3600)+(Integer.parseInt(onwork[1])*60);//�W�Z�ɶ��ഫ���
	    endSec =(Integer.parseInt(offwork[0])*3600)+(Integer.parseInt(offwork[1])*60);//�U�Z�ɶ��ഫ���;	
	    intervalSec = interval * 3600;
		
		for(int i = 0; i < node.TimeInterval.length; i++)
		{
			//-2�N��Ӯɬq���W�Z
			node.TimeInterval[i] = "���W�Z".trim();
		}		
		node.StartTime = startSec;
		node.EndTime = endSec;		
		node.halfworktime=(int)(((startSec+1800)+(endSec+2700))/2);
		startInterval = (int)((startSec+1800) / intervalSec);
		endInterval = (int)((endSec+2700) / intervalSec);
		
		for(int i = startInterval; i <= endInterval; i++)
		{
			//�W�Z�ɶ����|���ƯZ
			node.TimeInterval[i] = "���ƯZ".trim();
		}
		//�B�z�𮧮ɶ�
		/*for(int i = startInterval + (int)(restInterval/interval); i < startInterval + (int)(restInterval/interval) + (int)(1/interval); i++)
		{
			node.TimeInterval[i] = "��";
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
						case 0://�]��
							 AddFlag=true;
							break;
						case 1://�g��
							if(DriverList.get(areaindex).getCar(timeindex).get(index).station.indexOf("�g��")!=-1)
								 AddFlag=true;
							break;
						case 2://����
							if(DriverList.get(areaindex).getCar(timeindex).get(index).station.indexOf("����")!=-1)
								 AddFlag=true;
							break;
						case 3://���M
							if(DriverList.get(areaindex).getCar(timeindex).get(index).station.indexOf("���M")!=-1)
								 AddFlag=true;
							break;
						case 4://�s��
							if(DriverList.get(areaindex).getCar(timeindex).get(index).station.indexOf("�s��")!=-1)
								 AddFlag=true;
							break;
						 default: //�w�]����
							 AddFlag=true;
						}
						//�p�G�M�䪺�ɶ����渨�b�u�ɥH���N�[�i�h 
						 if(TimeInterval>DriverList.get(areaindex).getCar(timeindex).get(index).StartTimeInterval
						 &&TimeInterval<DriverList.get(areaindex).getCar(timeindex).get(index).EndTimeInterval)
							 if(DriverList.get(areaindex).getCar(timeindex).get(index).TimeInterval[TimeInterval].indexOf("���ƯZ")!=-1)
								if(AddFlag)
									OriginDriverTable.add(DriverList.get(areaindex).getCar(timeindex).get(index));
					  }
				}
			}
		return OriginDriverTable;
		}
	//Ū���Y�ӥq�����mList��filter�i�ϥ� carindex:�n��W��X�Ӫ�����
	public LinkedList <DriverTable> getareafilterDriverTable(int carindex,List<carGroup> DriverList,defineVariable Variable)
	 {
		LinkedList <DriverTable> OriginDriverTable = new LinkedList<DriverTable>();
		if(carindex<=Variable.areanum)//�ثe�u��4�ӯ�
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
	//Ū��4�j����
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
	//Ū���q�����mList��filter�i�ϥ�
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
	//���o�]�Z��
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
						 //�N��l�q���[�Jfilterlist
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
			//�����q���ק��m
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
	    //�ھڶǤJ���̾A�X�ƤJ������driverNumber�A��ʧ��㪺������
		//DriverList����l�q���� TargetDriver:�n�ƤJ���q�� tableName:�n�g�J����ƪ�W��
		public  void ModifyOriginDriverTable(defineVariable Variable,RequestTable Reqtable,double interval,DriverTable TargetDriver,String tableName, Map<Integer,RequestTable> indexmap,List<carGroup> car)
		{
			if(Reqtable.DestinationAddress.indexOf("�s�_������ϫئ���152��")!=-1)
				System.out.println("111");
			double	IntervalSec = interval * 3600;//�Ninterval���ɶ��Ѥp�ɬ�����ন�Ѭ����
			int	StartInterval = (int)( (Reqtable.originalStartTime) / IntervalSec);//�W���ɶ��b�@�Ѥ���interval index
			//�U���ɶ��b�@�Ѥ���interval index
			int	EndInterval = (Reqtable.originalDestinationTime / (int) IntervalSec);
			TargetDriver=car.get(TargetDriver.index[0]).getCar(TargetDriver.index[1]).get(TargetDriver.index[2]);
			if(Reqtable.AssignSharing!=-1)
			{
				//��AssignSharing������-1�ɡA�N����ƤJ�w�����@���ƯZ�AAssignSharing���ȥN���O�w�g�ƤJ�o���������w��index
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
				//�ק郞����Time Interval
				for(int i = StartInterval; i <= EndInterval; i++)
				{
					TargetDriver.TimeInterval[i]= intervalString;					
				}				
				//��s�q���i�Ϊ��𮧰϶�
				 //�u�ɤp��4�p�ɤ���𮧮ɶ�
				if((TargetDriver.EndTime-TargetDriver.StartTime)>14400)
					TargetDriver.relaxarry=new ArrayList<String>(TargetDriver.temprelaxarry);	
				
				 Reqtable.Targetdrivers=TargetDriver.ID;
				 AssignSharingreq.Targetdrivers=TargetDriver.ID;
				 try
				{
					 //��s��e�w���̻P�@���̪��ƯZ���A
					 String sql = "UPDATE "+tableName+" SET arranged = 1,Targetdrivers='"+Reqtable.Targetdrivers+"' WHERE �ѧO�X = '" + Reqtable.Number + "' AND arrangedate = '" + Variable.date + "' AND arrangetime = '" + Variable.time + "'";
					 Variable.smt.executeUpdate(sql);	
					 sql = "UPDATE "+tableName+" SET arranged = 1,Targetdrivers='"+AssignSharingreq.Targetdrivers+"' WHERE �ѧO�X = '" + AssignSharingreq.Number + "' AND arrangedate = '" + Variable.date + "' AND arrangetime = '" + Variable.time + "'";
					 Variable.smt.executeUpdate(sql);
			    }
				catch(Exception e)
				{
						
				}
			
			}
			else
			{
			    //�N��e�w���̼g�J�q���Z��
				for(int i = StartInterval; i <= EndInterval; i++)
				{
					TargetDriver.TimeInterval[i]=String.valueOf(Reqtable.Number);	
				}
				//��s�q���i�Ϊ��𮧰϶� �p��4�p�ɤ���𮧮ɶ�
				if((TargetDriver.EndTime-TargetDriver.StartTime)>14400)
					TargetDriver.relaxarry=new ArrayList<String>(TargetDriver.temprelaxarry);	
				Reqtable.Targetdrivers=TargetDriver.ID;
				try
				{
					//��s��e�w���̪��ƯZ���A
					String sql = "UPDATE "+tableName+" SET arranged = 1,Targetdrivers='"+Reqtable.Targetdrivers+"' WHERE �ѧO�X = '" + Reqtable.Number + "' AND arrangedate = '" + Variable.date + "' AND arrangetime = '" + Variable.time + "'";
					Variable.smt.executeUpdate(sql);		
				}
				catch(Exception e)
				{
				
				}
			}
			//��s�q���Z��
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
						", `���~�I1`='"+Order[0]+"', `���~�I2`='"+Order[1]+"' WHERE `date`= '" + variable.date + "' AND arrangetime = '" + variable.time+"' AND `AssignSharing`='"+reqinfo+"'";
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
				//�Y�Z
				for(int index=Interval;index<=Interval+3;index++)
				{
					if(!(Driver.TimeInterval[index].indexOf("���W�Z")!=-1)
						&&!(Driver.TimeInterval[index].indexOf("���ƯZ")!=-1)
						&&!(Driver.TimeInterval[index].indexOf("��")!=-1))
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
				//���Z
				for(int index=Interval;index>=Interval-3;index--)
				{
					if(!(Driver.TimeInterval[index].indexOf("���W�Z")!=-1)
						&&!(Driver.TimeInterval[index].indexOf("���ƯZ")!=-1)
						&&!(Driver.TimeInterval[index].indexOf("��")!=-1))
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
