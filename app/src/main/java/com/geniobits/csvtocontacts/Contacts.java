package com.geniobits.csvtocontacts;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;

public class Contacts {
    private String name;
    private String phone_number;
    private String email;

    public Contacts(String name, String phone_number, String email) {
        this.name = name;
        this.phone_number = phone_number;
        this.email = email;
    }

    public boolean ImportToContacts(Context context){
        ArrayList<ContentProviderOperation> ops = new ArrayList < ContentProviderOperation > ();

        ops.add(ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        //------------------------------------------------------ Names
        if (name != null) {
            // first and last names
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, "")
                    .build());
        }

        //------------------------------------------------------ Mobile Number
        if (phone_number != null) {
            ops.add(ContentProviderOperation.
                    newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone_number)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build());
        }

        //------------------------------------------------------ Email
        if (email != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, email)
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                    .build());
        }

        // Asking the Contact provider to create a new contact
        try {
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean askPermission(Context context){
        final boolean[] is_permission = {false};
        Dexter.withContext(context)
                .withPermissions(
                        Manifest.permission.WRITE_CONTACTS,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
                /* ... */ is_permission[0] =report.areAllPermissionsGranted();
            }
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */
                token.continuePermissionRequest();
            }
        }).check();
        return is_permission[0];
    }

    public static void ImportAllContacts(ArrayList<Contacts> contactsArrayList, Context context){
        for (Contacts contact: contactsArrayList){
            if(contact.phone_number!=null)
                if(!contact.phone_number.equals(""))
                    contact.ImportToContacts(context);
        }
    }

    public static void DeleteAllContacts(ArrayList<Contacts> contactsArrayList, Context context){
        for (Contacts contact: contactsArrayList){
            contact.deleteContact(context);
        }
    }

    public boolean deleteContact(Context ctx) {
        try {
            Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone_number));
            Cursor cur = ctx.getContentResolver().query(contactUri, null, null, null, null);
            try {
                if (cur.moveToFirst()) {
                    do {
                        if (cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)).equalsIgnoreCase(name)) {
                            String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                            ctx.getContentResolver().delete(uri, null, null);

                        }

                    } while (cur.moveToNext());
                }
                return true;

            } catch (Exception e) {
                System.out.println(e.getStackTrace());
                return false;
            } finally {
                cur.close();
            }

        }catch (Exception e) {
            System.out.println(e.getStackTrace());
            return false;
        }
    }
}
