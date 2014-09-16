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
	public int AssignSharingNumber;//�������w�@���ѧO�X	
	public int StartDistanceValue;//�����W�@�ӭ��ȤU���a�I���e�w���̤W���a�I���Ȧ�ɶ�
	public int EndDistanceValue;//������e���ȤU���a�I��U�@�ӹw���̤W���a�I���Ȧ�ɶ�
	public int PreviousrequstTime;
	public int  NextrequstTime;
	public int []index={-1,-1,-1};//����3�harray��index
	public double OriginLat;
	public double OriginLon;
	public double DestinationLat;
	public double DestinationLon;	
	public boolean Share;
	public boolean Arrange;
	public boolean Arrangedflag;	
	public boolean[] xindianRoadSplitArea={false,false} ;
	public List<String> temprelaxarry;//�����w�B�z�L�ѤU�i�Կ�𮧪��϶��A���n�̫�minfilter�����~�|�g�^�q��

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
	//���w����Ư�����ۤ���j�p
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
	//�^�ǤT�a�U���I�b�x�_���ݨD��
	  public static List<RequestTable> getSpecialareatotaiperarray(int Specialareareqindex,List<reqGroup> requestTable,defineVariable Variable)
		{
			  List<RequestTable> Specialareatotaiperarray=new ArrayList<RequestTable>(300);		  
			  for(int eara=0;eara<Variable.Specialarea[Specialareareqindex].length;eara++)
			  {
				  for(int timeindex=0;timeindex<40;timeindex++)
					 {
					  //�j�M�H�W���ϰ������req
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
	//�^�ǯS��ϰ쪺�ݨD�� �F�_���ϰ�ΤT�a��骺�ݨD��
	  public static List<RequestTable> getSpecialareaarray(int Specialareareqindex,List<reqGroup> requestTable,List<reqGroup> tailrequestTable,Map<Integer, RequestTable> IndexMap,defineVariable Variable)
		{
			  List<RequestTable> Specialareaarray=new ArrayList<RequestTable>(300);		
			  for(int eara=0;eara<Variable.Specialarea[Specialareareqindex].length;eara++)
			  {
				  for(int timeindex=0;timeindex<Variable.intervalnum;timeindex++)
				  {
					  //�j�M�H�W���ϰ������req
					  for(int k=0;k<requestTable.get(Variable.Specialarea[Specialareareqindex][eara]).getreq(timeindex).size();k++)
					  {
						  if(!requestTable.get(Variable.Specialarea[Specialareareqindex][eara]).getreq(timeindex).get(k).Arrange&&
								  requestTable.get(Variable.Specialarea[Specialareareqindex][eara]).getreq(timeindex).get(k).Status==1)
						  {
							  Specialareaarray.add(requestTable.get(Variable.Specialarea[Specialareareqindex][eara]).getreq(timeindex).get(k));
						  }   
					  }
					  //�j�M�H�U���ϰ������req
					  for(int k=0;k<tailrequestTable.get(Variable.Specialarea[Specialareareqindex][eara]).getreq(timeindex).size();k++)
					  {
						  if(!tailrequestTable.get(Variable.Specialarea[Specialareareqindex][eara]).getreq(timeindex).get(k).Arrange&&
								  tailrequestTable.get(Variable.Specialarea[Specialareareqindex][eara]).getreq(timeindex).get(k).Status==1)
						  {
							  //�H�W��������Ƭ��D�H�K��ƿ���
							  Specialareaarray.add(IndexMap.get(tailrequestTable.get(Variable.Specialarea[Specialareareqindex][eara]).getreq(timeindex).get(k).Number));
						  }   
					  }
				   }
			  }
			  return Specialareaarray;
		}
	
	//�^�ǤU��4�I��ߤW8�I �U���a�I���x�_
	  public static List<RequestTable> getnighttotaipeiarray(List<reqGroup> requestTable,defineVariable Variable)
		{
			  List<RequestTable> nighttotaipeiarray=new ArrayList<RequestTable>(300);	
				 for(int areaindex=0;areaindex<Variable.areanum;areaindex++)
				 { 				
					 for(int timeindex=0;timeindex<Variable.intervalnum;timeindex++)
					 { 
						 for(int index=0;index<requestTable.get(areaindex).getreq(timeindex).size();index++)
						 {
							  //�U�Z�ɬq�U���a�I���x�_							
							  if((requestTable.get(areaindex).getreq(timeindex).get(index).OriginTime>=57600
								&&requestTable.get(areaindex).getreq(timeindex).get(index).OriginTime<=72000)
							    ||(requestTable.get(areaindex).getreq(timeindex).get(index).DestinationTime>=57600
							    &&requestTable.get(areaindex).getreq(timeindex).get(index).DestinationTime<=72000))
								  {
								    if(requestTable.get(areaindex).getreq(timeindex).get(index).Destinationarea.indexOf("�x�_��")!=-1&&
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
	//�^�ǤW���ɶ��b�ߤW��req 
	  public static List<RequestTable> getnightreqarray(List<reqGroup> requestTable,defineVariable Variable)
		{
			  List<RequestTable> nightreq=new ArrayList<RequestTable>(300);		  
			  for(int area=0;area<Variable.areanum;area++)
			  {
				  for(int timeindex=0;timeindex<40;timeindex++)
					 {
					  for(int k=0;k<requestTable.get(area).getreq(timeindex).size();k++)
						 {
						  //���ƯZ
						  if(!requestTable.get(area).getreq(timeindex).get(k).Arrange&&requestTable.get(area).getreq(timeindex).get(k).Status==1)
							  {
							  //����ߤW���I�L�Ҧ�req
							  if(requestTable.get(area).getreq(timeindex).get(k).OriginTime>=64800)
								  nightreq.add(requestTable.get(area).getreq(timeindex).get(k));
							  }
						  }				 
					 }
			  }
			  return nightreq;
		}
	//�^�ǶW���Ȧ�ɶ�
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
	 //�^�Ǧ���array
	 public static List<RequestTable> getpairarray(List<RequestTable> requestTable)
	 {
				  List<RequestTable> pairarray=new ArrayList<RequestTable>(300);	
				  List<RequestTable> Arrangearray=new ArrayList<RequestTable>(300);
				 
				  //���X�H�ƹL�Z��req
				  for(int j=0;j<requestTable.size();j++)
				  {
					  if(requestTable.get(j).Arrange)
						  {
						  Arrangearray.add(requestTable.get(j));
						  requestTable.remove(j);
						  j--;
						  }
				  }
				  //�ˬd�w�ƹL�Z�̬O�_������
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
				  //�ˬd�w�ƹL�Z�P���ƯZ���O�_������
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
				//�ˬd���ƯZ�̪��O�_������
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
	//�^�Ǥ�����array
	 public static List<RequestTable> getnonpairarray(List<RequestTable> requestTable)
	 {
		 List<RequestTable> nonpairarray=new ArrayList<RequestTable>(300);	
		 for(int i=0;i<requestTable.size();i++)
		 {
			  nonpairarray.add(requestTable.get(i));
		 }
			 return nonpairarray;
	 }
	//�^�Ǥ�����array
	public static Map<Integer, RequestTable> getindexmap(Map<Integer, RequestTable> IndexMap,List<reqGroup> requestTable,defineVariable Variable )
	{  
		 //�N�Ҧ��w���̩�Jmap
		 for(int areaindex=0;areaindex<Variable.areanum;areaindex++)
		 { 				
			 for(int timeindex=0;timeindex<Variable.intervalnum;timeindex++)
			 { 
				 for(int index=0;index<requestTable.get(areaindex).getreq(timeindex).size();index++)
				 {
					 
					 requestTable.get(areaindex).getreq(timeindex).get(index).originalStartTime=requestTable.get(areaindex).getreq(timeindex).get(index).OriginTime;//�x�s�쥻���W���ɶ�
					 requestTable.get(areaindex).getreq(timeindex).get(index).originalDestinationTime=requestTable.get(areaindex).getreq(timeindex).get(index).DestinationTime;//�x�s�쥻���U�ɶ�
					 //�p�G�S���@�����O10�����e��
					 if(requestTable.get(areaindex).getreq(timeindex).get(index).AssignSharing==-1)
					 {
						 requestTable.get(areaindex).getreq(timeindex).get(index).DestinationTime+=Variable.TolerableTime;//�W���ɶ����e�e��10����
						 requestTable.get(areaindex).getreq(timeindex).get(index).OriginTime-=Variable.TolerableTime;//�U���ɶ�����e��10����	
					 }
					 else
					 {
						 //���@�����ndouble
						 requestTable.get(areaindex).getreq(timeindex).get(index).DestinationTime+=Variable.TolerableTime*1.5;//�W���ɶ����e�e��10����
						 requestTable.get(areaindex).getreq(timeindex).get(index).OriginTime-=Variable.TolerableTime*1.5;//�U���ɶ�����e��10����	
					 }
					 //���W7:30~8:30�y�p�ɬq�A�[�W15������
					 if(requestTable.get(areaindex).getreq(timeindex).get(index).originalStartTime>=Variable.areaPrioritystartmorningpeaktime&& requestTable.get(areaindex).getreq(timeindex).get(index).originalStartTime<=Variable.areaPriorityendmorningpeaktime)
						 requestTable.get(areaindex).getreq(timeindex).get(index).OriginTime-=Variable.morningpeaktime;                      
					 //�U��16:45~18:30�y�p�ɬq�A�[�W20������						
					 if(requestTable.get(areaindex).getreq(timeindex).get(index).originalStartTime>=Variable.areaPrioritystartafternoonpeaktime&& requestTable.get(areaindex).getreq(timeindex).get(index).originalStartTime<=Variable.areaPriorityendafternoonpeaktime)
						 requestTable.get(areaindex).getreq(timeindex).get(index).OriginTime-=Variable.afternoonpeaktime;                       
					 //�����j���m
					 requestTable.get(areaindex).getreq(timeindex).get(index).index[0]=areaindex;
					 requestTable.get(areaindex).getreq(timeindex).get(index).index[1]=timeindex;
					 requestTable.get(areaindex).getreq(timeindex).get(index).index[2]=index;
					 //��Jmap
					 IndexMap.put(requestTable.get(areaindex).getreq(timeindex).get(index).Number,requestTable.get(areaindex).getreq(timeindex).get(index));	
				}
			}
		}
		return IndexMap;
	}
	//�N���B�z�����аO�����ƤJ
	public static void modifyReqstatus(	List<RequestTable> Reqlist,defineVariable Variable )
	{
		if(Reqlist.size()>0)
		{
			for(int i = 0; i < Reqlist.size();i++)
			{	
				try 
				{
					if(!Reqlist.get(i).Arrange)
					Variable.smt2.executeUpdate("UPDATE userrequests SET arranged = 0 WHERE �ѧO�X = '" + Reqlist.get(i).Number + "' AND arrangedate = '" + Variable.date + "' AND arrangetime = '" + Variable.time + "'");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
		 
}
