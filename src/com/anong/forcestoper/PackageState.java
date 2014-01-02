package com.anong.forcestoper;

public class PackageState {
	public static void setEnable(String packagename){
		try {
			RootShell.getRootShell().runCMD("pm enable ".concat(packagename));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void setDisable(String packagename){
		try {
			RootShell.getRootShell().runCMD("pm disable ".concat(packagename));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
