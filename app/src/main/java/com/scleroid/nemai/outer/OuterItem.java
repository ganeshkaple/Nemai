package com.scleroid.nemai.outer;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ramotion.garlandview.header.HeaderDecorator;
import com.ramotion.garlandview.header.HeaderItem;
import com.ramotion.garlandview.inner.InnerLayoutManager;
import com.ramotion.garlandview.inner.InnerRecyclerView;
import com.scleroid.nemai.R;
import com.scleroid.nemai.inner.InnerAdapter;
import com.scleroid.nemai.inner.InnerModel;

import java.util.ArrayList;
import java.util.List;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

/**
 * Created by Ganesh on 15-11-2017.
 */

public class OuterItem extends HeaderItem {

    private final static float MIDDLE_RATIO_START = 0.7f;
    private final static float MIDDLE_RATIO_MAX = 0.1f;
    private final static float MIDDLE_RATIO_DIFF = MIDDLE_RATIO_START - MIDDLE_RATIO_MAX;

    private final static float FOOTER_RATIO_START = 1.1f;
    private final static float FOOTER_RATIO_MAX = 0.35f;
    private final static float FOOTER_RATIO_DIFF = FOOTER_RATIO_START - FOOTER_RATIO_MAX;

    private final static float ANSWER_RATIO_START = 0.75f;
    private final static float ANSWER_RATIO_MAX = 0.35f;
    private final static float ANSWER_RATIO_DIFF = ANSWER_RATIO_START - ANSWER_RATIO_MAX;

    private final static float AVATAR_RATIO_START = 1f;
    private final static float AVATAR_RATIO_MAX = 0.25f;
    private final static float AVATAR_RATIO_DIFF = AVATAR_RATIO_START - AVATAR_RATIO_MAX;


    private final View mHeader;
    private final View mHeaderAlpha;

    private final InnerRecyclerView mRecyclerView;

    private final TextView mHeaderCaption1;
    private final TextView mHeaderCaption2;
    private final TextView source;
    private final TextView destination;

    private final View mMiddle;
    private final View mMiddleAnswer;
    private final View mFooter;

    private final List<View> mMiddleCollapsible = new ArrayList<>(2);

    private final int m10dp;
    private final int m120dp;
    private final int mTitleSize1;
    private final int mTitleSize2;

    private boolean mIsScrolling;

    public OuterItem(View itemView, RecyclerView.RecycledViewPool pool) {
        super(itemView);

        // Init header
        m10dp = itemView.getContext().getResources().getDimensionPixelSize(R.dimen.dp10);
        m120dp = itemView.getContext().getResources().getDimensionPixelSize(R.dimen.dp120);
        mTitleSize1 = itemView.getContext().getResources().getDimensionPixelSize(R.dimen.header_title2_text_size);
        mTitleSize2 = itemView.getContext().getResources().getDimensionPixelSize(R.dimen.header_title2_name_text_size);

        mHeader = itemView.findViewById(R.id.header);
        mHeaderAlpha = itemView.findViewById(R.id.header_alpha);

        mHeaderCaption1 = itemView.findViewById(R.id.header_text_1);
        mHeaderCaption2 = itemView.findViewById(R.id.header_text_2);
        source = itemView.findViewById(R.id.tv_name);
        destination = itemView.findViewById(R.id.tv_info);


        mMiddle = itemView.findViewById(R.id.header_middle);
        mMiddleAnswer = itemView.findViewById(R.id.header_middle_answer);
        mFooter = itemView.findViewById(R.id.header_footer);

        //  mMiddleCollapsible.add((View)mAvatar.getParent());
        mMiddleCollapsible.add((View) source.getParent());

        // Init RecyclerView
        mRecyclerView = itemView.findViewById(R.id.recycler_view);
        mRecyclerView.setRecycledViewPool(pool);
        mRecyclerView.setAdapter(new InnerAdapter());

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                mIsScrolling = newState != RecyclerView.SCROLL_STATE_IDLE;
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                onItemScrolled(recyclerView, dx, dy);
            }
        });

        mRecyclerView.addItemDecoration(new HeaderDecorator(
                itemView.getContext().getResources().getDimensionPixelSize(R.dimen.inner_item_height),
                itemView.getContext().getResources().getDimensionPixelSize(R.dimen.inner_item_offset)));

        // Init fonts
        DataBindingUtil.bind(((FrameLayout) mHeader).getChildAt(0));
    }

    @Override
    public View getHeader() {
        return mHeader;
    }

    @Override
    public View getHeaderAlphaView() {
        return mHeaderAlpha;
    }

    @Override
    public boolean isScrolling() {
        return mIsScrolling;
    }

    @Override
    public InnerRecyclerView getViewGroup() {
        return mRecyclerView;
    }

    void setContent(@NonNull List<InnerModel> innerDataList) {
        final Context context = itemView.getContext();

        final InnerModel header = innerDataList.subList(0, 1).get(0);
        final List<InnerModel> tail = innerDataList.subList(1, innerDataList.size());

        mRecyclerView.setLayoutManager(new InnerLayoutManager());
        ((InnerAdapter) mRecyclerView.getAdapter()).addData(tail);


        final String title1 = header.name + "?";

        final Spannable title2 = new SpannableString(header.name + "? - " + header.mobileNo);
        title2.setSpan(new AbsoluteSizeSpan(mTitleSize1), 0, title1.length(), SPAN_INCLUSIVE_INCLUSIVE);
        title2.setSpan(new AbsoluteSizeSpan(mTitleSize2), title1.length(), title2.length(), SPAN_INCLUSIVE_INCLUSIVE);
        title2.setSpan(new ForegroundColorSpan(Color.argb(204, 255, 255, 255)), title1.length(), title2.length(), SPAN_INCLUSIVE_INCLUSIVE);

        mHeaderCaption1.setText(title1);
        mHeaderCaption2.setText(title2);

        source.setText(String.format("%s %s", header.name, context.getString(R.string.asked)));
        destination.setText(String.format("%s %s", header.address, header.address));
    }

    void clearContent() {
        // Glide.clear(mAvatar);
        ((InnerAdapter) mRecyclerView.getAdapter()).clearData();
    }

    private float computeRatio(RecyclerView recyclerView) {
        final View child0 = recyclerView.getChildAt(0);
        final int pos = recyclerView.getChildAdapterPosition(child0);
        if (pos != 0) {
            return 0;
        }

        final int height = child0.getHeight();
        final float y = Math.max(0, child0.getY());
        return y / height;
    }

    private void onItemScrolled(RecyclerView recyclerView, int dx, int dy) {
        final float ratio = computeRatio(recyclerView);

        final float footerRatio = Math.max(0, Math.min(FOOTER_RATIO_START, ratio) - FOOTER_RATIO_DIFF) / FOOTER_RATIO_MAX;
        final float avatarRatio = Math.max(0, Math.min(AVATAR_RATIO_START, ratio) - AVATAR_RATIO_DIFF) / AVATAR_RATIO_MAX;
        final float answerRatio = Math.max(0, Math.min(ANSWER_RATIO_START, ratio) - ANSWER_RATIO_DIFF) / ANSWER_RATIO_MAX;
        final float middleRatio = Math.max(0, Math.min(MIDDLE_RATIO_START, ratio) - MIDDLE_RATIO_DIFF) / MIDDLE_RATIO_MAX;

        ViewCompat.setPivotY(mFooter, 0);
        ViewCompat.setScaleY(mFooter, footerRatio);
        ViewCompat.setAlpha(mFooter, footerRatio);

        ViewCompat.setPivotY(mMiddleAnswer, mMiddleAnswer.getHeight());
        //   ViewCompat.setScaleY(mMiddleAnswer, 1f - answerRatio);
        //     ViewCompat.setAlpha(mMiddleAnswer, 0.5f - answerRatio);

        ViewCompat.setAlpha(mHeaderCaption1, answerRatio);
        ViewCompat.setAlpha(mHeaderCaption2, 1f - answerRatio);

        final View mc2 = mMiddleCollapsible.get(1);
        ViewCompat.setPivotX(mc2, 0);
        ViewCompat.setPivotY(mc2, mc2.getHeight() / 2);

        for (final View view : mMiddleCollapsible) {
            ViewCompat.setScaleX(view, avatarRatio);
            ViewCompat.setScaleY(view, avatarRatio);
            ViewCompat.setAlpha(view, avatarRatio);
        }

        final ViewGroup.LayoutParams lp = mMiddle.getLayoutParams();
        lp.height = m120dp - (int) (m10dp * (1f - middleRatio));
        mMiddle.setLayoutParams(lp);
    }


}
