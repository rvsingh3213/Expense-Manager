package com.example.expensemanager;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanager.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

public class DashBoardFragment extends Fragment {

    private final String TAG=getClass().getName().toString();
    private FloatingActionButton fab_main_btn;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expense_btn;

    //Dashboard Income and Expense Reuslt

    private TextView incomeSetResult;
    private TextView expenseSetResult;



    //for text view

    private TextView fab_income_txt;
    private TextView fab_expense_txt;


    //boolean for animation thing
    private boolean isOpen=false;

    //Objects of Animation Class

    private Animation FadeOpen,FadeClose;

    //Firebase.... Database

    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;


    //Recycler View
    private RecyclerView mRecyclerIncome;
    private RecyclerView mRecyclerExpense;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View myView= inflater.inflate(R.layout.fragment_dash_board, container, false);

       //Total income and Expense Result
        incomeSetResult=myView.findViewById(R.id.income_set_result);
        expenseSetResult=myView.findViewById(R.id.expense_set_result);
       //Initialize Firebase variables
        mAuth=FirebaseAuth.getInstance();

        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid=mUser.getUid();

        mIncomeDatabase= FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase=FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);

        mIncomeDatabase.keepSynced(true);
        mExpenseDatabase.keepSynced(true);


        //Recycler connect
        //id are for dashboard carefull while connecting
        mRecyclerIncome=myView.findViewById(R.id.recycler_income);
        mRecyclerExpense=myView.findViewById(R.id.recycler_expense);


       //connect Floating ActionButtons to Layout

        fab_main_btn=myView.findViewById(R.id.fb_main_plus_btn);
        fab_income_btn=myView.findViewById(R.id.income_ft_btn);
        fab_expense_btn=myView.findViewById(R.id.expense_ft_btn);


        //connect  floating text

        fab_income_txt=myView.findViewById(R.id.income_ft_text);
        fab_expense_txt=myView.findViewById(R.id.expense_ft_text);

        //for Animation Connect..
        FadeOpen= AnimationUtils.loadAnimation(getContext(),R.anim.fade_open);
        FadeClose=AnimationUtils.loadAnimation(getContext(),R.anim.fade_close);

        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        //call addData() method written down

                addData();
                if(isOpen)
                {
                    fab_income_btn.startAnimation(FadeClose);
                    fab_expense_btn.startAnimation(FadeClose);

                    fab_income_btn.setClickable(false);
                    fab_expense_btn.setClickable(false);

                    fab_income_txt.startAnimation(FadeClose);
                    fab_expense_txt.startAnimation(FadeClose);

                    fab_income_txt.setClickable(false);
                    fab_expense_txt.setClickable(false);

                    isOpen=false;


                }
                else
                {
                    fab_income_btn.startAnimation(FadeOpen);
                    fab_expense_btn.startAnimation(FadeOpen);

                    fab_income_btn.setClickable(true);
                    fab_expense_btn.setClickable(true);

                    fab_income_txt.startAnimation(FadeOpen);
                    fab_expense_txt.startAnimation(FadeOpen);

                    fab_income_txt.setClickable(true);
                    fab_expense_txt.setClickable(true);

                    isOpen=true;
                }
            }
        });

        //Calculate total income and Expense

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int totalIncome=0;
                for(DataSnapshot mySnap: snapshot.getChildren())
                {
                    Data data=mySnap.getValue(Data.class);

                    totalIncome+=data.getAmount();

                }
                String strIncome=String.valueOf(totalIncome);
                incomeSetResult.setText(strIncome);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalExpense=0;
                for(DataSnapshot mySnap: snapshot.getChildren())
                {   Data data=mySnap.getValue(Data.class);
                    totalExpense+=data.getAmount();
                }
                String strExpense= String.valueOf(totalExpense);
                expenseSetResult.setText(strExpense);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //Recycler of DashBoard work

        LinearLayoutManager layoutManagerIncome=new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);

        layoutManagerIncome.setStackFromEnd(true);
        layoutManagerIncome.setReverseLayout(true);
        mRecyclerIncome.setHasFixedSize(true);
        mRecyclerIncome.setLayoutManager(layoutManagerIncome);

        LinearLayoutManager layoutManagerExpense=new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        layoutManagerExpense.setStackFromEnd(true);
        layoutManagerExpense.setReverseLayout(true);
        mRecyclerExpense.setHasFixedSize(true);
        mRecyclerExpense.setLayoutManager(layoutManagerExpense);


        return myView;
    }

    // Floating button Animation
    private void ftAnimation()
    {
        if(isOpen)
        {
            fab_income_btn.startAnimation(FadeClose);
            fab_expense_btn.startAnimation(FadeClose);

            fab_income_btn.setClickable(false);
            fab_expense_btn.setClickable(false);

            fab_income_txt.startAnimation(FadeClose);
            fab_expense_txt.startAnimation(FadeClose);

            fab_income_txt.setClickable(false);
            fab_expense_txt.setClickable(false);

            isOpen=false;


        }
        else
        {
            fab_income_btn.startAnimation(FadeOpen);
            fab_expense_btn.startAnimation(FadeOpen);

            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);

            fab_income_txt.startAnimation(FadeOpen);
            fab_expense_txt.startAnimation(FadeOpen);

            fab_income_txt.setClickable(true);
            fab_expense_txt.setClickable(true);

            isOpen=true;
        }
    }
    private void addData()
    {
        //Fab Button income...

        fab_income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //call incomeDataInsert()

                incomeDataInsert();
            }
        });

        fab_expense_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call expenseDataInsert()

                expenseDataInsert();

            }
        });
    }

    public void incomeDataInsert(){
        AlertDialog.Builder myDialog=new AlertDialog.Builder(getActivity());

        LayoutInflater inflater=LayoutInflater.from(getActivity());

        View myView=inflater.inflate(R.layout.custom_layout_for_insertdata,null);
        myDialog.setView(myView);

        final AlertDialog dialog=myDialog.create();

        //this we have addedd that is user click somewhere else then
        // dialog should not get away
        dialog.setCancelable(false);

        final EditText edtAmount=myView.findViewById(R.id.amount_edt);
        final EditText edtType=myView.findViewById(R.id.type_edt);
        final EditText edtNote=myView.findViewById(R.id.note_edt);

        Button btnSave=myView.findViewById(R.id.btnSave);
        Button btnCancel=myView.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String type=edtType.getText().toString().trim();
                String amount=edtAmount.getText().toString().trim();
                String note=edtNote.getText().toString().trim();

                if(TextUtils.isEmpty(type))
                {
                    edtType.setError("Required Field..");
                    return;
                }

                if(TextUtils.isEmpty(amount))
                {
                    edtAmount.setError("Required Field..");
                    return;
                }

                int ourAmountInt =Integer.parseInt(amount);
                if(TextUtils.isEmpty(note))
                {
                    edtNote.setError("Required Field..");
                    return;
                }

                String id=mIncomeDatabase.push().getKey();

                String mDate= DateFormat.getDateInstance().format(new Date());

                Data data=new Data(ourAmountInt,type,note,id,mDate);

                mIncomeDatabase.child(id).setValue(data);
                //Log.i(TAG,"RV:"+amount+" "+type+" "+note+" "+id+" "+mDate+" ");
                Toast.makeText(getActivity(),"Data Added..",Toast.LENGTH_SHORT).show();

                //after saved will remove Floating buttons
                ftAnimation();
                dialog.dismiss();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //remove floating button after click on cancel
                ftAnimation();
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    // here we are using same  custom_layout_for_insertdata layout
    // as same need to be inflated when income or expense FloatingActButton Pressed

    public void expenseDataInsert(){
        AlertDialog.Builder myDialog=new AlertDialog.Builder(getActivity());

        LayoutInflater inflater=LayoutInflater.from(getActivity());

        View myView=inflater.inflate(R.layout.custom_layout_for_insertdata,null);

        myDialog.setView(myView);

        final AlertDialog dialog=myDialog.create();


        //this we have addedd that is user click somewhere else then
        // dialog should not get away
        dialog.setCancelable(false);
        


        final EditText edtAmount=myView.findViewById(R.id.amount_edt);
        final EditText edtType=myView.findViewById(R.id.type_edt);
        final EditText edtNote=myView.findViewById(R.id.note_edt);

        Button btnSave=myView.findViewById(R.id.btnSave);
        Button btnCancel=myView.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String type=edtType.getText().toString().trim();
                String amount=edtAmount.getText().toString().trim();
                String note=edtNote.getText().toString().trim();

                if(TextUtils.isEmpty(type))
                {
                    edtType.setError("Required Field..");
                    return;
                }

                if(TextUtils.isEmpty(amount))
                {
                    edtAmount.setError("Required Field..");
                    return;
                }

                int ourAmountInt =Integer.parseInt(amount);
                if(TextUtils.isEmpty(note))
                {
                    edtNote.setError("Required Field..");
                    return;
                }

                // ssaving the expenses into Database
                String id=mExpenseDatabase.push().getKey();

                String mDate=DateFormat.getDateInstance().format(new Date());

                Data data=new Data(ourAmountInt,type,note,id,mDate);

                mExpenseDatabase.child(id).setValue(data);

                Toast.makeText(getActivity(),"Data Added..",Toast.LENGTH_SHORT).show();
                //when user click on save button then also we will remove Floating Button
                ftAnimation();
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //when user cancels then we remove floating button
                ftAnimation();
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    @Override
    public void onStart() {
        super.onStart();

        final String TAG=getClass().getName().toString();
        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid=mUser.getUid();
        Query queryIncome = FirebaseDatabase.getInstance()
                .getReference()
                .child("IncomeData")
                .child(uid);
        FirebaseRecyclerOptions<Data> optionsIncome =
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(queryIncome, Data.class)
                        .build();

        //for Expense Data over Dashboard

        Query queryExpense = FirebaseDatabase.getInstance()
                .getReference()
                .child("ExpenseDatabase")
                .child(uid);
        FirebaseRecyclerOptions<Data> optionsExpense =
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(queryExpense, Data.class)
                        .build();
        final FirebaseRecyclerAdapter<Data,IncomeViewHolder> incomeAdapter=new FirebaseRecyclerAdapter<Data, IncomeViewHolder>(optionsIncome) {

            @NonNull
            @Override
            public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.dashboard_income, parent, false);

                return new IncomeViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull IncomeViewHolder holder, int position, @NonNull Data model) {

                holder.setIncomeAmount(model.getAmount());
                holder.setIncomeType(model.getType());
                holder.setIncomeDate(model.getDate());

            }

        };  //FirebaseRecyclerAdapter end



        final FirebaseRecyclerAdapter<Data,ExpenseViewHolder> expenseAdapter=new FirebaseRecyclerAdapter<Data, ExpenseViewHolder>(optionsExpense) {
            @NonNull
            @Override
            public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.dashboard_expense, parent, false);

                return new ExpenseViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position, @NonNull Data model) {

                holder.setExpenseType(model.getType());
                holder.setExpenseAmount(model.getAmount());
                holder.setExpenseDate(model.getDate());
                Log.i(TAG,"RR Expense Data:"+model.getType()+" "+model.getAmount()+" "+model.getDate());
            }

            };


        //setAdapter for IncomeData
        mRecyclerIncome.setAdapter(incomeAdapter);
        incomeAdapter.startListening();

        //setAdapter for Expense Data
        mRecyclerExpense.setAdapter(expenseAdapter);
        expenseAdapter.startListening();
    }       //onStart() Ends


    //For inCome Data

    public static class IncomeViewHolder extends RecyclerView.ViewHolder{

        View mIncomeView;

        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            mIncomeView=itemView;
        }

        public  void setIncomeType(String type)
        {
            TextView mType=mIncomeView.findViewById(R.id.type_income_dashboard);
            mType.setText(type);
        }
        public void setIncomeAmount(int Amount)
        {
            TextView mAmount=mIncomeView.findViewById(R.id.amount_income_dashboard);

            mAmount.setText(String.valueOf(Amount));
        }
        public void setIncomeDate(String date)
        {
            TextView mDate=mIncomeView.findViewById(R.id.date_income_dashboard);
            mDate.setText(date);
        }

    }

    //For Expense Data

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder{

        View mExpenseView;
        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            mExpenseView=itemView;
        }

        public void setExpenseType(String type)
        {
            TextView mType=mExpenseView.findViewById(R.id.type_expense_dashboard);
            mType.setText(type);
        }
        public void setExpenseAmount(int Amount)
        {
            TextView mAmount=mExpenseView.findViewById(R.id.amount_expense_dashboard);
            mAmount.setText(String.valueOf(Amount));

        }
        public void setExpenseDate(String date)
        {
            TextView mDate=mExpenseView.findViewById(R.id.date_expense_dashboard);
            mDate.setText(date);
        }
    }




}





