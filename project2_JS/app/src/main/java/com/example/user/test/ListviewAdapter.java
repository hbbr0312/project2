package com.example.user.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListviewAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<Listviewitem> data;
    private int layout;
    private Context c;

    public ListviewAdapter(Context context, int layout, ArrayList<Listviewitem> data){
        this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data=data;
        this.layout=layout;
        this.c = context;
    }
    @Override
    public int getCount(){return data.size();}
    @Override
    public String getItem(int position){return data.get(position).getName();}
    @Override
    public long getItemId(int position){return position;}
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView==null){
            convertView=inflater.inflate(layout,parent,false);
        }
        Listviewitem listviewitem=data.get(position);
        /**프로필*/
        ImageView icon=(ImageView)convertView.findViewById(R.id.profile);
        icon.setImageBitmap(listviewitem.getIcon()); //기본 카카오이미지로 아이콘
        /**이름*/
        TextView name= (TextView)convertView.findViewById(R.id.fname);
        name.setText(listviewitem.getName());
        /**아이디*/
        TextView id=(TextView)convertView.findViewById(R.id.fid);
        id.setText(listviewitem.getDate());
        /**날짜*/
        //TextView date=(TextView)convertView.findViewById(R.id.fdate);
        //date.setText(listviewitem.getDate());
        /**사진*/
        ImageView photo=(ImageView)convertView.findViewById(R.id.fphoto);
        photo.setImageBitmap(listviewitem.getPhoto());
        /**텍스트*/
        TextView text = (TextView)convertView.findViewById(R.id.ftext);
        text.setText(listviewitem.getText());
        return convertView;
    }
}