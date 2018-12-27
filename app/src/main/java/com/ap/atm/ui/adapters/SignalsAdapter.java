package com.ap.atm.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ap.atm.R;
import com.ap.atm.models.SignalModel;
import com.ap.atm.ui.activities.AddPortraitSignalActivity;
import com.ap.atm.ui.activities.SignalsActivity;
import com.ap.atm.utils.ApiUtils;
import com.ap.atm.utils.DialogUtils;
import com.ap.atm.utils.FormUtils;
import com.ap.atm.utils.SessionUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Andmari on 19/12/2018.
 */

public class SignalsAdapter extends RecyclerView.Adapter<SignalsAdapter.MyViewHolder> {
    private Context mContext;
    private List<SignalModel> mListData;

    public SignalsAdapter(Context context, List<SignalModel> listData) {
        this.mContext = context;
        this.mListData = listData;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private TextView mDate;
        private TextView mAddress;
        private TextView mStreet1;
        private TextView mStreet2;

        public MyViewHolder(View view) {
            super(view);
            mView = view.findViewById(R.id.mMainViewItemSignal);
            mDate = view.findViewById(R.id.mDateItemSignal);
            mAddress = view.findViewById(R.id.mAddressGoogleSignal);
            mStreet1 = view.findViewById(R.id.mStreet1Signal);
            mStreet2 = view.findViewById(R.id.mStreet2Signal);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_signal, parent, false);
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.mDate.setText(FormUtils.dateToStringddMMMyyyy(FormUtils.yyyyMMddTHHmmssToDate(mListData.get(position).date_creat)));
        holder.mAddress.setText(mListData.get(position).address_google);
        holder.mStreet1.setText(mListData.get(position).street1);
        holder.mStreet2.setText(mListData.get(position).street2);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionMenu(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    private void showOptionMenu(View v, final int position){
        PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_edit:
                        Intent intent = new Intent(mContext, AddPortraitSignalActivity.class);
                        intent.putExtra(SessionUtils.params.signal.name(), mListData.get(position));
                        SignalsActivity mActivity = (SignalsActivity) mContext;
                        mActivity.startActivityForResult(intent, SessionUtils.CREATE_NEW_ELEMENT);
                        return true;
                    case R.id.action_delete:
                        dialogDelete(position);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.inflate(R.menu.empty_menu);
        popupMenu.show();
    }

    private void dialogDelete(final int position){
        final MaterialDialog mDialog = DialogUtils.showDialogAcceptCancel(mContext, mContext.getString(R.string.confirm_title), mContext.getString(R.string.sure_delete));
        mDialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                deleteRegister(position);
            }
        });
    }

    private void deleteRegister(final int mPosition){
        final MaterialDialog mDialog = DialogUtils.showProgress(mContext, mContext.getString(R.string.title_get_data), mContext.getString(R.string.deleting_data));
        String mUrl = ApiUtils.API_URL+ ApiUtils.DELETE_SIGNALS;
        RequestParams mParams = new RequestParams();
        mParams.put("id", mListData.get(mPosition).id);
        new AsyncHttpClient().post(mUrl, mParams, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                mDialog.dismiss();
                Log.d("resp delete Sem", rawJsonResponse);
                try {
                    JSONObject mJson = new JSONObject(rawJsonResponse);
                    if(mJson.getString("Status").contentEquals("OK")){
                        mListData.remove(mPosition);
                        notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Object errorResponse) {
                mDialog.dismiss();
                Toast.makeText(mContext, mContext.getString(R.string.error_server), Toast.LENGTH_LONG).show();
            }

            @Override
            protected Object parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return null;
            }
        });

    }

}
