package com.example.user.test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment3 extends Fragment {
    private ListView lv;
    private Button bt;
    private ImageView iv;
    private Bitmap bm;
    ArrayList<HashMap<String, String>> feedList= new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment3, container, false);
        lv = (ListView) view.findViewById(R.id.ListView3);
        bt = (Button) view.findViewById(R.id.newfeed);

        /*
        HashMap<String, String> contact = new HashMap<>();
        contact.put("name","hoe");
        contact.put("id","010");
        contact.put("date","20190107");
        contact.put("photo","Nophoto");
        contact.put("pppp","Nophoto");
        feedList.add(contact);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ListAdapter adapter = new ExtendedSimpleAdapter(
                getActivity(), feedList,
                R.layout.list_item, new String[]{"name","id","date","photo"},
                new int[] {R.id.fname,R.id.fid, R.id.fdate,R.id.photo});
        lv.setAdapter(adapter);
        iv = (ImageView) v.findViewById(R.id.fphoto);
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        bm = BitmapFactory.decodeFile("/storage/emulated/0/Download/9S4QWSR0_400x400.jpg",bfo);
        iv.setImageBitmap(bm);*/

        /*icon : 페북프로필
           name: 페북에서 가져온 이름
           id: 페북 아이디
           date: 올린 시각
           photo: 올리는 사진의 imgpath
           text: 사용자가 치는 text //EditText
        */

        /**TEST*/
        ArrayList<Listviewitem> data = new ArrayList<>();
        Listviewitem item0 = new Listviewitem("null","heo","hbbr1","20180808","/storage/emulated/0/Download/9S4QWSR0_400x400.jpg","오늘은 스폰지밥을 봤다");
        Listviewitem item1 = new Listviewitem("null","heo","hbbr1","20180808","/storage/emulated/0/Download/9S4QWSR0_400x400.jpg","오늘도 봤다");
        data.add(item0);
        data.add(item1);
        ListviewAdapter adapter1 = new ListviewAdapter(getActivity(),R.layout.list_feed,data);
        lv.setAdapter(adapter1);



        return view;
    }
    /*
    * HashMap<String, String> contact = new HashMap<>();
        contact.put("name",name);
        contact.put("mobile",number);
        contact.put("email",email);
        contact.put("photo",photo);
        feedList.add(contact);
    * */


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
            //iv.setImageBitmap(bitmap);
        }
    }

}