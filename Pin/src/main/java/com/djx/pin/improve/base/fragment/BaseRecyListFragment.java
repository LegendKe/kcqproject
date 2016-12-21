package com.djx.pin.improve.base.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.djx.pin.R;
import com.djx.pin.improve.base.adapter.BaseRecyAdapter;
import com.djx.pin.utils.myutils.ConstantUtils;

import butterknife.Bind;

/**
 * Created by 柯传奇 on 2016/12/1 0002.
 */
public abstract class BaseRecyListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, BaseRecyAdapter.OnItemClickListener {

    private LinearLayoutManager layoutManager;
    private BaseRecyAdapter adapter;

    protected int currentPage = 0;//加载的当前页码
    protected boolean isRefresh = true;//标志当前为刷新还是加载更多
    protected boolean isBottom;//标志数据是否被加载完


    @Override
    protected int setLayoutId() {
        return R.layout.fragment_base_list;
    }

    @Bind(R.id.recyclerView)
    protected RecyclerView recyclerView;
    @Bind(R.id.swiperefreshlayout)
    protected SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.rl_emptyLayout)
    RelativeLayout relativeLayout;
    @Bind(R.id.framelayout_parent)
    protected FrameLayout framelayout_parent;
    protected int dyScroll,firstVisibleItemPosition;//dyScroll>0 : 上滑（滑动速度）
  @Override
    protected void initView() {
      swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
      layoutManager = new LinearLayoutManager(getContext());
      recyclerView.setLayoutManager(layoutManager);
      recyclerView.setHasFixedSize(true);
      adapter = getListAdapter();
      recyclerView.setAdapter(adapter);
      adapter.setOnItemClickListener(this);
    }

    protected OnScrollStateChangeListener getScrollStateChangeListener() {
        return null;
    }
    public interface OnScrollStateChangeListener{
        void onChanged(int top,int dy,int firstVisibleItemPosition);
    }
    @Override
    protected void initEvent() {
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            public int lastVisibleItem;
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem =layoutManager.findLastVisibleItemPosition();
                dyScroll = dy;
                firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                int top = 0;
                if(recyclerView.getChildAt(0) != null){
                    top = recyclerView.getChildAt(0).getTop();
                }
                //滑到顶部
                //通知需要更新状态的对象
                if(getScrollStateChangeListener() != null){
                    getScrollStateChangeListener().onChanged(top,dy,firstVisibleItemPosition);
                }
            }
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState ==RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 ==adapter.getItemCount() && isBottom == false) {

                    currentPage++;
                    adapter.setState(ConstantUtils.STATE_LOAD_MORE);
                    isRefresh = false;

                    loadMoreData();
                }
            }
        });
    }

    /**
     * recyclerView滑到底,列表加载更多
     */
    protected void loadMoreData() {

    }
    /**
     * @return BaseRecyAdapter实现类
     */
    protected abstract BaseRecyAdapter getListAdapter();

}
