package com.siddartharao.hifriends;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder>{

    private List<String> imageUrls;
    private String theRoomCode;

    //private List<String> itemList;
    //private OnItemClickListener clickListener;


    public ImageAdapter(List<String> imageUrls,String theRoomCode) {
        this.imageUrls = imageUrls;
        this.theRoomCode = theRoomCode;
    }


    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);
        // Load and display the image using an image loading library like Picasso or Glide
        Glide.with(holder.imageView.getContext()).load(imageUrl).into(holder.imageView);


        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, imageFullScreen.class);
                intent.putExtra("image", imageUrl);
                intent.putExtra("code",theRoomCode);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView imageName;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            imageName = itemView.findViewById(R.id.tv);

        }
    }
}
