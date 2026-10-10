package eu.faircode.xlua.ui.dashboard;

import android.content.Context;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatSpinner;
import eu.faircode.xlua.R;

/** Dialog-based selection with the same adapter, indices and events as legacy selectors. */
public class DashboardSelector extends AppCompatSpinner {
    public DashboardSelector(Context context, AttributeSet attrs) {
        // Spinner.MODE_DIALOG is hidden by AppCompatSpinner; zero is the public
        // framework value and keeps the selector dialog-based on supported APIs.
        super(context, attrs, androidx.appcompat.R.attr.spinnerStyle, 0);
        setBackgroundResource(R.drawable.dashboard_card_background);
        int spacing = getResources().getDimensionPixelSize(R.dimen.dashboard_spacing);
        setPaddingRelative(spacing, spacing / 2, spacing, spacing / 2);
        setMinimumHeight(getResources().getDimensionPixelSize(R.dimen.dashboard_touch_target));
    }
}
