package eu.faircode.xlua.ui.dashboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatCheckedTextView;

/** CheckedTextView-compatible collection row with a compact switch indicator. */
public class DashboardCheckedTextView extends AppCompatCheckedTextView {
    public DashboardCheckedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Draw the switch ourselves at the trailing edge. CheckedTextView's
        // built-in mark follows the text direction and was centered on some OEMs.
        setCheckMarkDrawable(null);
        setGravity(android.view.Gravity.CENTER_VERTICAL);
        setPaddingRelative(dp(16), dp(8), dp(64), dp(8));
        setMinHeight(dp(56));
    }
    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int right = getWidth() - dp(16);
        int top = (getHeight() - dp(24)) / 2;
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(isChecked() ? 0xFF8B5CF6 : 0xFF343946);
        canvas.drawRoundRect(right - dp(44), top, right, top + dp(24), dp(12), dp(12), p);
        p.setColor(isChecked() ? 0xFFFFFFFF : 0xFF9AA1B0);
        canvas.drawCircle(right - (isChecked() ? dp(12) : dp(32)), top + dp(12), dp(8), p);
    }
    @Override public void setChecked(boolean checked) {
        super.setChecked(checked);
        invalidate();
    }
    private int dp(int v) { return Math.round(v * getResources().getDisplayMetrics().density); }
}
