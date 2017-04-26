package com.ribeiromobware.watertank;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import com.ribeiromobware.watertank.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.KeyListener;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WaterTank extends Activity {
	
	public static WaterTank sWaterTank;
	// Chronometer variables
	Timer chronometer = new Timer();
	public EditText chrono;
	//private Handler mHandler = new Handler();
	//private long startTime, elapsedTime;
	//private final int REFRESH_RATE = 100;
	//private String hours,minutes,seconds,milliseconds;
	//private long secs,mins,hrs;
	private boolean timerflag, canceled=false;
	//
	private ExpandableListView list;
	private List<String> listGroup;
    private HashMap<String, List<String>> listItem;
	private CustomExpandableListAdapter lAdapter;
	//private AdapterElist listAdapter = new AdapterElist(this);
	private Button btnCr;
	private TextView txtResult, txtOpt;
	private InputFilter filter;
	private float timeCon=0, tankH=0, deltaH=0, result=0, resultP=0, topR=0, botr=0, factorH=0, inputedVolume=0,
	deltar=0, deltaR=0, topFinalR=0, vTank=0, efectVol, s=0;
	private int idG=0, idC=0, x=0;
	private String Groupselected, Childselected, capacity, selec, remain;
	private SharedPreferences settings;
	private SharedPreferences.Editor prefEditor;
	private DecimalFormat df = new DecimalFormat("#.##");
	public float g=(float)9.80665, gal=(float)3.78541, psi=(float)1.421;
	public String[] convertedVol0, convertedVol1, convertedVol2, convertedPress0, convertedPress1, convertedPress2;
	public String[][] listVol, listPress;
	public String lang, SI, unitV, unitP, lcdbefore, sampleMode, listDsp, press;
	long stopedtime=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		lang = Locale.getDefault().toString();
		sWaterTank = this;
    	setContentView(R.layout.act_calc);
    	//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    	//defChoiceDiag();
    	//listAdapter.listdsp="wt";
    	lang = Locale.getDefault().toString();
    	settings = PreferenceManager.getDefaultSharedPreferences(this);
    	prefEditor=settings.edit();
    	sampleMode = settings.getString("sample_type", "vol");
    	SI = settings.getString("use_si", "");
    	checkScreenSize();
    	getMsg();
    	listDsp = "wt";
    	getListByLang();
    	((TextView)findViewById(R.id.txtSelec)).setVisibility(View.VISIBLE);
		//((TextView)findViewById(R.id.txtResult)).setVisibility(View.VISIBLE);
		txtResult = (TextView) findViewById(R.id.txtResult);
		setAnim();
		btnCr = (Button) findViewById(R.id.btncr);
    	btnCr.setText(R.string.btn_start);												
    	recoverTimer();
    	listItem = ExpandableListDataPump.getData();
    	listGroup = new ArrayList<String>(listItem.keySet());
    	list = (ExpandableListView) findViewById(R.id.explsttank);
		lAdapter = new CustomExpandableListAdapter(this, listGroup, listItem, listPress, listDsp, press, unitV, unitP);
		list.setAdapter(lAdapter);
		//list.setAdapter(listAdapter);
		showSelect();
		showToastSelec();
		list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
				
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				//Brandselected = (String) parent.getExpandableListAdapter().getGroup(groupPosition);
				//Modelselected = (String) parent.getExpandableListAdapter().getChild(groupPosition, childPosition);
				idG = groupPosition;
				idC = childPosition;
				showSelect();
				//txtOpt.setText(selec+" -> "+Brandselected+" - "+Modelselected);
				return true;
			}
		});
		
    	chrono.setOnClickListener (new View.OnClickListener() {
			
			@Override
			public void onClick (View v) {
				listItem.remove(String.valueOf(getResources().getStringArray(R.array.geometry)[1]));
				//lAdapter.setNewItems(listGroup, listItem);
				lAdapter.notifyDataSetChanged();
				x=x+1;
				if(x==2){
					defChoiceDiag();
					return;
				}
				Handler handler=new Handler();
				handler.postDelayed(new Runnable() {
				    @Override
				    public void run() {
				    	x=0; 
				    }
				}, 1600);
				return;
			}
		});
    	
    	chrono.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				if (timerflag==true){
					setInputTime();
					inputFilter();
					chrono.setFilters(new InputFilter[] { filter, new InputFilter.LengthFilter(8)});
			        btnCr.setText(R.string.btn_calc);
					return true;
				}else {
					recoverTimer();
				}
				return true;
			}
			
		});
		
    	btnCr.setOnClickListener (new View.OnClickListener() {
			
			@Override
			public void onClick (View v) {
				if(btnCr.getText()==getResources().getString(R.string.btn_start)){
					executeCr();
					return;
				}else if(btnCr.getText()==getResources().getString(R.string.btn_stop)){
					stopCr();
					btnCr.setText(R.string.btn_start);
					showResult();
					return;
				}else if(btnCr.getText()==getResources().getString(R.string.cancel)){	
					stopCr();
					canceled=true;
					btnCr.setText(R.string.btn_start);
				}else{
					//((TextView)findViewById(R.id.txtResult)).setVisibility(View.INVISIBLE);
					getInpValue();
					calculateInp();
					showResultByInput();
				}
				return;
			}
		});					  
		
	}
	
	public static WaterTank getInstance() {
	    return sWaterTank;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int action = event.getAction();
		int keyCode = event.getKeyCode();
	    	switch (keyCode) {
	    	case KeyEvent.KEYCODE_VOLUME_UP:
	    		if (action == KeyEvent.ACTION_DOWN) {
	    			if(btnCr.getText()==getResources().getString(R.string.btn_start)){
						executeCr();
	    			}else if (btnCr.getText()==getResources().getString(R.string.btn_calc)){
						//((TextView)findViewById(R.id.txtResult)).setVisibility(View.INVISIBLE);
						getInpValue();
						calculateInp();
						showResultByInput();
					}
	            }
	            return true;
	        case KeyEvent.KEYCODE_VOLUME_DOWN:
	            if (action == KeyEvent.ACTION_DOWN) {
		            if(btnCr.getText()==getResources().getString(R.string.btn_stop)){
						stopCr();
						btnCr.setText(R.string.btn_start);
						showResult();
					}else if(btnCr.getText()==getResources().getString(R.string.cancel)){	
						stopCr();
						canceled=true;
						btnCr.setText(R.string.btn_start);
					}
	            }
	            return true;
	        default:
	            return super.dispatchKeyEvent(event);
	        }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.base, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
	
		int id = item.getItemId();
		
		if (id == R.id.instructions) {
			launchInstruct();
			return true;
		}
		else if (id == R.id.action_settings) {
			launchSettings();
			return true;
		} 
		else if (id == R.id.rltabout){
			launchInfo();
			//LayoutInflater inflater = getLayoutInflater();
			//View layout = inflater.inflate(R.layout.act_showsplash,(ViewGroup) findViewById(R.id.rltabout));
			//text.setText("This is a custom toast");
			//Toast toast = new Toast(getApplicationContext());
			//toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
			//toast.setDuration(Toast.LENGTH_LONG);
			//toast.setView(layout);
			//toast.show();
		}		
		else {
			onDestroy();
			finish();
			//Intent intent = new Intent(getApplicationContext(), Instruct.class);
	    	//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    	//intent.putExtra("EXIT", true);
	    	//startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void showToastSelec(){
		Handler handler=new Handler();
		handler.postDelayed(new Runnable() {
		    @Override
		    public void run() {
		    	Context context = getApplicationContext();
				String text = getResources().getString(R.string.toastselection);
				int duration = Toast.LENGTH_LONG;
				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
				toast.setGravity(Gravity.CENTER, 0, 0); 
		    }
		}, 4000);	
	}
	
	private void checkScreenSize(){
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int height = displayMetrics.heightPixels;
		int width = displayMetrics.widthPixels;
		RelativeLayout layout = (RelativeLayout)findViewById(R.id.rltcalc);
		android.view.ViewGroup.LayoutParams params = layout.getLayoutParams();
		if (height>width){
			// Changes the height and width to the specified *pixels*  		
			params.height = (int) (height-(height*(12/100.0f)));
			return;
		}else if (height<width){
			//(int) (displayMetrics.heightPixels-(displayMetrics.heightPixels*(13/100.0f)));
			params.height = (int) (width-(width*(12/100.0f)));
			return;
		}
	}

	private void playSb7(){
		final MediaPlayer beep = MediaPlayer.create(this, R.raw.beep7);
		beep.start();
		Handler handler=new Handler();
		handler.postDelayed(new Runnable() {
		    @Override
		    public void run() {
		    	beep.release();
		    }
		}, 395);
		return;
	}
	
	private void playSb9(){
		final MediaPlayer beep = MediaPlayer.create(this, R.raw.beep9);
		beep.start();
		Handler handler=new Handler();
		handler.postDelayed(new Runnable() {
		    @Override
		    public void run() {
		    	beep.release();
		    	
		    }
		}, 1000);
		return;
	}
	
	private void setAnim(){
    	Animation blink = AnimationUtils.loadAnimation(this, R.anim.blink);
		txtResult.setAnimation(blink);
		txtResult.getAnimation().cancel();
		txtResult.getAnimation().reset();
		txtResult.setAlpha(0);
    }
	
	private void defChoiceDiag(){
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.dmsgsample).setTitle(R.string.dtitlesample);
		builder.setPositiveButton(R.string.two, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   prefEditor.remove("sample_type");
	        	   prefEditor.apply();
	        	   prefEditor.putString("sample_type", "time");
	        	   prefEditor.apply();
	        	   sampleMode = settings.getString("sample_type", "vol");
	           }
	    });
		builder.setNegativeButton(R.string.one, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   prefEditor.remove("sample_type");
	        	   prefEditor.apply();
	        	   prefEditor.putString("sample_type", "vol");
	        	   prefEditor.apply();
	        	   sampleMode = settings.getString("sample_type", "vol");
	           }
	    });
		AlertDialog dialog = builder.create();
		dialog.show();
    }
	
	private void inputVolDiag(){
		final EditText inputVol = new EditText(this);
		inputFilter();
		inputVol.setFilters(new InputFilter[] { filter, new InputFilter.LengthFilter(5)});
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.dinpvolmsg).setTitle(R.string.dinpvoltitle);
		builder.setView(inputVol);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {       
				public void onClick(DialogInterface dialog, int id) {
					inputedVolume = Float.parseFloat(inputVol.getText().toString()+0);
					chrono.setFilters(new InputFilter[] {});
	           }
	    });
		builder.setNegativeButton(R.string.redo, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   chrono.setFilters(new InputFilter[] {});
	        	   //chronometer.startTime = System.currentTimeMillis() - chronometer.elapsedTime;
	        	   chronometer.mHandler.removeCallbacks(chronometer.startTimer);
	        	   chrono.setText("00:00'00.0");
	        	   btnCr.setText(R.string.btn_start);
	        	   dialog.dismiss();
	           }
	    });
		AlertDialog dialog = builder.create();
		Vibrator vibrate = (Vibrator) getSystemService(VIBRATOR_SERVICE);//this.context.getSystemService(Context.VIBRATOR_SERVICE);
		 // Vibrate for 600 milliseconds
		vibrate.vibrate(600);
		dialog.show();		
	}
    
    private void getMsg(){
    	capacity = getResources().getString(R.string.capacity);
    	selec = getResources().getString(R.string.option);
		remain = getResources().getString(R.string.remain);
		press = getResources().getString(R.string.pressure);
    }
	
    private void collapseList(){
    	for(int i=0; i<=getResources().getStringArray(R.array.geometry).length; i++){
    		list.collapseGroup(i);
    		list.setEnabled(false);
    	}
    }
    
    private void showSelect(){
    	Groupselected = (String) list.getExpandableListAdapter().getGroup(idG);
		Childselected = (String) list.getExpandableListAdapter().getChild(idG, idC);
		efectVol = Float.parseFloat(Childselected);
		txtOpt = (TextView) findViewById(R.id.txtSelec);
		//if (lang!="pt_BR"){
			//txtOpt.setText(selec+" -> "+Brandselected+" - "+"Vol."+" "+Modelselected);
			//return;
		//}else if(lang=="pt_BR"){
		txtOpt.setText(selec+" -> "+Groupselected+" - "+Childselected+" "+unitV);
		//}
		//return;
    }
    
    private void getListByLang(){    	
    	//listAdapter.Mainlist = getResources().getStringArray(R.array.geometry);
    	press = getResources().getString(R.string.pressure);
    	convertedVol2 = new String[]{"0"};
    	convertedPress2 = new String[]{"0"};
    	int id, i;
    	float fgetter;
    	String getter; 
    	if ("mtc".equals(SI)){
    		id = getResources().getStringArray(R.array.volumeF).length;
    		convertedVol0 = new String[id];
    		convertedPress0 = new String[id];
    		for (i = 0; i < convertedVol0.length; i++){
    			convertedVol0[i] = getResources().getStringArray(R.array.volumeF)[i];
    			convertedPress0[i] = String.valueOf(df.format(Float.valueOf(getResources().getStringArray(R.array.heightF)[i])-0.10));
    		}
    		id = getResources().getStringArray(R.array.volumeC).length;
    		convertedVol1 = new String[id];
    		convertedPress1 = new String[id];
    		for (i = 0; i < convertedVol1.length; i++){
    			convertedVol1[i] = getResources().getStringArray(R.array.volumeC)[i];
    			convertedPress1[i] = String.valueOf(df.format(Float.valueOf(getResources().getStringArray(R.array.heightC)[i])));
    		}
    		listVol = new String[][]{convertedVol0,convertedVol1,convertedVol2};
    		//listAdapter.Sublist = listVol;
    		listPress = new String[][]{convertedPress0,convertedPress1,convertedPress2};
    		//listAdapter.Listpress = listPress;
    		unitV = getResources().getString(R.string.unitliter);
    		//listAdapter.unitv=unit;
	    	unitP = getResources().getString(R.string.unitmca);
	    	//listAdapter.unitp = punit;
	    	return;
    	}else if ("si".equals(SI)){
    		id = getResources().getStringArray(R.array.volumeF).length;
    		convertedVol0 = new String[id];
    		convertedPress0 = new String[id];
    		for (i = 0; i < convertedVol0.length; i++){
    			fgetter = Float.valueOf(getResources().getStringArray(R.array.volumeF)[i])/gal;
    			convertedVol0[i] = String.valueOf(fgetter);//getResources().getStringArray(R.array.volumeF)[i];
    			getter = String.valueOf(df.format(Float.valueOf(getResources().getStringArray(R.array.heightF)[i])-0.10/psi));
    			convertedPress0[i] = String.valueOf(getter);//getResources().getStringArray(R.array.psiF)[i];
    		}
    		id = getResources().getStringArray(R.array.volumeC).length;
    		convertedVol1 = new String[id];
    		convertedPress1 = new String[id];
    		for (i = 0; i < convertedVol1.length; i++){
    			getter = String.valueOf(df.format(Float.valueOf(getResources().getStringArray(R.array.volumeC)[i])/gal));
    			convertedVol1[i] = String.valueOf(getter);//getResources().getStringArray(R.array.volumeC)[i];
    			getter = String.valueOf(df.format(Float.valueOf(getResources().getStringArray(R.array.heightC)[i])/psi));
    			convertedPress1[i] = String.valueOf(getter);//getResources().getStringArray(R.array.psiC)[i];
    		}
        	listVol = new String[][]{convertedVol0,convertedVol1,convertedVol2};
        	//listAdapter.Sublist = listVol;
        	listPress = new String[][]{convertedPress0,convertedPress1,convertedPress2};
        	//listAdapter.Listpress = listPress;
        	unitV = getResources().getString(R.string.unitgal);
        	//listAdapter.unitv=unit;
        	unitP = getResources().getString(R.string.unitpsi);
        	//listAdapter.unitp = punit;
        	return;
    	}
    	return;
    }
     
    private void getInpValue(){
		if(timerflag==false){
			switch(sampleMode){
			case "time":
				chronometer.elapsedTime = Long.parseLong((chrono.getText().toString()));
			break; case "vol":
				inputedVolume = Float.parseFloat((chrono.getText().toString()));
				inputedVolume = inputedVolume/2;
			break;
			}
		}
		return;
    }
	
	private void setInputTime(){
    	chrono.setKeyListener((KeyListener) chrono.getTag());
    	chrono.setCursorVisible(true);
		chrono.setBackgroundResource(R.drawable.roundedwhite);
		//Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/chrono4.ttf");
		chrono.setTypeface(Typeface.DEFAULT);
		chrono.setText("");
		chrono.setFocusableInTouchMode(true);
		chrono.requestFocus();
		timerflag=false;
		
    }
    
    private void inputFilter(){
    	filter = new InputFilter() {
			public CharSequence filter(CharSequence source, int start, int end,
                    Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isDigit(source.charAt(i))) { // Accept only digits ; otherwise show toast
                    	Context context = getApplicationContext();
        				String text = getResources().getString(R.string.toastinput);
        				int duration = Toast.LENGTH_LONG;
        				Toast toast = Toast.makeText(context, text, duration);
        				toast.show();
        				toast.setGravity(Gravity.CENTER, 0, 0); 
                        return "";
                    }
                }
                return null;
            }
        };
    }
    
    private void recoverTimer(){
    	chrono = (EditText)findViewById(R.id.chrono);
		chrono.setVisibility(View.VISIBLE);
		chrono.setFilters(new InputFilter[] {});
		chrono.setText("00:00'00.0");
		chrono.setCursorVisible(false);
		Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/chrono4.ttf");
		chrono.setTypeface(custom_font);
		chrono.setTag(chrono.getKeyListener());
		chrono.setKeyListener(null);
		chrono.setBackgroundResource(R.drawable.lcd);
		chrono.setFocusableInTouchMode(false);
		btnCr.setText(R.string.btn_start);
		btnCr.requestFocus();
		timerflag=true;
    }
    
    private void blinkResult(){
    	txtResult.setAlpha(1); 
		txtResult.getAnimation().start();
		Handler handler=new Handler();
		handler.postDelayed(new Runnable(){
			@Override
			public void run() {
				txtResult.getAnimation().cancel();
				txtResult.getAnimation().reset();
			}
			
		}, 9000);
    }
    
	private void startCr(){
		chronometer.display="wt";
		chrono.setText("00:00'00.0");
		chronometer.startTime = System.currentTimeMillis();
		chronometer.mHandler.removeCallbacks(chronometer.startTimer);
		chronometer.mHandler.postDelayed(chronometer.startTimer, 0);
		playSb7();
	}
    
    private void stopCr(){
    	chronometer.mHandler.removeCallbacks(chronometer.startTimer);
		list.setEnabled(true);
		playSb9();
	}

	private void autoStop(){
    	final Handler handler = new Handler();
    	handler.postDelayed(new Runnable(){
	    	@Override
			public void run() {
	    		if (canceled==false){
		    		//stopCr();
		    		chronometer.mHandler.removeCallbacks(chronometer.startTimer);
		    		chrono.setText("00:00'15.0");
		    		list.setEnabled(true);
		    		btnCr.setText(R.string.btn_start);
		    		playSb9();
		    		inputVolDiag();
	    		}else{
	    			handler.removeCallbacks(null);
	    		}
			}
		
		}, 15000);
	}

	private void executeCr(){
		if ("vol".equals(sampleMode)){
			collapseList();
			startCr();
			txtResult.setAlpha(0);
		    btnCr.setText(R.string.cancel);
	    	canceled = false;
		    autoStop();
			return;
		}else if("time".equals(sampleMode)){
			collapseList();
			startCr();
			txtResult.setAlpha(0);
		    btnCr.setText(R.string.btn_stop);
			return;
		}
	}

	private void calculateInp(){
		
	}
	
	private void showResultByInput(){
		
	}

	private void getMeasures(){
		timeCon = (chronometer.elapsedTime/1000);
		switch (idG){
		case 0:
			tankH = Float.parseFloat(String.valueOf(getResources().getStringArray(R.array.heightF)[idC]));
			topR = Float.parseFloat(String.valueOf(getResources().getStringArray(R.array.topdiamF)[idC]))/2;
			botr = Float.parseFloat(String.valueOf(getResources().getStringArray(R.array.botdiamF)[idC]))/2;
		break; case 1:
			tankH = Float.parseFloat(String.valueOf(getResources().getStringArray(R.array.heightC)[idC]));
			topR = Float.parseFloat(String.valueOf(getResources().getStringArray(R.array.diamC)[idC]))/2;
		break; case 2:
		
		break;
		}
		s = Float.valueOf((float) ((0.5*g*Math.pow(timeCon,2))/10000));
		deltaH = Float.valueOf(tankH-s);
	}
    
    private void getGeo() {
    	getMeasures();
    	switch (idG){
    	case 0:
			factorH  = (tankH-deltaH)/tankH;
			deltar = (topR-botr);
			deltaR = deltar*factorH;
			topFinalR = topR-deltaR;
			vTank = Float.parseFloat(String.valueOf(((Math.PI*tankH)/3)*(Math.pow(topR, 2)+(topR*botr)+Math.pow(botr, 2))*1000));
			result = Float.parseFloat(String.valueOf(((Math.PI*deltaH)/3)*(Math.pow(topFinalR, 2)+(topFinalR*botr)+Math.pow(botr, 2))*1000));
		break;case 1:
			vTank = Float.parseFloat(String.valueOf(Math.PI*Math.pow(topR, 2)*tankH*1000));
			result = Float.parseFloat(String.valueOf(Math.PI*Math.pow(topR, 2)*deltaH*1000));
    	break; case 2:
    	
    	break;
    	};
    	
    }
	
    private void showResult(){
		getGeo();
		float x = (float) 10;
		resultP = deltaH;
		String chksi = settings.getString("use_si", "mtc");
		if ("si".equals(chksi)){
			efectVol=efectVol*gal;
		}
		if (result>efectVol) {
			txtResult.setText(getResources().getString(R.string.msglowtime));
			blinkResult();
			return;
		} else if (result>=x){
			if ("si".equals(chksi)){
				result = result/gal;
				resultP = deltaH/psi;
			}
			txtResult.setText(capacity+" "+df.format(vTank)+" "+unitV+"\n"+remain+" "+ df.format(result)+" "+unitV+" - "+press+" "+df.format(resultP)+" "+unitP);
			blinkResult();
			return;
		}else if (result<x){
			txtResult.setText(getResources().getString(R.string.msgvout));
			blinkResult();
			return;
		}
		cleanVar();
		return;
    }
	
    private void cleanVar(){
		timeCon=0; tankH=0; deltaH=0; result=0; resultP=0; topR=0; botr=0; factorH=0;
		deltar=0; deltaR=0; topFinalR=0; vTank=0; s=0;
		//idG=0; idC=0;
	}
	
    private void launchInfo() {
    	Intent open = new Intent(this, Info.class);
    	startActivity(open);
	}
    
    private void launchInstruct() {
    	Intent open = new Intent(this, Instruct.class);
    	startActivity(open);
	}
    
    private void launchSettings() {
    	Intent open = new Intent(this, Setup.class);
    	startActivity(open);
	}
    
    @Override
	public void onDestroy() {
		super.onDestroy(); 
		finish();
    }

}