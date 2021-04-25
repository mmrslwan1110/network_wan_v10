package com.example.network_wan_v10.ui.gallery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.network_wan_v10.R;
import com.example.network_wan_v10.ui.gallery.bean.BluetoothMessage;

import java.util.List;


/**
 * Created by lenew on 2016/7/8 0008.
 */
public class MessageListAdapter extends BaseAdapter {

    private Context mContext;
    private List<BluetoothMessage> list;

    public MessageListAdapter(Context context,List list){
        mContext = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_msg_list,null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();

        BluetoothMessage message = list.get(position);

        if(0==message.getIsMe()){
            viewHolder.head.setText(message.getSenderNick());
            viewHolder.content.setText(message.getContent());
            viewHolder.myLayout.setVisibility(View.GONE);
            viewHolder.layout.setVisibility(View.VISIBLE);
        }else {
            viewHolder.myHead.setText("我");
            viewHolder.myContent.setText(message.getContent());

            viewHolder.myLayout.setVisibility(View.VISIBLE);
            viewHolder.layout.setVisibility(View.GONE);
        }



        return convertView;
    }


    public static class ViewHolder{
        public TextView content;
        public TextView head;

        public TextView myContent;
        public TextView myHead;

        public RelativeLayout layout;
        public RelativeLayout myLayout;

        public ViewHolder(View view){
            head = (TextView) view.findViewById(R.id.head);
            content = (TextView) view.findViewById(R.id.msg_content);

            myContent = (TextView) view.findViewById(R.id.my_msg_content);
            myHead = (TextView) view.findViewById(R.id.my_head);

            layout = (RelativeLayout) view.findViewById(R.id.layout);
            myLayout = (RelativeLayout) view.findViewById(R.id.my_layout);
        }

    }

}
