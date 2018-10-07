package com.example.shenhch.mineact;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class MainActivity extends AppCompatActivity {

    private EditText blNo;
    private Button btn;
    private TextView tv;
    View.OnClickListener ocl = null;
    static String logs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        blNo = (EditText)findViewById(R.id.blNo);
        System.out.println(blNo+"onCreate done.");
        tv = (TextView)findViewById(R.id.body);
        btn = (Button)findViewById(R.id.buttonQuery);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryBlNo();
            }
        });




    }


    public void queryBlNo(){
        String blNumber = blNo.getText().toString();
        String path = "https://m.coscon.com/NewEBWeb/cutoff/findCutoffNumber.do";
        String numberType = "blNo";
        new postTask().execute(numberType,blNumber, path);
        System.out.println("queryBlNo done.");
    }

    public final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    class postTask extends AsyncTask{
        @Override
        protected Object doInBackground(Object[] params) {
            String numberType = params[0].toString();
            String number = params[1].toString();
            String path = params[2].toString();


            try{
                URL url = new URL(path);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setHostnameVerifier(DO_NOT_VERIFY);
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(5000);
                String s = "numberType="+numberType+"&number="+number;
                conn.setRequestProperty("Content-Length", s.length()+"");
                conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                os.write(s.getBytes());
                if(conn.getResponseCode()==200){
                    InputStream is = conn.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String str = br.readLine();
                    logs = str;
                    System.out.println(logs);
                    logs = likeGson(logs);
                    System.out.println(str);
                    if(logs!=null){
                        tv.setText(logs);
                    }else{
                        tv.setText("请输入需要查询的提单号(✺ω✺)");
                    }
                    return str;
                }
                System.out.println("doInBackground done.");
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o){
            super.onPostExecute(o);
            String s = (String) o;
            Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
        }

        public String likeGson(String str){
            String skh = "(?<=\\[)[^\\]]+";
            Pattern pattern = Pattern.compile(skh);
            Matcher matcher = pattern.matcher(str);
            boolean is = matcher.find();
            if (is){
                String[] res = matcher.group().split("\\}");
                //System.out.println(res[0]);
                int len = res.length;
                String finalres = "";
                for(int i = 0; i<len; i++){
                    String[] eve = res[i].split("\",|l,|:,");
                    String temp = "";
                    for(String x: eve){
                        x = x.replaceAll("\"", "");

                        if(x.contains("nul")){
                            x = x+"l";
                        }
                        if(x.contains("{")){

                        }else {
                            temp += x + "\n";
                        }
                    }
                    finalres += temp + "_____________________________________________________" + "\n";
                    //System.out.println("----------------------------------------------------");
                }
                System.out.println(finalres);

                return finalres;
            }else{
                return "No Result Found!";
            }

        }




    }



}
