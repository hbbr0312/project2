package com.example.user.test;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment1 extends Fragment {

    private ListView lv;
    ArrayList<HashMap<String, String>> contactList;
    private JSONArray jsonArray = new JSONArray();
    private String user_id;
    private String downresults;

    public Fragment1() {
        // Required empty public constructor
    }

//리스트 눌렀을 때 화면전환

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.w("fragment1","onCreateView");
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_fragment1, container, false);
        contactList = new ArrayList<>();
        lv = (ListView) view.findViewById(R.id.ListView);

        user_id = getArguments().getString("id");

        Button downloadBt = (Button) view.findViewById(R.id.download);
        downloadBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("fragment1","click!");
                downloadContact();
            }
        });

        Button uploadBt = (Button) view.findViewById(R.id.upload);
        uploadBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadConbtacts();
            }
        });

        askForWriteContactPermission(getActivity());
        askForReadExternalPermission(getActivity());
        askForWriteExternalPermission(getActivity());

        if (askForContactPermission(getActivity())) {
                loadContacts();
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //Data data = contactList.get(position);
                        HashMap<String,String> data = contactList.get(position);//값 전달
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + data.get("mobile")));
                        Bundle bundle = new Bundle();
                        bundle.putString(ContactsContract.Intents.Insert.PHONE, data.get("mobile"));
                        intent.putExtras(bundle);

                        startActivity(intent);
                    }
                });
        }



        //Log.e("fragment1","permission is "+askForContactPermission(getActivity()));

        //permission = askForContactPermission(getActivity());

        return view;
    }

    private void uploadConbtacts(){
        contactList.clear();
        jsonArray = new JSONArray();

        ContentResolver contentResolver = getActivity().getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);
        if(cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                queryContactInfo(Integer.parseInt(id));
            }
        }

        for(int i = 0; i < contactList.size(); i++){
            HashMap<String, String> contact = new HashMap<>();
            contact = contactList.get(i);

            JSONObject obj = new JSONObject();
            try {
                obj.put("name", contact.get("name"));
                obj.put("email",contact.get("email"));
                obj.put("mobile",contact.get("mobile"));

                Bitmap bm = queryContactImage(Integer.parseInt(contact.get("photo")));

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
                byte[] b = baos.toByteArray();
                String encodedImage =Base64.encodeToString(b,Base64.DEFAULT);

                obj.put("img",encodedImage);

            }catch (JSONException e){
                e.printStackTrace();
            }
            jsonArray.put(obj);
        }
        Log.e("=============",jsonArray.toString());
        Log.e("id",user_id);

        new JSONTask().execute("http://socrip4.kaist.ac.kr:3880/uploadcont/"+user_id);


    }

    //연락처 불러오기
    private void loadContacts() {

        contactList.clear();

        ContentResolver contentResolver = getActivity().getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);
        if(cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                queryContactInfo(Integer.parseInt(id));

            }

        }

        ListAdapter adapter = new ExtendedSimpleAdapter(
                getActivity(), contactList,
                R.layout.list_item, new String[]{"name","email","mobile","photo"},
                new int[] {R.id.name,R.id.email, R.id.mobile,R.id.photo});

        lv.setAdapter(adapter);


    }

    private void downloadContact(){
        downresults = new String();
        jsonArray = new JSONArray();

        new JSONTask().execute("http://socrip4.kaist.ac.kr:3880/cont/"+user_id);


    }

    private void queryContactInfo(int rawContactId) {

        ContentResolver contentResolver = getActivity().getContentResolver();

        Cursor c = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[] {
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.PHOTO_ID

                }, ContactsContract.Data.RAW_CONTACT_ID + "=?", new String[] { Integer.toString(rawContactId) }, null);


        String name = "";
        String number = "Phone Number";
        String email ="Email";

        //int photoId = 555;
        String photo ="Nophoto";

        HashMap<String, String> contact = new HashMap<>();


        if (c != null) {
            if (c.moveToFirst()) {

                name = c.getString(2);
                number = c.getString(0);
                int type = c.getInt(1);


                int photoId = c.getInt(3);
                Bitmap bitmap = queryContactImage(photoId);

                if(photoId != 0)
                    photo = Integer.toString(photoId);


                Cursor emails = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        new String[]{Integer.toString(rawContactId)}, null);

                while (emails.moveToNext())
                {
                    email = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                }

                emails.close();

                //showSelectedNumber(type, number, name, bitmap);
            }
            c.close();
        }

        contact.put("name",name);
        contact.put("mobile",number);
        contact.put("email",email);
        contact.put("photo",photo);

        contactList.add(contact);

    }


    private Bitmap queryContactImage(int imageDataRow) {

        ContentResolver contentResolver = getActivity().getContentResolver();

        Cursor c = contentResolver.query(ContactsContract.Data.CONTENT_URI, new String[] {
                ContactsContract.CommonDataKinds.Photo.PHOTO
        }, ContactsContract.Data._ID + "=?", new String[] {
                Integer.toString(imageDataRow)
        }, null);
        byte[] imageBytes = null;
        if (c != null) {
            if (c.moveToFirst()) {
                imageBytes = c.getBlob(0);
            }
            c.close();
        }

        if (imageBytes != null) {
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        } else {
            return null;
        }
    }


    //*contact permission*//
    public static final int PERMISSION_REQUEST_CONTACT = 123;
    public boolean askForContactPermission(final Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                        Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder( context);
                    builder.setTitle("Contacts access needed");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("please confirm Contacts access");//TODO put real question
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {Manifest.permission.READ_CONTACTS}
                                    , PERMISSION_REQUEST_CONTACT);
                        }
                    });
                    builder.show();

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions( (Activity) context,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            PERMISSION_REQUEST_CONTACT);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
                return false;
            }
            else
            {
                return true;
            }
        }
        else{
            return true;
        }
    }
    public boolean askForReadExternalPermission(final Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder( context);
                    builder.setTitle("External Storage access needed");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("please confirm External Storage access");//TODO put real question
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {Manifest.permission.READ_EXTERNAL_STORAGE}
                                    , PERMISSION_REQUEST_CONTACT);
                        }
                    });
                    builder.show();
                } else {
                    ActivityCompat.requestPermissions( (Activity) context,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_CONTACT);
                }
                return false;
            }
            else { return true;
            }
        }
        else{ return true;
        }
    }
    public boolean askForWriteExternalPermission(final Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder( context);
                    builder.setTitle("External Storage access needed");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("please confirm External Storage access");//TODO put real question
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {Manifest.permission.WRITE_EXTERNAL_STORAGE}
                                    , PERMISSION_REQUEST_CONTACT);
                        }
                    });
                    builder.show();
                } else {
                    ActivityCompat.requestPermissions( (Activity) context,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_CONTACT);
                }
                return false;
            }
            else { return true;
            }
        }
        else{ return true;
        }
    }

    public boolean askForWriteContactPermission(final Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                        Manifest.permission.WRITE_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder( context);
                    builder.setTitle("Contacts access needed");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("please confirm Contacts access");//TODO put real question
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {Manifest.permission.WRITE_CONTACTS}
                                    , PERMISSION_REQUEST_CONTACT);
                        }
                    });
                    builder.show();

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions( (Activity) context,
                            new String[]{Manifest.permission.WRITE_CONTACTS},
                            PERMISSION_REQUEST_CONTACT);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
                return false;
            }
            else
            {
                return true;
            }
        }
        else{
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CONTACT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //loadContacts();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast.makeText(getActivity(), "GET_ACCOUNTS Denied",
                            Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    public void addContact(String name, String email, String mobile, byte[] img){
        ArrayList <ContentProviderOperation> ops = new ArrayList < ContentProviderOperation > ();

        ops.add(ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        if (name != null) {
            ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(
                            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                            name).build());
        }

        if (email != null) {
            ops.add(ContentProviderOperation.
                    newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, email)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build());
        }

        if (mobile != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, mobile)
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                    .build());
        }

        if (img != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, img)
                    .build());
        }

        try {
            Log.e("store","process");
            getActivity().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public byte[] toByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        return stream.toByteArray();
    }

    public class JSONTask extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("contacts", jsonArray);
                Log.e("id",user_id);
                jsonObject.accumulate("user_id",user_id);

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
                    //URL url = new URL("http://192.168.25.16:3000/users");
                    URL url = new URL(urls[0]);
                    //연결을 함
                    Log.e("url",urls[0]);
                    con = (HttpURLConnection) url.openConnection();


                    con.setRequestMethod("POST");//POST방식으로 보냄
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
                    //con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                    con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    con.connect();

                    //서버로 보내기위해서 스트림 만듬
                    OutputStream outStream = con.getOutputStream();

                    //버퍼를 생성하고 넣음
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    Log.e("====1====",jsonObject.toString());
                    writer.write(jsonObject.toString());
                    Log.e("====2====",jsonObject.toString());
                    writer.flush();
                    Log.e("====3====",jsonObject.toString());
                    writer.close();//버퍼를 받아줌
                    Log.e("====4====",jsonObject.toString());

                    //서버로 부터 데이터를 받음
                    InputStream stream = con.getInputStream();
                    Log.e("===5=====",jsonObject.toString());

                    reader = new BufferedReader(new InputStreamReader(stream));
                    Log.e("====6====",jsonObject.toString());
                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }
                    Log.e("======3======",buffer.toString());
                    return buffer.toString();//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임

                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(con != null){
                        con.disconnect();
                    }
                    try {
                        if(reader != null){
                            reader.close();//버퍼를 닫아줌
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            /*
            try {
                JSONObject jsonObject = new JSONObject(result);
                imgdata = jsonObject.getString("name");
            }catch (JSONException e){
                e.printStackTrace();
            }

            //Log.e("fsd",result);


            byte[] bytedata = Base64.decode(imgdata,0);
            ByteArrayInputStream inStream = new ByteArrayInputStream(bytedata);
            Bitmap bitmap = BitmapFactory.decodeStream(inStream) ;

            imageView.setImageBitmap(bitmap);
            */
            if( result != "Contact upload complete!") {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray json = jsonObject.getJSONArray("contacts");
                    Log.e("json", "process");
                    for (int i = 0; i < json.length(); i++) {
                        Log.e("for", "error");
                        JSONObject iter = json.getJSONObject(i);
                        String name = iter.getString("name");
                        String email = iter.getString("email");
                        String mobile = iter.getString("mobile");
                        String img = iter.getString("img");

                        byte[] bytedata = Base64.decode(img, 0);

                        addContact(name, email, mobile, bytedata);
                    }
                } catch (JSONException e) {
                    Log.e("json", "error");
                    e.printStackTrace();
                }
                Log.e("load", "error");
                loadContacts();
            }


            Log.e("af","post");

            //textView.setText(result);//서버로 부터 받은 값을 출력해주는 부
        }
    }

}