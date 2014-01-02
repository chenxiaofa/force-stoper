package com.anong.forcestoper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class Configure implements Serializable{
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 7599234039569130669L;
	public boolean enableMissedCall = true;
	public List<String> AppList = new ArrayList<String>();

	
	public Configure(Context context){
    	final PackageManager mpackmgr = context.getPackageManager();
        final List<PackageInfo> packs = mpackmgr.getInstalledPackages(0);  
        final String myApp = context.getPackageName();
   	 	for(PackageInfo pinfo:packs){
   	 		if(!(pinfo.packageName.equals(myApp)||isSysApp(pinfo))){
   	 			AppList.add(pinfo.packageName);
   	 		}
   	 	}
	}
    private boolean isSysApp(PackageInfo pinfo){
    	return pinfo.applicationInfo.sourceDir.substring(1, 7).equals("system");
    }
    
    /*
     * 
     * 	Get the configuration from file
     * 	config.cfg
     * 
     * 
     * 
     * 
     * 
     * */
	public static Configure getConfigure(Context context){
        Configure conf = null;
    	File configFile = new File(context.getFilesDir().getPath()+"config.cfg");
    	if(!configFile.exists()){
	        return new Configure(context);
    	}else{
    		try {
				ObjectInputStream objinput = new ObjectInputStream(new FileInputStream(configFile));
				conf = (Configure)objinput.readObject();
				objinput.close();
    		} catch (Exception e) {
    			Log.e("Error",e.getMessage());
				e.printStackTrace();
			} 
    	}
    	return conf;
	}
    /*
     * 
     * 	write the configuration to file
     * 	config.cfg
     * 
     * 
     * 
     * 
     * 
     * */
	public static void writeConfigure(Context context,Configure conf){
    	File configFile = new File(context.getFilesDir().getPath()+"config.cfg");
    	if(configFile.exists())
    		configFile.delete();
    	try {
			configFile.createNewFile();
	    	ObjectOutputStream  objoutput = new ObjectOutputStream (new FileOutputStream(configFile));
	    	objoutput.writeObject(conf);
	    	objoutput.flush();
	    	objoutput.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
