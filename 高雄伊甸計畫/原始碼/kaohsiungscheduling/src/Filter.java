//�ƯZfilter�����A�i�H�ھڻݨD�⴫module
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
		Variable = variable;//�w�q�ܼƪ�l��
		FiltersEnable = enable;	
		Interval = interval;//�@��interval���ɶ����u(�p��)			
		IntervalSec = Interval * 3600;//�Ninterval���ɶ��Ѥp�ɬ�����ন�Ѭ����	
		Driver=driver;//��e���q��
		IndexMap = indexmap;//�s��Ҧ��w����queue		
		Variable.input = new double[4];//�s��W�U���a�}���g�n�׭ȡA��¥Ψӷ�@�ǤJfunction�Ϊ��ѼơCinput[0]���W���a�}���n�סAinput[1]���W���a�}���g�סAinput[2]�U���a�}���n�סAinput[3]���U���a�}���g�סC	
		Variable.address = new String[2];//�s��W�U���a�I���a�}�A��¥Ψӷ�@�ǤJfunction�Ϊ��ѼơCaddress[0]���W���a�}�Aaddress[1]���U���a�}�C
		//canShare = -1;//�b�P�w�@���ɩҨϥΪ��ѼơA�p�G����>-1�N��o���w���N�M�Y��w���X�֬��@���Z���C
		smartSearch = inputilf;//��M��a�I����q�ɶ�������	
		StartInterval=startindex;//�_�l�϶�
		//����M��i�Ϊ��Ŷ� �̦h1.5�p�ɪŶ�
		for(int i = startindex; i <startindex+3;i++)
		{
			if(Driver.TimeInterval[i].indexOf("���ƯZ")!=-1)
				EndInterval=i;
			else
			{
				EndInterval=i;
				break;
			}
		}
		 //�M��W�@��
		 TableIndex=Variable.PreRequestTableQuery(Driver,StartInterval,Variable,IndexMap);
		 //�M��U�@��
		 NextTableIndex=Variable.NextRequestTableQuery(Driver,EndInterval,Variable,IndexMap);
			//�p�G�W�@��ΤU�@�볣�X�{�s�������� xindianRoadSplitArea[index] index=0�N��W�� 1���U��
		 if(TableIndex.xindianRoadSplitArea[1])
		 {
			 xindianRoadSplitArea=TableIndex.xindianRoadSplitArea[1];
		 }else  if(NextTableIndex.xindianRoadSplitArea[0])
		{
			 xindianRoadSplitArea=NextTableIndex.xindianRoadSplitArea[0];
		}
		 //���o�W�@�몺�a�} �H��K�������t��
		 Getaddress(tempaddress,0);
		 //���o�U�@�몺�a�} �H��K�������t��
		 Getaddress(tempaddress,1);
	}
	//�ϰ��������������L�ذ�
	public void AreaCorrespond(List<RequestTable> ReqList)
	{		
		//���s�_���H�~�ϰ��ˬd�������a�}
		if(TableIndex.Destinationarea.indexOf("�x�_��")!=-1&&NextTableIndex.Originarea.indexOf("�x�_��")!=-1)
		{
			if(TableIndex.originalDestinationTime-NextTableIndex.originalStartTime<=10800)	
				if(TableIndex.DestinationAddress.indexOf(NextTableIndex.OriginAddress)!=-1)		
					ReqList.removeAll(ReqList);
		}/*else if(TableIndex.Destinationarea.indexOf("�s�_���a�q��")!=-1&&TableIndex.Destinationarea.indexOf("�s�_���T�l��")==-1)
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
				//���W7:30~8:30�y�p�ɬq ��//�U��16:45~18:30�y�p�ɬq	
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
	//�s�����s�Ϫ����L�o�ϰ�
	public void xindianRoadSplitAreaFilter(List<RequestTable> ReqList)
	{		
		if(xindianRoadSplitArea)
		{
			for(int i = 0; i < ReqList.size();i++)
			{
				if(ReqList.get(i).Originarea.indexOf("�s��")==-1)
				{
					ReqList.remove(i);	
					i--;
					continue;
				}
				if(ReqList.get(i).Destinationarea.indexOf("�s��")==-1)
				{
					ReqList.remove(i);	
					i--;					
				}
			}
		}
		
	}
	//�����t�� �ثe���h�M��W�@��ΤU�@��O�_���ۦP��
	public void RoadToRoadFilter(List<RequestTable> ReqList)
	{
		String[] check=new String[]{"��","��","�D"};
		String[] reqtempaddress = new String[]{"null","null"};	
		for(int i = 0; i < ReqList.size();i++)
		{
			//��l��
			reqtempaddress[0]="null";
			reqtempaddress[1]="null";
			//�W���a�}���X����r
			for(int index = 0; index<=2;index++ )
			{
				if(ReqList.get(i).OriginAddress.indexOf(check[index])!=-1)
				{ 
					reqtempaddress[0]=ReqList.get(i).OriginAddress.substring(0,ReqList.get(i).OriginAddress.indexOf(check[index])+1);
					break;
				}
			 }
			 //�U���a�}���X����r
			for(int index = 0; index<=2;index++ )
			{
				if(ReqList.get(i).DestinationAddress.indexOf(check[index])!=-1)
				{ 
					reqtempaddress[1]=ReqList.get(i).DestinationAddress.substring(0,ReqList.get(i).DestinationAddress.indexOf(check[index])+1);
					break;
				}
			}
			//���q���̪��Z��O�_���]�t����r��
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
	//�s�ӥq�������@������
	public void AssignSharingCarFilter(List<RequestTable> ReqList)
	{
		for(int index=0;index<Variable.TiroDriver.size();index++)
		{
			if(Variable.TiroDriver.get(index).indexOf(Driver.ID)!=-1)
			{
				for(int i = 0; i < ReqList.size();i++)
				{
					//�w���̦����w�@���h�R��
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
	//�S���������W�L5��
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
	//����filter�A��ª��L�o�����ŦX���ت��w����
	public void CarFilter(List<RequestTable> ReqList)
	{
		for(int i = 0; i < ReqList.size(); )
		{
			if(ReqList.get(i).OriginAddress.indexOf("�{�Ҹ�224��")!=-1&&Driver.CallNum.indexOf("52")!=-1)
				System.out.println("");
			//���ؤ��Ū��L�o��			
			if(ReqList.get(i).Car.equals(""))//�j�����w���̥i�H��ܤj�p��
				 i++;
			 else
			 {
				if(ReqList.get(i).Car.indexOf(Driver.Car)!=-1)//�p�G�O�p�����w���̥u���ܤp�������j��
					i++;
				else
					 ReqList.remove(i);
				}	
			}
	}
	//���Afilter�A�L�o����ƶi�q���Z���w���̥H�Τ��ŦX�W�U�Z�ɶ����w����
	public void StatusFilter(List<RequestTable> ReqList)
	{
		//�έ쥻�W�U���ɶ��h��϶�
		int ReqStartInterval = 0;
		//�U���ɶ��b�@�Ѥ���interval index
		int ReqEndInterval =0;
		for(int i = 0; i < ReqList.size(); i++)
		{
			if(ReqList.get(i).Arrange)
			{	
				ReqList.remove(i);	
				i--;
				continue;
			}
			//�έ쥻�W�U���ɶ��h��϶�
			 ReqStartInterval = (int)( (ReqList.get(i).originalStartTime) / IntervalSec);//�W���ɶ��b�@�Ѥ���interval index
			//�U���ɶ��b�@�Ѥ���interval index
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
			//�L�o�ثe�q�����@�p�ɥH���i�H�Ϊ��w����,�W�L1�p�ɥH�~���R��
			if(!(ReqStartInterval>=StartInterval&&ReqStartInterval<=EndInterval
					&&ReqEndInterval>=StartInterval&&ReqEndInterval<=EndInterval))
			{
				ReqList.remove(i);
				i--;
			}
		}
	}
	//�ϰ�Filter
	public  void areaFilter(boolean NightFlag,List<RequestTable> ReqList,int Classification)
	{   
			//conform[0]:�W�@�Z�w���̻P��e�w���̬O�_���ŦX�ϰ�� conform[1]:�U�@�Z�w���̻P��e�w���̬O�_���ŦX�ϰ��
			int[] Area = {-1,-1};
			for(int i = 0; i < ReqList.size(); i++)
			{			
				//�p�G�n�諸�w���̥X�{�s�������D���N�ˬd
				if(ReqList.get(i).xindianRoadSplitArea[0])
				{	
					if(TableIndex.Destinationarea.indexOf("�s��")==-1 )
					 {
							ReqList.remove(i);//�p�G���ŦX�N�R���q��
							i--;
							continue;
					 }
				}
				if(ReqList.get(i).xindianRoadSplitArea[1])
					 if(NextTableIndex.Originarea.indexOf("�s��")==-1 )
					 {
							ReqList.remove(i);//�p�G���ŦX�N�R���q��
							i--;
							continue;
					 }		
				
				if(TableIndex.DestinationAddress=="null")//�p�G�S���N���v���]���̻�
				{
					Area[0]=9;
				 }else
				 {
					 //�O���W�@�Z���ȤU���a�I�P�ثe�w���̪��W���a�I���v����
					 if(NightFlag)//�P�_�{�b�O�_�ϥΩ]�Z weight
					 { 
						 Area[0]=defineVariable.nightareaWeight[defineVariable.switchareaindex(Driver.station)][defineVariable.switchareaindex(ReqList.get(i).Originarea)];
					 }
					 else 
					 {
						 Area[0]=defineVariable.AreaWeight[Variable.Area.get(TableIndex.Destinationarea)-1][Variable.Area.get(ReqList.get(i).Originarea)];
					 }
				  }
				if(NextTableIndex.OriginAddress=="null")//�p�G�S���N���v���]���̻�
				{
					Area[1]=9;
				}
			    else
				{
			    	//�O���W�@�Z���ȤU���a�I�P�ثe�w���̪��W���a�I���v����
			    	 if(NightFlag)//�P�_�{�b�O�_�ϥΩ]�Z weight
					 { 
			    		 Area[1]=defineVariable.nightareaWeight[defineVariable.switchareaindex(Driver.station)][defineVariable.switchareaindex(NextTableIndex.Originarea)];
					 }
					 else 
					 {
						 Area[1]=defineVariable.AreaWeight[Variable.Area.get(ReqList.get(i).Destinationarea)-1][Variable.Area.get(NextTableIndex.Originarea)];
					 }
			    	
				}
				  //�T�{�ثe�䪺�O���@�Ӱϰ����O
				  if(!(CheckAreaWeightsClass(Classification,Area)))
				  {
						ReqList.remove(i);//�p�G���ŦX�N�R���q��
						i--;
				  }
			}
	 }	
	/*//�ˬd�^�t�ϰ쪺Filter
	public  void endareaFilter(List<RequestTable> ReqList) 
	{
		for(int i = 0; i < ReqList.size(); i++)
		{
			//�p�G�w���̤W���ɶ��j��q���������ɬq+1�p�ɻ��ˬd�^�t�ϰ�
			if(ReqList.get(i).OriginTime>=(Driver.halfworktime+Variable.halfworktimeTolerableTime))
			 {
				//�ˬd�W���O�_�ŦX�^�t�ϰ�
				if(!defineVariable.backareaWeight[defineVariable.switchareaindex(Driver.station)][defineVariable.switchareaindex(ReqList.get(i).Originarea)])//�d�ߨ����P���������v��			
				 {
					 ReqList.remove(i);//���ŦX�R��
					 i--;
					 continue;
				 }
				//�ˬd�U���O�_�ŦX�^�t�ϰ�
				 if(!defineVariable.backareaWeight[defineVariable.switchareaindex(Driver.station)][defineVariable.switchareaindex(ReqList.get(i).Destinationarea)])//�d�ߨ����P���������v��			
				 {
					 ReqList.remove(i);//���ŦX�R��
					 i--;										
				 }
			 }
		}
	}*/
	//�P�_�ӥq���O�_�Ӫ��α��e�o��w����
	public int DistanceTimeFilter(List<RequestTable> ReqList)
	{
		boolean canbreak = false;	
		int OriginTime=0;
		int DestinationTime=0;
		RequestTable AssignSharingReq=null;
		for(int i = 0; i < ReqList.size(); i++)
		{	
				canbreak = false;				
				//�^�Ǥp�󵥩�-2�N��google�d�ߦ����ߧY�פ�{��
				if(Variable.errorcode<=-2)
				{
					break;
				}
				
				/*******************************�B�z�@����*******************************/
				if(ReqList.get(i).AssignSharing!=-1)
				{
					 AssignSharingReq=IndexMap.get(ReqList.get(i).AssignSharing);
					 //�d�߻P��e�w���̦@����W�@�Z���Ȧ�ɶ�
					int [] traveltime1=Variable.DistanceTime(TableIndex,AssignSharingReq,smartSearch,Variable,IntervalSec);
					//�O���W�@��P�@���̮Ȧ�ɶ�
					AssignSharingReq.StartDistanceValue=traveltime1[1];
					//�d�߻P��e�w���̦@����U�@�Z���Ȧ�ɶ�
					traveltime1=Variable.DistanceTime(AssignSharingReq,NextTableIndex,smartSearch,Variable,IntervalSec);
					//�O���U�@��P�@���̮Ȧ�ɶ�
					AssignSharingReq.EndDistanceValue=traveltime1[1];
					
					//�ݦ@�����@�Ӥ�����W��
					if(ReqList.get(i).OriginTime<AssignSharingReq.OriginTime)
						OriginTime=ReqList.get(i).OriginTime;
					else
						OriginTime=AssignSharingReq.OriginTime;
					
					//�ݦ@�����@�Ӥ���ߤU��
					if(ReqList.get(i).DestinationTime>AssignSharingReq.DestinationTime)						
						DestinationTime=ReqList.get(i).DestinationTime;
					else
						DestinationTime=AssignSharingReq.DestinationTime;	
				
					
				}else
				{
					OriginTime=ReqList.get(i).OriginTime;
					DestinationTime=ReqList.get(i).DestinationTime;
				}/******************************************************************/			
				//�ˬd�W�@�Z�U���a�I��ثe�w���̤W���a�I�O�_�ӱo��
				if(ReqList.get(i).StartDistanceValue<0)			
				{
					int [] traveltime=new int[2];//�Ĥ@������W�@��req��Interval index  �ĤG��Ȧ�ɶ�
					traveltime=Variable.DistanceTime(TableIndex,ReqList.get(i),smartSearch,Variable,IntervalSec);
					if(traveltime[1]<=-2)
					{
						//�^�Ǥp�󵥩�-2�N��google�d�ߦ����ߧY�פ�{��
						Variable.errorcode=traveltime[1];
						break;
					}
					ReqList.get(i).StartDistanceValue=traveltime[1];
				}
				
				
				/*******************************�B�z�@����*******************************/
				if(ReqList.get(i).AssignSharing!=-1)
				{
					//�p�G���@���N�����@�ӮȦ�ɶ������
					if(ReqList.get(i).StartDistanceValue<AssignSharingReq.StartDistanceValue)
					{
						ReqList.get(i).StartDistanceValue=AssignSharingReq.StartDistanceValue;
					}
				}
				/*******************************************************************/
	
				//�o��request���w���ɶ���h�W�@�a�I���ɶ��p��google maps api���o���ɶ��A�R�h�o�ӥq��
				if((OriginTime-TableIndex.DestinationTime)<ReqList.get(i).StartDistanceValue || ReqList.get(i).StartDistanceValue < 0)
				{
					ReqList.remove(i);
					i--;
					canbreak=true;
				}
				
				//�ˬd�ثe�w���̤U���a�I��U�@�Z�W���a�I�O�_�ӱo��
				if(canbreak)//�p�G�Ӥ��δN�����ˬd�U���ϰ�
				{
					continue;
				}
				
				//�U���a�I�P���᪺�W���a�I�����Ȧ�ɶ�
				if(ReqList.get(i).EndDistanceValue<0)
				{
					int [] traveltime=new int[2];//�Ĥ@������W�@��req��Interval index  �ĤG��Ȧ�ɶ�
					traveltime=Variable.DistanceTime(ReqList.get(i),NextTableIndex,smartSearch,Variable,IntervalSec);
					if(traveltime[1]<=-2)
					{
						//�^�Ǥp�󵥩�-2�N��google�d�ߦ����ߧY�פ�{��
						Variable.errorcode=traveltime[1];
							break;
					}
					ReqList.get(i).EndDistanceValue=traveltime[1];
				}
				/*******************************�B�z�@����*******************************/
				if(ReqList.get(i).AssignSharing!=-1)
				{
					//�p�G���@���N�����@�ӮȦ�ɶ������
					if(ReqList.get(i).EndDistanceValue<AssignSharingReq.EndDistanceValue)
					{
						ReqList.get(i).EndDistanceValue=AssignSharingReq.EndDistanceValue;
					}
				}
				/*******************************************************************/
				//�P�_�O�_�ӱo��
				if((NextTableIndex.OriginTime - DestinationTime)< ReqList.get(i).EndDistanceValue || ReqList.get(i).EndDistanceValue < 0)
				{
					ReqList.remove(i);
					i--;	
				}
		}
		return  Variable.errorcode;
	}	
	//�ˬd�O�_�������𮧮ɶ�
	public void restFilter(List<RequestTable> ReqList)
	{
		//�u�ɤp��6�p�ɤ���𮧮ɶ�
		if((Driver.EndTime-Driver.StartTime)>Variable.nonrelax)
		{
			int startInterval = (int)((Driver.startreqtime+1800) / IntervalSec);//�p���Y�Z+�b�p�ɩҦb�϶�
			//�p����Z-�b�p�ɩҦb�϶�		
			int endInterval = ((Driver.endreqtime+1800) /(int) IntervalSec);	
			int Spendtimecount=0;
			for(int i = 0; i < ReqList.size(); i++)
			{
				Spendtimecount=0;
				//�^�Ǥp�󵥩�-2�N��google�d�ߦ����ߧY�פ�{��
				if(Variable.errorcode<=-2)
				{
					break;
				}
				//���ƻs�쥻�𮧰϶���array��temprelaxarry
				 ReqList.get(i).temprelaxarry=null;///�M��
				 ReqList.get(i).temprelaxarry=new ArrayList<String>(Driver.relaxarry);
				//�p�G�w���̮ɶ��϶����b�q���i�𮧮ɶ����N�n�ˬd�O�_���������𮧮ɶ�	
				if(ReqList.get(i).temprelaxarry.size()>=2)
				{
					//�έ쥻�W�U���ɶ��h��϶�
					int ReqStartInterval = (int)( (ReqList.get(i).originalStartTime) / IntervalSec);//�W���ɶ��b�@�Ѥ���interval index
					//�U���ɶ��b�@�Ѥ���interval index
					int ReqEndInterval = (ReqList.get(i).originalDestinationTime / (int) IntervalSec);
					//���R��req�Ҧ����϶�
					for(int index = ReqStartInterval; index <= ReqEndInterval; index++)
					{
						//�����b�𮧰϶��N�R��
						if(index>=startInterval&&index<=endInterval)
							ReqList.get(i).temprelaxarry.remove(String.valueOf(index));
					}
					//���B�z��U���w���̤W���P�W�@��U������
					//��X�W�@�Z���ƯZ���϶��Ȧ�ɶ� �Ĥ@�欰�W�@��Ҧb���϶� �ĤG�欰�Ȧ�ɶ�	
					int [] traveltime=Variable.DistanceTime(TableIndex,ReqList.get(i),smartSearch,Variable,IntervalSec);
					//�^��error code
					if(traveltime[1]<=-2)
					{
						Variable.errorcode=traveltime[1];
							break;
					}
					//�����W�@�ӤU���ӱ��o��Ȧ�ɶ�
					ReqList.get(i).StartDistanceValue=traveltime[1];
					//�����W�@�ӹw���̤U���ɶ�
					ReqList.get(i).PreviousrequstTime=TableIndex.DestinationTime;
					Spendtimecount=GetSpendTimeCount(traveltime[1],ReqList.get(i),TableIndex);
					//�R���}���Ҫ�O�����				
					for(int count=traveltime[0]+1;count<(traveltime[0]+Spendtimecount+1);count++)
					{
						if(count>=startInterval&&count<=endInterval)
							ReqList.get(i).temprelaxarry.remove(String.valueOf(count));//�R���}���Ҫ�O���ɶ�
					}
					//�p�G�P�W�@��e�ή�ƭp�⧹�𮧰϶��p��2��ߧY�R��
					if(ReqList.get(i).temprelaxarry.size()<2)
					{
						//�p�G�i�Ϊ��𮧮ɶ��p��1�p�ɴN�R��
						ReqList.get(i).temprelaxarry=null;
						ReqList.remove(i);
						i--;
						continue;
					}
					//�^�Ǥp�󵥩�-2�N��google�d�ߦ����ߧY�פ�{��
					if(Variable.errorcode<=-2)
					{
						break;
					}
					
					if(NextTableIndex.originalStartTime>ReqList.get(i).originalStartTime)
					{
						//�p���̤����Ȧ�ɶ�
						 int [] traveltime1=Variable.DistanceTime(ReqList.get(i),NextTableIndex,smartSearch,Variable,IntervalSec);
						 if(traveltime1[1]<=-2)
						 {
							Variable.errorcode=traveltime1[1];
							break;
						}	
						//�����o��w���̤U���h���U�@��Ȧ�ɶ�
						 ReqList.get(i).EndDistanceValue=traveltime1[1];
						//�����U�@�ӹw���̤W���ɶ�
						ReqList.get(i).NextrequstTime=NextTableIndex.OriginTime;
						//�p���O�����
						Spendtimecount=GetSpendTimeCount(traveltime1[1],NextTableIndex,ReqList.get(i));
						//�R���}���Ҫ�O�����				
						for(int count=traveltime1[0]+1;count<(traveltime1[0]+1+Spendtimecount);count++)
						{
							if(count>=startInterval&&count<=endInterval)
								ReqList.get(i).temprelaxarry.remove(String.valueOf(count));//�R���}���Ҫ�O���ɶ�
						}
						   //�p�G�P�U�@��e�ή�ƭp�⧹�𮧰϶��p��2��ߧY�R��
						if(ReqList.get(i).temprelaxarry.size()<2)
						{
							//�p�G�i�Ϊ��𮧮ɶ��p��1�p�ɴN�R��
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
					//�p�G�i�Ϊ��𮧮ɶ��p��1�p�ɴN�R��
					ReqList.remove(i);
					i--;
				}
			}
		}
	}
	//�L�o�W�L�@�ӥH�W���w���̿�̨�	
	public RequestTable MinFilter(List<RequestTable> ReqList) 
	{		
		RequestTable TargetReq=null;//�ﭫ���w����	
		//�q�����`�ƭn�j��0�~���
		if(FiltersEnable[4] == 1&&ReqList.size()>0)
		{
			int minValue = -1;//�����q����w���̪��̵u�Z��
			int StartTime=-1;//�����W���ɶ�
			for(int i = 0; i < ReqList.size(); i++)
			{
				//�p�G��������F���w�����W���a�I�Ҫ�O���ɶ����O�����ɶ��u
				if(minValue==-1||ReqList.get(i).StartDistanceValue< minValue)
				{
					//��s��q�ɶ�����
					minValue = ReqList.get(i).StartDistanceValue;
					//��s�ﭫ�w����
					TargetReq=ReqList.get(i);
					//��s�W���ɶ�����
					StartTime=ReqList.get(i).originalStartTime;
				}
				else if (ReqList.get(i).StartDistanceValue == minValue&&(ReqList.get(i).originalStartTime<StartTime))//�p�G�P�ɶ���F�N�Ĩ��֥��W��
				{
					//��s��q�ɶ�����
					minValue = ReqList.get(i).StartDistanceValue;
					//��s�ﭫ�w����
					TargetReq=ReqList.get(i);
					//��s�W���ɶ�����
					StartTime=ReqList.get(i).originalStartTime;
				}
			}
		}	
		return TargetReq;
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
	//�p����뤧���ݭn��h�֮ɶ�����
	public int GetSpendTimeCount(int traveltime,RequestTable tagetreq,RequestTable previousone)
	{ 
		//�p��[�W�e�Ԯɶ�
		int temptraveltime=traveltime+Variable.TolerableTime;				
		//�p�G�W�@��P�P��e��䤤�@�ӹJ��y�p�ɨ�n�[�Wdelay time			
		if((tagetreq.originalStartTime>=Variable.areaPrioritystartmorningpeaktime&&tagetreq.originalStartTime<=Variable.areaPriorityendmorningpeaktime)
				||(previousone.originalDestinationTime>=Variable.areaPrioritystartmorningpeaktime&&previousone.originalDestinationTime<=Variable.areaPriorityendmorningpeaktime))
			temptraveltime+=Variable.morningpeaktime;                       //���W7:30~8:30�y�p�ɬq�A�[�W15������
		if(tagetreq.originalStartTime>=Variable.areaPrioritystartafternoonpeaktime&&tagetreq.originalStartTime<=Variable.areaPriorityendafternoonpeaktime
				||(previousone.originalDestinationTime>=Variable.areaPrioritystartafternoonpeaktime&&previousone.originalDestinationTime<=Variable.areaPriorityendafternoonpeaktime))
			temptraveltime+=Variable.afternoonpeaktime;                      //�U��16:45~18:30�y�p�ɬq�A�[�W20������					
		//�W�@��U���ҳѤU�i�����ɶ�
		int tempSecond=(previousone.originalDestinationTime-(previousone.originalDestinationTime%60));	
		int min = (int)((tempSecond % 3600) / 60);//��Ʀ��� 
		//�����i�δݾl���ɶ�
		int Residualtime=0;
		if(min>=30&&min<60)
			Residualtime=(60-min);
		else if(min<30&&min>=0)
			Residualtime=(30-min); 
		temptraveltime=temptraveltime-(Residualtime*60);	
		//�o��W���ҳѪ��ɶ�
		tempSecond=(tagetreq.originalStartTime-(tagetreq.originalStartTime%60));
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
		return Spendtimecount;
	}		
	//�P�_
	public boolean CheckAreaWeightsClass(int Classification,int[] Area)
	{ 
		boolean Conform=false;
		//�ˬd�ثe�_�ŦX�n��M�����O
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

