package com.djx.pin.improve.positiveenergy.detail;

import com.djx.pin.beans.CivilizationDetailCommentInfo;
import com.djx.pin.improve.base.adapter.BaseRecyAdapter;
import com.djx.pin.improve.base.fragment.BaseRecyListFragment;
import com.djx.pin.improve.detail.Fragment2ActivityInterface;
import com.djx.pin.utils.myutils.ConstantUtils;

/**
 * Created by 柯传奇 on 2016/12/9 0009.
 */
public class WishTreeDetailFragment extends BaseRecyListFragment {

    private WishTreeDetailAdapter detailAdapter;
    private WishTreeDetailPresenter presenter;
    private String id;

    /**
     * @return BaseRecyAdapter实现类
     */
    @Override
    protected BaseRecyAdapter getListAdapter() {
        detailAdapter = new WishTreeDetailAdapter(context);
        detailAdapter.setIsAddHeadView(true);
        return detailAdapter;
    }

    @Override
    protected void initData() {
        id = getArguments().getString("id");
        presenter = new WishTreeDetailPresenter(getContext(),this);
        presenter.loadData(id,currentPage,2);
    }

    @Override
    public void onItemClick(int pos) {}

    @Override
    public void onRefresh() {
        isRefresh = true;
        presenter.loadData(id,currentPage,2);
    }

    public void loadSuccess() {
        swipeRefreshLayout.setRefreshing(false);
    }

    public void onNoMoreData() {
        isBottom = true;
        detailAdapter.setState(ConstantUtils.STATE_NO_MORE);
    }

    public void showData(final CivilizationDetailCommentInfo.Result result) {
        detailAdapter.setAdapterData(result);
        fragment2Activity.passData(result);
    }

    public void loadFail() {
        swipeRefreshLayout.setRefreshing(false);
    }

    Fragment2ActivityInterface fragment2Activity;
    public void setFragment2Activity(Fragment2ActivityInterface fragment2Activity) {
        this.fragment2Activity = fragment2Activity;
    }
}
