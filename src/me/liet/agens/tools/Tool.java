package me.liet.agens.tools;

import java.io.File;
import java.io.IOException;

import me.liet.agnes.core.Logger;
import me.liet.agnes.core.Shell;
import me.liet.agnes.core.Shell.OutputReceiver;
import android.content.Context;
import android.util.Log;



public class Tool
{
  protected String mPath = null;
  protected String mBasename = null;
  protected String mDirectory = null;

  public Tool(String name, Context context){
    File stat;

    mPath = context.getFilesDir().getAbsolutePath() + "/tools/" + name;
    mDirectory = mPath.substring(0, mPath.lastIndexOf('/'));
    Log.v("PATH",mPath);
    stat = new File(mPath);
    if(!stat.exists()) {
      Logger.error("cannot find tool: '"+name+"'");
      Logger.error(mPath +": No such file or directory");
      Logger.error("this tool will be disabled.");
      mPath = "false";
    } else {
      mBasename = stat.getName();
    }
  }

  public Tool(String name){
    mPath = mBasename = name;
  }

  public void run(String args, OutputReceiver receiver) throws IOException, InterruptedException{
    Shell.exec(mPath + " " + args, receiver);
  }

  public void run(String args) throws IOException, InterruptedException{
    run(args, null);
  }

  public Thread async(String args, OutputReceiver receiver){
    return Shell.async(mPath + " " + args, receiver);
  }

  public Thread async(String args, OutputReceiver receiver, boolean chdir) {
    if(chdir)
    	
      return Shell.async("cd '" + mDirectory + "' && " + mPath + " " + args, receiver);
    else
      return Shell.async(mPath + " " + args, receiver);
  }

  public boolean kill(){
    return kill("9");
  }

  public boolean kill(String signal){
    try{
      Shell.exec("killall -" + signal + " " + mBasename);

      return true;
    }
    catch(Exception e){
     Log.v("TAG",e.toString());
    }

    return false;
  }
}
