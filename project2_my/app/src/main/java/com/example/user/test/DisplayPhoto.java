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
    private String encodedImage;
    private String path;
    private Bitmap bm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_photo);
        mContext = this;

        /** 전송메시지 */
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        String imgPath = extras.getString("filename");

        /**상단바*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String rawImgname = getIntent().getStringExtra("filename");
        int index = rawImgname.lastIndexOf("/");
        rawImgname = rawImgname.substring(index+1);
        Log.i("imgname",rawImgname);
        setTitle(rawImgname); //상단바 title변경

        /** 완성된 이미지 보여주기  */
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        bfo.inSampleSize = 2;
        ImageView iv = (ImageView)findViewById(R.id.imageView);
        Bitmap bm0 = BitmapFactory.decodeFile(imgPath, bfo);
        Bitmap resized = Bitmap.createScaledBitmap(bm0, imgWidth, imgHeight, true);
        iv.setImageBitmap(resized);

        /** 리스트로 가기 버튼 */
        Button btn = (Button)findViewById(R.id.btn_back);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            { onBackPressed(); }
        });

        /**upload button**/
        //path = Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera/IMG_20190106_061606.jpg";//"/Download/spongebob";
        //"/DCIM/Camera/spongebob.jpg";
        //File file = new File(path);

        bm = BitmapFactory.decodeFile(imgPath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,40,baos);
        byte[] b = baos.toByteArray();
        encodedImage =Base64.encodeToString(b,Base64.DEFAULT);

        byte[] bytedata = Base64.decode(encodedImage,0);
        ByteArrayInputStream inStream = new ByteArrayInputStream(bytedata);
        Bitmap bitmap = BitmapFactory.decodeStream(inStream) ;
        //imageView.setImageBitmap(bitmap);



        upl = (Button) findViewById(R.id.upload);
        upl.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                // TODO : click event
                //new JSONTask().execute("http://socrip4.kaist.ac.kr:3980/");
                new JSONTask().execute("http://socrip4.kaist.ac.kr:3980/uploadimage/hbbr1");
                Log.e("======================","buttonclickc");
                /*
                String url = "http://socrip4.kaist.ac.kr:3880/upload11";

                NetworkTask networkTask = new NetworkTask(url, encodedImage);
                networkTask.execute();
                */

            }
        });


    }
    public class JSONTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                JSONObject img = new JSONObject();

                img.accumulate("name","spongebob");
                img.accumulate("img",encodedImage);
                jsonObject.accumulate("images", jsonArray);

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
                    //URL url = new URL("http://192.168.25.16:3000/users");
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
                    Log.e("========",jsonObject.toString());
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
            */
            //Log.e("fsd",result);
            if(result==null) Log.e("result","null");

            byte[] bytedata = Base64.decode(result,0);
            ByteArrayInputStream inStream = new ByteArrayInputStream(bytedata);
            Bitmap bitmap = BitmapFactory.decodeStream(inStream) ;
            //imageView.setImageBitmap(bitmap);
        }
    }



}
