package com.order.print.ui.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.order.print.R;
import com.order.print.bean.BluetoothBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pt198 on 05/09/2018.
 */

public class DeviceAdapter extends BaseAdapter {
    private List<BluetoothBean> mDatas=new ArrayList<>();
    private Context mContext;
    public DeviceAdapter(Context context, List<BluetoothBean> datas){
        mContext=context;
        setData(datas);
    }
    private void setData(List<BluetoothBean> datas){
        this.mDatas.clear();
        if(datas!=null) {
            this.mDatas.addAll(datas);
        }
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int i) {
        return mDatas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder=null;
        if(view==null){
            view= LayoutInflater.from(mContext).inflate(R.layout.list_item_device,null);
            holder=new ViewHolder();
            holder.tv=view.findViewById(R.id.tv_name);
            view.setTag(holder);
        }else{
            holder=(ViewHolder) view.getTag();
        }
        BluetoothBean bean=mDatas.get(i);
        holder.tv.setText(bean.mBluetoothName);
        return view;
    }
    private class ViewHolder{
        private TextView tv;
    }
}
