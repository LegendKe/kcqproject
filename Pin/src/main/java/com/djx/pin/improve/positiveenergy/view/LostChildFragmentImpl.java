package com.djx.pin.improve.positiveenergy.view;

import com.djx.pin.beans.LostChildInfo;
import com.djx.pin.improve.base.adapter.BaseRecyAdapter;
import com.djx.pin.improve.base.fragment.BaseRecyListFragment;
import com.djx.pin.improve.positiveenergy.adapter.LostChildAdapter;
import com.djx.pin.improve.positiveenergy.presenter.LostChildPressenterImpl;
import com.djx.pin.utils.myutils.ConstantUtils;

import java.util.List;

/**
 * Created by 柯传奇 on 2016/12/8 0008.
 */
public class LostChildFragmentImpl extends BaseRecyListFragment implements LostChildFramentView{

    private LostChildPressenterImpl presenter;
    private LostChildAdapter lostChildAdapter;

    @Override
    protected void initView() {
        super.initView();
        presenter = new LostChildPressenterImpl(getContext(),this);
        presenter.loadData(currentPage);
    }

    /**
     * @return BaseRecyAdapter实现类
     */
    @Override
    protected BaseRecyAdapter getListAdapter() {
        lostChildAdapter = new LostChildAdapter(context);
        return lostChildAdapter;
    }

    @Override
    public void onItemClick(int pos) {
        presenter.dealWithItemClick(lostChildAdapter,pos);
    }

    @Override
    public void onRefresh() {
        currentPage = 0;
        isBottom = false;
        isRefresh = true;
        presenter.loadData(0);
    }

    /**
     * 显示数据
     *
     * @param list
     */
    @Override
    public void showData(List<LostChildInfo.Result.ChildInfo> list) {
        if(isRefresh){
            lostChildAdapter.addAll(list);
        }else {
            lostChildAdapter.addMoreData(list);
        }
    }

    @Override
    public void hideProgress() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showPregress() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void noMoreData() {
        isBottom = true;
        lostChildAdapter.setState(ConstantUtils.STATE_NO_MORE);
    }

    @Override
    public void loadError() {
        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * recyclerView滑到底,列表加载更多
     */
    @Override
    protected void loadMoreData() {
        presenter.loadData(currentPage);
    }
}
