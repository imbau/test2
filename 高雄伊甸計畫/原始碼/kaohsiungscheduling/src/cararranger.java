import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class cararranger
 */
@WebServlet("/cararranger.view")
public class cararranger extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private String Lat = null, Lon = null;
    private int X = -1, Y = -1;
    defineVariable Variable;	
    List<reqGroup> requestTable;	//�ݨD��
	//�N�Ҧ��ݨD��mIndexMap�H�Q�d�ߤW�@�Z�ΤU�@�Z�w���̪���T
    Map<Integer, RequestTable> IndexMap = new HashMap<Integer, RequestTable>(); 
    //List<carGroup> car = new ArrayList<carGroup>();//������	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public cararranger() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		response.setContentType("text/html;charset=UTF-8");		
		String orderhour = null;
		String ordermin = null;
		String startarea = null, startadd = null, endarea = null, endadd = null;
		int IntervalSec = (int)(0.5 * 3600);
		String ordertime = null;
		String carid = null;
		String cartype = null;
		String sLat = null, sLon = null, eLat = null, eLon = null;
		String name = null;
		String Disabilities = null;
		String account = null;	
		String startRemark="null";
		String endRemark="null";
		int sX = -1, sY = -1, eX = -1, eY = -1;
		int status = -1;
		int sharing = 0;		
		int traveltime = -1;
		int startsec =-1;
		int endsec =-1 ;			
		int flag=0;
		int reqnum=-1;	
		int operationsnum=0;
		
	    try
	    {
	    	System.out.println("start");
	    	Variable = new defineVariable();
	    	Variable.date = request.getParameter("arrangedate");
	    	Variable.time = request.getParameter("arrangetime");	    	
	    	//�b��
	    	account = request.getParameter("account").trim();
	    	//�W�U���ϰ�Φa�}
	    	startarea = request.getParameter("startarea").trim();
	    	startadd = request.getParameter("startadd").trim();	    	
	    	endarea = request.getParameter("endarea").trim();
	    	endadd = request.getParameter("endadd").trim();
    		carid = request.getParameter("carid").trim();     	
    		requestTable=new ArrayList<reqGroup>();	//�ݨD��
    		for(int i=0;i<Variable.areanum;i++)
			    requestTable.add(new reqGroup(Variable.intervalnum));//��l�ƹw����H�W���ϰ�
    		//��l��Ū���w���o��ƪ���		
			ReadTempRequest input = new ReadTempRequest(Variable.con, Variable.date, Variable.time);
			//Ū��request�w����
			requestTable = input.ReadOrderTable(requestTable,Variable);	
		    //�N�Ҧ��w���̩�Jmap
			 for(int j=0;j<Variable.areanum;j++)
			 { 				
				 for(int l=0;l<Variable.intervalnum;l++)
				 { 
					 for(int k=0;k<requestTable.get(j).getreq(l).size();k++)
					 {
					  IndexMap.put(requestTable.get(j).getreq(l).get(k).Number,requestTable.get(j).getreq(l).get(k));	
					 }
				}
			}
    		Variable.rs = Variable.smt.executeQuery("SELECT * from userrequests WHERE `�ѧO�X`="+account+" AND arrangedate = '" + Variable.date + "' AND arrangetime = '" + Variable.time + "'");
    		if(Variable.rs.next())
	    	{
	    	
	    		//��l��Ū���w���o��ƪ���		
				//ReadTempRequest input = new ReadTempRequest(Variable.con, Variable.date, Variable.time);
	    		RequestTable req;
	    		reqnum=Integer.valueOf(Variable.rs.getString("�ѧO�X"));
	    		req= IndexMap.get(reqnum);
	    		//req=input.buildRequestNode(Variable.rs);	
	    		startsec =req.OriginTime;
	    	    endsec =req.DestinationTime ;	    	     
	    	   
	    	    if(req.AssignSharing==-1)
	    	    	sharing=Integer.valueOf(request.getParameter("sharing"));
	    	    else
	    	    	sharing=req.AssignSharing;
	    	    
	    		//�{����ƵL�@���Q�n�@��
	    	    if(sharing>0)
	    	    {	
	    	    	req.AssignSharing=sharing;
	    	    	//�P�_�@�����ӥ��W��
	    	    	if(IndexMap.get(sharing).OriginTime>req.OriginTime)
	    	    		startsec =req.OriginTime;
	    	    	else
	    	    		startsec =IndexMap.get(sharing).OriginTime;
	    	    	//�P�_�@�����ӳ̫�U��
	    	    	if(IndexMap.get(sharing).DestinationTime>req.DestinationTime)
	    	    		endsec =IndexMap.get(sharing).DestinationTime;
	    	    	else
	    	    		endsec =req.DestinationTime;
	    	    	
		        }
	    	    flag=1;	    		
	    	}
	    	else
	    	{
	    		//����
	    		cartype = request.getParameter("cartype").trim();
	    		RequestTable req=new RequestTable();	    		
	    		//�W�r
	    		name = request.getParameter("name");
	    		//�٧O
	    		Disabilities = request.getParameter("Disabilities");
	    		//���A
	    		status = Integer.valueOf(request.getParameter("status"));
	    		//�@��
	    		sharing = Integer.valueOf(request.getParameter("sharing"));
	    		//��q�ɶ�
	    		req.TravelTime= Integer.valueOf(request.getParameter("traveltime"));
	    		//�W���Ƶ�
	    		startRemark=request.getParameter("startRemark");
	    		//�U���Ƶ�
	    		endRemark=request.getParameter("endRemark");	
	    		//�b��
	    		req.RequestNumber=account;
	    		//�W���ϰ�
	    		req.Originarea=startarea;	    		
		        //�U���ϰ�
	    		req.Destinationarea=endarea;		    	
	    		traveltime = Integer.valueOf(request.getParameter("traveltime"));
	    		//�B�z�w���ɶ�	    	
	    		orderhour = request.getParameter("orderhour");	    		
	    		ordermin = request.getParameter("orderminute");
	    	    startsec = Integer.valueOf(orderhour) * 3600 + Integer.valueOf(ordermin) * 60;
	    	    req.OriginTime=startsec;
	    	    endsec = startsec + traveltime;		    	    
	    	    req.DestinationTime=endsec;
	    	    
	    		if(Integer.valueOf(orderhour) < 10)
	    		{
	    			orderhour = "0" + orderhour;
	    			}
	    		if(Integer.valueOf(ordermin) < 10)
	    		{
	    			ordermin = "0" + ordermin;
	    			}
	    		ordertime = orderhour + ordermin;	    		
	    		//�B�z�a�}�g�n�ץH��xy�y��
	    		GetGeocode(Variable.smt, startarea, startadd);
	    		sLat = Lat;
	    		sLon = Lon;
	    		sX = X;
	    		sY = Y;
	    		GetGeocode(Variable.smt, endarea, endadd);
	    		eLat = Lat;
	    		eLon = Lon;
	    		eX = X;
	    		eY = Y;	
	    		Variable.rs = Variable.smt.executeQuery("SELECT �ѧO�X from userrequests WHERE arrangedate = '" + Variable.date + "' AND arrangetime = '" + Variable.time + "' ORDER BY �ѧO�X");
	  		  
			  	if(Variable.rs.last())
			  	{
			  		reqnum = Variable.rs.getInt("�ѧO�X")+1;
			  	}
			  	req.Number=reqnum;
			  	flag=0;	
			  	IndexMap.put(req.Number,req);	
	    	}	    	
	    	//Ū���q����		    	
			int intervalCount = (int)(24 / 0.5);
	        DriverTable Target=new DriverTable(intervalCount);
	        Target =Target.readsingleDrivertable(Variable.con, Variable.date, Variable.time,Variable.smt,carid,Variable);
	        int StartInterval=(int)(startsec / IntervalSec);
			int EndInterval=(int)(endsec / IntervalSec);				
			if(sharing>0)
		  	{
				RequestTable req=IndexMap.get(reqnum);
				InsertSharingData(req,Target,sharing);
				if(req.AssignSharing!=-1)
					sharing=req.AssignSharing;				
		  	}
			if(Target.Holiday==1)
			{
				Target =check(Target,Variable,StartInterval,EndInterval,carid,reqnum,sharing);	
			}
			else
			{
				if((IndexMap.get(reqnum).OriginTime>=(Target.StartTime+1800)&&(IndexMap.get(reqnum).OriginTime<=(Target.EndTime+2700))))
					Target =check(Target,Variable,StartInterval,EndInterval,carid,reqnum,sharing);		
				else
					Target=null;
			}
		
			
			if(Target!=null&&flag==0)
			{
				
			    String sharingAmbition="�_";
			    if(sharing==0)
			    {
			    	sharingAmbition="�_";
			    }else  if(sharing!=0&&sharing!=IndexMap.get(reqnum).AssignSharing)
			    {
			    	sharingAmbition="�i";
			    }else if(sharing!=0&&sharing==IndexMap.get(reqnum).AssignSharing)
			    {
			    	sharingAmbition=String.valueOf(IndexMap.get(sharing).RequestNumber);
			    	String sql = "UPDATE userrequests SET `�@���N�@`='"+account+"' WHERE `�ѧO�X`= '" + sharing + "' AND arrangedate = '" + Variable.date +"' AND arrangetime = '" + Variable.time + "'";
					Variable.smt.executeUpdate(sql);
			    }
			    //�N�o����Ʒs�W�ܹw���`��			   
				String sqlquery = "insert into userrequests (�ѧO�X, arrangedate, arrangetime,Targetdrivers, ��F�ɶ�, ���A, �@���N�@, �m�W, �b��, �٧O, �ɬq, �W���ϰ�, �W���a�}, sLat, sLon, sX, sY, �U���ϰ�, �U���a�}, eLat, eLon, eX, eY, ����,GETONRemark,OffCarRemark,arranged) values (";
				sqlquery += reqnum + ", '" + Variable.date + "','" + Variable.time + "','" + carid + "'," + endsec + ", '" + (status == 1?"���`":"�Ը�") + "','" +sharingAmbition+ "','";
				sqlquery += name +"','" + account + "','" + Disabilities + "','" + ordertime + "','" + startarea + "','" + startadd + "'," + sLat + "," + sLon + "," + sX + "," + sY + ",'";
				sqlquery += endarea + "','" + endadd + "'," + eLat + "," + eLon + "," + eX + "," + eY + ",'" + cartype +  "','" +startRemark+ "','" +endRemark+"', 1)";	   
				//System.out.println(sqlquery);
				Variable.smt.executeUpdate(sqlquery);
				Target.UpdateNode(Variable,Target.RestTime1,Target);
				Variable.rs = Variable.smt.executeQuery("SELECT * FROM `arrange_log` WHERE date = '" + Variable.date +"' AND time = '" + Variable.time + "'");
				if(Variable.rs.next())
		    	{
					operationsnum=Variable.rs.getInt("operationsnum");
					if(operationsnum==5)
					{
						operationsnum=1;
					}
					else
					{
						operationsnum++;
					}
					String sql = "UPDATE arrange_log SET operationsnum ="+operationsnum+"' WHERE date = '" + Variable.date +"' AND time = '" + Variable.time + "'";
					Variable.smt.executeUpdate(sql);	
		    	}
			
				
				PrintWriter writer = response.getWriter();
				writer.write("1,���\�s�W�����w�����");
				writer.flush();
				writer.close();
				Variable.smt.close();
				Variable.con.close();
				Target=null;
				reqnum=-1;
			}
			else if(Target!=null&&flag==1)//�ק�req
			{
				System.out.println("4_1");
				if(sharing>0)
				{
					//�N�o����ƭק�ܹw���`��
					String sql = "UPDATE userrequests SET arranged = 1 ,Targetdrivers='"+Target.ID+"' WHERE �ѧO�X = '" + reqnum + "' AND arrangedate = '" + Variable.date +"' AND arrangetime = '" + Variable.time + "'";
					Variable.smt.executeUpdate(sql);	
				    sql = "UPDATE userrequests SET arranged = 1 ,Targetdrivers='"+Target.ID+"' WHERE �ѧO�X = '" + sharing + "' AND arrangedate = '" + Variable.date +"' AND arrangetime = '" + Variable.time + "'";
					Variable.smt.executeUpdate(sql);	
				}else
				{
					String sql = "UPDATE userrequests SET arranged = 1 ,Targetdrivers='"+Target.ID+"' WHERE �ѧO�X = '" + reqnum + "' AND arrangedate = '" + Variable.date +"' AND arrangetime = '" + Variable.time + "'";
					Variable.smt.executeUpdate(sql);	
				}
				Variable.rs = Variable.smt.executeQuery("SELECT * FROM `arrange_log` WHERE date = '" + Variable.date +"' AND time = '" + Variable.time + "'");
				if(Variable.rs.next())
		    	{
					operationsnum=Variable.rs.getInt("operationsnum");
					if(operationsnum==5)
					{
						operationsnum=1;
					}
					else
					{
						operationsnum++;
					}
					String sql = "UPDATE arrange_log SET operationsnum ="+operationsnum+" WHERE date = '" + Variable.date +"' AND time = '" + Variable.time + "'";
					Variable.smt.executeUpdate(sql);	
		    	}
				Target.UpdateNode(Variable,Target.RestTime1,Target);
				PrintWriter writer = response.getWriter();
				writer.write("1,���\�ƤJ�����w�����");
				writer.flush();
				writer.close();
				Variable.smt.close();
				Variable.con.close();
				Target=null;	
				reqnum=-1;
			}
			else
			{
				System.out.println("5");
			//outstatus = "0";
				PrintWriter writer = response.getWriter();
				writer.write("0,�L�k�ƤJ�������!!");
				writer.flush();
				writer.close();
				Target=null;	
			}
		} catch (Exception e) {
			// TODO �۰ʲ��ͪ� catch �϶�
			PrintWriter writer = response.getWriter();
			writer.write("0,�L�k�ƤJ�������!!");
			writer.flush();
			writer.close();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)throws IOException 
	{
		
	}
	private void InsertSharingData(RequestTable req1,DriverTable Target,int sharing) throws SQLException, IOException
	{
		
		req1.AssignSharing=sharing;
		RequestTable req2=IndexMap.get(req1.AssignSharing);		
		req2.AssignSharing=req1.Number;
		
		String reqinfo=null;
		String reqinfo1=null;
		int arrivetime=0;
		String[] Order=new String[2];
		if(req1.DestinationTime>req2.DestinationTime)
		{
			arrivetime=req1.DestinationTime;
			Order[0]="1_1";
			Order[1]="1_0";	
			reqinfo=req2.Number+"_"+req1.Number;
			reqinfo1=req2.RequestNumber+"_"+req1.RequestNumber;
		}else
		{
			arrivetime=req2.DestinationTime;
			Order[0]="1_1";
			Order[1]="0_1";	
			reqinfo=req1.Number+"_"+req2.Number;
			reqinfo1=req1.RequestNumber+"_"+req2.RequestNumber;
		}
		String sql ="INSERT INTO travelinformationofcarsharing(AssignSharing, ���W����,date,arrangetime,�_�I,���~�I1,���~�I2,���I,arrivetime,starttime)VALUES (";
			   sql+="'"+reqinfo+"' , ";//�w��������
			   sql+="'"+reqinfo1+"' , ";//���W����
			   sql+="'"+Variable.date+"' , '"+Variable.time+"',";//���
			   sql+="'0_0' , "+"'"+Order[0]+"' , "+"'"+Order[1]+"' , "+"'0_0' ,";//�_�I,���~�I1,���~�I2,���I
			   sql+=arrivetime+","+req1.OriginTime+")";//arrivetime,starttime
			   Variable.smt.executeUpdate(sql);
			   sql="UPDATE userrequests SET `�@���N�@` ='"+req2.RequestNumber+"'	WHERE `�ѧO�X`='"+req1.Number+"' and  `arrangedate`='"+Variable.date+"' and `arrangetime`='"+Variable.time+"'";
			   Variable.smt.executeUpdate(sql);
			   sql="UPDATE userrequests SET `�@���N�@` ='"+req1.RequestNumber+"'	WHERE `�ѧO�X`='"+req2.Number+"' and  `arrangedate`='"+Variable.date+"' and `arrangetime`='"+Variable.time+"'";
			   Variable.smt.executeUpdate(sql);
	}
	public DriverTable check(DriverTable Target,defineVariable Variable,int StartInterval,int EndInterval,String carid,int reqnum,int sharing) throws IOException, SQLException
	{	
		//boolean flag=false;
		//���n�ƤJ��������		
		int arrangeflag=0;
		if(Target.TimeInterval[StartInterval].indexOf("���ƯZ")!=-1)
		{
			arrangeflag=1;
		}
		if(Target.Holiday==1)
				arrangeflag=1;
		for(int i = StartInterval; i <= EndInterval; i++)
		{
			
			if(sharing!=0)
			{
				RequestTable req1=IndexMap.get(reqnum);
				RequestTable req2=IndexMap.get(req1.AssignSharing);				
				if(UpdateSharingData(req1,req2))
				{
				    Target.TimeInterval[i] = String.valueOf(req1.Number+"_"+req2.Number);
				}else
				{
					Target.TimeInterval[i] = String.valueOf(req2.Number+"_"+req1.Number);
				}
			}
			else
			{			
				if((Target.TimeInterval[i].indexOf("���ƯZ")!=-1||Target.TimeInterval[i].indexOf("���W�Z")!=-1)&&arrangeflag==1)
				{
					Target.TimeInterval[i] = String.valueOf(reqnum);
				}
				else
				{
					Target=null;
				}
			}
			//flag=true;
			//}
			//else
			//{
			// flag=false;
			//}
		}
		//if(flag)
			return Target;
		//else
			//return null;
	}
	private boolean UpdateSharingData(RequestTable req1,RequestTable req2) throws SQLException, IOException
	{
		String reqinfo=null;
		int arrivetime=0;
		String[] Order=new String[2];
		reqinfo=String.valueOf(req1.Number)+"_"+String.valueOf(req2.Number);				
		ResultSet rs = Variable.smt2.executeQuery("SELECT AssignSharing FROM travelinformationofcarsharing WHERE date = '" + Variable.date + "' AND arrangetime = '" 
				+ Variable.time + "' AND `AssignSharing`='"+reqinfo+"'");
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
					", `���~�I1`='"+Order[0]+"', `���~�I2`='"+Order[1]+"' WHERE `date`= '" + Variable.date + "' AND arrangetime = '" + Variable.time+"' AND `AssignSharing`='"+reqinfo+"'";
			Variable.smt.executeUpdate(sql);			   
			return true;
		 }else
		 {
			return false;
		 }
		
	}
	private void GetGeocode(Statement smt, String area, String add)
	{
		ResultSet rs2 = null;
		Lat = null;
		Lon  = null;
		X = -1;
		Y = -1;
		try
		{
			rs2 = smt.executeQuery("SELECT �W���a�}�g��, �W���a�}�n��, �W���a�}X, �W���a�}Y from traveltime WHERE �W���a�} ='" + area + add + "'");
			if(rs2.next())
			{
				Lon = rs2.getString("�W���a�}�g��");
				Lat = rs2.getString("�W���a�}�n��");
				X = rs2.getInt("�W���a�}X");
				Y = rs2.getInt("�W���a�}Y");
			}
			else
			{
				rs2.close();
				rs2 = smt.executeQuery("SELECT �U���a�}�g��, �U���a�}�n��, �U���a�}X, �U���a�}Y from traveltime WHERE �U���a�} ='" + area + add + "'");
				if(rs2.next())
				{
					Lon = rs2.getString("�U���a�}�g��");
					Lat = rs2.getString("�U���a�}�n��");
					X = rs2.getInt("�U���a�}X");
					Y = rs2.getInt("�U���a�}Y");
				}
				else
				{
					GoogleMapsAPI gmapi = new GoogleMapsAPI(Variable);
					double[] temp =gmapi.GeocodingAPI(area + add);
					Lon = String.valueOf(temp[1]);
					Lat = String.valueOf(temp[0]);
					Y = (int)((temp[0] - 24) * 110754.8256 + 2655032.3);			//coordinate transform
					X = (int)((temp[1] - 121) * 101745.445 + 250000);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
