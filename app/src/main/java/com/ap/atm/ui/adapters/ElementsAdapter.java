package com.ap.atm.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ap.atm.R;
import com.ap.atm.models.ElementSemaphoreModel;
import com.ap.atm.models.ImageModel;

import java.util.List;

/**
 * Created by Andmari on 21/12/2018.
 */

public class ElementsAdapter extends RecyclerView.Adapter<ElementsAdapter.MyViewHolder> {
    private Context context;
    private List<ElementSemaphoreModel> mListData;

    public ElementsAdapter(Context context, List<ElementSemaphoreModel> listData) {
        this.context = context;
        this.mListData = listData;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView mDelete;
        private TextView mName, mType;

        public MyViewHolder(View view) {
            super(view);
            mDelete = view.findViewById(R.id.mItemDeleteElement);
            mName = view.findViewById(R.id.mItemNameElement);
            mType = view.findViewById(R.id.mItemTypeElement);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_elements, parent, false);
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.mName.setText(mListData.get(position).elemento);
        holder.mType.setText(mListData.get(position).tipo);
        holder.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListData.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

}