import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
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

import jxl.read.biff.BiffException;
/**
 * Servlet implementation class RoutingArranger
 */
@WebServlet("/Specialareatotaipei.view")
public class SpecialareatotaipeiOfpreRoutingArranger extends HttpServlet
{
	private static final long serialVersionUID = 1L;	
	 defineVariable Variable;//放置定義的變數
    /**
     * @throws Exception 
     * @see HttpServlet#HttpServlet()
     */
    public SpecialareatotaipeiOfpreRoutingArranger()
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
		List<reqGroup> tailrequestTable=new ArrayList<reqGroup>();	//需求表
	    List<carGroup> car = new ArrayList<carGroup>();//車輛表
	    
		////////////////////////////////////////////////////////////////////////////		
		ILF ilf = null;
		try {
			Variable = new defineVariable();//初始化定義變數				
			Variable.date = request.getParameter("arrangedate");//排班日期
			Variable.time = request.getParameter("arrangetime");//排班時間		
			Variable.smt2.executeUpdate("UPDATE progress SET percent =0 WHERE `index` =7 and `date`='"+Variable.date+"' and `time`='"+Variable.time+"'");
			Variable.smt2.executeUpdate("UPDATE `arrange_log` SET `progress`=2  WHERE `date`='"+Variable.date+"' and `time`='"+Variable.time+"'");
			//ilf = new ILF(Variable.con,Variable.date,Variable.time);//初始化歷史資料搜尋物件	
			ilf = new ILF(Variable.con,Variable);//初始化歷史資料搜尋物件	
			for(int i=0;i<Variable.areanum;i++)
				 car.add(new carGroup(Variable.intervalnum));	//初始化司機表
			for(int i=0;i<Variable.areanum;i++)
			    requestTable.add(new reqGroup(Variable.intervalnum));//初始化預約表以上車區域
			for(int i=0;i<Variable.areanum;i++)
				tailrequestTable.add(new reqGroup(Variable.intervalnum));//初始化預約表以下車區域做分類
			
			
			

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
				//讀取request預約表，
				tailrequestTable =input.ReadEndTable();
				
				//將所有需求放置IndexMap以利查詢上一班或下一班預約者的資訊
			    Map<Integer, RequestTable> IndexMap = new HashMap<Integer, RequestTable>();			   
			    Map<String, DriverTable> carIndexMap = new HashMap<String, DriverTable>();
			    //將所有預約者放入map
			    IndexMap=RequestTable.getindexmap(IndexMap,requestTable,Variable);		
						
				//讀取司機表	
				 car=DriverTable.readDrivertable(Variable.con, Variable.date, Variable.time, Variable.smt, car,ilf,IndexMap,Variable,requestTable);	
				 if(Variable.errorcode<=-2)
						break;
				 //將所有司機放入map 以利查詢
				 carIndexMap=DriverTable.GetcarIndexMap(carIndexMap,car,Variable);
				//執行filter					
				Variable.recentPercent=0;//排班進度值歸零
				//int count=0;	//計算處理過的預約者統計值				//讀取超長旅行時間array
				//讀取三鶯林口桃園下車地點在台北的需求 1代表讀取defineVariable裡的Specialeara array第一行
				List<RequestTable> Specialearaarray1 =RequestTable.getSpecialareatotaiperarray(1,requestTable,Variable); 		    		
			    //0代表讀取defineVariable裡的Specialearacar array第一行讀取土城中和車子的index
				//放置carindex要去讀取某個區域的車子index				
				Specialearaarray1=Southwestprocess(car,FilterEnable,ilf,IndexMap,Specialearaarray1,TimeUnit,carIndexMap,DriverTable);
				//回傳小於等於-2代表google查詢有錯立即終止程式
				if(Variable.errorcode<=-2)
					 break;	
				
				/***************************寫出司機全部班表********************************/
			   /* File file = new File("C:/AppServ/www/routingarrange/log/司機班表.txt");
				file.delete();		
				debug=new debug("司機班表","txt",1);				
				debug.printDriverTable(car);
				debug.fileclose();*/
				/***************************測試功能********************************/
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
				checkdata();
				break;
				/***************************更新資料庫裡的司機班表資料********************************/	
						
			}
			System.out.println(":優先區域往台北排班結束:");	
			
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
			//執行完排班寫回進度100
			try 
			{
				Variable.smt2.executeUpdate("UPDATE progress SET percent =100 WHERE `index` =7 and `date`='"+Variable.date+"' and `time`='"+Variable.time+"'");
				ilf=null;
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
	//特殊區域的filterprocess :三鶯林口土城下車在土城
	public boolean Specialearafilterprocess(List<DriverTable> filterDriverTable,RequestTable Req
				,double TimeUnit,Map<Integer, RequestTable> IndexMap,ILF ilf,int[] FilterEnable,List<carGroup> car) throws Exception
	{		
		boolean Found=false;
		DriverTable TargetDriver=null;
		for(int Classification=1;Classification<=5;Classification++)
		{
			List<DriverTable> DriverTable= new LinkedList<DriverTable>(filterDriverTable);
			PreRountingArrangerFilter filter=null;
			TargetDriver=null;
			//filter初始化	
			filter = new PreRountingArrangerFilter(Variable.TolerableTime,FilterEnable,Req, TimeUnit,Req.Number, IndexMap, Variable, ilf);
			//車種filter
			filter.CarFilter(DriverTable);
			//檢查時段filter
			filter.StatusFilter(DriverTable);	
			//檢查區域filter
			filter.areaFilter(DriverTable,Classification);
			//檢查回廠區域filter
			filter.endareaFilter(DriverTable);	
			//檢查是否有足夠的休息時間
			filter.restFilter(DriverTable);
			//檢查是否超過預定的接客上限數
			filter.maxofTrip(DriverTable);	
			//檢查是否來得及接當前預約者與來得及接下一班預約者
			filter.DistanceTimeFilter(DriverTable);		
			//取得最佳司機			 
			TargetDriver = filter.MinFilter(DriverTable,false);
			if(TargetDriver != null)
			{
				//修改司機表ModifyOriginDriverTable("原始司機表","要排入司機", "要修改的資料表名稱");			
				TargetDriver.ModifyOriginDriverTable(Variable,Req,TimeUnit,TargetDriver, "userrequests",IndexMap,car);	
				TargetDriver.ArrangedCount++;
				//同步需求表
				Req.Arrange=true;
				Found=true;
				break;
			}else if(Classification==5)
			{
				//更新預約者的排班標記
				 String sql = "UPDATE userrequests SET arranged = -4 WHERE 識別碼 = '" +Req.Number + "' AND arrangedate = '" + Variable.date +"' AND arrangetime = '" + Variable.time + "'";
				 Variable.smt2.executeUpdate(sql);//寫到資料庫
				 Found=false;
			}
		}
		return Found;
	}
	//處理三鶯林口桃園
	 public List<RequestTable> Southwestprocess(List<carGroup> car,int[] FilterEnable,ILF ilf,Map<Integer, RequestTable> IndexMap
					,List<RequestTable> Specialearaarray,double TimeUnit,Map<String, DriverTable> carIndexMap,DriverTable DriverTable) throws Exception
			{
				//判斷是否有三鶯林口桃園的需求者
				if(Specialearaarray.size()>0)
				{
					
					//依序讀取出來處理
					for(int reqindex=0;reqindex<Specialearaarray.size();)
					{
						if(!Specialearaarray.get(reqindex).Arrange)
						{
							//取得四金釵
							List<DriverTable> filterDriverTable=DriverTable.getfFournobileDriverTable(car,Variable); 
							if(Specialearafilterprocess(filterDriverTable,Specialearaarray.get(reqindex),
								TimeUnit,IndexMap,ilf,FilterEnable,car))
							{
								//System.out.println(reqindex+"ok"); 
								Specialearaarray.remove(reqindex);						
							}
							else
							{
								reqindex++;
							}	
					    }
						//回傳小於等於-2代表google查詢有錯立即終止程式
						if(Variable.errorcode<=-2)
							break;
					}
				}
				return Specialearaarray;
		}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	 public void  checkdata() throws ClassNotFoundException, IOException, SQLException, BiffException, InterruptedException
		{
			 ResultSet rs = null;
			 String sqlQuery="SELECT * FROM `arrangedtable`  WHERE `date`='"+	Variable.date+"' and `arrangetime`='"+	Variable.time+"'";
			 String sqlQuery1="";
			 rs=	Variable.smt.executeQuery(sqlQuery); 
			 rs.first();
			 sqlQuery1="UPDATE userrequests SET Targetdrivers='null' ,arranged=-4 WHERE arranged=1 and `arrangedate`='"+Variable.date+"' and `arrangetime`='"+	Variable.time+"'";
			 Variable.smt2.executeUpdate(sqlQuery1);
			  do
				{	
				for(int i = 1; i < 17; i++)
				{
					//讀取班次資料並回復Node內的timeinterval
					if(rs.getInt("run" + String.valueOf(i)) != -1)
					{  
						String information = rs.getString("user"+ String.valueOf(i));
						String[] testnumber = information.split("_");				
						if(testnumber.length == 1)
						{
							int informationNum = Integer.valueOf(testnumber[0]);	
							//sqlQuery1="UPDATE userrequests SET Targetdrivers='null' WHERE `arrangedate`='2013-07-30' and `arrangetime`='13:53:11'";
							sqlQuery1="UPDATE userrequests SET arranged=1 ,Targetdrivers='"+rs.getString("carid")+"' WHERE `arrangedate`='"+Variable.date+"' and `arrangetime`='"+Variable.time+"' AND `識別碼`='"+informationNum+"'";
							Variable.smt2.executeUpdate(sqlQuery1);
						}
						else
						{
							
							int informationNum = Integer.valueOf(testnumber[0]);	
							int informationNum1 = Integer.valueOf(testnumber[1]);	
							sqlQuery1="UPDATE userrequests SET  arranged=1 ,Targetdrivers='"+rs.getString("carid")+"' WHERE `arrangedate`='"+Variable.date+"' and `arrangetime`='"+Variable.time+"' AND `識別碼`='"+informationNum+"'";
							Variable.smt2.executeUpdate(sqlQuery1);
							sqlQuery1="UPDATE userrequests SET  arranged=1 , Targetdrivers='"+rs.getString("carid")+"' WHERE `arrangedate`='"+Variable.date+"' and `arrangetime`='"+Variable.time+"' AND `識別碼`='"+informationNum1+"'";
							Variable.smt2.executeUpdate(sqlQuery1);
						}
					}else
					{
						break;
					}
				}
				}while(rs.next());
		}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// TODO Auto-generated method stub

	}
	

}
