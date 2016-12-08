package com.harshal.roomiesexpenses.fragments;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import np.Button;
import np.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.harshal.roomiesexpenses.RoomiesExpensesActivity;
import com.harshal.roomiesexpenses.R;
import com.harshal.roomiesexpenses.adapters.RoommatesLVAdapter;
import com.harshal.roomiesexpenses.database.ExpensesDbHelper;
import com.harshal.roomiesexpenses.entities.Roommate;
import com.harshal.roomiesexpenses.utils.Util;
import com.melnykov.fab.FloatingActionButton;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;


public class AddRoommateFragment extends REBaseFragment {

    private static final int GALLARY_ACTIVITY_REQUEST_CODE = 1;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 2;
    private static final int PICK_CONTACT = 3;

    private ImageView imageViewPicture;
    private EditText editTextName, editTextEmailId, editTextPhoneNo;
    private Button btnAddRoommate, btnCancel;
    private ListView listViewRoommates;
    private ExpensesDbHelper dbHelper;
    private ArrayList<Roommate> arrLstRoommates;
    private AlertDialog alertDialog;
    private Bitmap bitmap = null;
    private RelativeLayout relLayAddRoommate;
    private boolean isToEdit = false;
    private Roommate roommateToEdit = new Roommate(0, "", "", "", null, true);
    private ImageView imgGetFromContacts;
    private FloatingActionButton fabAddRoommate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_roommate, container, false);
        inItAllUIViews(view);
        registerForContextMenu(listViewRoommates);
        dbHelper = new ExpensesDbHelper(mContext);
        setAdapterOnRoommatesListView();

        return view;
    }

    private void setAdapterOnRoommatesListView() {
        arrLstRoommates = dbHelper.getAllRoomies();
        listViewRoommates.setAdapter(new RoommatesLVAdapter(mContext, arrLstRoommates));
        fabAddRoommate.setVisibility(View.VISIBLE);
        fabAddRoommate.show(true);
    }

    private void inItAllUIViews(View view) {
        relLayAddRoommate = (RelativeLayout)view.findViewById(R.id.relLayAddRoommate);
        imageViewPicture = (ImageView)view.findViewById(R.id.imageViewPicture);
        editTextName = (EditText) view.findViewById(R.id.editTextName);
        editTextEmailId = (EditText) view.findViewById(R.id.editTextEmailId);
        editTextPhoneNo = (EditText) view.findViewById(R.id.editTextPhoneNo);
        btnAddRoommate = (Button)view.findViewById(R.id.btnAddRoommate);
        btnCancel = (Button)view.findViewById(R.id.btnCancel);
        listViewRoommates = (ListView)view.findViewById(R.id.listViewRoommates);

        btnAddRoommate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewRoommate();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearLinLayAddRoommatesData();
            }
        });

        imageViewPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogToAddPicture();
            }
        });

        fabAddRoommate = (FloatingActionButton)view.findViewById(R.id.fabAddRoommate);
        fabAddRoommate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleAddRoommatesLayout();
            }
        });
        fabAddRoommate.attachToListView(listViewRoommates);

        imgGetFromContacts = (ImageView)view.findViewById(R.id.imgGetFromContacts);
        imgGetFromContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, PICK_CONTACT);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.listViewRoommates) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(arrLstRoommates.get(info.position).getName());
            String[] menuItems = {"Edit", "Delete", ""};
            if(arrLstRoommates.get(info.position).isActive()){
                menuItems[2] = "Inactive";
            }else{
                menuItems[2] = "Active";
            }
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        switch (menuItemIndex) {
            case 0:
                isToEdit = true;
                btnAddRoommate.setText("Edit Roommate");
                bitmap = arrLstRoommates.get(info.position).getPicture();
                roommateToEdit = arrLstRoommates.get(info.position);
                editTextName.setText(arrLstRoommates.get(info.position).getName());
                editTextEmailId.setText(arrLstRoommates.get(info.position).getEmailId());
                editTextPhoneNo.setText(arrLstRoommates.get(info.position).getMobileNo());
                imageViewPicture.setImageBitmap(bitmap);
                relLayAddRoommate.setVisibility(View.VISIBLE);

                fabAddRoommate.setVisibility(View.GONE);
                break;

            case 1:
                alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setTitle(arrLstRoommates.get(info.position).getName());
                alertDialog.setMessage("Are you sure do you want to delete this roommate?");
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                        dbHelper.deleteRoommateFromRoomiesTable(arrLstRoommates.get(info.position).getId());
                        setAdapterOnRoommatesListView();
                    }
                });
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
                break;

            case 2:
                dbHelper.toggleRoommatesActivationStatus(arrLstRoommates.get(info.position).getId());
                setAdapterOnRoommatesListView();
                break;
            default:
                break;
        }
        return true;
    }

    private void toggleAddRoommatesLayout() {
        if (relLayAddRoommate.getVisibility() == View.VISIBLE) {
            relLayAddRoommate.setVisibility(View.GONE);
            clearLinLayAddRoommatesData();
        }else {
            relLayAddRoommate.setVisibility(View.VISIBLE);
            fabAddRoommate.setVisibility(View.GONE);
        }
    }

    private void showDialogToAddPicture() {
        alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.setTitle("ADD CAKE PICTURE");
        alertDialog.setMessage("Would You Like To Pick Picture From : ");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "GALLARY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
                Intent pickPhoto = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, GALLARY_ACTIVITY_REQUEST_CODE);
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "CAMERA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
                // create Intent to take a picture and return control to the calling application
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // start the image capture Intent
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });
        alertDialog.show();
    }

    private void addNewRoommate() {
        if(isValidInfo()){
            if(bitmap == null){
                bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.user_for_db);
            }
            Roommate roommate = new Roommate(roommateToEdit.getId(), editTextName.getText().toString(),
                    editTextEmailId.getText().toString(), editTextPhoneNo.getText().toString(), bitmap, roommateToEdit.isActive());
            if(isToEdit){
                dbHelper.updateRoommateIntoRoomiesTable(roommate);
            }else{
                dbHelper.insertRoommateIntoRoomiesTable(roommate);
            }

            if(mContext!=null)
                hideKeyboard(mContext);

            clearLinLayAddRoommatesData();
            setAdapterOnRoommatesListView();
        }else{
            Toast.makeText(mContext, "Please enter valid information", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearLinLayAddRoommatesData() {
        isToEdit = false;
        bitmap = null;
        imageViewPicture.setImageResource(R.mipmap.ic_add_member);
        editTextName.setText("");
        editTextEmailId.setText("");
        editTextPhoneNo.setText("");
        if (relLayAddRoommate.getVisibility() == View.VISIBLE)
            relLayAddRoommate.setVisibility(View.GONE);
        btnAddRoommate.setText("Add Roommate");
        fabAddRoommate.setVisibility(View.VISIBLE);
    }

    private boolean isValidInfo() {
        boolean isValid = false;
        if(editTextName.getText().toString().equals(""))
            editTextName.setError("Please Enter Name");
        else
            editTextName.setError(null);
        /*if(editTextEmailId.getText().toString().equals(""))
            editTextEmailId.setError("Please Enter Email Id");
        else
            editTextEmailId.setError(null);
        if(editTextPhoneNo.getText().toString().equals(""))
            editTextPhoneNo.setError("Please Enter Mobile No");
        else
            editTextPhoneNo.setError(null);*/

        if(editTextName.getError() == null /*&& editTextEmailId.getError() == null && editTextPhoneNo.getError() == null*/)
            isValid = true;
        return isValid;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GALLARY_ACTIVITY_REQUEST_CODE:
                if (resultCode == mContext.RESULT_OK) {
                    Uri selectedImage = data.getData();
                    try {
                        bitmap = decodeUri(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    imageViewPicture.setImageBitmap(bitmap);
                }else if (resultCode == mContext.RESULT_CANCELED) {
                    // User cancelled the image capture
                    Toast.makeText(mContext, "Gallery Image Is Not Selected...", Toast.LENGTH_SHORT).show();
                }else {
                    // Image capture failed, advise user
                    Toast.makeText(mContext, "Please Try Again Now...", Toast.LENGTH_SHORT).show();
                }
                break;


            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
                if (resultCode == mContext.RESULT_OK) {
                    bitmap = (Bitmap) data.getExtras().get("data");
                    imageViewPicture.setImageBitmap(bitmap);
                }else if (resultCode == mContext.RESULT_CANCELED) {
                    // User cancelled the image capture
                    Toast.makeText(mContext, "Captured Image is Cancelled...", Toast.LENGTH_SHORT).show();
                }else {
                    // Image capture failed, advise user
                    Toast.makeText(mContext, "Please Try Again Now...", Toast.LENGTH_SHORT).show();
                }
                break;

            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {
                    setContactInfo(data);
                }
                break;
        }
    }

    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true; //
        BitmapFactory.decodeStream(mContext.getContentResolver()
                .openInputStream(selectedImage), null, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 260; // Is this kilobites? 306

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        o2.inScaled = false; // Better quality?
        Bitmap mImageBitmap = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(selectedImage), null, o2);
        return BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(selectedImage), null, o2);

    }

    private void setContactInfo(Intent data) {
        String contactId="", name="", phoneNumber="", emailAddress="", city="", postalCode="";
        Uri contactData = data.getData();
        try {
            Cursor c = mContext.getContentResolver().query(contactData, null, null, null, null);
            if (c.moveToFirst()) {
                contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                name = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));

                Cursor cursor = mContext.getContentResolver().query(contactData, null, null, null, null);
                cursor.moveToFirst();
                int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                phoneNumber = cursor.getString(phoneIndex);
                cursor.close();

                // Find Email Addresses
                Cursor emails = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null);
                while (emails.moveToNext()) {
                    emailAddress = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                }
                emails.close();
/*
                String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                if (hasPhone.equalsIgnoreCase("1"))
                    hasPhone = "true";
                else
                    hasPhone = "false";

                if (Boolean.parseBoolean(hasPhone)) {
                    Cursor phones = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{contactId}, null);
                    int count = phones.getCount();
                    while (phones.moveToNext()) {
                        phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    phones.close();
                }
*/

                bitmap = null;
                InputStream inputStream = ContactsContract.Contacts
                        .openContactPhotoInputStream(mContext.getContentResolver(),
                                ContentUris.withAppendedId(
                                        ContactsContract.Contacts.CONTENT_URI,
                                        new Long(contactId)));
                if (inputStream != null) {
                    bitmap = BitmapFactory.decodeStream(inputStream);
                }
                if (inputStream != null) {
                    inputStream.close();
                }

            }  //while (cursor.moveToNext())
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder("Unable to read ");
        if("".equals(emailAddress))
            builder.append("Email, ");
        if("".equals(phoneNumber))
            builder.append("Phone Number, ");
        if("".equals(emailAddress) || "".equals(emailAddress)){
            String msg = builder.substring(0, builder.length()-2);
            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
        }
        editTextName.setText(name);
        editTextPhoneNo.setText(phoneNumber);
        editTextEmailId.setText(emailAddress);
        if(bitmap != null) {
            imageViewPicture.setImageBitmap(bitmap);
        }else{
            imageViewPicture.setImageResource(R.mipmap.ic_add_member);
        }
    }
}
