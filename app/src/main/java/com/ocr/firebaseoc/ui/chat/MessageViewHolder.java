package com.ocr.firebaseoc.ui.chat;

import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.ocr.firebaseoc.R;
import com.ocr.firebaseoc.databinding.ItemChatBinding;
import com.ocr.firebaseoc.model.Message;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    private ItemChatBinding binding;

    private final int colorCurrentUser;
    private final int colorRemoteUser;

    private boolean isSender;

    public MessageViewHolder(@NonNull View itemView, boolean isSender) {
        super(itemView);
        this.isSender = isSender;
        binding = ItemChatBinding.bind(itemView);

        // Setup default colors
        colorCurrentUser = ContextCompat.getColor(itemView.getContext(), R.color.colorAccent);
        colorRemoteUser = ContextCompat.getColor(itemView.getContext(), R.color.colorPrimary);
    }

    // Updates the UI with a new message
    public void updateWithMessage(Message message, RequestManager glide){

        // Update message text
        binding.messageTextView.setText(message.getMessage());
        // Set position: sent/Received
        binding.messageTextView.setTextAlignment(isSender ? View.TEXT_ALIGNMENT_TEXT_END : View.TEXT_ALIGNMENT_TEXT_START);

        // Update date
        if (message.getDateCreated() != null) binding.dateTextView.setText(this.convertDateToHour(message.getDateCreated()));

        // Update isMentor
        binding.profileIsMentor.setVisibility(message.getUserSender().getIsMentor() ? View.VISIBLE : View.INVISIBLE);

        // Update profile picture
        if (message.getUserSender().getUrlPicture() != null)
            glide.load(message.getUserSender().getUrlPicture())
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.profileImage);

        // Update image sent
        if (message.getUrlImage() != null){
            glide.load(message.getUrlImage())
                    .into(binding.senderImageView);
            binding.senderImageView.setVisibility(View.VISIBLE);
        } else {
            binding.senderImageView.setVisibility(View.GONE);
        }

        updateLayoutFromSenderType();
    }

    // Organizes: sent messages on the right - received messages on the left
    private void updateLayoutFromSenderType(){

        //Updates Message Bubble's Background color
        ((GradientDrawable) binding.messageTextContainer.getBackground()).setColor(isSender ? colorCurrentUser : colorRemoteUser);
        binding.messageTextContainer.requestLayout();

        if(!isSender){
            updateProfileContainer();
            updateMessageContainer();
        }
    }

    // Pushes the ProfileContainer to the left if RemoteUser
    private void updateProfileContainer(){
        ConstraintLayout.LayoutParams profileContainerLayoutParams = (ConstraintLayout.LayoutParams) binding.profileContainer.getLayoutParams();
        profileContainerLayoutParams.endToEnd = ConstraintLayout.LayoutParams.UNSET;
        profileContainerLayoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        binding.profileContainer.requestLayout();
    }

    // Pushes the MessageContainer to the right of the ProfileContainer if RemoteUser
    private void updateMessageContainer(){
        ConstraintLayout.LayoutParams messageContainerLayoutParams = (ConstraintLayout.LayoutParams) binding.messageContainer.getLayoutParams();
        messageContainerLayoutParams.startToStart = ConstraintLayout.LayoutParams.UNSET;
        messageContainerLayoutParams.endToStart = ConstraintLayout.LayoutParams.UNSET;
        messageContainerLayoutParams.startToEnd = binding.profileContainer.getId();
        messageContainerLayoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        messageContainerLayoutParams.horizontalBias = 0.0f;
        binding.messageContainer.requestLayout();

        // Updates message's content & date text gravity : Aligns it to the left if RemoteUser
        LinearLayout.LayoutParams messageTextLayoutParams = (LinearLayout.LayoutParams) binding.messageTextContainer.getLayoutParams();
        messageTextLayoutParams.gravity = Gravity.START;
        binding.messageTextContainer.requestLayout();

        LinearLayout.LayoutParams dateLayoutParams = (LinearLayout.LayoutParams) binding.dateTextView.getLayoutParams();
        dateLayoutParams.gravity = Gravity.BOTTOM | Gravity.START;
        binding.dateTextView.requestLayout();

    }

    private String convertDateToHour(Date date){
        DateFormat dfTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return dfTime.format(date);
    }

}