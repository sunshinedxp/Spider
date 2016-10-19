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
	private static HashMap<String, String> page = new HashMap<String,String>();   //保存每个页面的内容
	
	private static LinkedHashSet<String> newURL = new LinkedHashSet<String>();							//新url
	private static LinkedHashSet<String> oldURL = new LinkedHashSet<String>();							//旧url
	private static HashSet<String> URList = new HashSet<String>();										//爬过的url
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

	//获取页面
	public static String getHtml()
	{
		String urlString = oldURL.iterator().next();
		
    	//如果没有被遍历过
    	if (!URList.contains(urlString)) 
    	{
    		//发起GET请求
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
    				page.put(urlString, tempLine);            //保存页面链接和对应的页面内容
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
	
	//提取url
	public static void crawlLinks(String line) 
	{
		
		Pattern pattern = Pattern.compile("href=[\"']?((https?://)?/?[^\"']+)[\"']?.*?>(.+)");
		Matcher matcher = pattern.matcher(line);
		
		if (matcher.find()) 
		{
			String newLink = matcher.group(1).trim().split("\\?")[0]; //链接
			System.out.println("newLink : "+newLink);
			//判断获取到的链接是否以http开头   
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
			
			//去除链接末尾的 
			if(newLink.endsWith("/"))
			{
				newLink = newLink.substring(0, newLink.length() - 1);
			}
			System.out.println("12");
			//去重，并且丢弃其他网站的链接
			if (!oldURL.contains(newLink) && !newURL.contains(newLink) && newLink.startsWith(RootURL))
			{
				
				newURL.add(newLink);
			}
		} 

		oldURL.addAll(newURL);
	}

}