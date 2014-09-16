import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.TreeMap;

public class PreProcess
{
	private List<reqGroup> ReqTable;//�ݨD�w����H�W���a�Ϥ���
	private List<reqGroup> Tailreq;//���Z�ݨD�w����H�U���a�Ϥ���
	private List<carGroup> DrTable;//�q����
	private int TolerableStartTime = 0;//�Y�Z�e�Ԯɶ�
	private int TolerableEndTime = 0;//���Z���Z�e�Ԯɶ�
	private double Interval = 0.0;//���ɶ� �C��30�������@���
	private ILF ilf = null;
	private int carsize = 0;	//������
	private static defineVariable variable;//�w�q���ܼ�	
	public PreProcess(defineVariable Variable,List<reqGroup> requestTable,List<reqGroup> tailreq, List<carGroup> car,	int tolerableStartTime, int tolerableEndTime, double timeUnit,
			int size, ILF ilf2) {
		// TODO �۰ʲ��ͪ��غc�l Stub

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
	public void Start()//�Y���Z�{���i�J�I
	{
		
		int recentPercent = -1;//�����Y���Z�i��				
		int i=0,carflag=0;//�p�����諸flag
		ProgressUpdate proupdate = new ProgressUpdate();//��Ʈw��s�i�ת�	
		try
		{
			
			while(carflag<6)//�Y���ƯZ�Ĥ@���]�Z �ĤG������g�� �ĤT���S���� �ĥ|���p�� �Ĥ����@�먮��
			 {
				//�p�G�Ogoogle���o�Ϳ��~�N�פ�{��
				if(variable.errorcode<=-2)
					break;
				

				for(int j=0;j<variable.areanum;j++)//32�Ӧa��
				{ 				
					for(int l=0;l<variable.intervalnum;l++)//�ɶ��`�϶���
					{ 
						for(int k=0;k<DrTable.get(j).getCar(l).size();k++)
						{  
							int[] earaarry={-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};//�����䴩�a��
							int[] earaarry1={-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};//�D�n�䴩�a��	
							int[] earaarry2={-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};//���U�䴩�a��	
							int arraycount1=-1,arraycount=-1,arraycount2=-1;//�����D�n�򻲧U�a�Ϫ��ƶq	
							int searchrang=0;///gh marked 2012/1/11 �W�[�j�M�d��Ѽ�							
							if(DrTable.get(j).getCar(l).get(k).station.equals("����"))//����䴩���a��
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
								earaarry1[0]=2;//���U�ϰ�	
								arraycount1++;
								earaarry1[1]=4;//���U�ϰ�	
								
								
							}	
							else if(DrTable.get(j).getCar(l).get(k).station.equals("�g��"))//�g���䴩���a��
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
								for(int x=0;x<variable.areanum;x++)//�@�먮���j�M�䴩���ϰ�
								{ 
								if(defineVariable.areaWeight[defineVariable.switchareaindex(DrTable.get(j).getCar(l).get(k).station)][x]==1)
									{
										arraycount++;
										earaarry[arraycount]=x;//�S��䴩�ϰ�
										}
								if(defineVariable.areaWeight[defineVariable.switchareaindex(DrTable.get(j).getCar(l).get(k).station)][x]==2)
								{
									arraycount1++;
									earaarry1[arraycount1]=x;//�D�n�ϰ�										
								 }
								if(defineVariable.areaWeight[defineVariable.switchareaindex(DrTable.get(j).getCar(l).get(k).station)][x]==3)
								{
									arraycount2++;
									earaarry2[arraycount2]=x;//���U�ϰ�										
								 }
								}
								for(int index=0;index<=9;index++)
								{
									arraycount1++;
									if(earaarry2[index]==-1)
										break;
									earaarry1[arraycount1]=earaarry2[index];//�⻲�U�ϰ���D�n�ϰ줧��	
								}
								
								
							}
							if(carflag==0&&DrTable.get(j).getCar(l).get(k).EndTime>75600)//�]�Z��
							{
								//���Z����
								for(int index=0;index<(arraycount+arraycount1+2);index++)
								{ 
									 //�^�Ǥp�󵥩�-2�N��google�d�ߦ����ߧY�פ�{��
									 if(variable.errorcode<=-2)
										 break;
									if(!DrTable.get(j).getCar(l).get(k).EndArrange)
									{
										//����D�n�a��
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
											//���U�a��
											if(OrderEnd(DrTable.get(j).getCar(l).get(k),searchrang,earaarry1[index-arraycount-1],carflag,9))
											{ 
												DrTable.get(j).getCar(l).get(k).EndArrange=true;
												i++;	
											}
											}
										}
								}
								
								//�Y�Z//�]�Z���o��䴩�ϰ�A��]�b��]�Z�u���w����Z�p�G���M��T�a�|�y���W��
								for(int aaa=0;aaa<3;aaa++)//���`�Z���ĤG����P�ɶ����Ը� //���`�Z���ĤT���䩵��15����
								{
									//�^�Ǥp�󵥩�-2�N��google�d�ߦ����ߧY�פ�{��
									 if(variable.errorcode<=-2)
										 break;
									i=arrange(earaarry1,earaarry, i,DrTable.get(j).getCar(l).get(k), arraycount, arraycount1, searchrang, carflag,aaa);
								}
							}
							else if(carflag==3&&DrTable.get(j).getCar(l).get(k).station.equals("�g��"))//�g�����Y���Z
							{
								for(int specialcarindex=0;specialcarindex<=arraycount;specialcarindex++)
								{
									for(int specialcarsearchrang=3;specialcarsearchrang>=0;specialcarsearchrang--)
									{
										//�^�Ǥp�󵥩�-2�N��google�d�ߦ����ߧY�פ�{��
										if(variable.errorcode<=-2)
											break;
										if(!DrTable.get(j).getCar(l).get(k).StartArrange)//�P�_�O�_�w�ƤJ�Y�Z	
										{    //�i�J�S���Z�D��
											if(PreProcessspecialcar(DrTable.get(j).getCar(l).get(k),earaarry[specialcarindex],specialcarsearchrang,carflag))
											{  
												i++;
												DrTable.get(j).getCar(l).get(k).StartArrange=true;//�p�G������Y�Z�Хܬ��w�ƤJ�Y�Z
												break;
											}
										}
									}
								}
								for(int specialcarindex=0;specialcarindex<=arraycount;specialcarindex++)
								{
									//�^�Ǥp�󵥩�-2�N��google�d�ߦ����ߧY�פ�{��
									 if(variable.errorcode<=-2)
										 break;
									if(!DrTable.get(j).getCar(l).get(k).EndArrange)//�P�_�O�_�w�ƤJ���Z
										{
										if(specialcarindex<=arraycount)
										{   //�߯Z�D��
											if(OrderEnd(DrTable.get(j).getCar(l).get(k),searchrang,earaarry[specialcarindex],carflag,9))
											{ 
												DrTable.get(j).getCar(l).get(k).EndArrange=true;//�p�G�������Z�Хܬ��w�ƤJ���Z
												i++;
												break;
												}
											}
										}
								}
							}
							else if(carflag==2&&DrTable.get(j).getCar(l).get(k).station.equals("����"))//������Y���Z
							{
								for(int specialcarindex=0;specialcarindex<=arraycount;specialcarindex++)
								{
									for(int specialcarsearchrang=3;specialcarsearchrang>=0;specialcarsearchrang--)
									{
										//�^�Ǥp�󵥩�-2�N��google�d�ߦ����ߧY�פ�{��
										if(variable.errorcode<=-2)
											break;
										if(!DrTable.get(j).getCar(l).get(k).StartArrange)//�P�_�O�_�w�ƤJ�Y�Z	
										{   //�i�J�S���Z�D��
											if(PreProcessspecialcar(DrTable.get(j).getCar(l).get(k),earaarry[specialcarindex],specialcarsearchrang,carflag))
											{  
												i++;
												DrTable.get(j).getCar(l).get(k).StartArrange=true;//�p�G������Y�Z�Хܬ��w�ƤJ�Y�Z
												break;
											}
										}
									}
								  }
								for(int specialcarindex=0;specialcarindex<=arraycount;specialcarindex++)
								{
									//�^�Ǥp�󵥩�-2�N��google�d�ߦ����ߧY�פ�{��
									 if(variable.errorcode<=-2)
										 break;
									if(!DrTable.get(j).getCar(l).get(k).EndArrange)//�P�_�O�_�w�ƤJ���Z
										{
										if(specialcarindex<=arraycount)
										{   //�߯Z�D��
											if(OrderEnd(DrTable.get(j).getCar(l).get(k),searchrang,earaarry[specialcarindex],carflag,9))
											{ 
												DrTable.get(j).getCar(l).get(k).EndArrange=true;//�p�G�������Z�Хܬ��w�ƤJ���Z
												i++;
												break;
												}
											}
										}
								}
							}
							//�|�j�����S��䴩  
							else if(carflag==1)							
							{ 
								for(int index=0;index<variable.Zhonghespecialcar.size();index++)
									if(DrTable.get(j).getCar(l).get(k).ID.indexOf(variable.Zhonghespecialcar.get(index))!=-1)
									{
										//�^�Ǥp�󵥩�-2�N��google�d�ߦ����ߧY�פ�{��
										if(variable.errorcode<=-2)
											break;
										//�Y�Z
										i=arrange(earaarry, earaarry1, i,DrTable.get(j).getCar(l).get(k), arraycount, arraycount1, searchrang, carflag,4);
										//���Z
										i=arrange1(earaarry, earaarry1, i,DrTable.get(j).getCar(l).get(k), arraycount, arraycount1, searchrang, carflag,4);
									}
							}//�p��
							else if(carflag==4&&DrTable.get(j).getCar(l).get(k).Car.equals("�p��"))							
							{   
								//�^�Ǥp�󵥩�-2�N��google�d�ߦ����ߧY�פ�{��
								 if(variable.errorcode<=-2)
									 break;
								//�Y�Z
								i=arrange(earaarry, earaarry1, i,DrTable.get(j).getCar(l).get(k), arraycount, arraycount1, searchrang, carflag,4);
								//���Z
								i=arrange1(earaarry, earaarry1, i,DrTable.get(j).getCar(l).get(k), arraycount, arraycount1, searchrang, carflag,4);
							}//�@��j��
							else if(carflag>=5)							
							{   
								//�Y�Z
								for(int aaa=0;aaa<3;aaa++)//���`�Z���ĤG����P�ɶ����Ը� //���`�Z���ĤT���䩵��15����
								{
									//�^�Ǥp�󵥩�-2�N��google�d�ߦ����ߧY�פ�{��
									 if(variable.errorcode<=-2)
										 break;
									i=arrange(earaarry, earaarry1, i,DrTable.get(j).getCar(l).get(k), arraycount, arraycount1, searchrang, carflag,aaa);
								}
								//���Z
								for(int aaa=0;aaa<3;aaa++)//�Ĥ@���䴣�e15���� ���`�Z���ĤG����P�ɶ� �ĤT����P�ɶ����Ը�
								{
									//�^�Ǥp�󵥩�-2�N��google�d�ߦ����ߧY�פ�{��
									 if(variable.errorcode<=-2)
										 break;
									i=arrange1(earaarry, earaarry1, i,DrTable.get(j).getCar(l).get(k), arraycount, arraycount1, searchrang, carflag,aaa);
								}
								
							}
							/**************************�@�릭�߯Z���D��********************************************/
							recentPercent =(int)(((float)i/(carsize*2))*100); 
							proupdate.updatedatabase(2, recentPercent,variable.date,variable.time); //��s�i�ת�
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
	/************************�S���Z�D��*********************************/
	private boolean PreProcessspecialcar(DriverTable Node,int carindex,int searchrang,int specialcar) throws Exception
	{	// Node:�q����T  carindex:�ثe�ҷj�M�a��  searchrang:�j�M�d��  specialcar:�S��Z��flag�p�G��1�N�ק��Ʈw�ɶ�	
		double[] input = new double[4];//�����g�n��
		int[] XY = new int[4];//����XY��
		String[] address = new String[2];//�����d�ߨ��I���a�}		
		input[0] = Node.Lat;
		input[1] = Node.Lon;
		XY[0] = Node.X;
		XY[1] = Node.Y;
		boolean find=false,flag=false;//find�P�_�O�_�������� flag����ؿ��
		address[0] = Node.Address;	
		int starttime = Node.StartTime + TolerableStartTime;//�Y�Z�ɶ�		
		int startimeinx=((int)(starttime)/1800)-searchrang;//�Y�Z�ɶ����e�@��
		int Endtimeindex=((int)(starttime)/1800)+searchrang;//�Y�Z�ɶ����e�@��
		if(startimeinx<0)
			startimeinx=0;
		
		 for(int l=startimeinx;l<=Endtimeindex;l++)//�j�M�ŦX�ɶ��������l
		 { 
			
		  for(int k=0;k<ReqTable.get(carindex).getreq(l).size();k++)
			 {
			 if(ReqTable.get(carindex).getreq(l).get(k).Car.equals(""))//�j�����w���̥i�H��ܤj�p��
				 flag=true;
			 else
			 {
				 if(ReqTable.get(carindex).getreq(l).get(k).Car.equals(Node.Car))//�p�G�O�p�����w���̥u���ܤp�������j��
					 flag=true;
				 }	
			 //�P�_�O�_�w�ƹL�Z �P�_�O�_���A�����` �Ȧ�ɶ��n���� �O�_�ŦX����
			
			 if((starttime-ReqTable.get(carindex).getreq(l).get(k).OriginTime)<=4500&&ReqTable.get(carindex).getreq(l).get(k).Arrange == false
					 && ReqTable.get(carindex).getreq(l).get(k).TravelTime > -1&& flag==true&&ReqTable.get(carindex).getreq(l).get(k).OriginTime<32400)
			 {
				//�����N���ƤJ
				 if(starttime!=ReqTable.get(carindex).getreq(l).get(k).OriginTime)
				 {	 
					 //�p�G���Y�Z�ɶ����@�˭ק��Y�Z�ɶ��P���Z�ɶ�
					 Node.StartTime=ReqTable.get(carindex).getreq(l).get(k).OriginTime-1800;
					 Node.EndTime=Node.StartTime+32400;					
				  }
				
				 //�g�J��Ʈw
				 Node.Greedyflag=true;	
				 writedatabase(ReqTable.get(carindex).getreq(l).get(k),Node,1,1);
				 find=true;//�����N�פ�
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
	//���Y�Z
	//gh marked 2012/1/11 �W�[�j�M�d��Ѽ�
	//private void OrderStart(int NodeNumber) throws IOException
	private boolean OrderStart(DriverTable Node,int searchrang,int carindex,int carflag,int aaa) throws Exception
	{		
		// Node:�q����T  carindex:�ثe�ҷj�M�a��  searchrang:�j�M�d��  specialcar:�S��Z��flag�p�G��1�N�ק��Ʈw�ɶ�
		int starttime = Node.StartTime + TolerableStartTime;//�Y�Z�ɶ�
		int ilfReturn = -1;			
		TreeMap<Integer,RequestTable> Processmap1 = new TreeMap<Integer, RequestTable>();//���Y�Z�ɶ��̼˪����l���X	
		TreeMap<Integer, RequestTable> Processmap2 = new TreeMap<Integer, RequestTable>();//���Y�Z�ɶ���15�������l���X					
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
		
		int startimeinx=(int)(starttime)/1800;//�Y�Z�j�M�_�l�϶�			
		//int temstarttime=0;//�����S��Z�����X�Ԯɶ��p�G�S���Z���٭��l�X�Ԯɶ�	
		int latertime=starttime;
		int Candidateflag=1;//���`�Z���Ĥ@����P�ɶ�
		if(aaa==2)
		{
			Candidateflag=2;
		}
		if(aaa==1)//���`�Z���ĤG���䩵��15����
		{
			latertime=starttime+900;//���\�Y�Z��15�����X�Z	
		}
		
		for(int l=startimeinx;l<=startimeinx+2;l++)//�j�M�ŦX�ɶ��϶������l
			 { 
			 //�^�Ǥp�󵥩�-2�N��google�d�ߦ���
			 if(variable.errorcode<=-2)
				 break;
			 
			 for(int k=0;k<ReqTable.get(carindex).getreq(l).size();k++)
				 {
				 if(ReqTable.get(carindex).getreq(l).get(k).Car.equals(""))//�j�����w���̥i�H��ܤj�p��
					 flag=true;
				 else
				 {
					 if(ReqTable.get(carindex).getreq(l).get(k).Car.equals(Node.Car))//�p�G�O�p�����w���̥u���ܤp�������j��
						 flag=true;
					 }	
				 int oncartime=0;				
				 //�P�_�O�_�w�ƹL�Z �P�_�O�_���A�����` �Ȧ�ɶ��n���� �O�_�ŦX���� ���o���Y�Z�ɶ��� ���o���Y�Z�߶W�L15��
			
				 if((ReqTable.get(carindex).getreq(l).get(k).OriginTime>=starttime)&&ReqTable.get(carindex).getreq(l).get(k).OriginTime<=latertime&&ReqTable.get(carindex).getreq(l).get(k).OriginTime > Node.StartTime&& ReqTable.get(carindex).getreq(l).get(k).Arrange == false
						 && ReqTable.get(carindex).getreq(l).get(k).Status == Candidateflag && ReqTable.get(carindex).getreq(l).get(k).TravelTime > -1&& flag==true)
				 {  
					 input[2] = ReqTable.get(carindex).getreq(l).get(k).OriginLat;
					 input[3]  = ReqTable.get(carindex).getreq(l).get(k).OriginLon;					
					 address[1] = ReqTable.get(carindex).getreq(l).get(k).OriginAddress;
					 ilfReturn = ilf.SearchHistory(input, address, Node.StartTime);	//���t��W���a�I���Ȧ�ɶ�				
					//�^�Ǥp�󵥩�-2�N��google�d�ߦ����ߧY�פ�{��
					 if(ilfReturn<=-2)
					 {
						 variable.errorcode=ilfReturn;
						 break;
					 }
					 
					 //�w���̤W�U���ɶ����b���W7:30~8:30�y�p�ɬq�A�[�W15������
					 if(((Node.StartTime+ilfReturn)>=27000&&(Node.StartTime+ilfReturn)<=31200))
					 {
						 oncartime=oncartime+variable.morningpeaktime;
					 }
					 if(carindex==1&&Node.station.equals("���M")&&(oncartime+ilfReturn)>(1200+defineVariable.map_Revise_Traveltime))
						 continue;//�p�G���M�䴩�s���Ȧ�ɶ��[�y�p�ɨ�W�L20�����ƤJ					 
					 if(ilfReturn > -1)
					 {  					
						 if(((Node.StartTime + ilfReturn+oncartime)-ReqTable.get(carindex).getreq(l).get(k).OriginTime)<=defineVariable.map_Revise_Traveltime)//�P�_�Ӥ��Ӫ��α���w����	
							 {								
							 if(ReqTable.get(carindex).getreq(l).get(k).OriginTime ==starttime)//��q���Y�Z�ɶ��̼�
							 {
								 Processmap1.put((ReqTable.get(carindex).getreq(l).get(k).TravelTime+ilfReturn),ReqTable.get(carindex).getreq(l).get(k));									
							 }
							 else//��q���Y�Z�ɶ���
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
		if(!Processmap1.isEmpty())//�Ǯ��Y�Z����H�Ȧ�ɶ��̪�����
		{
			key=Processmap1.lastKey();
			Target=Processmap1.get(key);					
		}
		else if(!Processmap2.isEmpty())//��q����15��
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
			if(Node.station.equals("���M"))
			if(Target.Originarea.indexOf("�s�_���T�l��")!=-1||Target.Originarea.indexOf("�s�_���a�q��")!=-1||Target.Destinationarea.indexOf("�s�_���T�l��")!=-1||Target.Destinationarea.indexOf("�s�_���a�q��")!=-1)
			{
					Node.EndTime=Node.EndTime-1800;//���䴩�T�l�a�q�����M���������b�p�ɤU�Z
					writetime=1;
			}
			if(((Target.OriginTime-starttime)<900))//�p�G���߱���w���� �߯Z���o�A��15��
			{				
				  Node.Greedyflag=true;						
			}
			writedatabase(Target,Node,writetime,1);//�g�J��Ʈw
			return true;
		}
		else
		{
			return false;
		}
		
	}
	//�Ƨ��Z
		//gh marked 2012/1/11 �W�[�j�M�d��Ѽ�
		//private void OrderEnd(int NodeNumber) throws IOException	
		private boolean OrderEnd(DriverTable Node,int searchrang,int carindex,int specialcar,int aaa) throws Exception
		{
			
			int endtime = Node.EndTime + TolerableEndTime;//�߯Z�ɶ�:�X�Ԯɶ�+45��				
			int ilfReturn = -1;			
			TreeMap<Integer, RequestTable> Processmap1 = new TreeMap<Integer, RequestTable>();	
			TreeMap<Integer, RequestTable> Processmap2 = new TreeMap<Integer, RequestTable>();
			double[] input = new double[4];
			int[] XY = new int[4];
			String[] address = new String[2];
			boolean flag = false;//����flag
			int writettime=0;
			input[2] = Node.Lat;
			input[3] = Node.Lon;
			XY[2] = Node.X;
			XY[3] = Node.Y;
			address[1] = Node.Address;		
			int Candidateflag=1;
			int minendtime=endtime;//���\����ɶ�
			
			/*if(aaa==0)	//�Ĥ@����15��������
			{
			minendtime=endtime-900;
			}*/
			
		    minendtime=endtime-900;	
		    
			if(aaa==2)//�ĤT������
			{
				Candidateflag=2;
			}
			//�p�G���Z�b�T�l�a�q�n�h�[30���Ȧ�ɶ�
			if(Node.station.equals("���M")&&(carindex==6||carindex==7||carindex==11))
			{
				endtime=endtime-1800;
				writettime=1;
			}
			int timeinx=(int)(endtime)/1800;	//�߯Z�_�l�϶�
		
			for(int l=timeinx;l>=timeinx-1;l--)//�j�M�ŦX�ɶ��϶������l
			{ 
				 //�^�Ǥp�󵥩�-2�N��google�d�ߦ����ߧY�פ�{��
				 if(variable.errorcode<=-2)
					 break;
				for(int k=Tailreq.get(carindex).getreq(l).size()-1;k>=0;k--)
				{ 		
					int oncartime=0;
					if(Tailreq.get(carindex).getreq(l).get(k).Car.equals(""))//�j�����w���̥i�H��ܤj�p��
								flag=true;
							else
							{
								if(Tailreq.get(carindex).getreq(l).get(k).Car.equals(Node.Car))//�p�G�O�p�����w���̥u���ܤp�������j��
									flag=true;
							}
						//�P�_�O�_�w�ƹL�Z �P�_�O�_���A�����` �Ȧ�ɶ��n���� �O�_�ŦX���� ���o��߯Z�ɶ��� ���o��߯Z���W�L15��
						if(Tailreq.get(carindex).getreq(l).get(k).OriginTime<=endtime
								&&(Tailreq.get(carindex).getreq(l).get(k).OriginTime>=minendtime)&&Tailreq.get(carindex).getreq(l).get(k).Status == Candidateflag
								&& Tailreq.get(carindex).getreq(l).get(k).Arrange == false&& Tailreq.get(carindex).getreq(l).get(k).TravelTime != -1&& flag==true)
						 {	
							 input[0] = Tailreq.get(carindex).getreq(l).get(k).DestinationLat;
							 input[1] = Tailreq.get(carindex).getreq(l).get(k).DestinationLon;
							 address[0] = Tailreq.get(carindex).getreq(l).get(k).DestinationAddress;								
							 ilfReturn = ilf.SearchHistory(input, address,Tailreq.get(carindex).getreq(l).get(k).DestinationTime);
							//�^�Ǥp�󵥩�-2�N��google�d�ߦ��� �ߧY�פ�{��
							if(ilfReturn<=-2)
							{
								 variable.errorcode=ilfReturn;
								 break;
							}
							 //�w���̤U���ɶ���^�t���b�U��16:45~18:30�y�p�ɬq�A�[�W20������
							 if(((Tailreq.get(carindex).getreq(l).get(k).DestinationTime+ilfReturn)>=60300&&(Tailreq.get(carindex).getreq(l).get(k).DestinationTime+ilfReturn)<=66600)
									 ||((Tailreq.get(carindex).getreq(l).get(k).DestinationTime)>=60300&&(Tailreq.get(carindex).getreq(l).get(k).DestinationTime)<=66600))
							 {
								 oncartime+=variable.afternoonpeaktime;
							 }
							 if(carindex==1&&Node.station.equals("���M")&&(oncartime+ilfReturn)>(1200+defineVariable.map_Revise_Traveltime))
								 continue;//�p�G���M�䴩�s���Ȧ�ɶ��[�y�p�ɨ�W�L20�����ƤJ							
							 //�Ȧ�ɶ��[�y�p�ɨ褣�o�W�L45��
							 if(ilfReturn > -1&&(ilfReturn+oncartime)<=(2700+defineVariable.map_Revise_Traveltime))
								{
								
								 if((Tailreq.get(carindex).getreq(l).get(k).DestinationTime+ilfReturn+oncartime)<(endtime+variable.tolerablebacktime))//�P�_�Ӥ��Ӫ��Φ^�t
									{										 
									 if(Tailreq.get(carindex).getreq(l).get(k).OriginTime ==endtime)//��q���߯Z�ɶ��@��
									 {										
									 	 Processmap1.put(ilfReturn,Tailreq.get(carindex).getreq(l).get(k));											
									 }
									 else//��q���߯Z�ɶ���
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
					Node.EndTime=Target.OriginTime-2700;//�߯Z�վ�ɶ�
					Node.StartTime=Node.EndTime-32400;
					writettime=1;
					break;
				
				}
				
				writedatabase(Target,Node,writettime,2);//�g�J��Ʈw 2�N��g�J�߯Z
				return true;
			}
			else
			{   
				/*if(specialcar==1)
				{
				 Node.EndTime=Node.EndTime;//�S�䴩�T�l�a�q�����M�����٭�X�Ԯɶ�
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
						", `���~�I1`='"+Order[0]+"', `���~�I2`='"+Order[1]+"' WHERE `date`= '" + variable.date + "' AND arrangetime = '" + variable.time+"' AND `AssignSharing`='"+reqinfo+"'";
			    variable.smt.executeUpdate(sql);			   
				return true;
			 }else
			 {
				return false;
			 }
			
		}
		private void writedatabase(RequestTable Target,DriverTable Node,int specialcar,int mode) throws IOException, InterruptedException, Exception
		{

			int IntervalSec = (int)(Interval * 3600);//���ɶ��ഫ���� 0.5�p���ഫ��1800��
			int StartInterval =Target.OriginTime / IntervalSec;//�p��w���̰_�l�϶�
			int EndInterval = ((Target.DestinationTime % IntervalSec)  > 0 ? (Target.DestinationTime / IntervalSec) : (Target.DestinationTime / IntervalSec) - 1);//�p��w���̵����϶�
		    
			
			String worktime=null;//��W�Z�ɶ��ഫ���r��
		    String[] run=new String[2];
		    String hour=null,min=null,hour1=null,min1=null;		
		    String sql;
		    String reqinfo=null;
		    switch(mode)
			{
				case 1:
					Node.StartArrange=true;//2011/1/11 �s�W�аO�w�Ʀ��Z
					run[0]="run1";
					run[1]="user1";
					break;
				case 2:
					Node.EndArrange=true;//2011/1/11 �s�W�аO�w�Ʊ߯Z
					run[0]="run2";
					run[1]="user2";
					break;
			}
		    Node.ArrangedCount++;//�q���w�ƤJ���w���̼�+1
		    
			//���w�@��		  
			if(Target.AssignSharing==-1)
		    {
			  Target.Targetdrivers=Node.ID.trim();
			  for(int j = StartInterval; j <= EndInterval; j++)
			  {
					Node.TimeInterval[j] = String.valueOf(Target.Number);
			  }
			  reqinfo=String.valueOf(Target.Number);
			  /********************************��s��Ʈw�w���̪��ƯZ���A�H�Χ�s�q���Z��***************************************************************/
			    sql = "UPDATE userrequests SET arranged = 1 ,Targetdrivers='"+Target.Targetdrivers+"' WHERE �ѧO�X = '" + Target.Number + "' AND arrangedate = '" + variable.date +"' AND arrangetime = '" + variable.time + "'";
				variable.smt.executeUpdate(sql);	
		    }
			else
			{ 
			  Target.Targetdrivers=Node.ID.trim();
			  //��X�P��e�w���̦@�����t�@�ӹw����			  
			  RequestTable SharingRequest=new RequestTable();
			  boolean found=false;
			  for(int area=0;area<variable.areanum;area++)//32�Ӧa��
			  { 	
				  for(int timeindex=0;timeindex<variable.intervalnum;timeindex++)//�ɶ��`�϶���
				  { 
					  for(int k=0;k<ReqTable.get(area).getreq(timeindex).size();k++)
					  { 
						  if(Target.AssignSharing==ReqTable.get(area).getreq(timeindex).get(k).Number)
							{
								SharingRequest=ReqTable.get(area).getreq(timeindex).get(k);
								//�аO���w�ƯZ
								ReqTable.get(area).getreq(timeindex).get(k).Arrange = true;	
								SharingRequest.Targetdrivers=Node.ID.trim();
								/********************************��s��Ʈw�w���̪��ƯZ���A�H�Χ�s�q���Z��***************************************************************/
							    sql = "UPDATE userrequests SET arranged = 1 ,Targetdrivers='"+Target.Targetdrivers+"' WHERE �ѧO�X = '" + Target.Number + "' AND arrangedate = '" + variable.date +"' AND arrangetime = '" + variable.time + "'";
								variable.smt.executeUpdate(sql);	
							    sql = "UPDATE userrequests SET arranged = 1 ,Targetdrivers='"+SharingRequest.Targetdrivers+"' WHERE �ѧO�X = '" + SharingRequest.Number + "' AND arrangedate = '" + variable.date +"' AND arrangetime = '" + variable.time + "'";
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
			
			  /********************************��s��Ʈw�w���̪��ƯZ���A�H�Χ�s�q���Z��***************************************************************/
			sql = "UPDATE arrangedtable SET "+run[0]+"="+StartInterval+" ,"+run[1]+"='"+reqinfo+"' WHERE date = '" + variable.date + "' AND arrangetime = '" + variable.time + "' AND carid = '" +Node.ID + "'";
			variable.smt.executeUpdate(sql);	
			Target.Arrange = true;	
			
			/*************************�N�S������ʨ쪺�W�Z�ɶ��g�^��Ʈw***********************************/
				if(specialcar==1)
				{
					hour=settime((Node.StartTime/3600));
					min=settime(((Node.StartTime % 3600) / 60));
					hour1=settime((Node.EndTime/3600));
					min1=settime(((Node.EndTime % 3600) / 60));				
					worktime=hour+":"+min+"~"+hour1+":"+min1;
					sql = "UPDATE availablecars SET �ɬq='"+worktime+"' WHERE `date`= '" + variable.date + "' AND `time`= '" + variable.time + "' AND `����`='" +Node.ID + "'";
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
			//���Z
			for(int index=0;index<(arraycount+arraycount1+2);index++)
			{ 
				if(!Node.StartArrange)
				{
					if(index<=arraycount)//����D�n�a��
					{ 
						if(OrderStart(Node,searchrang,earaarry[index],specialcar,aaa))
						{
							i++;
							Node.StartArrange=true;
							
					}
						}
					else//���U�a��
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
			//�߯Z
			for(int index=0;index<(arraycount+arraycount1+2);index++)
			{ 
				if(!Node.EndArrange)
				{//����D�n�a��
					
					if(index<=arraycount)
					{  
						if(OrderEnd(Node,searchrang,earaarry[index],specialcar,aaa))
						{ 
							Node.EndArrange=true;
							i++;									
						}
				}
				else 
				{//���U�a��
					
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
        //�S��Z���M��
		/*private int arrange1(int[] earaarry,int i,DriverTable Node,int arraycount,int searchrang,int specialcar,int aaa) throws Exception
		{			
			//�߯Z
			for(int index=0;index<(arraycount);index++)
			{ 
				if(!Node.EndArrange)
				{//����D�n�a�Ϧb��//���U�a��
					
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