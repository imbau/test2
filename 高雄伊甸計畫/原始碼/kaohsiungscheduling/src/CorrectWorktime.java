import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
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
@WebServlet("/CorrectWorktime.view")
public class CorrectWorktime extends HttpServlet {
	private static final long serialVersionUID = 1L;
    defineVariable Variable;	
    static List<reqGroup> requestTable;	//需求表  
	//將所有需求放置IndexMap以利查詢上一班或下一班預約者的資訊
    Map<Integer, RequestTable> IndexMap = new HashMap<Integer, RequestTable>();    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CorrectWorktime() {
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
	 * @throws IOException 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		// TODO Auto-generated method stub
		response.setContentType("text/html;charset=UTF-8");	
		String carid = null;
		ResultSet rs;
		String insertSQL = "UPDATE arrangedtable SET ";	
		String SelectSQL="";
		String constrain ="";
		boolean flag=false;
		int[] worktime=new int[4];
		String[] worktime1=new String[4];
		try {
			Variable = new defineVariable();
			//排班日期
			Variable.date = request.getParameter("arrangedate");
	    	Variable.time = request.getParameter("arrangetime");	
	    	//車號
	    	carid = request.getParameter("carid").trim(); 
	    	constrain = " WHERE date = '" + Variable.date + "' AND arrangetime = '" + Variable.time  + "' AND carid = '" + carid + "'";
	    	worktime[0] =Integer.valueOf(request.getParameter("starthr").trim()); 
	    	worktime[1] = Integer.valueOf(request.getParameter("startmin").trim()); 
	    	worktime[2] =Integer.valueOf(request.getParameter("endhr").trim()); 
	    	worktime[3]=Integer.valueOf(request.getParameter("endmin").trim()); 
	    	//檢查欲設定頭尾班時間是否超過原本已排入的班次時間
	    	SelectSQL="SELECT * FROM `arrangedtable`  WHERE date = '" + Variable.date + "' AND arrangetime = '" + Variable.time  + "' AND carid = '" + carid + "'";
	    	rs=Variable.smt2.executeQuery(SelectSQL);
	    	rs.first();
	    	if(rs.getInt("run1")!=-1)
	    	{
	    		RequestTable Request =Setreq(rs.getString("user1"));
		    	int run=-1;
		    	String user="user";
		    	for(int i = 1; i < 17; i++)
				{
		    		//讀取班次資料並回復Node內的timeinterval
					if(rs.getInt("run" + String.valueOf(i)) != -1&&rs.getInt("run" + String.valueOf(i)) != 0)
					{  
						run=i;
					}
					else
					{
						break;
					}
				}
		    	//檢查欲設定頭班時間是否小於原本第一班預約者上車時間
		    	if((worktime[0]*3600+worktime[1]*60)<=Request.OriginTime)
		    	{
		    		flag=true;
		    	}
		    	//檢查欲設定尾班時間是否超過最後一趟的上車時間
		    	if(run>1&&flag)
		    	{
		    		user+=run;
		    		Request =Setreq(rs.getString(user));
		    		if((worktime[2]*3600+worktime[3]*60)>=Request.OriginTime)
		    			flag=true;
		    		else
		    			flag=false;
		    	}
	    	}
	    	else
	    	{
	    		flag=true;
	    	}
	    	if(flag)
	    	{
	    		//修改班表上的時間
		    	insertSQL+="worktime="+(worktime[0]*3600+worktime[1]*60)+constrain;
		    	Variable.smt.executeUpdate(insertSQL); 
		    	//把頭尾班時間改成出勤時間 傳入參數一為頭尾班時間 參數二為頭班往前的時間 參數三為尾班往前時間
		    	Setworktime(worktime,30,45);
		    	for(int index=0;index<4;index++)
		    	{
		    		if(worktime[index]<9)
		    			worktime1[index]="0"+worktime[index];
		    		else
		    			worktime1[index]=String.valueOf(worktime[index]);
		    	}
		    	//修改司機表上的時間
		    	insertSQL = "UPDATE availablecars SET ";	
		    	constrain = " WHERE date = '" + Variable.date + "' AND time = '" + Variable.time  + "' AND 車號 = '" + carid + "'";
		    	insertSQL+="時段='"+worktime1[0]+":"+worktime1[1]+"~"+worktime1[2]+":"+worktime1[3]+"' "+constrain;
		    	Variable.smt.executeUpdate(insertSQL); 
		    	PrintWriter writer = response.getWriter();
				writer.write("1,成功修改司機時間");
				writer.flush();
				writer.close();
			}
	    	else
	    	{
	    		PrintWriter writer = response.getWriter();
				writer.write("1,修改司機時間失敗");
				writer.flush();
				writer.close();
	    	}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			PrintWriter writer = response.getWriter();
			writer.write("1,修改司機時間失敗");
			writer.flush();
			writer.close();
			e.printStackTrace();
		}
		
		
	}
	public RequestTable Setreq(String trip) throws Exception
	{
		RequestTable Request=null;
		try 
		{
			ResultSet rs;
		    Request=new RequestTable();
			String[] testnumber = trip.split("_");
			rs = Variable.smt.executeQuery("SELECT * FROM userrequests WHERE arrangedate = '" + Variable.date + "' AND arrangetime = '" +Variable.time + "' AND 識別碼 ='" + testnumber[0]+"'");
			ReadTempRequest input = new ReadTempRequest(Variable.con, Variable.date, Variable.time);
			Request=input.readsinglereq(rs,Variable);	 
			return Request;
		} catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			
		}
		return Request;

	}
	public void Setworktime(int[] worktime,int head,int tail)
	{
		//頭班減30分等於出勤時間
		worktime[1]-=head;
		//尾班減45分
		worktime[3]-=tail;
		if(worktime[1]<0)
		{
			worktime[0]--;
			worktime[1]=60+worktime[1];
		}
		if(worktime[3]<0)
		{
			worktime[2]--;
			worktime[3]=60+worktime[3];
		}
	}
}
