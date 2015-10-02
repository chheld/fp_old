package com.support.android.designlibdemo.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.support.android.designlibdemo.R;
import com.support.android.designlibdemo.model.Kontakt.Kontakt;

import java.util.ArrayList;

public class ContactListAdapter extends ArrayAdapter<Kontakt> {

    public ContactListAdapter(Context c, ArrayList<Kontakt> o) {
         super(c, R.layout.item_contactlist, o);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Kontakt kontakt = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_contactlist, parent, false);

            // Viewholder an View anbinden
            viewHolder = new ViewHolder();
            viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.ivKontakt);
            viewHolder.tvKdNr = (TextView) convertView.findViewById(R.id.tvKdNr);
            viewHolder.tvKTxt = (TextView) convertView.findViewById(R.id.tvKTxt);
            viewHolder.tvPersonnr = (TextView) convertView.findViewById(R.id.tvANr);
            viewHolder.tvKonkaktname = (TextView) convertView.findViewById(R.id.tvName);
            // Hier weitere Anbindungen hinzufuegen

            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Populate the data into the template view using the data object
        viewHolder.tvPersonnr.setText(kontakt.getPERSONNR());
        viewHolder.tvKTxt.setText(kontakt.getRELFIRMA_KTXT());
        viewHolder.tvKdNr.setText(kontakt.getFIRMANR());
        viewHolder.ivIcon.setImageResource(kontakt.getIcon());
        viewHolder.tvKonkaktname.setText(kontakt.getVORNAME() + " " +kontakt.getNAME());
        // Hier weitere Zuweisungen hinzufuegen

        return convertView;
    }

    // View lookup cache
    private static class ViewHolder {
        ImageView ivIcon;
        TextView tvKdNr;
        TextView tvKTxt;
        TextView tvPersonnr;
        TextView tvKonkaktname;
        // Hier weitere Holder-Eigenschaften hinzufuegen
    }

}
