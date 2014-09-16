/*
 * This file is part of the dSploit.
 *
 * Copyleft of Simone Margaritelli aka evilsocket <evilsocket@gmail.com>
 *
 * dSploit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * dSploit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with dSploit.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.liet.agnes.core;



import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import me.liet.agens.tools.Ettercap;
import me.liet.agnes.MainActivity;
import me.liet.agnes.ShellUtils;
import android.content.Context;
import android.util.Log;


public class System
{
	private static Context mContext = null;
	private static Ettercap mEttercap = null;
	private static Boolean mInitialized = false;
	public static final String IPV4_FORWARD_FILEPATH = "/proc/sys/net/ipv4/ip_forward";
	public static Network mNetwork = null;
	
 
	  public static String getFifosPath() {
		    return mContext.getFilesDir().getAbsolutePath() + "/fifos/";
		  }

   public static void init(Context context) throws Exception{
	   mContext = context;
	   try {
		   String oldPath = "tools/tcpdump";
		   String newPath =  mContext.getFilesDir().getAbsolutePath() + "/tools/";
		   File toolsFile = new File(newPath);
		   if(!toolsFile.exists()){
			   toolsFile.mkdir();
		   }
		   newPath +=  "tcpdump/";
		   toolsFile = new File(newPath);
		   if(!toolsFile.exists()){
			   toolsFile.mkdir();
		   }
		   newPath += "tcpdump";
		   toolsFile = new File(newPath);
		   if(toolsFile.exists()){
			  return; 
		   }
	       InputStream is = context.getAssets().open(oldPath);
	       FileOutputStream fos = new FileOutputStream(new File(newPath));
	       byte[] buffer = new byte[1024];
	       int byteCount=0;               
	            while((byteCount=is.read(buffer))!=-1) {//循环从输入流读取 buffer字节        
	                fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
	       }
	       fos.flush();//刷新缓冲区
	       is.close();
	       fos.close();
	       
	       ShellUtils.execCommand("chmod 777 "+newPath, true, false);	        
	    } catch (Exception e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();                  
	    }                 
	   mNetwork = new Network(mContext);
	   mInitialized = true;
	   reloadTools();
   }
	
   
   public static void reloadTools() {
	    mEttercap = new Ettercap(mContext);
   }
   
   public static void setForwarding(boolean enabled){
		    Logger.debug("Setting ipv4 forwarding to " + enabled);

		    String status = (enabled ? "1" : "0"),
		      cmd = "echo " + status + " > " + IPV4_FORWARD_FILEPATH;

		    try{
		      Shell.exec(cmd);
		    }
		    catch(Exception e){
		      Log.v("AGNES",e.toString());
		    }
   }
   
   public static void errorLogging(Exception e){
	   Log.v("AGNES",e.toString());
   }
   
   public static Network getNetwork() {
	    return mNetwork;
	  }
   
   public static Ettercap getEttercap(){
	    return mEttercap;
	  }


}
