package com.sust.adminkinblood;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.sust.adminkinblood.notification.APIService;
import com.sust.adminkinblood.notification.Client;
import com.sust.adminkinblood.notification.Data;
import com.sust.adminkinblood.notification.MyResponse;
import com.sust.adminkinblood.notification.NotificationSender;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.xml.transform.sax.TemplatesHandler;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.CALL_PHONE;

public class   Donors extends AppCompatActivity implements AdapterClass.OnListListener {


    private static final String TAG = "Donor Activity";
    //private DatabaseReference ref;
    private ArrayList<Dnr_Healper> dnr_list = new ArrayList<>();
    ;
    private RecyclerView recyclerView;
    private androidx.appcompat.widget.SearchView searchView;

    private AdapterClass adapterClass;
    private CollectionReference COLLECTION_REFERENCE_USERS, COLLECTION_REFERENCE_REQUESTS, COLLECTION_REFERENCE_DONORDATAFORUSER, COLLECTION_REFERENCE_CONFIRMED_REQUESTS;
    private FirebaseFirestore DATABASE_REFERENCE;
    private Dialog donorinfo;
    private String uid;
    private String requesterUidForDonor_;
    private APIService apiService;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donors);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        DATABASE_REFERENCE = FirebaseFirestore.getInstance();

        COLLECTION_REFERENCE_USERS = DATABASE_REFERENCE.collection("Users");
        COLLECTION_REFERENCE_REQUESTS = DATABASE_REFERENCE.collection("Requests");
        COLLECTION_REFERENCE_DONORDATAFORUSER = DATABASE_REFERENCE.collection("DonorDataForUser");
        COLLECTION_REFERENCE_CONFIRMED_REQUESTS = DATABASE_REFERENCE.collection("Confirmed_Requests");



        /*ref = FirebaseDatabase.getInstance().getReference().child("Users");*/

        recyclerView = findViewById(R.id.rv);
        searchView = findViewById(R.id.search);
//        adapterClass =new AdapterClass(this,dnr_list);
//        recyclerView.setAdapter(adapterClass);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        donorinfo = new Dialog(this);


        COLLECTION_REFERENCE_USERS.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                ArrayList<DocumentSnapshot> myList = (ArrayList<DocumentSnapshot>) queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot d : myList) {
                    Dnr_Healper dnr = d.toObject(Dnr_Healper.class);

                    //  if(dnr.getDonorStatus() != null)

                    if (dnr.getDonorStatus().equals("positive") && dnr.isAvailable() && !dnr.isDonating() && dnr.getRequestStatus().equals("negative")) {
                        // Toast.makeText(Donors.this,"positive held",Toast.LENGTH_SHORT).show();
                        dnr_list.add(dnr);
                    }
                }
                adapterClass.notifyDataSetChanged();
                String size = String.valueOf(dnr_list.size());
                Toast.makeText(Donors.this,size,Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(Donors.this, "Document does not exist", Toast.LENGTH_SHORT).show();
            }
        });
        adapterClass = new AdapterClass(this, dnr_list, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapterClass);


        /*if (ref != null) {
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        list = new ArrayList<>();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            list.add(ds.getValue(Dnr_Healper.class));
                        }
                        AdapterClass adapterClass = new AdapterClass(getApplicationContext(), list);
                        recyclerView.setAdapter(adapterClass);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(Donors.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }*/

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                // adapterClass.getFilter().filter(s);

                search(s);

                //  Toast.makeText(Donors.this,"In",Toast.LENGTH_SHORT).show();
                return true;
            }

            private void search(String str) {

                //  String place = "" ;
                ArrayList<Dnr_Healper> mylist = new ArrayList<>();

//                     for(int i = 0 ; i<str.length();i++){
//                         if(str.charAt(i) == '+' || str.charAt(i) ==  '-') { place = str.substring(i+1);
//                         break;}
//
//                     }
//
//                   //  place.toLowerCase().trim();


                for (Dnr_Healper object : dnr_list) {
                    if (object.getBloodGroup().toLowerCase().contains(str.toLowerCase())) {
                        mylist.add(object);

                    }
                }


                AdapterClass adapterClass = new AdapterClass(Donors.this, mylist, Donors.this);

                adapterClass.notifyDataSetChanged();

                recyclerView.setAdapter(adapterClass);

            }


        });
    }


    @Override
    public void OnListClick(int position) {
        showDonorDialog(position);

        Log.d("TAG", "OnListClick: " + position);
    }

    private void showDonorDialog(int i) {

        donorinfo.setContentView(R.layout.dialog_donor);
        donorinfo.setCanceledOnTouchOutside(true);

        Button btn_call = donorinfo.findViewById(R.id.dia_btn_call);
        Button btn_assign = donorinfo.findViewById(R.id.dia_btn_assign_donor);
        Button btn_notify_user = donorinfo.findViewById(R.id.dia_btn_notify_user);

        TextView dnr_name = donorinfo.findViewById(R.id.dia_dnr_nm);
        TextView bld_grp = donorinfo.findViewById(R.id.dia_bld_grp);
        TextView dnr_add = donorinfo.findViewById(R.id.dia_add);
        TextView dnr_num = donorinfo.findViewById(R.id.dia_phn_num);

        dnr_name.setText(dnr_list.get(i).getFullName());
        bld_grp.setText(dnr_list.get(i).getBloodGroup());
        dnr_add.setText(dnr_list.get(i).getCurrentLocationAddress());
        dnr_num.setText(dnr_list.get(i).getPhoneNumber());
        uid = dnr_list.get(i).getUid();

        btn_call.setOnClickListener(view -> {
            if (ActivityCompat.checkSelfPermission(Donors.this, CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + dnr_list.get(i).getPhoneNumber()));
                startActivity(intent);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{CALL_PHONE}, 401);
                }
            }
        });

        btn_assign.setOnClickListener(view -> {
            EditText requesterUidForDonor = donorinfo.findViewById(R.id.requester_uid_for_donor);
            requesterUidForDonor_ = requesterUidForDonor.getText().toString();
            if (requesterUidForDonor_.isEmpty()) {
                requesterUidForDonor.setError("Field cannot be empty");
                requesterUidForDonor.requestFocus();
                return;
            }
            COLLECTION_REFERENCE_REQUESTS.document(requesterUidForDonor_).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Rqst_Helper rqst_helper = documentSnapshot.toObject(Rqst_Helper.class);
                    if (rqst_helper != null) {
                        COLLECTION_REFERENCE_CONFIRMED_REQUESTS
                                .document(uid)
                                .set(rqst_helper, SetOptions.merge())
                                .addOnSuccessListener(aVoid -> FirebaseFirestore.getInstance()
                                        .collection("Users")
                                        .document(uid).update("donating", true)
                                        .addOnSuccessListener(aVoid1 -> {

                                            if (dnr_list.get(i).getFormFactor().equals("nonApp")){
                                                Toast.makeText(Donors.this, "Donor Assigned", Toast.LENGTH_SHORT).show();
                                            } else {
                                                FirebaseDatabase.getInstance().getReference().child("Tokens").child(uid).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        Map<String, String> hm = (Map<String, String>) snapshot.getValue();
                                                        assert hm != null;
                                                        sendNotifications(hm.get("token"), "assign");
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(e -> Toast.makeText(Donors.this, e.getMessage(), Toast.LENGTH_SHORT).show())).addOnFailureListener(e -> Toast.makeText(Donors.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(Donors.this, "Error Occured!!!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Donors.this, "Request Doesnt exist", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> Toast.makeText(Donors.this, e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        btn_notify_user.setOnClickListener(view -> COLLECTION_REFERENCE_REQUESTS
                .document(requesterUidForDonor_)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Map<String, Object> hm = new HashMap<>();
                    hm.put("donorHaveToGoLatitude", documentSnapshot.getDouble("donorHaveToGoLatitude"));
                    hm.put("donorHaveToGoLongitude", documentSnapshot.getDouble("donorHaveToGoLongitude"));
                    hm.put("donorName", dnr_list.get(i).getFullName());
                    hm.put("donorPhoneNumber", dnr_list.get(i).getPhoneNumber());
                    hm.put("donorUID", uid);

                    COLLECTION_REFERENCE_DONORDATAFORUSER
                            .document(requesterUidForDonor_)
                            .set(hm, SetOptions.merge())
                            .addOnSuccessListener(aVoid12 -> COLLECTION_REFERENCE_REQUESTS
                                    .document(requesterUidForDonor_)
                                    .delete().addOnSuccessListener(aVoid1 -> {
                                        Map<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("requestStatus", "positive");
                                        COLLECTION_REFERENCE_USERS
                                                .document(requesterUidForDonor_)
                                                .set(hashMap, SetOptions.merge())
                                                .addOnSuccessListener(aVoid2 -> {
                                                    FirebaseDatabase.getInstance().getReference().child("Tokens").child(requesterUidForDonor_).addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            Map<String, String> hm = (Map<String, String>) snapshot.getValue();
                                                            assert hm != null;
                                                            sendNotifications(hm.get("token"), "notify");
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                }).addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
                                    }).addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show()))
                            .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
                }).addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show()));

        donorinfo.show();
        Objects.requireNonNull(donorinfo.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void sendNotifications(String token, String message) {
        Log.d("tikka", token);
        Data data = new Data(message);
        NotificationSender sender = new NotificationSender(data, token);
        apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        if (response.body().success != 1) {
                            Toast.makeText(Donors.this, "Response body error", Toast.LENGTH_SHORT).show();
                        } else {
                            if (message.equals("assign")) {
                                Toast.makeText(Donors.this, "Donor Assigned", Toast.LENGTH_SHORT).show();
                            } else if (message.equals("notify")) {
                                Toast.makeText(Donors.this, "User Notified", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(Donors.this, "Response body null", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Donors.this, "Response code error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });
    }
}

