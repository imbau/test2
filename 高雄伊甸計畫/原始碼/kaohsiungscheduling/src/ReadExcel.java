
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

/**
 * Servlet implementation class ReadExcel
 */
@WebServlet("/ReadExcel.view")
public class ReadExcel extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Workbook book;
    Sheet sheet;
	int errorcode=-1;
	boolean errorflag=false;	
	String Index="-1";
	defineVariable Variable;
	List<AssignSharingRequestTable> AssignSharingrequestTable=new ArrayList<AssignSharingRequestTable>(defineVariable.AssignSharingnum);//指定共乘需求表
	List<RecordAddress> RecordAddress;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReadExcel() {    	
        super();
      
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	
	/***********************************************
	 *  錯誤代碼對應的錯誤
	 *  --------googleapi部分-----------
	 *  
	 *  -2:已超過今日配額
	 *  -3:要求已遭拒絕
	 *  -4:不存在的addres
	 *  -5:查詢(address或latlng)遺失了
	 *  ---------預約表部分------------
	 *  -6:需求表時間欄位格式有誤
	 *  -7:行政區域格式有誤
	 *  -8:需求表格式有誤
	 *  --------車輛表部分-------------
	 *  -9:缺少呼號!!
	 *  -10:缺少工作時段!!
	 *  -11:缺少車廠地址!!
	 *  -12:缺少出班日期!!
	 *  -13:缺少平日或假日的標記!!
	 ***********************************************/
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// TODO Auto-generated method stub
		errorcode=-1;
		Index=String.valueOf(0);
	    errorflag=false;
		Connection con = null;
		Statement smt = null;
		String reqFileName = null, carFileName = null;
		String Department=new String();
		String[] temp2 = new String[2];
		//讀取車輛表		
		LinkedList<DriverTable> OriginDriverTable = new LinkedList<DriverTable>();
		Map<Integer, RequestTable> IndexMap = new HashMap<Integer,RequestTable>();
		Map<String, DriverTable> CarIndexMap = new HashMap<String, DriverTable>();
		int mode = Integer.valueOf(request.getParameter("mode"));
		
		int holiday=0;
		//boolean googleApiError = false;
		LinkInfo linkinfo = new LinkInfo();	
		try
		{
			//讀取資料庫
			Variable=new defineVariable();
			Class.forName("com.mysql.jdbc.Driver");	
			con = DriverManager.getConnection(linkinfo.getDatabaseURL() , linkinfo.getID(), linkinfo.getPW());
			smt = con.createStatement();	
			RecordAddress= new ArrayList<RecordAddress>();
			//取得歷史修正地址
			RecordAddress=GetRecordAddress(RecordAddress,con,Variable);
			// 取得目前排班單位
			Variable.rs = Variable.smt.executeQuery("SELECT * FROM `onlineuser` WHERE `IP`='"+linkinfo.getIpAddr(request)+"'");
			if(Variable.rs.next())
			{
				Department=Variable.rs.getString("Department").trim();
			}
			
			if(mode == 1)
			{
				LinkedList<RequestTable> RequestTable = new LinkedList<RequestTable>();
				//讀取預約表跟車輛表到資料庫
				System.out.println("Rmode = " + mode);			
				Variable.date= request.getParameter("arrangedate");
				Variable.time= request.getParameter("arrangetime");
				temp2[0]=Variable.date;
				temp2[1]=Variable.time;
				reqFileName = request.getParameter("requestfilename");
				carFileName = request.getParameter("carfilename");			
				
				smt.executeUpdate("INSERT INTO Notdischarged (`date`,`time`,`Normal`,`Candidate`)values('"+Variable.date+"','"+Variable.time+"',0,0)");		    
				DriverTable InsertDriverTable=new DriverTable(Variable.intervalnum);
				if(!carFileName.equals("no"))
				{
					
					 Index=String.valueOf(InsertDriverTable.insertDrivertable(Variable,carFileName));
					 holiday=InsertDriverTable.GetHoliday();
					 if(Variable.errorcode<=-9)
					 { 
						 errorflag=true;
						 errorcode=Variable.errorcode;
					 }
					 	
				}
				if(!errorflag)
				{
					//smt.executeUpdate("insert into arrange_log (date, time,company,VehicleTable,progress,TurnoutDate,holiday) values ('" + Variable.date + "', '" + Variable.time + "','"+Department+ "','"+carFileName+"',0,'"+InsertDriverTable.GetTurnoutDate()+"',0)");
					ReadInputfromExcel input = new ReadInputfromExcel(Variable);
					input.inicartable();
					OriginDriverTable=InsertDriverTable.readDrivertable(Variable.con,Variable.date,Variable.time,Variable.smt,Variable,OriginDriverTable);
					for(int Driverindex = 0; Driverindex < OriginDriverTable.size();Driverindex++ )
					{
						CarIndexMap.put(OriginDriverTable.get(Driverindex).CallNum, OriginDriverTable.get(Driverindex));
					}
					RequestTable=ImportReqToDatabase(linkinfo,reqFileName,smt,RequestTable,CarIndexMap);
					for(int Reqindex = 0; Reqindex < RequestTable.size();Reqindex++ )
					{
						IndexMap.put(RequestTable.get(Reqindex).Number, RequestTable.get(Reqindex));
					}
					List<AssignSharingRequestTable> AssignSharingpair=AssignSharingRequestTable.getpairarray(AssignSharingrequestTable);
					AssignSharingwritedatabase(AssignSharingpair,Variable.smt2,temp2,IndexMap);
					WriteDriverTable(AssignSharingpair,RequestTable,Variable,IndexMap,CarIndexMap,con);	
					for(int index=1;index<=7;index++)
						smt.executeUpdate("UPDATE progress SET `percent`=0 WHERE  `index`='"+index+"' and `date`='"+Variable.date+"' and `time`='"+Variable.time+"'");		
					smt.executeUpdate("insert into arrange_log (date, time,company,VehicleTable,Reqtable,progress,TurnoutDate,holiday,operationsnum) values ('" + Variable.date + "', '" + Variable.time + "','"+Department+ "','"+carFileName+"','"+reqFileName+"',0,'"+InsertDriverTable.GetTurnoutDate()+"',"+holiday+",0)");
				}
			}
			else if(mode == 2)
			{
				//尋找旅行時間
				String sql="";
				int count=0;
				int recentPercent=0;
				List<reqGroup> RequestTable=new ArrayList<reqGroup>();	//需求表
				for(int i=0;i<Variable.areanum;i++)
					RequestTable.add(new reqGroup(Variable.intervalnum));//初始化預約表
				System.out.println("mode = " + mode);				
				Variable.date= request.getParameter("arrangedate");
				Variable.time= request.getParameter("arrangetime");
				temp2[0]=Variable.date;
				temp2[1]=Variable.time;
				ILF smartSearch = new ILF(con,Variable);						
				//初始化讀取預約這資料物件		
				ReadTempRequest input = new ReadTempRequest(Variable.con, Variable.date, Variable.time);
				//讀取request預約表，
				RequestTable = input.ReadOrderTable(RequestTable,Variable);	
				//回傳預約人數
				int reqsize=input.GetReqSize(Variable.smt);
				//尋找旅行時間
				 for(int areaindex=0;areaindex<Variable.areanum;areaindex++)
				 { 				
					 for(int timeindex=0;timeindex<Variable.intervalnum;timeindex++)
					 { 
						 for(int index=0;index<RequestTable.get(areaindex).getreq(timeindex).size();index++)
						 {
							 int TravelTime=-1;							 
							 TravelTime=smartSearch.SearchHistory(RequestTable.get(areaindex).getreq(timeindex).get(index),Variable);
							if(Variable.errorcode<-1)	
							{
								errorflag=true;	
								errorcode=Variable.errorcode;
								Index=RequestTable.get(areaindex).getreq(timeindex).get(index).RequestNumber;
								break;
							}else
							{
								sql = "UPDATE userrequests SET sLat="+RequestTable.get(areaindex).getreq(timeindex).get(index).OriginLat+",sLon="
										 +RequestTable.get(areaindex).getreq(timeindex).get(index).OriginLon+",eLat="+RequestTable.get(areaindex).getreq(timeindex).get(index).DestinationLat+
										 ",eLon="+RequestTable.get(areaindex).getreq(timeindex).get(index).DestinationLon+",抵達時間="+RequestTable.get(areaindex).getreq(timeindex).get(index).DestinationTime+
										 ",`traveltimeindex`="+TravelTime+ " WHERE no="+RequestTable.get(areaindex).getreq(timeindex).get(index).RequestTableIndaex;
							    Variable.smt.addBatch(sql);									
								count++;
								
							}
							
						 }
						 if(errorflag)	
							 break;
						 else if((int)((count * 100) / reqsize) > recentPercent)
						 {
								recentPercent = (int)((count * 100) / reqsize );
								smt.executeUpdate("UPDATE progress SET `percent`=" + recentPercent + " WHERE   `index`= '3' and `date`='"+Variable.date+"' and `time`='"+Variable.time+"'");
						 }
					}
					 if(errorflag)	
						 break;
				 }
				 if(!errorflag)	
				 {	 
					 Variable.smt.executeBatch(); 		
					 //尋找完畢
					 smt.executeUpdate("UPDATE progress SET `percent`=100 WHERE  `index`= '3' and `date`='"+Variable.date+"' and `time`='"+Variable.time+"'");
					 DriverTable InsertDriverTable=new DriverTable(Variable.intervalnum);
					 OriginDriverTable=InsertDriverTable.readDrivertable(Variable.con,Variable.date,Variable.time,Variable.smt,Variable,OriginDriverTable);
					 //如果有沖趟就剔除
					 for(int Driverindex = 0; Driverindex < OriginDriverTable.size();Driverindex++ )
					 {
						 if(OriginDriverTable.get(Driverindex).writingerror!="0")
							 OriginDriverTable.get(Driverindex).UpdateNode(Variable,OriginDriverTable.get(Driverindex).RestTime1,  OriginDriverTable.get(Driverindex));
					 }
				}
			}
			else if(mode == 3)
			{
				System.out.println("mode = " + mode);	
				Variable.date= request.getParameter("arrangedate");
				Variable.time= request.getParameter("arrangetime");
				reqFileName = request.getParameter("requestfilename");				
				temp2[0]=Variable.date;
				temp2[1]=Variable.time;
				modifysubsidizeNumber(Variable.smt2, linkinfo, temp2[0],temp2[1],reqFileName);
				 errorflag=false;
				//smt.executeUpdate("delete FROM `progress` WHERE `date`='"+Variable.date+"' and `time`='"+Variable.time+"' and `no`!=0");
			}
		if(errorflag)	
			{
				smt.executeUpdate("delete FROM `availablecars` WHERE `date`='"+Variable.date+"' and `time`='"+Variable.time+"'");
				smt.executeUpdate("delete FROM `arrangedtable` WHERE `date`='"+Variable.date+"' and `arrangetime`='"+Variable.time+"'");
				smt.executeUpdate("delete FROM `progress` WHERE `date`='"+Variable.date+"' and `time`='"+Variable.time+"' and `no`!=0");
				smt.executeUpdate("delete FROM `userrequests` WHERE `arrangedate`='"+Variable.date+"' and `arrangetime`='"+Variable.time+"'");
				smt.executeUpdate("delete FROM `arrange_log` WHERE `date`='"+Variable.date+"' and `time`='"+Variable.time+"'");
				
			}
			System.gc();
		    reqFileName = null;
		    carFileName = null;	
		    RecordAddress=null;
			System.gc();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		PrintWriter out = response.getWriter();
		if(!errorflag)	
		{
			System.out.println("預約資料讀取完成...");
			if(mode < 3)
				out.println("success," +Variable.date + "," + Variable.time );	
			else if(mode==3)
				out.println("success," +Variable.date + "," + Variable.time +","+	temp2[0]+","+ 	temp2[1] );	
		}
		else
		{
			out.println(errorcode+"," +Variable.date + "," + Variable.time+","+Index);	
		}
	}
	//匯入預約表
	public LinkedList<RequestTable> ImportReqToDatabase(LinkInfo linkinfo,String reqFileName,Statement smt,LinkedList<RequestTable> RequestTable,Map<String, DriverTable> CarIndexMap)		
	{
		try 
		{
			String insertSQL = "insert into userrequests (`識別碼`,`arrangedate` ,`arrangetime`,`Reservationnumber`,`Targetdrivers`,`狀態`,"+
								"`共乘意願`,`姓名`,`帳號`,`telephone`,`level`,`障別`,`VisuallyImpaired`,`Wheelchair`,`時段`,`上車區域`,"+
								 "`上車地址`,`下車區域`,`下車地址`,`訂車時間`,`Customcar`,`Waiting`,subsidizeNumber,`車種`,`抵達時間`,`GETONRemark`,"+
								 "`OffCarRemark`,`sLat`,`sLon`,`eLat`,`eLon`,`arranged`,startbehaviorrecordindex,endbehaviorrecordindex) values ('";
			//讀取excel，同時在記憶體中形成request table array以及將資料存入資料庫中			
			int count = DataCount(linkinfo.getUploadLink() + reqFileName);	
			int last_number = 0;
			int recentPercent = -1;	
			int[] behaviorrecordindex={-1,-1};
			int count1= sheet.getColumns();
			String[] Remark={" "," "};
			RecordAddress tempNode = new RecordAddress();			
		    if(count1<16)
			{
		    	Index=String.valueOf(checkHeader(sheet));
		    	errorcode=-8;
			    errorflag=true;
			}
		    else
			{
		    	for(int i = 1; i < count; i++)
				{
					RequestTable reqNode = new RequestTable();
					Remark[0]="";
					Remark[1]="";
					Cell[] data =  sheet.getRow(i);				//read whole row data
					try
					{
						if(data[10].getContents().equals(""))
							break;
						
					}
					catch(Exception e)
					{
						break;
					}
					//形成insert一筆request資料進資料庫內的SQL語法
					String temp = "";
					
					temp = temp + insertSQL + (i+last_number) ;		
					reqNode.Number=(i+last_number);
					//排班日期
					temp = temp + "', '" + Variable.date;
					//排班時間
					temp = temp + "', '" + Variable.time;				
					for(int j = 0; j <= 19; j++)
					{
						//依序將excel裡的資料匯進userrequests	
						//填入車號
						if(j==1)
						{
							if(data[1].getContents().equals(""))
							{
								temp=temp +"', '"+"null";
								reqNode.Targetdrivers="null";
							}else
							{
								try
								{
									temp=temp +"', '"+CarIndexMap.get(Variable.clearNotChinese(data[1].getContents().trim())).ID;					
									reqNode.Targetdrivers=Variable.clearNotChinese(data[1].getContents().trim());
								}
								catch(Exception e)
								{
									//填錯 車輛表與預約表呼號對不起來
									temp=temp +"', '"+"null";
									reqNode.Targetdrivers="null";
								}
										
							}		
						}//將地址與備註分開
						else if(j==13||j==15)
						{	
							tempNode=CheckAddress(RecordAddress,data[j].getContents());
							//判斷否有找到歷史修正地址
							if(tempNode.originaladdress=="null")
							{
								tempNode=AddRecordAddress(RecordAddress, smt,data[j].getContents(),data[j-1].getContents());
							}
							if(j==13)
							{
								reqNode.OriginAddress =tempNode.area+tempNode.Address;
								//Remark[0]=tempNode.Description;						
								behaviorrecordindex[0]=tempNode.RecordAddressIndex;
							}
							else
							{
								reqNode.DestinationAddress=tempNode.area+tempNode.Address;
								//Remark[1]=tempNode.Description;
								behaviorrecordindex[1]=tempNode.RecordAddressIndex;
							}
							String[] StartAddress={" "," "};		
							StartAddress=data[j].getContents().trim().split("/");
							StartAddress[0]=Variable.clearNotChinese(StartAddress[0]);						
							if(StartAddress.length>=2)
							{
								//如果超過1個斜線就將後面字串累加
								for(int Addressindex=1;Addressindex<StartAddress.length;Addressindex++)
								{
									if(j==13)
										Remark[0]+=Variable.clearNotChinese(StartAddress[Addressindex]);	
									else										
										Remark[1]+=Variable.clearNotChinese(StartAddress[Addressindex]);	
								}
							}
					        temp = temp + "', '" +StartAddress[0];							
						}
						else							
						{	
							if(j==5)	//紀錄帳號
							{
								reqNode.RequestNumber=data[j].getContents().trim();
							}
							if(j==11)
							{
								//errorcode=-6 時間格式錯誤
								if(data[j].getContents().trim().length()<4||data[j].getContents().trim().length()>4)
								{
									errorcode=-6;
									Index=String.valueOf(i);
									errorflag=true;
									break;
								}
								else
								{
									reqNode.OriginTime=Integer.valueOf(data[j].getContents().substring(0, 2))*3600+Integer.valueOf(data[j].getContents().substring(2, 4))*60;
									reqNode.DestinationTime=reqNode.OriginTime;		
								}
							}
							//errorcode=-7 行政區域錯誤
							if(j==12||j==14)
							{
								if(defineVariable.switchareaindex(data[j].getContents().trim())==-1)
								{
									errorcode=-7;
									Index=String.valueOf(i);
									errorflag=true;
									break;
								}
							}							
							temp = temp + "', '" + data[j].getContents().trim();
						}
					}
					//車種預設為大車，地址裡有包含小車就標成小車
					if(data[13].getContents().indexOf("小車")!=-1||data[15].getContents().indexOf("小車")!=-1)
						temp = temp + "','小車";
					else
						temp = temp + "',' ";
					//`抵達時間`,`GETONRemark`,"`OffCarRemark`,`sLat`,`sLon`,`eLat`,`eLon`,`arranged`
					temp = temp + "', '" +reqNode.DestinationTime;
					temp = temp + "', '" +Remark[0];
					temp = temp + "', '" +Remark[1];
					for(int j = 0; j < 4; j++)
					{
						temp = temp + "', '" +"-1";
					}					
				    if(reqNode.Targetdrivers.indexOf("null")!=-1)
						temp = temp +"',-1,"+behaviorrecordindex[0]+","+behaviorrecordindex[1]+")";	
					else
						temp = temp +"',1,"+behaviorrecordindex[0]+","+behaviorrecordindex[1]+")";	
				   //讀取指定共乘 共乘意願不等於可或否
				   if(!(data[3].getContents().trim().indexOf("可")!=-1||data[3].getContents().trim().indexOf("否")!=-1))
					{
					   AssignSharingRequestTable tempnode=new AssignSharingRequestTable();
					   tempnode.RequestNumber=reqNode.RequestNumber;//紀錄本身帳號	
					   tempnode.originalStartTime=reqNode.DestinationTime;//紀錄本身上車時間
					   tempnode.DestinationTime=reqNode.DestinationTime;//紀錄本身下車時間
					   tempnode.Number=reqNode.Number;//紀錄本身識別碼		
					   tempnode.AssignSharingRequestNumber=data[3].getContents().trim();							
					   AssignSharingrequestTable.add(tempnode);
					}				
				
					//新增一筆資料
				   Variable.smt2.executeUpdate(temp);
				  // 	Variable.smt2.addBatch(temp);
					RequestTable.add(reqNode);
					if((int)((i * 100) / count) > recentPercent)
					{
						recentPercent = (int)((i * 100) /  count);
						smt.executeUpdate("	UPDATE progress SET `percent`=" + recentPercent + " WHERE  `index`= '8' and `date`='"+Variable.date+"' and `time`='"+Variable.time+"'");
					}
				}
			}
		  //  Variable.smt2.executeBatch(); 
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("匯入失敗");			
		}
		 return RequestTable;
	}
	public void modifysubsidizeNumber(Statement smt2,LinkInfo linkinfo,String date,String time,String reqFileName)
	{
		int count = DataCount(linkinfo.getUploadLink() + reqFileName);	
		int count1= sheet.getColumns();
		  if(count1<16)
			{
			  Index=String.valueOf(checkHeader(sheet));
		    	errorcode=-8;
			    errorflag=true;
			}
		    else
		    {
		    	for(int i = 1; i < count; i++)
				{				
					Cell[] data =  sheet.getRow(i);				//read whole row data
					
					try {
						smt2.execute("UPDATE `userrequests` SET  `subsidizeNumber`='"+data[19].getContents().trim()+"' WHERE `Reservationnumber`='"+data[0].getContents().trim()+"' and `arrangedate`='"+date+"' and `arrangetime`='"+time+"'");
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						errorcode=-15;
					}
				}
		    }
	}
	public void WriteDriverTable(List<AssignSharingRequestTable> req,LinkedList<RequestTable> RequestTable,defineVariable Variable,Map<Integer, RequestTable> IndexMap,Map<String, DriverTable> CarIndexMap,Connection con)		
	{
		int StartInterval=0;	
		Statement smt = null;
		try
		{
			smt = con.createStatement();	
			for(int index = 0; index < RequestTable.size();index++ )
			{
				if(!RequestTable.get(index).Arrange)
				{					
					StartInterval = (int)( RequestTable.get(index).OriginTime / 1800);//預約時間在一天中的interval index
					if(RequestTable.get(index).Targetdrivers.indexOf("null")!=-1)
					{
						continue;
					}
					DriverTable Target=new DriverTable(48);	
					
					Target=CarIndexMap.get(RequestTable.get(index).Targetdrivers);				
					String Infoation="null";				
					if(RequestTable.get(index).AssignSharingNumber==-1)
					{
						Infoation=String.valueOf(RequestTable.get(index).Number);
					}
					else
					{
						Infoation=String.valueOf(RequestTable.get(index).Number+"_"+RequestTable.get(index).AssignSharingNumber);
						IndexMap.get(RequestTable.get(index).AssignSharingNumber).Arrange=true;
					}
					if(Target.TimeInterval[StartInterval].indexOf("未排班")!=-1||Target.TimeInterval[StartInterval].indexOf("不上班")!=-1)
					{
						Target.TimeInterval[StartInterval]=Infoation;	
						RequestTable.get(index).Arrange=true;
					}
					else
					{ 
						String[] testnumber = Target.TimeInterval[StartInterval].split("_");
						smt.executeUpdate("UPDATE userrequests SET `arranged` = -2 , `Targetdrivers`='null' WHERE `識別碼` ="+RequestTable.get(index).Number+" and `arrangedate`='"+Variable.date+"' and `arrangetime`='"+Variable.time+"'");
						smt.executeUpdate("UPDATE userrequests SET `arranged` = -2 ,`Targetdrivers`='null' WHERE `識別碼` ="+testnumber[0]+" and `arrangedate`='"+Variable.date+"' and `arrangetime`='"+Variable.time+"'");
						smt.executeUpdate("INSERT INTO writingerror (date,time,reqindex,carcallnum) VALUES ('"+Variable.date+"','"+Variable.time+"','"+RequestTable.get(index).Number+"','"+RequestTable.get(index).Targetdrivers+"')");
						smt.executeUpdate("INSERT INTO writingerror (date,time,reqindex,carcallnum) VALUES ('"+Variable.date+"','"+Variable.time+"','"+testnumber[0]+"','"+RequestTable.get(index).Targetdrivers+"')");
						if(testnumber.length>2)
						{
							smt.executeUpdate("UPDATE userrequests SET `arranged` = -2 WHERE `識別碼` ="+ testnumber[1]+" and `arrangedate`='"+Variable.date+"' and `arrangetime`='"+Variable.time+"'");
							smt.executeUpdate("INSERT INTO writingerror (date,time,reqindex,carcallnum) VALUES ('"+Variable.date+"','"+Variable.time+"','"+testnumber[1]+"','"+RequestTable.get(index).Targetdrivers+"')");
						}
						
						Target.TimeInterval[StartInterval]="未排班";
					}
				}			
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println(Index+"出錯了");
		}
		//寫入資料庫
		for (Object key : CarIndexMap.keySet()) 
		{
            CarIndexMap.get(key).UpdateNode(Variable,CarIndexMap.get(key).RestTime1, CarIndexMap.get(key));
        }
	}
	//把地址加到修正地址裡面
	public RecordAddress  AddRecordAddress(List<RecordAddress> RecordAddress,Statement smt,String address,String area) 
	{		
		ResultSet  rs=null;
		RecordAddress Node=new RecordAddress();
		String[] StartAddress={" "," "};		
		Node.area=area.trim();
		Node.originaladdress=address.trim();
		StartAddress=Node.originaladdress.split("/");
		Node.Address=Variable.clearNotChinese(StartAddress[0]);	
		if(StartAddress.length>=2)
		{
			//如果超過1個斜線就將後面字串累加
			for(int Addressindex=1;Addressindex<StartAddress.length;Addressindex++)
				Node.Description+=Variable.clearNotChinese(StartAddress[Addressindex]);	
		}
		try 
		{
			//新增置資料庫
			smt.executeUpdate("INSERT INTO `behaviorrecord`(`originalarea`, `originaladdress`, `area`, `Address`, `Description`) VALUES ('"+area+"','"+Node.originaladdress+"','"+area+"','"+Node.Address+"','"+Node.Description+"')");
			//get behaviorrecord index
			rs = Variable.smt.executeQuery("SELECT * FROM `behaviorrecord` WHERE `originaladdress`='"+Node.originaladdress+"'");
			if(rs.next())
			{
				Node.RecordAddressIndex=rs.getInt("no");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return Node;		
	}
	
	//把指定共乘的標註到資料庫
	public void AssignSharingwritedatabase(List<AssignSharingRequestTable> req,Statement smt,String[] date,Map<Integer, RequestTable> IndexMap) throws IOException, SQLException
	{		
		String insertSQL=null;
		for(int i = 0; i < req.size(); i++)
		{
												
			insertSQL ="INSERT INTO travelinformationofcarsharing(AssignSharing, 車上乘員,date,arrangetime,起點,中繼點1,中繼點2,終點,arrivetime,starttime)VALUES (";
			insertSQL+="'"+req.get(i).Number+"_"+req.get(i).AssignSharingNumber+"' , ";//預約表格欄位
			insertSQL+="'"+req.get(i).RequestNumber+"_"+req.get(i).AssignSharingRequestNumber+"' , ";//車上乘員
			insertSQL+="'"+date[0]+"' , '"+date[1]+"',";//日期
			insertSQL+="'0_0' , "+"'0_0' , "+"'0_0' , "+"'0_0' ,";//起點,中繼點1,中繼點2,終點
			insertSQL+="0,"+req.get(i).originalStartTime+")";//arrivetime,starttime		
			IndexMap.get(req.get(i).Number).AssignSharingNumber=req.get(i).AssignSharingNumber;			
			smt.executeUpdate(insertSQL);
		}
	}
	public int checkHeader(Sheet sheet)		
	{
		int Column=-1;
		String[] Header={"預約編號","車號","狀態","共乘意願","姓名","帳號",	"等級","障別","視障",
		    	"輪椅","時段","上車區域","上車地址","下車區域","下車地址","訂車時間","訂車人員","兩趟中是否有一為候補"};
		for(int i=0;i<sheet.getColumns();i++)
		{
			Cell[] data =  sheet.getColumn(i);
			if(!(data[0].getContents().trim().indexOf(Header[i])!=-1))
			{
				Column=i+1;
				break;
			}
		}
		return Column;
	}
	//取得修正地址列表
	public List<RecordAddress>GetRecordAddress(List<RecordAddress> RecordAddress,Connection conn,defineVariable variable)						
	{
		try 
		{
			ResultSet rs = null;
			Statement smt=conn.createStatement();
			rs = smt.executeQuery("SELECT * FROM `behaviorrecord` WHERE 1");
			while(rs.next())
			{	
				RecordAddress tempNode = new RecordAddress();				
				tempNode.originalarea = rs.getString("originalarea").trim();
				tempNode.Address = rs.getString("Address").trim();
				tempNode.originaladdress = rs.getString("originaladdress").trim();
				tempNode.Description = rs.getString("Description").trim();
				tempNode.area = rs.getString("area").trim();
				tempNode.RecordAddressIndex=rs.getInt("no");
				RecordAddress.add(tempNode);
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
		    e.printStackTrace();
		}
		return RecordAddress;
	}
	//錯誤地址修正	
	public RecordAddress CheckAddress(List<RecordAddress> RecordAddress,String address)						
	{
		RecordAddress recordAddress=new RecordAddress();
		recordAddress.originaladdress="null";
		for(int i = 0; i < RecordAddress.size(); i++)
		{
			if(RecordAddress.get(i).originaladdress.trim().equals(address.trim()))
			{
				recordAddress=RecordAddress.get(i);
				break;
			}
		}
		return recordAddress;
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

}
