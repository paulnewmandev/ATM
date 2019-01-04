package com.ap.atm.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ap.atm.R;
import com.ap.atm.models.ElementSemaphoreModel;
import com.ap.atm.utils.ApiUtils;
import com.ap.atm.utils.DialogUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Andmari on 21/12/2018.
 */

public class ElementsAdapter extends RecyclerView.Adapter<ElementsAdapter.MyViewHolder> {
    private Context context;
    private List<ElementSemaphoreModel> mListData;
    private boolean isEdit;

    public ElementsAdapter(Context context, List<ElementSemaphoreModel> listData, boolean isEdit) {
        this.context = context;
        this.mListData = listData;
        this.isEdit = isEdit;
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
        holder.mName.setText(mListData.get(position).element);
        holder.mType.setText(mListData.get(position).type_element_id);
        holder.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEdit && mListData.get(position).updated_at.length()>0){
                    deleteElement(position);
                }else{
                    mListData.remove(position);
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    private void deleteElement(final int position){
        final MaterialDialog mDialog = DialogUtils.showProgress(context, "Eliminando elemento", null);
        String mUrl = ApiUtils.API_URL+"deleteElemento";
        RequestParams mParams = new RequestParams();
        mParams.put(ApiUtils.parameters.id.name(), mListData.get(position).id);
        new AsyncHttpClient().post(mUrl, mParams, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                mDialog.dismiss();
                try {
                    JSONObject mJson = new JSONObject(rawJsonResponse);
                    if(mJson.getString(ApiUtils.responses.Status.name()).contentEquals("OK")){
                        mListData.remove(position);
                        notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Object errorResponse) {
                mDialog.dismiss();
                Toast.makeText(context, context.getString(R.string.error_server), Toast.LENGTH_LONG).show();
            }

            @Override
            protected Object parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return null;
            }
        });
    }

}