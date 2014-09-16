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
@WebServlet("/GeneralScheduling.view")
public class GeneralScheduling extends HttpServlet
{
	private static final long serialVersionUID = 1L;		
	 defineVariable Variable;//放置定義的變數

    /**
     * @throws Exception 
     * @see HttpServlet#HttpServlet()
     */
    public GeneralScheduling()
    {
        super();    
        // TODO Auto-generated constructor stub
    }
    /***********************************************
	 *  錯誤代碼對應的錯誤
		-2:已超過今日配額
        -3:要求已遭拒絕
        -4:不存在的addres
        -5:查詢(address或latlng)遺失了
        -6:有車子缺頭尾班
        -7:排班中斷
	*/
	/**
	 * @throws IOException 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse respognse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		 System.out.print("\n排班開始");
		// TODO Auto-generated method stub
		//參數宣告////////////////////////////////////////////////////////////////	
		double TimeUnit = 0.0;	
		int[] FilterEnable = new int[5];//紀錄Filter啟動參數	
		int Percentmode=0;//紀錄正常或候補的進度值
		int reqsize=0;//紀錄目前正常排班或候補排班的未排過班預約者數	
	    List<reqGroup> requestTable=new ArrayList<reqGroup>();	//需求表
		List<carGroup> car = new ArrayList<carGroup>();//車輛表
		////////////////////////////////////////////////////////////////////////////		
		ILF ilf = null;
		
		try {
			Variable = new defineVariable();//初始化定義變數							
			ilf = new ILF(Variable.con,Variable);//初始化歷史資料搜尋物件	
			Variable.date = request.getParameter("arrangedate");//排班日期
			Variable.time = request.getParameter("arrangetime");//排班時間	
			
			//ilf = new ILF(Variable.con,Variable.date,Variable.time);//初始化歷史資料搜尋物件
			int mode = Integer.valueOf(request.getParameter("mode"));	//判別目前正在排的是正常班次或候補班次
			
			for(int i=0;i<Variable.areanum;i++)
				 car.add(new carGroup(Variable.intervalnum));	//初始化司機表
			for(int i=0;i<Variable.areanum;i++)
			    requestTable.add(new reqGroup(Variable.intervalnum));//初始化預約表

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
				if(mode==1)//候補
				{
					Percentmode=4;//如果是候補班次就設定寫入進度表index第4個
					//更新資料庫紀錄目前操作的動作
					Variable.smt2.executeUpdate("UPDATE `arrange_log` SET `progress`=5  WHERE `date`='"+Variable.date+"' and `time`='"+Variable.time+"'");
				}
				else if(mode==2)//正趟
				{
					Percentmode=1;//如果是正常班次就設定寫入進度表index第1個
					//更新資料庫紀錄目前操作的動作
					Variable.smt2.executeUpdate("UPDATE `arrange_log` SET `progress`=4  WHERE `date`='"+Variable.date+"' and `time`='"+Variable.time+"'");
				}
				
				DriverTable DriverTable = new DriverTable(0);
			    //初始化讀取預約這資料物件		
				ReadTempRequest input = new ReadTempRequest(Variable.con, Variable.date, Variable.time);
				//讀取request預約表，
				requestTable = input.ReadOrderTable(requestTable,Variable);	
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
				double startTime,endTime,totTime;
				startTime = System.currentTimeMillis();
				LinkedList<RequestTable> OriginReqlist=readareaarray( requestTable,mode);	
				reqsize=OriginReqlist.size();
				int count=0;	//計算處理過的預約者統計值
				int tineindex=13;//排班處理的時間順序
				//依時間間格去排班
				for(;tineindex<=46;tineindex++)
				{
					LinkedList<DriverTable> OriginDriverTable =DriverTable.filterDriverTable(car,DriverTable.carsize,tineindex,6,Variable);
					if(OriginDriverTable.size()==0)
						continue;
					for(int Classification=1;Classification<=7;Classification++)
					{
						for(int i = 0; i < OriginDriverTable.size();i++ )
						{
							if(OriginDriverTable.size()==0)
								continue;
							RequestTable TagetReq=null;//選重的預約者
							//取得可用的預約表
							List<RequestTable> FilterReqList = new LinkedList<RequestTable>(OriginReqlist);
							Filter filter= new Filter(tineindex,FilterEnable,OriginDriverTable.get(i),TimeUnit, IndexMap,Variable, ilf);
							filter.AreaCorrespond(FilterReqList);	
							//新人不接共乘趟
							filter.AssignSharingCarFilter(FilterReqList);	
							//特殊車不接超過五趟
							filter.SpecialCarFilter(FilterReqList);	
							//過濾車種
							filter.CarFilter(FilterReqList);	
							//過濾時段
							filter.StatusFilter(FilterReqList);	
							//對應的路之間不接預約者
							filter.AreaCorrespond(FilterReqList);	
							//上下班時段，1小時內部不接超過2趟
							filter.NoMoreThanTwoFilter(FilterReqList);							
							TagetReq=FilterProcess(filter, FilterReqList,Classification,Classification);
							if(Variable.errorcode<=-2)
								 break;
							if(TagetReq!=null)
							{
								count+=Modifyinfo(OriginReqlist,TagetReq, IndexMap ,OriginDriverTable.get(i), TimeUnit, car,requestTable);						
							}
							Variable.recentPercent =(int)(((float)count /reqsize)* 100);//將已排入的預約者數轉換成百分比								
							//寫入資料庫
							if(Variable.recentPercent==100)
								Variable.recentPercent=99;
							Variable.smt2.executeUpdate("UPDATE progress SET percent ="+Variable.recentPercent+" WHERE `index` ="+Percentmode+" and `date`='"+Variable.date+"' and `time`='"+Variable.time+"'");				
						}
					}
				}		
				//假設google api發生錯誤立即寫出排班結果
				if(Variable.errorcode<=-2)
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
					checkdata(-1);
					break;
				}
				 endTime = System.currentTimeMillis();
			    //取得程式結束的時間
				 totTime = endTime - startTime;
				 System.out.println("Using Time: " + totTime+" ms");
				 RequestTable.modifyReqstatus(OriginReqlist, Variable);
				/*if(mode==1)
					car =resttime(car);*/
				/***************************更新資料庫裡的司機班表資料********************************/
				for(int areaindex=0;areaindex<Variable.areanum;areaindex++)
				 {
					 for(int intervalindex=0;intervalindex<Variable.intervalnum;intervalindex++)
					 {
						 for(int index=0;index<car.get(areaindex).getCar(intervalindex).size();index++)
						 {
							
							 car.get(areaindex).getCar(intervalindex).get(index).UpdateNode(Variable,car.get(areaindex).getCar(intervalindex).get(index).RestTime1,car.get(areaindex).getCar(intervalindex).get(index));
						 }
					}
				 }
				checkdata(0);
				//執行完排班寫回進度100
				Variable.smt2.executeUpdate("UPDATE progress SET percent =100 WHERE `index` ="+Percentmode+" and `date`='"+Variable.date+"' and `time`='"+Variable.time+"'");
				break;
				/***************************更新資料庫裡的司機班表資料********************************/	
						
			}	
			ilf=null;
			Variable.smt.close();			
			Variable.con.close();
			requestTable.clear();
			car.clear();			
			System.gc();
		}
		catch(Exception e)
		{
            //發生排班錯誤立即結寫出結果
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
			try {
				checkdata(-1);
			} catch (ClassNotFoundException | BiffException | SQLException
					| InterruptedException e1) {
				// TODO 自動產生的 catch 區塊
				e1.printStackTrace();
			}			
			Variable.errorcode=-7;
			PrintWriter out = response.getWriter();
			out.println(Variable.errorcode);
			e.printStackTrace();
		}
		
		if(Variable.errorcode<=-2)
		{
			PrintWriter out = response.getWriter();
			out.println(Variable.errorcode);
			
		}else
		{
			PrintWriter out = response.getWriter();
			out.println("Success");
		}
		
	}
	public RequestTable  FilterProcess(Filter filter,List<RequestTable>FilterReqList,int Classification,int mode ) 
	{
		RequestTable TargetReq=null;//紀錄選重的預約者
		if(mode==1)
		{
			//第一段以路找尋
			//路到路
			filter.RoadToRoadFilter(FilterReqList);	
		}
	   	//檢查是否屬於新店偏遠山區 並過濾區域
	//	 filter.xindianRoadSplitAreaFilter(FilterReqList);	
		//檢查區域filter 如果屬於新店偏遠山區就不檢查 
	//	if(!filter.xindianRoadSplitArea)		
		filter.areaFilter(false,FilterReqList,Classification);	
		//檢查回廠區域 
		/*if(!filter.xindianRoadSplitArea)
			filter.endareaFilter(FilterReqList);		*/
		//檢查是否有足夠的休息時間
		filter.restFilter(FilterReqList);
		//旅行時間
		filter.DistanceTimeFilter(FilterReqList);		
		TargetReq = filter.MinFilter(FilterReqList);
		return TargetReq;
	}
	public int Modifyinfo(	LinkedList<RequestTable> OriginReqlist,RequestTable req,Map<Integer, RequestTable> IndexMap ,DriverTable TargetDriver,double TimeUnit,List<carGroup> car,List<reqGroup> requestTable)
	{
		int Arrangereqnum=0;
		//修改司機表ModifyOriginDriverTable("原始司機表","要排入司機", "要修改的資料表名稱");
		TargetDriver.ModifyOriginDriverTable(Variable,req,TimeUnit,TargetDriver, "userrequests",IndexMap,car);	
		//將預約者標記成已排過班
		req.Arrange = true;	
		OriginReqlist.remove(req);
		Arrangereqnum++;
		if(req.AssignSharing!=-1)
		{
			IndexMap.get(req.AssignSharing).Arrange=true;
			Arrangereqnum++;
			OriginReqlist.remove(IndexMap.get(req.AssignSharing));
		}
	    return Arrangereqnum;
	}
	//取得預約者list
	public 	LinkedList <RequestTable> readareaarray(List<reqGroup> requestTable,int mode)
	   {
	    	//初始化預約者陣列
			LinkedList <RequestTable> readearaarray = new LinkedList<RequestTable>();
		   
		   for(int areaindex=0;areaindex<Variable.areanum;areaindex++)//32個地區
		   {
			   for(int timeindex=0;timeindex<Variable.intervalnum;timeindex++)//時間總區間數
			   {
				   	for(int index=0;index<requestTable.get(areaindex).getreq(timeindex).size();index++)
				   	{		  
				   		
				   		if(requestTable.get(areaindex).getreq(timeindex).get(index).Arrange==false)				   			
				   		{
				   			
				   			if(requestTable.get(areaindex).getreq(timeindex).get(index).Status!=mode)//依傳進來的值讀取正常或候補的預約者
				   			{
				   				readearaarray.add(requestTable.get(areaindex).getreq(timeindex).get(index));
				   			}
				   			
				   		}
				   	}
				}
			}
		    return readearaarray;
		}
  	public void  checkdata(int interrupt) throws ClassNotFoundException, IOException, SQLException, BiffException, InterruptedException
	{
		 ResultSet rs = null;
		 String sqlQuery="SELECT * FROM `arrangedtable`  WHERE `date`='"+	Variable.date+"' and `arrangetime`='"+	Variable.time+"'";
		 String sqlQuery1="";
		 rs=	Variable.smt.executeQuery(sqlQuery); 
		 rs.first();
		 if(interrupt==0)
			 sqlQuery1="UPDATE userrequests SET Targetdrivers='null' ,arranged=0 WHERE arranged=1 AND `arrangedate`='"+Variable.date+"' and `arrangetime`='"+	Variable.time+"'";
		 if(interrupt==-1)
			 sqlQuery1="UPDATE userrequests SET Targetdrivers='null' ,arranged=-1 WHERE  `arrangedate`='"+Variable.date+"' and `arrangetime`='"+	Variable.time+"'";
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
  
  
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// TODO Auto-generated method stub

	}
	

}
