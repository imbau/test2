//排班filter部分，可以根據需求抽換module
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Filter
{
	private int[] FiltersEnable;
	private double Interval;
	private int StartInterval;
	private int EndInterval;
	private double IntervalSec;
	Map<Integer, RequestTable> IndexMap = new HashMap<Integer, RequestTable>();
	private ILF smartSearch = null;
	private defineVariable Variable;	
	private DriverTable Driver=null;
	Map<Integer,DriverTable> carmap=null;		
	RequestTable TableIndex=null;
	RequestTable NextTableIndex=null;
	String[] tempaddress= new String[]{"null","null","null","null"};
	boolean xindianRoadSplitArea=false;
	public Filter(int startindex,int[] enable, DriverTable driver, double interval, Map<Integer,RequestTable> indexmap,defineVariable variable, ILF inputilf) throws Exception
	{
		Variable = variable;//定義變數初始化
		FiltersEnable = enable;	
		Interval = interval;//一個interval的時間長短(小時)			
		IntervalSec = Interval * 3600;//將interval的時間由小時為單位轉成由秒為單位	
		Driver=driver;//當前的司機
		IndexMap = indexmap;//存放所有預約者queue		
		Variable.input = new double[4];//存放上下車地址的經緯度值，單純用來當作傳入function用的參數。input[0]為上車地址的緯度，input[1]為上車地址的經度，input[2]下車地址的緯度，input[3]為下車地址的經度。	
		Variable.address = new String[2];//存放上下車地點的地址，單純用來當作傳入function用的參數。address[0]為上車地址，address[1]為下車地址。
		//canShare = -1;//在判定共乘時所使用的參數，如果此值>-1代表這筆預約將和某比預約合併為共乘班次。
		smartSearch = inputilf;//找尋兩地點間交通時間的物件	
		StartInterval=startindex;//起始區間
		//往後尋找可用的空間 最多1.5小時空間
		for(int i = startindex; i <startindex+3;i++)
		{
			if(Driver.TimeInterval[i].indexOf("未排班")!=-1)
				EndInterval=i;
			else
			{
				EndInterval=i;
				break;
			}
		}
		 //尋找上一趟
		 TableIndex=Variable.PreRequestTableQuery(Driver,StartInterval,Variable,IndexMap);
		 //尋找下一趟
		 NextTableIndex=Variable.NextRequestTableQuery(Driver,EndInterval,Variable,IndexMap);
			//如果上一趟或下一趟都出現新店的郊區 xindianRoadSplitArea[index] index=0代表上車 1為下車
		 if(TableIndex.xindianRoadSplitArea[1])
		 {
			 xindianRoadSplitArea=TableIndex.xindianRoadSplitArea[1];
		 }else  if(NextTableIndex.xindianRoadSplitArea[0])
		{
			 xindianRoadSplitArea=NextTableIndex.xindianRoadSplitArea[0];
		}
		 //取得上一趟的地址 以方便做路的配對
		 Getaddress(tempaddress,0);
		 //取得下一趟的地址 以方便做路的配對
		 Getaddress(tempaddress,1);
	}
	//區域對應之間不接其他誇區
	public void AreaCorrespond(List<RequestTable> ReqList)
	{		
		//除新北市以外區域檢查對應的地址
		if(TableIndex.Destinationarea.indexOf("台北市")!=-1&&NextTableIndex.Originarea.indexOf("台北市")!=-1)
		{
			if(TableIndex.originalDestinationTime-NextTableIndex.originalStartTime<=10800)	
				if(TableIndex.DestinationAddress.indexOf(NextTableIndex.OriginAddress)!=-1)		
					ReqList.removeAll(ReqList);
		}/*else if(TableIndex.Destinationarea.indexOf("新北市鶯歌區")!=-1&&TableIndex.Destinationarea.indexOf("新北市三峽區")==-1)
		{
			if(TableIndex.DestinationAddress.indexOf(NextTableIndex.OriginAddress)!=-1)		
				ReqList.removeAll(ReqList);
		}*/
	
		 
	}
	public  void NoMoreThanTwoFilter(List<RequestTable> ReqList)
	{
		if(TableIndex!=null&&NextTableIndex!=null)
		{	
			for(int i = 0; i < ReqList.size();)
			{
				//早上7:30~8:30尖峰時段 或//下午16:45~18:30尖峰時段	
				if((ReqList.get(i).originalStartTime>=Variable.areaPrioritystartmorningpeaktime&& ReqList.get(i).originalStartTime<=Variable.areaPriorityendmorningpeaktime)
						 ||(ReqList.get(i).originalStartTime>=Variable.areaPrioritystartafternoonpeaktime&&ReqList.get(i).originalStartTime<=Variable.areaPriorityendafternoonpeaktime))
				{
					if((ReqList.get(i).originalStartTime-TableIndex.originalDestinationTime)<=3600&&(NextTableIndex.originalStartTime-ReqList.get(i).originalDestinationTime)<=3600)
					{
						i++;
					}
					else
					{
						ReqList.remove(i);	
					}
				}
				else
				{
					i++;
				}
			}
		}
		else
		{
			ReqList.removeAll(ReqList);
		}
	}
	//新店較山區的路過濾區域
	public void xindianRoadSplitAreaFilter(List<RequestTable> ReqList)
	{		
		if(xindianRoadSplitArea)
		{
			for(int i = 0; i < ReqList.size();i++)
			{
				if(ReqList.get(i).Originarea.indexOf("新店")==-1)
				{
					ReqList.remove(i);	
					i--;
					continue;
				}
				if(ReqList.get(i).Destinationarea.indexOf("新店")==-1)
				{
					ReqList.remove(i);	
					i--;					
				}
			}
		}
		
	}
	//路的配對 目前路去尋找上一趟或下一趟是否有相同路
	public void RoadToRoadFilter(List<RequestTable> ReqList)
	{
		String[] check=new String[]{"街","路","道"};
		String[] reqtempaddress = new String[]{"null","null"};	
		for(int i = 0; i < ReqList.size();i++)
		{
			//初始化
			reqtempaddress[0]="null";
			reqtempaddress[1]="null";
			//上車地址取出關鍵字
			for(int index = 0; index<=2;index++ )
			{
				if(ReqList.get(i).OriginAddress.indexOf(check[index])!=-1)
				{ 
					reqtempaddress[0]=ReqList.get(i).OriginAddress.substring(0,ReqList.get(i).OriginAddress.indexOf(check[index])+1);
					break;
				}
			 }
			 //下車地址取出關鍵字
			for(int index = 0; index<=2;index++ )
			{
				if(ReqList.get(i).DestinationAddress.indexOf(check[index])!=-1)
				{ 
					reqtempaddress[1]=ReqList.get(i).DestinationAddress.substring(0,ReqList.get(i).DestinationAddress.indexOf(check[index])+1);
					break;
				}
			}
			//比對司機裡的班表是否有包含關鍵字元
			for(int index = 0; index<=3;index++ )
			{
				if(tempaddress[index].indexOf(reqtempaddress[0])!=-1||tempaddress[index].indexOf(reqtempaddress[1])!=-1)
				{	
					i++;					
					break;	
				}		
				if(index==3)
					ReqList.remove(i);
			}
			
		}
		//System.out.println("355");
	}
	//新來司機不接共乘乘客
	public void AssignSharingCarFilter(List<RequestTable> ReqList)
	{
		for(int index=0;index<Variable.TiroDriver.size();index++)
		{
			if(Variable.TiroDriver.get(index).indexOf(Driver.ID)!=-1)
			{
				for(int i = 0; i < ReqList.size();i++)
				{
					//預約者有指定共乘則刪除
					if(ReqList.get(i).AssignSharing!=-1)
					{
						ReqList.remove(i);	
						i--;
					}
				}
				break;
			}
		}
	}
	//特殊車輛不接超過5趟
	public void SpecialCarFilter(List<RequestTable> ReqList)
	{
		for(int index=0;index<Variable.SpecialCar.size();index++)
		{
			if(Variable.SpecialCar.get(index).indexOf(Driver.ID)!=-1)
				if(Driver.ArrangedCount>6)
				{
					ReqList.removeAll(ReqList);
				}
		}
	}
	//車種filter，單純的過濾掉不符合車種的預約者
	public void CarFilter(List<RequestTable> ReqList)
	{
		for(int i = 0; i < ReqList.size(); )
		{
			if(ReqList.get(i).OriginAddress.indexOf("逢甲路224號")!=-1&&Driver.CallNum.indexOf("52")!=-1)
				System.out.println("");
			//車種不符的過濾掉			
			if(ReqList.get(i).Car.equals(""))//大車的預約者可以選擇大小車
				 i++;
			 else
			 {
				if(ReqList.get(i).Car.indexOf(Driver.Car)!=-1)//如果是小車的預約者只能選擇小車不能選大車
					i++;
				else
					 ReqList.remove(i);
				}	
			}
	}
	//狀態filter，過濾不能排進司機班表的預約者以及不符合上下班時間的預約者
	public void StatusFilter(List<RequestTable> ReqList)
	{
		//用原本上下車時間去算區間
		int ReqStartInterval = 0;
		//下車時間在一天中的interval index
		int ReqEndInterval =0;
		for(int i = 0; i < ReqList.size(); i++)
		{
			if(ReqList.get(i).Arrange)
			{	
				ReqList.remove(i);	
				i--;
				continue;
			}
			//用原本上下車時間去算區間
			 ReqStartInterval = (int)( (ReqList.get(i).originalStartTime) / IntervalSec);//上車時間在一天中的interval index
			//下車時間在一天中的interval index
			 ReqEndInterval = (ReqList.get(i).originalDestinationTime / (int) IntervalSec);
			if(!(Driver.startreqtime<ReqList.get(i).originalStartTime&&Driver.endreqtime>ReqList.get(i).originalStartTime))
			{
				ReqList.remove(i);
				i--;
				continue;
			}
			if(!(Driver.startreqtime<ReqList.get(i).originalDestinationTime&&Driver.endreqtime>ReqList.get(i).originalDestinationTime))
			{
				ReqList.remove(i);
				i--;
				continue;
			}
			//過濾目前司機的一小時以內可以用的預約者,超過1小時以外都刪除
			if(!(ReqStartInterval>=StartInterval&&ReqStartInterval<=EndInterval
					&&ReqEndInterval>=StartInterval&&ReqEndInterval<=EndInterval))
			{
				ReqList.remove(i);
				i--;
			}
		}
	}
	//區域Filter
	public  void areaFilter(boolean NightFlag,List<RequestTable> ReqList,int Classification)
	{   
			//conform[0]:上一班預約者與當前預約者是否有符合區域表 conform[1]:下一班預約者與當前預約者是否有符合區域表
			int[] Area = {-1,-1};
			for(int i = 0; i < ReqList.size(); i++)
			{			
				//如果要選的預約者出現新店偏遠道路就檢查
				if(ReqList.get(i).xindianRoadSplitArea[0])
				{	
					if(TableIndex.Destinationarea.indexOf("新店")==-1 )
					 {
							ReqList.remove(i);//如果不符合就刪除司機
							i--;
							continue;
					 }
				}
				if(ReqList.get(i).xindianRoadSplitArea[1])
					 if(NextTableIndex.Originarea.indexOf("新店")==-1 )
					 {
							ReqList.remove(i);//如果不符合就刪除司機
							i--;
							continue;
					 }		
				
				if(TableIndex.DestinationAddress=="null")//如果沒找到就把權限設為最遠
				{
					Area[0]=9;
				 }else
				 {
					 //記錄上一班乘客下車地點與目前預約者的上車地點的權重值
					 if(NightFlag)//判斷現在是否使用夜班 weight
					 { 
						 Area[0]=defineVariable.nightareaWeight[defineVariable.switchareaindex(Driver.station)][defineVariable.switchareaindex(ReqList.get(i).Originarea)];
					 }
					 else 
					 {
						 Area[0]=defineVariable.AreaWeight[Variable.Area.get(TableIndex.Destinationarea)-1][Variable.Area.get(ReqList.get(i).Originarea)];
					 }
				  }
				if(NextTableIndex.OriginAddress=="null")//如果沒找到就把權限設為最遠
				{
					Area[1]=9;
				}
			    else
				{
			    	//記錄上一班乘客下車地點與目前預約者的上車地點的權重值
			    	 if(NightFlag)//判斷現在是否使用夜班 weight
					 { 
			    		 Area[1]=defineVariable.nightareaWeight[defineVariable.switchareaindex(Driver.station)][defineVariable.switchareaindex(NextTableIndex.Originarea)];
					 }
					 else 
					 {
						 Area[1]=defineVariable.AreaWeight[Variable.Area.get(ReqList.get(i).Destinationarea)-1][Variable.Area.get(NextTableIndex.Originarea)];
					 }
			    	
				}
				  //確認目前找的是哪一個區域類別
				  if(!(CheckAreaWeightsClass(Classification,Area)))
				  {
						ReqList.remove(i);//如果不符合就刪除司機
						i--;
				  }
			}
	 }	
	/*//檢查回廠區域的Filter
	public  void endareaFilter(List<RequestTable> ReqList) 
	{
		for(int i = 0; i < ReqList.size(); i++)
		{
			//如果預約者上車時間大於司機的中間時段+1小時需檢查回廠區域
			if(ReqList.get(i).OriginTime>=(Driver.halfworktime+Variable.halfworktimeTolerableTime))
			 {
				//檢查上車是否符合回廠區域
				if(!defineVariable.backareaWeight[defineVariable.switchareaindex(Driver.station)][defineVariable.switchareaindex(ReqList.get(i).Originarea)])//查詢車輛與場站之間權重			
				 {
					 ReqList.remove(i);//不符合刪除
					 i--;
					 continue;
				 }
				//檢查下車是否符合回廠區域
				 if(!defineVariable.backareaWeight[defineVariable.switchareaindex(Driver.station)][defineVariable.switchareaindex(ReqList.get(i).Destinationarea)])//查詢車輛與場站之間權重			
				 {
					 ReqList.remove(i);//不符合刪除
					 i--;										
				 }
			 }
		}
	}*/
	//判斷該司機是否來的及接送這位預約者
	public int DistanceTimeFilter(List<RequestTable> ReqList)
	{
		boolean canbreak = false;	
		int OriginTime=0;
		int DestinationTime=0;
		RequestTable AssignSharingReq=null;
		for(int i = 0; i < ReqList.size(); i++)
		{	
				canbreak = false;				
				//回傳小於等於-2代表google查詢有錯立即終止程式
				if(Variable.errorcode<=-2)
				{
					break;
				}
				
				/*******************************處理共乘者*******************************/
				if(ReqList.get(i).AssignSharing!=-1)
				{
					 AssignSharingReq=IndexMap.get(ReqList.get(i).AssignSharing);
					 //查詢與當前預約者共乘跟上一班的旅行時間
					int [] traveltime1=Variable.DistanceTime(TableIndex,AssignSharingReq,smartSearch,Variable,IntervalSec);
					//記錄上一趟與共乘者旅行時間
					AssignSharingReq.StartDistanceValue=traveltime1[1];
					//查詢與當前預約者共乘跟下一班的旅行時間
					traveltime1=Variable.DistanceTime(AssignSharingReq,NextTableIndex,smartSearch,Variable,IntervalSec);
					//記錄下一趟與共乘者旅行時間
					AssignSharingReq.EndDistanceValue=traveltime1[1];
					
					//看共乘哪一個比較先上車
					if(ReqList.get(i).OriginTime<AssignSharingReq.OriginTime)
						OriginTime=ReqList.get(i).OriginTime;
					else
						OriginTime=AssignSharingReq.OriginTime;
					
					//看共乘哪一個比較晚下車
					if(ReqList.get(i).DestinationTime>AssignSharingReq.DestinationTime)						
						DestinationTime=ReqList.get(i).DestinationTime;
					else
						DestinationTime=AssignSharingReq.DestinationTime;	
				
					
				}else
				{
					OriginTime=ReqList.get(i).OriginTime;
					DestinationTime=ReqList.get(i).DestinationTime;
				}/******************************************************************/			
				//檢查上一班下車地點到目前預約者上車地點是否來得及
				if(ReqList.get(i).StartDistanceValue<0)			
				{
					int [] traveltime=new int[2];//第一格紀錄上一個req的Interval index  第二格旅行時間
					traveltime=Variable.DistanceTime(TableIndex,ReqList.get(i),smartSearch,Variable,IntervalSec);
					if(traveltime[1]<=-2)
					{
						//回傳小於等於-2代表google查詢有錯立即終止程式
						Variable.errorcode=traveltime[1];
						break;
					}
					ReqList.get(i).StartDistanceValue=traveltime[1];
				}
				
				
				/*******************************處理共乘者*******************************/
				if(ReqList.get(i).AssignSharing!=-1)
				{
					//如果有共乘就比對哪一個旅行時間比較長
					if(ReqList.get(i).StartDistanceValue<AssignSharingReq.StartDistanceValue)
					{
						ReqList.get(i).StartDistanceValue=AssignSharingReq.StartDistanceValue;
					}
				}
				/*******************************************************************/
	
				//這筆request的預約時間減去上一地點的時間小於google maps api取得的時間，刪去這個司機
				if((OriginTime-TableIndex.DestinationTime)<ReqList.get(i).StartDistanceValue || ReqList.get(i).StartDistanceValue < 0)
				{
					ReqList.remove(i);
					i--;
					canbreak=true;
				}
				
				//檢查目前預約者下車地點到下一班上車地點是否來得及
				if(canbreak)//如果來不及就不用檢查下車區域
				{
					continue;
				}
				
				//下車地點與之後的上車地點間的旅行時間
				if(ReqList.get(i).EndDistanceValue<0)
				{
					int [] traveltime=new int[2];//第一格紀錄上一個req的Interval index  第二格旅行時間
					traveltime=Variable.DistanceTime(ReqList.get(i),NextTableIndex,smartSearch,Variable,IntervalSec);
					if(traveltime[1]<=-2)
					{
						//回傳小於等於-2代表google查詢有錯立即終止程式
						Variable.errorcode=traveltime[1];
							break;
					}
					ReqList.get(i).EndDistanceValue=traveltime[1];
				}
				/*******************************處理共乘者*******************************/
				if(ReqList.get(i).AssignSharing!=-1)
				{
					//如果有共乘就比對哪一個旅行時間比較長
					if(ReqList.get(i).EndDistanceValue<AssignSharingReq.EndDistanceValue)
					{
						ReqList.get(i).EndDistanceValue=AssignSharingReq.EndDistanceValue;
					}
				}
				/*******************************************************************/
				//判斷是否來得及
				if((NextTableIndex.OriginTime - DestinationTime)< ReqList.get(i).EndDistanceValue || ReqList.get(i).EndDistanceValue < 0)
				{
					ReqList.remove(i);
					i--;	
				}
		}
		return  Variable.errorcode;
	}	
	//檢查是否有足夠休息時間
	public void restFilter(List<RequestTable> ReqList)
	{
		//工時小於6小時不找休息時間
		if((Driver.EndTime-Driver.StartTime)>Variable.nonrelax)
		{
			int startInterval = (int)((Driver.startreqtime+1800) / IntervalSec);//計算頭班+半小時所在區間
			//計算尾班-半小時所在區間		
			int endInterval = ((Driver.endreqtime+1800) /(int) IntervalSec);	
			int Spendtimecount=0;
			for(int i = 0; i < ReqList.size(); i++)
			{
				Spendtimecount=0;
				//回傳小於等於-2代表google查詢有錯立即終止程式
				if(Variable.errorcode<=-2)
				{
					break;
				}
				//先複製原本休息區間的array給temprelaxarry
				 ReqList.get(i).temprelaxarry=null;///清空
				 ReqList.get(i).temprelaxarry=new ArrayList<String>(Driver.relaxarry);
				//如果預約者時間區間落在司機可休息時間內就要檢查是否有足夠的休息時間	
				if(ReqList.get(i).temprelaxarry.size()>=2)
				{
					//用原本上下車時間去算區間
					int ReqStartInterval = (int)( (ReqList.get(i).originalStartTime) / IntervalSec);//上車時間在一天中的interval index
					//下車時間在一天中的interval index
					int ReqEndInterval = (ReqList.get(i).originalDestinationTime / (int) IntervalSec);
					//先刪除req所佔的區間
					for(int index = ReqStartInterval; index <= ReqEndInterval; index++)
					{
						//有落在休息區間就刪除
						if(index>=startInterval&&index<=endInterval)
							ReqList.get(i).temprelaxarry.remove(String.valueOf(index));
					}
					//先處理當下的預約者上車與上一趟下車之間
					//找出上一班有排班的區間旅行時間 第一格為上一趟所在的區間 第二格為旅行時間	
					int [] traveltime=Variable.DistanceTime(TableIndex,ReqList.get(i),smartSearch,Variable,IntervalSec);
					//回傳error code
					if(traveltime[1]<=-2)
					{
						Variable.errorcode=traveltime[1];
							break;
					}
					//紀錄上一個下車來接這趟旅行時間
					ReqList.get(i).StartDistanceValue=traveltime[1];
					//紀錄上一個預約者下車時間
					ReqList.get(i).PreviousrequstTime=TableIndex.DestinationTime;
					Spendtimecount=GetSpendTimeCount(traveltime[1],ReqList.get(i),TableIndex);
					//刪除開車所花費的格數				
					for(int count=traveltime[0]+1;count<(traveltime[0]+Spendtimecount+1);count++)
					{
						if(count>=startInterval&&count<=endInterval)
							ReqList.get(i).temprelaxarry.remove(String.valueOf(count));//刪除開車所花費的時間
					}
					//如果與上一趟占用格數計算完休息區間小於2格立即刪除
					if(ReqList.get(i).temprelaxarry.size()<2)
					{
						//如果可用的休息時間小於1小時就刪除
						ReqList.get(i).temprelaxarry=null;
						ReqList.remove(i);
						i--;
						continue;
					}
					//回傳小於等於-2代表google查詢有錯立即終止程式
					if(Variable.errorcode<=-2)
					{
						break;
					}
					
					if(NextTableIndex.originalStartTime>ReqList.get(i).originalStartTime)
					{
						//計算兩者之間旅行時間
						 int [] traveltime1=Variable.DistanceTime(ReqList.get(i),NextTableIndex,smartSearch,Variable,IntervalSec);
						 if(traveltime1[1]<=-2)
						 {
							Variable.errorcode=traveltime1[1];
							break;
						}	
						//紀錄這趟預約者下車去接下一趟旅行時間
						 ReqList.get(i).EndDistanceValue=traveltime1[1];
						//紀錄下一個預約者上車時間
						ReqList.get(i).NextrequstTime=NextTableIndex.OriginTime;
						//計算花費的格數
						Spendtimecount=GetSpendTimeCount(traveltime1[1],NextTableIndex,ReqList.get(i));
						//刪除開車所花費的格數				
						for(int count=traveltime1[0]+1;count<(traveltime1[0]+1+Spendtimecount);count++)
						{
							if(count>=startInterval&&count<=endInterval)
								ReqList.get(i).temprelaxarry.remove(String.valueOf(count));//刪除開車所花費的時間
						}
						   //如果與下一趟占用格數計算完休息區間小於2格立即刪除
						if(ReqList.get(i).temprelaxarry.size()<2)
						{
							//如果可用的休息時間小於1小時就刪除
							ReqList.get(i).temprelaxarry=null;
							ReqList.remove(i);
							i--;
						}
					}
					else
					{
						ReqList.get(i).temprelaxarry=null;
						ReqList.remove(i);
						i--;
					}
				}else
				{
					//如果可用的休息時間小於1小時就刪除
					ReqList.remove(i);
					i--;
				}
			}
		}
	}
	//過濾超過一個以上的預約者選最佳	
	public RequestTable MinFilter(List<RequestTable> ReqList) 
	{		
		RequestTable TargetReq=null;//選重的預約者	
		//司機的總數要大於0才能選
		if(FiltersEnable[4] == 1&&ReqList.size()>0)
		{
			int minValue = -1;//紀錄司機到預約者的最短距離
			int StartTime=-1;//紀錄上車時間
			for(int i = 0; i < ReqList.size(); i++)
			{
				//如果此車輛抵達此預約之上車地點所花費的時間較記錄的時間短
				if(minValue==-1||ReqList.get(i).StartDistanceValue< minValue)
				{
					//更新交通時間紀錄
					minValue = ReqList.get(i).StartDistanceValue;
					//更新選重預約者
					TargetReq=ReqList.get(i);
					//更新上車時間紀錄
					StartTime=ReqList.get(i).originalStartTime;
				}
				else if (ReqList.get(i).StartDistanceValue == minValue&&(ReqList.get(i).originalStartTime<StartTime))//如果同時間到達就採取誰先上車
				{
					//更新交通時間紀錄
					minValue = ReqList.get(i).StartDistanceValue;
					//更新選重預約者
					TargetReq=ReqList.get(i);
					//更新上車時間紀錄
					StartTime=ReqList.get(i).originalStartTime;
				}
			}
		}	
		return TargetReq;
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
	//計算趟跟趟之間需要花多少時間間格
	public int GetSpendTimeCount(int traveltime,RequestTable tagetreq,RequestTable previousone)
	{ 
		//計算加上容忍時間
		int temptraveltime=traveltime+Variable.TolerableTime;				
		//如果上一趟與與當前趟其中一個遇到尖峰時刻要加上delay time			
		if((tagetreq.originalStartTime>=Variable.areaPrioritystartmorningpeaktime&&tagetreq.originalStartTime<=Variable.areaPriorityendmorningpeaktime)
				||(previousone.originalDestinationTime>=Variable.areaPrioritystartmorningpeaktime&&previousone.originalDestinationTime<=Variable.areaPriorityendmorningpeaktime))
			temptraveltime+=Variable.morningpeaktime;                       //早上7:30~8:30尖峰時段再加上15分延遲
		if(tagetreq.originalStartTime>=Variable.areaPrioritystartafternoonpeaktime&&tagetreq.originalStartTime<=Variable.areaPriorityendafternoonpeaktime
				||(previousone.originalDestinationTime>=Variable.areaPrioritystartafternoonpeaktime&&previousone.originalDestinationTime<=Variable.areaPriorityendafternoonpeaktime))
			temptraveltime+=Variable.afternoonpeaktime;                      //下午16:45~18:30尖峰時段再加上20分延遲					
		//上一趟下車所剩下可扣的時間
		int tempSecond=(previousone.originalDestinationTime-(previousone.originalDestinationTime%60));	
		int min = (int)((tempSecond % 3600) / 60);//轉化成分 
		//扣掉可用殘餘的時間
		int Residualtime=0;
		if(min>=30&&min<60)
			Residualtime=(60-min);
		else if(min<30&&min>=0)
			Residualtime=(30-min); 
		temptraveltime=temptraveltime-(Residualtime*60);	
		//這趟上車所剩的時間
		tempSecond=(tagetreq.originalStartTime-(tagetreq.originalStartTime%60));
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
		return Spendtimecount;
	}		
	//判斷
	public boolean CheckAreaWeightsClass(int Classification,int[] Area)
	{ 
		boolean Conform=false;
		//檢查目前否符合要找尋的類別
		switch(Classification)
		{
			case 1:	  
				if(Area[0]==0&&Area[1]==0)
					Conform=true;
		  		break;
		  		
		  	case 2:
		  		if(Area[0]==0&&Area[1]<=1)
					Conform=true;
		  		else if(Area[0]<=1&&Area[1]==0)
					Conform=true;
		  		break;
		  		
		  	case 3:
		  		if(Area[0]==1&&Area[1]==1)
					Conform=true;
		  		break;
		  		
		  	case 4:
		  		if(Area[0]<=1&&Area[1]<=2)
					Conform=true;
		  		else if(Area[0]<=2&&Area[1]<=1)
					Conform=true;
		  		break;
		  		
		  	case 5:
		  		if(Area[0]==2&&Area[1]==2)
					Conform=true;
		  		break;			
			case 6:
		  		if(Area[0]<=3&&Area[1]<=2)
					Conform=true;
		  		else if(Area[0]<=2&&Area[1]<=3)
					Conform=true;
		  		break;
			case 7:
		  		if(Area[0]==3&&Area[1]==3)
					Conform=true;
		  		break;			
			 default: 
				 Conform=false;
		  }
		return Conform;	  
	 }
	public String[]  Getaddress(String[] tempaddress,int mode)
	{
		if(mode==0)
		{	
			tempaddress[0]=TableIndex.OriginAddress;
			tempaddress[1]=TableIndex.DestinationAddress;
		}
		else
		{
			tempaddress[2]=NextTableIndex.OriginAddress;
			tempaddress[3]=NextTableIndex.DestinationAddress;
		}
		return tempaddress;
	}
}

