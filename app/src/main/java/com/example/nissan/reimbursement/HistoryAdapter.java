package com.example.nissan.reimbursement;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder>
{
    public static final String KEY_TYPE="type";
    public static final String KEY_STATUS="status";
    public static final String KEY_AMOUNT="amount";
    public static final String KEY_CURRENTAPPROVER="currentapprover";
    // Inside this class, we’ll have another class for the ViewHolder thus
    public List<HistoryList> historyLists;
    private Context context;

    public HistoryAdapter(List<HistoryList> historyLists, Context context) {
        this.historyLists = historyLists;
        this.context = context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_list,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        final HistoryList historyList = historyLists.get(position);
        holder.type.setText(historyList.getType());
        if(historyList.getStatus().equals("Rejected"))
            holder.status.setTextColor(Color.RED);
        else if(historyList.getStatus().equals("Initiated"))
            holder.status.setTextColor(Color.BLUE);
        else if(historyList.getStatus().equals("Approved"))
            holder.status.setTextColor(Color.GREEN);

        holder.status.setText(historyList.getStatus());
        holder.amount.setText(historyList.getAmount());
        holder.currentApprover.setText(historyList.getCurrentApprover());
        holder.showRole.setText(historyList.getRole());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HistoryList historyList1 = historyLists.get(position);
                Intent skipIntent = new Intent(v.getContext(), ApproveForm.class);
                Intent skipIntent2 = new Intent(v.getContext(), InitiateForm.class);

                skipIntent.putExtra("req_id",historyList1.getReq());
                skipIntent.putExtra(KEY_TYPE, historyList1.getType());
                skipIntent.putExtra(KEY_STATUS, historyList1.getStatus());
                skipIntent.putExtra(KEY_AMOUNT, historyList1.getAmount());
                skipIntent.putExtra(KEY_CURRENTAPPROVER, historyList1.getCurrentApprover());

                skipIntent2.putExtra("req_id",historyList1.getReq());
                skipIntent2.putExtra(KEY_TYPE, historyList1.getType());
                skipIntent2.putExtra(KEY_STATUS, historyList1.getStatus());
                skipIntent2.putExtra(KEY_AMOUNT, historyList1.getAmount());
                skipIntent2.putExtra(KEY_CURRENTAPPROVER, historyList1.getCurrentApprover());
               // Log.d("xxxRole",historyList1.getRole());
                if(historyList1.getRole().equals("Initiator"))
                {
                   // Log.d("xxxRole1","approver");
                    v.getContext().startActivity(skipIntent);
                }
                else if(historyList1.getRole().equals("Approver"))
                {
                  //  Log.d("xxxRole2","initiat");
                    v.getContext().startActivity(skipIntent2);

                }

            }
        });

    }
    @Override
    public int getItemCount() {
        return historyLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView type;
        public TextView status;
        public TextView amount;
        public TextView currentApprover;
        public RelativeLayout relativeLayout;
        public TextView showRole;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            type = (TextView)itemView.findViewById(R.id.department);
            status = (TextView) itemView.findViewById(R.id.status);
            amount = (TextView) itemView.findViewById(R.id.amount);
            currentApprover = (TextView) itemView.findViewById(R.id.approver);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
            showRole = (TextView) itemView.findViewById(R.id.app);

        }
    }


}