package com.ocr.firebaseoc.ui.chat;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.bumptech.glide.RequestManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.ocr.firebaseoc.R;
import com.ocr.firebaseoc.manager.UserManager;
import com.ocr.firebaseoc.model.Message;

// FirestoreRecyclerAdapter is an adapter featuring a connection to our DB Firestore
// so that our Adapter is intrinsically linked to our messages on Firestore
public class MentorChatAdapter extends FirestoreRecyclerAdapter<Message, MessageViewHolder> {

    public interface Listener {
        void onDataChanged();
    }

    // VIEW TYPES
    private static final int SENDER_TYPE = 1;
    private static final int RECEIVER_TYPE = 2;

    private final RequestManager glide;

    private Listener callback;

    public MentorChatAdapter(@NonNull FirestoreRecyclerOptions<Message> options, RequestManager glide, Listener callback) {
        super(options);
        this.glide = glide;
        this.callback = callback;
    }

    // Determines message's type : Sent/Received
    @Override
    public int getItemViewType(int position) {
        String currentUserId = UserManager.getInstance().getCurrentUser().getUid();
        boolean isSender = getItem(position).getUserSender().getUid().equals(currentUserId);

        return (isSender) ? SENDER_TYPE : RECEIVER_TYPE;
    }

    @Override
    protected void onBindViewHolder(@NonNull MessageViewHolder holder, int position, @NonNull Message model) {
        holder.itemView.invalidate();
        holder.updateWithMessage(model, this.glide);
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MessageViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat, parent, false), viewType == 1);
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        this.callback.onDataChanged();
    }
}