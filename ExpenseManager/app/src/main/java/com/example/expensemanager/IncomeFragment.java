package com.example.expensemanager;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanager.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;


public class IncomeFragment extends Fragment {

    private String TAG=getClass().getName().toString();
    //Firebase database

    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;

    //Recycler view

    private RecyclerView recyclerView;

    //Text view for putting total income
    private TextView incomeTotal;


    //Update Records

    private EditText edtAmount;
    private EditText edtType;
    private EditText edtNote;

    //for Update and Delete Button

    private Button btnUpdate;
    private Button btnDelete;

    //Data item VALUE for update purpose
    private int Amount;
    private String type;
    private String note;

    private String pos_key;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View myView= inflater.inflate(R.layout.fragment_income, container, false);

        mAuth=FirebaseAuth.getInstance();

        FirebaseUser mUser=mAuth.getCurrentUser();

        String uid=mUser.getUid();

        mIncomeDatabase=FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);

        //for totalIncomeSumm
        incomeTotal=myView.findViewById(R.id.income_txt_result);
        // id is taken from fragment_income
        // and layout of this each Rview will be defines in income_recycler_data
        recyclerView=myView.findViewById(R.id.recycler_id_income);

        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalIncome=0;
                for(DataSnapshot mySnapshot:snapshot.getChildren())
                {
                    Data data=mySnapshot.getValue(Data.class);
                    totalIncome+=data.getAmount();
                }
                String totalSum=String.valueOf(totalIncome);
                incomeTotal.setText(totalSum);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return myView;
    }

    // This is how we will get data from our Firebase Database using
    // will use
    @Override
    public void onStart() {
        super.onStart();
        final String TAG=getClass().getName().toString();
        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid=mUser.getUid();
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("IncomeData")
                .child(uid);
        FirebaseRecyclerOptions<Data> options =
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(query, Data.class)
                        .build();
        FirebaseRecyclerAdapter<Data,MyViewHolder> adapter= new FirebaseRecyclerAdapter<Data, MyViewHolder>(
            options)
        {
           @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
               View view = LayoutInflater.from(parent.getContext())
                       .inflate(R.layout.income_recycler_data, parent, false);

               return new MyViewHolder(view);

             //  return null;
            }

            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder viewHolder, final int position, @NonNull final Data model) {

                Log.i(TAG,"RV:"+model.getDate()+" "+model.getId()+" "+model.getNote()+" "+model.getAmount()+"type:"+model.getType());


                viewHolder.setType(model.getType());
                viewHolder.setNote(model.getNote());
                viewHolder.setDate(model.getDate());
                viewHolder.setAmount(model.getAmount());

                // with this we can update the Records
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                            type= model.getType();
                            note=model.getNote();
                            Amount=model.getAmount();
                            // this is to remember the position
                        // so that this position can be used for Record to UPDATE or DELETE
                            pos_key=getRef(position).getKey();

                            //pos_key used at line   <<<   266  AND 269  AND 281 >>>

                            updateDataItem();
                    }
                });

                }



        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }

        private void setType(String type)
        {
            TextView mType=mView.findViewById(R.id.type_txt_income);
            mType.setText(type);

        }
        private void setNote(String note){

            TextView mNote=mView.findViewById(R.id.note_txt_income);
            mNote.setText(note);

        }

        private void setDate(String date){
            TextView mDate=mView.findViewById(R.id.date_txt_income);
            mDate.setText(date);
        }

        private void setAmount(int amount){

            TextView mAmount=mView.findViewById(R.id.amount_txt_income);
            String stamount=String.valueOf(amount);
            mAmount.setText(stamount);

        }
    }

    private void updateDataItem(){
        Log.i(TAG,"Update is Tried");
        AlertDialog.Builder myDialog=new AlertDialog.Builder(getActivity());

        LayoutInflater inflater=LayoutInflater.from(getActivity());

        View myView=inflater.inflate(R.layout.update_data_item,null);

        myDialog.setView(myView);

        edtAmount=myView.findViewById(R.id.amount_edt);
        edtType=myView.findViewById(R.id.type_edt);
        edtNote=myView.findViewById(R.id.note_edt);

        //tHESE setText values will be for taking record item and putting into update_data_item
        //so that user can see these were the values


        edtAmount.setText(String.valueOf(Amount));
        //this setSelection method will put the cursor at the ned of our text
        edtAmount.setSelection(String.valueOf(Amount).length());

        edtType.setText(type);
        edtType.setSelection(type.length());

        edtNote.setText(note);
        edtNote.setSelection(note.length());

        btnUpdate=myView.findViewById(R.id.btn_update);
        btnDelete=myView.findViewById(R.id.btn_update_delete);

        final AlertDialog dialog=myDialog.create();


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Amount=Integer.parseInt(edtAmount.getText().toString().trim());
                type=edtType.getText().toString().trim();
                note=edtNote.getText().toString().trim();

                String mDate= DateFormat.getDateInstance().format(new Date());
                // so this i where the Pos_key is used
                Data data=new Data(Amount,type,note,pos_key,mDate);

                // pos_key used
                mIncomeDatabase.child(pos_key).setValue(data);


                dialog.dismiss();

            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // this line will help to remove data
                mIncomeDatabase.child(pos_key).removeValue();
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    
}



