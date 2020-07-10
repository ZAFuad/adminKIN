package com.sust.adminkinblood;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static android.Manifest.permission.CALL_PHONE;

public class AdapterClass extends RecyclerView.Adapter<AdapterClass.ViewHolder> {
    Context mContext;
    ArrayList<Dnr_Healper> mData;
    Dialog myDialog;

    public AdapterClass(Context mContext, ArrayList<Dnr_Healper> list) {
        this.mData = list;
        this.mContext = mContext;
    }

    public AdapterClass(ArrayList<Dnr_Healper> list) {
        this.mData = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_donor, parent, false);
        final ViewHolder vHolder = new ViewHolder(view);

        myDialog = new Dialog(mContext);
        myDialog.setContentView(R.layout.dialog_donor);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView dnr_name = myDialog.findViewById(R.id.dia_dnr_nm);
        TextView bld_grp = myDialog.findViewById(R.id.dia_bld_grp);
        TextView dnr_add = myDialog.findViewById(R.id.dia_add);
        TextView dnr_num = myDialog.findViewById(R.id.dia_phn_num);
        Button btn_call = myDialog.findViewById(R.id.dia_btn_call);

        dnr_name.setText(mData.get(vHolder.getAdapterPosition()).getFullName());
        bld_grp.setText(mData.get(vHolder.getAdapterPosition()).getBloodGroup());
        dnr_add.setText(mData.get(vHolder.getAdapterPosition()).getAddress());
        dnr_num.setText(mData.get(vHolder.getAdapterPosition()).getPhoneNumber());

        btn_call.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(mContext, CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + mData.get(i).getPhoneNumber()));
                    ((Activity)mContext).startActivity(intent);
                }
                else {
                    ((Activity)mContext).requestPermissions(new String[]{CALL_PHONE},401);
                }
            }
        });




        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {

        holder.dnr_Name.setText(mData.get(i).getFullName());
        holder.bld_grp.setText(mData.get(i).getBloodGroup());
        holder.phn_num.setText(mData.get(i).getPhoneNumber());
        holder.call.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

                if (ActivityCompat.checkSelfPermission(mContext, CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + mData.get(i).getPhoneNumber()));
                    ((Activity)mContext).startActivity(intent);
                }
                else {
                    ((Activity)mContext).requestPermissions(new String[]{CALL_PHONE},401);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView dnr_Name,bld_grp,phn_num;
        private ImageView call;
        private LinearLayout item_Donor;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                item_Donor= itemView.findViewById(R.id.Dnr_id);;
                dnr_Name = itemView.findViewById(R.id.dnr_name);
                bld_grp = itemView.findViewById(R.id.bld_grp);
                phn_num= itemView.findViewById(R.id.phn_num);
                call = itemView.findViewById(R.id.call);


            }

            }
}
