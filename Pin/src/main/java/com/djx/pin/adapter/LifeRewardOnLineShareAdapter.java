package com.djx.pin.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.beans.LifeRewardOnlineDetailInfo;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.TurnIntoTime;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by Administrator on 2016/7/28 0028.
 */
public class LifeRewardOnLineShareAdapter extends RecyclerView.Adapter<LifeRewardOnLineShareAdapter.ViewHolder> implements View.OnClickListener {

    OnShareAvatarListener onShareAvatarListener;
    private Context context;
    List<LifeRewardOnlineDetailInfo.Result.Share> list;
    LayoutInflater inflater;

    View HeaderView;
    View FooterView;
    Map urlMap;
    public LifeRewardOnLineShareAdapter(Context context, OnShareAvatarListener onShareAvatarListener) {
        this.context = context;
        this.onShareAvatarListener = onShareAvatarListener;
        list = new ArrayList();
        inflater = LayoutInflater.from(context);
        urlMap=new HashMap();
    }

    public List getList(){
        return list;
    }
    public void addData(LifeRewardOnlineDetailInfo.Result.Share info) {
        list.add(info);
    }

    public void addHeader(View headerView) {
        HeaderView = headerView;
        notifyItemInserted(0);
    }

    public void addFooter(View footerView) {
        FooterView = footerView;
        notifyItemInserted(list.size() + 1);
    }

    public void clear() {
        list.clear();
    }

    @Override
    public int getItemViewType(int position) {
        if (HeaderView == null) {
            return 0;
        } else if (position == 0) {
            return 1;
        } else if (position == (list.size() + 1)) {
            return 2;
        }
        return 0;

    }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     * <p/>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p/>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #(ViewHolder, int, List)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(ViewHolder, int)
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ViewHolder viewHolder;
        View v = null;
        switch (viewType) {
            case 0:
                v = inflater.inflate(R.layout.lv_item_share, parent, false);

                break;
            case 1:
                v = HeaderView;

                break;
            case 2:
                v = FooterView;
                break;
        }
        viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#} to reflect the item at the given
     * position.
     * <p/>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link ViewHolder#()} which will
     * have the updated adapter position.
     * <p/>
     * Override {@link (ViewHolder, int, List)} instead if Adapter can
     * handle effcient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position > 0 && position < list.size() + 1) {
            LifeRewardOnlineDetailInfo.Result.Share shareInfo = list.get(position - 1);
            holder.tv_NickName_MHDA.setText(shareInfo.nickname);
            holder.tv_time_MHDA.setText(TurnIntoTime.getCreateTime(shareInfo.create_time));

            int shareType = shareInfo.type;
            switch (shareType) {
                case 1:
                    holder.tv_ShareCotent_MHDA.setText("分享到微信");
                    break;
                case 2:
                    holder.tv_ShareCotent_MHDA.setText("分享到QQ");
                    break;
                case 3:
                    holder.tv_ShareCotent_MHDA.setText("分享到微博");
                    break;
            }

            try {
                getOneImageViewUrl(holder.cimg_Avatar_MHDA, shareInfo.portrait, 1, 50, 50);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            holder.cimg_Avatar_MHDA.setTag(position);

            holder.cimg_Avatar_MHDA.setOnClickListener(this);
        }
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if (HeaderView == null) {
            return list.size();
        } else {
            return list.size() + 2;
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) manager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (getItemViewType(position) == 1) {
                        return gridLayoutManager.getSpanCount();
                    } else {
                        return 1;
                    }
                }
            });
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        onShareAvatarListener.setOnShareAvatarListener(v, 1);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView cimg_Avatar_MHDA;
        public TextView tv_NickName_MHDA;
        public TextView tv_time_MHDA;
        public TextView tv_ShareCotent_MHDA;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_NickName_MHDA = (TextView) itemView.findViewById(R.id.tv_NickName);
            tv_time_MHDA = (TextView) itemView.findViewById(R.id.tv_time);
            tv_ShareCotent_MHDA = (TextView) itemView.findViewById(R.id.tv_ShareCotent);
            cimg_Avatar_MHDA = (CircleImageView) itemView.findViewById(R.id.cimg);
        }
    }


    /**
     * 请求七牛图片下载地址,只有一个ImageView
     */
    public void getOneImageViewUrl(final ImageView imageView, String id, int media_type, int height, int width) throws UnsupportedEncodingException {
        String url= (String) urlMap.get(id);
        if (url!=null){
            Picasso.with(context.getApplicationContext()).load(url).into(imageView);
            return;
        }
        final JSONObject newObj = new JSONObject();
        try {
            newObj.put("size", 1);

            JSONArray array = new JSONArray();
            JSONObject detaileObj = new JSONObject();
            detaileObj.put("id", id);
            detaileObj.put("media_type", media_type);
            detaileObj.put("height", height);
            detaileObj.put("width", width);
            array.put(detaileObj);
            newObj.put("list", array);
            StringEntity entity = new StringEntity(newObj.toString(), "utf-8");

            AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    String str = new String(bytes);


                    try {
                        JSONObject obj = new JSONObject(str);

                        obj = obj.getJSONObject("result");
                        JSONArray array1 = obj.getJSONArray("list");
                        for (int j = 0; j < array1.length(); j++) {
                            obj = array1.getJSONObject(j);
                            urlMap.put(obj.getString("id"),obj.getString("url"));
                            Picasso.with(context.getApplicationContext()).load(obj.getString("url")).into(imageView);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    Log.e("失败", "失败");
                }
            };
            AndroidAsyncHttp.post(context.getApplicationContext(), ServerAPIConfig.GetQiNiuUrl, entity, " application/json;charset=UTF-8", res);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface OnShareAvatarListener {
        void setOnShareAvatarListener(View view, int type);
    }
}
