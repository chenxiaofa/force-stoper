package com.anong.forcestoper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import android.util.Log;


public class RootShell{
	
	private static final String TAG = "RootShell";
	
	public static final int ERR_UNROOTED = 0;
	public static final int ERR_AUTH_FAILED = -1;
	public static final int AUTH_OK = 1;
	
	private static RootShell instance = null;
	
	public static String err_msg = null;
	private Process process = null;
	private DataOutputStream os = null;
	private DataInputStream is = null;
	private RootShell(){
		try {
			process = Runtime.getRuntime().exec("su");
			os   = new DataOutputStream(process.getOutputStream());
			is   = new DataInputStream(process.getInputStream());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void Destroy(){
		try {
			if(os != null)
				os.close();
			if(process != null)
				process.destroy();
			process  = null;
			os       = null;
			instance = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public String runCMD(String cmd){
		

		try {
			Process process = Runtime.getRuntime().exec("su");
			DataOutputStream os   = new DataOutputStream(process.getOutputStream());
			os.writeBytes(cmd + "\n");
			os.flush();
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
			Log.i("CMD",cmd);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				if(os!=null)
					os.close();
				if(process!=null)
					process.destroy();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
			
		
		return null;
	}
	public static RootShell getRootShell() throws Exception{
		switch(RootShell.getRootAuth()){
		case RootShell.ERR_AUTH_FAILED:
		case RootShell.ERR_UNROOTED:
				throw new Exception(err_msg);
		case RootShell.AUTH_OK:
			if(instance==null)
				instance = new RootShell();
			break;
		}
		return instance;
	}
	static int getRootAuth(){
		int returnCode = -1;
		Process proc = null;
		DataOutputStream os = null;
		try {
			proc = Runtime.getRuntime().exec("su");
			os   = new DataOutputStream(proc.getOutputStream());
			os.writeBytes("exit\n");
			os.flush();
			int exitValue = proc.waitFor();
			//Log.i(TAG, String.valueOf(exitValue));
			if(exitValue == 0 ){
				err_msg = null;
				returnCode = RootShell.AUTH_OK;
			}else{
				err_msg = "ERR_AUTH_FAILED";
				returnCode = RootShell.ERR_AUTH_FAILED;
			}
		} catch (Exception e) {
			e.printStackTrace();
			err_msg = "ERR_UNROOTED";
			returnCode = RootShell.ERR_UNROOTED;
		}finally{
			try {
				if(os!=null)
					os.close();
				if(proc!=null)
					proc.destroy();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return returnCode;
	}

	
}
