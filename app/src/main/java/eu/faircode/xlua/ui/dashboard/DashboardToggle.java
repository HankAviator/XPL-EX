package eu.faircode.xlua.ui.dashboard;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import com.google.android.material.color.MaterialColors;

/** Switch presentation while retaining every existing CheckBox listener and selection contract. */
public class DashboardToggle extends AppCompatCheckBox {
    private ToggleDrawable toggleDrawable;

    public DashboardToggle(Context context, AttributeSet attrs) {
        super(context, attrs);
        toggleDrawable = new ToggleDrawable();
        setButtonDrawable(toggleDrawable);
        setMinHeight(dp(48));
        setMinWidth(dp(48));
        setCompoundDrawablePadding(dp(10));
        setButtonTintList(null);
    }

    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }

    @Override public void setButtonTintList(ColorStateList tint) {
        // Preserve the existing all/partial-assigned color indication, without tinting the thumb.
        if (toggleDrawable != null) {
            toggleDrawable.assignmentTint = tint;
            toggleDrawable.invalidateSelf();
        }
    }

    @Override public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName("android.widget.Switch");
    }

    private final class ToggleDrawable extends Drawable {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private float position = isChecked() ? 1f : 0f;
        private boolean checked = isChecked();
        private ValueAnimator animation;
        private ColorStateList assignmentTint;

        @Override public boolean isStateful() { return true; }
        @Override protected boolean onStateChange(int[] states) {
            boolean next = false;
            for (int state : states) if (state == android.R.attr.state_checked) next = true;
            if (next != checked) {
                checked = next;
                if (animation != null) animation.cancel();
                float target = next ? 1f : 0f;
                if (!isAttachedToWindow() || !isLaidOut()) position = target;
                else {
                    animation = ValueAnimator.ofFloat(position, target);
                    animation.setDuration(160);
                    animation.addUpdateListener(value -> {
                        position = (float) value.getAnimatedValue();
                        invalidateSelf();
                    });
                    animation.start();
                }
            }
            invalidateSelf();
            return true;
        }
        @Override public void jumpToCurrentState() {
            if (animation != null) animation.cancel();
            position = checked ? 1f : 0f;
            invalidateSelf();
        }
        @Override public void draw(@NonNull Canvas canvas) {
            Rect bounds = getBounds();
            float left = bounds.left + dp(4), top = bounds.top + dp(12);
            int primary = MaterialColors.getColor(DashboardToggle.this, com.google.android.material.R.attr.colorPrimary);
            int surface = MaterialColors.getColor(DashboardToggle.this, com.google.android.material.R.attr.colorSurfaceContainerHigh);
            int thumb = MaterialColors.getColor(DashboardToggle.this, com.google.android.material.R.attr.colorOnSurface);
            if (assignmentTint != null) primary = assignmentTint.getColorForState(getDrawableState(), primary);
            paint.setColor(checked ? primary : surface);
            paint.setAlpha(isEnabled() ? 255 : 95);
            canvas.drawRoundRect(left, top, left + dp(40), top + dp(24), dp(12), dp(12), paint);
            float direction = getLayoutDirection() == LAYOUT_DIRECTION_RTL ? 1f - position : position;
            paint.setColor(thumb);
            paint.setAlpha(isEnabled() ? 255 : 95);
            canvas.drawCircle(left + dp(12) + dp(16) * direction, top + dp(12), dp(8), paint);
        }
        @Override public int getIntrinsicWidth() { return dp(48); }
        @Override public int getIntrinsicHeight() { return dp(48); }
        @Override public void setAlpha(int alpha) { }
        @Override public void setColorFilter(ColorFilter filter) { }
        @Override public int getOpacity() { return PixelFormat.TRANSLUCENT; }
    }
}
