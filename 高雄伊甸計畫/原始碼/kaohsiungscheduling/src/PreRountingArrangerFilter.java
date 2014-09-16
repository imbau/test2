//排班filter部分，可以根據需求抽換module
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PreRountingArrangerFilter
{
	private int[] FiltersEnable;
	private double Interval;
	private int StartInterval;
	private int EndInterval;
	private double IntervalSec;
	Map<Integer, RequestTable> IndexMap = new HashMap<Integer, RequestTable>();
	private ILF smartSearch = null;
	private defineVariable Variable;	
	private RequestTable Reqtable=null;
	Map<Integer,DriverTable> carmap=null;		
	public PreRountingArrangerFilter(int tolerabletime,int[] enable, RequestTable reqtable, double interval, int index, Map<Integer,RequestTable> indexmap,defineVariable variable, ILF inputilf) throws Exception
	{
		Variable =variable;//定義變數
		FiltersEnable = enable;	
		Interval = interval;//一個interval的時間長短(小時)										
		Variable.TolerableTime = tolerabletime;//乘客上下車以及車輛行駛時所造成的delay，將此時間延遲加入計算考慮
		IntervalSec = Interval * 3600;//將interval的時間由小時為單位轉成由秒為單位
		//用原本上下車時間去算區間
		StartInterval = (int)( (reqtable.originalStartTime) / IntervalSec);//上車時間在一天中的interval index
		//下車時間在一天中的interval index
		EndInterval = (reqtable.originalDestinationTime /(int) IntervalSec);
		Reqtable=reqtable;//當前的預約者
		IndexMap = indexmap;//存放所有預約者queue		
		Variable.input = new double[4];//存放上下車地址的經緯度值，單純用來當作傳入function用的參數。input[0]為上車地址的緯度，input[1]為上車地址的經度，input[2]下車地址的緯度，input[3]為下車地址的經度。
	    Variable.address = new String[2];//存放上下車地點的地址，單純用來當作傳入function用的參數。address[0]為上車地址，address[1]為下車地址。
		//canShare = -1;//在判定共乘時所使用的參數，如果此值>-1代表這筆預約將和某比預約合併為共乘班次。
		smartSearch = inputilf;//找尋兩地點間交通時間的物件	
		
	}
	//上班下班時間一小時不接2趟以上
	public  void NoMoreThanTwoFilter(List<DriverTable> DriverList)
	{
		 //早上7:30~8:30尖峰時段 或//下午16:45~18:30尖峰時段	
		 if((Reqtable.originalStartTime>=Variable.areaPrioritystartmorningpeaktime&& Reqtable.originalStartTime<=Variable.areaPriorityendmorningpeaktime)
				 ||(Reqtable.originalStartTime>=Variable.areaPrioritystartafternoonpeaktime&&Reqtable.originalStartTime<=Variable.areaPriorityendafternoonpeaktime))
		{
			 //如果落在上下班時間預約者就檢查是否與上一班或下一班間隔1小時
			 for(int i = 0; i < DriverList.size(); )
			 {
				 //尋找上一趟
				 RequestTable TableIndex=Variable.PreRequestTableQuery(DriverList.get(i),StartInterval,Variable,IndexMap);
				 //尋找下一趟
				 RequestTable NextTableIndex=Variable.NextRequestTableQuery(DriverList.get(i),EndInterval,Variable,IndexMap);
		      if((Reqtable.originalStartTime-TableIndex.originalDestinationTime)<=3600&&(NextTableIndex.originalStartTime-Reqtable.originalDestinationTime)<=3600)
		      {
		    		i++;
		      }
		      else
		      {
		    	  DriverList.remove(i);
		      }
			 }
		}
	}
	//特殊車輛不接超過5趟
	public void SpecialCarFilter(List<DriverTable> DriverList)
	{
		for(int index=0;index<Variable.SpecialCar.size();index++)
		{
			for(int i = 0; i < DriverList.size(); )
			{
				//特殊車輛
				if(Variable.SpecialCar.get(index).indexOf(DriverList.get(i).ID)!=-1)
				{
					//大於5趟就刪除
					if(DriverList.get(i).ArrangedCount>6)
					{
						DriverList.remove(i);
					}
					break;
				}
				else
				{
					i++;
				}
			}
		}
	}
/*	//路的配對 目前路去尋找上一趟或下一趟是否有相同路
	public void RoadToRoadFilter(List<DriverTable> DriverList)
	{
		String[] tempaddress;	
		String[] check=new String[]{"街","路","道"};
		String[] reqtempaddress;	
		
		for(int index = 0; index<=2;index++ )
		{
			if(Reqtable.OriginAddress.indexOf(check[index])!=-1)
			{ 
				System.out.println(Reqtable.OriginAddress.substring(0,Reqtable.OriginAddress.indexOf(check[index])));
				break;
			}
		}
		for(int index = 0; index<=2;index++ )
		{
			if(Reqtable.DestinationAddress.indexOf(check[index])!=-1)
			{ 
				System.out.println(Reqtable.DestinationAddress.substring(0,Reqtable.DestinationAddress.indexOf(check[index])));
				break;
			}
		}
		for(int index = 0; index<=2;index++ )
		{
			//if(Reqtable.OriginAddress.indexOf(check[index])!=-1)
				
		}
		for(int i = 0; i < DriverList.size(); )
		{
			tempaddress= new String[]{"null","null","null","null"};
			
			for(int timeindex = 1; timeindex<=20;timeindex++ )
			{
				//尋找上一趟與下一趟的地址.
				if(tempaddress[0]=="null"&&tempaddress[1]=="null")
					Getaddress(DriverList.get(i).TimeInterval[StartInterval-timeindex],tempaddress,0);
				if(tempaddress[2]=="null"&&tempaddress[3]=="null")
					Getaddress(DriverList.get(i).TimeInterval[StartInterval+timeindex],tempaddress,1);
				
				if(tempaddress[0]=="null"&&tempaddress[1]=="null"
					&&tempaddress[2]=="null"&&tempaddress[3]=="null")
					break;
			}
			for(int index = 0; index<=3;index++ )
			{
				//if(tempaddress[index].indexOf("")&&)
			}
		}
		
	}
	
	*/
	
	//車種filter，單純的過濾掉不符合預約所指定的車種
	public void CarFilter(List<DriverTable> DriverList)
	{
		//執行filter的條件，flag == 1 以及 用戶有指定車種
		if(FiltersEnable[0] == 1 &&!(Reqtable.Car.equals("")))
		{ 	//System.out.println(DriverList.size());
				for(int i = 0; i < DriverList.size(); )
				{
					//車種不符的過濾掉
					if(!(DriverList.get(i).Car.equals(Reqtable.Car)))
					{
						DriverList.remove(i);
					}
					else
					{
						i++;
					}
				}
			}		
	}
	//新來司機不接共乘乘客
	public void AssignSharingCarFilter(List<DriverTable> DriverList)
	{
		//有共乘的乘客才執行
		if(Reqtable.AssignSharing!=-1)
		{ 	
			for(int index=0;index<Variable.TiroDriver.size();index++)
			{
				for(int i = 0; i < DriverList.size(); )
					{
					//過濾掉新來的司機
					if(Variable.TiroDriver.get(index).indexOf(DriverList.get(i).ID)!=-1)
					{
						DriverList.remove(i);
						break;
					}
					else
					{
						i++;
					}
				}
			}
		}		
	}	
	//狀態filter，TimeInterval中標註的必須為未排班，或者只排的一個預約者且他願意共乘
	public void StatusFilter(List<DriverTable> DriverList) throws IOException
	{
		
	 if(FiltersEnable[1] == 1)
	  {
		for(int i = 0; i < DriverList.size(); i++)
		{
			//int existRequest = -1;			
			if(!(DriverList.get(i).startreqtime<Reqtable.originalStartTime&&DriverList.get(i).endreqtime>Reqtable.originalStartTime))
			{
				DriverList.remove(i);
				i--;
				continue;
			}
			if(!(DriverList.get(i).startreqtime<Reqtable.originalDestinationTime&&DriverList.get(i).endreqtime>Reqtable.originalDestinationTime))
			{
				DriverList.remove(i);
				i--;
				continue;
			}
			
			for(int j = StartInterval; j <= EndInterval; j++)
			{
				
				//上車時段到下車時段間司機的排班必須為空的，TimeInterval[index]內存入的狀態為不上班，移除這台車然後檢測下台車
				if(DriverList.get(i).TimeInterval[j].indexOf("不上班")!=-1)
				{
					DriverList.remove(i);
					i--;
					break;
				}				
				//TimeInterval[index]已排入行程
				else if(!(DriverList.get(i).TimeInterval[j].indexOf("未排班")!=-1))
				{
					DriverList.remove(i);
					i--;
					break;
					//本身不想跟別人共乘
					/*-if(!Reqtable.Share)
					{
						DriverList.remove(i);
						i--;
						break;
					}
					//可以接受共乘
					else
					{
						//判斷TimeInterval排入的行程是否已經是共乘狀態
						String[] temp = DriverList.get(i).TimeInterval[j].split("_");
						//已經兩個人排入這個時段內，移除這輛車
						if(temp.length > 1)
						{
							DriverList.remove(i);
							i--;
							break;
						}
						//只排入一個人
						else if(temp.length == 1)
						{
							//排入的人不想共乘，移除這輛車
							RequestTable getIndex = IndexMap.get(Integer.valueOf(temp[0]));
							if(!Reqtable.Share)
							{
								DriverList.remove(i);
								i--;
								break;
							}
							else
							{
								//搜尋範圍內不能有一筆以上已排入的使用者
								if(existRequest == -1)
								{
									existRequest = getIndex.Number;
								}
								else if(existRequest > -1 && existRequest !=getIndex.Number)
								{
									DriverList.remove(i);
									i--;
									break;
								}
							}
						}
						else
						{
							//System.out.println("Split the TimeInterval String error!!!");
						}
					}*/
				}
				
			}
		}
	}
	}
	//區域Filter
		public  void areaFilter(List<DriverTable> DriverList,int Classification)
		{
			//conform[0]:上一班預約者與當前預約者是否有符合區域表 conform[1]:下一班預約者與當前預約者是否有符合區域表
			int[] Area = {-1,-1};
		
			for(int i = 0; i < DriverList.size(); i++)
			{
		
			 RequestTable PreReqIndex = null;//紀錄上一班預約者或下一班預約者
			 //上車時間尋找上一班預約者
			 for(int  index = StartInterval; index >= 0; index--)
			 {
				if(index == 0)//尋找到第0區間
				{
					continue;
				}
				//找到上一個有排班的TimeInterval
				if(!(DriverList.get(i).TimeInterval[index].equals("不上班")) 
						&& !(DriverList.get(i).TimeInterval[index].equals("未排班")))
				{
				  //先判斷之前的下車地點在哪
				   String[] temp = DriverList.get(i).TimeInterval[index].split("_");
				   PreReqIndex = IndexMap.get(Integer.valueOf(temp[0]));
					break;
				}
			 }
			 if(PreReqIndex==null)//如果沒找到就把權限設為最遠
			 {
				 Area[0]=9;
			 }
			 else
			 {
				 //記錄上一班乘客下車地點與目前預約者的上車地點的權重值
				 Area[0]=defineVariable.Weight[defineVariable.switchareaindex(PreReqIndex.Destinationarea)][defineVariable.switchareaindex(Reqtable.Originarea)];
			 }
			 
			 //下車尋找下一班車
			 RequestTable NextReqIndex = null;//紀錄上一班預約者或下一班預約者變數初始化
			 for(int index = EndInterval; index <=  DriverList.get(i).TimeInterval.length; index++)
			 {
			    if(index == DriverList.get(i).TimeInterval.length)//如果尋找到最後一個區間
				{
					 continue;
				}
			    if(!DriverList.get(i).TimeInterval[index].equals("不上班") 
			    		&& !DriverList.get(i).TimeInterval[index].equals("未排班"))
				{
			    	//判斷之後的上車地點在哪	
			    	String[] temp = DriverList.get(i).TimeInterval[index].split("_");
			    	NextReqIndex = IndexMap.get(Integer.valueOf(temp[0]));
			    	break;				    
				 }
			  }
			  if(NextReqIndex==null)//如果沒找到就把權限設為最遠
			  {
				 Area[1]=9;
			  }
			  else
			  {
				 //記錄上一班乘客下車地點與目前預約者的上車地點的權重值
				 Area[1]=defineVariable.Weight[defineVariable.switchareaindex(Reqtable.Destinationarea)][defineVariable.switchareaindex(NextReqIndex.Originarea)];
			  }
			  if(NextReqIndex!=null&&PreReqIndex!=null)
			  { 
				  if(PreReqIndex.DestinationAddress.indexOf(NextReqIndex.OriginAddress)!=-1 )
					  if((NextReqIndex.OriginTime- PreReqIndex.originalDestinationTime>=9000)&&(NextReqIndex.OriginTime- PreReqIndex.originalDestinationTime<=14400))
					  {
						  DriverList.remove(i);//如果不符合就刪除司機
						  i--;
						  continue;
					  }
			  }
			   //確認目前找的是哪一個區域類別
			  if(!(CheckAreaWeightsClass(Classification,Area)))
			  {
				 DriverList.remove(i);//如果不符合就刪除司機
				 i--;
			  }
		   }	
	}	
	//區域Filter1 與原本的區域Filter功能差別在可置換夜班Weight
	public  void areaFilter1(List<DriverTable> DriverList,int run) throws IOException
	{
		
		//conform[0]:上一班預約者與當前預約者是否有符合區域表 conform[1]:下一班預約者與當前預約者是否有符合區域表
		boolean[] conform = {false,false};
		
		for(int i = 0; i < DriverList.size(); i++)
		{
		 conform[0] =false;//初始化上一班預約者的flag為false
		 conform[1] =false;//初始化下一班預約者的flag為false
		 conform[0]=checkeight(DriverList.get(i),run,defineVariable.switchareaindex(DriverList.get(i).station),defineVariable.switchareaindex(Reqtable.Originarea));//如果與上一班車區域表的權重值為9代表無法支援 所以conform flag為false
	     conform[1]=checkeight(DriverList.get(i),run,defineVariable.switchareaindex(DriverList.get(i).station),defineVariable.switchareaindex(Reqtable.Destinationarea));		 
		
	     if((!conform[0])||(!conform[1]))	//判斷前後區域是否有符合	
		 {
		   DriverList.remove(i);//如果不符合就刪除司機
		   i--;
		}	
	   }	
	 }	
	
	
	//檢查回廠區域的Filter
	public  void endareaFilter(List<DriverTable> DriverList) throws IOException
	{
		for(int i = 0; i < DriverList.size(); i++)
		{
			/* if(DriverList.get(i).ID.equals("8400-A2")&&Reqtable.RequestNumber.equals("6270"))
				 System.out.println("fff");*/
			//如果預約者上車時間大於司機的中間時段+1小時需檢查回廠區域 //上車時間需還原為原來上車時間
			if((Reqtable.OriginTime+300)>=(DriverList.get(i).halfworktime+Variable.halfworktimeTolerableTime))
			 {
								
				//檢查上車是否符合回廠區域
				if(!defineVariable.backareaWeight[defineVariable.switchareaindex(DriverList.get(i).station)][defineVariable.switchareaindex(Reqtable.Originarea)])//查詢車輛與場站之間權重			
				 {
					 DriverList.remove(i);//不符合刪除
					 i--;
					 continue;
					
				 }
				//檢查下車是否符合回廠區域
				 if(!defineVariable.backareaWeight[defineVariable.switchareaindex(DriverList.get(i).station)][defineVariable.switchareaindex(Reqtable.Destinationarea)])//查詢車輛與場站之間權重			
				 {
					 DriverList.remove(i);//不符合刪除
					 i--;										
				 }
				
			 }			
		}
		
	}
	//判斷該司機是否來的及接送這位預約者
	public int DistanceTimeFilter(List<DriverTable> DriverList)
	{
		//非共乘者計算時間
		if(FiltersEnable[2] == 1&&Reqtable.AssignSharing==-1)
		{
			//int recentTimeResult = -1;								//交通時間
			boolean canbreak = false;	
			for(int i = 0; i < DriverList.size(); i++)
			{

				/* if(DriverList.get(i).ID.equals("8400-A2")&&Reqtable.RequestNumber.equals("6270"))
					 System.out.println("fff");*/
				//回傳小於等於-2代表google查詢有錯立即終止程式
				if(Variable.errorcode<=-2)
				{
					break;
				}
				
				canbreak = false;
				//檢查上一班下車地點到目前預約者上車地點是否來得及
				if(DriverList.get(i).StartDistanceValue>0)
				{
					//假設這司機來不及接當前排的預約者就刪除，否則檢查下車來不來的及下一個預約者
					if((Reqtable.OriginTime-DriverList.get(i).PreviousrequstTime)< DriverList.get(i).StartDistanceValue)
					{
						DriverList.remove(i);
						i--;
						canbreak=true;
					}
				}
				else
				{
					//尋找上一趟
					RequestTable TableIndex=Variable.PreRequestTableQuery(DriverList.get(i),StartInterval,Variable,IndexMap);
					int [] traveltime=new int[2];//第一格紀錄上一個req的Interval index  第二格旅行時間
					traveltime=Variable.DistanceTime(TableIndex,Reqtable,smartSearch,Variable,IntervalSec);
					if(traveltime[1]<=-2)
					{
						//回傳小於等於-2代表google查詢有錯立即終止程式
						Variable.errorcode=traveltime[1];
							break;
					}
					//這筆request的預約時間減去上一地點的時間小於google maps api取得的時間，刪去這個司機
					if((Reqtable.OriginTime-TableIndex.DestinationTime)< traveltime[1] || traveltime[1] < 0)
					{
						DriverList.remove(i);
						i--;
						canbreak=true;
					}
					else
					{
						//當所有條件的過濾器都過濾完後，還剩餘超過一輛車，需透過DistanceValue的旅行時間資料，去選擇交通時間較短的車輛
						//紀錄司機要接當前預約者的旅行時間
						DriverList.get(i).StartDistanceValue=traveltime[1];
					}				 
				}
				
				//檢查目前預約者下車地點到下一班上車地點是否來得及
				if(canbreak)//如果來不及就不用檢查下車區域
				{
					continue;
				}
				
				//下車地點與之後的上車地點間的旅行時間
				if(DriverList.get(i).EndDistanceValue>0)
				{
					if((DriverList.get(i).NextrequstTime - Reqtable.DestinationTime)< DriverList.get(i).EndDistanceValue)
					{
						DriverList.remove(i);
						i--;	
					}
				}
				else
				{
					RequestTable NextTableIndex=Variable.NextRequestTableQuery(DriverList.get(i),EndInterval,Variable,IndexMap);
					int [] traveltime=new int[2];//第一格紀錄上一個req的Interval index  第二格旅行時間
					traveltime=Variable.DistanceTime(Reqtable,NextTableIndex,smartSearch,Variable,IntervalSec);
					if(traveltime[1]<=-2)
					{
						//回傳小於等於-2代表google查詢有錯立即終止程式
						Variable.errorcode=traveltime[1];
							break;
					}
					//判斷是否來得及
					if((NextTableIndex.OriginTime - Reqtable.DestinationTime)< traveltime[1] || traveltime[1] < 0)
					{
						DriverList.remove(i);
						i--;	
						
					}
				}
			}
		}
		return  Variable.errorcode;
	}	
	//判斷指定共乘是否來得及
	public int AssignSharingDistanceTimeFilter(List<DriverTable> DriverList) throws InterruptedException, IOException
	{
		if(Reqtable.AssignSharing!=-1)
		{
			RequestTable AssignSharingReq=IndexMap.get(Reqtable.AssignSharing);
			for(int i = 0; i < DriverList.size(); i++)
			{ 
				boolean canbreak = false;	
				//找出上一趟的預約者	
				//回傳小於等於-2代表google查詢有錯立即終止程式
				if(Variable.errorcode<=-2)
				{
					break;
				}
				RequestTable tableindex=Variable.PreRequestTableQuery(DriverList.get(i),StartInterval,Variable,IndexMap);		    
				//查詢當前預約者跟上一班的旅行時間
				int [] traveltime=Variable.DistanceTime(tableindex,Reqtable,smartSearch,Variable,IntervalSec);
				//回傳error code
			     if(traveltime[1]<=-2)
				 {
				   Variable.errorcode=traveltime[1];
				   break;
				}
				//查詢與當前預約者共乘跟上一班的旅行時間
				int [] traveltime1=Variable.DistanceTime(tableindex,AssignSharingReq,smartSearch,Variable,IntervalSec);
				//回傳error code
				if(traveltime1[1]<=-2)
				{
					Variable.errorcode=traveltime1[1];
					break;
				}					
				//司機接當前正在處理的預約者的旅行時間比共乘的預約者的短
				boolean staus=false;
				int OriginTime=0;
				int PreTraveltime=-1;
				//看共乘哪一個比較先上車
				if(Reqtable.OriginTime<AssignSharingReq.OriginTime)
					OriginTime=Reqtable.OriginTime;
				else
					OriginTime=AssignSharingReq.OriginTime;
				//在判斷哪一個旅行時間較長
				if(traveltime[1]<traveltime1[1])
				{
					PreTraveltime=traveltime1[1];
				}
				else
				{
					PreTraveltime=traveltime[1];
				}
				//判斷來不來的及
				//這筆request的預約時間減去上一地點的時間小於google maps api取得的時間，刪去這個司機
				staus=checkDistanceTime(PreTraveltime,(OriginTime-tableindex.DestinationTime));
				//來不及或旅行時間<0刪除
				if(!staus||(PreTraveltime<0))
				{
					DriverList.remove(i);
					i--;
					canbreak=true;
				}
				else
				{
					//當所有條件的過濾器都過濾完後，還剩餘超過一輛車，需透過DistanceValue的旅行時間資料，去選擇交通時間較短的車輛
					//紀錄司機要接當前預約者的旅行時間
					DriverList.get(i).StartDistanceValue=traveltime[1];
				}
				
				//回傳小於等於-2代表google查詢有錯立即終止程式
				if(Variable.errorcode<=-2)
				{
					break;
				}
				if(canbreak)//如果來不及就不用檢查下車區域
			    {
					continue;
				}
				
				//找出下一趟
				for(int nextindex=EndInterval;nextindex<DriverList.get(i).TimeInterval.length;nextindex++)
				{
					 if(!(DriverList.get(i).TimeInterval[nextindex].indexOf("不上班")!=-1) 
							 &&!(DriverList.get(i).TimeInterval[nextindex].indexOf("未排班")!=-1))
					 {  
						 //找到下一趟預約者
						 RequestTable nextrep=Variable.RequestTableQuery(DriverList.get(i).TimeInterval[nextindex],Variable,IndexMap);								
						 if(nextrep.originalStartTime>Reqtable.originalStartTime)
						 {
							 //查詢當前預約者跟下一班的旅行時間
							 traveltime=Variable.DistanceTime(Reqtable,nextrep,smartSearch,Variable,IntervalSec);
							 //回傳error code
							 if(traveltime[1]<=-2)
							 {
								 Variable.errorcode=traveltime[1];
								 break;							 
							 }
							 //查詢與當前預約者共乘跟下一班的旅行時間
							 traveltime1=Variable.DistanceTime(AssignSharingReq,nextrep,smartSearch,Variable,IntervalSec);
							 //回傳error code
							 if(traveltime1[1]<=-2)
							 {
								 Variable.errorcode=traveltime1[1];
								 break;
							 }
							 
							 int DestinationTime=0;
							 int NextTraveltime=-1;
							//看共乘哪一個比較晚下車
							if(Reqtable.DestinationTime>AssignSharingReq.DestinationTime)
								DestinationTime=Reqtable.DestinationTime;
							else
								DestinationTime=AssignSharingReq.DestinationTime;
								//在判斷哪一個旅行時間較長
							if(traveltime[1]<traveltime1[1])
							{
								NextTraveltime=traveltime1[1];
							}
							else
							{
								NextTraveltime=traveltime[1];
							}
							staus=false;
							//判斷來不來的及接到下一班預約者	
							staus=checkDistanceTime(NextTraveltime,(nextrep.OriginTime - DestinationTime));
							//來不及或旅行時間<0刪除
							if(!staus||(NextTraveltime<0))
							{
								DriverList.remove(i);
								i--;							
							}
							break;
						  }
						 }
					 }
				 }
			}
		return  Variable.errorcode;
	}
		//檢查是否有足夠休息時間
	public void restFilter(List<DriverTable> DriverList)
	{
		for(int i = 0; i < DriverList.size(); i++)
		{ 
			//回傳小於等於-2代表google查詢有錯立即終止程式
			if(Variable.errorcode<=-2)
			{
				break;
			}
			//工時小於6小時不找休息時間
			if((DriverList.get(i).EndTime-DriverList.get(i).StartTime)<=Variable.nonrelax)
				continue;
			int startInterval = (int)((DriverList.get(i).startreqtime+1800) / IntervalSec);//計算頭班所在區間
			//計算尾班所在區間				
			int endInterval = ((DriverList.get(i).endreqtime-1800) /(int) IntervalSec);
			int startindex=startInterval;				
			int endindex=endInterval;
			//先複製原本休息區間的array給temprelaxarry
			DriverList.get(i).temprelaxarry=null;///清空
			DriverList.get(i).temprelaxarry=new ArrayList<String>(DriverList.get(i).relaxarry);
			//如果預約者時間區間落在司機可休息時間內就要檢查是否有足夠的休息時間	
			if(DriverList.get(i).relaxarry.size()>=2)
			{
				//先刪除req所佔的區間
				for(int index = StartInterval; index <= EndInterval; index++)
				{
					//有落在休息區間就刪除
					if(index>=startindex&&index<=endindex)
						DriverList.get(i).temprelaxarry.remove(String.valueOf(index));
				}
				//找出上一趟的預約者
				RequestTable tableindex;
				tableindex = Variable.PreRequestTableQuery(DriverList.get(i),StartInterval+1,Variable,IndexMap);				
				//先處理當下的預約者上車與上一趟下車之間
				//找出上一班有排班的區間旅行時間 第一格為上一趟所在的區間 第二格為旅行時間	
				if(tableindex==null)
					System.out.println("");
				int [] traveltime=Variable.DistanceTime(tableindex,Reqtable,smartSearch,Variable,IntervalSec);
				//回傳error code
				if(traveltime[1]<=-2)
				{
					Variable.errorcode=traveltime[1];
						break;
				}
				
				//計算加上容忍時間
				int temptraveltime=traveltime[1]+Variable.TolerableTime;
				//紀錄上一個下車來接這趟旅行時間
				DriverList.get(i).StartDistanceValue=traveltime[1];
				//紀錄上一個預約者下車時間
				DriverList.get(i).PreviousrequstTime=tableindex.DestinationTime;
				//如果上一趟與與當前趟其中一個遇到尖峰時刻要加上delay time			
				if((Reqtable.originalStartTime>=Variable.areaPrioritystartmorningpeaktime&&Reqtable.originalStartTime<=Variable.areaPriorityendmorningpeaktime)
						||(tableindex.originalDestinationTime>=Variable.areaPrioritystartmorningpeaktime&&tableindex.originalDestinationTime<=Variable.areaPriorityendmorningpeaktime))
					temptraveltime+=Variable.morningpeaktime;                       //早上7:30~8:30尖峰時段再加上15分延遲
				if(Reqtable.originalStartTime>=Variable.areaPrioritystartafternoonpeaktime&&Reqtable.originalStartTime<=Variable.areaPriorityendafternoonpeaktime
						||(tableindex.originalDestinationTime>=Variable.areaPrioritystartafternoonpeaktime&&tableindex.originalDestinationTime<=Variable.areaPriorityendafternoonpeaktime))
					temptraveltime+=Variable.afternoonpeaktime;                      //下午16:45~18:30尖峰時段再加上20分延遲					
				//上一趟下車所剩下可扣的時間
				int tempSecond=(tableindex.originalDestinationTime-(tableindex.originalDestinationTime%60));
				int min = (int)((tempSecond % 3600) / 60);//轉化成分
				//扣掉可用殘餘的時間
				int Residualtime=0;
				if(min>=30&&min<60)
					Residualtime=(60-min);
				else if(min<30&&min>=0)
					Residualtime=(30-min);
				temptraveltime=temptraveltime-(Residualtime*60);					
				//這趟上車所剩的時間
				tempSecond=(Reqtable.originalStartTime-(Reqtable.originalStartTime%60));
				min = (int)((tempSecond % 3600) / 60);//轉化成分
				Residualtime=0;
				if(min==30||min==0)
					Residualtime=0;
				else if(min<30)
					Residualtime=min;
				else if(min>30)
					Residualtime=(min-30);
				//扣掉可用殘餘的時間
				temptraveltime-=(Residualtime*60);
				//計算要花費的格數
				int Spendtimecount=0;
				Spendtimecount= (temptraveltime / (int) IntervalSec);
				//刪除開車所花費的格數				
				for(int count=traveltime[0]+1;count<(traveltime[0]+Spendtimecount+1);count++)
				{
					if(count>=startindex&&count<=endindex)
						DriverList.get(i).temprelaxarry.remove(String.valueOf(count));//刪除開車所花費的時間
				}
				//如果與上一趟占用格數計算完休息區間小於2格立即刪除
				if(DriverList.get(i).temprelaxarry.size()<2)
				{
					//如果可用的休息時間小於1小時就刪除
					DriverList.get(i).temprelaxarry=null;
					DriverList.remove(i);
					i--;
					continue;
				}
				//回傳小於等於-2代表google查詢有錯立即終止程式
				if(Variable.errorcode<=-2)
				{
					break;
				}
				//處理下一趟與這趟	EndInterval:是當前req的結束區間	 	
				for(int nextindex=EndInterval;nextindex<DriverList.get(i).TimeInterval.length;nextindex++)
				{
					if(!(DriverList.get(i).TimeInterval[nextindex].indexOf("不上班")!=-1) 
						&&!(DriverList.get(i).TimeInterval[nextindex].indexOf("未排班")!=-1)
						&&!(DriverList.get(i).TimeInterval[nextindex].indexOf("休息")!=-1))
					{    //找到下一趟預約者
						 RequestTable nextrep=Variable.RequestTableQuery(DriverList.get(i).TimeInterval[nextindex],Variable,IndexMap);
						if(nextrep.originalStartTime>Reqtable.originalStartTime)
						{
							//計算兩者之間旅行時間
							 int [] traveltime1=Variable.DistanceTime(Reqtable,nextrep,smartSearch,Variable,IntervalSec);
							 if(traveltime1[1]<=-2)
							 {
								Variable.errorcode=traveltime1[1];
								break;
							}							 
							//計算加上容忍時間
							int temptraveltime1=traveltime1[1]+Variable.TolerableTime;
							//紀錄這趟預約者下車去接下一趟旅行時間
							DriverList.get(i).EndDistanceValue=traveltime1[1];
							//紀錄下一個預約者上車時間
							DriverList.get(i).NextrequstTime=nextrep.OriginTime;
							//如果上一趟與與當前趟其中一個遇到尖峰時刻要加上delay time
							//如果上一趟與與當前趟其中一個遇到尖峰時刻要加上delay time			
							if((Reqtable.originalStartTime>=Variable.areaPrioritystartmorningpeaktime&&Reqtable.originalStartTime<=Variable.areaPriorityendmorningpeaktime)
									||(tableindex.originalDestinationTime>=Variable.areaPrioritystartmorningpeaktime&&tableindex.originalDestinationTime<=Variable.areaPriorityendmorningpeaktime))
								temptraveltime1+=Variable.morningpeaktime;                       //早上7:30~8:30尖峰時段再加上15分延遲
							if(Reqtable.originalStartTime>=Variable.areaPrioritystartafternoonpeaktime&&Reqtable.originalStartTime<=Variable.areaPriorityendafternoonpeaktime
									||(tableindex.originalDestinationTime>=Variable.areaPrioritystartafternoonpeaktime&&tableindex.originalDestinationTime<=Variable.areaPriorityendafternoonpeaktime))
								temptraveltime1+=Variable.afternoonpeaktime;                      //下午16:45~18:30尖峰時段再加上20分延遲		
										
							//下一趟上車所剩下可扣的時間	
							tempSecond=(nextrep.originalStartTime-(nextrep.originalStartTime%60));
							min=0;
							min = (int)((tempSecond % 3600) / 60);//轉化成分
							Residualtime=0;
							if(min==30||min==0)
								Residualtime=0;
							else if(min<30)
								Residualtime=min;
							else if(min>30)
								Residualtime=(min-30);
							//扣掉可用殘餘的時間
							temptraveltime1=temptraveltime1-(Residualtime*60);
							//這一趟下車所剩下可扣的時間
							tempSecond=(Reqtable.originalDestinationTime-(Reqtable.originalDestinationTime%60));
							min=0;
							min = (int)((tempSecond % 3600) / 60);//轉化成分
							//扣掉可用殘餘的時間
							Residualtime=0;
						   if(min>=30&&min<60)
								 Residualtime=(60-min);
							else if(min<30&&min>=0)
								Residualtime=(30-min);
							temptraveltime1=temptraveltime1-(Residualtime*60);
							//計算要花費的格數
							Spendtimecount=0;
							Spendtimecount= (temptraveltime1 / (int) IntervalSec);
							//刪除開車所花費的格數				
							for(int count=traveltime1[0]+1;count<(traveltime1[0]+1+Spendtimecount);count++)
							{
								if(count>=startindex&&count<=endindex)
									DriverList.get(i).temprelaxarry.remove(String.valueOf(count));//刪除開車所花費的時間
							}
							break;
						}
					}	
				}	
			    //如果與下一趟占用格數計算完休息區間小於2格立即刪除
				if(DriverList.get(i).temprelaxarry.size()<2)
				{
					//如果可用的休息時間小於1小時就刪除
					DriverList.get(i).temprelaxarry=null;
					DriverList.remove(i);
					i--;
				}
			}
			else
			{
				//如果可用的休息時間小於1小時就刪除
				DriverList.remove(i);
				i--;
			}
	 }
	}
	//檢查趟數上限
	public void maxofTrip(List<DriverTable> DriverList) throws InterruptedException, IOException
	{
		for(int i = 0; i < DriverList.size(); i++)
		{
			if(DriverList.get(i).ArrangedCount>15)
			{
				DriverList.remove(i);
				i--;
			}
		}
	}
	//將超過一台以上的候補車輛刪減到剩下一台
	public DriverTable MinFilter(List<DriverTable> DriverList,boolean nightflag) throws IOException
	{		
		DriverTable TargetDriver=null;//預約者選重的司機			
		//司機的總數要大於0才能選
		if(FiltersEnable[4] == 1&&DriverList.size()>0)
		{
			int minValue = -1;//紀錄司機到預約者的最短距離
			int runCount = -1;//紀錄最大的連續Count			 
			for(int i = 0; i < DriverList.size(); i++)
			{
				if(nightflag)
				{
					//如果此車輛抵達此預約之上車地點所花費的時間較記錄的時間短
					if(minValue==-1||DriverList.get(i).StartDistanceValue< minValue)
					{
						//更新交通時間紀錄
						minValue = DriverList.get(i).StartDistanceValue;
						//更新選重車輛	
						TargetDriver=DriverList.get(i);
					}
				}else
				{
					//一般就找最多排班數
					if((runCount == -1) ||(DriverList.get(i).ArrangedCount>runCount))
					 {
						//記錄最大的排入預約數量
						runCount = DriverList.get(i).ArrangedCount;
						//記錄此車輛抵達此預約之上車地點所花費的時間
					    minValue = DriverList.get(i).StartDistanceValue;
						//記錄選重車輛					    
					    TargetDriver=DriverList.get(i);
					    }
					//如果車輛的閒置數量一樣多的情況，比對車輛抵達此預約之上車地點所花費的時間，選擇花費時間較少的車輛
					else if(DriverList.get(i).ArrangedCount == runCount)
					{
						//如果此車輛抵達此預約之上車地點所花費的時間較記錄的時間短
						if(DriverList.get(i).StartDistanceValue< minValue)
						{
							//更新交通時間紀錄
							minValue = DriverList.get(i).StartDistanceValue;
							//更新選重車輛							
							TargetDriver=DriverList.get(i);
						}
					}
				}
		  }
	}
		return TargetDriver;
	}	
	
	//判斷是否來不來的及再允許的時間內趕上
	public boolean checkDistanceTime(int DistanceValue,int AllowTravelTime)
	{ 
		if(AllowTravelTime<DistanceValue)
		{
			return false;
		}
		else
		{
			return true;
		}
	}		
	public boolean checkeight(DriverTable DriverTable,int run,int area1,int area2)
	{
		if(run==0)
		{
			if(defineVariable.Weight[area1][area2]==9)//查詢車輛與需求表之間權
			{
				return false;//如果與上一班車區域表的權重值為9代表無法支援 所以conform flag為false
			}
			else
			{
				return true;//如果與上一班車區域表符合conform flag為true
			}
		}else if(DriverTable.StartTime<43200)//第2趟check不是夜班司機
		{
			if(defineVariable.Weight[area1][area2]==9)//查詢車輛與需求表之間權
			{
				return false;//如果與上一班車區域表的權重值為9代表無法支援 所以conform flag為false
			}
			else
			{
				return true;//如果與上一班車區域表符合conform flag為true
			}
		}
		else //第2趟check是夜班司機
		{
			if(defineVariable.nightareaWeight[area1][area2]==9)//查詢車輛與需求表之間權
			{
				
				return false;//如果與上一班車區域表的權重值為9代表無法支援 所以conform flag為false
			}
			else
			{
				return true;//如果與上一班車區域表符合conform flag為true
			}
		}
	}
	public String[]  Getaddress(String reqnum,String[] tempaddress,int mode)
	{
		String[] number=reqnum.split("_");
		if(number.length<2)
		{
			if(number[0].indexOf("班")==-1)
			{
				RequestTable req=IndexMap.get( Integer.valueOf(number[0]));
				if(mode==0)
				{	
					tempaddress[0]=req.OriginAddress;
					tempaddress[1]=req.DestinationAddress;
				}
				else
				{
					tempaddress[2]=req.OriginAddress;
					tempaddress[3]=req.DestinationAddress;
				}
			}
		}else
		{
			
		}
		
		//IndexMap=0;
		return tempaddress;
	}
	//判斷
	public boolean CheckAreaWeightsClass(int Classification,int[] Area)
	{ 
		boolean Conform=false;
		//檢查目前否符合要找尋的類別
		switch(Classification)
		{
			case 1:	  
				if(Area[0]==1&&Area[1]==1)
					Conform=true;
		  		break;
		  		
		  	case 2:
		  		if(Area[0]<=1&&Area[1]<=2)
					Conform=true;
		  		else if(Area[0]<=2&&Area[1]<=1)
					Conform=true;
		  		break;
		  		
		  	case 3:
		  		if(Area[0]<=2&&Area[1]<=2)
					Conform=true;
		  		break;
		  		
		  	case 4:
		  		if(Area[0]<=3&&Area[1]<=2)
					Conform=true;
		  		else if(Area[0]<=2&&Area[1]<=3)
					Conform=true;
		  		break;
		  		
		  	case 5:
		  		if(Area[0]==3&&Area[1]==3)
					Conform=true;
		  		break;				  
		  }
		return Conform;	  
	 }
}

