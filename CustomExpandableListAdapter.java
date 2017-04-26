package com.ribeiromobware.watertank;

import java.util.HashMap;
import java.util.List;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
//import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listGroup;
    private HashMap<String, List<String>> listItem;
    private String[][] listPress; //= WaterTank.sWaterTank.listPress;
    private String listDsp;
    private String press; //= WaterTank.sWaterTank.getResources().getString(R.string.pressure);
	private String unitV; //= WaterTank.sWaterTank.unit;
	private String unitP; //= WaterTank.sWaterTank.punit;

    public CustomExpandableListAdapter(Context context, List<String> listGroup, HashMap<String, List<String>> listItem,
    		String[][] listPress, String listDsp, String press, String unitV, String unitP) {
        this.context = context; this.listGroup = listGroup; this.listItem = listItem; this.listDsp = listDsp;
        this.listPress = listPress; this.press = press; this.unitV = unitV; this.unitP = unitP;
    }

    @Override
    public Object getChild(int grPosition, int itPosition) {
        return this.listItem.get(this.listGroup.get(grPosition))
        		.get(itPosition);
    }

    @Override
    public long getChildId(int grPosition, int itPosition) {
        return itPosition;
    }

    @Override
    public View getChildView(int grPosition, final int itPosition,
    		boolean isLastChild, View convertView, ViewGroup parent) {
        final String itListText = (String) getChild(grPosition, itPosition);
        TextView textViewModels = new TextView(context);
        if("wt".equals(listDsp)){
			textViewModels.setText(itListText+" "+unitV+" - "+press+" "+listPress[grPosition][itPosition]+" "+unitP);//Sublist[groupPosition][childPosition]);
			//return textViewModels;
		}else if ("cr".equals(listDsp)){
			textViewModels.setText(itListText);//Sublist[groupPosition][childPosition]);
			//return textViewModels;
		}
		// Definimos um alinhamento
		textViewModels.setPadding(10 , 5, 0, 5);
		//textViewModels.setTextColor(R.color.White);//(WaterTank.getInstance().getResources().getColor(R.color.White));
		return textViewModels;
        //if (convertView == null) {
            //LayoutInflater layoutInflater = (LayoutInflater) this.context
            //.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //convertView = layoutInflater.inflate(R.layout.list_item, null);
        //}
        //TextView expandedListTextView = (TextView) convertView
        //.findViewById(R.id.expandedListItem);
        //expandedListTextView.setText(expandedListText);
        //return convertView;
    }

    @Override
    public int getChildrenCount(int grPosition) {
        return this.listItem.get(this.listGroup.get(grPosition))
                .size();
    }

    @Override
    public Object getGroup(int grPosition) {
        return this.listGroup.get(grPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listGroup.size();
    }

    @Override
    public long getGroupId(int grPosition) {
        return grPosition;
    }

    @Override
    public View getGroupView(int grPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String grListText = (String) getGroup(grPosition);
        //TextView listTitleTextView = (TextView) convertView.findViewById(R.id.listTitle);
        //listTitleTextView.setTypeface(null, Typeface.BOLD);
        //listTitleTextView.setText(listTitle);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.explistext, null);
        }
        TextView textViewGroup = (TextView) convertView.findViewById(R.id.txtGDsc);
        //TextView textViewBrands = new TextView(context);
        textViewGroup.setText(grListText);//Mainlist[groupPosition] + " " + Sublist[groupPosition].length);
        // Definimos um alinhamento
     	textViewGroup.setPadding(45, 5, 0, 5);
     	// Definimos o tamanho do texto
     	textViewGroup.setTextSize(18);
     	// Definimos que o texto estará em negrito
     	textViewGroup.setTypeface(null, Typeface.BOLD);
     	//textViewBrands.setTextColor(R.color.White);//(WaterTank.getInstance().getResources().getColor(R.color.White));
		//if (Chronometer.sChrono.fstEv==false){
			TextView textItCount = (TextView) convertView.findViewById(R.id.txtItCount);
	     	textItCount.setText(String.valueOf(getChildrenCount(grPosition)));
			textItCount.setPadding(0, 5, 5, 5);
			textItCount.setTextSize(18);
			textItCount.setTypeface(null, Typeface.BOLD);
		//}
		//return textViewBrands;
        return convertView;
         
    }

    public void setNewItems(List<String> listGroup, HashMap<String, List<String>> listItem) {
        this.listGroup = listGroup;
        this.listItem = listItem;
        notifyDataSetChanged();
    }
    
    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int grPosition, int itPosition) {
        return true;
    }
}