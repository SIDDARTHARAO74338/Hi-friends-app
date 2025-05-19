package com.siddartharao.hifriends;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class chatAdapter extends RecyclerView.Adapter implements SwipeToDeleteListener{

    private SwipeToDeleteListener adapterCallback;
    int ITEM_SEND = 1;
    int ITEM_RECIVE = 2;
    Context context;
    ArrayList<Messages> messagesArrayList;
    ArrayList<Messages> notesSource;
    private final OnItemClickListener onItemClickListener;

    public chatAdapter(Context context, ArrayList<Messages> messagesArrayList, OnItemClickListener onItemClickListener,SwipeToDeleteListener adapterCallback) {
        this.context = context;
        this.messagesArrayList = messagesArrayList;
        this.onItemClickListener = onItemClickListener;
        this.adapterCallback = adapterCallback;
        notesSource = messagesArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == ITEM_SEND) {
            // Display a toast for emojis
            View view = LayoutInflater.from(context).inflate(R.layout.list_item2, parent, false);
            return new SenderViewHolder(view);
        } else {
            // Display a toast for text
            View view = LayoutInflater.from(context).inflate(R.layout.list_item1, parent, false);
            return new SenderViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Messages messages = messagesArrayList.get(position);
        SenderViewHolder viewHolder = (SenderViewHolder) holder;
        viewHolder.txtmessage.setText(messages.getMessage());
        viewHolder.messagetime.setText(messages.getTimeStamp());
        viewHolder.userId.setText(messages.getSenderId());
        viewHolder.Toptxtmessage.setText(messages.getMessage_top());
        viewHolder.emoji.setText(messages.getEmoji());

        if (messages.getEmoji().equals("")){
            viewHolder.emoji.setVisibility(View.GONE);
        }else{
            viewHolder.emoji.setVisibility(View.VISIBLE);
        }

        if (messages.getMessage_top().equals("")){
            viewHolder.Toptxtmessage.setVisibility(View.GONE);
        }else{
            viewHolder.Toptxtmessage.setVisibility(View.VISIBLE);
        }

        if (containsEmoji(messages.getMessage())) {
            // Increase text size for emoji-containing text
            viewHolder.txtmessage.setTextSize(30); // Set your desired text size here
        } else {
            // Reset text size to default
            viewHolder.txtmessage.setTextSize(18); // Set your default text size here
        }


        viewHolder.items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(position);
            }
        });

        getItemText(position);



    }

    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Messages messages = messagesArrayList.get(position);

        String firebaseAuth = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (firebaseAuth.equals(messages.getUserUrl())){
            return ITEM_SEND;
        } else {
            return ITEM_RECIVE;
        }
    }

    class SenderViewHolder extends RecyclerView.ViewHolder {

        TextView txtmessage,Toptxtmessage;
        TextView messagetime;
        TextView userId,emoji;
        View items;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);

            Toptxtmessage = itemView.findViewById(R.id.ToptxtMessages);
            txtmessage = itemView.findViewById(R.id.txtMessages);
            messagetime = itemView.findViewById(R.id.messageTime);
            userId = itemView.findViewById(R.id.userId);
            emoji = itemView.findViewById(R.id.emoji);
            items = itemView.findViewById(R.id.items);


        }
    }

    private boolean containsEmoji(String text) {
        // Implement logic to check if the text contains emojis
        // For simplicity, you can use a basic implementation like this:
        //return text.matches(".*[\\uD83C-\\uDBFF\\uDC00-\\uDFFF]+.*");

        Pattern pattern = Pattern.compile("[\\uD83C-\\uDBFF\\uDC00-\\uDFFF]+");
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public String getItemText(int position) {
        // Return the text of the item at the given position
        // Adjust this based on your data structure
        return messagesArrayList.get(position).getMessage();
    }

    @Override
    public void onSwipe(int position, int direction) {

        if (adapterCallback != null) {
            adapterCallback.onSwipe(position, direction);
        }
        notifyItemChanged(position);

    }


    public void searchNotes(final String searchKeyWord){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (searchKeyWord.trim().isEmpty()){
                    messagesArrayList = notesSource;
                }else{
                    ArrayList<Messages> temp = new ArrayList<>();
                    for (Messages note : notesSource){
                        if (note.getMessage().toLowerCase().contains(searchKeyWord.toLowerCase())
                        ||note.getMessage_top().toLowerCase().contains(searchKeyWord.toLowerCase())
                        ||note.getTimeStamp().toLowerCase().contains(searchKeyWord.toLowerCase())){
                            temp.add(note);
                        }
                    }
                    messagesArrayList = temp;
                }

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });

            }
        },500);
    }

    /*class ReciverViewHolder extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        TextView txtmessage;
        TextView messagetime;

        public ReciverViewHolder(@NonNull View itemView) {
            super(itemView);

            circleImageView = itemView.findViewById(R.id.profile_image);
            txtmessage = itemView.findViewById(R.id.txtMessages);
            messagetime = itemView.findViewById(R.id.messageTime);
            //messagetimeRe.setText();


        }
    }*/

}
