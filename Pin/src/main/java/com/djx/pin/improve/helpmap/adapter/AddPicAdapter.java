package com.djx.pin.improve.helpmap.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.djx.pin.R;
import com.djx.pin.utils.BitmapUtil;
import com.djx.pin.utils.myutils.ScreenTools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 柯传奇 on 2016/11/18 0018.
 */
public class AddPicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final int TYPE_ADD = 0;
    private static final int TYPE_COMMON = 1;
    private final int perWidth;
    private Context context;
    private List<String> list;
    private  BitmapUtil.SizeMessage  sizeMessage;

    public AddPicAdapter(Context context) {
        this.context = context;
        list = new ArrayList<>();
        sizeMessage = new BitmapUtil.SizeMessage(context, false, 160, 160);
        ScreenTools instance = ScreenTools.instance(context);
        int screenWidth = instance.getScreenWidth();
        perWidth = (screenWidth -instance.dip2px(92))/3;
    }
    public void addData(String path){
        this.list.add(path);
        notifyDataSetChanged();
    }

    public void addAll(List<String> list){
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if(position == list.size()){
            return TYPE_ADD;
        }else {
            return TYPE_COMMON;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_COMMON){
            return new CommonViewHold(View.inflate(context, R.layout.item_add_image,null));
        }else {
            return new ADDViewHold(View.inflate(context, R.layout.item_image_add,null));
        }
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if(holder instanceof CommonViewHold){
                ((CommonViewHold) holder).imageView.setImageBitmap(BitmapUtil.loadBitmap(list.get(position),sizeMessage));
                ((CommonViewHold) holder).delete.setVisibility(View.GONE);
            }

    }

    @Override
    public int getItemCount() {
        if(list.size() < 9){
            return list.size()+1;
        }else {
            return 9;
        }
    }

    class CommonViewHold extends RecyclerView.ViewHolder{
        ImageView imageView,delete;
        public CommonViewHold(View itemView) {
            super(itemView);
            imageView = ((ImageView) itemView.findViewById(R.id.iv_add));
            itemView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, perWidth));
            delete = ((ImageView) itemView.findViewById(R.id.iv_delete));
            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    delete.setVisibility(View.VISIBLE);
                    return true;
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    list.remove(getLayoutPosition());
                    notifyItemRemoved(getLayoutPosition());
                    if(deletePicListener != null){
                        deletePicListener.deletePic(getLayoutPosition());
                    }
                }
            });
        }
    }
    class ADDViewHold extends RecyclerView.ViewHolder{
        public ADDViewHold(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //打开相册
                    if(addPidListener!= null){
                        addPidListener.addPic();
                    }
                }
            });
        }
    }
    AddPicListener addPidListener;
    public void setAddPidListener(AddPicListener addPidListener) {
        this.addPidListener = addPidListener;
    }
    public interface AddPicListener{
        void addPic();
    }
    DeletePicListener deletePicListener;

    public void setDeletePicListener(DeletePicListener deletePicListener) {
        this.deletePicListener = deletePicListener;
    }

    public interface DeletePicListener{
        void deletePic(int pos);
    }
}
