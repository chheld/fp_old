package com.support.android.designlibdemo.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.support.android.designlibdemo.R;
import com.support.android.designlibdemo.model.Auftrag.Auftrag;

import java.util.ArrayList;

public class OrderListAdapter extends ArrayAdapter<Auftrag> {

    public OrderListAdapter(Context c, ArrayList<Auftrag> o) {
         super(c, R.layout.item_orderlist, o);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Auftrag ord = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_orderlist, parent, false);

            // Viewholder an View anbinden
            viewHolder = new ViewHolder();
            viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.ivOrder);
            viewHolder.tvANr = (TextView) convertView.findViewById(R.id.tvANr);
            viewHolder.tvKdNr = (TextView) convertView.findViewById(R.id.tvKdNr);
            viewHolder.tvKTxt = (TextView) convertView.findViewById(R.id.tvKTxt);
            viewHolder.tvBemerkung = (TextView) convertView.findViewById(R.id.tvBemerkung);
            // Hier weitere Anbindungen hinzufuegen
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Populate the data into the template view using the data object
        viewHolder.ivIcon.setImageResource(ord.getIcon());
        viewHolder.tvANr.setText(ord.getANR());
        viewHolder.tvKdNr.setText(ord.getMNR());
        viewHolder.tvKTxt.setText(ord.getKTXT());
        viewHolder.tvBemerkung.setText(ord.getBEMERKUNG());
        // Hier weitere Zuweisungen hinzufuegen

        return convertView;
    }

    // View lookup cache
    private static class ViewHolder {
        ImageView ivIcon;
        TextView tvANr;
        TextView tvKdNr;
        TextView tvKTxt;
        TextView tvBemerkung;
        // Hier weitere Holder-Eigenschaften hinzufuegen
    }

}
