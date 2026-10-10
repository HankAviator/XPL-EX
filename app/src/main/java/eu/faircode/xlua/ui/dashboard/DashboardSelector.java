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
        setPaddingRelative(spacing, 0, spacing, 0);
        setMinimumHeight(getResources().getDimensionPixelSize(R.dimen.dashboard_touch_target));
        setDropDownVerticalOffset(0);
    }

    /** Use a compact anchored popup instead of Spinner's full-screen system dialog. */
    @Override
    public boolean performClick() {
        dismissPopup();
        if (getAdapter() == null || getAdapter().getCount() == 0)
            return true;

        final ListView list = new ListView(getContext());
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        list.setDivider(null);
        list.setDividerHeight(0);
        int padding = Math.round(8 * getResources().getDisplayMetrics().density);
        list.setPadding(padding, padding, padding, padding);
        list.setClipToPadding(false);
        list.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        SpinnerAdapter adapter = getAdapter();
        if (!(adapter instanceof ListAdapter))
            return true;
        list.setAdapter(new android.widget.BaseAdapter() {
            public int getCount() { return adapter.getCount(); }
            public Object getItem(int position) { return adapter.getItem(position); }
            public long getItemId(int position) { return adapter.getItemId(position); }
            public View getView(int position, View recycled, android.view.ViewGroup parent) {
                View row = adapter.getDropDownView(position, recycled, parent);
                row.setActivated(position == getSelectedItemPosition());
                return row;
            }
        });
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
        popup.showAsDropDown(this, 0, padding);
        return true;
    }

    public void dismissPopup() {
        if (popup != null) popup.dismiss();
    }

    @Override protected void onDetachedFromWindow() {
        dismissPopup();
        super.onDetachedFromWindow();
    }
}
