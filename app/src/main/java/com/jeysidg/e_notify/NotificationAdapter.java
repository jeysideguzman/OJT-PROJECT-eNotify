package com.jeysidg.e_notify;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private List<NotificationModel> notificationList;

    public NotificationAdapter(List<NotificationModel> notificationList) {
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_notifhistory, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationModel notification = notificationList.get(position);

        // Bind notification data to ViewHolder views
        holder.visitorNameTextView.setText(notification.getVisitorsName());
        holder.timestampTextView.setText(notification.getTimestamp());
        holder.companyClientTextView.setText(notification.getCompanyClient());
        holder.purposeofVisitTextview.setText(notification.getPurposeofVisit());

    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView visitorNameTextView, timestampTextView, companyClientTextView, purposeofVisitTextview;

        public ViewHolder(View itemView) {
            super(itemView);
            visitorNameTextView = itemView.findViewById(R.id.visitorNameTv);
            timestampTextView = itemView.findViewById(R.id.timestampTv);
            companyClientTextView = itemView.findViewById(R.id.companyClientTv);
            purposeofVisitTextview = itemView.findViewById(R.id.purposeOfVisitTv);
        }
    }
}

