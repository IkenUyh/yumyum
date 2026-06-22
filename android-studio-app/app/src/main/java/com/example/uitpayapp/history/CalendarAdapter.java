package com.example.uitpayapp.history;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<CalendarItem> items;
    private Date startDate = null;
    private Date endDate = null;
    private Date today; // Biến lưu ngày hiện tại đã khử thời gian

    public CalendarAdapter(List<CalendarItem> items) {
        this.items = items;

        // Khởi tạo ngày hiện tại ngay khi tạo Adapter để tránh tạo lại nhiều lần trong onBind
        Calendar calToday = Calendar.getInstance();
        calToday.set(Calendar.HOUR_OF_DAY, 0);
        calToday.set(Calendar.MINUTE, 0);
        calToday.set(Calendar.SECOND, 0);
        calToday.set(Calendar.MILLISECOND, 0);
        this.today = calToday.getTime();
    }

    public Date getStartDate() { return startDate; }
    public Date getEndDate() { return endDate; }

    public void clearSelection() {
        this.startDate = null;
        this.endDate = null;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView tv = new TextView(parent.getContext());
        if (viewType == CalendarItem.TYPE_MONTH_HEADER) {
            tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tv.setPadding(40, 48, 40, 24);
            tv.setTextSize(16);
            tv.setTextColor(Color.parseColor("#222222"));
            tv.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
            tv.setGravity(Gravity.CENTER);
            return new HeaderViewHolder(tv);
        } else {
            int width = parent.getWidth() / 7;
            LinearLayout container = new LinearLayout(parent.getContext());
            container.setLayoutParams(new ViewGroup.LayoutParams(width, width));
            container.setGravity(Gravity.CENTER);

            tv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(14);
            container.addView(tv);
            return new CellViewHolder(container, tv);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CalendarItem item = items.get(position);

        if (holder.getItemViewType() == CalendarItem.TYPE_MONTH_HEADER) {
            ((TextView) holder.itemView).setText(item.getText());
        } else if (holder.getItemViewType() == CalendarItem.TYPE_WEEK_HEADER) {
            CellViewHolder h = (CellViewHolder) holder;
            h.textView.setText(item.getText());
            h.textView.setTextColor(Color.parseColor("#757575"));
            h.container.setBackgroundColor(Color.TRANSPARENT);
        } else {
            CellViewHolder h = (CellViewHolder) holder;

            // Khôi phục trạng thái mặc định tránh lỗi trùng lặp khi reuse ViewHolder
            h.container.setBackgroundColor(Color.TRANSPARENT);
            h.textView.setBackground(null);
            h.textView.setTextColor(Color.parseColor("#212121"));

            if (item.isPadding()) {
                h.textView.setText("");
                h.itemView.setOnClickListener(null);
                return;
            }

            h.textView.setText(item.getText());

            if (item.getDate() != null) {
                // ĐÃ BỔ SUNG: Kiểm tra nếu ngày thuộc tương lai (sau ngày hiện tại)
                if (item.getDate().after(today)) {
                    h.textView.setTextColor(Color.parseColor("#CCCCCC")); // Làm nhạt màu chữ
                    h.itemView.setOnClickListener(null); // Hủy sự kiện click
                    h.itemView.setClickable(false); // Khóa phản hồi
                    return; // Ngắt tiến trình xử lý, không cho nhuộm màu vùng chọn
                }

                // Các ngày hợp lệ (Quá khứ + Hiện tại) xử lý logic chọn như bình thường
                h.itemView.setClickable(true);
                long time = item.getDate().getTime();

                if (startDate != null && endDate == null) {
                    if (time == startDate.getTime()) {
                        setCircleSelected(h, true);
                    }
                } else if (startDate != null && endDate != null) {
                    long start = startDate.getTime();
                    long end = endDate.getTime();

                    if (time == start) {
                        setCircleSelected(h, true);
                    } else if (time == end) {
                        setCircleSelected(h, false);
                    } else if (time > start && time < end) {
                        h.container.setBackgroundColor(Color.parseColor("#FFF0ED"));
                        h.textView.setTextColor(Color.parseColor("#EE4D2D"));
                    }
                }

                // Thiết lập sự kiện chọn ngày
                h.itemView.setOnClickListener(v -> {
                    if (startDate == null || (startDate != null && endDate != null)) {
                        startDate = item.getDate();
                        endDate = null;
                    } else {
                        if (item.getDate().before(startDate)) {
                            startDate = item.getDate();
                        } else {
                            endDate = item.getDate();
                        }
                    }
                    notifyDataSetChanged();
                });
            }
        }
    }

    private void setCircleSelected(CellViewHolder h, boolean isStart) {
        android.graphics.drawable.GradientDrawable shape = new android.graphics.drawable.GradientDrawable();
        shape.setShape(android.graphics.drawable.GradientDrawable.OVAL);
        shape.setColor(Color.parseColor("#EE4D2D"));

        h.textView.setBackground(shape);
        h.textView.setTextColor(Color.WHITE);

        if (endDate != null) {
            h.container.setBackgroundColor(Color.parseColor("#FFF0ED"));
        }
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        HeaderViewHolder(View v) { super(v); }
    }

    static class CellViewHolder extends RecyclerView.ViewHolder {
        View container;
        TextView textView;
        CellViewHolder(View c, TextView t) {
            super(c);
            container = c;
            textView = t;
        }
    }
}