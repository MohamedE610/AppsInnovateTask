package com.example.e610.appsinnovatetask;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.e610.appsinnovatetask.Adapters.ContactsAdapter;

import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity {

    /***  3.Load phone contacts
      this activity to display Contacts by using RecyclerView and ContactsAdapter  ***/

    ArrayList<String> contactsList;
    private RecyclerView recyclerView;
    private ContactsAdapter contactsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        Intent intent=getIntent();
        contactsList=intent.getStringArrayListExtra("contactsList");
        contactsAdapter = new ContactsAdapter(contactsList,this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler1);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.setAdapter(contactsAdapter);

    }

}
