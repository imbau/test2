import java.io.*;
import java.net.*;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import javax.xml.xpath.*;;

public class GoogleMapsAPI 
{
	public XPathFactory factory;	//
	public XPath xpath;	
	private defineVariable Variable;
	public GoogleMapsAPI(defineVariable variable)
	{
		factory = XPathFactory.newInstance();
		xpath = factory.newXPath();
		Variable=variable;
	}
	public static int ZIPCode(String area,Map<String, Integer>  Area1, int[][] AreaWeight)
	{
		int ZIPCode=1;
		ZIPCode=AreaWeight[Area1.get(area)-1][0];		
		return ZIPCode;
	}
	public String[] DirectionAPI(String start, String end) throws InterruptedException
	{
		 
			String prefixURL = "http://maps.googleapis.com/maps/api/directions/json?";	//http request url prefix output format is xml
			String posfixURL = "&mode=driving&sensor=false&avoid=highways";								//http request url posfix mode is driving and no use sensor
			String[] Result ={"null","null","null","null","null","null"};
			try
			{
				//start=Variable.deleteCommon(start,Variable.Area);
				String originAddress = "origin=" +ZIPCode(start.substring(0, 6),Variable.Area,Variable.AreaWeight)+ URLEncoder.encode(start, "UTF-8");				//distance matrix api's parameter construct: origin address
				//end=Variable.deleteCommon(end,Variable.Area);
				String destiAddress = "&destination="+ZIPCode(end.substring(0, 6),Variable.Area,Variable.AreaWeight)+ URLEncoder.encode(end, "UTF-8");		//distance matrix api's parameter construct: destination address
				String urlString = prefixURL + originAddress + destiAddress + posfixURL;
				URL url = new URL(urlString);
				HttpURLConnection connection = (HttpURLConnection)url.openConnection();
				StringBuilder builder = new StringBuilder();
				if(connection.getResponseCode() == HttpURLConnection.HTTP_OK)
				{
					BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
					String strLine = null;
					
					while((strLine = reader.readLine()) != null)
					{
						builder.append(strLine);
					}
				}
				JSONObject json = new JSONObject(builder.toString());
				if(json.getString("status").equals("OK"))
				{
					Result[0] = "success";
					JSONArray routesArray = json.getJSONArray("routes");
					JSONObject route = routesArray.getJSONObject(0);
					JSONArray legs = route.getJSONArray("legs");
					JSONObject leg = legs.getJSONObject(0);
					Result[1] = String.valueOf(leg.getJSONObject("start_location").getDouble("lat"));
					Result[2] = String.valueOf(leg.getJSONObject("start_location").getDouble("lng"));
					Result[3] = String.valueOf(leg.getJSONObject("end_location").getDouble("lat"));
					Result[4] = String.valueOf(leg.getJSONObject("end_location").getDouble("lng"));
					Result[5] = String.valueOf(leg.getJSONObject("duration").getInt("value"));				    
				}else if(json.getString("status").equals("OVER_QUERY_LIMIT"))
				{
					Result[0] = "fail";
					Result[1] = json.getString("status");
					
				}else if(json.getString("status").equals("ZERO_RESULTS"))
				{
					Result[0] = "fail";
					Result[1] = json.getString("status");
				}
				else if(json.getString("status").equals("REQUEST_DENIED"))
				{
					Result[0] = "fail";
					Result[1] = json.getString("status");
				}
				else if(json.getString("status").equals("INVALID_REQUEST"))
				{
					Result[0] ="fail";
					Result[1] = json.getString("status");
				}
				else if(json.getString("status").equals("NOT_FOUND"))
				{
					Result[0] ="fail";
					Result[1] = json.getString("status");								
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}	
		 return Result;
	}
	
	public double[] GeocodingAPI(String address)
	{
		String prefixURL = "http://maps.googleapis.com/maps/api/geocode/xml?";
		String posfixURL = "&sensor=false";
		double returnValue[] = {-1.0, -1.0};
		try
		{
			String originAddress = "address=" +ZIPCode(address.substring(0, 6),Variable.Area,Variable.AreaWeight)+ URLEncoder.encode(address.trim(), "UTF-8");				//distance matrix api's parameter construct: origin address			
			String urlOriginString = prefixURL + originAddress + posfixURL;				//combine prefix part, origin address part, destination address part and posfix part
			try
			{
				URL urlOrigin = new URL(urlOriginString);
				try
				{
					String resultOriginString;
					
					HttpURLConnection connectionOrigin = (HttpURLConnection) urlOrigin.openConnection();

					//connection.setDoInput(true);
					//BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
					
					 //connection.connect();
					InputStreamReader insrOrigin = new InputStreamReader(connectionOrigin.getInputStream(), "UTF-8"); //got request return xml object


					try		//parse xml file
					{
						//File xmlFile = new File("<i>insr</i>");
						
						InputSource inputXmlOrigin = new InputSource(insrOrigin);
						
						resultOriginString =  (String)xpath.evaluate("/GeocodeResponse/result/geometry/location", inputXmlOrigin, XPathConstants.STRING);  //parse level
						
						String[] arrayOrigin = resultOriginString.split("\n");	
						returnValue[0] = Double.valueOf(arrayOrigin[1].trim());
						returnValue[1] = Double.valueOf(arrayOrigin[2].trim());
						
						return returnValue;
										
					}
					catch(XPathExpressionException ex)
					{
						//System.out.print("XPath error!!!");
					}
					/*int respInt = insr.read();
					while(respInt != -1)
					{
						System.out.print((char) respInt);
						respInt = insr.read();
					}*/
					//InputStream urlStream = connection.getInputStream();
				}
				catch(Exception ex)
				{
					System.out.print("\n" + ex + "\n");
				}
			}
			catch(Exception ex)
			{
				System.out.print("error2\n");
			}
		}
		catch(Exception ex)
		{
			System.out.print("error3\n");
		}
		return returnValue;
	}
	


}
