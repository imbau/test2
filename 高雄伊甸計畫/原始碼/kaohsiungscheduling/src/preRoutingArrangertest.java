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
@WebServlet("/PreRoutingArrangertest.view")
public class preRoutingArrangertest extends HttpServlet
{
	private static final long serialVersionUID = 1L;		
	 defineVariable Variable;//放置定義的變數	
	 
    /**
     * @throws Exception 
     * @see HttpServlet#HttpServlet()
     */
    public preRoutingArrangertest()
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
		//int ILFEnable = -1;	
		List<reqGroup> requestTable=new ArrayList<reqGroup>();	//以上車分類需求表		
		List<carGroup> car = new ArrayList<carGroup>();//車輛表
		List<reqGroup> TailRequestTable=new ArrayList<reqGroup>();	//以下車分類需求表	
		////////////////////////////////////////////////////////////////////////////		
		ILF ilf = null;
		System.out.println("優先地區排班開始");
		try
		{
			Variable = new defineVariable();//初始化定義變數		
			Variable.date = request.getParameter("arrangedate");//排班日期
			Variable.time = request.getParameter("arrangetime");//排班時間	
			//ilf = new ILF(Variable.con,Variable.date,Variable.time);//初始化歷史資料搜尋物件	
			//int mode = Integer.valueOf(request.getParameter("mode"));	//判別目前正在排的是正常班次或候補班次
			for(int i=0;i<Variable.areanum;i++)
			{
				car.add(new carGroup(Variable.intervalnum));	//初始化司機表
				requestTable.add(new reqGroup(Variable.intervalnum));//初始化預約表以上車區域分類
				TailRequestTable.add(new reqGroup(Variable.intervalnum));//初始化預約表以下車區域分類
			}
		
			 Variable.smt2.executeUpdate("UPDATE progress SET percent =0 WHERE `index` =6 and `date`='"+Variable.date+"' and `time`='"+Variable.time+"'");
			 Variable.smt2.executeUpdate("UPDATE `arrange_log` SET `progress`=3  WHERE `date`='"+Variable.date+"' and `time`='"+Variable.time+"'");

			//////////////////////////////////////////////////////////////////////
									
			//從資料庫讀取參數配置表
			Variable.rs = Variable.smt.executeQuery("select *from setting");//取得設定參數
			
			//取得一筆參數set
			while(Variable.rs.next())
			{
				//設定參數
				TimeUnit = Variable.rs.getFloat("Time unit");				
				//ILFEnable =Variable.rs.getInt("ILFEnable");
				FilterEnable[0] = Variable.rs.getInt("Filter1");
				FilterEnable[1] = Variable.rs.getInt("Filter2");
				FilterEnable[2] = Variable.rs.getInt("Filter3");
				FilterEnable[3] = Variable.rs.getInt("Filter4");
				FilterEnable[4] = Variable.rs.getInt("Filter5");	
				ilf = new ILF(Variable.con,Variable);//初始化歷史資料搜尋物件	
				DriverTable DriverTable = new DriverTable(0);				
			    //初始化讀取預約這資料物件		
				ReadTempRequest input = new ReadTempRequest(Variable.con, Variable.date, Variable.time);
				//讀取request預約表，
				requestTable = input.ReadOrderTable(requestTable,Variable);			
				TailRequestTable= input.ReadEndTable();
				//將所有需求放置IndexMap以利查詢上一班或下一班預約者的資訊
			    Map<Integer, RequestTable> IndexMap = new HashMap<Integer, RequestTable>();			   
			    Map<String, DriverTable> carIndexMap = new HashMap<String, DriverTable>();
			    IndexMap=RequestTable.getindexmap(IndexMap,requestTable,Variable);			    
				//讀取司機表	
				 car=DriverTable.readDrivertable(Variable.con, Variable.date, Variable.time, Variable.smt, car,ilf,IndexMap,Variable,requestTable);			    
				 if(Variable.errorcode<=-2)
						break;
				 //將所有司機放入map 以利查詢
				 carIndexMap=DriverTable.GetcarIndexMap(carIndexMap,car,Variable);
				//執行filter					
				Variable.recentPercent=0;//排班進度值歸零		
				//test
				double startTime,endTime,totTime;
				startTime = System.currentTimeMillis();
				//讀取超長旅行時間array				
				List<RequestTable> longtimearray=RequestTable.getlongtimearray(requestTable,Variable);					
				//讀取成對array				
			//	List<RequestTable> pairarray=RequestTable.getpairarray(longtimearray);	
				
				//讀取時間在下午4點到晚上8點往台北 
				List<RequestTable> nighttotaipeiarray =RequestTable.getnighttotaipeiarray(requestTable,Variable); 		    		
				
				//讀取晚上6點過後的需求者
				List<RequestTable> nightreqarray =RequestTable.getnightreqarray(requestTable,Variable); 		    		
			   
				//把夜晚的req加入下車地點在台北array後面
				nighttotaipeiarray.addAll(nightreqarray);				
				//去除重複資料 因為nighttotaipeiarray的資料有跟nightreqarray重疊
				nighttotaipeiarray=removeDuplicateWithOrder(nighttotaipeiarray);	
				
				//讀取上下車地點在東北角的需求 0代表讀取defineVariable裡的Specialeara array第一行
				List<RequestTable> NortheastSpecialearaarray =RequestTable.getSpecialareaarray(0,requestTable,TailRequestTable,IndexMap,Variable); 		    		
				//移除重複抓取的需求者 
				//原因:抓取東北角需求者的方法是同時地區同時段先抓以上車地點在東北角 
				//然後再抓以下車地點在東北角 
				//有些資料會重複抓取 需移除
				NortheastSpecialearaarray=removeDuplicateWithOrder(NortheastSpecialearaarray);
				
				List<RequestTable> Specialearaarray =RequestTable.getSpecialareaarray(2,requestTable,TailRequestTable,IndexMap,Variable); 		    		
			  //汐止深坑的預約者
				Specialearaarray=removeDuplicateWithOrder(Specialearaarray);
			
				//讀取上下車地點在三鶯林口桃園的需求 1代表讀取defineVariable裡的Specialeara array第一行
				List<RequestTable> Specialearaarray1 =RequestTable.getSpecialareaarray(1,requestTable,TailRequestTable,IndexMap,Variable); 		    		
				//移除重複資料
				Specialearaarray1=removeDuplicateWithOrder(Specialearaarray1);
				
				//需處理的預約者總數
				Variable.reqsize=longtimearray.size()+nighttotaipeiarray.size()+NortheastSpecialearaarray.size()+Specialearaarray.size()+Specialearaarray1.size();
				
				//進行成對流程
			/*	if(Variable.errorcode>-2)
					pairprocess(requestTable,car,FilterEnable,ilf,IndexMap,pairarray,TimeUnit,carIndexMap,DriverTable,longtimearray);
				RequestTable.modifyReqstatus(pairarray, Variable);*/
				//進行不成對.
				if(longtimearray.size()>0&&Variable.errorcode>-2)
					process(car,FilterEnable,ilf,IndexMap,longtimearray,TimeUnit,DriverTable,requestTable, 0);
				RequestTable.modifyReqstatus(longtimearray, Variable);
				//執行夜班流程
				if(nighttotaipeiarray.size()>0&&Variable.errorcode>-2)					
					process(car,FilterEnable,ilf,IndexMap,nighttotaipeiarray,TimeUnit,DriverTable,requestTable,1);
				RequestTable.modifyReqstatus(nighttotaipeiarray, Variable);		
			//第一輪讀取汐止先排 第二輪讀取新店
				for(int carrun=0;carrun<1;carrun++)
				 { 
					if(Variable.errorcode<=-2)
						break;
					//0代表讀取defineVariable裡的Specialearacar array第一行讀取汐止新店車子的index
					//放置carindex要去讀取某個區域的車子index
					int carindex=Variable.Specialareacar[0][carrun];				
					NortheastSpecialearaarray=northeastprocess	(0,carindex,car,FilterEnable,ilf,IndexMap,NortheastSpecialearaarray,TimeUnit,DriverTable,requestTable);
				 }				
				RequestTable.modifyReqstatus(NortheastSpecialearaarray, Variable);
			/*	
			//將未處理完的加入跟汐止一起排
				Specialearaarray.addAll(NortheastSpecialearaarray);
				//第一輪讀取汐止先排 第二輪讀取新店
				for(int carrun=0;carrun<2;carrun++)
				 { 
					if(Variable.errorcode<=-2)
						break;
					//0代表讀取defineVariable裡的Specialearacar array第一行讀取汐止新店車子的index
					//放置carindex要去讀取某個區域的車子index
					int carindex=Variable.Specialareacar[0][carrun];							
					Specialearaarray=northeastprocess	(2,carindex,car,FilterEnable,ilf,IndexMap,Specialearaarray,TimeUnit,DriverTable,requestTable);
				 }
				
				RequestTable.modifyReqstatus(Specialearaarray, Variable);*/
			/*	
				//第一輪讀取土城先排 第二輪讀取中和
				for(int carrun=0;carrun<3;carrun++)
				 { 
					if(Variable.errorcode<=-2)
						break;
					//0代表讀取defineVariable裡的Specialearacar array第一行讀取土城中和車子的index
					//放置carindex要去讀取某個區域的車子index
					int carindex=Variable.Specialareacar[1][carrun];
					Specialearaarray1=Southwestprocess(carrun,carindex,car,FilterEnable,ilf,IndexMap,Specialearaarray1,TimeUnit,DriverTable,requestTable);
				 }
				RequestTable.modifyReqstatus(Specialearaarray1, Variable);
				
				*/
				if(Variable.errorcode<=-2)
				{
					break;
				}
			    endTime = System.currentTimeMillis();
			    //取得程式結束的時間
			    totTime = endTime - startTime;
			    System.out.println("Using Time: " + totTime+" ms");
				System.out.println(":優先區域排班結束:");
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
				//執行完排班寫回進度100
				Variable.smt2.executeUpdate("UPDATE progress SET percent =100 WHERE `index` =6 and `date`='"+Variable.date+"' and `time`='"+Variable.time+"'");
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
	//成對req filterprocess
	public boolean filterprocess(List<carGroup> car,int run,List<DriverTable> filterDriverTable,List<DriverTable> filterDriverTable1,RequestTable Req
			,RequestTable Req1,double TimeUnit,Map<Integer, RequestTable> IndexMap,ILF ilf,int[] FilterEnable)
	{
		 PreRountingArrangerFilter reqfilter=null;
		 PreRountingArrangerFilter req1filter=null;
		 DriverTable TargetDriver=null;
		 boolean Result=false;
		 try
		 {
			 //filter初始化	
			reqfilter = new PreRountingArrangerFilter(Variable.TolerableTime,FilterEnable,Req,TimeUnit,Req.Number, IndexMap,Variable, ilf);
		
			//filter初始化					 
			req1filter = new PreRountingArrangerFilter(Variable.TolerableTime,FilterEnable,Req1,TimeUnit,Req1.Number, IndexMap,Variable, ilf);

			 //車種filter
			 reqfilter.CarFilter(filterDriverTable);
			 //車種filter
			 req1filter.CarFilter(filterDriverTable1);
			//過濾新手司機
			 reqfilter.AssignSharingCarFilter(filterDriverTable);	
			//過濾新手司機
			 req1filter.AssignSharingCarFilter(filterDriverTable1);	
			 //檢查時段filter
			 reqfilter.StatusFilter(filterDriverTable);	
			 //檢查時段filter
			 req1filter.StatusFilter(filterDriverTable1);	
			 
			 //檢查上下班時段一小時內不可接超過2趟
			 reqfilter.NoMoreThanTwoFilter(filterDriverTable);				
			 req1filter.NoMoreThanTwoFilter(filterDriverTable1);				 
			 
			//檢查區域filter
			 reqfilter.areaFilter1(filterDriverTable,run);
			//檢查區域filter
			 req1filter.areaFilter1(filterDriverTable1,run);
			 
			//檢查回廠區域filter
			 reqfilter.endareaFilter(filterDriverTable);
			 //檢查回廠區域filter
			 req1filter.endareaFilter(filterDriverTable1);
			 
			//檢查是否有足夠的休息時間
			 reqfilter.restFilter(filterDriverTable);
			//檢查是否有足夠的休息時間
			 req1filter.restFilter(filterDriverTable1);
			 
			//檢查是否來得及接當前預約者與來得及接下一班預約者
			 reqfilter.DistanceTimeFilter(filterDriverTable);		 
			 //檢查是否來得及接當前預約者與來得及接下一班預約者
			 req1filter.DistanceTimeFilter(filterDriverTable1);	
			  
			 //檢查是否來得及接當前預約者與來得及接下一班預約者
			 reqfilter.AssignSharingDistanceTimeFilter(filterDriverTable);		
			 //檢查是否來得及接當前預約者與來得及接下一班預約者
			 req1filter.AssignSharingDistanceTimeFilter(filterDriverTable1);	
			 
			 //取得最佳司機
			if(run==0)
			{
			 TargetDriver = MinFilter(filterDriverTable,filterDriverTable1);		
			 if(TargetDriver != null)
			 {	
				 double IntervalSec = 0.5 * 3600;//將interval的時間由小時為單位轉成由秒為單位
				 int startInterval = (int)((TargetDriver.StartTime+1800+9000) / IntervalSec);//計算頭班所在區間
					//計算尾班所在區間
				 int endInterval = (((TargetDriver.EndTime+2700-9000) % IntervalSec)  > 0.0 ? (int)((TargetDriver.EndTime+2700-9000)/ IntervalSec) : (int)((TargetDriver.EndTime+2700-9000)/ IntervalSec)-1);
				 int startindex=startInterval;//頭班往後2小時
				 int endindex=endInterval;//尾班往前2小時				
				
				 //同步兩個需求的所占用的格數
				 //第1個預約者的區間 用原本上下車時間去算區間
				 int StartInterval = (int)( (Req.originalStartTime) / IntervalSec);//上車時間在一天中的interval index
				 //下車時間在一天中的interval index
				 int EndInterval = (((Req.originalDestinationTime) % IntervalSec)  > 0.0 ? (int)((Req.originalDestinationTime) / IntervalSec) : (int)((Req.originalDestinationTime) / IntervalSec) - 1);
				
				 //第2個預約者的區間 用原本上下車時間去算區間
				 int StartInterval1 = (int)( (Req1.originalStartTime) / IntervalSec);//上車時間在一天中的interval index
				 //下車時間在一天中的interval index
				 int EndInterval1 = (((Req1.originalDestinationTime) % IntervalSec)  > 0.0 ? (int)((Req1.originalDestinationTime) / IntervalSec) : (int)((Req1.originalDestinationTime) / IntervalSec) - 1);
				
				 
				 //先複製原本休息區間的array給temprelaxarry
				 TargetDriver.temprelaxarry=new ArrayList<String>(TargetDriver.relaxarry);
				 
				 
				 //先刪除第1個預約者所佔的區間
				 for(int index = StartInterval; index <= EndInterval; index++)
				 {
					//有落在休息區間就刪除
					 if(index>=startindex&&index<=endindex)
						 TargetDriver.temprelaxarry.remove(String.valueOf(index));
				 }
				 //刪除第2個預約者所佔的區間
				 for(int index = StartInterval1; index <= EndInterval1; index++)
				 {
					//有落在休息區間就刪除
					 if(index>=startindex&&index<=endindex)
						 TargetDriver.temprelaxarry.remove(String.valueOf(index));
				 }
							 
				 //修改司機表ModifyOriginDriverTable("原始司機表","要排入司機", "要修改的資料表名稱");
				 TargetDriver.ModifyOriginDriverTable(Variable,Req,TimeUnit,TargetDriver, "userrequests",IndexMap,car);	
				 TargetDriver.ArrangedCount++;
				 TargetDriver.ModifyOriginDriverTable(Variable,Req1,TimeUnit,TargetDriver, "userrequests",IndexMap,car);
				 TargetDriver.ArrangedCount++;
				 //同步需求表
				Req.Arrange=true;
				Req1.Arrange=true;
				if(Req.AssignSharing!=-1)
					IndexMap.get(Req.AssignSharing).Arrange=true;			
				if(Req1.AssignSharing!=-1)
					IndexMap.get(Req1.AssignSharing).Arrange=true;
				Variable.DealReqNum++;//更新進度表
				Variable.recentPercent=(int)((float)Variable.DealReqNum/Variable.reqsize*100);
				Variable.smt2.executeUpdate("UPDATE progress SET percent ="+Variable.recentPercent+" WHERE `index` =6 and `date`='"+Variable.date+"' and `time`='"+Variable.time+"'");
				Result=true;
			 }
		   }
		   else
		   {
			 TargetDriver = MinFilter(filterDriverTable,filterDriverTable1);		
			 if(TargetDriver != null)
			 {	
				 double IntervalSec = 0.5 * 3600;//將interval的時間由小時為單位轉成由秒為單位
				 int startInterval = (int)((TargetDriver.StartTime+1800+9000) / IntervalSec);//計算頭班所在區間
					//計算尾班所在區間
				 int endInterval = (((TargetDriver.EndTime+2700-9000) % IntervalSec)  > 0.0 ? (int)((TargetDriver.EndTime+2700-9000)/ IntervalSec) : (int)((TargetDriver.EndTime+2700-9000)/ IntervalSec)-1);
				 int startindex=startInterval;//頭班往後2小時
				 int endindex=endInterval;//尾班往前2小時				
				
				 //同步兩個需求的所占用的格數
				 //第1個預約者的區間 用原本上下車時間去算區間
				 int StartInterval = (int)( (Req.originalStartTime) / IntervalSec);//上車時間在一天中的interval index
				 //下車時間在一天中的interval index
				 int EndInterval = (((Req.originalDestinationTime) % IntervalSec)  > 0.0 ? (int)((Req.originalDestinationTime) / IntervalSec) : (int)((Req.originalDestinationTime) / IntervalSec) - 1);
				
				 //第2個預約者的區間 用原本上下車時間去算區間
				 int StartInterval1 = (int)( (Req1.originalStartTime) / IntervalSec);//上車時間在一天中的interval index
				 //下車時間在一天中的interval index
				 int EndInterval1 = (((Req1.originalDestinationTime) % IntervalSec)  > 0.0 ? (int)((Req1.originalDestinationTime) / IntervalSec) : (int)((Req1.originalDestinationTime) / IntervalSec) - 1);
				
				 
				 //先複製原本休息區間的array給temprelaxarry
				 TargetDriver.temprelaxarry=new ArrayList<String>(TargetDriver.relaxarry);
				 
				 
				 //先刪除第1個預約者所佔的區間
				 for(int index = StartInterval; index <= EndInterval; index++)
				 {
					//有落在休息區間就刪除
					 if(index>=startindex&&index<=endindex)
						 TargetDriver.temprelaxarry.remove(String.valueOf(index));
				 }
				 //刪除第2個預約者所佔的區間
				 for(int index = StartInterval1; index <= EndInterval1; index++)
				 {
					//有落在休息區間就刪除
					 if(index>=startindex&&index<=endindex)
						 TargetDriver.temprelaxarry.remove(String.valueOf(index));
				 }
				 
				 
					//修改司機表ModifyOriginDriverTable("原始司機表","要排入司機", "要修改的資料表名稱");
				 	 TargetDriver.ModifyOriginDriverTable(Variable,Req,TimeUnit,TargetDriver, "userrequests",IndexMap,car);	
					 TargetDriver.ArrangedCount++;
					 TargetDriver.ModifyOriginDriverTable(Variable,Req1,TimeUnit,TargetDriver, "userrequests",IndexMap,car);
					 TargetDriver.ArrangedCount++;
					 //同步需求表
					 Req.Arrange=true;
					 Req1.Arrange=true;
					 if(Req.AssignSharing!=-1)
						 IndexMap.get(Req.AssignSharing).Arrange=true;					 
					 if(Req1.AssignSharing!=-1)
						 IndexMap.get(Req1.AssignSharing).Arrange=true;		
					 Variable.DealReqNum++;//更新進度表
					 Variable.recentPercent=(int)((float)Variable.DealReqNum/Variable.reqsize*100);
					 Variable.smt2.executeUpdate("UPDATE progress SET percent ="+Variable.recentPercent+" WHERE `index` =6 and `date`='"+Variable.date+"' and `time`='"+Variable.time+"'");
					 Result=true;
			 }
			 else
			 {
				 DriverTable TargetDriver1=null;
				 DriverTable TargetDriver2=null;
				 
				//第1個需求者
				 TargetDriver1=reqfilter.MinFilter(filterDriverTable,false);
				 if(TargetDriver1 != null)
				 {
					 double IntervalSec = 0.5 * 3600;//將interval的時間由小時為單位轉成由秒為單位
					 int startInterval = (int)((TargetDriver1.StartTime+1800+9000) / IntervalSec);//計算頭班所在區間
						//計算尾班所在區間
					 int endInterval = (((TargetDriver1.EndTime+2700-9000) % IntervalSec)  > 0.0 ? (int)((TargetDriver1.EndTime+2700-9000)/ IntervalSec) : (int)((TargetDriver1.EndTime+2700-9000)/ IntervalSec)-1);
					 int startindex=startInterval;//頭班往後2小時				
					 int endindex=endInterval;//尾班往前2小時	
					 //同步兩個需求的所占用的格數
					 //第1個預約者的區間 用原本上下車時間去算區間
					 int StartInterval = (int)( (Req.originalStartTime) / IntervalSec);//上車時間在一天中的interval index
					 //下車時間在一天中的interval index
					 int EndInterval = (((Req.originalDestinationTime) % IntervalSec)  > 0.0 ? (int)((Req.originalDestinationTime) / IntervalSec) : (int)((Req.originalDestinationTime) / IntervalSec) - 1);
					 //先複製原本休息區間的array給temprelaxarry
					 TargetDriver1.temprelaxarry=new ArrayList<String>(TargetDriver1.relaxarry);
					 
					 
					 //先刪除第1個預約者所佔的區間
					 for(int index = StartInterval; index <= EndInterval; index++)
					 {
						//有落在休息區間就刪除
						 if(index>=startindex&&index<=endindex)
							 TargetDriver1.temprelaxarry.remove(String.valueOf(index));
					 }
					 //修改司機表ModifyOriginDriverTable("原始司機表","要排入司機", "要修改的資料表名稱");
					 TargetDriver1.ModifyOriginDriverTable(Variable,Req,TimeUnit,TargetDriver1, "userrequests",IndexMap,car);	
				   
				   TargetDriver1.ArrangedCount++;
				   //同步需求表
				   Req.Arrange=true;
				   if(Req.AssignSharing!=-1)
					   IndexMap.get(Req.AssignSharing).Arrange=true;				   
				 }
				 
				 //第2個需求者
				 TargetDriver2=req1filter.MinFilter(filterDriverTable1,false);
				 if(TargetDriver2 != null)
				 {
					 double IntervalSec = 0.5 * 3600;//將interval的時間由小時為單位轉成由秒為單位
					 int startInterval = (int)((TargetDriver2.StartTime+1800+9000) / IntervalSec);//計算頭班所在區間
						//計算尾班所在區間
					 int endInterval = (((TargetDriver2.EndTime+2700-9000) % IntervalSec)  > 0.0 ? (int)((TargetDriver2.EndTime+2700-9000)/ IntervalSec) : (int)((TargetDriver2.EndTime+2700-9000)/ IntervalSec)-1);
					 int startindex=startInterval;//頭班往後2小時
					 int endindex=endInterval;//尾班往前2小時	
					 //第2個預約者的區間 用原本上下車時間去算區間
					 int StartInterval1 = (int)( (Req1.originalStartTime) / IntervalSec);//上車時間在一天中的interval index
					 //下車時間在一天中的interval index
					 int EndInterval1 = (((Req1.originalDestinationTime) % IntervalSec)  > 0.0 ? (int)((Req1.originalDestinationTime) / IntervalSec) : (int)((Req1.originalDestinationTime) / IntervalSec) - 1);
					//先複製原本休息區間的array給temprelaxarry
					 TargetDriver2.temprelaxarry=new ArrayList<String>(TargetDriver2.relaxarry);
					//刪除第2個預約者所佔的區間
					 for(int index = StartInterval1; index <= EndInterval1; index++)
					 {
						//有落在休息區間就刪除
						 if(index>=startindex&&index<=endindex)
							 TargetDriver2.temprelaxarry.remove(String.valueOf(index));
					 }				 
					//修改司機表ModifyOriginDriverTable("原始司機表","要排入司機", "要修改的資料表名稱");
				   TargetDriver2.ModifyOriginDriverTable(Variable,Req1,TimeUnit,TargetDriver2, "userrequests",IndexMap,car);	
				   TargetDriver2.ArrangedCount++;
				   //同步需求表
				   Req1.Arrange=true;
				   if(Req1.AssignSharing!=-1)
					   IndexMap.get(Req1.AssignSharing).Arrange=true;				   
				   }
				 Result=true;		
			 }
			}
		 } catch (Exception e) 
		 {
			 // TODO Auto-generated catch block
			 System.out.println("排班出錯");
		 }
		 return Result;		
	}
	//特殊區域的filterprocess :東北角與三鶯林口土城
/*public boolean Specialearafilterprocess(List<carGroup> car,List<DriverTable> filterDriverTable,RequestTable Req
				,double TimeUnit,Map<Integer, RequestTable> IndexMap,ILF ilf,int[] FilterEnable) throws Exception
	{
		boolean Found=false;
		DriverTable TargetDriver=null;
		PreRountingArrangerFilter filter=null;
		
		for(int Classification=1;Classification<=5;Classification++)
		{
			List<DriverTable> DriverTable= new LinkedList<DriverTable>(filterDriverTable);
			TargetDriver=null;
			//filter初始化	
			 //filter初始化					 
			 filter = new PreRountingArrangerFilter(Variable.TolerableTime,FilterEnable,Req, TimeUnit,Req.Number, IndexMap,Variable, ilf);
			 //車種filter
			 filter.CarFilter(DriverTable);
			// filter.RoadToRoadFilter(filterDriverTable);
			 //過濾新手司機
			 filter.AssignSharingCarFilter(DriverTable);		
			 //檢查時段filter
			 filter.StatusFilter(DriverTable);	
			 //檢查上下班時段一小時內不可接超過2趟
			 filter.NoMoreThanTwoFilter(filterDriverTable);					
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
			 filter.AssignSharingDistanceTimeFilter(DriverTable);
			 //取得最佳司機	
			 TargetDriver = filter.MinFilter(DriverTable,false);
			 if(TargetDriver != null)
			 {
				//修改司機表ModifyOriginDriverTable("原始司機表","要排入司機", "要修改的資料表名稱");			
				TargetDriver.ModifyOriginDriverTable(Variable,Req,TimeUnit,TargetDriver, "userrequests",IndexMap,car);	
				TargetDriver.ArrangedCount++;

				 //同步需求表
				 Req.Arrange=true;
				 if(Req.AssignSharing!=-1)
					 IndexMap.get(Req.AssignSharing).Arrange=true;	
				 Variable.DealReqNum++;//更新進度表
				 Variable.recentPercent=(int)((float)Variable.DealReqNum/Variable.reqsize*100);
				 Variable.smt2.executeUpdate("UPDATE progress SET percent ="+Variable.recentPercent+" WHERE `index` =6 and `date`='"+Variable.date+"' and `time`='"+Variable.time+"'");
				 Found=true;
				 break;
			 }else if(Classification==5)
			 {
				 Found=false;
			 }
		}
		return Found;			
	}*/
	//處理非對稱長時間與夜班去台北的filterprocess
	public RequestTable filterprocess(boolean NightFlag,Filter filter,List<RequestTable> FilterReqList,int Classification)
	{
		
		  RequestTable TagetReq=null;//選重的預約者		
		//車種filter
		 filter.CarFilter(FilterReqList);		
		 if(Classification<=1)
		 {
				//第一段以路找尋
				//路到路
				filter.RoadToRoadFilter(FilterReqList);	
		 }
		 //過濾新手司機
		 filter.AssignSharingCarFilter(FilterReqList);		
		 //檢查時段filter
		 filter.StatusFilter(FilterReqList);		
		 //對應的路之間不接預約者
		 filter.AreaCorrespond(FilterReqList);	
		//特殊車不接超過五趟
		filter.SpecialCarFilter(FilterReqList);			 
		 //檢查上下班時段一小時內不可接超過2趟
		 filter.NoMoreThanTwoFilter(FilterReqList);		
		//檢查區域filter
		 filter.areaFilter(NightFlag,FilterReqList,Classification);		
		//檢查回廠區域filter
		 //filter.endareaFilter(FilterReqList);		
		//檢查是否有足夠的休息時間
		 filter.restFilter(FilterReqList);
		 //檢查是否來得及接當前預約者與來得及接下一班預約者
		 filter.DistanceTimeFilter(FilterReqList);	
		 TagetReq = filter.MinFilter(FilterReqList);	  
		  return TagetReq;
	}
	//處理西南角程序
   public List<RequestTable> Southwestprocess(int carrun,int carindex,List<carGroup> car,int[] FilterEnable,ILF ilf,Map<Integer, RequestTable> IndexMap
					,List<RequestTable> RequestArray,double TimeUnit,DriverTable DriverTable,List<reqGroup> requestTable) 
	{
	   if(RequestArray.size()>0)
	   {
		   boolean NightFlag=false;
		   //司機表
		   LinkedList<DriverTable> OriginDriverTable= new LinkedList<DriverTable>();
		   if(carrun==0)//讀取土城的車
		   {
			   OriginDriverTable =DriverTable.getareafilterDriverTable(carindex,car,Variable); 
		   }
		   else if(carrun==1) //四大金釵
		   {
			   OriginDriverTable =DriverTable.getfFournobileDriverTable(car,Variable); 
			}
		   else if(carrun==2)  // 讀取中和車
		   {			   
			   OriginDriverTable =DriverTable.getareafilterDriverTable(carindex,car,Variable); 
			}
		   for(int i = 0; i < OriginDriverTable.size();i++ )
		   {
			   //排班處理的時間順序，依伊甸目前6點半是首班所以設定13			
			   for(int tineindex=13;tineindex<=46;tineindex++)
			   {
				   //目前要排的時間點須在司機上班時段
				   if(tineindex>OriginDriverTable.get(i).StartTimeInterval&&tineindex<OriginDriverTable.get(i).EndTimeInterval)
					   for(int Classification=1;Classification<=7;Classification++)//依據區域權重高低去搜尋
						{
						   //判斷是否要用夜班權重
						   NightFlag=false;
						   RequestTable TagetReq=null;//選重的預約者
						   //取得可用的預約表
						   List<RequestTable> FilterReqList = new LinkedList<RequestTable>(RequestArray);
							try 
							{
								//filter初始化		
								Filter filter= new Filter(tineindex,FilterEnable, OriginDriverTable.get(i),TimeUnit,IndexMap,Variable,ilf);
								if(OriginDriverTable.get(i).StartTime>43200)//依司機的出班時間去決定是否啟用夜班權重，如果大於中午12點就啟用夜班權重
									 NightFlag=true;
								TagetReq=filterprocess(NightFlag,filter,FilterReqList,Classification);
								if(TagetReq!=null)
								{												
									Modifyinfo(RequestArray,TagetReq, IndexMap ,OriginDriverTable.get(i), TimeUnit, car,requestTable);			
									break;
								}
							} catch (Exception e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
				   }
			   }
		   }
				return RequestArray;
	}
		//處理東北角程序
		public List<RequestTable> northeastprocess(int specialareaindex,int carindex,List<carGroup> car,int[] FilterEnable,ILF ilf,Map<Integer, RequestTable> IndexMap
				,List<RequestTable> RequestArray,double TimeUnit,DriverTable DriverTable,List<reqGroup> requestTable ) 
		{
			if(RequestArray.size()>0)
			{
				boolean NightFlag=false;			
				//司機表
				LinkedList<DriverTable> OriginDriverTable =DriverTable.getareafilterDriverTable(carindex,car,Variable); 
			  
				//根據specialareaindex過濾車子
				if(!(specialareaindex==2&&carindex==0))//在排汐止的預約者時取得汐止車不過濾
					filterCar(OriginDriverTable, specialareaindex, IndexMap);				
				for(int i = 0; i < OriginDriverTable.size();i++ )
				{
					//排班處理的時間順序，依伊甸目前6點半是首班所以設定13			
					for(int tineindex=13;tineindex<=46;tineindex++)
					{
						//目前要排的時間點須在司機上班時段
						 if(tineindex>OriginDriverTable.get(i).StartTimeInterval&&tineindex<OriginDriverTable.get(i).EndTimeInterval)
							 for(int Classification=1;Classification<=7;Classification++)//依據區域權重高低去搜尋
							 {
								 //判斷是否要用夜班權重
								 NightFlag=false;
								 RequestTable TagetReq=null;//選重的預約者
								 //取得可用的預約表
								 List<RequestTable> FilterReqList = new LinkedList<RequestTable>(RequestArray);
								 try 
									{
								    	 //filter初始化		
								    	Filter filter= new Filter(tineindex,FilterEnable, OriginDriverTable.get(i),TimeUnit,IndexMap,Variable,ilf);
								    	if(OriginDriverTable.get(i).StartTime>43200)//依司機的出班時間去決定是否啟用夜班權重，如果大於中午12點就啟用夜班權重
								    		NightFlag=true;
								    	TagetReq=filterprocess(NightFlag,filter,FilterReqList,Classification);
								    	if(TagetReq!=null)
										{
											Modifyinfo(RequestArray,TagetReq, IndexMap ,OriginDriverTable.get(i), TimeUnit, car,requestTable);			
											
											break;
										}
									 } catch (Exception e)
									 {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
							 }
					}
					
					
				}
			}
			return RequestArray;
		}	
	//夜班程序及不成對旅行時間長的預約者
	public void process(List<carGroup> car,int[] FilterEnable,ILF ilf,Map<Integer, RequestTable> IndexMap
			,List<RequestTable> RequestArray,double TimeUnit,DriverTable DriverTable,List<reqGroup> requestTable,int Night) 
	{
		boolean NightFlag=false;
		int tineindex=13;//排班處理的時間順序，依伊甸目前6點半是首班所以設定13
		if(Night==1)//夜班車則是中午12點開始 所以設定23
			tineindex=23;//排班處理的時間順序
		for(;tineindex<=46;tineindex++)
		{
			//司機表
			LinkedList<DriverTable> OriginDriverTable =DriverTable.filterDriverTable(car,DriverTable.carsize,tineindex,0,Variable);
			if(OriginDriverTable.size()==0)
				continue;
			//依據區域權重高低去搜尋			
			for(int Classification=1;Classification<=7;Classification++)
			{
				for(int i = 0; i < OriginDriverTable.size();i++ )
				{
					if(OriginDriverTable.size()==0)
						continue;
					RequestTable TagetReq=null;//選重的預約者
					//取得可用的預約表
					List<RequestTable> FilterReqList = new LinkedList<RequestTable>(RequestArray);
				    try 
					{
				    	 //filter初始化		
				    	Filter filter= new Filter(tineindex,FilterEnable, OriginDriverTable.get(i),TimeUnit,IndexMap,Variable,ilf);
				    	if(OriginDriverTable.get(i).StartTime>43200)//依司機的出班時間去決定是否啟用夜班權重，如果大於中午12點就啟用夜班權重
				    		NightFlag=true;
				    	TagetReq=filterprocess(NightFlag,filter,FilterReqList,Classification);
				    	if(TagetReq!=null)
						{
							Modifyinfo(RequestArray,TagetReq, IndexMap ,OriginDriverTable.get(i), TimeUnit, car,requestTable);			
							//有排入班次的司機移除，避免重複進去排班
							OriginDriverTable.remove(i);
							i--;
						}
					 } catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	public void pairprocess(List<reqGroup> requestTable,List<carGroup> car,int[] FilterEnable,ILF ilf,Map<Integer, RequestTable> IndexMap
			,List<RequestTable> pairarray,double TimeUnit,Map<String, DriverTable> carIndexMap,DriverTable DriverTable,	List<RequestTable> longtimearray) throws Exception
	{
		
		 for(int j=0;j<pairarray.size();)
		  {
			 if(Variable.errorcode<=-2)
					break;
			if(Math.abs(pairarray.get(j).DestinationTime-pairarray.get(j+1).OriginTime)<Variable.differencevalue)
			{
			 //兩個都未排
		    if(!pairarray.get(j).Arrange&&!pairarray.get(j+1).Arrange)
		    {
		    	 for(int run=0;run<2;run++)
				 {
		    		 //第一個req的車子
		    		 List<DriverTable> filterDriverTable =DriverTable.getfilterDriverTable(car,Variable);
			    	 //第二個req車子
		    		 List<DriverTable> filterDriverTable1 =DriverTable.getfilterDriverTable(car,Variable);
					 if(filterprocess(car,run,filterDriverTable,filterDriverTable1,pairarray.get(j)
			    			 ,pairarray.get(j+1),TimeUnit,IndexMap,ilf,FilterEnable))
			    	 {
			    		break;
			    	 }					
				 }
		    	 j+=2;
		    }
		    else if(pairarray.get(j).Arrange&&pairarray.get(j+1).Arrange)//兩個都已排過
		    {
		    	j+=2;
		    	continue;				    	
		    }
		    else 
		    {
		    	//其中一個未排
		    	if(pairarray.get(j).Arrange)
		    	{
		    		DriverTable TargetDriver=null;	
		    		TargetDriver=carIndexMap.get(pairarray.get(j).Targetdrivers);
		    		//修改司機表ModifyOriginDriverTable("原始司機表","要排入司機", "要修改的資料表名稱");				    		
		    		DriverTable.ModifyOriginDriverTable(Variable,pairarray.get(j+1),TimeUnit,TargetDriver,"userrequests",IndexMap,car);
		    		//同步需求表
		    		requestTable.get(pairarray.get(j+1).index[0]).getreq(pairarray.get(j+1).index[1]).get(pairarray.get(j+1).index[2]).Arrange=true;
		    		if(pairarray.get(j+1).AssignSharing!=-1)
		    			IndexMap.get(pairarray.get(j+1).AssignSharing).Arrange=true;
		    	}
		    	else
		    	{
		    		DriverTable TargetDriver=null;	
		    		TargetDriver=carIndexMap.get(pairarray.get(j+1).Targetdrivers);
		    		//修改司機表ModifyOriginDriverTable("原始司機表","要排入司機", "要修改的資料表名稱");				    		
		    		DriverTable.ModifyOriginDriverTable(Variable,pairarray.get(j),TimeUnit,TargetDriver,"userrequests",IndexMap,car);
		    		//同步需求表
		    		requestTable.get(pairarray.get(j).index[0]).getreq(pairarray.get(j).index[1]).get(pairarray.get(j).index[2]).Arrange=true;
		    		if(pairarray.get(j).AssignSharing!=-1)
		    			IndexMap.get(pairarray.get(j).AssignSharing).Arrange=true;
		    	}
		    	j+=2;
		     }
		  }
		 else
		 {
			if(!pairarray.get(j).Arrange)
			{
				longtimearray.add(pairarray.get(j));
			}
			
			if(!pairarray.get(j+1).Arrange)		    
			{
				longtimearray.add(pairarray.get(j+1));
			}
			j+=2;
		  }
	    }
	}
	
	public DriverTable MinFilter(List<DriverTable> DriverList,List<DriverTable> DriverList1) throws IOException
	{		
		DriverTable TargetDriver=null;//預約者選重的司機	
		List<DriverTable> CandidateDriverList=new ArrayList<DriverTable>(200);
		if(DriverList.size()>0&&DriverList1.size()>0)
		{
			
			for(int i=0;i<DriverList.size();i++)
			{
				for(int j=0;j<DriverList1.size();j++)
				{
					if(DriverList.get(i).ID.indexOf(DriverList1.get(j).ID)!=-1)
					{
						CandidateDriverList.add(DriverList.get(i));
					}
				}
			}
		}
		//司機的總數要大於0才能選
		int minValue =999;//紀錄司機到預約者的最短距離
		
		for(int i = 0; i < CandidateDriverList.size(); i++)
		{//如果此車輛抵達此預約之上車地點所花費的時間較記錄的時間短
		    if(CandidateDriverList.get(i).StartDistanceValue< minValue)
			  {
			   //更新交通時間紀錄
			   minValue = CandidateDriverList.get(i).StartDistanceValue;
			   //更新選重車輛							
			  TargetDriver=CandidateDriverList.get(i);
			  }	
		}
	
		return TargetDriver;
	}
	public   static   List<RequestTable>  removeDuplicateWithOrder(List<RequestTable> list)  
	{ 
		for(int z=0;z<list.size()-1;z++)
		{
			for(int j=list.size()-1;j>z;j--)
			{
				if(list.get(j).equals(list.get(z)))  
				{ 
			        list.remove(j); 
			    } 
			}
		}
	     return list;
	}
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
	public void Modifyinfo(	List<RequestTable> OriginReqlist,RequestTable req,Map<Integer, RequestTable> IndexMap ,DriverTable TargetDriver,double TimeUnit,List<carGroup> car,List<reqGroup> requestTable)
	{
		//修改司機表ModifyOriginDriverTable("原始司機表","要排入司機", "要修改的資料表名稱");
		TargetDriver.ModifyOriginDriverTable(Variable,req,TimeUnit,TargetDriver, "userrequests",IndexMap,car);	
		//將預約者標記成已排過班
		req.Arrange = true;	
		OriginReqlist.remove(req);
		Variable.DealReqNum++;//更新進度表
		if(req.AssignSharing!=-1)
		{
			IndexMap.get(req.AssignSharing).Arrange=true;
			Variable.DealReqNum++;//更新進度表
			OriginReqlist.remove(IndexMap.get(req.AssignSharing));
		}		
		Variable.recentPercent=(int)((float)Variable.DealReqNum/Variable.reqsize*100);
		try {
			Variable.smt2.executeUpdate("UPDATE progress SET percent ="+Variable.recentPercent+" WHERE `index` =6 and `date`='"+Variable.date+"' and `time`='"+Variable.time+"'");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//過濾沒包含區域的車輛以利作區域集中排班
	public void filterCar(LinkedList<DriverTable> OriginDriverTable,int  specialareaindex,Map<Integer, RequestTable> IndexMap )
	{
		for(int i = 0; i < OriginDriverTable.size();i++ )
		{
			if(OriginDriverTable.get(i).CallNum.indexOf("52")!=-1)
				System.out.println("222");
			String[] Area=new String[2];
			//取到中間結束
			int StopTimeIndex=(OriginDriverTable.get(i).StartTimeInterval+OriginDriverTable.get(i).EndTimeInterval)/2;
			//先取的上半部最後下車的區域(司機node,起始區間,結束區間,1代表往後找-1代表往前找,預約者映射表)
			Area[0]=GetArea(OriginDriverTable.get(i), OriginDriverTable.get(i).StartTimeInterval, StopTimeIndex, 1, IndexMap);
			
			//取的下半部最後下車的區域(司機node,起始區間,結束區間,1代表往後找-1代表往前找,預約者映射表)
			Area[1]=GetArea(OriginDriverTable.get(i), OriginDriverTable.get(i).EndTimeInterval, StopTimeIndex, -1, IndexMap);
			
			for(int area=0;area<Variable.Specialarea[specialareaindex].length;area++)
			{
				if(Variable.switchareaindex(Area[0])==Variable.Specialarea[specialareaindex][area]
								 ||Variable.switchareaindex(Area[1])==Variable.Specialarea[specialareaindex][area])
				{
					//代表有包含區域				
					break;
				}else if(area==(Variable.Specialarea[specialareaindex].length-1))	
				{
					//如果找到下班的間隔沒包含區域就刪除
					OriginDriverTable.remove(i);
					i--;
				}
			}	
		}	
	}
	//取得到中點最後上下車的區域
	public String GetArea( DriverTable driver,int starttimeindex,int stoptimeindex,int direction,Map<Integer, RequestTable> indexmap)
	{
		String Area="";			
		for(int timeindex=starttimeindex;timeindex<=stoptimeindex;timeindex=timeindex+direction)
		{
			if(driver.TimeInterval[timeindex].indexOf("未排班")==-1&&driver.TimeInterval[timeindex].indexOf("不上班")==-1)
			{
				 String[]  temp =driver.TimeInterval[timeindex].split("_");
				 RequestTable Requst= indexmap.get(Integer.valueOf(temp[0]));
				 if(starttimeindex<stoptimeindex)
					 Area=Requst.Destinationarea;
				 else
					 Area=Requst.Originarea;
			}
	
		}
		return Area;
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// TODO Auto-generated method stub

	}
	

}
