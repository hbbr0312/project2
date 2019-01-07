package com.example.user.test;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
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

public class Fragment2 extends Fragment {
    private GridView gv;
    private Button upload;
    private ArrayList<String> uploadList;
    private Bitmap bm;

    public Fragment2(){
    }
    View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment2, container, false);
        v=view;
        Log.i("fragment2","onCreateView");
        setHasOptionsMenu(true);

        gv = (GridView) view.findViewById(R.id.ImgGridView);
        Button loadI = (Button) view.findViewById(R.id.loadi);
        loadI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("fragment2","click!");
                if(checkPermissionREAD_EXTERNAL_STORAGE(getActivity())) loadImage(v,getActivity());
            }
        });


        if(checkPermissionREAD_EXTERNAL_STORAGE(getActivity())) {
            Log.e("permission Image","true");
            loadImage(view,getActivity());
        }



        return view;
    }
    /*
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser)
        {
            if(checkPermissionREAD_EXTERNAL_STORAGE(getActivity())) {
                loadImage(v,getActivity());
                Log.i("fragment2","fragment2");
            }
        }
        else
        {
            //preload 될때(전페이지에 있을때)
        }
    }*/

    public void loadImage(View view,Context con){
        final ImageAdapter ia = new ImageAdapter(con);
        gv.setAdapter(ia);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ia.callImageViewer(position);
            }
        });
        upload = (Button) view.findViewById(R.id.dbupload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("uploading...","....");
                uploadList = ia.getList();
                Log.e("uploadList size",""+uploadList.size());
                new uploadTask().execute("http://socrip4.kaist.ac.kr:3980/uploadimage/hbbr");
            }
        });
    }

    /**Permission*/
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do your stuff
                } else {
                    Toast.makeText(getActivity(), "GET_ACCOUNTS Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);

                // other 'case' lines to check for other
                // permissions this app might request
        }
    }


    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        Log.i("fragment2","checkPermissionREAD_EXTERNAL_STORAGE");
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    //showDialog("External storage", context,
                    //Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        Log.i("fragment2","showDialog");
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    public String encoding(String imgPath){
        Log.i("encoding...",imgPath);
        bm = BitmapFactory.decodeFile(imgPath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,40,baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b,Base64.DEFAULT);
    }

    /**업로드**/
    public class uploadTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {

                try {
                    Log.e("uploadList size",""+uploadList.size());
                    for(int j=0; j<uploadList.size(); j++){
                    JSONObject jsonObject = new JSONObject();
                    JSONArray jsonArray;
                    JSONObject img;
                    JSONArray ar = new JSONArray();
                    //String send = "";

                    //JSONObject를 만들고 key value 형식으로 값을 저장해준다.

                        jsonObject = new JSONObject();
                        jsonArray = new JSONArray();
                        img = new JSONObject();

                        String imgpath = uploadList.get(j);
                        String current = encoding(imgpath);
                        int index = imgpath.lastIndexOf("/");
                        String filename = imgpath.substring(index + 1);
                        img.put("name", filename);
                        img.put("img", current);

                        String test = img.toString();
                        //jsonArray.put(img);
                        Log.e("=========", img.toString());
                        Log.e("jason ?",test.substring(test.length()-3,test.length()));
                        //jsonObject.put("images", jsonArray);


                        //Log.e("jsonarray", jsonObject.toString());
                        //send += jsonObject.toString() + "\n";



                    HttpURLConnection con = null;
                    BufferedReader reader = null;

                    try {
                        URL url = new URL(urls[0]);
                        //연결을 함

                        con = (HttpURLConnection) url.openConnection();

                        con.setRequestMethod("POST");//POST방식으로 보냄
                        con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                        con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
                        con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                        con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                        con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                        con.connect();

                        //서버로 보내기위해서 스트림 만듬
                        OutputStream outStream = con.getOutputStream();

                        //버퍼를 생성하고 넣음
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                        //Log.e("========", jsonObject.toString());

                        writer.write(img.toString() + "\n");
                        writer.flush();

                        //writer.close();//버퍼를 받아줌
                        writer.close();

                        //서버로 부터 데이터를 받음
                        InputStream stream = con.getInputStream();
                        Log.e("enter","input stream");

                        reader = new BufferedReader(new InputStreamReader(stream));
                        reader.read();
                        reader.close();

                        StringBuffer buffer = new StringBuffer();
                        String line;

                        while((line = reader.readLine()) != null){
                            buffer.append(line);
                        } ////여기까지 안가고 에러 뜸

                        /*
                        if(buffer.toString()=="ok") {
                            Log.i("server said ",buffer.toString());
                            Log.i("..........",j+"th img");
                            if(j==uploadList.size()-1) return "success";
                            continue;
                        }
                        else break;*/

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (con != null) {
                            con.disconnect();
                        }
                        try {
                            if (reader != null) {
                                reader.close();//버퍼를 닫아줌
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }
}