package com.jeysidg.e_notify;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.MyHolder>{


    Context context;
    List<ModelUser> userList;

    String myUid;

    public AdapterUser(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_users, viewGroup, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {

        final String hisUID = userList.get(i).getUid();

        String userImage = userList.get(i).getImage();
        String userName = userList.get(i).getName();

        String userCompany = userList.get(i).getCompany();
        String userDepartment = userList.get(i).getDepartment();


        final String userEmail = userList.get(i).getEmail();
        //final String uid = userList.get(i).getUid();

        //set data
        myHolder.mNameTv.setText(userName);
        myHolder.mEmailTv.setText(userEmail);
        myHolder.mCompanyTv.setText(userCompany);
        myHolder.mDepartmentTv.setText(userDepartment);

        try {
            Picasso.get().load(userImage)
                    .placeholder(R.drawable.profpic)
                    .into(myHolder.mAvatarIv);
        }
        catch (Exception e) {

        }

        //item click
        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, ""+userEmail, Toast.LENGTH_SHORT).show();
                //show dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setItems(new String[]{"Answer Visitor Form", "View profile"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0){
                            //profile clicked
                            Intent intent = new Intent(context, VisitorformActivity.class);
                            intent.putExtra("hisUid",hisUID);
                            context.startActivity(intent);
                        } else if (which == 1) {
                            Intent intent = new Intent(context, ViewProfileActivity.class);
                            intent.putExtra("uid",hisUID);
                            context.startActivity(intent);
                        }

                    }
                });
                builder.create().show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    // view holder class
    class MyHolder extends RecyclerView.ViewHolder{

        ImageView mAvatarIv;
        TextView mNameTv, mEmailTv, mCompanyTv, mDepartmentTv;


        public MyHolder(@NonNull View itemView) {

            super(itemView);

            //init views
            mAvatarIv = itemView.findViewById(R.id.avatarIv);
            mNameTv = itemView.findViewById(R.id.nameTv);
            mEmailTv = itemView.findViewById(R.id.emailTv);
            mCompanyTv = itemView.findViewById(R.id.companyTv);
            mDepartmentTv = itemView.findViewById(R.id.departmentTv);
        }
    }
}
