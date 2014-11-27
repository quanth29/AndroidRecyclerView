package com.example.belladunovska.recyclerviewtutorial;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 * Created by belladunovska on 25/11/14.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    boolean mItemPressed = false;
    boolean mSwiping = false;
    HashMap<Long, Integer> mItemIdTopMap = new HashMap<Long, Integer>();

    private static final int SWIPE_DURATION = 250;
    private static final int MOVE_DURATION = 150;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {


        float mDownX;
        private int mSwipeSlop = -1;

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public boolean onTouch(final View v, MotionEvent event) {
            if (!clicked) {
                if (mSwipeSlop < 0) {
                    mSwipeSlop = ViewConfiguration.get(c).
                            getScaledTouchSlop();
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mItemPressed) {
                            // Multi-item swipes not handled
                            return false;
                        }
                        mItemPressed = true;
                        mDownX = event.getX();
                        return false;
                    case MotionEvent.ACTION_CANCEL:
                        v.setAlpha(1);
                        v.setTranslationX(0);
                        mItemPressed = false;
                        return false;
                    case MotionEvent.ACTION_MOVE: {
                        float x = event.getX() + v.getTranslationX();
                        float deltaX = x - mDownX;
                        float deltaXAbs = Math.abs(deltaX);
                        if (!mSwiping) {
                            if (deltaXAbs > mSwipeSlop) {
                                mSwiping = true;
                                mRecyclerView.requestDisallowInterceptTouchEvent(true);
                            }
                        }
                        if (mSwiping) {
                            v.setTranslationX((x - mDownX));
                            v.setAlpha(1 - deltaXAbs / v.getWidth());
                        }
                    }
                    return true;
                    case MotionEvent.ACTION_UP: {
                        // User let go - figure out whether to animate the view out, or back into place
                        if (mSwiping) {
                            float x = event.getX() + v.getTranslationX();
                            float deltaX = x - mDownX;
                            float deltaXAbs = Math.abs(deltaX);
                            float fractionCovered;
                            float endX;
                            float endAlpha;
                            final boolean remove;
                            if (deltaXAbs > v.getWidth() / 4) {
                                // Greater than a quarter of the width - animate it out
                                fractionCovered = deltaXAbs / v.getWidth();
                                endX = deltaX < 0 ? -v.getWidth() : v.getWidth();
                                endAlpha = 0;
                                remove = true;
                            } else {
                                // Not far enough - animate it back
                                fractionCovered = 1 - (deltaXAbs / v.getWidth());
                                endX = 0;
                                endAlpha = 1;
                                remove = false;
                            }
                            // Animate position and alpha of swiped item
                            // NOTE: This is a simplified version of swipe behavior, for the
                            // purposes of this demo about animation. A real version should use
                            // velocity (via the VelocityTracker class) to send the item off or
                            // back at an appropriate speed.
                            long duration = (int) ((1 - fractionCovered) * SWIPE_DURATION);
                            mRecyclerView.setEnabled(false);
                            v.animate().setDuration(duration).
                                    alpha(endAlpha).translationX(endX).
                                    withEndAction(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Restore animated values
                                            v.setAlpha(1);
                                            v.setTranslationX(0);
                                            if (remove) {
                                                animateRemoval(mRecyclerView, v);
                                            } else {
                                                mSwiping = false;
                                                mRecyclerView.setEnabled(true);
                                            }
                                        }
                                    });
                        }
                    }

                    mItemPressed = false;
                    return false;
                    default:
                        return false;
                }
            }
            return false;
        }


    };
    private boolean clicked = false;
    private ArrayList<GregorianCalendar> mDataset;
    private ArrayList<String> events;
    private Context c;
    private RecyclerView mRecyclerView;

    public RecyclerViewAdapter(RecyclerView mRecyclerView, Context c, ArrayList<GregorianCalendar> mDataset) {
        this.mDataset = mDataset;
        this.c = c;
        this.events = new ArrayList<String>();
        events.add("one");
        events.add("two");
        events.add("three");
        events.add("four");
        events.add("five");
        events.add("six");
        this.mRecyclerView = mRecyclerView;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_view_item, viewGroup, false);
        RecyclerViewAdapter.ViewHolder vh = new RecyclerViewAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {
        if (clicked) {
            holder.mTextView.setText(events.get(getPosition(position - 1)));
        } else {
            GregorianCalendar d = mDataset.get(position);
            String day = DateUtils.formatDateTime(c, d.getTimeInMillis(), DateUtils.FORMAT_SHOW_WEEKDAY);
            String date = DateUtils.formatDateTime(c, d.getTimeInMillis(), DateUtils.FORMAT_NO_MONTH_DAY);
            String year = DateUtils.formatDateTime(c, d.getTimeInMillis(), DateUtils.FORMAT_SHOW_YEAR);
            holder.mTextView.setText(date + " " + day);
        }
        holder.mTextView.setTag(position);
    }


    //Calculates which element frome events do we neeed.
    public int getPosition(int clicked) {
        while (clicked >= events.size()) {
            clicked = clicked - (events.size());
        }
        return clicked;
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    public void addToList(GregorianCalendar cal, int position) {
        mDataset.add(cal);
        notifyItemInserted(position);
    }

    public void removeItemFromList(int position) {

        for (int i = 0; i < events.size(); i++) {
            int toRemove = 0;
            if (position == 0) {
                toRemove = mDataset.size() - position + i - 2;
            } else {
                toRemove = mDataset.size() - 1;
            }
            mDataset.remove(toRemove);
        }
        notifyDataSetChanged();
    }

    public void swapClicked() {
        if (clicked) {
            clicked = false;
        } else {
            clicked = true;
        }

    }

    // Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // public data item is just a string in this case
        public TextView mTextView;

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void onClick(View v) {
            if (!mSwiping) {
                int addedPosition = Integer.parseInt(mTextView.getTag().toString()) + 1;
                if (clicked) {
                    removeItemFromList(addedPosition);
                } else {
                    for (int i = 0; i < events.size(); i++) {
                        addToList(new GregorianCalendar(2014, 10, 23), addedPosition + i);
                    }
                }
                swapClicked();
            }
        }

        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.myTextView);
            v.setOnClickListener(this);
            v.setOnTouchListener(mTouchListener);
        }

    }

    public void remove(int position) {
        mDataset.remove(position);
        notifyDataSetChanged();
    }

    private void animateRemoval(final RecyclerView recyclerView, View viewToRemove) {
        int firstVisiblePosition = 0;
        for (int i = 0; i < recyclerView.getChildCount(); ++i) {
            View child = recyclerView.getChildAt(i);
            if (child != viewToRemove) {
                int position = firstVisiblePosition + i;
                long itemId = R.id.myTextView;
                mItemIdTopMap.put(itemId, child.getTop());
            }
        }
        // Delete the item from the adapter
        int position = recyclerView.getChildPosition(viewToRemove);
//        mAdapter.remove(mAdapter.getItem(position));
        remove(position);

        final ViewTreeObserver observer = recyclerView.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public boolean onPreDraw() {
                observer.removeOnPreDrawListener(this);
                boolean firstAnimation = true;
                int firstVisiblePosition = 0;
                for (int i = 0; i < recyclerView.getChildCount(); ++i) {
                    final View child = recyclerView.getChildAt(i);
                    int position = firstVisiblePosition + i;
                    long itemId = R.id.myTextView;
                    Integer startTop = mItemIdTopMap.get(itemId);
                    int top = child.getTop();
                    if (startTop != null) {
                        if (startTop != top) {
                            int delta = startTop - top;
                            child.setTranslationY(delta);
                            child.animate().setDuration(MOVE_DURATION).translationY(0);
                            if (firstAnimation) {
                                child.animate().withEndAction(new Runnable() {
                                    public void run() {
                                        mSwiping = false;
                                        recyclerView.setEnabled(true);
                                    }
                                });
                                firstAnimation = false;
                            }
                        }
                    } else {
                        // Animate new views along with the others. The catch is that they did not
                        // exist in the start state, so we must calculate their starting position
                        // based on neighboring views.
                        int childHeight = child.getHeight() + recyclerView.getHeight();
                        startTop = top + (i > 0 ? childHeight : -childHeight);
                        int delta = startTop - top;
                        child.setTranslationY(delta);
                        child.animate().setDuration(MOVE_DURATION).translationY(0);
                        if (firstAnimation) {
                            child.animate().withEndAction(new Runnable() {
                                public void run() {
                                    mSwiping = false;
                                    recyclerView.setEnabled(true);
                                }
                            });
                            firstAnimation = false;
                        }
                    }
                }
                mItemIdTopMap.clear();
                return true;
            }
        });
    }
}

