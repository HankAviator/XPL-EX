package eu.faircode.xlua.ui.dashboard;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.SpinnerAdapter;
import androidx.appcompat.widget.AppCompatSpinner;
import eu.faircode.xlua.R;

/** Dialog-based selection with the same adapter, indices and events as legacy selectors. */
public class DashboardSelector extends AppCompatSpinner {
    private PopupWindow popup;

    public DashboardSelector(Context context, AttributeSet attrs) {
        // Spinner.MODE_DIALOG is hidden by AppCompatSpinner; zero is the public
        // framework value and keeps the selector dialog-based on supported APIs.
        super(context, attrs, androidx.appcompat.R.attr.spinnerStyle, 0);
        setBackgroundResource(R.drawable.dashboard_card_background);
        int spacing = getResources().getDimensionPixelSize(R.dimen.dashboard_spacing);
        setPaddingRelative(spacing, spacing / 2, spacing, spacing / 2);
        setMinimumHeight(getResources().getDimensionPixelSize(R.dimen.dashboard_touch_target));
        setDropDownVerticalOffset(0);
    }

    /** Use a compact anchored popup instead of Spinner's full-screen system dialog. */
    @Override
    public boolean performClick() {
        if (getAdapter() == null || getAdapter().getCount() == 0)
            return true;

        final ListView list = new ListView(getContext());
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        list.setDivider(null);
        list.setDividerHeight(0);
        list.setPadding(8, 8, 8, 8);
        list.setClipToPadding(false);
        list.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        SpinnerAdapter adapter = getAdapter();
        if (!(adapter instanceof ListAdapter))
            return true;
        list.setAdapter((ListAdapter) adapter);
        int selected = getSelectedItemPosition();
        if (selected >= 0)
            list.setItemChecked(selected, true);

        int width = getWidth();
        if (width <= 0)
            width = getResources().getDisplayMetrics().widthPixels - 32;
        int rowHeight = Math.round(52 * getResources().getDisplayMetrics().density);
        int wantedHeight = rowHeight * adapter.getCount() + list.getPaddingTop() + list.getPaddingBottom();
        int maxHeight = Math.round(getResources().getDisplayMetrics().heightPixels * 0.62f);

        popup = new PopupWindow(list, width, Math.min(wantedHeight, maxHeight), true);
        popup.setBackgroundDrawable(getContext().getDrawable(R.drawable.dashboard_popup_background));
        popup.setOutsideTouchable(true);
        popup.setElevation(Math.round(12 * getResources().getDisplayMetrics().density));
        popup.setAnimationStyle(android.R.style.Animation_Dialog);
        list.setOnItemClickListener((parent, view, position, id) -> {
            setSelection(position);
            if (popup != null)
                popup.dismiss();
        });
        popup.setOnDismissListener(() -> popup = null);
        popup.showAsDropDown(this, 0, -getHeight());
        return true;
    }
}
