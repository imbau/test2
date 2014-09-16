import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
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
@WebServlet("/FalseTrip.view")
public class FalseTrip extends HttpServlet {
	private static final long serialVersionUID = 1L;
    defineVariable Variable;	
    static List<reqGroup> requestTable;	//需求表  
	//將所有需求放置IndexMap以利查詢上一班或下一班預約者的資訊
    Map<Integer, RequestTable> IndexMap = new HashMap<Integer, RequestTable>();    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FalseTrip() {
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
	{
		
		// TODO Auto-generated method stub
		response.setContentType("text/html;charset=UTF-8");	
		String carid = null;
		String orderhour = null;		
		String ordermin = null;
		String TimeLength = null;
		String Meeting = null;
		String insertSQL = "insert into userrequests (`識別碼`,`arrangedate` ,`arrangetime`,`Reservationnumber`,`Targetdrivers`,`狀態`,"+
				"`共乘意願`,`姓名`,`帳號`,`telephone`,`level`,`障別`,`VisuallyImpaired`,`Wheelchair`,`時段`,`上車區域`,"+
				 "`上車地址`,`下車區域`,`下車地址`,`訂車時間`,`Customcar`,`Waiting`,`subsidizeNumber`,`車種`,`抵達時間`,`GETONRemark`,"+
				 "`OffCarRemark`,`sLat`,`sLon`,`eLat`,`eLon`,`arranged`) values (";
		try 
		{
			Variable = new defineVariable();
			//排班日期
			Variable.date = request.getParameter("arrangedate");
	    	Variable.time = request.getParameter("arrangetime");	  
	    	//車號
	    	carid = request.getParameter("carid").trim();   
	    	//處理預約時間	    	
    		orderhour = request.getParameter("orderhour");	    		
    		ordermin = request.getParameter("orderminute");
    		//空趟的時間長度
    		TimeLength= request.getParameter("TimeLength");
    		//會議趟變數
    		Meeting= request.getParameter("Meeting");
    		RequestTable reqNode = new RequestTable();
    		//判斷是否為會議趟
    		if(Meeting.indexOf("1")!=-1)
    		{
    			reqNode.DestinationAddress="新北市板橋區文化路二段331號";
    			reqNode.OriginAddress="新北市板橋區文化路二段331號";
    			reqNode.TravelTime=Integer.valueOf(TimeLength)*1800;
    			reqNode.OriginTime= Integer.valueOf(orderhour) * 3600 + Integer.valueOf(ordermin) * 60;
    			reqNode.DestinationTime=reqNode.OriginTime+reqNode.TravelTime-1;
    			reqNode.RequestNumber="否"; 
    			
    			if(Integer.valueOf(orderhour)<=9)
    				orderhour="0"+orderhour;    		
    			//計算上下車時間的區間
    			int StartInterval=(int)(reqNode.OriginTime / Variable.IntervalSec);
    			int EndInterval=((reqNode.DestinationTime% Variable.IntervalSec)  > 0.0 ? (int)(reqNode.DestinationTime / Variable.IntervalSec) : (int)(reqNode.DestinationTime / Variable.IntervalSec)-1);
                //設定gps
    			SetGps(reqNode);
    			//取得預約總數
    			Variable.rs = Variable.smt.executeQuery("SELECT 識別碼 from userrequests WHERE arrangedate = '" + Variable.date + "' AND arrangetime = '" + Variable.time + "' ORDER BY 識別碼");
    			if(Variable.rs.last())
			  	{
    				reqNode.Number = Variable.rs.getInt("識別碼")+1;
			  	}
    			int intervalCount = (int)(24 / 0.5);
    	        DriverTable Target=new DriverTable(intervalCount);
    	        Target =Target.readsingleDrivertable(Variable.con, Variable.date, Variable.time,Variable.smt,carid,Variable);
    			if(!(check(Target,Variable,StartInterval,EndInterval,carid,reqNode.Number).ID.indexOf("null")!=-1))
    			{
    				//新增空趟
    				insertSQL+=reqNode.Number+",'"+Variable.date+"','"+Variable.time+"','12345','"+carid+"','候補','否','伊甸','9999','無','健康','健康','否','否','"+(orderhour+ordermin)+"'";
    				insertSQL+=",'新北市板橋區','文化路二段331號','新北市板橋區','文化路二段331號','"+(Variable.date+Variable.time)+"','排班人員',' ',' ',' ',"+reqNode.DestinationTime+",' ',' ',"+reqNode.OriginLat+","+reqNode.OriginLon+",";
    				insertSQL+=reqNode.DestinationLat+","+reqNode.DestinationLon+",1)";
    				Variable.smt.executeUpdate(insertSQL);    				
    			    Target.UpdateNode(Variable,Target.RestTime1,Target);
    				PrintWriter writer = response.getWriter();
    				writer.write("1,成功新增此筆預約資料");
    				writer.flush();
    				writer.close();
    				Variable.smt.close();
    				Variable.con.close();
    				Target=null;
    			}
    			else
    			{
    				PrintWriter writer = response.getWriter();
    				writer.write("0,無法排入");
    				writer.flush();
    				writer.close();
    				Variable.smt.close();
    				Variable.con.close();
    				Target=null;    				
    			}
    		}
    		else
    		{
    			
    		}
	    	
	    	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public DriverTable check(DriverTable Target,defineVariable Variable,int StartInterval,int EndInterval,String carid,int reqnum) throws IOException, SQLException
	{	
		//找到要排入的那輛車		
		int arrangeflag=0;
		if(Target.TimeInterval[StartInterval].indexOf("未排班")!=-1)
		{
			arrangeflag=1;
		}
		for(int i = StartInterval; i <=EndInterval; i++)
		{
			if(Target.TimeInterval[i].indexOf("未排班")!=-1||arrangeflag==1)
			{
				Target.TimeInterval[i] = String.valueOf(reqnum);
			}
			else
			{
				Target.ID="null";
			}
			if(StartInterval==i)
				arrangeflag=0;
		}
		return Target;		
	}
	private void SetGps(RequestTable reqNode)
	{
		try
		{
			GoogleMapsAPI gmapi = new GoogleMapsAPI(Variable);
			double[] temp =gmapi.GeocodingAPI(reqNode.OriginAddress);
			reqNode.OriginLon = temp[1];
			reqNode.OriginLat = temp[0];
			temp =gmapi.GeocodingAPI(reqNode.DestinationAddress);
			reqNode.DestinationLon = temp[1];
			reqNode.DestinationLat = temp[0];
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
