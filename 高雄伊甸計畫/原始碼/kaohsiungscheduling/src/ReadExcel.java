
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
	List<AssignSharingRequestTable> AssignSharingrequestTable=new ArrayList<AssignSharingRequestTable>(defineVariable.AssignSharingnum);//���w�@���ݨD��
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
	 *  ���~�N�X���������~
	 *  --------googleapi����-----------
	 *  
	 *  -2:�w�W�L����t�B
	 *  -3:�n�D�w�D�ڵ�
	 *  -4:���s�b��addres
	 *  -5:�d��(address��latlng)�򥢤F
	 *  ---------�w������------------
	 *  -6:�ݨD��ɶ����榡���~
	 *  -7:��F�ϰ�榡���~
	 *  -8:�ݨD��榡���~
	 *  --------��������-------------
	 *  -9:�ʤ֩I��!!
	 *  -10:�ʤ֤u�@�ɬq!!
	 *  -11:�ʤ֨��t�a�}!!
	 *  -12:�ʤ֥X�Z���!!
	 *  -13:�ʤ֥���ΰ��骺�аO!!
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
		//Ū��������		
		LinkedList<DriverTable> OriginDriverTable = new LinkedList<DriverTable>();
		Map<Integer, RequestTable> IndexMap = new HashMap<Integer,RequestTable>();
		Map<String, DriverTable> CarIndexMap = new HashMap<String, DriverTable>();
		int mode = Integer.valueOf(request.getParameter("mode"));
		
		int holiday=0;
		//boolean googleApiError = false;
		LinkInfo linkinfo = new LinkInfo();	
		try
		{
			//Ū����Ʈw
			Variable=new defineVariable();
			Class.forName("com.mysql.jdbc.Driver");	
			con = DriverManager.getConnection(linkinfo.getDatabaseURL() , linkinfo.getID(), linkinfo.getPW());
			smt = con.createStatement();	
			RecordAddress= new ArrayList<RecordAddress>();
			//���o���v�ץ��a�}
			RecordAddress=GetRecordAddress(RecordAddress,con,Variable);
			// ���o�ثe�ƯZ���
			Variable.rs = Variable.smt.executeQuery("SELECT * FROM `onlineuser` WHERE `IP`='"+linkinfo.getIpAddr(request)+"'");
			if(Variable.rs.next())
			{
				Department=Variable.rs.getString("Department").trim();
			}
			
			if(mode == 1)
			{
				LinkedList<RequestTable> RequestTable = new LinkedList<RequestTable>();
				//Ū���w����򨮽�����Ʈw
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
				//�M��Ȧ�ɶ�
				String sql="";
				int count=0;
				int recentPercent=0;
				List<reqGroup> RequestTable=new ArrayList<reqGroup>();	//�ݨD��
				for(int i=0;i<Variable.areanum;i++)
					RequestTable.add(new reqGroup(Variable.intervalnum));//��l�ƹw����
				System.out.println("mode = " + mode);				
				Variable.date= request.getParameter("arrangedate");
				Variable.time= request.getParameter("arrangetime");
				temp2[0]=Variable.date;
				temp2[1]=Variable.time;
				ILF smartSearch = new ILF(con,Variable);						
				//��l��Ū���w���o��ƪ���		
				ReadTempRequest input = new ReadTempRequest(Variable.con, Variable.date, Variable.time);
				//Ū��request�w����A
				RequestTable = input.ReadOrderTable(RequestTable,Variable);	
				//�^�ǹw���H��
				int reqsize=input.GetReqSize(Variable.smt);
				//�M��Ȧ�ɶ�
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
										 ",eLon="+RequestTable.get(areaindex).getreq(timeindex).get(index).DestinationLon+",��F�ɶ�="+RequestTable.get(areaindex).getreq(timeindex).get(index).DestinationTime+
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
					 //�M�䧹��
					 smt.executeUpdate("UPDATE progress SET `percent`=100 WHERE  `index`= '3' and `date`='"+Variable.date+"' and `time`='"+Variable.time+"'");
					 DriverTable InsertDriverTable=new DriverTable(Variable.intervalnum);
					 OriginDriverTable=InsertDriverTable.readDrivertable(Variable.con,Variable.date,Variable.time,Variable.smt,Variable,OriginDriverTable);
					 //�p�G���R��N�簣
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
			System.out.println("�w�����Ū������...");
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
	//�פJ�w����
	public LinkedList<RequestTable> ImportReqToDatabase(LinkInfo linkinfo,String reqFileName,Statement smt,LinkedList<RequestTable> RequestTable,Map<String, DriverTable> CarIndexMap)		
	{
		try 
		{
			String insertSQL = "insert into userrequests (`�ѧO�X`,`arrangedate` ,`arrangetime`,`Reservationnumber`,`Targetdrivers`,`���A`,"+
								"`�@���N�@`,`�m�W`,`�b��`,`telephone`,`level`,`�٧O`,`VisuallyImpaired`,`Wheelchair`,`�ɬq`,`�W���ϰ�`,"+
								 "`�W���a�}`,`�U���ϰ�`,`�U���a�}`,`�q���ɶ�`,`Customcar`,`Waiting`,subsidizeNumber,`����`,`��F�ɶ�`,`GETONRemark`,"+
								 "`OffCarRemark`,`sLat`,`sLon`,`eLat`,`eLon`,`arranged`,startbehaviorrecordindex,endbehaviorrecordindex) values ('";
			//Ū��excel�A�P�ɦb�O���餤�Φ�request table array�H�αN��Ʀs�J��Ʈw��			
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
					//�Φ�insert�@��request��ƶi��Ʈw����SQL�y�k
					String temp = "";
					
					temp = temp + insertSQL + (i+last_number) ;		
					reqNode.Number=(i+last_number);
					//�ƯZ���
					temp = temp + "', '" + Variable.date;
					//�ƯZ�ɶ�
					temp = temp + "', '" + Variable.time;				
					for(int j = 0; j <= 19; j++)
					{
						//�̧ǱNexcel�̪���ƶ׶iuserrequests	
						//��J����
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
									//��� ������P�w����I���藍�_��
									temp=temp +"', '"+"null";
									reqNode.Targetdrivers="null";
								}
										
							}		
						}//�N�a�}�P�Ƶ����}
						else if(j==13||j==15)
						{	
							tempNode=CheckAddress(RecordAddress,data[j].getContents());
							//�P�_�_�������v�ץ��a�}
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
								//�p�G�W�L1�ӱ׽u�N�N�᭱�r��֥[
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
							if(j==5)	//�����b��
							{
								reqNode.RequestNumber=data[j].getContents().trim();
							}
							if(j==11)
							{
								//errorcode=-6 �ɶ��榡���~
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
							//errorcode=-7 ��F�ϰ���~
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
					//���عw�]���j���A�a�}�̦��]�t�p���N�Ц��p��
					if(data[13].getContents().indexOf("�p��")!=-1||data[15].getContents().indexOf("�p��")!=-1)
						temp = temp + "','�p��";
					else
						temp = temp + "',' ";
					//`��F�ɶ�`,`GETONRemark`,"`OffCarRemark`,`sLat`,`sLon`,`eLat`,`eLon`,`arranged`
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
				   //Ū�����w�@�� �@���N�@������i�Χ_
				   if(!(data[3].getContents().trim().indexOf("�i")!=-1||data[3].getContents().trim().indexOf("�_")!=-1))
					{
					   AssignSharingRequestTable tempnode=new AssignSharingRequestTable();
					   tempnode.RequestNumber=reqNode.RequestNumber;//���������b��	
					   tempnode.originalStartTime=reqNode.DestinationTime;//���������W���ɶ�
					   tempnode.DestinationTime=reqNode.DestinationTime;//���������U���ɶ�
					   tempnode.Number=reqNode.Number;//���������ѧO�X		
					   tempnode.AssignSharingRequestNumber=data[3].getContents().trim();							
					   AssignSharingrequestTable.add(tempnode);
					}				
				
					//�s�W�@�����
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
			System.out.println("�פJ����");			
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
					StartInterval = (int)( RequestTable.get(index).OriginTime / 1800);//�w���ɶ��b�@�Ѥ���interval index
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
					if(Target.TimeInterval[StartInterval].indexOf("���ƯZ")!=-1||Target.TimeInterval[StartInterval].indexOf("���W�Z")!=-1)
					{
						Target.TimeInterval[StartInterval]=Infoation;	
						RequestTable.get(index).Arrange=true;
					}
					else
					{ 
						String[] testnumber = Target.TimeInterval[StartInterval].split("_");
						smt.executeUpdate("UPDATE userrequests SET `arranged` = -2 , `Targetdrivers`='null' WHERE `�ѧO�X` ="+RequestTable.get(index).Number+" and `arrangedate`='"+Variable.date+"' and `arrangetime`='"+Variable.time+"'");
						smt.executeUpdate("UPDATE userrequests SET `arranged` = -2 ,`Targetdrivers`='null' WHERE `�ѧO�X` ="+testnumber[0]+" and `arrangedate`='"+Variable.date+"' and `arrangetime`='"+Variable.time+"'");
						smt.executeUpdate("INSERT INTO writingerror (date,time,reqindex,carcallnum) VALUES ('"+Variable.date+"','"+Variable.time+"','"+RequestTable.get(index).Number+"','"+RequestTable.get(index).Targetdrivers+"')");
						smt.executeUpdate("INSERT INTO writingerror (date,time,reqindex,carcallnum) VALUES ('"+Variable.date+"','"+Variable.time+"','"+testnumber[0]+"','"+RequestTable.get(index).Targetdrivers+"')");
						if(testnumber.length>2)
						{
							smt.executeUpdate("UPDATE userrequests SET `arranged` = -2 WHERE `�ѧO�X` ="+ testnumber[1]+" and `arrangedate`='"+Variable.date+"' and `arrangetime`='"+Variable.time+"'");
							smt.executeUpdate("INSERT INTO writingerror (date,time,reqindex,carcallnum) VALUES ('"+Variable.date+"','"+Variable.time+"','"+testnumber[1]+"','"+RequestTable.get(index).Targetdrivers+"')");
						}
						
						Target.TimeInterval[StartInterval]="���ƯZ";
					}
				}			
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println(Index+"�X���F");
		}
		//�g�J��Ʈw
		for (Object key : CarIndexMap.keySet()) 
		{
            CarIndexMap.get(key).UpdateNode(Variable,CarIndexMap.get(key).RestTime1, CarIndexMap.get(key));
        }
	}
	//��a�}�[��ץ��a�}�̭�
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
			//�p�G�W�L1�ӱ׽u�N�N�᭱�r��֥[
			for(int Addressindex=1;Addressindex<StartAddress.length;Addressindex++)
				Node.Description+=Variable.clearNotChinese(StartAddress[Addressindex]);	
		}
		try 
		{
			//�s�W�m��Ʈw
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
	
	//����w�@�����е����Ʈw
	public void AssignSharingwritedatabase(List<AssignSharingRequestTable> req,Statement smt,String[] date,Map<Integer, RequestTable> IndexMap) throws IOException, SQLException
	{		
		String insertSQL=null;
		for(int i = 0; i < req.size(); i++)
		{
												
			insertSQL ="INSERT INTO travelinformationofcarsharing(AssignSharing, ���W����,date,arrangetime,�_�I,���~�I1,���~�I2,���I,arrivetime,starttime)VALUES (";
			insertSQL+="'"+req.get(i).Number+"_"+req.get(i).AssignSharingNumber+"' , ";//�w��������
			insertSQL+="'"+req.get(i).RequestNumber+"_"+req.get(i).AssignSharingRequestNumber+"' , ";//���W����
			insertSQL+="'"+date[0]+"' , '"+date[1]+"',";//���
			insertSQL+="'0_0' , "+"'0_0' , "+"'0_0' , "+"'0_0' ,";//�_�I,���~�I1,���~�I2,���I
			insertSQL+="0,"+req.get(i).originalStartTime+")";//arrivetime,starttime		
			IndexMap.get(req.get(i).Number).AssignSharingNumber=req.get(i).AssignSharingNumber;			
			smt.executeUpdate(insertSQL);
		}
	}
	public int checkHeader(Sheet sheet)		
	{
		int Column=-1;
		String[] Header={"�w���s��","����","���A","�@���N�@","�m�W","�b��",	"����","�٧O","����",
		    	"����","�ɬq","�W���ϰ�","�W���a�}","�U���ϰ�","�U���a�}","�q���ɶ�","�q���H��","��뤤�O�_���@���Ը�"};
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
	//���o�ץ��a�}�C��
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
	//���~�a�}�ץ�	
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

}
