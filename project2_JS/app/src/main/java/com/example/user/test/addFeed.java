package com.example.user.test;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.TestLooperManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class addFeed extends AppCompatActivity {

    Button button;
    ImageView profile_img, content_img;
    TextView name, id, date;
    EditText editText;
    Bitmap cont_bitmap, profile_bitmap;
    String content;
    String user_id, user_name, cur_date;
    String enc_profile, enc_content;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_feed);

        Intent intent = getIntent();
        user_id = intent.getStringExtra("id");
        user_name = intent.getStringExtra("name");
        cur_date = getTime();

        profile_img = (ImageView) findViewById(R.id.profile);
        content_img = (ImageView) findViewById(R.id.fphoto);
        button = (Button) findViewById(R.id.uploadbtn);
        editText = (EditText) findViewById(R.id.fedit);
        date = (TextView) findViewById(R.id.fdate);
        name = (TextView) findViewById(R.id.fname);
        id = (TextView) findViewById(R.id.fid);

        new DownloadImage().execute("https://graph.facebook.com/"+ user_id + "/picture?type=small");
        id.setText(user_id);
        name.setText(user_name);
        date.setText(cur_date);


        content_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content = editText.getText().toString();
                new JSONTask().execute("http://socrip4.kaist.ac.kr:3880/tab3up");

            }
        });
    }


    private String getTime(){
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");
        long mNow = System.currentTimeMillis();
        Date mDate = new Date(mNow);
        return mFormat.format(mDate);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                try {
                    // 선택한 이미지에서 비트맵 생성
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    cont_bitmap = BitmapFactory.decodeStream(in);
                    in.close();
                    content_img.setImageBitmap(cont_bitmap);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    cont_bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                    byte[] b = baos.toByteArray();
                    enc_content =Base64.encodeToString(b,Base64.DEFAULT);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void SetProfileImage (Bitmap bm){
        profile_img.setImageBitmap(bm);
    }

    public class JSONTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("user_id", user_id);
                jsonObject.accumulate("user_img", enc_profile);
                jsonObject.accumulate("name", user_name);
                jsonObject.accumulate("date",cur_date);
                jsonObject.accumulate("text",content);
                jsonObject.accumulate("img",enc_content);

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
                    URL url = new URL(urls[0]);
                    Log.e("url",urls[0]);
                    con = (HttpURLConnection) url.openConnection();


                    con.setRequestMethod("POST");//POST방식으로 보냄
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
                    con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                    con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    con.connect();

                    OutputStream outStream = con.getOutputStream();

                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    writer.write(jsonObject.toString());
                    writer.flush();
                    writer.close();

                    InputStream stream = con.getInputStream();
                    Log.e("===5=====",jsonObject.toString());

                    reader = new BufferedReader(new InputStreamReader(stream));
                    Log.e("====6====",jsonObject.toString());
                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }
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
            Toast.makeText(getApplicationContext(),"Upload Complete!",Toast.LENGTH_SHORT).show();
        }
    }


    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Bitmap doInBackground(String... URL) {

            String imageURL = URL[0];

            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // Set the bitmap into ImageView
            profile_bitmap = result;
            SetProfileImage(profile_bitmap);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            profile_bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
            byte[] b = baos.toByteArray();
            enc_profile =Base64.encodeToString(b,Base64.DEFAULT);
        }
    }

}
