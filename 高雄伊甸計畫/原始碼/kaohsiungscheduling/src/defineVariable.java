import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;



public class defineVariable {	
	Connection con = null;
	Statement smt = null,smt2=null;
	ResultSet  rs=null,rs2 = null,rs3=null;				
	String date = null;
    String time = null;
    double timeinterval=0.0;
    double recentLat = -1.0, recentLon = -1.0;//用來記錄上台車輛的出車地址的經緯度，加入先排入每輛車頭尾班的機制後，此部分的功能效用不大
    int ProcessTableIndex=-1;  
    int DealReqNum=0;  
    int TolerableTime =600;
	int tolerableStartTime =1800;
	int tolerableEndTime = 2700;	
	int tolerablebacktime= 4500;	
	int IntervalSec=1800;
	int morningpeaktime=900;//上午尖峰時刻延遲時間
	int afternoonpeaktime=1200;//下午尖峰時刻延遲時間
	int tolerableShareTime=900;
	int ZhonghetoXindianAllowTravelTime=1200;//下午尖峰時刻延遲時間
	int timeindex=0;
	int recentPercent=0;	
	int halfworktimeTolerableTime=3600;//終場時端往後的時間 目前為一小時
	int areanum=32;//32個地區
    int intervalnum=48;//24小時每半小時為一格
	int areaPrioritymorningpeaktime=31500;//處理優先處區域的早上尖峰時刻
	int areaPrioritystartafternoonpeaktime=59400;//處理下午尖峰時刻起始時間16:30
	int areaPriorityendafternoonpeaktime=67200;//處理下午尖峰時刻結束時間18:30
	int areaPrioritystartmorningpeaktime=27600;//處理上午尖峰時刻起始時間7:45
	int areaPriorityendmorningpeaktime=31200;//處理上午尖峰時刻結束時間8:30
	int searchRangetime=3600;
	int differencevalue=10800;//長距離預約者上車下車時間之間的最大時間誤差
	int errorcode=-1;//紀錄google錯誤代碼		
	final static int map_Revise_Traveltime=300;//修正GOOGLE回傳時間 容忍時間為1分鐘
	int maxofTrip=10;//預定的接客上限數
	int reqsize=0;//紀錄目前正常排班或候補排班的未排過班預約者數
	static int[][] AreaWeight;
    static int AssignSharingnum=10;//指定共乘數目的初始直
    Map<String, Integer> Area = new HashMap<String, Integer> ();//區域對照碼    
	List<String> Zhonghespecialcar= new ArrayList<String>(10);//中和特殊支援的車	
	List<String> TiroDriver= new ArrayList<String>(10);//新手車	
	List<String> SpecialCar= new ArrayList<String>(10);//特殊車	
	List<String>  xindianRoad= new ArrayList<String>(100);//新店的路
	double[] input = new double[4];	
	String[] address= new String[2];	
	int nonrelax=21600;
	LinkInfo linkinfo ;
	HttpServletResponse Response;
	int[][] Specialareacar={
			{0,1},
			{3,2,2}
           };
	int[][] Specialarea={
						{12,8,18,23},
						{19,11,7,6},
						{0,10}
	                   };
	//讀取長旅行時間的順序
	int[][] longtime={
			{12,8,23,18,31,0,19,11,7,6,30,29,28,27,22,21,1,2,3,4,5,9,10,13,14,15,16,17,20,24,25,26}
			};
    int[][] areaPriority={
							 {17,6,7,15,13,24,25,26,19},//處理優先處區域的尖峰時刻
							 {12,23,31,20,18,8,21,20,19,11,7,6,15,13,24,25,26,16,14},//區域優先順序
							 {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31}//第三輪全部再檢查一次
							 };	
    static boolean[][] backareaWeight={ /*req*/
		  				//汐止,新店, 中和, 土城	,永和	,板橋	,三峽	,鶯歌	,瑞芳	,新莊	,深坑	,林口	,貢寮	,八里	,泰山	,淡水	,樹林	,台北市	  ,基隆市	 ,桃園縣		,烏來	,坪林	,石碇	 ,雙溪	,五股     ,三重	 ,蘆洲   ,金山	 ,三芝	 ,石門	 ,萬里 ,平溪
    		/*car汐止*/	{true,true,true ,false  ,true   ,true  ,false   ,false	,true	,false	,true	 ,false ,true  ,false	,false	,false	,false	,true	  ,true     ,false		,false	,false	,true	 ,true	 ,false	 ,false  ,false  ,true   ,true  ,true   ,true ,true},
    		/* 新店*/   {true,true,true ,true   ,true   ,true  ,true   ,true		,false	,true	,true	 ,true ,false  ,true		,true	,true	,true	,true	  ,false     ,false		,true	,true	,true	 ,false	 ,false	 ,false  ,false  ,false   ,false  ,false   ,false ,true},
    		/*中和*/	 	{true,true,true ,true  ,true   ,true  ,true   ,true		,false	,true	,false	 ,true ,false  ,true		,true	,true	,true	,true	  ,false     ,true		,false	,false	,true	 ,false	 ,true	 ,true   ,true  ,false   ,false  ,false   ,false ,false},
    		/*土城*/		{false,true,true ,true  ,true   ,true  ,true   ,true		,false	,true	,false	 ,true ,false  ,true		,true	,false	,true	,true	  ,false     ,true		,false	,false	,false	 ,false	 ,true	 ,true   ,true  ,false   ,false  ,false   ,false ,false},
    					};
    
    
    //汐止與土城權重未使用
	static int[][] nightareaWeight={ /*req*/
					  //汐止,新店,中和,土城,永和,板橋,三峽,鶯歌,瑞芳,新莊,深坑,林口,貢寮,八里,泰山,淡水,樹林,台北市,基隆市,桃園縣,烏來,坪林,石碇,雙溪,五股,三重,蘆洲,金山,三芝,石門,萬里,平溪
		 /*car汐止*/		{9	,9	,9   ,9  ,9   ,9   ,9   ,9	,9	,9	,9	 ,9	 ,9	  ,9	,9	,9	,9	,9	  ,9     ,9		,9	,9	,9	 ,9	 ,9	 ,9	  ,9  ,9   ,9  ,9   ,9  ,9},
			/* 新店*/   {2	,1  ,1	 ,3  ,1   ,3   ,9   ,9 	,3	,3	,1	 ,9	 ,3	  ,9	,9	,9	,3	,2	  ,3     ,9		,1	,1	,1   ,3  ,9	 ,3	  ,9  ,9   ,9  ,9   ,9  ,3},
			/*中和*/	 	{3  ,1  ,1   ,1   ,1   ,2   ,3   ,3  ,9	,3	,9	 ,3	 ,9	  ,3	,3	,3	,3	,2	  ,9     ,3		,9	,9	,9	,9  ,3	 ,3	  ,3  ,9   ,9  ,9   ,9  ,9},
			/*土城*/		{9	,9  ,9   ,9  ,9   ,9   ,9   ,9	,9	,9	,9	 ,9	 ,9   ,9	,9	,9	,9	,9	  ,9     ,9		,9	,9	,9	,9  ,9	 ,9	  ,9  ,9   ,9  ,9   ,9  ,9},
			             };
	//頭尾班權重	
	static int[][] areaWeight={ /*req*/
					  //汐止,新店,中和,土城,永和,板橋,三峽,鶯歌,瑞芳,新莊,深坑,林口,貢寮,八里,泰山,淡水,樹林,台北市,基隆市,桃園縣,烏來,坪林,石碇,雙溪,五股,三重,蘆洲,金山,三芝,石門,萬里,平溪
		 /*car汐止*/		{2	,9	,9   ,9  ,9   ,9   ,9   ,9	,1	,9	,9	 ,9	 ,1	  ,9	,9	,9	,9	,9	  ,9     ,9		,9	,1	,9	 ,1	 ,9	 ,9	  ,9  ,9   ,9  ,9   ,9  ,9},
			/* 新店*/   {1	,2  ,3	 ,9  ,3   ,9   ,9   ,9 	,9	,9	,2	 ,9	 ,9	  ,9	,9	,9	,9	,9	  ,9     ,9		,2	,2	,2   ,9  ,9	 ,9	  ,9  ,9   ,9  ,9   ,9  ,9},
			/*中和*/	 	{9  ,3  ,2   ,1  ,2   ,3   ,1   ,1  ,9	,3	,9	 ,9	 ,9	  ,9	,9	,9	,3	,9	  ,9     ,9		,9	,9	,9	,9  ,9	 ,9	  ,9  ,9   ,9  ,9   ,9  ,9},
			/*土城*/		{9	,9  ,9   ,3  ,9   ,9   ,1   ,1	,9	,9	,9	 ,9	 ,9   ,9	,9	,9	,1	,9	  ,9     ,2		,9	,9	,9	,9  ,9	 ,9	  ,9  ,9   ,9  ,9   ,9  ,9},
			             };
	static int[][] Weight={ /*req*/
					  //汐止,新店,中和,土城,永和,板橋,三峽,鶯歌,瑞芳,新莊,深坑,林口,貢寮,八里,泰山,淡水,樹林,台北市,基隆市,桃園縣,烏來,坪林,石碇,雙溪,五股,三重,蘆洲,金山,三芝,石門,萬里,平溪
			/*car汐止*/	{1	,2	,9	 ,9	 ,9	  ,9	,9	,9	,1	,9	,2	 ,9	 ,2	  ,9	,9	,9	,9	,2	  ,2     ,9		,9	,9	,9	,2	 ,9  ,9   ,9  ,9  ,9   ,9  ,9   ,1},
			/* 新店*/   	{2	,1	,2	 ,9	 ,2	  ,3	,9	,9 	,9	,9	,1	 ,9	 ,9	  ,9	,9	,9	,9	,2	  ,9     ,9		,1	,1	,1	,9	 ,9	 ,9	  ,9  ,9  ,9   ,9  ,9   ,3},
			/*中和*/	 	{9  ,1	,1	 ,1	 ,1	  ,2	,3	,3  ,9	,3	,9	 ,9	 ,9	  ,9	,3	,9	,3	,3	  ,9     ,9		,9	,9	,9	,9	 ,9	 ,3	  ,3  ,9  ,9   ,9  ,9   ,9},
			/*土城*/		{9	,9	,1 	 ,1	 ,3	  ,2	,2	,2	,9	,3	,9	 ,3	 ,9   ,9	,9	,9	,2	,9	  ,9     ,3		,9	,9	,9	,9	 ,3	 ,9	  ,9  ,9  ,9   ,9  ,9   ,9},		
			/*永和*/		{9	,1	,1	 ,3	 ,1	  ,3	,9	,9	,9	,9	,9	 ,9	 ,9	  ,9	,9	,9	,9	,2	  ,9     ,9		,9	,9	,9	,9	 ,9	 ,9	  ,9  ,9  ,9   ,9  ,9   ,9},
			/*板橋*/		{9	,3	,2	 ,2	 ,3   ,1		,3	,9	,9	,2	,9	 ,9	 ,9	  ,9	,3	,9	,2	,3	  ,9     ,9		,9	,9	,9	,9	 ,3	 ,2	  ,9  ,9  ,9   ,9  ,9   ,9},
			/*三峽*/		{9	,9  ,3	 ,2	 ,9	  ,3	,1	,1 	,9	,3	,9	 ,2	 ,9	  ,9	,9	,9	,2	,9	  ,9     ,1		,9	,9	,9	,9	 ,9	 ,9	  ,9  ,9  ,9   ,9  ,9   ,9},
			/*鶯歌*/		{9	,9  ,3   ,2	 ,9	  ,9	,1	,1	,9	,9	,9	 ,1	 ,9	  ,3	,9	,9	,3	,9	  ,9     ,1		,9	,9	,9	,9	 ,9	 ,9	  ,9  ,9  ,9   ,9  ,9   ,9},
			/*瑞芳*/		{1	,9  ,9   ,9	 ,9	  ,9	,9	,9	,1	,9	,9	 ,9	 ,2	  ,9	,9	,9	,9	,9	  ,2     ,9		,9	,9	,9	,2	 ,9	 ,9	  ,9  ,9  ,9   ,9  ,9   ,9},
			/*新莊*/		{9	,9  ,3   ,3  ,9   ,2   ,3   ,9	,9	,1	,9	 ,3	 ,9	  ,3	,2	,9	,2	,3	  ,9     ,3		,9	,9	,9	,9	 ,2	 ,2	  ,2  ,9  ,9   ,9  ,9   ,9},
			/*深坑*/		{2	,1  ,9   ,9  ,9   ,9   ,9   ,9	,9	,9	,1	 ,9	 ,9	  ,9	,9	,9	,9	,2	  ,9     ,9		,9	,9	,2	,9	 ,9	 ,9	  ,9  ,9  ,9   ,9  ,9   ,9},
			/*林口*/		{9	,9  ,9   ,3  ,9   ,9   ,2   ,1	,9	,3	,9	 ,1	 ,9	  ,2	,2	,9	,3	,9	  ,9     ,2		,9	,9	,9	,9	 ,2	 ,3	  ,3  ,9  ,9   ,9  ,9   ,9},
			/*貢寮*/		{2	,9  ,9   ,9  ,9   ,9   ,9   ,9	,2	,9	,9	 ,9	 ,1	  ,9	,9	,9	,9	,9	  ,3     ,9		,9	,9	,9	,3	 ,9	 ,9	  ,9  ,9  ,9   ,9  ,9   ,3},
			/*八里*/		{9	,9  ,9   ,9  ,9   ,9   ,9   ,3	,9	,3	,9	 ,2	 ,9	  ,1		,3	,2	,9	,3	  ,9     ,9		,9	,9	,9	,9	 ,2	 ,3	  ,3  ,9  ,9   ,9  ,9   ,9},
			/*泰山*/		{9	,9  ,3   ,9  ,9   ,3   ,9   ,9	,9	,2	,9	 ,2	 ,9	  ,3	,1	,3	,3	,9	  ,9     ,3		,9	,9	,9	,9	 ,2	 ,3	  ,3  ,9  ,9   ,9  ,9   ,9},
			/*淡水*/		{9	,9  ,9   ,9  ,9   ,9   ,9   ,9	,9	,9	,9	 ,9	 ,9	  ,2	,3	,1	,9	,2	  ,9     ,9		,9	,9	,9	,9	 ,3	 ,3	  ,3  ,3  ,2   ,3  ,9   ,9},
			/*樹林*/		{9	,9  ,3   ,2  ,9   ,2   ,2   ,3	,9	,2	,9	 ,3	 ,9	  ,9	,3	,9	,1	,9	  ,9     ,3		,9	,9	,9	,9	 ,3	 ,3	  ,9  ,9  ,9   ,9  ,9   ,9},
			/*台北市*/	{2	,2  ,3   ,9  ,2	   ,3   ,9   ,9	,9	,3	,2	 ,9	 ,9	  ,3	,9	,2	,9	,1	  ,2     ,9		,9	,9	,9	,9	 ,3	 ,2   ,3  ,3  ,3   ,3  ,9   ,9},
			/*基隆市*/	{2	,9  ,9   ,9  ,9   ,9   ,9   ,9	,2	,9	,9	 ,9	 ,3	  ,9	,9	,9	,9	,2	  ,1     ,9		,9	,9	,9	,9	 ,9	 ,9	  ,9  ,9  ,9   ,9  ,2   ,3},
			/*桃園縣*/ 	{9	,9  ,9   ,3  ,9   ,9   ,1   ,1	,9	,3	,9	 ,2	 ,9	  ,9	,3	,9	,3	,9	  ,9     ,1		,9	,9	,9	,9	 ,9	 ,9	  ,9  ,9  ,9   ,9  ,9   ,9},
			/*烏來*/ 	{9	,1  ,9   ,9  ,9   ,9   ,9   ,9	,9	,9	,9	 ,9	 ,9	  ,9	,9	,9	,9	,9	  ,9     ,9		,1	,9	,9	,9	 ,9	 ,9	  ,9  ,9  ,9   ,9  ,9   ,9},
			/*坪林*/ 	{9	,1  ,9   ,9  ,9   ,9   ,9   ,9	,9	,9	,9	 ,9	 ,9	  ,9	,9	,9	,9	,9	  ,9     ,9		,9	,1	,1	,9	 ,9	 ,9	  ,9  ,9  ,9   ,9  ,9   ,9},
			/*石碇*/ 	{9	,1  ,9   ,9  ,9   ,9   ,9   ,9	,9	,9	,2	 ,9	 ,9	  ,9	,9	,9	,9	,9	  ,9     ,9		,9	,1	,1	,9	 ,9	 ,9	  ,9  ,9  ,9   ,9  ,9   ,1},
			/*雙溪*/ 	{2	,9  ,9   ,9  ,9   ,9   ,9   ,9	,2	,9	,9	 ,9	 ,3	  ,9	,9	,9	,9	,9	  ,2     ,9		,9	,9	,2	,1	 ,9	 ,9	  ,9  ,9  ,9   ,9  ,9   ,1},
			/*五股*/ 	{9	,9  ,9   ,3  ,9   ,3   ,9   ,9	,9	,2	,9	 ,2	 ,9	  ,2	,2	,3	,3	,3	  ,9     ,9		,9	,9	,9	,9	 ,1	 ,1	  ,1  ,9  ,9   ,9  ,9   ,9},
			/*三重*/ 	{9	,9  ,3   ,9  ,9   ,2   ,9   ,9	,9	,2	,9	 ,3	 ,9	  ,3	,3	,3	,3	,2	  ,9     ,9		,9	,9	,9	,9	 ,1	 ,1	  ,1  ,9  ,9   ,9  ,9   ,9},
			/*蘆洲*/ 	{9	,9  ,3   ,9  ,9   ,3   ,9   ,9	,9	,2	,9	 ,3	 ,9	  ,3	,3	,3	,9	,3	  ,9     ,9		,9	,9	,9	,9	 ,1	 ,1	  ,1  ,9  ,9   ,9  ,9   ,9},
			/*金山*/ 	{9	,9  ,9   ,9  ,9   ,9   ,9   ,9	,9	,9	,9	 ,9	 ,9	  ,9	,9	,3	,9	,3	  ,9     ,9		,9	,9	,9	,9	 ,9	 ,9	  ,9  ,1  ,1   ,1  ,1   ,9},
			/*三芝*/ 	{9	,9  ,9   ,9  ,9   ,9   ,9   ,9	,9	,9	,9	 ,9	 ,9	  ,9	,9	,2	,9	,3	  ,9     ,9		,9	,9	,9	,9	 ,9	 ,9	  ,9  ,9  ,1   ,1  ,9   ,9},
			/*石門*/ 	{9	,9  ,9   ,9  ,9   ,9   ,9   ,9	,9	,9	,9	 ,9	 ,9	  ,9	,9	,3	,9	,3	  ,9     ,9		,9	,9	,9	,9	 ,9	 ,9	  ,9  ,1  ,1   ,1  ,2   ,9},
			/*萬里*/ 	{9	,9  ,9   ,9  ,9   ,9   ,9   ,9	,9	,9	,9	 ,9	 ,9	  ,9	,9	,9	,9	,3	  ,2     ,9		,9	,9	,9	,9	 ,9	 ,9	  ,9  ,1  ,9   ,2  ,1   ,9},
			/*平溪*/ 	{2	,3  ,9   ,9  ,9   ,9   ,9   ,9	,9	,9	,9	 ,9	 ,3	  ,9	,9	,9	,9	,9	  ,3     ,9		,9	,9	,1	,1	 ,9	 ,9	  ,9  ,9  ,9   ,9  ,9   ,1},
						};
	public defineVariable() throws Exception{
		Class.forName("com.mysql.jdbc.Driver");
		linkinfo = new LinkInfo();	
		con = DriverManager.getConnection(linkinfo.getDatabaseURL() , linkinfo.getID(), linkinfo.getPW());
		smt = con.createStatement();	
		smt2 = con.createStatement();
		GetSetting();
		GetSpecialCar();
		GetArea(Area);
		GetAreaWeight();
		
	}
	public void GetAreaWeight() throws SQLException
	{
				AreaWeight = new int[61][]; 	
				int rscrowindex=0;
				for(int index = 0; index< AreaWeight.length; index++) 
				{ 
					AreaWeight[index] = new int[62]; 
			    } 
				rs = smt.executeQuery("SELECT * FROM `area` WHERE 1");					
				while(rs.next())
				{
					for(int rsindex=3;rsindex<=64;rsindex++)						
					 	AreaWeight[rscrowindex][rsindex-3]=rs.getInt(rsindex);
					rscrowindex++;
				}
	}
	public void GetSetting() throws SQLException
	{
		rs = smt.executeQuery("SELECT * FROM `setting` WHERE 1");
		rs.first();
		//讀取設定
		if(rs.next())
		{
			TolerableTime=rs.getInt("tolerabletime");
		}
	}
	public void GetXindianRoad() throws SQLException
	{
		ResultSet Xindianrs = null; 
		Xindianrs = smt.executeQuery("SELECT * FROM `xindian` WHERE 1");
		do
		{
		  xindianRoad.add(rs.getString("roadname"));
		}while(Xindianrs.next());
	}
	public void GetSpecialCar() throws SQLException
	{
		rs = smt.executeQuery("SELECT * FROM `filtratecar` WHERE 1");
		rs.first();
		do
		{
			if(rs.getInt("option")==0)//中和車支援土城車的司機
				Zhonghespecialcar.add(rs.getString("carid"));
			else if(rs.getInt("option")==1)//新來司機不接共乘
				TiroDriver.add(rs.getString("carid"));
			else if(rs.getInt("option")==2)//特殊車輛不接超過5趟
				SpecialCar.add(rs.getString("carid"));
		}while(rs.next());
		
		
	}
	public Map<String, Integer> GetArea(Map<String, Integer> Area) throws SQLException
	{
		ResultSet rs = null; 
		String sqlQuery="SELECT * FROM `area` WHERE 1";
        try 
        {
			rs=	smt.executeQuery(sqlQuery);
			rs.first();
			do
			{	
				Area.put(rs.getString("Area"),rs.getInt("no"));			
			}while(rs.next());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        
        return Area;
	}
	

	//找出當下需求者		
	public RequestTable RequestTableQuery(String req,defineVariable Variable,Map<Integer, RequestTable> IndexMap)
	{
		RequestTable tableIndex=null;
		//先判斷當下的上車地點在哪
		 String[]  temp =req.split("_");
		 tableIndex = IndexMap.get(Integer.valueOf(temp[0]));
		return tableIndex;
	}
	
	//找出下一趟的需求者		
	public RequestTable NextRequestTableQuery(DriverTable DriverNode,int EndInterval,defineVariable Variable,Map<Integer, RequestTable> IndexMap)
	{
		RequestTable tableIndex=null;		
		for(int  index = EndInterval; index <=DriverNode.TimeInterval.length; index++)
		{
			//找到下一個有排班的TimeInterval				
			if(!(DriverNode.TimeInterval[index].indexOf("不上班")!=-1) 
				&&!(DriverNode.TimeInterval[index].indexOf("未排班")!=-1))
			{
				String[]  temp = DriverNode.TimeInterval[index].split("_");					
				tableIndex = IndexMap.get(Integer.valueOf(temp[0]));
				break;
			}
		}
		return tableIndex;
	}	
	//找出上一趟的需求者		
	public RequestTable PreRequestTableQuery(DriverTable DriverNode,int StartInterval,defineVariable Variable,Map<Integer, RequestTable> IndexMap)
	{
		RequestTable tableIndex=null;		
		for(int  index = StartInterval; index >=0; index--)
		{
			//找到上一個有排班的TimeInterval				
			if(!(DriverNode.TimeInterval[index].indexOf("不上班")!=-1) 
					&&!(DriverNode.TimeInterval[index].indexOf("未排班")!=-1))
			{
				String[]  temp = DriverNode.TimeInterval[index].split("_");					
				tableIndex = IndexMap.get(Integer.valueOf(temp[0]));
				 break;
			}
		}
		return tableIndex;
	}	
	//計算這一趟趟跟上一趟之間的旅行時間
	public int[] DistanceTime(RequestTable Tripreq,RequestTable Currentreq,ILF ilf,defineVariable Variable,double IntervalSec)
	{
		int [] traveltime=new int[2];//第一格紀錄上一個req的Interval index  第二格旅行時間
		try {
			int recentTimeResult=-1;
			//計算上一趟下車所在的區間
			int index =(Tripreq.originalDestinationTime/ (int)IntervalSec);
			traveltime[0]=index;//儲存上一趟所在的區間
			Variable.input[0] =Tripreq.DestinationLat;
			Variable.input[1] = Tripreq.DestinationLon;	
			Variable.address[0] =Tripreq.DestinationAddress;
			//填入這筆資料的起點地址資訊
			Variable.input[2] = Currentreq.OriginLat;
			Variable.input[3] =Currentreq.OriginLon;
			Variable.address[1] = Currentreq.OriginAddress;
			//取得上一班車的下車地點到當前預約者上車地點的旅行時間
			recentTimeResult = ilf.SearchHistory(Variable.input, Variable.address,Tripreq.DestinationTime);
		
			if(recentTimeResult==-1)//如果旅行時間回傳-1延遲1秒重新尋找
			{
				Thread.sleep(1000);
				recentTimeResult = ilf.SearchHistory(Variable.input,Variable.address,Tripreq.DestinationTime);
			}	
			traveltime[1]=recentTimeResult;			
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return traveltime;
	}
	public static int switchareaindex(String areadata)
	{
	int area=0;
	switch(areadata)
	{	
		case "新北市汐止區":
		case "汐止":
			area=0;
		break;	
		case "新北市新店區":
		case "新店":
			area=1;
		break;
		case "新北市中和區":
		case "中和":
			area=2;
			break;
		case "新北市土城區":
		case "土城":
			area=3;
			break;			
		case "新北市永和區":
			area=4;
			break;			
		case "新北市板橋區":
			area=5;
			break;
		case "新北市三峽區":
			area=6;
			break;			
		case "新北市鶯歌區":
			area=7;
			break;
		case "新北市瑞芳區":
			area=8;
			break;
		case "新北市新莊區":
			area=9;
			break;
		case "新北市深坑區":
			area=10;
			break;
		case "新北市林口區":
			area=11;
			break;
		case "新北市貢寮區":
			area=12;
			break;			
		case "新北市八里區":
			area=13;
			break;		
		case "新北市泰山區":
			area=14;
			break;
		case "新北市淡水區":
			area=15;
			break;
		case "新北市樹林區":			
			area=16;
			break;		
		case "台北市士林區":	
		case "台北市南港區":
		case "台北市大同區":	
		case "台北市大安區":			
		case "台北市中正區":			
		case "台北市內湖區":			
		case "台北市文山區":			
		case "台北市北投區":			
		case "台北市松山區":
		case "台北市萬華區":
		case "台北市信義區":
		case "台北市中山區":
			area=17;
			break;	
		case "基隆市七堵區":
		case "基隆市中正區":
		case "基隆市安樂區":
		case "基隆市仁愛區":
		case "基隆市信義區":	
		case "基隆市暖暖區":	
		case "基隆市五堵區":	
		case "基隆市中山區":	
			area=18;
			break;	
		case "桃園縣中壢市":
		case "桃園縣桃園市":
		case "桃園縣龜山鄉":
		case "桃園縣八德市":
		case "桃園縣大溪鎮":
		case "桃園縣大園鄉":
		case "桃園縣楊梅市":
		case "桃園縣新屋鄉":
		case "桃園縣觀音鄉":
		case "桃園縣復興鄉":
		case "桃園縣平鎮市":
		case "桃園縣蘆竹鄉":	
		case "桃園縣龍潭鄉":	
		case "桃園縣中壢區":
		case "桃園縣桃園區":
		case "桃園縣龜山區":
		case "桃園縣八德區":
		case "桃園縣大溪區":
		case "桃園縣大園區":
		case "桃園縣楊梅區":
		case "桃園縣新屋區":
		case "桃園縣觀音區":
		case "桃園縣復興區":
		case "桃園縣平鎮區":
		case "桃園縣蘆竹區":	
		case "桃園縣龍潭區":	
			area=19;
			break;	
		case "新北市烏來區":
			area=20;
			break;	
		case "新北市坪林區":
			area=21;
			break;	
		case "新北市石碇區":
			area=22;
			break;	
		case "新北市雙溪區":
			area=23;
			break;
		case "新北市五股區":
			area=24;
			break;	
		case "新北市三重區":
			area=25;
			break;	
		case "新北市蘆洲區":
			area=26;
			break;	
		case "新北市金山區":
			area=27;
			break;	
		case "新北市三芝區":
			area=28;
			break;	
		case "新北市石門區":
			area=29;
			break;	
		case "新北市萬里區":
			area=30;
			break;	
		case "新北市平溪區":
			area=31;
			break;	
		default:
			area=-1;
		    break;
			
			
		}
		return area;
	}
	//檢查頭班往後2格是否有班 尾班往前2格
	public void SetHeadTailteamTime(int StartTimeInterval,int EndTimeInterval,DriverTable Driver,Map<Integer,RequestTable> IndexMap)
	{
		
		//符合頭班時間的第一班預約者上車時間
		for(int index=StartTimeInterval;index<=StartTimeInterval+3;index++)
		{
			if(!(Driver.TimeInterval[index].indexOf("不上班")!=-1)
				&&!(Driver.TimeInterval[index].indexOf("未排班")!=-1)
				&&!(Driver.TimeInterval[index].indexOf("休息")!=-1))
			{
				String[] testnumber = Driver.TimeInterval[index].split("_");
				Driver.startreqtime=IndexMap.get(Integer.valueOf(testnumber[0])).originalStartTime;
				Driver.StartArrange=true;
				Driver.StartTimeInterval=index;
				break;
			}
		}
		//符合尾班時間的最後一班上車時間
		for(int index=EndTimeInterval;index>=EndTimeInterval-3;index--)
		{
			if(!(Driver.TimeInterval[index].indexOf("不上班")!=-1)
				&&!(Driver.TimeInterval[index].indexOf("未排班")!=-1)
				&&!(Driver.TimeInterval[index].indexOf("休息")!=-1))
			{

				String[] testnumber = Driver.TimeInterval[index].split("_");
				Driver.endreqtime=IndexMap.get(Integer.valueOf(testnumber[0])).originalStartTime;
				Driver.EndTimeInterval=index;
				Driver.EndArrange=true;
				break;
			}
		}
	}
	//假日版設定頭尾班
	public void SetHeadTailteamTime1(DriverTable Driver,Map<Integer,RequestTable> IndexMap)
	{
		//符合頭班時間的第一班預約者上車時間
		for(int index=0;index<=Driver.HalfWorkTimeInterval-1;index++)
		{
			if(!(Driver.TimeInterval[index].indexOf("不上班")!=-1)
				&&!(Driver.TimeInterval[index].indexOf("未排班")!=-1)
				&&!(Driver.TimeInterval[index].indexOf("休息")!=-1))
			{
				String[] testnumber = Driver.TimeInterval[index].split("_");
				Driver.startreqtime=IndexMap.get(Integer.valueOf(testnumber[0])).originalStartTime;
				Driver.StartArrange=true;
				Driver.StartTimeInterval=index;
				break;
			}
		}
		//符合尾班時間的最後一班上車時間
		for(int index=Driver.TimeInterval.length-1;index>=Driver.HalfWorkTimeInterval+1;index--)
		{
			if(!(Driver.TimeInterval[index].indexOf("不上班")!=-1)
				&&!(Driver.TimeInterval[index].indexOf("未排班")!=-1)
				&&!(Driver.TimeInterval[index].indexOf("休息")!=-1))
			{
				String[] testnumber = Driver.TimeInterval[index].split("_");
				Driver.endreqtime=IndexMap.get(Integer.valueOf(testnumber[0])).originalStartTime;
				Driver.EndArrange=true;
				Driver.EndTimeInterval=index;
				break;
			}
		}
	}
	//檢查頭班往後1格是否有班 尾班往前1格 mode:0檢查頭班1檢查尾班
	public boolean Check(int Interval,DriverTable Driver,int mode,int HalfWorkTime)
	{
		boolean[] CheckStuas={false,false};
		
		if(mode==0)
		{
			//頭班
			int maxindex=0;
			int minindex=0;
			if(Driver.Holiday==0)
			{	//平日檢查頭班時間後一格
				minindex=Interval;
				maxindex=Interval+3;
			}
			else
			{
				//假日檢查到中午時段
				maxindex=HalfWorkTime-1;
			}
			for(int index=minindex;index<=maxindex;index++)
			{
				if(!(Driver.TimeInterval[index].indexOf("不上班")!=-1)
					&&!(Driver.TimeInterval[index].indexOf("未排班")!=-1)
					&&!(Driver.TimeInterval[index].indexOf("休息")!=-1))
				{
					if(index==Interval)
						CheckStuas[0]=true;
					else
						CheckStuas[1]=true;
					break;
				}
				else
				{
					if(index==Interval)
						CheckStuas[0]=false;
					else
						CheckStuas[1]=false;
				}
			}
		}
		else
		{
			//尾班
			int maxindex=0;
			int minindex=0;
			if(Driver.Holiday==0)
			{	
				//平日檢查尾班前一格
				minindex=Interval-3;
				maxindex=Interval;
			}
			else
			{
				//檢查到中午時段
				maxindex=Driver.TimeInterval.length-1;
				minindex=HalfWorkTime+1;
			}
			for(int index=maxindex;index>=minindex;index--)
			{
				if(!(Driver.TimeInterval[index].indexOf("不上班")!=-1)
					&&!(Driver.TimeInterval[index].indexOf("未排班")!=-1)
					&&!(Driver.TimeInterval[index].indexOf("休息")!=-1))
				{
					if(index==Interval)
						CheckStuas[0]=true;
					else
						CheckStuas[1]=true;
					break;
				}
				else
				{
					if(index==Interval)
						CheckStuas[0]=false;
					else
						CheckStuas[1]=false;
				}					
			}
		}
		if(CheckStuas[1]==true||CheckStuas[0]==true)
			return true;
		else
			return false;		
	}	
	public boolean CheckStatus(DriverTable node,defineVariable Variable)
	{
		boolean[] flag={false,false};
		int starttime = node.StartTime + Variable.tolerableStartTime;//頭班時間
		int endtime = node.EndTime + Variable.tolerableEndTime;//晚班時間:出勤時間+45分		
		//計算頭班區間
		int StartInterval = (int)(starttime / Variable.IntervalSec);//上車時間在一天中的interval index
		//計算尾班區間
		int EndInterval=(endtime/ Variable.IntervalSec);
		node.HalfWorkTimeInterval=(StartInterval+EndInterval)/2;
		flag[0]=Variable.Check(StartInterval,node,0,node.HalfWorkTimeInterval);
		flag[1]=Variable.Check(EndInterval,node,1,node.HalfWorkTimeInterval);
		if(flag[0]==false||flag[1]==false)
			return true;
		else
			return false;
    }
	//去除英文字員與標點符號，但保留-減號
	public  String clearNotChinese(String buff)
	 {
	   	String tmpString =buff.replaceAll("(?i)[^0-9\u4E00-\u9FA5-]", "");	
	    	return tmpString;
	   }
	public void Checkreq(List<reqGroup> requestTable,String reqnum) 
	{
		boolean Found=false;
		 for(int areaindex=0;areaindex<areanum;areaindex++)
		 { 				
			 for(int timeindex=0;timeindex<intervalnum;timeindex++)
			 { 
				 for(int index=0;index<requestTable.get(areaindex).getreq(timeindex).size();index++)
				 {
					 if(requestTable.get(areaindex).getreq(timeindex).get(index).Number==Integer.valueOf(reqnum))
					 {
						 requestTable.get(areaindex).getreq(timeindex).get(index).Arrange=true;
						 Found=true;
						 break;
					 }
				 }
				 if(Found)
					 break;
			 }
			 if(Found)
				 break;
		 } 
	}
	public void CheckErrorCode(int j)
	{
		switch(j)
		{
			case 2:
				errorcode=-9;						
				break;
			case 8:
				errorcode=-10;
				break;
			case 9:	
				errorcode=-11;
				break;
			default:
				errorcode=-1;
			    break;
		}
		
	}
	public String deleteCommon(String a,Map <String, Integer> Area1)
	{
		  String Area="";
		  String[] Region={"區","鄉","鎮","市"};
		  int lastindex=-1;
		  for(int i=0;i<4;i++)
		  {
			  if(a.lastIndexOf(Region[i])!=-1)
			  {
				  lastindex=a.lastIndexOf(Region[i]);
				  break;
			  }
		  }	
		  if(lastindex>-1)
		  {
			  LinkedHashSet<String> lhs = new LinkedHashSet<String> ();			  
			  for(int i = 0 ; i < lastindex-1 ; i++)
			  {
				  for(int j=0;j<3;j++)
				  {
					  if(Region[j].indexOf(a.substring(i,i+1))==-1)
					  { 
						  lhs.add(a.substring(i,i+1));// 重覆的字元 只會留一個 ,這樣可以解決刪除字串中重複字元問題~
					  }
					  else
					  {
						  lhs.remove(Region[j]);
					  }
				  }
			  }
			  Iterator<String> iterator=lhs.iterator();
			  while(iterator.hasNext())
			  {
			   Area+=iterator.next();
			  }
			  for (Object key : Area1.keySet()) 
			  {
				  if(a.indexOf(key.toString())!=-1)			     	
				  {
						Area=key.toString();		
						break;
				 }
				 if(key.toString().indexOf(a)!=-1)			     	
				 {
						Area=key.toString();		
						break;
			      }
			  }
		  }	
		  return Area;
	} 
	//判斷是否有新店較山區的路
	public boolean CheckXindianRoad(String address,defineVariable Variable)
	{ 
		boolean flag=false;
		//int Spendtimecount=0;
		for(int i = 0; i < Variable.xindianRoad.size(); i++)
		{
			if(address.indexOf(Variable.xindianRoad.get(i))!=-1)
			{
				flag=true;	
				break;
			}
		}
		return flag;
	}
	
}
