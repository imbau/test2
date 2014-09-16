//�ƯZfilter�����A�i�H�ھڻݨD�⴫module
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
		Variable =variable;//�w�q�ܼ�
		FiltersEnable = enable;	
		Interval = interval;//�@��interval���ɶ����u(�p��)										
		Variable.TolerableTime = tolerabletime;//���ȤW�U���H�Ψ�����p�ɩҳy����delay�A�N���ɶ�����[�J�p��Ҽ{
		IntervalSec = Interval * 3600;//�Ninterval���ɶ��Ѥp�ɬ�����ন�Ѭ����
		//�έ쥻�W�U���ɶ��h��϶�
		StartInterval = (int)( (reqtable.originalStartTime) / IntervalSec);//�W���ɶ��b�@�Ѥ���interval index
		//�U���ɶ��b�@�Ѥ���interval index
		EndInterval = (reqtable.originalDestinationTime /(int) IntervalSec);
		Reqtable=reqtable;//��e���w����
		IndexMap = indexmap;//�s��Ҧ��w����queue		
		Variable.input = new double[4];//�s��W�U���a�}���g�n�׭ȡA��¥Ψӷ�@�ǤJfunction�Ϊ��ѼơCinput[0]���W���a�}���n�סAinput[1]���W���a�}���g�סAinput[2]�U���a�}���n�סAinput[3]���U���a�}���g�סC
	    Variable.address = new String[2];//�s��W�U���a�I���a�}�A��¥Ψӷ�@�ǤJfunction�Ϊ��ѼơCaddress[0]���W���a�}�Aaddress[1]���U���a�}�C
		//canShare = -1;//�b�P�w�@���ɩҨϥΪ��ѼơA�p�G����>-1�N��o���w���N�M�Y��w���X�֬��@���Z���C
		smartSearch = inputilf;//��M��a�I����q�ɶ�������	
		
	}
	//�W�Z�U�Z�ɶ��@�p�ɤ���2��H�W
	public  void NoMoreThanTwoFilter(List<DriverTable> DriverList)
	{
		 //���W7:30~8:30�y�p�ɬq ��//�U��16:45~18:30�y�p�ɬq	
		 if((Reqtable.originalStartTime>=Variable.areaPrioritystartmorningpeaktime&& Reqtable.originalStartTime<=Variable.areaPriorityendmorningpeaktime)
				 ||(Reqtable.originalStartTime>=Variable.areaPrioritystartafternoonpeaktime&&Reqtable.originalStartTime<=Variable.areaPriorityendafternoonpeaktime))
		{
			 //�p�G���b�W�U�Z�ɶ��w���̴N�ˬd�O�_�P�W�@�Z�ΤU�@�Z���j1�p��
			 for(int i = 0; i < DriverList.size(); )
			 {
				 //�M��W�@��
				 RequestTable TableIndex=Variable.PreRequestTableQuery(DriverList.get(i),StartInterval,Variable,IndexMap);
				 //�M��U�@��
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
	//�S���������W�L5��
	public void SpecialCarFilter(List<DriverTable> DriverList)
	{
		for(int index=0;index<Variable.SpecialCar.size();index++)
		{
			for(int i = 0; i < DriverList.size(); )
			{
				//�S����
				if(Variable.SpecialCar.get(index).indexOf(DriverList.get(i).ID)!=-1)
				{
					//�j��5��N�R��
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
/*	//�����t�� �ثe���h�M��W�@��ΤU�@��O�_���ۦP��
	public void RoadToRoadFilter(List<DriverTable> DriverList)
	{
		String[] tempaddress;	
		String[] check=new String[]{"��","��","�D"};
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
				//�M��W�@��P�U�@�몺�a�}.
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
	
	//����filter�A��ª��L�o�����ŦX�w���ҫ��w������
	public void CarFilter(List<DriverTable> DriverList)
	{
		//����filter������Aflag == 1 �H�� �Τᦳ���w����
		if(FiltersEnable[0] == 1 &&!(Reqtable.Car.equals("")))
		{ 	//System.out.println(DriverList.size());
				for(int i = 0; i < DriverList.size(); )
				{
					//���ؤ��Ū��L�o��
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
	//�s�ӥq�������@������
	public void AssignSharingCarFilter(List<DriverTable> DriverList)
	{
		//���@�������Ȥ~����
		if(Reqtable.AssignSharing!=-1)
		{ 	
			for(int index=0;index<Variable.TiroDriver.size();index++)
			{
				for(int i = 0; i < DriverList.size(); )
					{
					//�L�o���s�Ӫ��q��
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
	//���Afilter�ATimeInterval���е������������ƯZ�A�Ϊ̥u�ƪ��@�ӹw���̥B�L�@�N�@��
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
				
				//�W���ɬq��U���ɬq���q�����ƯZ�������Ū��ATimeInterval[index]���s�J�����A�����W�Z�A�����o�x���M���˴��U�x��
				if(DriverList.get(i).TimeInterval[j].indexOf("���W�Z")!=-1)
				{
					DriverList.remove(i);
					i--;
					break;
				}				
				//TimeInterval[index]�w�ƤJ��{
				else if(!(DriverList.get(i).TimeInterval[j].indexOf("���ƯZ")!=-1))
				{
					DriverList.remove(i);
					i--;
					break;
					//�������Q��O�H�@��
					/*-if(!Reqtable.Share)
					{
						DriverList.remove(i);
						i--;
						break;
					}
					//�i�H�����@��
					else
					{
						//�P�_TimeInterval�ƤJ����{�O�_�w�g�O�@�����A
						String[] temp = DriverList.get(i).TimeInterval[j].split("_");
						//�w�g��ӤH�ƤJ�o�Ӯɬq���A�����o����
						if(temp.length > 1)
						{
							DriverList.remove(i);
							i--;
							break;
						}
						//�u�ƤJ�@�ӤH
						else if(temp.length == 1)
						{
							//�ƤJ���H���Q�@���A�����o����
							RequestTable getIndex = IndexMap.get(Integer.valueOf(temp[0]));
							if(!Reqtable.Share)
							{
								DriverList.remove(i);
								i--;
								break;
							}
							else
							{
								//�j�M�d�򤺤��঳�@���H�W�w�ƤJ���ϥΪ�
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
	//�ϰ�Filter
		public  void areaFilter(List<DriverTable> DriverList,int Classification)
		{
			//conform[0]:�W�@�Z�w���̻P��e�w���̬O�_���ŦX�ϰ�� conform[1]:�U�@�Z�w���̻P��e�w���̬O�_���ŦX�ϰ��
			int[] Area = {-1,-1};
		
			for(int i = 0; i < DriverList.size(); i++)
			{
		
			 RequestTable PreReqIndex = null;//�����W�@�Z�w���̩ΤU�@�Z�w����
			 //�W���ɶ��M��W�@�Z�w����
			 for(int  index = StartInterval; index >= 0; index--)
			 {
				if(index == 0)//�M����0�϶�
				{
					continue;
				}
				//���W�@�Ӧ��ƯZ��TimeInterval
				if(!(DriverList.get(i).TimeInterval[index].equals("���W�Z")) 
						&& !(DriverList.get(i).TimeInterval[index].equals("���ƯZ")))
				{
				  //���P�_���e���U���a�I�b��
				   String[] temp = DriverList.get(i).TimeInterval[index].split("_");
				   PreReqIndex = IndexMap.get(Integer.valueOf(temp[0]));
					break;
				}
			 }
			 if(PreReqIndex==null)//�p�G�S���N���v���]���̻�
			 {
				 Area[0]=9;
			 }
			 else
			 {
				 //�O���W�@�Z���ȤU���a�I�P�ثe�w���̪��W���a�I���v����
				 Area[0]=defineVariable.Weight[defineVariable.switchareaindex(PreReqIndex.Destinationarea)][defineVariable.switchareaindex(Reqtable.Originarea)];
			 }
			 
			 //�U���M��U�@�Z��
			 RequestTable NextReqIndex = null;//�����W�@�Z�w���̩ΤU�@�Z�w�����ܼƪ�l��
			 for(int index = EndInterval; index <=  DriverList.get(i).TimeInterval.length; index++)
			 {
			    if(index == DriverList.get(i).TimeInterval.length)//�p�G�M���̫�@�Ӱ϶�
				{
					 continue;
				}
			    if(!DriverList.get(i).TimeInterval[index].equals("���W�Z") 
			    		&& !DriverList.get(i).TimeInterval[index].equals("���ƯZ"))
				{
			    	//�P�_���᪺�W���a�I�b��	
			    	String[] temp = DriverList.get(i).TimeInterval[index].split("_");
			    	NextReqIndex = IndexMap.get(Integer.valueOf(temp[0]));
			    	break;				    
				 }
			  }
			  if(NextReqIndex==null)//�p�G�S���N���v���]���̻�
			  {
				 Area[1]=9;
			  }
			  else
			  {
				 //�O���W�@�Z���ȤU���a�I�P�ثe�w���̪��W���a�I���v����
				 Area[1]=defineVariable.Weight[defineVariable.switchareaindex(Reqtable.Destinationarea)][defineVariable.switchareaindex(NextReqIndex.Originarea)];
			  }
			  if(NextReqIndex!=null&&PreReqIndex!=null)
			  { 
				  if(PreReqIndex.DestinationAddress.indexOf(NextReqIndex.OriginAddress)!=-1 )
					  if((NextReqIndex.OriginTime- PreReqIndex.originalDestinationTime>=9000)&&(NextReqIndex.OriginTime- PreReqIndex.originalDestinationTime<=14400))
					  {
						  DriverList.remove(i);//�p�G���ŦX�N�R���q��
						  i--;
						  continue;
					  }
			  }
			   //�T�{�ثe�䪺�O���@�Ӱϰ����O
			  if(!(CheckAreaWeightsClass(Classification,Area)))
			  {
				 DriverList.remove(i);//�p�G���ŦX�N�R���q��
				 i--;
			  }
		   }	
	}	
	//�ϰ�Filter1 �P�쥻���ϰ�Filter�\��t�O�b�i�m���]�ZWeight
	public  void areaFilter1(List<DriverTable> DriverList,int run) throws IOException
	{
		
		//conform[0]:�W�@�Z�w���̻P��e�w���̬O�_���ŦX�ϰ�� conform[1]:�U�@�Z�w���̻P��e�w���̬O�_���ŦX�ϰ��
		boolean[] conform = {false,false};
		
		for(int i = 0; i < DriverList.size(); i++)
		{
		 conform[0] =false;//��l�ƤW�@�Z�w���̪�flag��false
		 conform[1] =false;//��l�ƤU�@�Z�w���̪�flag��false
		 conform[0]=checkeight(DriverList.get(i),run,defineVariable.switchareaindex(DriverList.get(i).station),defineVariable.switchareaindex(Reqtable.Originarea));//�p�G�P�W�@�Z���ϰ���v���Ȭ�9�N��L�k�䴩 �ҥHconform flag��false
	     conform[1]=checkeight(DriverList.get(i),run,defineVariable.switchareaindex(DriverList.get(i).station),defineVariable.switchareaindex(Reqtable.Destinationarea));		 
		
	     if((!conform[0])||(!conform[1]))	//�P�_�e��ϰ�O�_���ŦX	
		 {
		   DriverList.remove(i);//�p�G���ŦX�N�R���q��
		   i--;
		}	
	   }	
	 }	
	
	
	//�ˬd�^�t�ϰ쪺Filter
	public  void endareaFilter(List<DriverTable> DriverList) throws IOException
	{
		for(int i = 0; i < DriverList.size(); i++)
		{
			/* if(DriverList.get(i).ID.equals("8400-A2")&&Reqtable.RequestNumber.equals("6270"))
				 System.out.println("fff");*/
			//�p�G�w���̤W���ɶ��j��q���������ɬq+1�p�ɻ��ˬd�^�t�ϰ� //�W���ɶ����٭쬰��ӤW���ɶ�
			if((Reqtable.OriginTime+300)>=(DriverList.get(i).halfworktime+Variable.halfworktimeTolerableTime))
			 {
								
				//�ˬd�W���O�_�ŦX�^�t�ϰ�
				if(!defineVariable.backareaWeight[defineVariable.switchareaindex(DriverList.get(i).station)][defineVariable.switchareaindex(Reqtable.Originarea)])//�d�ߨ����P���������v��			
				 {
					 DriverList.remove(i);//���ŦX�R��
					 i--;
					 continue;
					
				 }
				//�ˬd�U���O�_�ŦX�^�t�ϰ�
				 if(!defineVariable.backareaWeight[defineVariable.switchareaindex(DriverList.get(i).station)][defineVariable.switchareaindex(Reqtable.Destinationarea)])//�d�ߨ����P���������v��			
				 {
					 DriverList.remove(i);//���ŦX�R��
					 i--;										
				 }
				
			 }			
		}
		
	}
	//�P�_�ӥq���O�_�Ӫ��α��e�o��w����
	public int DistanceTimeFilter(List<DriverTable> DriverList)
	{
		//�D�@���̭p��ɶ�
		if(FiltersEnable[2] == 1&&Reqtable.AssignSharing==-1)
		{
			//int recentTimeResult = -1;								//��q�ɶ�
			boolean canbreak = false;	
			for(int i = 0; i < DriverList.size(); i++)
			{

				/* if(DriverList.get(i).ID.equals("8400-A2")&&Reqtable.RequestNumber.equals("6270"))
					 System.out.println("fff");*/
				//�^�Ǥp�󵥩�-2�N��google�d�ߦ����ߧY�פ�{��
				if(Variable.errorcode<=-2)
				{
					break;
				}
				
				canbreak = false;
				//�ˬd�W�@�Z�U���a�I��ثe�w���̤W���a�I�O�_�ӱo��
				if(DriverList.get(i).StartDistanceValue>0)
				{
					//���]�o�q���Ӥ��α���e�ƪ��w���̴N�R���A�_�h�ˬd�U���Ӥ��Ӫ��ΤU�@�ӹw����
					if((Reqtable.OriginTime-DriverList.get(i).PreviousrequstTime)< DriverList.get(i).StartDistanceValue)
					{
						DriverList.remove(i);
						i--;
						canbreak=true;
					}
				}
				else
				{
					//�M��W�@��
					RequestTable TableIndex=Variable.PreRequestTableQuery(DriverList.get(i),StartInterval,Variable,IndexMap);
					int [] traveltime=new int[2];//�Ĥ@������W�@��req��Interval index  �ĤG��Ȧ�ɶ�
					traveltime=Variable.DistanceTime(TableIndex,Reqtable,smartSearch,Variable,IntervalSec);
					if(traveltime[1]<=-2)
					{
						//�^�Ǥp�󵥩�-2�N��google�d�ߦ����ߧY�פ�{��
						Variable.errorcode=traveltime[1];
							break;
					}
					//�o��request���w���ɶ���h�W�@�a�I���ɶ��p��google maps api���o���ɶ��A�R�h�o�ӥq��
					if((Reqtable.OriginTime-TableIndex.DestinationTime)< traveltime[1] || traveltime[1] < 0)
					{
						DriverList.remove(i);
						i--;
						canbreak=true;
					}
					else
					{
						//��Ҧ����󪺹L�o�����L�o����A�ٳѾl�W�L�@�����A�ݳz�LDistanceValue���Ȧ�ɶ���ơA�h��ܥ�q�ɶ����u������
						//�����q���n����e�w���̪��Ȧ�ɶ�
						DriverList.get(i).StartDistanceValue=traveltime[1];
					}				 
				}
				
				//�ˬd�ثe�w���̤U���a�I��U�@�Z�W���a�I�O�_�ӱo��
				if(canbreak)//�p�G�Ӥ��δN�����ˬd�U���ϰ�
				{
					continue;
				}
				
				//�U���a�I�P���᪺�W���a�I�����Ȧ�ɶ�
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
					int [] traveltime=new int[2];//�Ĥ@������W�@��req��Interval index  �ĤG��Ȧ�ɶ�
					traveltime=Variable.DistanceTime(Reqtable,NextTableIndex,smartSearch,Variable,IntervalSec);
					if(traveltime[1]<=-2)
					{
						//�^�Ǥp�󵥩�-2�N��google�d�ߦ����ߧY�פ�{��
						Variable.errorcode=traveltime[1];
							break;
					}
					//�P�_�O�_�ӱo��
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
	//�P�_���w�@���O�_�ӱo��
	public int AssignSharingDistanceTimeFilter(List<DriverTable> DriverList) throws InterruptedException, IOException
	{
		if(Reqtable.AssignSharing!=-1)
		{
			RequestTable AssignSharingReq=IndexMap.get(Reqtable.AssignSharing);
			for(int i = 0; i < DriverList.size(); i++)
			{ 
				boolean canbreak = false;	
				//��X�W�@�몺�w����	
				//�^�Ǥp�󵥩�-2�N��google�d�ߦ����ߧY�פ�{��
				if(Variable.errorcode<=-2)
				{
					break;
				}
				RequestTable tableindex=Variable.PreRequestTableQuery(DriverList.get(i),StartInterval,Variable,IndexMap);		    
				//�d�߷�e�w���̸�W�@�Z���Ȧ�ɶ�
				int [] traveltime=Variable.DistanceTime(tableindex,Reqtable,smartSearch,Variable,IntervalSec);
				//�^��error code
			     if(traveltime[1]<=-2)
				 {
				   Variable.errorcode=traveltime[1];
				   break;
				}
				//�d�߻P��e�w���̦@����W�@�Z���Ȧ�ɶ�
				int [] traveltime1=Variable.DistanceTime(tableindex,AssignSharingReq,smartSearch,Variable,IntervalSec);
				//�^��error code
				if(traveltime1[1]<=-2)
				{
					Variable.errorcode=traveltime1[1];
					break;
				}					
				//�q������e���b�B�z���w���̪��Ȧ�ɶ���@�����w���̪��u
				boolean staus=false;
				int OriginTime=0;
				int PreTraveltime=-1;
				//�ݦ@�����@�Ӥ�����W��
				if(Reqtable.OriginTime<AssignSharingReq.OriginTime)
					OriginTime=Reqtable.OriginTime;
				else
					OriginTime=AssignSharingReq.OriginTime;
				//�b�P�_���@�ӮȦ�ɶ�����
				if(traveltime[1]<traveltime1[1])
				{
					PreTraveltime=traveltime1[1];
				}
				else
				{
					PreTraveltime=traveltime[1];
				}
				//�P�_�Ӥ��Ӫ���
				//�o��request���w���ɶ���h�W�@�a�I���ɶ��p��google maps api���o���ɶ��A�R�h�o�ӥq��
				staus=checkDistanceTime(PreTraveltime,(OriginTime-tableindex.DestinationTime));
				//�Ӥ��ΩήȦ�ɶ�<0�R��
				if(!staus||(PreTraveltime<0))
				{
					DriverList.remove(i);
					i--;
					canbreak=true;
				}
				else
				{
					//��Ҧ����󪺹L�o�����L�o����A�ٳѾl�W�L�@�����A�ݳz�LDistanceValue���Ȧ�ɶ���ơA�h��ܥ�q�ɶ����u������
					//�����q���n����e�w���̪��Ȧ�ɶ�
					DriverList.get(i).StartDistanceValue=traveltime[1];
				}
				
				//�^�Ǥp�󵥩�-2�N��google�d�ߦ����ߧY�פ�{��
				if(Variable.errorcode<=-2)
				{
					break;
				}
				if(canbreak)//�p�G�Ӥ��δN�����ˬd�U���ϰ�
			    {
					continue;
				}
				
				//��X�U�@��
				for(int nextindex=EndInterval;nextindex<DriverList.get(i).TimeInterval.length;nextindex++)
				{
					 if(!(DriverList.get(i).TimeInterval[nextindex].indexOf("���W�Z")!=-1) 
							 &&!(DriverList.get(i).TimeInterval[nextindex].indexOf("���ƯZ")!=-1))
					 {  
						 //���U�@��w����
						 RequestTable nextrep=Variable.RequestTableQuery(DriverList.get(i).TimeInterval[nextindex],Variable,IndexMap);								
						 if(nextrep.originalStartTime>Reqtable.originalStartTime)
						 {
							 //�d�߷�e�w���̸�U�@�Z���Ȧ�ɶ�
							 traveltime=Variable.DistanceTime(Reqtable,nextrep,smartSearch,Variable,IntervalSec);
							 //�^��error code
							 if(traveltime[1]<=-2)
							 {
								 Variable.errorcode=traveltime[1];
								 break;							 
							 }
							 //�d�߻P��e�w���̦@����U�@�Z���Ȧ�ɶ�
							 traveltime1=Variable.DistanceTime(AssignSharingReq,nextrep,smartSearch,Variable,IntervalSec);
							 //�^��error code
							 if(traveltime1[1]<=-2)
							 {
								 Variable.errorcode=traveltime1[1];
								 break;
							 }
							 
							 int DestinationTime=0;
							 int NextTraveltime=-1;
							//�ݦ@�����@�Ӥ���ߤU��
							if(Reqtable.DestinationTime>AssignSharingReq.DestinationTime)
								DestinationTime=Reqtable.DestinationTime;
							else
								DestinationTime=AssignSharingReq.DestinationTime;
								//�b�P�_���@�ӮȦ�ɶ�����
							if(traveltime[1]<traveltime1[1])
							{
								NextTraveltime=traveltime1[1];
							}
							else
							{
								NextTraveltime=traveltime[1];
							}
							staus=false;
							//�P�_�Ӥ��Ӫ��α���U�@�Z�w����	
							staus=checkDistanceTime(NextTraveltime,(nextrep.OriginTime - DestinationTime));
							//�Ӥ��ΩήȦ�ɶ�<0�R��
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
		//�ˬd�O�_�������𮧮ɶ�
	public void restFilter(List<DriverTable> DriverList)
	{
		for(int i = 0; i < DriverList.size(); i++)
		{ 
			//�^�Ǥp�󵥩�-2�N��google�d�ߦ����ߧY�פ�{��
			if(Variable.errorcode<=-2)
			{
				break;
			}
			//�u�ɤp��6�p�ɤ���𮧮ɶ�
			if((DriverList.get(i).EndTime-DriverList.get(i).StartTime)<=Variable.nonrelax)
				continue;
			int startInterval = (int)((DriverList.get(i).startreqtime+1800) / IntervalSec);//�p���Y�Z�Ҧb�϶�
			//�p����Z�Ҧb�϶�				
			int endInterval = ((DriverList.get(i).endreqtime-1800) /(int) IntervalSec);
			int startindex=startInterval;				
			int endindex=endInterval;
			//���ƻs�쥻�𮧰϶���array��temprelaxarry
			DriverList.get(i).temprelaxarry=null;///�M��
			DriverList.get(i).temprelaxarry=new ArrayList<String>(DriverList.get(i).relaxarry);
			//�p�G�w���̮ɶ��϶����b�q���i�𮧮ɶ����N�n�ˬd�O�_���������𮧮ɶ�	
			if(DriverList.get(i).relaxarry.size()>=2)
			{
				//���R��req�Ҧ����϶�
				for(int index = StartInterval; index <= EndInterval; index++)
				{
					//�����b�𮧰϶��N�R��
					if(index>=startindex&&index<=endindex)
						DriverList.get(i).temprelaxarry.remove(String.valueOf(index));
				}
				//��X�W�@�몺�w����
				RequestTable tableindex;
				tableindex = Variable.PreRequestTableQuery(DriverList.get(i),StartInterval+1,Variable,IndexMap);				
				//���B�z��U���w���̤W���P�W�@��U������
				//��X�W�@�Z���ƯZ���϶��Ȧ�ɶ� �Ĥ@�欰�W�@��Ҧb���϶� �ĤG�欰�Ȧ�ɶ�	
				if(tableindex==null)
					System.out.println("");
				int [] traveltime=Variable.DistanceTime(tableindex,Reqtable,smartSearch,Variable,IntervalSec);
				//�^��error code
				if(traveltime[1]<=-2)
				{
					Variable.errorcode=traveltime[1];
						break;
				}
				
				//�p��[�W�e�Ԯɶ�
				int temptraveltime=traveltime[1]+Variable.TolerableTime;
				//�����W�@�ӤU���ӱ��o��Ȧ�ɶ�
				DriverList.get(i).StartDistanceValue=traveltime[1];
				//�����W�@�ӹw���̤U���ɶ�
				DriverList.get(i).PreviousrequstTime=tableindex.DestinationTime;
				//�p�G�W�@��P�P��e��䤤�@�ӹJ��y�p�ɨ�n�[�Wdelay time			
				if((Reqtable.originalStartTime>=Variable.areaPrioritystartmorningpeaktime&&Reqtable.originalStartTime<=Variable.areaPriorityendmorningpeaktime)
						||(tableindex.originalDestinationTime>=Variable.areaPrioritystartmorningpeaktime&&tableindex.originalDestinationTime<=Variable.areaPriorityendmorningpeaktime))
					temptraveltime+=Variable.morningpeaktime;                       //���W7:30~8:30�y�p�ɬq�A�[�W15������
				if(Reqtable.originalStartTime>=Variable.areaPrioritystartafternoonpeaktime&&Reqtable.originalStartTime<=Variable.areaPriorityendafternoonpeaktime
						||(tableindex.originalDestinationTime>=Variable.areaPrioritystartafternoonpeaktime&&tableindex.originalDestinationTime<=Variable.areaPriorityendafternoonpeaktime))
					temptraveltime+=Variable.afternoonpeaktime;                      //�U��16:45~18:30�y�p�ɬq�A�[�W20������					
				//�W�@��U���ҳѤU�i�����ɶ�
				int tempSecond=(tableindex.originalDestinationTime-(tableindex.originalDestinationTime%60));
				int min = (int)((tempSecond % 3600) / 60);//��Ʀ���
				//�����i�δݾl���ɶ�
				int Residualtime=0;
				if(min>=30&&min<60)
					Residualtime=(60-min);
				else if(min<30&&min>=0)
					Residualtime=(30-min);
				temptraveltime=temptraveltime-(Residualtime*60);					
				//�o��W���ҳѪ��ɶ�
				tempSecond=(Reqtable.originalStartTime-(Reqtable.originalStartTime%60));
				min = (int)((tempSecond % 3600) / 60);//��Ʀ���
				Residualtime=0;
				if(min==30||min==0)
					Residualtime=0;
				else if(min<30)
					Residualtime=min;
				else if(min>30)
					Residualtime=(min-30);
				//�����i�δݾl���ɶ�
				temptraveltime-=(Residualtime*60);
				//�p��n��O�����
				int Spendtimecount=0;
				Spendtimecount= (temptraveltime / (int) IntervalSec);
				//�R���}���Ҫ�O�����				
				for(int count=traveltime[0]+1;count<(traveltime[0]+Spendtimecount+1);count++)
				{
					if(count>=startindex&&count<=endindex)
						DriverList.get(i).temprelaxarry.remove(String.valueOf(count));//�R���}���Ҫ�O���ɶ�
				}
				//�p�G�P�W�@��e�ή�ƭp�⧹�𮧰϶��p��2��ߧY�R��
				if(DriverList.get(i).temprelaxarry.size()<2)
				{
					//�p�G�i�Ϊ��𮧮ɶ��p��1�p�ɴN�R��
					DriverList.get(i).temprelaxarry=null;
					DriverList.remove(i);
					i--;
					continue;
				}
				//�^�Ǥp�󵥩�-2�N��google�d�ߦ����ߧY�פ�{��
				if(Variable.errorcode<=-2)
				{
					break;
				}
				//�B�z�U�@��P�o��	EndInterval:�O��ereq�������϶�	 	
				for(int nextindex=EndInterval;nextindex<DriverList.get(i).TimeInterval.length;nextindex++)
				{
					if(!(DriverList.get(i).TimeInterval[nextindex].indexOf("���W�Z")!=-1) 
						&&!(DriverList.get(i).TimeInterval[nextindex].indexOf("���ƯZ")!=-1)
						&&!(DriverList.get(i).TimeInterval[nextindex].indexOf("��")!=-1))
					{    //���U�@��w����
						 RequestTable nextrep=Variable.RequestTableQuery(DriverList.get(i).TimeInterval[nextindex],Variable,IndexMap);
						if(nextrep.originalStartTime>Reqtable.originalStartTime)
						{
							//�p���̤����Ȧ�ɶ�
							 int [] traveltime1=Variable.DistanceTime(Reqtable,nextrep,smartSearch,Variable,IntervalSec);
							 if(traveltime1[1]<=-2)
							 {
								Variable.errorcode=traveltime1[1];
								break;
							}							 
							//�p��[�W�e�Ԯɶ�
							int temptraveltime1=traveltime1[1]+Variable.TolerableTime;
							//�����o��w���̤U���h���U�@��Ȧ�ɶ�
							DriverList.get(i).EndDistanceValue=traveltime1[1];
							//�����U�@�ӹw���̤W���ɶ�
							DriverList.get(i).NextrequstTime=nextrep.OriginTime;
							//�p�G�W�@��P�P��e��䤤�@�ӹJ��y�p�ɨ�n�[�Wdelay time
							//�p�G�W�@��P�P��e��䤤�@�ӹJ��y�p�ɨ�n�[�Wdelay time			
							if((Reqtable.originalStartTime>=Variable.areaPrioritystartmorningpeaktime&&Reqtable.originalStartTime<=Variable.areaPriorityendmorningpeaktime)
									||(tableindex.originalDestinationTime>=Variable.areaPrioritystartmorningpeaktime&&tableindex.originalDestinationTime<=Variable.areaPriorityendmorningpeaktime))
								temptraveltime1+=Variable.morningpeaktime;                       //���W7:30~8:30�y�p�ɬq�A�[�W15������
							if(Reqtable.originalStartTime>=Variable.areaPrioritystartafternoonpeaktime&&Reqtable.originalStartTime<=Variable.areaPriorityendafternoonpeaktime
									||(tableindex.originalDestinationTime>=Variable.areaPrioritystartafternoonpeaktime&&tableindex.originalDestinationTime<=Variable.areaPriorityendafternoonpeaktime))
								temptraveltime1+=Variable.afternoonpeaktime;                      //�U��16:45~18:30�y�p�ɬq�A�[�W20������		
										
							//�U�@��W���ҳѤU�i�����ɶ�	
							tempSecond=(nextrep.originalStartTime-(nextrep.originalStartTime%60));
							min=0;
							min = (int)((tempSecond % 3600) / 60);//��Ʀ���
							Residualtime=0;
							if(min==30||min==0)
								Residualtime=0;
							else if(min<30)
								Residualtime=min;
							else if(min>30)
								Residualtime=(min-30);
							//�����i�δݾl���ɶ�
							temptraveltime1=temptraveltime1-(Residualtime*60);
							//�o�@��U���ҳѤU�i�����ɶ�
							tempSecond=(Reqtable.originalDestinationTime-(Reqtable.originalDestinationTime%60));
							min=0;
							min = (int)((tempSecond % 3600) / 60);//��Ʀ���
							//�����i�δݾl���ɶ�
							Residualtime=0;
						   if(min>=30&&min<60)
								 Residualtime=(60-min);
							else if(min<30&&min>=0)
								Residualtime=(30-min);
							temptraveltime1=temptraveltime1-(Residualtime*60);
							//�p��n��O�����
							Spendtimecount=0;
							Spendtimecount= (temptraveltime1 / (int) IntervalSec);
							//�R���}���Ҫ�O�����				
							for(int count=traveltime1[0]+1;count<(traveltime1[0]+1+Spendtimecount);count++)
							{
								if(count>=startindex&&count<=endindex)
									DriverList.get(i).temprelaxarry.remove(String.valueOf(count));//�R���}���Ҫ�O���ɶ�
							}
							break;
						}
					}	
				}	
			    //�p�G�P�U�@��e�ή�ƭp�⧹�𮧰϶��p��2��ߧY�R��
				if(DriverList.get(i).temprelaxarry.size()<2)
				{
					//�p�G�i�Ϊ��𮧮ɶ��p��1�p�ɴN�R��
					DriverList.get(i).temprelaxarry=null;
					DriverList.remove(i);
					i--;
				}
			}
			else
			{
				//�p�G�i�Ϊ��𮧮ɶ��p��1�p�ɴN�R��
				DriverList.remove(i);
				i--;
			}
	 }
	}
	//�ˬd��ƤW��
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
	//�N�W�L�@�x�H�W���Ըɨ����R���ѤU�@�x
	public DriverTable MinFilter(List<DriverTable> DriverList,boolean nightflag) throws IOException
	{		
		DriverTable TargetDriver=null;//�w���̿ﭫ���q��			
		//�q�����`�ƭn�j��0�~���
		if(FiltersEnable[4] == 1&&DriverList.size()>0)
		{
			int minValue = -1;//�����q����w���̪��̵u�Z��
			int runCount = -1;//�����̤j���s��Count			 
			for(int i = 0; i < DriverList.size(); i++)
			{
				if(nightflag)
				{
					//�p�G��������F���w�����W���a�I�Ҫ�O���ɶ����O�����ɶ��u
					if(minValue==-1||DriverList.get(i).StartDistanceValue< minValue)
					{
						//��s��q�ɶ�����
						minValue = DriverList.get(i).StartDistanceValue;
						//��s�ﭫ����	
						TargetDriver=DriverList.get(i);
					}
				}else
				{
					//�@��N��̦h�ƯZ��
					if((runCount == -1) ||(DriverList.get(i).ArrangedCount>runCount))
					 {
						//�O���̤j���ƤJ�w���ƶq
						runCount = DriverList.get(i).ArrangedCount;
						//�O����������F���w�����W���a�I�Ҫ�O���ɶ�
					    minValue = DriverList.get(i).StartDistanceValue;
						//�O���ﭫ����					    
					    TargetDriver=DriverList.get(i);
					    }
					//�p�G���������m�ƶq�@�˦h�����p�A��郞����F���w�����W���a�I�Ҫ�O���ɶ��A��ܪ�O�ɶ����֪�����
					else if(DriverList.get(i).ArrangedCount == runCount)
					{
						//�p�G��������F���w�����W���a�I�Ҫ�O���ɶ����O�����ɶ��u
						if(DriverList.get(i).StartDistanceValue< minValue)
						{
							//��s��q�ɶ�����
							minValue = DriverList.get(i).StartDistanceValue;
							//��s�ﭫ����							
							TargetDriver=DriverList.get(i);
						}
					}
				}
		  }
	}
		return TargetDriver;
	}	
	
	//�P�_�O�_�Ӥ��Ӫ��ΦA���\���ɶ������W
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
			if(defineVariable.Weight[area1][area2]==9)//�d�ߨ����P�ݨD�����v
			{
				return false;//�p�G�P�W�@�Z���ϰ���v���Ȭ�9�N��L�k�䴩 �ҥHconform flag��false
			}
			else
			{
				return true;//�p�G�P�W�@�Z���ϰ��ŦXconform flag��true
			}
		}else if(DriverTable.StartTime<43200)//��2��check���O�]�Z�q��
		{
			if(defineVariable.Weight[area1][area2]==9)//�d�ߨ����P�ݨD�����v
			{
				return false;//�p�G�P�W�@�Z���ϰ���v���Ȭ�9�N��L�k�䴩 �ҥHconform flag��false
			}
			else
			{
				return true;//�p�G�P�W�@�Z���ϰ��ŦXconform flag��true
			}
		}
		else //��2��check�O�]�Z�q��
		{
			if(defineVariable.nightareaWeight[area1][area2]==9)//�d�ߨ����P�ݨD�����v
			{
				
				return false;//�p�G�P�W�@�Z���ϰ���v���Ȭ�9�N��L�k�䴩 �ҥHconform flag��false
			}
			else
			{
				return true;//�p�G�P�W�@�Z���ϰ��ŦXconform flag��true
			}
		}
	}
	public String[]  Getaddress(String reqnum,String[] tempaddress,int mode)
	{
		String[] number=reqnum.split("_");
		if(number.length<2)
		{
			if(number[0].indexOf("�Z")==-1)
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
	//�P�_
	public boolean CheckAreaWeightsClass(int Classification,int[] Area)
	{ 
		boolean Conform=false;
		//�ˬd�ثe�_�ŦX�n��M�����O
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

