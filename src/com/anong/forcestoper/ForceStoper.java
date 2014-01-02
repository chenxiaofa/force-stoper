package com.anong.forcestoper;

import java.io.IOException;
import java.util.List;

//import android.content.Context;

public class ForceStoper {
	public static final String cmdhead = "am force-stop";
	private static RootShell rootshell = null;
	
	public ForceStoper(/*Context context*/) {
	}
	private static RootShell rootshellInstance(){
		if (rootshell == null)
			try {
				rootshell = RootShell.getRootShell();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		return rootshell;
	}
	public static void ForceStop(String[] packageNames){
		for(String packageName:packageNames)
			ForceStopApp(packageName);

	}

	public static void ForceStop(List<String> packageNames){
		for(String packageName:packageNames)
			ForceStopApp(packageName);
		ForceStopApp("com.anong.forcestoper");
	}
	public static void ForceStopApp(String packageName){
		try {
			rootshellInstance().runCMD(makeCmd(packageName));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static String makeCmd(String packageName){
		return cmdhead.concat(" ").concat(packageName);
	}
	
	
	public static void forceStopOne(String packagename){
		ForceStopApp(packagename);
	}
	
}
