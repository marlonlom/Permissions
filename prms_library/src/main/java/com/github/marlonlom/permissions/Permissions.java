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

package com.github.marlonlom.permissions;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Android permissions utility for requesting and checking results.
 *
 * @author marlonlom
 */
public final class Permissions {

  /**
   * For each permissions . builder.
   *
   * @param permissions the permissions
   * @return the permissions . builder
   */
  public static Permissions.Builder forEach(String... permissions) {
    return new Builder(permissions);
  }

  /**
   * The interface With permissions.
   *
   * @author marlonlom
   */
  public interface IWithPermissions {
    /**
     * Using with request code.
     *
     * @param requestCode the request code
     * @return the with request code
     */
    IWithRequestCode using(int requestCode);

    /**
     * Comparing with request code.
     *
     * @param requestCode the request code
     * @param requestedCode the requested code
     * @return the with request code
     */
    IWithRequestCode comparing(int requestCode, int requestedCode);
  }

  /**
   * The interface With request code.
   *
   * @author marlonlom
   */
  public interface IWithRequestCode {
    /**
     * Request boolean.
     *
     * @param activity the activity
     * @return the boolean
     */
    boolean request(Activity activity);

    /**
     * With results with grant results.
     *
     * @param grantResults the grant results
     * @return the with grant results
     */
    IWithGrantResults withResults(int[] grantResults);
  }

  /**
   * The interface With grant results.
   *
   * @author marlonlom
   */
  public interface IWithGrantResults {
    /**
     * Check.
     *
     * @param listeners the listeners
     */
    void check(PermissionsCheckedListener[] listeners);
  }

  /**
   * The interface Permissions checked listener.
   *
   * @author marlonlom
   */
  public interface PermissionsCheckedListener {
    /**
     * On granted.
     */
    void onGranted();

    /**
     * On denied.
     */
    void onDenied();
  }

  /**
   * The fluent builder implementation class.
   *
   * @author marlonlom
   */
  public static class Builder implements IWithPermissions, IWithRequestCode, IWithGrantResults {

    private String[] mPermissions;
    private int mRequestCode;
    private int mComparedCode;
    private int[] mGrantResults;

    /**
     * Instantiates a new Builder.
     *
     * @param permissions the permissions
     */
    public Builder(String[] permissions) {
      mPermissions = permissions;
    }

    @Override public IWithRequestCode using(int requestCode) {
      mRequestCode = requestCode;
      return this;
    }

    @Override public IWithRequestCode comparing(int requestCode, int requestedCode) {
      mRequestCode = requestCode;
      mComparedCode = requestedCode;
      return this;
    }

    @Override public boolean request(Activity activity) {
      boolean granted = false;
      ArrayList<String> permissionsNeeded = new ArrayList<>();
      for (final String permission : this.mPermissions) {
        int permissionCheck = ContextCompat.checkSelfPermission(activity, permission);
        boolean hasPermission = (permissionCheck == PackageManager.PERMISSION_GRANTED);
        granted = hasPermission;
        if (!hasPermission) {
          permissionsNeeded.add(permission);
        }
      }
      if (!granted) {
        ActivityCompat.requestPermissions(activity,
            permissionsNeeded.toArray(new String[permissionsNeeded.size()]), this.mRequestCode);
      }
      return granted;
    }

    @Override public IWithGrantResults withResults(int[] grantResults) {
      this.mGrantResults = Arrays.copyOf(grantResults, grantResults.length);
      return this;
    }

    @Override public void check(PermissionsCheckedListener[] listeners) {
      if (mComparedCode == mRequestCode && mGrantResults.length > 0) {
        for (int i = 0; i < listeners.length; i++) {
          final PermissionsCheckedListener listener = listeners[i];
          if (listener != null) {
            switch (mGrantResults[i]) {
              case PackageManager.PERMISSION_GRANTED:
                listener.onGranted();
                break;
              case PackageManager.PERMISSION_DENIED:
                listener.onDenied();
                break;
            }
          }
        }
      }
    }
  }
}
