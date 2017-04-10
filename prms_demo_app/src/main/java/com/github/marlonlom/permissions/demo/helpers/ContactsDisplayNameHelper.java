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

    public static String queryContact(@NonNull Context context, @NonNull Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null,
                null, null);
        StringBuilder stringBuilder = new StringBuilder();
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.Identity.DISPLAY_NAME);
            final String displayName = cursor.getString(columnIndex);
            stringBuilder.append(displayName.trim());
            cursor.close();
        }
        return stringBuilder.toString();
    }
}
