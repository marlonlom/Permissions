package com.github.marlonlom.permissions.demo;

import android.Manifest.permission;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.marlonlom.permissions.Permissions;
import com.github.marlonlom.permissions.demo.helpers.ContactsDisplayNameHelper;

public class MainActivity extends AppCompatActivity {

    private static final int CONTACTS_REQUEST_CODE = 101;
    private static final int PICK_CONTACT = 102;
    private TextView mTextViewDemoTitle;
    private FloatingActionButton mFloatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTextViewDemoTitle = (TextView) findViewById(R.id.textViewDemoTitle);
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectContacts();
            }
        });
    }

    private void selectContacts() {
        boolean requestSuccessful = Permissions.forEach(permission.READ_CONTACTS)
                .using(CONTACTS_REQUEST_CODE).request(this);
        if (requestSuccessful) {
            openContactsWindow();
        }
    }

    private void openContactsWindow() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Permissions.forEach(permission.READ_CONTACTS)
                .comparing(CONTACTS_REQUEST_CODE, requestCode)
                .withResults(grantResults)
                .check(new Permissions.PermissionsCheckedListener[]{
                        new Permissions.PermissionsCheckedListener() {

                            @Override
                            public void onGranted() {
                                openContactsWindow();
                            }

                            @Override
                            public void onDenied() {
                                showDeniedMessage();
                            }
                        }});
    }

    private void showDeniedMessage() {
        Snackbar.make(mFloatingActionButton,
                R.string.text_error_contacts_permission_needed,
                Snackbar.LENGTH_LONG)
                .setAction(R.string.home_action_retry,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                openContactsWindow();
                            }
                        })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PICK_CONTACT:
                if (resultCode == Activity.RESULT_OK) {
                    final Uri uri = data.getData();
                    final String displayName = ContactsDisplayNameHelper.queryContact(this, uri);
                    if (!displayName.isEmpty()) {
                        mTextViewDemoTitle.setText(
                                getString(R.string.text_info_contact_name_found).concat(
                                        displayName));
                    } else {
                        mTextViewDemoTitle.setText(R.string.text_error_contact_name_empty);
                    }
                } else {
                    mTextViewDemoTitle.setText(R.string.text_error_contact_not_selected);
                }

                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
