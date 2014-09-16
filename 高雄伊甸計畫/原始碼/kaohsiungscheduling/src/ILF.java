
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class ILF {
	private ResultSet rs = null;
	public int LastNumber;
	private List<TravelTimeStruct> traveltime = null;
	GoogleMapsAPI gmsapi = null;	
	private Connection con;
	private Statement smt,smt1;
	private defineVariable Variable;
	private Map<String,templatlon> templatlon= null;
	public ILF(Connection conn,defineVariable variable)
	{
		Variable=variable;
		con = conn;
		try
		{
			smt = con.createStatement();				
			smt1= con.createStatement();	
			//�ץ����g�n��
			rs = smt.executeQuery("SELECT * FROM `templatlon` WHERE 1");
			templatlon = new HashMap<String, templatlon>();	
			while(rs.next())
			{		
				templatlon TempNode= new templatlon();
				TempNode.No = rs.getInt("no");
				TempNode.Address = rs.getString("address").trim();
				TempNode.Latitude = Double.valueOf(rs.getString("latitude"));
				TempNode.Longitude = Double.valueOf(rs.getString("longitude"));	
				templatlon.put(TempNode.Address,TempNode);			
			}
			
			//�Ȧ�ɶ����v����
			rs = smt.executeQuery("SELECT * FROM traveltime");
			traveltime = new ArrayList<TravelTimeStruct>();			
			while(rs.next())
			{			
				TravelTimeStruct tempNode = new TravelTimeStruct();
				tempNode.No = rs.getInt("�ѧO�X");
				tempNode.StartAddress = rs.getString("�W���a�}").trim();
				tempNode.StartLon = Double.valueOf(rs.getString("�W���a�}�g��"));
				tempNode.StartLat = Double.valueOf(rs.getString("�W���a�}�n��"));					
				tempNode.EndAddress = rs.getString("�U���a�}").trim();
				tempNode.EndLon = Double.valueOf(rs.getString("�U���a�}�g��"));
				tempNode.EndLat = Double.valueOf(rs.getString("�U���a�}�n��"));
				tempNode.OriginTravelTime = rs.getInt("��l��q�ɶ�");				
				traveltime.add(tempNode);
			}
			LastNumber = traveltime.size();
			System.out.println("�Ȧ�ɶ���Ƽƶq: "+ LastNumber);
			rs.close();
			rs=null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		gmsapi =  new GoogleMapsAPI(Variable);					//Google maps api object
	
	}
	//����O����
	public void Freememory() throws InterruptedException, IOException
	{
		traveltime=null;
		System.gc();		
	}
	//��M���v��ơA��J�ѼƬ�input: �_�W�I[�n��][�g��][�n��][�g��]�AXY:�_�W�I[X][Y][X][Y]�A�_�W�I[�a�}][�a�}]�Atime:�ɶ�(��쬰��)
	public int SearchHistory(double[] input, String[] address, int time) throws InterruptedException, IOException
	{
		double[] StartMinMaxLatLon=new double[4];
		double[] EndMinMaxLatLon=new double[4];
		address[0]=address[0].trim();
		address[1]=address[1].trim();	
		//�^�ǵ��G
		int result = -1;
		//�_�I���I�a�}�ۦP�A�^�ǮȦ�ɶ�0
		if(address[0].equals(address[1]))
		{
			return 0;
		}
		//���o�_�I200���س̤j�P�̤p�g�n��
		StartMinMaxLatLon=GetAround(input[0],input[1],150);
		//���o���I200���س̤j�P�̤p�g�n��
		EndMinMaxLatLon=GetAround(input[2],input[3],150);
		for(int i = 0; i < LastNumber; i++)
		{
			//�P�_�O�_�ŦX�d��
			if(CheckAccordAround(traveltime.get(i),StartMinMaxLatLon,EndMinMaxLatLon))
			{	if(traveltime.get(i).OriginTravelTime >= 0)
				{
					//���o�i�ήȦ�ɶ�
					result = traveltime.get(i).OriginTravelTime;	
					return result;
				}
				else
				{
					result=SearchGooglemapapi(address,input,traveltime.get(i).No);
				    return result;
				}
			}else
			{   //�p�G�g�n�ק䤣��^�k�r����
				if((traveltime.get(i).StartAddress.trim().equals(address[0].trim())&& traveltime.get(i).EndAddress.trim().equals(address[1].trim())) || (traveltime.get(i).StartAddress.trim().equals(address[1].trim()) && traveltime.get(i).EndAddress.trim().equals(address[0].trim())))
				{
					if(traveltime.get(i).OriginTravelTime >= 0)
					{
						//���o�i�ήȦ�ɶ�
						result = traveltime.get(i).OriginTravelTime;	
						return result;
					}
				}
			}
		}
		//���v��Ƨ䤣���google
		if(result >= 0)
		{
			return result;
		}
		else
		{
			result=SearchGooglemapapi(address,input,-1);
			return result;
		}
	}
	//��M���v�g�n��
	public double[]  SearchLatLonHistory(String address) 
	{
		 double[] ReturnValue = {-1.0, -1.0};
		//�d�߾��v�g�n�װT��		
		if(templatlon.get(address.trim())!=null)
		{
			templatlon TempNode= new templatlon();
			TempNode=templatlon.get(address.trim());
			ReturnValue[0]  = TempNode.Latitude;
			ReturnValue[1]  =TempNode.Longitude;				
		}
		else
		{
			//�d�ߤ�����v�g�n�״N�h�dgoogle		
			ReturnValue=gmsapi.GeocodingAPI(address.trim());
			if(ReturnValue[0]!=-1||ReturnValue[1]!=-1)
				AddTempLatLon(address.trim(),ReturnValue);
		}
		return ReturnValue;
	}
	//��M���v��ơA�ѼƬ��ɶ��r��A�_�I�a�}�r��A���I�a�}�r��
	public int SearchHistory(RequestTable reqtable,defineVariable variable) throws Exception 
	{
		boolean isfound = false;
		int result = -1;
	    double[] ReturnValue = {-1.0, -1.0};
	    int traveltimeindex=-1;
		try
		{
			//�_�l�I�g�n��
			ReturnValue=SearchLatLonHistory(reqtable.OriginAddress.trim());
			//���X�g�n��
			reqtable.OriginLat = ReturnValue[0];
			reqtable.OriginLon = ReturnValue[1];		
			if(reqtable.OriginLat ==-1||reqtable.OriginLon ==-1)
				variable.errorcode=-17;
			}catch(Exception e)
			{
				variable.errorcode=-17;
				//e.printStackTrace();
			}
		//�_�l�I�g�n��
		try
		{
			ReturnValue=SearchLatLonHistory(reqtable.DestinationAddress.trim());
			reqtable.DestinationLat = ReturnValue[0];
			reqtable.DestinationLon =ReturnValue[1];	
			if(reqtable.DestinationLat ==-1||reqtable.DestinationLon ==-1)
				variable.errorcode=-17;
		}catch(Exception e)
		{
			variable.errorcode=-17;
			//e.printStackTrace();
		}
		for(int i = 0 ; i < LastNumber; i++)
		{			
			if((traveltime.get(i).StartAddress.trim().equals(reqtable.OriginAddress.trim())&& traveltime.get(i).EndAddress.trim().equals(reqtable.DestinationAddress.trim())) || (traveltime.get(i).StartAddress.trim().equals(reqtable.DestinationAddress.trim()) && traveltime.get(i).EndAddress.trim().equals(reqtable.OriginAddress.trim())))
			{
				//��Ʈw������q�ɶ��O�i�Ϊ�
				if(traveltime.get(i).OriginTravelTime >= 0)
				{
					reqtable.TravelTime = traveltime.get(i).OriginTravelTime;
					reqtable.DestinationTime = reqtable.TravelTime + reqtable.OriginTime;
					isfound = true;	//�����M
					traveltimeindex= traveltime.get(i).No;
					break;
				}
				else
				{
					String[] travelTimeResult = gmsapi.DirectionAPI(reqtable.OriginAddress, reqtable.DestinationAddress);
					if(travelTimeResult[0].equals("success"))
					{
						reqtable.TravelTime = Integer.valueOf(travelTimeResult[5]);
						reqtable.DestinationTime = reqtable.TravelTime + reqtable.OriginTime;
						try
						{
							smt.executeUpdate("update traveltime set ��l��q�ɶ� = " + travelTimeResult[5] + ", �ץ���q�ɶ� = " + reqtable.TravelTime + " where �ѧO�X = " + traveltime.get(i).No);
							traveltimeindex= traveltime.get(i).No;
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
						isfound = true;//����j�M
						break;
					}
					else
					{
						int errorcode=0;
						switch(travelTimeResult[1])
						{
						//�w�W�L����t�B
						case "OVER_QUERY_LIMIT":
						{
							//�b����@���P�_�O�_�O�u���t�B�Χ�
							//������1��
							Thread.sleep(1000);
							travelTimeResult = gmsapi.DirectionAPI(reqtable.OriginAddress, reqtable.DestinationAddress);
							if(travelTimeResult[0].equals("success"))
							{
								reqtable.TravelTime = Integer.valueOf(travelTimeResult[5]);								
								reqtable.DestinationTime = reqtable.TravelTime + reqtable.OriginTime;
								try
								{
									smt.executeUpdate("update traveltime set ��l��q�ɶ� = " + travelTimeResult[5] + ", �ץ���q�ɶ� = " + reqtable.TravelTime + " where �ѧO�X = " + traveltime.get(i).No);
									traveltimeindex= traveltime.get(i).No;
								}
								catch(Exception e)
								{
									e.printStackTrace();
								}
								isfound = true;//����j�M
								break;
							}else
							{
								//�p�G�٬OOVER_QUERY_LIMIT�N�O�t�B�Χ�
								errorcode=-2;
							}
						}
						break;
						//�n�D�w�D�ڵ�
						case "REQUEST_DENIED":
							errorcode=-3;
							break;
						//���s�b��address
						case "ZERO_RESULTS":
							errorcode=-4;
							break;
						//�d��(address��latlng)�򥢤F
						case "INVALID_REQUEST":
							errorcode=-5;
							break;	
						}		
						if(errorcode<-2)
						{
							reqtable.TravelTime = errorcode;//travelTimeResult[0];//dti.TimeInterval(hour, min, travelTimeResult[0]);
							reqtable.DestinationTime =errorcode;//reqtable.TravelTime + reqtable.OriginTime;
						}
						isfound = true;//����j�M
						break;
					}
				}
			}			
		}
		if(!isfound)
		{
			if(result == -1||(reqtable.OriginLat==-1.0))
			{
				String[] travelTimeResult = gmsapi.DirectionAPI(reqtable.OriginAddress, reqtable.DestinationAddress);
				if(travelTimeResult[0].equals("success"))
				{
					reqtable.TravelTime = Integer.valueOf(travelTimeResult[5]);//dti.TimeInterval(hour, min, travelTimeResult[0]);
					reqtable.DestinationTime = reqtable.TravelTime + reqtable.OriginTime;		
					traveltimeindex=AddEntity(reqtable.TravelTime, reqtable.OriginAddress, reqtable.DestinationAddress, reqtable); ;
				}else
				{
					int errorcode=0;
					switch(travelTimeResult[1])
					{
					//�w�W�L����t�B
					case "OVER_QUERY_LIMIT":
					{
						//�b����@���P�_�O�_�O�u���t�B�Χ�
						//������1��
						Thread.sleep(1000);
						travelTimeResult = gmsapi.DirectionAPI(reqtable.OriginAddress, reqtable.DestinationAddress);
						if(travelTimeResult[0].equals("success"))
						{
							reqtable.TravelTime = Integer.valueOf(travelTimeResult[5]);//dti.TimeInterval(hour, min, travelTimeResult[0]);
							reqtable.DestinationTime = reqtable.TravelTime + reqtable.OriginTime;
							traveltimeindex=AddEntity(reqtable.TravelTime, reqtable.OriginAddress, reqtable.DestinationAddress, reqtable);
						}else
						{
							//�p�G�٬OOVER_QUERY_LIMIT�N�O�t�B�Χ�
							errorcode=-2;
						}
					}
					break;
					//�n�D�w�D�ڵ�
					case "REQUEST_DENIED":
						errorcode=-3;
						break;
					//���s�b��address
					case "ZERO_RESULTS":
						errorcode=-4;
						break;
					//�d��(address��latlng)�򥢤F
					case "INVALID_REQUEST":
						errorcode=-5;
						break;	
					}		
					if(errorcode<=-2)
					{
						reqtable.TravelTime = errorcode;//travelTimeResult[0];//dti.TimeInterval(hour, min, travelTimeResult[0]);
						reqtable.DestinationTime =errorcode;//reqtable.TravelTime + reqtable.OriginTime;
					}
				}
			}
		}		
		return traveltimeindex;
	}		
	public void AddTempLatLon(String address,double[] latlonvalue)
	{
		templatlon TempNode= new templatlon();
		try
		{	
			//��s���Ʈw
			String sqlQuery = "INSERT INTO `templatlon`(`address`, `latitude`, `longitude`) VALUES ('";
			sqlQuery  = sqlQuery +  address.trim()+ "'," + latlonvalue[0] + "," + latlonvalue[1]+ ")";
			smt.executeUpdate(sqlQuery);
			
			//��straveltime�ª��Ȧ�ɶ�
			sqlQuery = "UPDATE `traveltime` SET `�W���a�}�n��`="+String.valueOf(latlonvalue[0])+",`�W���a�}�g��`="+String.valueOf(latlonvalue[1])+" WHERE `�W���a�}`='"+address.trim()+"'";
			smt.executeUpdate(sqlQuery);
		
			sqlQuery = "UPDATE `traveltime` SET `�U���a�}�n��`="+String.valueOf(latlonvalue[0])+",`�U���a�}�g��`="+String.valueOf(latlonvalue[1])+" WHERE `�U���a�}`='"+address.trim()+"'";
			
			smt.executeUpdate(sqlQuery);
			ResultSet rs = null;
			rs = smt1.executeQuery("SELECT *  FROM templatlon WHERE `address` = '" + address.trim() +"'");
			
			//��s��map�O����
			if(rs.next())
			{	
				TempNode.No = rs.getInt("no");
			}
			TempNode.Address = address.trim();
			TempNode.Latitude = latlonvalue[0];
			TempNode.Longitude = latlonvalue[1]	;
			templatlon.put(TempNode.Address,TempNode);	
			
		}
		catch(Exception e)
		{
			System.out.println("�o�Ϳ��~");
			e.printStackTrace();		
		}	
	}
	
	//�ƯZ�ɪ��s�W�Ȧ���
	public void AddEntity(int[] inputtraveltime, double[] LatLon, String[] address)
	{
		try
		{
			String sqlQuery = "insert into traveltime (�W���a�}, �W���a�}�g��, �W���a�}�n��, �U���a�}, �U���a�}�g��, �U���a�}�n��, ��l��q�ɶ�) values ('";
			sqlQuery  = sqlQuery + address[0] + "','" + String.valueOf(LatLon[1]) + "','" + String.valueOf(LatLon[0]) + "','";
			sqlQuery = sqlQuery + address[1] + "','" + String.valueOf(LatLon[3]) + "','" + String.valueOf(LatLon[2]) + "',";
			sqlQuery = sqlQuery + inputtraveltime[0]+")";			
			smt.executeUpdate(sqlQuery);
			ResultSet rs = null;
			rs = smt.executeQuery("SELECT `�ѧO�X` ,  `�W���a�}` ,  `�W���a�}�g��` ,  `�W���a�}�n��` ,  `�U���a�}` ,  `�U���a�}�g��` ,  `�U���a�}�n��` ,  `��l��q�ɶ�`   FROM traveltime WHERE �W���a�} = '" + address[0] + "' AND �U���a�} = '" + address[1] + "'");
			if(rs.next())
			{		
				TravelTimeStruct tempNode = new TravelTimeStruct();				
				tempNode.No = rs.getInt("�ѧO�X");
				tempNode.StartAddress = rs.getString("�W���a�}");
				tempNode.StartLon = Double.valueOf(rs.getString("�W���a�}�g��"));
				tempNode.StartLat = Double.valueOf(rs.getString("�W���a�}�n��"));
				tempNode.EndAddress = rs.getString("�U���a�}");
				tempNode.EndLon = Double.valueOf(rs.getString("�U���a�}�g��"));
				tempNode.EndLat = Double.valueOf(rs.getString("�U���a�}�n��"));
				tempNode.OriginTravelTime = rs.getInt("��l��q�ɶ�");				
				traveltime.add(tempNode);
				LastNumber++;				
			}
			rs.close();
			rs = null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("1 add data error!!! From " + address[0] + " to " + address[1]);
		}		
	}
	
	//�פJ��Ʈw�ɪ��s�W�Ȧ��� 
	public int AddEntity(int inputtraveltime, String origin, String destination, RequestTable reqtable)	
	{
		int No=0;
		try
		{
			String sqlQuery = "insert into traveltime (�W���a�}, �W���a�}�g��, �W���a�}�n��, �U���a�}, �U���a�}�g��, �U���a�}�n��, ��l��q�ɶ�) values ('";
			sqlQuery  = sqlQuery + reqtable.OriginAddress + "','" + String.valueOf(reqtable.OriginLon) + "','" + String.valueOf(reqtable.OriginLat) + "','";
			sqlQuery = sqlQuery + reqtable.DestinationAddress + "','" + String.valueOf(reqtable.DestinationLon) + "','" + String.valueOf(reqtable.DestinationLat) + "',";
			sqlQuery = sqlQuery + inputtraveltime + ")";
			smt.executeUpdate(sqlQuery);
			ResultSet rs = null;
			rs = smt.executeQuery("SELECT `�ѧO�X` ,  `�W���a�}` ,  `�W���a�}�g��` ,  `�W���a�}�n��` ,  `�U���a�}` ,  `�U���a�}�g��` ,  `�U���a�}�n��` ,  `��l��q�ɶ�`  FROM traveltime WHERE �W���a�} = '" + reqtable.OriginAddress + "' AND �U���a�} = '" + reqtable.DestinationAddress + "'");
			if(rs.next())
			{				
				TravelTimeStruct tempNode = new TravelTimeStruct();
				tempNode.No = rs.getInt("�ѧO�X");
				tempNode.StartAddress = rs.getString("�W���a�}");
				tempNode.StartLon = Double.valueOf(rs.getString("�W���a�}�g��"));
				tempNode.StartLat = Double.valueOf(rs.getString("�W���a�}�n��"));
				tempNode.EndAddress = rs.getString("�U���a�}");
				tempNode.EndLon = Double.valueOf(rs.getString("�U���a�}�g��"));
				tempNode.EndLat = Double.valueOf(rs.getString("�U���a�}�n��"));
				tempNode.OriginTravelTime = rs.getInt("��l��q�ɶ�");			
				traveltime.add(tempNode);
				LastNumber++;
				No=tempNode.No;
			}
			rs.close();
			rs = null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("2 add data error!!! From " + origin + " to " + destination);
		}	
		return No;
	}
	//���o�g�n�׽d��
	//[�̤p�n��][�̤j�n��][�̤p�g��][�̤j�g��]
	public double[] GetAround(double latitude, double longitude, int raidusMile) 
	{
		double degree = (24901 * 1609) / 360.0;
		double dpmLat = 1 / degree;
		double radiusLat = dpmLat * raidusMile;
		double minLat = latitude - radiusLat;//�̤p�n��
	    double maxLat = latitude + radiusLat;//�̤j�n��
	    double mpdLng = degree * Math.cos(latitude * (3.14159265 / 180));
	    double dpmLng = 1 / mpdLng;
	    double radiusLng = dpmLng * raidusMile;
	    double minLng = longitude - radiusLng;//�̤p�g��
	    double maxLng = longitude + radiusLng;//�̤j�g��
	    return new double[]{minLat, maxLat,minLng, maxLng};	
	 }
	//�P�_�O�_�ŦX�g�n�׽d��
	public boolean CheckAccordAround(TravelTimeStruct Node,double[] StartMinMaxLatLon,double[] EndMinMaxLatLon) 
	{
		//�n�׬O�_���b�d��
		if(Node.StartLat>=StartMinMaxLatLon[0]&&Node.StartLat<=StartMinMaxLatLon[1])
		{
			//�P�_�g�׬O�_���b�d��
			if(Node.StartLon>=StartMinMaxLatLon[2]&&Node.StartLon<=StartMinMaxLatLon[3])
			{
				//�P�_�n�׬O�_���b�d��
				if(Node.EndLat>=EndMinMaxLatLon[0]&&Node.EndLat<=EndMinMaxLatLon[1])
				{
					//�P�_�g�׬O�_���b�d��
					if(Node.EndLon>=EndMinMaxLatLon[2]&&Node.EndLon<=EndMinMaxLatLon[3])
					{
						return true;
				    }
					else
					{
						return false;
					}
			    }
				else
				{
					return false;
				}
			}
			else
			{
				return false;
			}
		}else if(Node.EndLat>=StartMinMaxLatLon[0]&&Node.EndLat<=StartMinMaxLatLon[1])
		{
			//�P�_�g�׬O�_���b�d��
			if(Node.EndLon>=StartMinMaxLatLon[2]&&Node.EndLon<=StartMinMaxLatLon[3])
			{
				//�P�_�n�׬O�_���b�d��
				if(Node.StartLat>=EndMinMaxLatLon[0]&&Node.StartLat<=EndMinMaxLatLon[1])
				{
					//�P�_�g�׬O�_���b�d��
					if(Node.StartLon>=EndMinMaxLatLon[2]&&Node.StartLon<=EndMinMaxLatLon[3])
					{
						return true;
					}
					else
					{
						return false;
					}
				}
				else
				{
					return false;
				}
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	public int SearchGooglemapapi(String[] address,double[] input,int traveltimenum) 
	{
		int result=-1;
		//��Ʈw���s�񪺮Ȧ�ɶ���ƬO���~�����ճz�Lgoogle map���o�i�ήȦ�ɶ�
		String[] travelTimeResult;
		try {
			travelTimeResult = gmsapi.DirectionAPI(address[0].trim(), address[1].trim());
			//google map api ���\���^�i�ήȦ�ɶ�			
			if(travelTimeResult[0].equals("success"))
			{
				result = Integer.valueOf(travelTimeResult[5]);
				//�p�G�쥻��Ʈw����s��Ʈw���
				if(traveltimenum>=0)
				{
					try
					{
						smt.executeUpdate("update traveltime set ��l��q�ɶ� = " + result +" where �ѧO�X = " + traveltimenum);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					//��Ʈw�S����Ʒs�W�@��
					int[] timeinput = {result, result,-1};
					AddEntity(timeinput, input, address);
				}
				return result;
			}else
			{
				switch(travelTimeResult[1])
				{
					//�w�W�L����t�B
					case "OVER_QUERY_LIMIT":
						//�b����@���P�_�O�_�O�u���t�B�Χ�
						//������1��
						Thread.sleep(2000);
						travelTimeResult = gmsapi.DirectionAPI(address[0].trim(), address[1].trim());
						if(travelTimeResult[0].equals("success"))
						{
							if(traveltimenum>=0)
							{
								try
								{
									smt.executeUpdate("update traveltime set ��l��q�ɶ� = " + result +" where �ѧO�X = " + traveltimenum);
								}
								catch(Exception e)
								{
									e.printStackTrace();
								}
							}
							else
							{
								//��Ʈw�S����Ʒs�W�@��
								int[] timeinput = {result, result,-1};
								AddEntity(timeinput, input, address);
							}
							return result;
						}else
						{
							//�p�G�٬OOVER_QUERY_LIMIT�N�O�t�B�Χ�
							result=-2;
							System.out.println("123"+"OVER_QUERY_LIMIT");
						}
						break;
					//�n�D�w�D�ڵ�
					case "REQUEST_DENIED":
						result=-3;
						System.out.println("REQUEST_DENIED");
						break;
						//���s�b��address
					case "ZERO_RESULTS":
						result=-4;
						System.out.println("ZERO_RESULTS");
						break;
						//�d��(address��latlng)�򥢤F
					case "INVALID_REQUEST":
						result=-5;
						System.out.println("INVALID_REQUEST");
						break;	
					case "NOT_FOUND":
						result=-1;
						System.out.println("NOT_FOUND");
						break;	
						
				}		
			}
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			SearchGooglemapapi(address,input,traveltimenum);
		
		}		
		return result;
  }
}
