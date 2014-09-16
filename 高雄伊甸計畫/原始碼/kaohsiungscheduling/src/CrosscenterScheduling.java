import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
@WebServlet("/CrosscenterScheduling.view")
public class CrosscenterScheduling extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private String Lat = null, Lon = null;
    private int X = -1, Y = -1;
    defineVariable Variable;	
    static List<reqGroup> requestTable;	//需求表
	//將所有需求放置IndexMap以利查詢上一班或下一班預約者的資訊
    Map<Integer, RequestTable> IndexMap = new HashMap<Integer, RequestTable>(); 
    //List<carGroup> car = new ArrayList<carGroup>();//車輛表	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CrosscenterScheduling() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)throws IOException 
	{
		
		// TODO Auto-generated method stub
		response.setContentType("text/html;charset=UTF-8");	
		String startarea = null, startadd = null, endarea = null, endadd = null;
		int IntervalSec = (int)(0.5 * 3600);
		String carid = null;
		String account = null;	

		
		String Designationdate="null";
		String Designationtime="null";
		int sharing = 0;	
		int reqnum=-1;	
		
	    try {
	    	System.out.println("start");
	    	Variable = new defineVariable();
	    	Variable.date = request.getParameter("arrangedate");
	    	Variable.time = request.getParameter("arrangetime");
	    	Designationdate= request.getParameter("Designationdate");
	    	Designationtime=request.getParameter("Designationtime");
	    	//讀取司機表	
	    	//for(int i=0;i<Variable.areanum;i++)
				 //car.add(new carGroup(40));	//初始化司機表
	    	//帳號
	    	account = request.getParameter("account").trim();
	    	//上下車區域及地址
	    	startarea = request.getParameter("startarea").trim();
	    	startadd = request.getParameter("startadd").trim();	    	
	    	endarea = request.getParameter("endarea").trim();
	    	endadd = request.getParameter("endadd").trim();
    		carid = request.getParameter("carid").trim();     	
    		requestTable=new ArrayList<reqGroup>();	//需求表
    		for(int i=0;i<Variable.areanum;i++)
			    requestTable.add(new reqGroup(Variable.intervalnum));//初始化預約表以上車區域
    		//初始化讀取預約這資料物件		
			ReadTempRequest input = new ReadTempRequest(Variable.con, Variable.date, Variable.time);
			//讀取request預約表
			requestTable = input.ReadOrderTable(requestTable,Variable);			 
		    //將所有預約者放入map
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
			Variable.rs = Variable.smt.executeQuery("SELECT * from userrequests WHERE `上車區域`='"+startarea+"' AND `上車地址`='"+startadd+"' AND `下車區域`='"+endarea+"' AND `下車地址`='"+endadd+"' AND `帳號`='"+account+"' AND arrangedate = '" + Designationdate + "' AND arrangetime = '" + Designationtime + "'");
	    	if(Variable.rs.next())
	    	{
	    		RequestTable req=new RequestTable();
	    		req=input.readsinglereq(Variable.rs,Variable);
	    		req.Number=Integer.valueOf(Variable.rs.getString("識別碼"));	
	    		Variable.rs2 = Variable.smt.executeQuery("SELECT 識別碼 from userrequests WHERE arrangedate = '" + Variable.date + "' AND arrangetime = '" + Variable.time + "' ORDER BY 識別碼");
		  		if(Variable.rs2.last())
			  	{
			  		reqnum = Variable.rs2.getInt("識別碼")+1;	
			  	}
		  		int intervalCount = (int)(24 / 0.5);
		        DriverTable Target=new DriverTable(intervalCount);
		        Target =Target.readsingleDrivertable(Variable.con, Variable.date, Variable.time,Variable.smt,carid,Variable);
		        int StartInterval=(int)(req.OriginTime / IntervalSec);
				int EndInterval=(int)(req.DestinationTime / IntervalSec);	
				if((req.OriginTime>=(Target.StartTime+1800)&&(req.OriginTime<=(Target.EndTime+2700))))
					Target =check(Target,Variable,StartInterval,EndInterval,carid,reqnum,sharing);		
				else
					Target=null;
				
				 if(Target!=null)//修改req
				{
					String sql = "UPDATE userrequests SET arrangedate='"+Variable.date+"' ,arrangetime='"+Variable.time+"' ,識別碼='"+reqnum+"',arranged = 1 ,Targetdrivers='"+Target.ID+"' WHERE 識別碼 = '" + req.Number + "' AND arrangedate = '" + Designationdate +"' AND arrangetime = '" + Designationtime + "'";
					Variable.smt.executeUpdate(sql);					
					Target.UpdateNode(Variable,Target.RestTime1,Target);
					sql="insert into bulletin (date,time,message) values ('";
					SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Calendar dateCalendar = Calendar.getInstance();
					String DateTime = dateformat.format(dateCalendar.getTime());					
					String[] DealTime = DateTime.split(" ");
					sql=sql+DealTime[0]+"','"+DealTime[1]+"','"+Designationdate+"  "+Designationtime+"  帳號:"+account+"已被支援!!')";
					Variable.smt.executeUpdate(sql);						
					PrintWriter writer = response.getWriter();
					writer.write("1,成功排入此筆預約資料");
					writer.flush();
					writer.close();
					Variable.smt.close();
					Variable.con.close();
					Target=null;	
					reqnum=-1;
				}
				 else
				{
					PrintWriter writer = response.getWriter();
					writer.write("0,無法排入此筆資料!!");
					writer.flush();
					writer.close();
						
				}
				
	    	}else
	    	{
	    		System.out.println("5");
				PrintWriter writer = response.getWriter();
				writer.write("0,無法排入此筆資料!!");
				writer.flush();
				writer.close();
	    	}
		} catch (Exception e) 
		{
			// TODO 自動產生的 catch 區塊
			System.out.println("5");
			PrintWriter writer = response.getWriter();
			writer.write("0,無法排入此筆資料!!");
			writer.flush();
			writer.close();
		}		
	  
}
	private void InsertSharingData(RequestTable req1,DriverTable Target,int StartInterval) throws SQLException, IOException
	{
		
		for(int i = StartInterval; i <= StartInterval; i++)
		{
			if(Target.TimeInterval[i].indexOf("未排班")!=-1)
			{
				req1.AssignSharing=-1;
			}
			else
			{
				req1.AssignSharing=Integer.valueOf(Target.TimeInterval[i]);
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
				
				String sql ="INSERT INTO travelinformationofcarsharing(AssignSharing, 車上乘員,date,arrangetime,起點,中繼點1,中繼點2,終點,arrivetime,starttime)VALUES (";
					   sql+="'"+reqinfo+"' , ";//預約表格欄位
				       sql+="'"+reqinfo1+"' , ";//車上乘員
				       sql+="'"+Variable.date+"' , '"+Variable.time+"',";//日期
				       sql+="'0_0' , "+"'"+Order[0]+"' , "+"'"+Order[1]+"' , "+"'0_0' ,";//起點,中繼點1,中繼點2,終點
				       sql+=arrivetime+","+req1.OriginTime+")";//arrivetime,starttime
				       Variable.smt.executeUpdate(sql);
			}
		}
		
	}
	public DriverTable check(DriverTable Target,defineVariable Variable,int StartInterval,int EndInterval,String carid,int reqnum,int sharing) throws IOException, SQLException
	{	
		//boolean flag=false;
		//找到要排入的那輛車		
		int arrangeflag=0;
		if(Target.TimeInterval[StartInterval].indexOf("未排班")!=-1)
		{
			arrangeflag=1;
		}
		System.out.println(StartInterval);	
		System.out.println(Target.TimeInterval[StartInterval]);	
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
				System.out.println(arrangeflag);
				if(Target.TimeInterval[i].indexOf("未排班")!=-1||arrangeflag==1)
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
					", `中繼點1`='"+Order[0]+"', `中繼點2`='"+Order[1]+"' WHERE `date`= '" + Variable.date + "' AND arrangetime = '" + Variable.time+"' AND `AssignSharing`='"+reqinfo+"'";
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
			rs2 = smt.executeQuery("SELECT 上車地址經度, 上車地址緯度, 上車地址X, 上車地址Y from traveltime WHERE 上車地址 ='" + area + add + "'");
			if(rs2.next())
			{
				Lon = rs2.getString("上車地址經度");
				Lat = rs2.getString("上車地址緯度");
				X = rs2.getInt("上車地址X");
				Y = rs2.getInt("上車地址Y");
			}
			else
			{
				rs2.close();
				rs2 = smt.executeQuery("SELECT 下車地址經度, 下車地址緯度, 下車地址X, 下車地址Y from traveltime WHERE 下車地址 ='" + area + add + "'");
				if(rs2.next())
				{
					Lon = rs2.getString("下車地址經度");
					Lat = rs2.getString("下車地址緯度");
					X = rs2.getInt("下車地址X");
					Y = rs2.getInt("下車地址Y");
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
			
		}
		
	}

}
