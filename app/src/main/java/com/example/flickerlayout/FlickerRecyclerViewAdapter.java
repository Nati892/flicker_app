package com.example.flickerlayout;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class FlickerRecyclerViewAdapter extends RecyclerView.Adapter<FlickerRecyclerViewAdapter.FlickerImageViewHolder> {
    private static final String TAG = "FlickerRecyclerViewAdap";
    private List<Photo> mPhotoList;
    private Context mContext;

    public FlickerRecyclerViewAdapter(List<Photo> photoList, Context context) {
        mPhotoList = photoList;
        mContext = context;
    }


    @NonNull
    @Override
    public FlickerImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //View creator
        Log.d(TAG, "onCreateViewHolder: starts");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.browse, parent, false);
        return new FlickerImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlickerImageViewHolder holder, int position) {
        Photo photoItem = mPhotoList.get(position);
        Log.d(TAG, "onBindViewHolder: " + photoItem.getTitle() + " ----> " + position);
        Picasso.get().load(photoItem.getImage())
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(holder.thumbnail);
        if (holder.title!=null)
        holder.title.setText(photoItem.getTitle());

    }

    @Override
    public int getItemCount() {
        if (mPhotoList != null)
            if (mPhotoList.size() != 0)
                return mPhotoList.size();
        return 0;
    }

    public void loadNewData(List<Photo> newPhotos) {
        mPhotoList = newPhotos;
        notifyDataSetChanged();
    }


    public Photo getPhoto(int position) {
        Log.d(TAG, "getPhoto: starts");
        if (mPhotoList != null)
            if (mPhotoList.size() != 0)
                return mPhotoList.get(position);
        return null;

    }


    static class FlickerImageViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "FlickerImageViewHolder";
        ImageView thumbnail = null;
        TextView title = null;


        public FlickerImageViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "FlickerImageViewHolder: starts");
            this.thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            this.title = (TextView) itemView.findViewById(R.id.texttitle);

        }
    }
}
