import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.TreeMap;

public class PreProcess
{
	private List<reqGroup> ReqTable;//需求預約表以上車地區分類
	private List<reqGroup> Tailreq;//尾班需求預約表以下車地區分類
	private List<carGroup> DrTable;//司機表
	private int TolerableStartTime = 0;//頭班容忍時間
	private int TolerableEndTime = 0;//尾班尾班容忍時間
	private double Interval = 0.0;//單位時間 每個30分鐘為一單位
	private ILF ilf = null;
	private int carsize = 0;	//車輛數
	private static defineVariable variable;//定義的變數	
	public PreProcess(defineVariable Variable,List<reqGroup> requestTable,List<reqGroup> tailreq, List<carGroup> car,	int tolerableStartTime, int tolerableEndTime, double timeUnit,
			int size, ILF ilf2) {
		// TODO 自動產生的建構子 Stub

		ReqTable = requestTable;
		Tailreq=tailreq;
		DrTable = car;		
		TolerableStartTime = tolerableStartTime;
		TolerableEndTime = tolerableEndTime;
		Interval = timeUnit;	
		ilf = ilf2;	
		carsize=size;
		variable=Variable;
	}
	public void Start()//頭尾班程式進入點
	{
		
		int recentPercent = -1;//紀錄頭尾班進度				
		int i=0,carflag=0;//小車先選的flag
		ProgressUpdate proupdate = new ProgressUpdate();//資料庫更新進度表	
		try
		{
			
			while(carflag<6)//頭尾排班第一輪夜班 第二輪汐止土城 第三輪特殊車輛 第四輪小車 第五輪一般車選
			 {
				//如果是google有發生錯誤就終止程式
				if(variable.errorcode<=-2)
					break;
				

				for(int j=0;j<variable.areanum;j++)//32個地區
				{ 				
					for(int l=0;l<variable.intervalnum;l++)//時間總區間數
					{ 
						for(int k=0;k<DrTable.get(j).getCar(l).size();k++)
						{  
							int[] earaarry={-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};//紀錄支援地區
							int[] earaarry1={-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};//主要支援地區	
							int[] earaarry2={-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};//輔助支援地區	
							int arraycount1=-1,arraycount=-1,arraycount2=-1;//紀錄主要跟輔助地區的數量	
							int searchrang=0;///gh marked 2012/1/11 增加搜尋範圍參數							
							if(DrTable.get(j).getCar(l).get(k).station.equals("汐止"))//汐止支援的地區
								{
								earaarry[0]=8;	
								arraycount++;								
								earaarry[1]=12;								
								arraycount++;
								earaarry[2]=23;								
								arraycount++;
								earaarry[3]=21;								
								arraycount++;
								earaarry[4]=31;								
								arraycount++;
								earaarry[5]=0;
								arraycount++;
															
								}
							else if(DrTable.get(j).getCar(l).get(k).ID.equals("1413-A3")
									||DrTable.get(j).getCar(l).get(k).ID.equals("9053-G3")
									||DrTable.get(j).getCar(l).get(k).ID.equals("1409-A3")
									||DrTable.get(j).getCar(l).get(k).ID.equals("4350-YS")
									||DrTable.get(j).getCar(l).get(k).ID.equals("2757-L5")
									||DrTable.get(j).getCar(l).get(k).ID.equals("1412-A3"))
							{
								earaarry[0]=7;								
								arraycount++;
								earaarry[1]=6;								
								arraycount++;
								earaarry[2]=9;								
								arraycount++;
								earaarry[3]=3;								
								arraycount++;
								
								arraycount1++;
								earaarry1[0]=2;//輔助區域	
								arraycount1++;
								earaarry1[1]=4;//輔助區域	
								
								
							}	
							else if(DrTable.get(j).getCar(l).get(k).station.equals("土城"))//土城支援的地區
							{
								earaarry[0]=19;								
								arraycount++;
								earaarry[1]=7;								
								arraycount++;
								earaarry[2]=6;				
								arraycount++;
								earaarry[3]=16;							
								arraycount++;								
							}
							else								
							{
								for(int x=0;x<variable.areanum;x++)//一般車輛搜尋支援的區域
								{ 
								if(defineVariable.areaWeight[defineVariable.switchareaindex(DrTable.get(j).getCar(l).get(k).station)][x]==1)
									{
										arraycount++;
										earaarry[arraycount]=x;//特殊支援區域
										}
								if(defineVariable.areaWeight[defineVariable.switchareaindex(DrTable.get(j).getCar(l).get(k).station)][x]==2)
								{
									arraycount1++;
									earaarry1[arraycount1]=x;//主要區域										
								 }
								if(defineVariable.areaWeight[defineVariable.switchareaindex(DrTable.get(j).getCar(l).get(k).station)][x]==3)
								{
									arraycount2++;
									earaarry2[arraycount2]=x;//輔助區域										
								 }
								}
								for(int index=0;index<=9;index++)
								{
									arraycount1++;
									if(earaarry2[index]==-1)
										break;
									earaarry1[arraycount1]=earaarry2[index];//把輔助區域放到主要區域之後	
								}
								
								
							}
							if(carflag==0&&DrTable.get(j).getCar(l).get(k).EndTime>75600)//夜班車
							{
								//尾班先選
								for(int index=0;index<(arraycount+arraycount1+2);index++)
								{ 
									 //回傳小於等於-2代表google查詢有錯立即終止程式
									 if(variable.errorcode<=-2)
										 break;
									if(!DrTable.get(j).getCar(l).get(k).EndArrange)
									{
										//先選主要地區
										if(index<=arraycount)
										{
											if(OrderEnd(DrTable.get(j).getCar(l).get(k),searchrang,earaarry[index],carflag,9))
											{ 
												DrTable.get(j).getCar(l).get(k).EndArrange=true;
												i++;									
											}
										}
										else 
										{
											if(earaarry1[index-arraycount-1]==-1)
												break;
											//輔助地區
											if(OrderEnd(DrTable.get(j).getCar(l).get(k),searchrang,earaarry1[index-arraycount-1],carflag,9))
											{ 
												DrTable.get(j).getCar(l).get(k).EndArrange=true;
												i++;	
											}
											}
										}
								}
								
								//頭班//夜班不得找支援區域，原因在於夜班優先定位尾班如果中和找三鶯會造成超時
								for(int aaa=0;aaa<3;aaa++)//正常班次第二輪找同時間的候補 //正常班次第三輪找延後15分鐘
								{
									//回傳小於等於-2代表google查詢有錯立即終止程式
									 if(variable.errorcode<=-2)
										 break;
									i=arrange(earaarry1,earaarry, i,DrTable.get(j).getCar(l).get(k), arraycount, arraycount1, searchrang, carflag,aaa);
								}
							}
							else if(carflag==3&&DrTable.get(j).getCar(l).get(k).station.equals("土城"))//土城排頭尾班
							{
								for(int specialcarindex=0;specialcarindex<=arraycount;specialcarindex++)
								{
									for(int specialcarsearchrang=3;specialcarsearchrang>=0;specialcarsearchrang--)
									{
										//回傳小於等於-2代表google查詢有錯立即終止程式
										if(variable.errorcode<=-2)
											break;
										if(!DrTable.get(j).getCar(l).get(k).StartArrange)//判斷是否已排入頭班	
										{    //進入特殊早班挑選
											if(PreProcessspecialcar(DrTable.get(j).getCar(l).get(k),earaarry[specialcarindex],specialcarsearchrang,carflag))
											{  
												i++;
												DrTable.get(j).getCar(l).get(k).StartArrange=true;//如果有找到頭班標示為已排入頭班
												break;
											}
										}
									}
								}
								for(int specialcarindex=0;specialcarindex<=arraycount;specialcarindex++)
								{
									//回傳小於等於-2代表google查詢有錯立即終止程式
									 if(variable.errorcode<=-2)
										 break;
									if(!DrTable.get(j).getCar(l).get(k).EndArrange)//判斷是否已排入尾班
										{
										if(specialcarindex<=arraycount)
										{   //晚班挑選
											if(OrderEnd(DrTable.get(j).getCar(l).get(k),searchrang,earaarry[specialcarindex],carflag,9))
											{ 
												DrTable.get(j).getCar(l).get(k).EndArrange=true;//如果有找到尾班標示為已排入尾班
												i++;
												break;
												}
											}
										}
								}
							}
							else if(carflag==2&&DrTable.get(j).getCar(l).get(k).station.equals("汐止"))//汐止排頭尾班
							{
								for(int specialcarindex=0;specialcarindex<=arraycount;specialcarindex++)
								{
									for(int specialcarsearchrang=3;specialcarsearchrang>=0;specialcarsearchrang--)
									{
										//回傳小於等於-2代表google查詢有錯立即終止程式
										if(variable.errorcode<=-2)
											break;
										if(!DrTable.get(j).getCar(l).get(k).StartArrange)//判斷是否已排入頭班	
										{   //進入特殊早班挑選
											if(PreProcessspecialcar(DrTable.get(j).getCar(l).get(k),earaarry[specialcarindex],specialcarsearchrang,carflag))
											{  
												i++;
												DrTable.get(j).getCar(l).get(k).StartArrange=true;//如果有找到頭班標示為已排入頭班
												break;
											}
										}
									}
								  }
								for(int specialcarindex=0;specialcarindex<=arraycount;specialcarindex++)
								{
									//回傳小於等於-2代表google查詢有錯立即終止程式
									 if(variable.errorcode<=-2)
										 break;
									if(!DrTable.get(j).getCar(l).get(k).EndArrange)//判斷是否已排入尾班
										{
										if(specialcarindex<=arraycount)
										{   //晚班挑選
											if(OrderEnd(DrTable.get(j).getCar(l).get(k),searchrang,earaarry[specialcarindex],carflag,9))
											{ 
												DrTable.get(j).getCar(l).get(k).EndArrange=true;//如果有找到尾班標示為已排入尾班
												i++;
												break;
												}
											}
										}
								}
							}
							//四大金釵特殊支援  
							else if(carflag==1)							
							{ 
								for(int index=0;index<variable.Zhonghespecialcar.size();index++)
									if(DrTable.get(j).getCar(l).get(k).ID.indexOf(variable.Zhonghespecialcar.get(index))!=-1)
									{
										//回傳小於等於-2代表google查詢有錯立即終止程式
										if(variable.errorcode<=-2)
											break;
										//頭班
										i=arrange(earaarry, earaarry1, i,DrTable.get(j).getCar(l).get(k), arraycount, arraycount1, searchrang, carflag,4);
										//尾班
										i=arrange1(earaarry, earaarry1, i,DrTable.get(j).getCar(l).get(k), arraycount, arraycount1, searchrang, carflag,4);
									}
							}//小車
							else if(carflag==4&&DrTable.get(j).getCar(l).get(k).Car.equals("小車"))							
							{   
								//回傳小於等於-2代表google查詢有錯立即終止程式
								 if(variable.errorcode<=-2)
									 break;
								//頭班
								i=arrange(earaarry, earaarry1, i,DrTable.get(j).getCar(l).get(k), arraycount, arraycount1, searchrang, carflag,4);
								//尾班
								i=arrange1(earaarry, earaarry1, i,DrTable.get(j).getCar(l).get(k), arraycount, arraycount1, searchrang, carflag,4);
							}//一般大車
							else if(carflag>=5)							
							{   
								//頭班
								for(int aaa=0;aaa<3;aaa++)//正常班次第二輪找同時間的候補 //正常班次第三輪找延後15分鐘
								{
									//回傳小於等於-2代表google查詢有錯立即終止程式
									 if(variable.errorcode<=-2)
										 break;
									i=arrange(earaarry, earaarry1, i,DrTable.get(j).getCar(l).get(k), arraycount, arraycount1, searchrang, carflag,aaa);
								}
								//尾班
								for(int aaa=0;aaa<3;aaa++)//第一輪找提前15分鐘 正常班次第二輪找同時間 第三輪找同時間的候補
								{
									//回傳小於等於-2代表google查詢有錯立即終止程式
									 if(variable.errorcode<=-2)
										 break;
									i=arrange1(earaarry, earaarry1, i,DrTable.get(j).getCar(l).get(k), arraycount, arraycount1, searchrang, carflag,aaa);
								}
								
							}
							/**************************一般早晚班次挑選********************************************/
							recentPercent =(int)(((float)i/(carsize*2))*100); 
							proupdate.updatedatabase(2, recentPercent,variable.date,variable.time); //更新進度表
							}
				 }
			 }  
				carflag++;
			 }
			if(variable.errorcode>-2)
    	 	proupdate.updatedatabase(2, 100,variable.date,variable.time);			  
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
	}	
	/************************特殊早班挑選*********************************/
	private boolean PreProcessspecialcar(DriverTable Node,int carindex,int searchrang,int specialcar) throws Exception
	{	// Node:司機資訊  carindex:目前所搜尋地區  searchrang:搜尋範圍  specialcar:特殊班次flag如果為1將修改資料庫時間	
		double[] input = new double[4];//紀錄經緯度
		int[] XY = new int[4];//紀錄XY直
		String[] address = new String[2];//紀錄查詢兩點間地址		
		input[0] = Node.Lat;
		input[1] = Node.Lon;
		XY[0] = Node.X;
		XY[1] = Node.Y;
		boolean find=false,flag=false;//find判斷是否有有早到 flag控制車種選擇
		address[0] = Node.Address;	
		int starttime = Node.StartTime + TolerableStartTime;//頭班時間		
		int startimeinx=((int)(starttime)/1800)-searchrang;//頭班時間往前一格
		int Endtimeindex=((int)(starttime)/1800)+searchrang;//頭班時間往前一格
		if(startimeinx<0)
			startimeinx=0;
		
		 for(int l=startimeinx;l<=Endtimeindex;l++)//搜尋符合時間內的車子
		 { 
			
		  for(int k=0;k<ReqTable.get(carindex).getreq(l).size();k++)
			 {
			 if(ReqTable.get(carindex).getreq(l).get(k).Car.equals(""))//大車的預約者可以選擇大小車
				 flag=true;
			 else
			 {
				 if(ReqTable.get(carindex).getreq(l).get(k).Car.equals(Node.Car))//如果是小車的預約者只能選擇小車不能選大車
					 flag=true;
				 }	
			 //判斷是否已排過班 判斷是否狀態為正常 旅行時間要為正 是否符合車種
			
			 if((starttime-ReqTable.get(carindex).getreq(l).get(k).OriginTime)<=4500&&ReqTable.get(carindex).getreq(l).get(k).Arrange == false
					 && ReqTable.get(carindex).getreq(l).get(k).TravelTime > -1&& flag==true&&ReqTable.get(carindex).getreq(l).get(k).OriginTime<32400)
			 {
				//先找到就先排入
				 if(starttime!=ReqTable.get(carindex).getreq(l).get(k).OriginTime)
				 {	 
					 //如果跟頭班時間不一樣修改頭班時間與尾班時間
					 Node.StartTime=ReqTable.get(carindex).getreq(l).get(k).OriginTime-1800;
					 Node.EndTime=Node.StartTime+32400;					
				  }
				
				 //寫入資料庫
				 Node.Greedyflag=true;	
				 writedatabase(ReqTable.get(carindex).getreq(l).get(k),Node,1,1);
				 find=true;//有找到就終止
				 break;
				 }
			 }
		
		 if(find)
			 break;
		 }
		 if(find)
			 return true;	
		 else
			 return false;	
	}
	//排頭班
	//gh marked 2012/1/11 增加搜尋範圍參數
	//private void OrderStart(int NodeNumber) throws IOException
	private boolean OrderStart(DriverTable Node,int searchrang,int carindex,int carflag,int aaa) throws Exception
	{		
		// Node:司機資訊  carindex:目前所搜尋地區  searchrang:搜尋範圍  specialcar:特殊班次flag如果為1將修改資料庫時間
		int starttime = Node.StartTime + TolerableStartTime;//頭班時間
		int ilfReturn = -1;			
		TreeMap<Integer,RequestTable> Processmap1 = new TreeMap<Integer, RequestTable>();//跟頭班時間依樣的車子集合	
		TreeMap<Integer, RequestTable> Processmap2 = new TreeMap<Integer, RequestTable>();//比頭班時間晚15分的車子集合					
		double[] input = new double[4];
		int[] XY = new int[4];
		String[] address = new String[2];
		boolean 	flag=false;			
		input[0] = Node.Lat;
		input[1] = Node.Lon;		
		XY[0] = Node.X;
		XY[1] = Node.Y;		
		int writetime=0;
		address[0] = Node.Address;
		//int count=0;
		
		int startimeinx=(int)(starttime)/1800;//頭班搜尋起始區間			
		//int temstarttime=0;//紀錄特殊班次的出勤時間如果沒找到班次還原原始出勤時間	
		int latertime=starttime;
		int Candidateflag=1;//正常班次第一輪找同時間
		if(aaa==2)
		{
			Candidateflag=2;
		}
		if(aaa==1)//正常班次第二輪找延後15分鐘
		{
			latertime=starttime+900;//允許頭班晚15分鐘出班	
		}
		
		for(int l=startimeinx;l<=startimeinx+2;l++)//搜尋符合時間區間的車子
			 { 
			 //回傳小於等於-2代表google查詢有錯
			 if(variable.errorcode<=-2)
				 break;
			 
			 for(int k=0;k<ReqTable.get(carindex).getreq(l).size();k++)
				 {
				 if(ReqTable.get(carindex).getreq(l).get(k).Car.equals(""))//大車的預約者可以選擇大小車
					 flag=true;
				 else
				 {
					 if(ReqTable.get(carindex).getreq(l).get(k).Car.equals(Node.Car))//如果是小車的預約者只能選擇小車不能選大車
						 flag=true;
					 }	
				 int oncartime=0;				
				 //判斷是否已排過班 判斷是否狀態為正常 旅行時間要為正 是否符合車種 不得比頭班時間早 不得比頭班晚超過15分
			
				 if((ReqTable.get(carindex).getreq(l).get(k).OriginTime>=starttime)&&ReqTable.get(carindex).getreq(l).get(k).OriginTime<=latertime&&ReqTable.get(carindex).getreq(l).get(k).OriginTime > Node.StartTime&& ReqTable.get(carindex).getreq(l).get(k).Arrange == false
						 && ReqTable.get(carindex).getreq(l).get(k).Status == Candidateflag && ReqTable.get(carindex).getreq(l).get(k).TravelTime > -1&& flag==true)
				 {  
					 input[2] = ReqTable.get(carindex).getreq(l).get(k).OriginLat;
					 input[3]  = ReqTable.get(carindex).getreq(l).get(k).OriginLon;					
					 address[1] = ReqTable.get(carindex).getreq(l).get(k).OriginAddress;
					 ilfReturn = ilf.SearchHistory(input, address, Node.StartTime);	//車廠到上車地點的旅行時間				
					//回傳小於等於-2代表google查詢有錯立即終止程式
					 if(ilfReturn<=-2)
					 {
						 variable.errorcode=ilfReturn;
						 break;
					 }
					 
					 //預約者上下車時間落在早上7:30~8:30尖峰時段再加上15分延遲
					 if(((Node.StartTime+ilfReturn)>=27000&&(Node.StartTime+ilfReturn)<=31200))
					 {
						 oncartime=oncartime+variable.morningpeaktime;
					 }
					 if(carindex==1&&Node.station.equals("中和")&&(oncartime+ilfReturn)>(1200+defineVariable.map_Revise_Traveltime))
						 continue;//如果中和支援新店旅行時間加尖峰時刻超過20分不排入					 
					 if(ilfReturn > -1)
					 {  					
						 if(((Node.StartTime + ilfReturn+oncartime)-ReqTable.get(carindex).getreq(l).get(k).OriginTime)<=defineVariable.map_Revise_Traveltime)//判斷來不來的及接到預約者	
							 {								
							 if(ReqTable.get(carindex).getreq(l).get(k).OriginTime ==starttime)//跟司機頭班時間依樣
							 {
								 Processmap1.put((ReqTable.get(carindex).getreq(l).get(k).TravelTime+ilfReturn),ReqTable.get(carindex).getreq(l).get(k));									
							 }
							 else//比司機頭班時間晚
							 {
								 Processmap2.put((ReqTable.get(carindex).getreq(l).get(k).TravelTime+ilfReturn),ReqTable.get(carindex).getreq(l).get(k));
							 }
							 }
						 }
					 flag=false;		
					 } 
				 }
			 }		
		RequestTable Target = null;	
		int key=0;		
		if(!Processmap1.isEmpty())//準時頭班先選以旅行時間最長先選
		{
			key=Processmap1.lastKey();
			Target=Processmap1.get(key);					
		}
		else if(!Processmap2.isEmpty())//比司機晚15分
		{
			key=Processmap2.lastKey();
			Target=Processmap2.get(key);					
		}
		
		if(Target != null)
		{
			
			/*if(specialcar==1)
			{
			 Node.StartTime=temstarttime;
			 Node.EndTime=Node.StartTime+32400-1800;
				}*/
			if(Node.station.equals("中和"))
			if(Target.Originarea.indexOf("新北市三峽區")!=-1||Target.Originarea.indexOf("新北市鶯歌區")!=-1||Target.Destinationarea.indexOf("新北市三峽區")!=-1||Target.Destinationarea.indexOf("新北市鶯歌區")!=-1)
			{
					Node.EndTime=Node.EndTime-1800;//有支援三峽鶯歌的中和車輛提早半小時下班
					writetime=1;
			}
			if(((Target.OriginTime-starttime)<900))//如果有晚接到預約者 晚班不得再晚15分
			{				
				  Node.Greedyflag=true;						
			}
			writedatabase(Target,Node,writetime,1);//寫入資料庫
			return true;
		}
		else
		{
			return false;
		}
		
	}
	//排尾班
		//gh marked 2012/1/11 增加搜尋範圍參數
		//private void OrderEnd(int NodeNumber) throws IOException	
		private boolean OrderEnd(DriverTable Node,int searchrang,int carindex,int specialcar,int aaa) throws Exception
		{
			
			int endtime = Node.EndTime + TolerableEndTime;//晚班時間:出勤時間+45分				
			int ilfReturn = -1;			
			TreeMap<Integer, RequestTable> Processmap1 = new TreeMap<Integer, RequestTable>();	
			TreeMap<Integer, RequestTable> Processmap2 = new TreeMap<Integer, RequestTable>();
			double[] input = new double[4];
			int[] XY = new int[4];
			String[] address = new String[2];
			boolean flag = false;//車種flag
			int writettime=0;
			input[2] = Node.Lat;
			input[3] = Node.Lon;
			XY[2] = Node.X;
			XY[3] = Node.Y;
			address[1] = Node.Address;		
			int Candidateflag=1;
			int minendtime=endtime;//允許延遲時間
			
			/*if(aaa==0)	//第一輪選15分鐘提早
			{
			minendtime=endtime-900;
			}*/
			
		    minendtime=endtime-900;	
		    
			if(aaa==2)//第三輪選後補
			{
				Candidateflag=2;
			}
			//如果收班在三峽鶯歌要多加30分旅行時間
			if(Node.station.equals("中和")&&(carindex==6||carindex==7||carindex==11))
			{
				endtime=endtime-1800;
				writettime=1;
			}
			int timeinx=(int)(endtime)/1800;	//晚班起始區間
		
			for(int l=timeinx;l>=timeinx-1;l--)//搜尋符合時間區間的車子
			{ 
				 //回傳小於等於-2代表google查詢有錯立即終止程式
				 if(variable.errorcode<=-2)
					 break;
				for(int k=Tailreq.get(carindex).getreq(l).size()-1;k>=0;k--)
				{ 		
					int oncartime=0;
					if(Tailreq.get(carindex).getreq(l).get(k).Car.equals(""))//大車的預約者可以選擇大小車
								flag=true;
							else
							{
								if(Tailreq.get(carindex).getreq(l).get(k).Car.equals(Node.Car))//如果是小車的預約者只能選擇小車不能選大車
									flag=true;
							}
						//判斷是否已排過班 判斷是否狀態為正常 旅行時間要為正 是否符合車種 不得比晚班時間晚 不得比晚班早超過15分
						if(Tailreq.get(carindex).getreq(l).get(k).OriginTime<=endtime
								&&(Tailreq.get(carindex).getreq(l).get(k).OriginTime>=minendtime)&&Tailreq.get(carindex).getreq(l).get(k).Status == Candidateflag
								&& Tailreq.get(carindex).getreq(l).get(k).Arrange == false&& Tailreq.get(carindex).getreq(l).get(k).TravelTime != -1&& flag==true)
						 {	
							 input[0] = Tailreq.get(carindex).getreq(l).get(k).DestinationLat;
							 input[1] = Tailreq.get(carindex).getreq(l).get(k).DestinationLon;
							 address[0] = Tailreq.get(carindex).getreq(l).get(k).DestinationAddress;								
							 ilfReturn = ilf.SearchHistory(input, address,Tailreq.get(carindex).getreq(l).get(k).DestinationTime);
							//回傳小於等於-2代表google查詢有錯 立即終止程式
							if(ilfReturn<=-2)
							{
								 variable.errorcode=ilfReturn;
								 break;
							}
							 //預約者下車時間到回廠落在下午16:45~18:30尖峰時段再加上20分延遲
							 if(((Tailreq.get(carindex).getreq(l).get(k).DestinationTime+ilfReturn)>=60300&&(Tailreq.get(carindex).getreq(l).get(k).DestinationTime+ilfReturn)<=66600)
									 ||((Tailreq.get(carindex).getreq(l).get(k).DestinationTime)>=60300&&(Tailreq.get(carindex).getreq(l).get(k).DestinationTime)<=66600))
							 {
								 oncartime+=variable.afternoonpeaktime;
							 }
							 if(carindex==1&&Node.station.equals("中和")&&(oncartime+ilfReturn)>(1200+defineVariable.map_Revise_Traveltime))
								 continue;//如果中和支援新店旅行時間加尖峰時刻超過20分不排入							
							 //旅行時間加尖峰時刻不得超過45分
							 if(ilfReturn > -1&&(ilfReturn+oncartime)<=(2700+defineVariable.map_Revise_Traveltime))
								{
								
								 if((Tailreq.get(carindex).getreq(l).get(k).DestinationTime+ilfReturn+oncartime)<(endtime+variable.tolerablebacktime))//判斷來不來的及回廠
									{										 
									 if(Tailreq.get(carindex).getreq(l).get(k).OriginTime ==endtime)//跟司機晚班時間一樣
									 {										
									 	 Processmap1.put(ilfReturn,Tailreq.get(carindex).getreq(l).get(k));											
									 }
									 else//比司機晚班時間早
									{
										 Processmap2.put(ilfReturn,Tailreq.get(carindex).getreq(l).get(k));
									}
								  }
								 }
								flag=false;		
								} 
						 }
					 }
						
			RequestTable Target = null;	
			int key=0;			
			if(aaa==0)
			{
			if(!Processmap2.isEmpty())
			{
				key=Processmap2.firstKey();
				Target=Processmap2.get(key);				
			}else if(!Processmap1.isEmpty())
			{
				key=Processmap1.firstKey();
				Target=Processmap1.get(key);				
			}
			}
			else
			{
			if(!Processmap1.isEmpty())
			{
				key=Processmap1.lastKey();
				Target=Processmap1.get(key);				
			}	
			else if(!Processmap2.isEmpty())
			{
				key=Processmap2.lastKey();
				Target=Processmap2.get(key);					
			}
				}
			if(Target != null)
			{ 
				switch(specialcar)
				{
				case 0:
					Node.EndTime=Target.OriginTime-2700;//晚班調整時間
					Node.StartTime=Node.EndTime-32400;
					writettime=1;
					break;
				
				}
				
				writedatabase(Target,Node,writettime,2);//寫入資料庫 2代表寫入晚班
				return true;
			}
			else
			{   
				/*if(specialcar==1)
				{
				 Node.EndTime=Node.EndTime;//沒支援三峽鶯歌的中和車輛還原出勤時間
				}*/
				return false;
			}
		}
		private boolean UpdateSharingData(RequestTable req1,RequestTable req2) throws SQLException, IOException
		{	
			String reqinfo=null;
			int arrivetime=0;
			String[] Order=new String[2];
			reqinfo=String.valueOf(req1.Number)+"_"+String.valueOf(req2.Number);				
			ResultSet rs = variable.smt2.executeQuery("SELECT AssignSharing FROM travelinformationofcarsharing WHERE date = '" + variable.date + "' AND arrangetime = '" 
					+ variable.time + "' AND `AssignSharing`='"+reqinfo+"'");
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
						", `中繼點1`='"+Order[0]+"', `中繼點2`='"+Order[1]+"' WHERE `date`= '" + variable.date + "' AND arrangetime = '" + variable.time+"' AND `AssignSharing`='"+reqinfo+"'";
			    variable.smt.executeUpdate(sql);			   
				return true;
			 }else
			 {
				return false;
			 }
			
		}
		private void writedatabase(RequestTable Target,DriverTable Node,int specialcar,int mode) throws IOException, InterruptedException, Exception
		{

			int IntervalSec = (int)(Interval * 3600);//單位時間轉換成秒 0.5小時轉換成1800秒
			int StartInterval =Target.OriginTime / IntervalSec;//計算預約者起始區間
			int EndInterval = ((Target.DestinationTime % IntervalSec)  > 0 ? (Target.DestinationTime / IntervalSec) : (Target.DestinationTime / IntervalSec) - 1);//計算預約者結束區間
		    
			
			String worktime=null;//把上班時間轉換成字串
		    String[] run=new String[2];
		    String hour=null,min=null,hour1=null,min1=null;		
		    String sql;
		    String reqinfo=null;
		    switch(mode)
			{
				case 1:
					Node.StartArrange=true;//2011/1/11 新增標記已排早班
					run[0]="run1";
					run[1]="user1";
					break;
				case 2:
					Node.EndArrange=true;//2011/1/11 新增標記已排晚班
					run[0]="run2";
					run[1]="user2";
					break;
			}
		    Node.ArrangedCount++;//司機已排入的預約者數+1
		    
			//指定共乘		  
			if(Target.AssignSharing==-1)
		    {
			  Target.Targetdrivers=Node.ID.trim();
			  for(int j = StartInterval; j <= EndInterval; j++)
			  {
					Node.TimeInterval[j] = String.valueOf(Target.Number);
			  }
			  reqinfo=String.valueOf(Target.Number);
			  /********************************更新資料庫預約者的排班狀態以及更新司機班表***************************************************************/
			    sql = "UPDATE userrequests SET arranged = 1 ,Targetdrivers='"+Target.Targetdrivers+"' WHERE 識別碼 = '" + Target.Number + "' AND arrangedate = '" + variable.date +"' AND arrangetime = '" + variable.time + "'";
				variable.smt.executeUpdate(sql);	
		    }
			else
			{ 
			  Target.Targetdrivers=Node.ID.trim();
			  //找出與當前預約者共乘的另一個預約者			  
			  RequestTable SharingRequest=new RequestTable();
			  boolean found=false;
			  for(int area=0;area<variable.areanum;area++)//32個地區
			  { 	
				  for(int timeindex=0;timeindex<variable.intervalnum;timeindex++)//時間總區間數
				  { 
					  for(int k=0;k<ReqTable.get(area).getreq(timeindex).size();k++)
					  { 
						  if(Target.AssignSharing==ReqTable.get(area).getreq(timeindex).get(k).Number)
							{
								SharingRequest=ReqTable.get(area).getreq(timeindex).get(k);
								//標記乘已排班
								ReqTable.get(area).getreq(timeindex).get(k).Arrange = true;	
								SharingRequest.Targetdrivers=Node.ID.trim();
								/********************************更新資料庫預約者的排班狀態以及更新司機班表***************************************************************/
							    sql = "UPDATE userrequests SET arranged = 1 ,Targetdrivers='"+Target.Targetdrivers+"' WHERE 識別碼 = '" + Target.Number + "' AND arrangedate = '" + variable.date +"' AND arrangetime = '" + variable.time + "'";
								variable.smt.executeUpdate(sql);	
							    sql = "UPDATE userrequests SET arranged = 1 ,Targetdrivers='"+SharingRequest.Targetdrivers+"' WHERE 識別碼 = '" + SharingRequest.Number + "' AND arrangedate = '" + variable.date +"' AND arrangetime = '" + variable.time + "'";
								variable.smt.executeUpdate(sql);									
								found=true;
								break;								
							}
						}
					  if(found)
						  break;
					}
				  if(found)
					  break;
			  }
			  if(UpdateSharingData(Target,SharingRequest))			  
			  {
				  reqinfo=String.valueOf(Target.Number)+"_"+String.valueOf(SharingRequest.Number);	
			  }else
			  {
				
				 UpdateSharingData(SharingRequest,Target); 
				 reqinfo=String.valueOf(SharingRequest.Number)+"_"+String.valueOf(Target.Number);
			  }
			  for(int j = StartInterval; j <= EndInterval; j++)
			  {
					Node.TimeInterval[j] =reqinfo;
			  }
			}
			
			  /********************************更新資料庫預約者的排班狀態以及更新司機班表***************************************************************/
			sql = "UPDATE arrangedtable SET "+run[0]+"="+StartInterval+" ,"+run[1]+"='"+reqinfo+"' WHERE date = '" + variable.date + "' AND arrangetime = '" + variable.time + "' AND carid = '" +Node.ID + "'";
			variable.smt.executeUpdate(sql);	
			Target.Arrange = true;	
			
			/*************************將特殊車輛更動到的上班時間寫回資料庫***********************************/
				if(specialcar==1)
				{
					hour=settime((Node.StartTime/3600));
					min=settime(((Node.StartTime % 3600) / 60));
					hour1=settime((Node.EndTime/3600));
					min1=settime(((Node.EndTime % 3600) / 60));				
					worktime=hour+":"+min+"~"+hour1+":"+min1;
					sql = "UPDATE availablecars SET 時段='"+worktime+"' WHERE `date`= '" + variable.date + "' AND `time`= '" + variable.time + "' AND `車號`='" +Node.ID + "'";
					variable.smt.executeUpdate(sql);					
					sql = "UPDATE arrangedtable SET worktime="+Node.StartTime+" WHERE `date`= '" + variable.date + "' AND `arrangetime`= '" + variable.time + "' AND `carid`='" +Node.ID + "'";
					variable.smt.executeUpdate(sql);					
				}
			
		}
		private String settime(int time1) throws IOException, InterruptedException
		{
			String time = null;
			
			if(time1==0)
				time="00";
			if(time1>0&&time1<10)
				time="0"+String.valueOf(time1);
			if(time1>=10)
				time=String.valueOf(time1);		
			return time;
		
		}
		
		private int arrange(int[] earaarry,int[] earaarry1,int i,DriverTable Node,int arraycount,int arraycount1,int searchrang,int specialcar,int aaa) throws Exception
		{
			//早班
			for(int index=0;index<(arraycount+arraycount1+2);index++)
			{ 
				if(!Node.StartArrange)
				{
					if(index<=arraycount)//先選主要地區
					{ 
						if(OrderStart(Node,searchrang,earaarry[index],specialcar,aaa))
						{
							i++;
							Node.StartArrange=true;
							
					}
						}
					else//輔助地區
					{
						if(earaarry1[index-arraycount-1]==-1)
							break;
						if(OrderStart(Node,searchrang,earaarry1[index-arraycount-1],specialcar,aaa))
						{ 
							i++;
							Node.StartArrange=true;		
							}
						}
					}
				}
			return i;
		}
		private int arrange1(int[] earaarry,int[] earaarry1,int i,DriverTable Node,int arraycount,int arraycount1,int searchrang,int specialcar,int aaa) throws Exception
		{			
			//晚班
			for(int index=0;index<(arraycount+arraycount1+2);index++)
			{ 
				if(!Node.EndArrange)
				{//先選主要地區
					
					if(index<=arraycount)
					{  
						if(OrderEnd(Node,searchrang,earaarry[index],specialcar,aaa))
						{ 
							Node.EndArrange=true;
							i++;									
						}
				}
				else 
				{//輔助地區
					
					if(earaarry1[index-arraycount-1]==-1)
						break;
					if(OrderEnd(Node,searchrang,earaarry1[index-arraycount-1],specialcar,aaa))
					{ 
						Node.EndArrange=true;
						i++;												
						}
				}
			}
			}
			return i;
		}
        //特殊班次專用
		/*private int arrange1(int[] earaarry,int i,DriverTable Node,int arraycount,int searchrang,int specialcar,int aaa) throws Exception
		{			
			//晚班
			for(int index=0;index<(arraycount);index++)
			{ 
				if(!Node.EndArrange)
				{//先選主要地區在選//輔助地區
					
					if(index<=arraycount)
					{  
						if(OrderEnd(Node,searchrang,earaarry[index],specialcar,aaa))
						{ 
							Node.EndArrange=true;
							i++;									
						}
				}
			}
			}
			return i;
		}*/
	}