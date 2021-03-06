package com.scleroid.nemai.data.controller;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.scleroid.nemai.data.AppDatabase;
import com.scleroid.nemai.data.models.Address;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Ganesh on 22-11-2017.
 */

public class AddressLab {

    private static final String TAG = AddressLab.class.getName();
    public static List<Address> addresss;
    private static AddressLab addressLab;
    private Address address;
    private Context context;

    public AddressLab(Context context) {
        this.context = context.getApplicationContext();


    }

    private static Address insertAddress(final AppDatabase db, Address address) {
        db.addressDao().insert(address);
        return address;
    }

    private static List<Address> getAllAddresss(final AppDatabase db) {
        List<Address> addresss = db.addressDao().getAll();
        return addresss;
    }

    private static Address getAddress(final AppDatabase db, int serialNo) {
        Address address = db.addressDao().findById(serialNo);
        return address;
    }

    private static int getCount(final AppDatabase db) {
        int count = db.addressDao().countAddress();
        return count;
    }

    private static void deleteAddress(final AppDatabase db, Address address) {
        db.addressDao().delete(address);

    }

    private static void deleteAllAddress(final AppDatabase db) {
        db.addressDao().nukeTable();

    }

    private static void updateAddress(AppDatabase db, Address address) {
        db.addressDao().update(address);
    }

    /* public static Address newAddress(final Context context) {

         AddressLab.addNewAddressAsync task = new AddressLab.addNewAddressAsync(AppDatabase.getAppDatabase(context), new Address());

         try {
             return task.execute().get();
         } catch (InterruptedException e) {
             e.printStackTrace();
         } catch (ExecutionException e) {
             e.printStackTrace();
         }
         return null;
     }
 */
    private static List<Address> getAllAddresssAsync(final Context context) {

        AddressLab.GetAllAsync task = new AddressLab.GetAllAsync(AppDatabase.getAppDatabase(context), context);

        Log.d(TAG, "addresss getAllAddresss " + addresss);

        try {
            return task.execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void addAddress(final Address address, final AppDatabase appDatabase) {


        addNewAddressAsync task = new addNewAddressAsync(appDatabase, address);
        task.execute();

    }

    public static void updateAddress(final Address address, final AppDatabase appDatabase) {


        updateAddressAsync task = new updateAddressAsync(appDatabase, address);
        task.execute();

    }

    public static void boomTable(final Context context) {

        AddressLab.DeleteAllAsync task = new AddressLab.DeleteAllAsync(AppDatabase.getAppDatabase(context));
        task.execute();
    }

    public static void DeleteUser(@NonNull final AppDatabase db, Address address) {
        AddressLab.DeleteUserAsync task = new AddressLab.DeleteUserAsync(db, address);
        task.execute();
    }

    private static class GetAllAsync extends AsyncTask<Void, Void, List<Address>> {

        private final AppDatabase mDb;

        ProgressDialog dialog;
        Context context;
        private List<Address> addresss;

        public GetAllAsync(AppDatabase db, Context context) {
            mDb = db;
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(false);
            dialog.setMessage("Retrieving...");
            dialog.show();
        }

        @Override
        protected void onPostExecute(List<Address> addresss) {
            //    listener.onResultsReceived(parcels);
            super.onPostExecute(addresss);

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        @Override
        protected List<Address> doInBackground(final Void... params) {
            addresss = getAllAddresss(mDb);
            Log.d(TAG, "addresss asynctask " + getAllAddresss(mDb));
            return addresss;
        }


    }

    private static class DeleteAllAsync extends AsyncTask<Void, Void, Void> {

        private final AppDatabase mDb;


        DeleteAllAsync(AppDatabase db) {
            mDb = db;

        }

        @Override
        protected Void doInBackground(final Void... params) {
            deleteAllAddress(mDb);
            return null;
        }

    }

    private static class DeleteUserAsync extends AsyncTask<Void, Void, Void> {

        private final AppDatabase mDb;
        private final Address address;

        DeleteUserAsync(AppDatabase db, Address address) {
            mDb = db;
            this.address = address;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            deleteAddress(mDb, address);
            return null;
        }

    }


    private static class addNewAddressAsync extends AsyncTask<Void, Void, Address> {

        private final AppDatabase mDb;
        private final Address address;

        addNewAddressAsync(AppDatabase db, Address address) {
            mDb = db;
            this.address = address;
        }

        @Override
        protected Address doInBackground(final Void... params) {
            insertAddress(mDb, address);

            return address;
        }

    }

    private static class updateAddressAsync extends AsyncTask<Void, Void, Address> {

        private final AppDatabase mDb;
        private final Address address;

        updateAddressAsync(AppDatabase db, Address address) {
            mDb = db;
            this.address = address;
        }

        @Override
        protected Address doInBackground(final Void... params) {
            updateAddress(mDb, address);

            return address;
        }

    }


}
