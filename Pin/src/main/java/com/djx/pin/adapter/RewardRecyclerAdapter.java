package com.djx.pin.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.activity.LoginActivity;
import com.djx.pin.beans.LifeRewardOnlineEntity;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.improve.common.PhotoShowActivity;
import com.djx.pin.myview.CircleImageView;
import com.djx.pin.myview.timepicker.common.ResizableImageView;
import com.djx.pin.utils.DateUtils;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.QiniuUtils;
import com.djx.pin.utils.ToastUtil;
import com.djx.pin.utils.myutils.PopupWindowUtils;
import com.djx.pin.widget.CustomImageView;
import com.djx.pin.widget.NineGridLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.rong.imkit.RongIM;

/**
 * Created by 柯传奇 on 2016/10/12 0012.
 */
public class RewardRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_COMMOM = 0, TYPE_FOOTVIEW = 1;
    private static final int STATE_LOAD_MORE = 1000, STATE_NO_MORE = 1001, STATE_EMPTY_ITEM = 1002, STATE_NETWORK_ERROR = 3;
    private final String session_id;
    private List<LifeRewardOnlineEntity.OnlineBean> lists;
    private ItemClickListener itemClickListener;
    private ItemAvatarClickListener itemAvatarClickListener;
    private Map<Integer, List<String>> map;
    private Activity context;
    private int state = STATE_LOAD_MORE;

    public RewardRecyclerAdapter(Activity context) {
        this.context = context;
        map = new HashMap<>();
        lists = new ArrayList<>();
        session_id = context.getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("session_id", null);
        layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }


    public void addAll(List<LifeRewardOnlineEntity.OnlineBean> lists) {
        this.lists.clear();
        this.lists.addAll(lists);
        notifyDataSetChanged();
    }

    public void addMoreData(List<LifeRewardOnlineEntity.OnlineBean> list) {
        this.lists.addAll(list);
        notifyDataSetChanged();
    }

    RecyclerView.LayoutParams layoutParams;
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTVIEW) {
            View view = View.inflate(context, R.layout.list_cell_footer, null);
            view.setLayoutParams(layoutParams);
            return new FootView(view);
        }
        return new CommonViewHolder(View.inflate(context, R.layout.item_recyadapter_reward_online, null));
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof CommonViewHolder) {
            final LifeRewardOnlineEntity.OnlineBean onlineBean = lists.get(position);
            final CommonViewHolder commonViewHolder = (CommonViewHolder) holder;
            commonViewHolder.tv_userName.setText(onlineBean.nickname);
            commonViewHolder.tv_time.setText(DateUtils.formatDate(new Date(onlineBean.start_time), DateUtils.yyyyMMDD) + "");
            commonViewHolder.tv_price.setText(onlineBean.price + "元");
            commonViewHolder.tv_viewNum.setText(onlineBean.view_num + "次浏览");
            commonViewHolder.tv_description.setText(onlineBean.description);
            commonViewHolder.tv_commentNum.setText(onlineBean.comment_num + "");
            commonViewHolder.tv_shareNum.setText(onlineBean.share_num + "");

            //判断订单状态
            if (onlineBean.status == 2) {//订单状态，0-发布未支付，1-发单成功，2-订单结束（停止招募）
                commonViewHolder.tv_order_closed.setVisibility(View.VISIBLE);
                commonViewHolder.tv_price.setVisibility(View.INVISIBLE);
            } else {
                //人已满
                commonViewHolder.tv_order_closed.setVisibility(View.INVISIBLE);
                commonViewHolder.tv_price.setVisibility(View.VISIBLE);
            }
            //头像
            if (onlineBean.portrait != null && onlineBean.portrait.length() > 0) {
                QiniuUtils.setAvatarByIdFrom7Niu(context,commonViewHolder.img_portrait,onlineBean.portrait);
            }else {
                commonViewHolder.img_portrait.setImageResource(R.mipmap.ic_defaultcontact);
            }
            //图片
            if (onlineBean.media != null && onlineBean.media.size() > 0) {
                final ArrayList<String> ids = new ArrayList<>();
                if (onlineBean.media.size() == 1) {
                    final String media_id = onlineBean.media.get(0).getMedia_id();
                    commonViewHolder.nineGridLayout.setVisibility(View.GONE);
                    commonViewHolder.oneImage.setVisibility(View.VISIBLE);
                    if(media_id != null){
                        QiniuUtils.setOneImageByIdFrom7Niu(context,commonViewHolder.oneImage, media_id);
                        commonViewHolder.oneImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ids.add(media_id);
                                Intent intent = new Intent(context, PhotoShowActivity.class);
                                intent.putExtra("CURRENT_POS", 0);
                                intent.putStringArrayListExtra("IDS", ids);
                                context.startActivity(intent);
                                //DialogUtils.showOneImage(context,ids.get(0),commonViewHolder.oneImage);
                            }
                        });
                    }
                } else if(onlineBean.media != null && onlineBean.media.size() > 1){
                    commonViewHolder.nineGridLayout.setVisibility(View.VISIBLE);
                    commonViewHolder.oneImage.setVisibility(View.GONE);
                    //图片urls
                    for (int i = 0; i < onlineBean.media.size(); i++) {
                        String media_id = onlineBean.media.get(i).getMedia_id();
                        ids.add(media_id);
                    }
                    QiniuUtils.set9GridByIdsFrom7Niu(context,ids,onlineBean.id,commonViewHolder.nineGridLayout);
                    map.put(position, ids);
                }
            } else {
                commonViewHolder.nineGridLayout.setVisibility(View.GONE);
                commonViewHolder.oneImage.setVisibility(View.GONE);
            }

        } else if (holder instanceof FootView) {
            FootView footView = (FootView) holder;
            switch (getState()) {
                case STATE_LOAD_MORE:
                    //footView.ll_footView.setVisibility(View.VISIBLE);
                    footView.progress.setVisibility(View.VISIBLE);
                    footView.text.setText("加载更多...");
                    break;
                case STATE_NO_MORE:
                    footView.progress.setVisibility(View.GONE);
                    footView.text.setVisibility(View.VISIBLE);
                    footView.text.setText("无更多数据");
                    break;
                case STATE_EMPTY_ITEM:
                    footView.progress.setVisibility(View.GONE);
                    footView.text.setText("暂无内容");
                    break;
                case STATE_NETWORK_ERROR:
                    footView.progress.setVisibility(View.GONE);
                    footView.text.setVisibility(View.VISIBLE);
                    footView.text.setText("加载出错了");
                    break;
                case 0:
                    break;
            }
        }

    }

    public int getState() {
        return this.state;
    }

    public void setState(int state) {
        this.state = state;
    }


    @Override
    public int getItemViewType(int position) {
        if (position == lists.size()) {
            return TYPE_FOOTVIEW;
        }
        return TYPE_COMMOM;
    }

    @Override
    public int getItemCount() {
        if (lists != null) {
            return lists.size() + 1;
        } else {
            return 0;
        }
    }

    public LifeRewardOnlineEntity.OnlineBean getItem(int position) {
        return lists.get(position);
    }

    View parentView;

    public void setParentView(RelativeLayout parentView) {
        this.parentView = parentView;
    }


    //footview
    public class FootView extends RecyclerView.ViewHolder {
        ProgressBar progress;
        TextView text;

        public FootView(View itemView) {
            super(itemView);
            progress = (ProgressBar) itemView.findViewById(R.id.progressbar);
            text = (TextView) itemView.findViewById(R.id.tv_name);
        }
    }


    class CommonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final CircleImageView portarit;
        private final ImageView iv_chat;
        TextView tv_userName;
        TextView tv_time;
        TextView tv_price;
        TextView tv_viewNum;
        TextView tv_description;
        TextView tv_commentNum;
        TextView tv_shareNum;
        CircleImageView img_portrait;
        NineGridLayout nineGridLayout;
        ResizableImageView oneImage;
        TextView tv_order_closed;

        public CommonViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            tv_userName = (TextView) itemView.findViewById(R.id.tv_userName);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_price = (TextView) itemView.findViewById(R.id.tv_price);
            tv_viewNum = (TextView) itemView.findViewById(R.id.tv_viewNum);
            tv_description = (TextView) itemView.findViewById(R.id.tv_description);
            tv_commentNum = (TextView) itemView.findViewById(R.id.tv_comment);
            tv_shareNum = (TextView) itemView.findViewById(R.id.tv_share);
            tv_order_closed = (TextView) itemView.findViewById(R.id.tv_order_closed);
            img_portrait = (CircleImageView) itemView.findViewById(R.id.img_portrait);
            nineGridLayout = (NineGridLayout) itemView.findViewById(R.id.imgs_9grid_layout);
            oneImage = (ResizableImageView) itemView.findViewById(R.id.iv_oneimage);
            portarit = (CircleImageView) itemView.findViewById(R.id.img_portrait);
            iv_chat = (ImageView) itemView.findViewById(R.id.iv_chat);

            nineGridLayout.setImageShowListener(new NineGridLayout.ImageShowListener() {
                @Override
                public void imageShow(int imgPos,ArrayList<CustomImageView> imageViews) {
                    List<String> ids = map.get(getLayoutPosition());
                    if (ids != null) {
                       /* DialogUtils.showImages(context,ids,imageViews,imgPos);*/
                        Intent intent = new Intent(context, PhotoShowActivity.class);
                        intent.putExtra("CURRENT_POS",imgPos);
                        intent.putStringArrayListExtra("IDS", ((ArrayList<String>) ids));
                        context.startActivity(intent);
                    }
                }
            });
            portarit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemAvatarClickListener.onItemAvatarClick(getLayoutPosition(), v);
                }
            });
            tv_commentNum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (context.getSharedPreferences(StaticBean.USER_INFO, context.MODE_PRIVATE).getBoolean("isLogined", false) == false) {
                        context.startActivity(new Intent(context,LoginActivity.class));
                        return;
                    }
                    final LifeRewardOnlineEntity.OnlineBean onlineBean = lists.get(getLayoutPosition());
                    final int comment_num = onlineBean.comment_num;
                    LogUtil.e("comment_num_-------------------------------------:" + comment_num);
                    PopupWindowUtils.commentPopupWindow(onlineBean.id, 7,session_id, context, parentView, new PopupWindowUtils.CommentSuccedCallBack() {
                        @Override
                        public void commentSucceed() {
                            tv_commentNum.setText((comment_num + 1) + "");
                        }
                    });
                }
            });
            tv_shareNum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (context.getSharedPreferences(StaticBean.USER_INFO, context.MODE_PRIVATE).getBoolean("isLogined", false) == false) {
                        context.startActivity(new Intent(context,LoginActivity.class));
                        return;
                    }
                    LifeRewardOnlineEntity.OnlineBean onlineBean = lists.get(getLayoutPosition());
                    PopupWindowUtils.sharePopupWindow(context, onlineBean.id, 7,session_id, onlineBean.description, parentView,false);
                }
            });
            iv_chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (context.getSharedPreferences(StaticBean.USER_INFO, context.MODE_PRIVATE).getBoolean("isLogined", false) == false) {
                        context.startActivity(new Intent(context,LoginActivity.class));
                        return;
                    }
                    LifeRewardOnlineEntity.OnlineBean onlineBean = lists.get(getLayoutPosition());
                    //检查是否是同一用户
                    String user_id = context.getSharedPreferences(StaticBean.USER_INFO, Context.MODE_PRIVATE).getString("user_id", null);
                    if (onlineBean.user_id.equals(user_id)) {
                        ToastUtil.shortshow(context, R.string.toast_error_talk);
                        return;
                    }
                    if (RongIM.getInstance() != null) {
                        if(chatClickListener != null){
                            chatClickListener.onItemChatClick(onlineBean.user_id, onlineBean.nickname);
                        }
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            if (context.getSharedPreferences(StaticBean.USER_INFO, context.MODE_PRIVATE).getBoolean("isLogined", false) == false) {
                Intent intent = new Intent(context,LoginActivity.class);
                context.startActivity(intent);
                return;
            }
            //获取item的位置
            itemClickListener.onItemClick(getLayoutPosition(), v);
        }
    }



    public void addOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void addOnItemAvatarClickListener(ItemAvatarClickListener avatarClickListener) {
        this.itemAvatarClickListener = avatarClickListener;
    }
    public void addOnItemChatClickListener(ItemChatClickListener chatClickListener) {
        this.chatClickListener = chatClickListener;
    }

    /**
     * 声明一个item的点击事件接口
     */
    public interface ItemClickListener {
        void onItemClick(int position, View itemView);
    }

    /**
     * 声明一个item的头像点击事件接口
     */
    public interface ItemAvatarClickListener {
        void onItemAvatarClick(int position, View view);
    }

    ItemChatClickListener chatClickListener;
    /**
     * 点击聊天接口
     */
    public interface ItemChatClickListener {
        void onItemChatClick(String user_id, String nickname);
    }

}
