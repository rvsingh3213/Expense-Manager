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

public class ExpenseFragment extends Fragment {

   //Firebase Database

    private FirebaseAuth mAuth;
    private DatabaseReference mExpenseDatabase;

    private RecyclerView recyclerView;

    //text view for TotalExpense

    private TextView totalExpense_txtView;

    // for Editing Expense
    private EditText edtAmount;
    private EditText edtNote;
    private EditText edtType;

    private Button btnUpdate;
    private Button btnDelete;

    //will be for data to help in update Expense
    private int Amount;
    private  String type;
    private String note;


    //This key will be help full in UPDATE and DELETE particular selected Data
    private String pos_key;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View myView= inflater.inflate(R.layout.fragment_expense, container, false);

        mAuth= FirebaseAuth.getInstance();
        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid=mUser.getUid();

        mExpenseDatabase= FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase")
                            .child(uid);

        recyclerView=myView.findViewById(R.id.recycler_id_expense);

        totalExpense_txtView=myView.findViewById(R.id.expense_txt_result);

        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());


        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalExpenseSum=0;
                for(DataSnapshot mySnapshot: snapshot.getChildren())
                {
                    Data data=mySnapshot.getValue(Data.class);
                    totalExpenseSum+=data.getAmount();

                }
                String stringAmount=String.valueOf(totalExpenseSum);

                totalExpense_txtView.setText(stringAmount);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return myView;
    }

    @Override
    public void onStart() {
        super.onStart();
        final String TAG=getClass().getName().toString();
        FirebaseUser mUser=mAuth.getCurrentUser();

        String uid=mUser.getUid();

        Query query=FirebaseDatabase.getInstance()
                .getReference().child("ExpenseDatabase").child(uid);

        FirebaseRecyclerOptions<Data> options=new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(query,Data.class).build();


        FirebaseRecyclerAdapter<Data,MyViewHolder> adapter=new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.expense_recycler_data, parent, false);

                return new ExpenseFragment.MyViewHolder(view);

            }

            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder viewHolder, final int position, @NonNull final Data model) {

                Log.i(TAG,"RV: Expense"+model.getDate()+" "+model.getId()+" "+model.getNote()+" "+model.getAmount()+"type:"+model.getType());


                viewHolder.setType(model.getType());
                viewHolder.setNote(model.getNote());
                viewHolder.setDate(model.getDate());
                viewHolder.setAmount(model.getAmount());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pos_key=getRef(position).getKey();
                        Amount=model.getAmount();
                        type=model.getType();
                        note=model.getNote();

                        updateDataItem();
                    }
                });

            }
        };


        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

        private static class MyViewHolder extends RecyclerView.ViewHolder{

            View mView;
            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                mView=itemView;
            }

            private void setDate(String date){
                TextView mDate=mView.findViewById(R.id.date_txt_expense);
                mDate.setText(date);
            }

            private void setType(String type){
                TextView mType=mView.findViewById(R.id.type_txt_expense);
                mType.setText(type);
            }

            private void setNote(String note){
                TextView mNote=mView.findViewById(R.id.note_txt_expense);
                mNote.setText(note);
            }

            private void setAmount(int amount){
                TextView mAmount=mView.findViewById(R.id.amount_txt_expense);

                String strAmount=String.valueOf(amount);

                mAmount.setText(strAmount);

            }


        }

        // will be updating Expense Data
        private void updateDataItem(){

            AlertDialog.Builder myDialog=new AlertDialog.Builder(getActivity());

            LayoutInflater inflater=LayoutInflater.from(getActivity());

            View myView=inflater.inflate(R.layout.update_data_item,null);

            myDialog.setView(myView);

            edtAmount=myView.findViewById(R.id.amount_edt);
            edtNote=myView.findViewById(R.id.note_edt);
            edtType=myView.findViewById(R.id.type_edt);

            edtAmount.setText(String.valueOf(Amount));
            edtAmount.setSelection(String.valueOf(Amount).length());
            edtType.setText(type);
            edtType.setSelection(type.length());
            edtNote.setText(note);
            edtNote.setSelection(note.length());



            btnDelete=myView.findViewById(R.id.btn_update_delete);
            btnUpdate=myView.findViewById(R.id.btn_update);

            final AlertDialog dialog=myDialog.create();
            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    type=edtType.getText().toString().trim();
                    note=edtNote.getText().toString().trim();
                    Amount=Integer.parseInt(edtAmount.getText().toString().trim());

                    String mDate= DateFormat.getDateInstance().format(new Date());

                    Data data=new Data(Amount,type,note,pos_key,mDate);


                    mExpenseDatabase.child(pos_key).setValue(data);

                    dialog.dismiss();
                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mExpenseDatabase.child(pos_key).removeValue();
                    dialog.dismiss();
                }
            });

            dialog.show();

        }

}



