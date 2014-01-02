package com.anong.forcestoper;

import java.util.HashMap;
import java.util.List;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class Main_Activity extends Activity {
	public static String TAG = "Main_Activity";
	PackageManager pm = null;
	HashMap<String,ApplicationInfo> mAppInfos = new HashMap<String, ApplicationInfo>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pm = getPackageManager();
        List<ApplicationInfo> applist = pm.getInstalledApplications(0);
        for(ApplicationInfo info:applist)
        	mAppInfos.put(info.packageName, info);
        showDialog();        
    }
    
    Handler handler = new Handler(){
    	private ProgressDialog mProgressDialog;
    	int curr = 0;
		public void handleMessage(Message msg)
        {
    		switch (msg.what){
    		case 0:
				List<String> appList = Configure.getConfigure(getApplicationContext()).AppList;

    			mProgressDialog = new ProgressDialog(Main_Activity.this);
    	        mProgressDialog.setIcon(android.R.drawable.ic_menu_delete);
    	        mProgressDialog.setTitle(R.string.dialogTitle);
    	        mProgressDialog.setMessage(Main_Activity.this.getText(R.string.processing));
    	        mProgressDialog.setProgressStyle(1);
    	        mProgressDialog.setMax(appList.size()+1);
    	        mProgressDialog.show();
    	        curr = 0;
    	        (new Thread(){
					public void run(){
						List<String> appList = Configure.getConfigure(getApplicationContext()).AppList;
						for(String packageName:appList){
							updateProgress(packageName);
							if(!app_force_stoped(packageName)){
								
								
								Log.i(TAG,packageName);
								ForceStoper.forceStopOne(packageName);
							}
						}
						updateProgress(getPackageName());
						ForceStoper.forceStopOne(getPackageName());
						handler.sendEmptyMessage(2);
					}
				}).start();
    	        break;
    		case 1://update progress
    			Bundle b = msg.getData();
    			String packageName = b.getString("packageName");
    			//mProgressDialog.setMessage("正在停止应用\n"+packageName);
    			mProgressDialog.incrementProgressBy(1);
    			break;
    		case 2://close progress
    			mProgressDialog.dismiss();
    			break;
    		}
    		
    			
    		
        }
    };
    public void updateProgress(String packageName){
    	Message msg = handler.obtainMessage();
    	Bundle b = new Bundle();
    	b.putString("packageName", packageName);
    	msg.setData(b);
    	msg.what = 1;
    	msg.sendToTarget();
    }

    public boolean app_force_stoped(String packageName){
    	if(pm==null)
    		return false;
    	try {
    		ApplicationInfo appInfo = mAppInfos.get(packageName);
			int flag = appInfo.flags;
			return (flag&ApplicationInfo.FLAG_STOPPED)!=0 || !appInfo.enabled;
    	} catch (Exception e) {
			// TODO Auto-generated catch block
    		e.printStackTrace();
			return true;
		}
    }
    protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent){
    	super.onActivityResult(paramInt1, paramInt2, paramIntent);
    	//showDialog();
    }

    private void showDialog(){
    	AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
    	localBuilder.setTitle(R.string.dialogTitle);
    	localBuilder.setIcon(android.R.drawable.ic_dialog_info);
    	localBuilder.setMessage(R.string.dialogcontent);
    	localBuilder.setPositiveButton(R.string.dialogbtngo,new DialogInterface.OnClickListener()
    	{
    		public void onClick(DialogInterface paramDialogInterface, int paramInt){
    			handler.sendEmptyMessage(0);
    		}
    	});
    	localBuilder.setNegativeButton(R.string.dialogbtnset,new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Intent i = new Intent(Main_Activity.this,App_List.class); 
				startActivity(i);
			}
    		
    	});
    	localBuilder.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
          public void onCancel(DialogInterface paramDialogInterface)
          {
        	Main_Activity.this.finish();
        	ForceStoper.forceStopOne(getPackageName());
          }
        });
        localBuilder.create();
        localBuilder.show();

    }
}
