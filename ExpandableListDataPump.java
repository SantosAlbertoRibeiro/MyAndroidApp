package com.ribeiromobware.watertank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class ExpandableListDataPump {
    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new LinkedHashMap<String, List<String>>();
        List<String> grZero = new ArrayList<String>();
        for (int z=0; z<WaterTank.sWaterTank.convertedVol0.length; z++){
        	grZero.add(WaterTank.sWaterTank.convertedVol0[z]);//getResources().getStringArray(R.array.volumeF)[z]);
		}
        expandableListDetail.put(WaterTank.sWaterTank.getResources().getStringArray(R.array.geometry)[0], grZero);
        List<String> grOne = new ArrayList<String>();
        for (int z=0; z<WaterTank.sWaterTank.convertedVol1.length; z++){
        	grOne.add(WaterTank.sWaterTank.convertedVol1[z]);//getResources().getStringArray(R.array.volumeF)[z]);
		}
        expandableListDetail.put(WaterTank.sWaterTank.getResources().getStringArray(R.array.geometry)[1], grOne);
        List<String> grTwo = new ArrayList<String>();
        for (int z=0; z<WaterTank.sWaterTank.convertedVol2.length; z++){
        	grTwo.add(WaterTank.sWaterTank.convertedVol2[z]);
		}
        expandableListDetail.put(WaterTank.sWaterTank.getResources().getStringArray(R.array.geometry)[2], grTwo);
        
        //List<String> cricket = new ArrayList<String>();
        //cricket.add("0.01");
        //cricket.add("1.25");
        //cricket.add("65.1");
        //cricket.add("2.47");
        //cricket.add("0.1");

        //List<String> football = new ArrayList<String>();
        //football.add("0.05");
        //football.add("54.");
        //football.add("3");
        //football.add("4");
        //football.add("45");

        //List<String> basketball = new ArrayList<String>();
        //basketball.add("45");
        //basketball.add("48");
        //basketball.add("45");
        //basketball.add("45");
        //basketball.add("45");

        //expandableListDetail.put("CRICKET TEAMS", cricket);
        //expandableListDetail.put("FOOTBALL TEAMS", football);
        //expandableListDetail.put("BASKETBALL TEAMS", basketball);
        return expandableListDetail;
    }
    
    public static HashMap<String, List<String>> genMsg(){
		HashMap<String, List<String>> header = new HashMap<String, List<String>>();
		List<String> grCr = new ArrayList<String>();
		header.put(Chronometer.sChrono.getResources().getString(R.string.listfstinp), grCr);
		return header;
	}
    
    public static HashMap<String, List<String>> addEvent(){
		HashMap<String, List<String>> event = new LinkedHashMap<String, List<String>>();
		List<String> list = new ArrayList<String>();
		list.add(Chronometer.sChrono.lapList);
		event.put(Chronometer.sChrono.event, list);
		return event;
	}
}