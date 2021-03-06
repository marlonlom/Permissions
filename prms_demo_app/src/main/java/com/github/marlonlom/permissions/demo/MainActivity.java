/*
 * Copyright (c) 2017, marlonlom
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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

/**
 * The type Main activity.
 *
 * @author marlonlom
 */
public class MainActivity extends AppCompatActivity {

  /**
   * The Contacts request code.
   */
  private static final int CONTACTS_REQUEST_CODE = 101;
  /**
   * The Pick contact request result code.
   */
  private static final int PICK_CONTACT = 102;
  /**
   * The text view demo title.
   */
  private TextView mTextViewDemoTitle;
  /**
   * The floating action button.
   */
  private FloatingActionButton mFloatingActionButton;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    mTextViewDemoTitle = (TextView) findViewById(R.id.textViewDemoTitle);
    mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
    mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        selectContacts();
      }
    });
  }

  /**
   * Select contacts.
   */
  private void selectContacts() {
    boolean isContactsPermissionRequested =
        Permissions.forEach(permission.READ_CONTACTS).using(CONTACTS_REQUEST_CODE).request(this);
    if (isContactsPermissionRequested) {
      openContactsWindow();
    }
  }

  /**
   * Open contacts window.
   */
  private void openContactsWindow() {
    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
    startActivityForResult(intent, PICK_CONTACT);
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    Permissions.forEach(permission.READ_CONTACTS)
        .comparing(CONTACTS_REQUEST_CODE, requestCode)
        .withResults(grantResults)
        .check(new Permissions.PermissionsCheckedListener[] {
            new Permissions.PermissionsCheckedListener() {

              @Override public void onGranted() {
                openContactsWindow();
              }

              @Override public void onDenied() {
                showDeniedMessage();
              }
            }
        });
  }

  /**
   * Show denied message.
   */
  private void showDeniedMessage() {
    Snackbar.make(mFloatingActionButton, R.string.text_error_contacts_permission_needed,
        Snackbar.LENGTH_LONG).setAction(R.string.home_action_retry, new View.OnClickListener() {
      @Override public void onClick(View v) {
        openContactsWindow();
      }
    }).show();
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    switch (requestCode) {
      case PICK_CONTACT:
        if (resultCode == Activity.RESULT_OK) {
          final Uri uri = data.getData();
          final String displayName = ContactsDisplayNameHelper.queryContact(this, uri);
          if (!displayName.isEmpty()) {
            mTextViewDemoTitle.setText(
                getString(R.string.text_info_contact_name_found).concat(displayName));
          } else {
            mTextViewDemoTitle.setText(R.string.text_error_contact_name_empty);
          }
        } else {
          mTextViewDemoTitle.setText(R.string.text_error_contact_not_selected);
        }

        break;
    }
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
