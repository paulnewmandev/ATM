package com.ap.atm.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ap.atm.R;
import com.ap.atm.models.ImageModel;

import java.util.List;

/**
 * Created by Andmari on 24/11/2018.
 */

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.MyViewHolder> {
    private Context context;
    private List<ImageModel> ListData;

    public ImagesAdapter(Context context, List<ImageModel> listData) {
        this.context = context;
        this.ListData = listData;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImage;
        private ImageView mRemoveImage;
        private ProgressBar progressBar;

        public MyViewHolder(View view) {
            super(view);
            mImage = view.findViewById(R.id.mSimpleImage);
            mRemoveImage = view.findViewById(R.id.mRemoveImage);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_images, parent, false);
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.mImage.setImageBitmap(ListData.get(position).imageBitmap);
        holder.mRemoveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListData.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return ListData.size();
    }

}
