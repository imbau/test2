import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


public class RequestTable implements Cloneable, Comparable<Object>,Comparator<Object> 
{
	public String RequestNumber;
	public String OriginAddress;
	public String DestinationAddress;
	public String Car;
	public String Originarea;
	public String Destinationarea;
	public String Targetdrivers;
	public int OriginTime;
	public int DestinationTime;
	public int TravelTime;	
	public int RequestTableIndaex;
	public int Status;
	public int Number;
	public int TargetIndex;		
	public int mapindex;
	public int originalStartTime;
	public int originalDestinationTime;
	public int AssignSharing;
	public int AssignSharingNumber;//紀錄指定共乘識別碼	
	public int StartDistanceValue;//紀錄上一個乘客下車地點到當前預約者上車地點的旅行時間
	public int EndDistanceValue;//紀錄當前乘客下車地點到下一個預約者上車地點的旅行時間
	public int PreviousrequstTime;
	public int  NextrequstTime;
	public int []index={-1,-1,-1};//紀錄3層array的index
	public double OriginLat;
	public double OriginLon;
	public double DestinationLat;
	public double DestinationLon;	
	public boolean Share;
	public boolean Arrange;
	public boolean Arrangedflag;	
	public boolean[] xindianRoadSplitArea={false,false} ;
	public List<String> temprelaxarry;//紀錄已處理過剩下可候選休息的區間，但要最後minfilter選取到才會寫回司機

	public RequestTable()
	{
		RequestTableIndaex=-1;
		RequestNumber = null;
		OriginTime = -1;
		DestinationTime = -1;
		originalStartTime=-1;
		originalDestinationTime=-1;
		TravelTime = -1;		
		OriginLat = -1.0;
		OriginLon = -1.0;
		OriginAddress = null;		
		DestinationLat = -1.0;
		DestinationLon = -1.0;
		DestinationAddress = null;
		Share = false;
		Arrange = false;
		Status = -1;
		Car = null;
		Number = -1;
		Originarea=null;
		Destinationarea=null;
		TargetIndex=0;
		Arrangedflag=true;
		Targetdrivers="null";		
		AssignSharing=-1;
		AssignSharingNumber=-1;
		StartDistanceValue=-1;
		EndDistanceValue=-1;
		PreviousrequstTime=-1;
		NextrequstTime=-1;
		temprelaxarry=new ArrayList<String>(12);		
	}
	//讓預約資料能夠互相比較大小
	public int compareTo(Object o1)
	{
		if(this.TravelTime > ((RequestTable)o1).TravelTime)
		{
			return 1;
		}
		else
		{
			return -1;
		}
	}
	 public int compare(Object obj1, Object obj2){
		 RequestTable o1=(RequestTable) obj1;
		 RequestTable o2=(RequestTable) obj2;

	        if(o1.OriginTime>o2.OriginTime){
	            return 1;
	        }

	        if(o1.OriginTime<o2.OriginTime){
	                return -1;
	        }
	        return 0;
	    }
	
	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch(CloneNotSupportedException e)
		{
			System.out.println(e);
			return null;
		}
	}
	
	public void printTable()
	{
		System.out.print("Request Number = " + RequestNumber + "\n");
		System.out.print("Origin Time = " + OriginTime + "\n");
		System.out.print("Destination Time = " + DestinationTime + "\n");
		System.out.print("Travel Time = " + TravelTime + "\n");
		System.out.print("OriginLat = " + OriginLat + "\n");
		System.out.print("OriginLon = " + OriginLon + "\n");
		System.out.print("DestinationLat = " + DestinationLat + "\n");
		System.out.print("DestinationLon = " + DestinationLon + "\n");
		System.out.print("Share car? " + Share + "\n");
	}
	public void SetValue(RequestTable input)
	{
		this.OriginTime = input.OriginTime;
		this.DestinationTime = input.DestinationTime;
		this.TravelTime = input.TravelTime;		
		this.OriginLat = input.OriginLat;
		this.OriginLon = input.OriginLon;
		this.DestinationLat = input.DestinationLat;
		this.DestinationLon = input.DestinationLon;
		this.Originarea=input.Originarea;
		this.Destinationarea=input.Destinationarea;
		
	}	
	//回傳三鶯下車點在台北的需求者
	  public static List<RequestTable> getSpecialareatotaiperarray(int Specialareareqindex,List<reqGroup> requestTable,defineVariable Variable)
		{
			  List<RequestTable> Specialareatotaiperarray=new ArrayList<RequestTable>(300);		  
			  for(int eara=0;eara<Variable.Specialarea[Specialareareqindex].length;eara++)
			  {
				  for(int timeindex=0;timeindex<40;timeindex++)
					 {
					  //搜尋以上車區域分類的req
					  for(int k=0;k<requestTable.get(Variable.Specialarea[Specialareareqindex][eara]).getreq(timeindex).size();k++)
						 {
						  if(!requestTable.get(Variable.Specialarea[Specialareareqindex][eara]).getreq(timeindex).get(k).Arrange&&
								  requestTable.get(Variable.Specialarea[Specialareareqindex][eara]).getreq(timeindex).get(k).Status==1)
						  {
							  if(defineVariable.switchareaindex(requestTable.get(Variable.Specialarea[Specialareareqindex][eara]).getreq(timeindex).get(k).Destinationarea)==17)
							  {
								  Specialareatotaiperarray.add(requestTable.get(Variable.Specialarea[Specialareareqindex][eara]).getreq(timeindex).get(k));
							  }
						  }
						 }
					 }
			  }
			  return Specialareatotaiperarray;
		}
	//回傳特殊區域的需求者 東北角區域或三鶯桃園的需求者
	  public static List<RequestTable> getSpecialareaarray(int Specialareareqindex,List<reqGroup> requestTable,List<reqGroup> tailrequestTable,Map<Integer, RequestTable> IndexMap,defineVariable Variable)
		{
			  List<RequestTable> Specialareaarray=new ArrayList<RequestTable>(300);		
			  for(int eara=0;eara<Variable.Specialarea[Specialareareqindex].length;eara++)
			  {
				  for(int timeindex=0;timeindex<Variable.intervalnum;timeindex++)
				  {
					  //搜尋以上車區域分類的req
					  for(int k=0;k<requestTable.get(Variable.Specialarea[Specialareareqindex][eara]).getreq(timeindex).size();k++)
					  {
						  if(!requestTable.get(Variable.Specialarea[Specialareareqindex][eara]).getreq(timeindex).get(k).Arrange&&
								  requestTable.get(Variable.Specialarea[Specialareareqindex][eara]).getreq(timeindex).get(k).Status==1)
						  {
							  Specialareaarray.add(requestTable.get(Variable.Specialarea[Specialareareqindex][eara]).getreq(timeindex).get(k));
						  }   
					  }
					  //搜尋以下車區域分類的req
					  for(int k=0;k<tailrequestTable.get(Variable.Specialarea[Specialareareqindex][eara]).getreq(timeindex).size();k++)
					  {
						  if(!tailrequestTable.get(Variable.Specialarea[Specialareareqindex][eara]).getreq(timeindex).get(k).Arrange&&
								  tailrequestTable.get(Variable.Specialarea[Specialareareqindex][eara]).getreq(timeindex).get(k).Status==1)
						  {
							  //以上車分類資料為主以免資料錯亂
							  Specialareaarray.add(IndexMap.get(tailrequestTable.get(Variable.Specialarea[Specialareareqindex][eara]).getreq(timeindex).get(k).Number));
						  }   
					  }
				   }
			  }
			  return Specialareaarray;
		}
	
	//回傳下午4點到晚上8點 下車地點往台北
	  public static List<RequestTable> getnighttotaipeiarray(List<reqGroup> requestTable,defineVariable Variable)
		{
			  List<RequestTable> nighttotaipeiarray=new ArrayList<RequestTable>(300);	
				 for(int areaindex=0;areaindex<Variable.areanum;areaindex++)
				 { 				
					 for(int timeindex=0;timeindex<Variable.intervalnum;timeindex++)
					 { 
						 for(int index=0;index<requestTable.get(areaindex).getreq(timeindex).size();index++)
						 {
							  //下班時段下車地點往台北							
							  if((requestTable.get(areaindex).getreq(timeindex).get(index).OriginTime>=57600
								&&requestTable.get(areaindex).getreq(timeindex).get(index).OriginTime<=72000)
							    ||(requestTable.get(areaindex).getreq(timeindex).get(index).DestinationTime>=57600
							    &&requestTable.get(areaindex).getreq(timeindex).get(index).DestinationTime<=72000))
								  {
								    if(requestTable.get(areaindex).getreq(timeindex).get(index).Destinationarea.indexOf("台北市")!=-1&&
								    		!requestTable.get(areaindex).getreq(timeindex).get(index).Arrange)
								    {
								    	 nighttotaipeiarray.add(requestTable.get(areaindex).getreq(timeindex).get(index));
								    }
								  }
						 }
					 }
				}
			  return nighttotaipeiarray;
		}
	//回傳上車時間在晚上的req 
	  public static List<RequestTable> getnightreqarray(List<reqGroup> requestTable,defineVariable Variable)
		{
			  List<RequestTable> nightreq=new ArrayList<RequestTable>(300);		  
			  for(int area=0;area<Variable.areanum;area++)
			  {
				  for(int timeindex=0;timeindex<40;timeindex++)
					 {
					  for(int k=0;k<requestTable.get(area).getreq(timeindex).size();k++)
						 {
						  //未排班
						  if(!requestTable.get(area).getreq(timeindex).get(k).Arrange&&requestTable.get(area).getreq(timeindex).get(k).Status==1)
							  {
							  //抓取晚上六點過所有req
							  if(requestTable.get(area).getreq(timeindex).get(k).OriginTime>=64800)
								  nightreq.add(requestTable.get(area).getreq(timeindex).get(k));
							  }
						  }				 
					 }
			  }
			  return nightreq;
		}
	//回傳超長旅行時間
	  public static List<RequestTable> getlongtimearray(List<reqGroup> requestTable,defineVariable Variable)
		{
			  List<RequestTable> longtimearray=new ArrayList<RequestTable>(300);		  
			  for(int j=0;j<Variable.longtime[0].length;j++)
			  {
				  for(int l=0;l<40;l++)
					 {
					  for(int k=0;k<requestTable.get(Variable.longtime[0][j]).getreq(l).size();k++)
						 {
						     if(requestTable.get(Variable.longtime[0][j]).getreq(l).get(k).TravelTime>=2000&&requestTable.get(Variable.longtime[0][j]).getreq(l).get(k).Status==1)
						    	 longtimearray.add(requestTable.get(Variable.longtime[0][j]).getreq(l).get(k));
						 }				 
					 }
			  }
			  return longtimearray;
		}
	 //回傳成對array
	 public static List<RequestTable> getpairarray(List<RequestTable> requestTable)
	 {
				  List<RequestTable> pairarray=new ArrayList<RequestTable>(300);	
				  List<RequestTable> Arrangearray=new ArrayList<RequestTable>(300);
				 
				  //取出以排過班的req
				  for(int j=0;j<requestTable.size();j++)
				  {
					  if(requestTable.get(j).Arrange)
						  {
						  Arrangearray.add(requestTable.get(j));
						  requestTable.remove(j);
						  j--;
						  }
				  }
				  //檢查已排過班裡是否有成對
				  for(int i=0;i<Arrangearray.size();i++)
				  {
					  for(int j=0;j<Arrangearray.size();j++)
					  {
						  if(Arrangearray.get(i).RequestNumber.indexOf(Arrangearray.get(j).RequestNumber)!=-1)
						  { 
							  if(Arrangearray.get(i).OriginAddress.indexOf(Arrangearray.get(j).DestinationAddress)!=-1
										&&Arrangearray.get(i).DestinationAddress.indexOf(Arrangearray.get(j).OriginAddress)!=-1)
							  {
								  if(Arrangearray.get(i).OriginTime<Arrangearray.get(j).OriginTime)
								  {
									  pairarray.add(Arrangearray.get(i));
									  pairarray.add(Arrangearray.get(j));
									  Arrangearray.remove(i);
									  if(j!=0)
										  Arrangearray.remove(j-1);
									  else if(j==0)
										  Arrangearray.remove(0);
									  
									  if(i==0)
									  {
										  i=0;
									  }else
									  { 
										  i--;
									   }
									  break;
								  }
								  else
								  {
									  pairarray.add(Arrangearray.get(j));
									  pairarray.add(Arrangearray.get(i)); 
									  Arrangearray.remove(i);
									  if(j!=0)
										  Arrangearray.remove(j-1);
									  else if(j==0)
										  Arrangearray.remove(0);
									  if(i==0)
									  {
										  i=0;
									  }else
									  { 
										  i--;
									   }
									  break;
								  }
								  
							  }
						  }
						  
					  } 
				  }
				  //檢查已排過班與未排班的是否有成對
				  for(int i=0;i<Arrangearray.size();i++)
				  {
					  for(int j=0;j<requestTable.size();j++)
					  {
						  if(Arrangearray.get(i).RequestNumber.indexOf(requestTable.get(j).RequestNumber)!=-1)
						  { 
							  if(Arrangearray.get(i).OriginAddress.indexOf(requestTable.get(j).DestinationAddress)!=-1
										&&Arrangearray.get(i).DestinationAddress.indexOf(requestTable.get(j).OriginAddress)!=-1)
							  {
								  if(Arrangearray.get(i).OriginTime<requestTable.get(j).OriginTime)
								  {
									  pairarray.add(Arrangearray.get(i));
									  pairarray.add(requestTable.get(j));
									  Arrangearray.remove(i);
									  requestTable.remove(j);
									  if(i==0)
									  {
										  i=0;
									  }else
									  { 
										  i--;
									   }
									  break;
								  }
								  else
								  {
									  pairarray.add(requestTable.get(j));
									  pairarray.add(Arrangearray.get(i)); 
									  Arrangearray.remove(i);
									  requestTable.remove(j);
									  if(i==0)
									  {
										  i=0;
									  }else
									  { 
										  i--;
									   }
									  break;
								  }
								  
							  }
						  }
						  
					  } 
				  }
				//檢查未排班裡的是否有成對
				  for(int i=0;i<requestTable.size();i++)
				  {
					  for(int j=0;j<requestTable.size();j++)
					  {
						  if(requestTable.get(i).RequestNumber.indexOf(requestTable.get(j).RequestNumber)!=-1)
						  { 
							  if(requestTable.get(i).OriginAddress.indexOf(requestTable.get(j).DestinationAddress)!=-1
										&&requestTable.get(i).DestinationAddress.indexOf(requestTable.get(j).OriginAddress)!=-1)
							  {
								  if(requestTable.get(i).OriginTime<requestTable.get(j).OriginTime)
								  {
									  pairarray.add(requestTable.get(i));
									  pairarray.add(requestTable.get(j));
									  requestTable.remove(i);
									  if(j!=0)
										  requestTable.remove(j-1);
									  else if(j==0)
										  requestTable.remove(0);
									  if(i==0)
									  {
										  i=0;
									  }else
									  { 
										  i--;
									   }
									  break;
								  }
								  else
								  {
									  pairarray.add(requestTable.get(j));
									  pairarray.add(requestTable.get(i)); 
									  requestTable.remove(i);
									  if(j!=0)
										  requestTable.remove(j-1);
									  else if(j==0)
										  requestTable.remove(0);
									  
									  if(i==0)
									  {
										  i=0;
									  }else
									  { 
										  i--;
									   }
									  break;
								  }
								  
							  }
						  }
						  
					  } 
				  }
				  
				  return pairarray;
			}
	//回傳不成對array
	 public static List<RequestTable> getnonpairarray(List<RequestTable> requestTable)
	 {
		 List<RequestTable> nonpairarray=new ArrayList<RequestTable>(300);	
		 for(int i=0;i<requestTable.size();i++)
		 {
			  nonpairarray.add(requestTable.get(i));
		 }
			 return nonpairarray;
	 }
	//回傳不成對array
	public static Map<Integer, RequestTable> getindexmap(Map<Integer, RequestTable> IndexMap,List<reqGroup> requestTable,defineVariable Variable )
	{  
		 //將所有預約者放入map
		 for(int areaindex=0;areaindex<Variable.areanum;areaindex++)
		 { 				
			 for(int timeindex=0;timeindex<Variable.intervalnum;timeindex++)
			 { 
				 for(int index=0;index<requestTable.get(areaindex).getreq(timeindex).size();index++)
				 {
					 
					 requestTable.get(areaindex).getreq(timeindex).get(index).originalStartTime=requestTable.get(areaindex).getreq(timeindex).get(index).OriginTime;//儲存原本的上車時間
					 requestTable.get(areaindex).getreq(timeindex).get(index).originalDestinationTime=requestTable.get(areaindex).getreq(timeindex).get(index).DestinationTime;//儲存原本的下時間
					 //如果沒有共乘的是10分鐘容忍
					 if(requestTable.get(areaindex).getreq(timeindex).get(index).AssignSharing==-1)
					 {
						 requestTable.get(areaindex).getreq(timeindex).get(index).DestinationTime+=Variable.TolerableTime;//上車時間提前容忍10分鐘
						 requestTable.get(areaindex).getreq(timeindex).get(index).OriginTime-=Variable.TolerableTime;//下車時間延後容忍10分鐘	
					 }
					 else
					 {
						 //有共乘的要double
						 requestTable.get(areaindex).getreq(timeindex).get(index).DestinationTime+=Variable.TolerableTime*1.5;//上車時間提前容忍10分鐘
						 requestTable.get(areaindex).getreq(timeindex).get(index).OriginTime-=Variable.TolerableTime*1.5;//下車時間延後容忍10分鐘	
					 }
					 //早上7:30~8:30尖峰時段再加上15分延遲
					 if(requestTable.get(areaindex).getreq(timeindex).get(index).originalStartTime>=Variable.areaPrioritystartmorningpeaktime&& requestTable.get(areaindex).getreq(timeindex).get(index).originalStartTime<=Variable.areaPriorityendmorningpeaktime)
						 requestTable.get(areaindex).getreq(timeindex).get(index).OriginTime-=Variable.morningpeaktime;                      
					 //下午16:45~18:30尖峰時段再加上20分延遲						
					 if(requestTable.get(areaindex).getreq(timeindex).get(index).originalStartTime>=Variable.areaPrioritystartafternoonpeaktime&& requestTable.get(areaindex).getreq(timeindex).get(index).originalStartTime<=Variable.areaPriorityendafternoonpeaktime)
						 requestTable.get(areaindex).getreq(timeindex).get(index).OriginTime-=Variable.afternoonpeaktime;                       
					 //紀錄迴圈位置
					 requestTable.get(areaindex).getreq(timeindex).get(index).index[0]=areaindex;
					 requestTable.get(areaindex).getreq(timeindex).get(index).index[1]=timeindex;
					 requestTable.get(areaindex).getreq(timeindex).get(index).index[2]=index;
					 //放入map
					 IndexMap.put(requestTable.get(areaindex).getreq(timeindex).get(index).Number,requestTable.get(areaindex).getreq(timeindex).get(index));	
				}
			}
		}
		return IndexMap;
	}
	//將未處理完的標記成未排入
	public static void modifyReqstatus(	List<RequestTable> Reqlist,defineVariable Variable )
	{
		if(Reqlist.size()>0)
		{
			for(int i = 0; i < Reqlist.size();i++)
			{	
				try 
				{
					if(!Reqlist.get(i).Arrange)
					Variable.smt2.executeUpdate("UPDATE userrequests SET arranged = 0 WHERE 識別碼 = '" + Reqlist.get(i).Number + "' AND arrangedate = '" + Variable.date + "' AND arrangetime = '" + Variable.time + "'");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
		 
}
