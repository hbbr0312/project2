package com.example.user.test;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DisplayPhoto extends AppCompatActivity {

    private Context mContext = null;
    private final int imgWidth = 500;
    private final int imgHeight = 500;
    private Button upl;
    private Button dwl;
    private String encodedImage;
    private String path;
    private Bitmap bm;
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_photo);
        mContext = this;

        /** 전송메시지 */
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        String imgPath = extras.getString("imgpath");
        Log.e("전송메시지","imgpath is " +imgPath);

        /**상단바*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String rawImgname = getIntent().getStringExtra("imgpath");
        int index = rawImgname.lastIndexOf("/");
        rawImgname = rawImgname.substring(index+1);
        Log.i("imgname",rawImgname);
        setTitle(rawImgname); //상단바 title변경

        /** 완성된 이미지 보여주기  */
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        bfo.inSampleSize = 2;
        iv = (ImageView)findViewById(R.id.imageView);
        Bitmap bm0 = BitmapFactory.decodeFile(imgPath, bfo);
        Bitmap resized = Bitmap.createScaledBitmap(bm0, imgWidth, imgHeight, true);
        iv.setImageBitmap(bm);//resized);

        /** 리스트로 가기 버튼 */
        Button btn = (Button)findViewById(R.id.btn_back);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            { onBackPressed(); }
        });


        /**Encoding*/
        bm = BitmapFactory.decodeFile(imgPath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,40,baos);
        byte[] b = baos.toByteArray();
        encodedImage =Base64.encodeToString(b,Base64.DEFAULT);
        String test= "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\n";
        byte[] bytedata = Base64.decode(test,0);
        ByteArrayInputStream inStream = new ByteArrayInputStream(bytedata);
        Bitmap bitmap = BitmapFactory.decodeStream(inStream) ;
        iv.setImageBitmap(bitmap);
        /*
        byte[] bytedata = Base64.decode(encodedImage,0);
        ByteArrayInputStream inStream = new ByteArrayInputStream(bytedata);
        Bitmap bitmap = BitmapFactory.decodeStream(inStream) ;
        iv.setImageBitmap(bitmap);*/


        /**UPLOAD Button*/
        upl = (Button) findViewById(R.id.upload);
        upl.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                new uploadTask().execute("http://socrip4.kaist.ac.kr:3980/uploadimage/hbbr1");
                Log.e("upload","click!");
            }
        });
        /**DOWNLOAD Button*/
        dwl = (Button) findViewById(R.id.download);
        dwl.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                new downloadTask().execute("http://socrip4.kaist.ac.kr:3980/image/hbbr1");
                Log.e("download","click!");
            }
        });


    }
    /**업로드**/
    public class uploadTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {

                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                JSONObject img = new JSONObject();

                img.put("name","spongebob");
                img.put("img",encodedImage);

                Log.i("encoded",encodedImage);

                jsonArray.put(img);
                Log.e("=========",img.toString());
                jsonObject.accumulate("images", jsonArray);

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
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
                    //Log.e("========",jsonObject.toString());
                    writer.write(jsonObject.toString()+"\n");
                    writer.flush();
                    writer.close();//버퍼를 받아줌

                    //서버로 부터 데이터를 받음
                    InputStream stream = con.getInputStream();


                    /*reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    } ////여기까지 안가고 에러 뜸
                    Log.e("======3======",buffer.toString());*/
                    return "ok";//buffer.toString();//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임

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
    }
    /**다운로드**/
    public class downloadTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                //JSONObject jsonObject = new JSONObject();

                HttpURLConnection con = null;
                BufferedReader reader = null;
                Log.e("enter","download");
                try{
                    URL url = new URL(urls[0]);
                    //연결을 함

                    con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("GET");//GET방식으로 받음
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
                    con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                    //con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    con.connect();

                    //서버로 보내기위해서 스트림 만듬
                    //OutputStream outStream = con.getOutputStream();

                    //버퍼를 생성하고 넣음
                    //BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    //Log.e("========",jsonObject.toString());
                    //writer.write("");
                    //writer.flush();
                    //writer.close();//버퍼를 받아줌

                    //서버로 부터 데이터를 받음
                    InputStream stream = con.getInputStream();
                    Log.e("enter","input stream");

                    reader = new BufferedReader(new InputStreamReader(stream));
                    reader.read();

                    StringBuffer buffer = new StringBuffer();
                    String line;
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    } ////여기까지 안가고 에러 뜸
                    Log.e("======3======",buffer.toString());
                    reader.close();
                    Log.e("touch","close");
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
            if(result==null) return;
            Log.e("result",""+result);
            Log.e("onPostExecute","i will decode");
            byte[] bytedata = Base64.decode(result,0);
            ByteArrayInputStream inStream = new ByteArrayInputStream(bytedata);
            Bitmap bitmap = BitmapFactory.decodeStream(inStream) ;
            iv.setImageBitmap(bitmap);
        }
    }
}
