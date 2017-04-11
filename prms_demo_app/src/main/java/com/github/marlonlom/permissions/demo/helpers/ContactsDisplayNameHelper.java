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

package com.github.marlonlom.permissions.demo.helpers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;

/**
 * Contacts display name helper class.
 *
 * @author marlonlom
 */
public class ContactsDisplayNameHelper {

  /**
   * Query contact string.
   *
   * @param context the context
   * @param uri the uri
   * @return the string
   */
  public static String queryContact(@NonNull Context context, @NonNull Uri uri) {
    Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
    StringBuilder stringBuilder = new StringBuilder();
    if (cursor != null) {
      cursor.moveToFirst();
      int columnIndex =
          cursor.getColumnIndex(ContactsContract.CommonDataKinds.Identity.DISPLAY_NAME);
      final String displayName = cursor.getString(columnIndex);
      stringBuilder.append(displayName.trim());
      cursor.close();
    }
    return stringBuilder.toString();
  }
}
