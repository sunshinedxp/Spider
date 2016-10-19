package Spider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class spider_thread
{
	private static HashMap<String, String> page = new HashMap<String,String>();   //����ÿ��ҳ�������
	
	private static LinkedHashSet<String> newURL = new LinkedHashSet<String>();							//��url
	private static LinkedHashSet<String> oldURL = new LinkedHashSet<String>();							//��url
	private static HashSet<String> URList = new HashSet<String>();										//������url
	private static String RootURL = "";
	
	public static void main(String[] args)
	{
		String url = "http://139.129.30.243/XSSTest";
//		Scanner in = new Scanner(System.in);
//		url = in.nextLine();
		RootURL = url;
		oldURL.add(url);
		while(oldURL.iterator().hasNext())
		{
			String line = getHtml();
			crawlLinks(line);
		}	
	}

	//��ȡҳ��
	public static String getHtml()
	{
		String urlString = oldURL.iterator().next();
		
    	//���û�б�������
    	if (!URList.contains(urlString)) 
    	{
    		//����GET����
    		try 
    		{
    			URL url = new URL(urlString);
    			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    			connection.setRequestMethod("GET");
    			connection.setConnectTimeout(2000);
    			connection.setReadTimeout(2000);
    			
	    		connection.connect();  

    			if (connection.getResponseCode() == 200) 
    			{
    				InputStream inputStream = connection.getInputStream();
    				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
    				String line = "";
    				String tempLine = "";
    				while ((line = reader.readLine()) != null) 
    				{
    					tempLine += line +"\n";
    				}
    				page.put(urlString, tempLine);            //����ҳ�����ӺͶ�Ӧ��ҳ������
    				//System.out.println(line+"\n");
    			
    				return tempLine;
    			}
    		}
    		catch (MalformedURLException e) 
    		{
    			e.printStackTrace();
    		} 
    		catch (IOException e) 
    		{
    			e.printStackTrace();
    		}
    		
    		try 
    		{
    			Thread.sleep(1000);
    		} 
    		catch (InterruptedException e) 
    		{
    			e.printStackTrace();
    		}
    	}
    	
    	oldURL.remove(urlString);
    	return "";
	}
	
	//��ȡurl
	public static void crawlLinks(String line) 
	{
		
		Pattern pattern = Pattern.compile("href=[\"']?((https?://)?/?[^\"']+)[\"']?.*?>(.+)");
		Matcher matcher = pattern.matcher(line);
		
		if (matcher.find()) 
		{
			String newLink = matcher.group(1).trim().split("\\?")[0]; //����
			System.out.println("newLink : "+newLink);
			//�жϻ�ȡ���������Ƿ���http��ͷ   
			if (!newLink.startsWith("http")) 
			{
				if (newLink.startsWith("/"))
				{
					newLink = RootURL + newLink;
				}
				else
				{
					newLink = RootURL + "/" + newLink;
				}
			}
			
			//ȥ������ĩβ�� 
			if(newLink.endsWith("/"))
			{
				newLink = newLink.substring(0, newLink.length() - 1);
			}
			System.out.println("12");
			//ȥ�أ����Ҷ���������վ������
			if (!oldURL.contains(newLink) && !newURL.contains(newLink) && newLink.startsWith(RootURL))
			{
				
				newURL.add(newLink);
			}
		} 

		oldURL.addAll(newURL);
	}

}