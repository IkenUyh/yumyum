package com.example.uitpayapp.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ChatMessage> messageList;

    public ChatAdapter(List<ChatMessage> messageList) {
        this.messageList = messageList;
    }

    @Override
    public int getItemViewType(int position) {
        return messageList.get(position).getSenderType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == ChatMessage.TYPE_SYSTEM) {
            return new SystemViewHolder(inflater.inflate(R.layout.activity_history_item_chat_system, parent, false));
        } else if (viewType == ChatMessage.TYPE_USER) {
            return new UserViewHolder(inflater.inflate(R.layout.activity_history_item_chat_user, parent, false));
        } else {
            return new DriverViewHolder(inflater.inflate(R.layout.activity_history_item_chat_driver, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage msg = messageList.get(position);
        if (holder instanceof SystemViewHolder) {
            ((SystemViewHolder) holder).tvText.setText(msg.getText());
            ((SystemViewHolder) holder).tvName.setText(msg.getSenderName());
        } else if (holder instanceof UserViewHolder) {
            ((UserViewHolder) holder).tvText.setText(msg.getText());
            ((UserViewHolder) holder).tvName.setText(msg.getSenderName());
        } else if (holder instanceof DriverViewHolder) {
            ((DriverViewHolder) holder).tvText.setText(msg.getText());
            ((DriverViewHolder) holder).tvName.setText(msg.getSenderName());
        }
    }

    @Override
    public int getItemCount() { return messageList.size(); }

    static class SystemViewHolder extends RecyclerView.ViewHolder {
        TextView tvText, tvName;
        SystemViewHolder(View v) { super(v); tvText = v.findViewById(R.id.tvSystemText); tvName = v.findViewById(R.id.tvSystemSenderName); }
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvText, tvName;
        UserViewHolder(View v) { super(v); tvText = v.findViewById(R.id.tvUserChatText); tvName = v.findViewById(R.id.tvUserSenderName); }
    }

    static class DriverViewHolder extends RecyclerView.ViewHolder {
        TextView tvText, tvName;
        DriverViewHolder(View v) { super(v); tvText = v.findViewById(R.id.tvDriverChatText); tvName = v.findViewById(R.id.tvDriverSenderName); }
    }
}