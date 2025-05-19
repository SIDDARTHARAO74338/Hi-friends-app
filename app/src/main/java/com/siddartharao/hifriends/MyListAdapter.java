package com.siddartharao.hifriends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class MyListAdapter extends BaseAdapter {

    private Context context;
    private List<String> mp3List;

    public MyListAdapter(Context context, List<String> mp3List) {
        this.context = context;
        this.mp3List = mp3List;
    }

    @Override
    public int getCount() {
        return mp3List.size();
    }

    @Override
    public Object getItem(int position) {
        return mp3List.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_back, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.text_view);
        textView.setText(mp3List.get(position));

        return convertView;
    }

}
