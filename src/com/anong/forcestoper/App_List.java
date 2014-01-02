
package com.anong.forcestoper;

import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.content.pm.ApplicationInfo;


public class App_List extends Activity {

	private Configure config = null;
	private boolean loadSystemApp = false;
	private TableLayout layout = null;
	private Button btn = null;
    /**
     * Called with the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.applist_view);
        config = Configure.getConfigure(this);
        layout = (TableLayout)this.findViewById(R.id.Layout);
        btn = (Button)this.findViewById(R.id.button1);
        btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				loadSystemApp = !loadSystemApp;
				reflushView();
			}
		});
        reflushView();
        

    }
    public void reflushView(){
    	btn.setText((loadSystemApp?this.getString(R.string.hideSysApp):this.getString(R.string.showSysApp)));
    	layout.removeAllViews();
		loadAppList(layout,loadSystemApp);
    }
    
    public void loadAppList(TableLayout layout,boolean loadSystemApp){
    	final PackageManager mpackmgr = this.getPackageManager();
        final List<PackageInfo> packs = mpackmgr.getInstalledPackages(0);  
        final String myApp = getPackageName();
    	 for(PackageInfo pinfo:packs){
 	        if(!myApp.equals(pinfo.packageName)&&(!isSysApp(pinfo)||loadSystemApp)){
 	        	Log.i(pinfo.packageName,String.valueOf(pinfo.applicationInfo.enabled));
 	        	Button btn_enable = new Button(this);
 	        	btn_enable.setText(pinfo.applicationInfo.enabled?"Õ£”√":"∆Ù”√");
 	        	btn_enable.setTag(pinfo);
 	        	btn_enable.setOnClickListener(btn_state_change);
 	        	TableRow pkg = new TableRow(this);
 	        	LinearLayout div = new LinearLayout(this);
 	        	LinearLayout div2 = new LinearLayout(this);
 	        	//settingDiv.setOrientation(LinearLayout.VERTICAL);
 	        	
 	        	ImageView img = new ImageView(this);
 	        	img.setImageDrawable(zoomDrawable(pinfo.applicationInfo.loadIcon(mpackmgr),100,100));
 	        	///*
 	        	CheckBox ckbox = new CheckBox(this);
 	        	ckbox.setText(pinfo.packageName);
 	        	ckbox.setChecked(inArray(config.AppList,pinfo.packageName));
 	        	ckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
 	
 					public void onCheckedChanged(CompoundButton buttonView,
 							boolean isChecked) {
 						int index = searchIndex(config.AppList,buttonView.getText().toString());
 						if(isChecked){
 							if(index==-1)
 								config.AppList.add(buttonView.getText().toString());
 						}else{
 							if(index!=-1)
 								config.AppList.remove(index);
 						}
 					}});
 					//*/
 	        	div.addView(img);
 	        	div.addView(ckbox);
 	        	pkg.addView(div);
 	        	
 	        	layout.addView(pkg);
 	        	layout.addView(btn_enable);
 	        }
 	        //System.out.println(String.valueOf(pinfo.applicationInfo.uid).concat(pinfo.packageName));
         }
    }
    OnClickListener btn_state_change = new OnClickListener(){
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			PackageInfo pinfo = (PackageInfo) v.getTag();
			if(pinfo.applicationInfo.enabled)
				PackageState.setDisable(pinfo.packageName);
			else
				PackageState.setEnable(pinfo.packageName);
			reflushView();
		}
 	};
    private boolean isSysApp(PackageInfo pinfo){
    	return pinfo.applicationInfo.sourceDir.substring(1, 7).equals("system");
    }
    private int searchIndex(List<String> whitelist,String value){
		if(whitelist!=null)
			for(int i = 0;i < whitelist.size();i++){
				if(whitelist.get(i).equals(value))
					return i;
			}
    	return -1;
    }
	private boolean inArray(List<String> whitelist,String value){
		if(whitelist!=null)
			for(String str:whitelist){
				if(str.equals(value))
					return true;
			}
		return false;
	}
    
    private Drawable zoomDrawable(Drawable drawable, int w, int h) {  
        int width = drawable.getIntrinsicWidth();  
        int height = drawable.getIntrinsicHeight();  
        Bitmap oldbmp = drawableToBitmap(drawable);  
        Matrix matrix = new Matrix();  
        float scaleWidth = ((float) w / width);  
        float scaleHeight = ((float) h / height);  
        matrix.postScale(scaleWidth, scaleHeight);  
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,  
                matrix, true);  
        return new BitmapDrawable(null, newbmp);  
    }  
      
    private Bitmap drawableToBitmap(Drawable drawable) {  
        int width = drawable.getIntrinsicWidth();  
        int height = drawable.getIntrinsicHeight();  
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888  
                : Bitmap.Config.RGB_565;  
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);  
        Canvas canvas = new Canvas(bitmap);  
        drawable.setBounds(0, 0, width, height);  
        drawable.draw(canvas);  
        return bitmap;  
    }  
    

	public void onPause(){
		super.onPause();
	}
	public void onDestroy(){
		super.onDestroy();
		Configure.writeConfigure(this,config);
		Intent i = new Intent(this,Main_Activity.class); 
		startActivity(i);
		finish();
	}
}

