package com.djx.pin.widget;



import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.djx.pin.utils.myutils.ScreenTools;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 柯传奇 on 2016/10/13 0013.
 */
public class NineGridLayout extends ViewGroup {

    /**
     * 图片之间的间隔
     */
    private int gap = 8;
    private int columns;//
    private int rows;//
    private List listData;
    private int totalWidth;
    private ImageShowListener imageShowListener;
    private ArrayList<String> cacheUrls;
    private ArrayList<CustomImageView> customImageViews = new ArrayList<>();

    public NineGridLayout(Context context) {
        super(context);
    }
    public NineGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        ScreenTools screenTools=ScreenTools.instance(getContext());
        totalWidth=screenTools.getScreenWidth()-screenTools.dip2px(40);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    }
    private void layoutChildrenView(){
        customImageViews.clear();
        int childrenCount = listData.size();
        int singleWidth = (totalWidth - gap * (3 - 1)) / 3;
        int singleHeight = singleWidth;
        //根据子view数量确定高度
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = singleHeight * rows + gap * (rows - 1);
        setLayoutParams(params);
        for (int i = 0; i < childrenCount; i++) {
            CustomImageView childrenView = (CustomImageView) getChildAt(i);
            Log.e("civ","---------------childrenView----"+childrenView);
            Log.e("civ","---------------listData.get(i)----"+listData.get(i));
            Log.e("civ","---------------((Image) listData.get(i)).getUrl()----"+((Image) listData.get(i)).getUrl());
            childrenView.setImageUrl(((Image) listData.get(i)).getUrl());
            int[] position = findPosition(i);
            int left = (singleWidth + gap) * position[1];
            int top = (singleHeight + gap) * position[0];
            int right = left + singleWidth;
            int bottom = top + singleHeight;
            childrenView.layout(left, top, right, bottom);
            customImageViews.add(childrenView);
        }
    }
    private int[] findPosition(int childNum) {
        int[] position = new int[2];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if ((i * columns + j) == childNum) {
                    position[0] = i;//行
                    position[1] = j;//列
                    break;
                }
            }
        }
        return position;
    }
    public int getGap() {
        return gap;
    }
    public void setGap(int gap) {
        this.gap = gap;
    }
    public void setImagesData(List<Image> lists) {
        if (lists == null || lists.isEmpty()) {
            return;
        }
        String img_url;
        cacheUrls = new ArrayList<>();
        for (int i = 0; i < lists.size(); i++) {
            img_url = ((Image) lists.get(i)).getUrl();
            cacheUrls.add(img_url);
        }
        //初始化布局
        generateChildrenLayout(lists.size());
        //这里做一个重用view的处理
        if (listData == null) {
            int i = 0;
            while (i < lists.size()) {
                CustomImageView iv = generateImageView(i);
                addView(iv,generateDefaultLayoutParams());
                i++;
            }
        } else {
            int oldViewCount = listData.size();
            int newViewCount = lists.size();
            if (oldViewCount > newViewCount) {
                removeViews(newViewCount - 1, oldViewCount - newViewCount);
            } else if (oldViewCount < newViewCount) {
                for (int i = 0; i < newViewCount - oldViewCount; i++) {
                    CustomImageView iv = generateImageView(i);
                    addView(iv,generateDefaultLayoutParams());
                }
            }
        }
        listData = lists;
        layoutChildrenView();
    }

    /**
     * 根据图片个数确定行列数量
     * 对应关系如下
     * num	row	column
     * 1	   1	1
     * 2	   1	2
     * 3	   1	3
     * 4	   2	2
     * 5	   2	3
     * 6	   2	3
     * 7	   3	3
     * 8	   3	3
     * 9	   3	3
     *
     * @param length
     */
    private void generateChildrenLayout(int length) {
        if (length <= 3) {
            rows = 1;
            columns = length;
        } else if (length <= 6) {
            rows = 2;
            columns = 3;
            if (length == 4) {
                columns = 2;
            }
        } else {
            rows = 3;
            columns = 3;
        }
    }

    private CustomImageView generateImageView(final int imgPos) {
        final CustomImageView iv = new CustomImageView(getContext());
        NineGridLayout.LayoutParams layoutParams = new NineGridLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        iv.setLayoutParams(layoutParams);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageShowListener != null){
                    imageShowListener.imageShow(imgPos,customImageViews);
                }
            }
        });
        iv.setBackgroundColor(Color.parseColor("#f5f5f5"));
        return iv;
    }

    public void setImageShowListener(ImageShowListener imageShowListener){
        this.imageShowListener = imageShowListener;
    }
    public interface ImageShowListener{
        void imageShow(int imgPos, ArrayList<CustomImageView> imageViews);
    }

}
