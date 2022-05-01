package ir.smartdevelopers.smartpopupmenu;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.ImageViewCompat;

import java.util.ArrayList;
import java.util.List;

public class SmartPopupMenu extends FrameLayout {
    private View mView;
    private final Rect offset=new Rect();
    private final LinearLayout mMenuLayout;
    private final ArrayList<MenuItem> mMenuItems=new ArrayList<>();
    private final int margin;
    private final int margin8;
    private final int deviceWidth;
    private final int deviceHeight;
    private boolean mShowDivider=true;
    private boolean mCancelable=true;
    private int itemTextColor;
    private int itemIconTintColor;
    private OnMenuItemClickListener mOnMenuItemClickListener;
    private boolean mShowing;
    public SmartPopupMenu(@NonNull Context context) {
        super(context);
        setOnClickListener(v->{
            if (mCancelable){
                close();
            }
        });
        DisplayMetrics metrics=getResources().getDisplayMetrics();
        mMenuLayout=new LinearLayout(context);
        mMenuLayout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        mMenuLayout.setOrientation(LinearLayout.VERTICAL);
        int padding= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,8,metrics);
        mMenuLayout.setPadding(0,padding,0,padding);
        setMenuBackgroundColor(Color.parseColor("#FEFEFE"));
        addView(mMenuLayout);
        margin= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,24,metrics);
        margin8= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,8,metrics);
        ViewCompat.setElevation(mMenuLayout,margin8);
        deviceWidth=metrics.widthPixels;
        deviceHeight=metrics.heightPixels;
        setFocusableInTouchMode(true);
        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK){
                    close();
                    return true;
                }
                return false;
            }
        });
    }




    public SmartPopupMenu addMenu(MenuItem menuItem){
        mMenuItems.add(menuItem);
        return this;
    }
    public SmartPopupMenu addMenus(List<MenuItem> menuItems){
        mMenuItems.addAll(menuItems);
        return this;
    }
    public void removeMenu(MenuItem menuItem){
        int menuPos=mMenuItems.indexOf(menuItem);
        if (menuPos==-1){
            return;
        }
        if (mMenuLayout.getChildCount() == 0){
            return;
        }
        int menuItemViewPos=mShowDivider ?  menuPos*2 : menuPos;
        int dividerViewPos=mShowDivider ? menuItemViewPos-1 : -1;
        mMenuLayout.removeViewAt(menuItemViewPos);
        if (dividerViewPos>=0){
            mMenuLayout.removeViewAt(dividerViewPos);
        }
        mMenuItems.remove(menuPos);
    }

    public SmartPopupMenu setShowDivider(boolean showDivider) {
        mShowDivider = showDivider;
        return this;
    }

    public SmartPopupMenu setCancelable(boolean cancelable) {
        mCancelable = cancelable;
        return this;
    }

    public void show(View view){
        mView=view;
        if (getParent() !=null){
            ((ViewGroup)getParent()).removeViewInLayout(this);
        }
        addAllMenus();
        mMenuLayout.measure(MeasureSpec.makeMeasureSpec(deviceWidth-(margin*2),MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(deviceHeight-(margin*2),MeasureSpec.AT_MOST) );
        view.getDrawingRect(offset);
        ViewGroup parent= (ViewGroup) view.getRootView();
        parent.offsetDescendantRectToMyCoords(view,offset);
        setBackgroundColor(Color.parseColor("#66000000"));
        ViewGroup.LayoutParams params=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        parent.addView(this,params);

        int y;
        int x;
        if (view.getRootView().getLayoutDirection()==LAYOUT_DIRECTION_RTL){
            int leftPos=offset.right + mMenuLayout.getMeasuredWidth() + margin8;
            if (leftPos > deviceWidth - margin){
                leftPos=deviceWidth - margin;
            }
            x=leftPos - mMenuLayout.getMeasuredWidth();
            mMenuLayout.setPivotX(0);
        }else {
            int xpos=offset.left-mMenuLayout.getMeasuredWidth()-margin8;
            if (xpos < margin){
                xpos=margin;
            }
            x=xpos;
            mMenuLayout.setPivotX(mMenuLayout.getMeasuredWidth());
        }
        if (offset.centerY()>deviceHeight/2){
            // in bottom middle
            int bottomPos=offset.bottom + (mMenuLayout.getMeasuredHeight()/3);
            if (bottomPos > deviceHeight - margin){
                bottomPos=deviceHeight - margin;
            }
            y=bottomPos-mMenuLayout.getMeasuredHeight();
        }else {
            // in top middle
            int ypos=offset.top - (mMenuLayout.getMeasuredHeight()/3);
            if (ypos < margin){
                ypos=margin;
            }
            y=ypos;
        }


        mMenuLayout.setY(y);

        mMenuLayout.setScaleX(0.9f);
        mMenuLayout.setScaleY(0.9f);
        mMenuLayout.setAlpha(0);

        mMenuLayout.setPivotY(mMenuLayout.getMeasuredHeight());
        setAlpha(0);
        post(()->{
            mMenuLayout.setX(x);
            int duration=150;
            animate().alpha(1).setDuration(duration).start();
            mMenuLayout.animate().setDuration(duration)
                    .alpha(1).scaleX(1).scaleY(1)
                    .setInterpolator(new OvershootInterpolator())
                    .start();
        });
        mShowing=true;
        requestFocus();
    }

    private void addAllMenus() {
        if (mMenuLayout!=null){
            mMenuLayout.removeAllViewsInLayout();
            for (MenuItem menuItem : mMenuItems){
                if (mMenuLayout.getChildCount()>0 && mShowDivider){
                    View divider= LayoutInflater.from(getContext()).inflate(R.layout.divider,mMenuLayout,false);
                    mMenuLayout.addView(divider);
                }
                View itemView=LayoutInflater.from(getContext()).inflate(R.layout.menu_item,mMenuLayout,false);
                TextView title=itemView.findViewById(R.id.txtTitle);
                ImageView imgIcon=itemView.findViewById(R.id.imgIcon);
                title.setText(menuItem.getTitle());
                if (itemTextColor!=0){
                    title.setTextColor(itemTextColor);
                }
                imgIcon.setImageResource(menuItem.getIconRes());
                if (itemIconTintColor!=0){
                    ImageViewCompat.setImageTintList(imgIcon,ColorStateList.valueOf(itemIconTintColor));
                }
                itemView.setTag(menuItem);
                itemView.setOnClickListener(v->{
                    if (mOnMenuItemClickListener != null) {
                        mOnMenuItemClickListener.onMenuItemClicked((MenuItem) itemView.getTag());
                    }
                    close();
                });
                mMenuLayout.addView(itemView);
            }
        }
    }

    public void close(){
        clearFocus();
        animate().alpha(0).start();
        mMenuLayout.animate().alpha(0).withEndAction(()->{
            if (getParent()!=null){
                ((ViewGroup)getParent()).removeView(this);
            }

        }).start();
        mShowing=false;

    }
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(offset.left,offset.top);
        mView.draw(canvas);
        canvas.restore();
        super.onDraw(canvas);

    }

    public void setMenuBackgroundColor(int menuBackgroundColor) {
        float cornerRadius=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,16,getResources().getDisplayMetrics());
        GradientDrawable bg=new GradientDrawable();
        bg.setColor(menuBackgroundColor);
        bg.setCornerRadius(cornerRadius);
        if (mMenuLayout != null) {
            mMenuLayout.setBackground(bg);
        }
    }

    public void setItemTextColor(int itemTextColor) {
        this.itemTextColor = itemTextColor;
    }

    public void setItemIconTintColor(int itemIconTintColor) {
        this.itemIconTintColor = itemIconTintColor;
    }

    public SmartPopupMenu setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        mOnMenuItemClickListener = onMenuItemClickListener;
        return this;
    }

    public void setMenuElevation(float elevation){
        if (mMenuLayout != null) {
            ViewCompat.setElevation(mMenuLayout,elevation);
        }
    }

    public boolean isShowing() {
        return mShowing;
    }

    public interface OnMenuItemClickListener{
        void onMenuItemClicked(MenuItem menuItem);
    }

}
