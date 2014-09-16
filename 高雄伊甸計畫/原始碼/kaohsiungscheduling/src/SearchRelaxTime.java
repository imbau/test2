import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
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
/**
 * Servlet implementation class RoutingArranger
 */
@WebServlet("/SearchRelaxTime.view")
public class SearchRelaxTime extends HttpServlet
{
	private static final long serialVersionUID = 1L;	
	 defineVariable Variable;//放置定義的變數
    /**
     * @throws Exception 
     * @see HttpServlet#HttpServlet()
     */
    public SearchRelaxTime()
    {
        super();    
        // TODO Auto-generated constructor stub
    }

	/**
	 * @throws IOException 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse respognse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		// TODO Auto-generated method stub
		//參數宣告////////////////////////////////////////////////////////////////	
		double TimeUnit = 0.0;	
		int[] FilterEnable = new int[5];//紀錄Filter啟動參數			
		List<reqGroup> requestTable=new ArrayList<reqGroup>();	//需求表		
	    List<carGroup> car = new ArrayList<carGroup>();//車輛表
	    
		////////////////////////////////////////////////////////////////////////////		
		
		try 
		{
			System.out.println("start");
			Variable = new defineVariable();//初始化定義變數				
			Variable.date = request.getParameter("arrangedate");//排班日期
			Variable.time = request.getParameter("arrangetime");//排班時間	
			ILF ilf = new ILF(Variable.con,Variable);//初始化歷史資料搜尋物件	
			for(int i=0;i<Variable.areanum;i++)
				 car.add(new carGroup(Variable.intervalnum));	//初始化司機表
			for(int i=0;i<Variable.areanum;i++)
			    requestTable.add(new reqGroup(Variable.intervalnum));//初始化預約表以上車區域	
			
			//////////////////////////////////////////////////////////////////////
									
			//從資料庫讀取參數配置表
			Variable.rs = Variable.smt.executeQuery("select *from setting");//取得設定參數
			
			//取得一筆參數set
			while(Variable.rs.next())
			{
				//設定參數
				TimeUnit = Variable.rs.getFloat("Time unit");	
				FilterEnable[0] = Variable.rs.getInt("Filter1");
				FilterEnable[1] = Variable.rs.getInt("Filter2");
				FilterEnable[2] = Variable.rs.getInt("Filter3");
				FilterEnable[3] = Variable.rs.getInt("Filter4");
				FilterEnable[4] = Variable.rs.getInt("Filter5");	
				
				DriverTable DriverTable = new DriverTable(0);		   
			    //初始化讀取預約這資料物件		
				ReadTempRequest input = new ReadTempRequest(Variable.con, Variable.date, Variable.time);
				//讀取request預約表，
				requestTable = input.ReadOrderTable(requestTable,Variable);
				//將所有需求放置IndexMap以利查詢上一班或下一班預約者的資訊
			    Map<Integer, RequestTable> IndexMap = new HashMap<Integer, RequestTable>();	
			    //將所有預約者放入map
			    IndexMap=RequestTable.getindexmap(IndexMap,requestTable,Variable);	
				//讀取司機表	
				car=DriverTable.readDrivertable(Variable.con, Variable.date, Variable.time, Variable.smt, car,ilf,IndexMap,Variable,requestTable);	
				if(Variable.errorcode<=-2)
					break;
				resttime(car);
		
				/***************************更新資料庫裡的司機班表資料********************************/
				for(int j=0;j<Variable.areanum;j++)
				 {
					 for(int l=0;l<Variable.intervalnum;l++)
					 {
						 for(int k=0;k<car.get(j).getCar(l).size();k++)
						 {
							 car.get(j).getCar(l).get(k).UpdateNode(Variable,car.get(j).getCar(l).get(k).RestTime1,car.get(j).getCar(l).get(k));
						 }
					}
				 }
				break;
				/***************************更新資料庫裡的司機班表資料********************************/	
						
			}
			System.out.println("尋找休息時間結束");	
			
		}
		catch(Exception e)
		{
			for(int j=0;j<Variable.areanum;j++)
			{
				 for(int l=0;l<Variable.intervalnum;l++)
				 {
					 for(int k=0;k<car.get(j).getCar(l).size();k++)
					 {
						 car.get(j).getCar(l).get(k).UpdateNode(Variable,car.get(j).getCar(l).get(k).RestTime1,car.get(j).getCar(l).get(k));
					 }
				}
			}
			Variable.errorcode=-7;
			PrintWriter out = response.getWriter();
			out.println(String.valueOf(Variable.errorcode));
			e.printStackTrace();
		}
		if(Variable.errorcode<=-2)
		{
			
			PrintWriter out = response.getWriter();
			out.println(Variable.errorcode);
			
		}else
		{
			try 
			{
				Variable.smt.close();			
				Variable.con.close();
				requestTable.clear();
				car.clear();			
				System.gc();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			PrintWriter out = response.getWriter();
			out.println("Success");
			
		}
			
		
	}
	 //尋找休息時間
  	public List<carGroup> resttime(List<carGroup> DriverList) throws IOException
	{
  		 String resttime = "null";//第一個半小時休息時間
		 String resttime1 = "null";//第二個半小時的休息時間
		 int midtime=0;//工時中間的
		for(int j=0;j<Variable.areanum;j++)
		 {
			 for(int l=0;l<Variable.intervalnum;l++)
			 {
				 for(int k=0;k<DriverList.get(j).getCar(l).size();k++)
				 {
					 resttime = "null";//第一個半小時休息時間
					 resttime1 = "null";//第二個半小時的休息時間
					 
					//工時小於6小時不找休息時間
					if((DriverList.get(j).getCar(l).get(k).EndTime-DriverList.get(j).getCar(l).get(k).StartTime)<Variable.nonrelax)
					{	
						 //站長固定休息 站長工時4小時
						 if(Integer.valueOf(DriverList.get(j).getCar(l).get(k).CallNum)==5||Integer.valueOf(DriverList.get(j).getCar(l).get(k).CallNum)==27)	 
						 {	 
							 DriverList.get(j).getCar(l).get(k).RestTime1="31:32";							
						 }
						continue;
					}
					midtime=DriverList.get(j).getCar(l).get(k).halfworktime/1800;
				    //先找中間時段的
					if(DriverList.get(j).getCar(l).get(k).relaxarry.contains(String.valueOf(midtime)))
					{
						resttime=String.valueOf(midtime);
						DriverList.get(j).getCar(l).get(k).relaxarry.remove(String.valueOf(midtime));
					}
					 if(resttime=="null")
						 resttime=Search(DriverList.get(j).getCar(l).get(k).relaxarry,midtime);
					 if(resttime1=="null")
					  resttime1=Search(DriverList.get(j).getCar(l).get(k).relaxarry,midtime);		
				    // DriverList.get(j).getCar(l).get(k).RestTime1=resttime1+":"+resttime;//寫到司機表的休息時間
					 if(resttime!="null"&&resttime1 !="null")
					 { 
						 if(Integer.valueOf(resttime)>Integer.valueOf(resttime1))
							 DriverList.get(j).getCar(l).get(k).RestTime1=resttime1+":"+resttime;//寫到司機表的休息時間
						 else
							  DriverList.get(j).getCar(l).get(k).RestTime1=resttime+":"+resttime1;//寫到司機表的休息時間
					 }
					 else
					 {
						  DriverList.get(j).getCar(l).get(k).RestTime1=resttime+":"+resttime1;//寫到司機表的休息時間
					 }
				 }
			}
		 }
		
		return DriverList;
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// TODO Auto-generated method stub

	}
	public String Search(List<String> relaxarry,int midtime) 
	{
		 String resttime = "null";
		//尋找上班中間時段附近	
		 for(int  shiftindex =1 ; shiftindex <=7; shiftindex++)
		 {
			 //先往前搜尋
			 if(relaxarry.contains(String.valueOf(midtime-shiftindex)))
			 {
				 resttime=String.valueOf(midtime-shiftindex);
				 relaxarry.remove(String.valueOf(midtime-shiftindex));	
				 break;
			 }//往後搜尋
			 else  if(relaxarry.contains(String.valueOf(midtime+shiftindex)))
			 {
				 resttime=String.valueOf(midtime+shiftindex);
				 relaxarry.remove(String.valueOf(midtime+shiftindex));
				 break;
			 }
		}		 
		return resttime;		
	}

}
