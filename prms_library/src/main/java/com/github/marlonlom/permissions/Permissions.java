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

    public static Permissions.Builder forEach(
            String... permissions) {
        return new Builder(permissions);
    }

    public interface IWithPermissions {
        IWithRequestCode using(int requestCode);

        IWithRequestCode comparing(int requestCode, int requestedCode);
    }

    public interface IWithRequestCode {
        boolean request(Activity activity);

        IWithGrantResults withResults(int[] grantResults);
    }

    public interface IWithGrantResults {
        void check(PermissionsCheckedListener[] listeners);
    }

    public interface PermissionsCheckedListener {
        void onGranted();

        void onDenied();
    }

    public static class Builder implements IWithPermissions, IWithRequestCode,
            IWithGrantResults {

        private String[] mPermissions;
        private int mRequestCode;
        private int mComparedCode;
        private int[] mGrantResults;

        public Builder(String[] permissions) {
            mPermissions = permissions;
        }

        @Override
        public IWithRequestCode using(int requestCode) {
            mRequestCode = requestCode;
            return this;
        }

        @Override
        public IWithRequestCode comparing(int requestCode, int requestedCode) {
            mRequestCode = requestCode;
            mComparedCode = requestedCode;
            return this;
        }

        @Override
        public boolean request(Activity activity) {
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
                        permissionsNeeded.toArray(new String[permissionsNeeded.size()]),
                        this.mRequestCode);
            }
            return granted;
        }

        @Override
        public IWithGrantResults withResults(int[] grantResults) {
            this.mGrantResults = Arrays.copyOf(grantResults, grantResults.length);
            return this;
        }

        @Override
        public void check(PermissionsCheckedListener[] listeners) {
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
