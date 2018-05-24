package com.paytmexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.paytm.pgsdk.Log;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    String st;
    String amt;
    String custid;
    String orderId;
    String checsum;
    public static final String MID="mMdd#ES4x2Zi1lZr ";
    public static final String INDUSTRY_TYPE_ID="Retail";
    public static final String CHANNEL_ID="WAP";
    public static final String WEBSITE="APPSTAGING";
    public static final String CALLBACK_URL="https://securegw.paytm.in/theia/paytmCallback";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.start_transaction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random r = new Random(System.currentTimeMillis());
                orderId = "ORDER" + (1 + r.nextInt(2)) * 10000
                        + r.nextInt(10000);
                String url = "https://whiteoval.000webhostapp.com/checksum.php";
                Map<String, String> params = new HashMap<String, String>();
                params.put("MID", MID);
                params.put("ORDER_ID", orderId);
                params.put("CUST_ID", custid);
                params.put("INDUSTRY_TYPE_ID", INDUSTRY_TYPE_ID);
                params.put("CHANNEL_ID", CHANNEL_ID);
                params.put("TXN_AMOUNT", amt);
                params.put("WEBSITE", WEBSITE);
                params.put("CALLBACK_URL", CALLBACK_URL);
                params.put("MOBILE_NO", "7777777777");

                JSONObject param = new JSONObject(params);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        checsum = response.optString("CHECKSUMHASH");
                        if (checsum.trim().length() != 0) {
                            onStartTransaction();
                        }
                    }

                    public void onStartTransaction() {
                        PaytmPGService Service = PaytmPGService.getStagingService();
                        Map<String, String> paramMap = new HashMap<String, String>();
                        paramMap.put("CALLBACK_URL",CALLBACK_URL);
                        paramMap.put("CHANNEL_ID",CHANNEL_ID);
                        paramMap.put("CHECKSUMHASH",checsum);
                        paramMap.put("CUST_ID",custid);
                        paramMap.put("INDUSTRY_TYPE_ID",INDUSTRY_TYPE_ID);
                        paramMap.put("MID",MID);
                        paramMap.put("ORDER_ID",orderId);
                        paramMap.put("TXN_AMOUNT",amt);
                        paramMap.put("WEBSITE",WEBSITE);




                       // paramMap.put("ORDER_ID", "TestMerchant000111007");
                        //paramMap.put("CUST_ID", "mohit.aggarwal@paytm.com");
                        //paramMap.put("INDUSTRY_TYPE_ID", "Retail");
                       // paramMap.put("CHANNEL_ID", "WAP");
                       // paramMap.put("TXN_AMOUNT", "1");
                       // paramMap.put("WEBSITE", "worldpressplg");
                        //paramMap.put("CALLBACK_URL", "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp");
                        //paramMap.put("CHECKSUMHASH", "w2QDRMgp1/BNdEnJEAPCIOmNgQvsi+BhpqijfM9KvFfRiPmGSt3Ddzw+oTaGCLneJwxFFq5mqTMwJXdQE2EzK4px2xruDqKZjHupz9yXev4=");


                        PaytmOrder Order = new PaytmOrder(paramMap);


                        Service.initialize(Order, null);

                        Service.startPaymentTransaction(MainActivity.this, true, true,
                                new PaytmPaymentTransactionCallback() {

                                    @Override
                                    public void someUIErrorOccurred(String inErrorMessage) {
                                        // Some UI Error Occurred in Payment Gateway Activity.
                                        // // This may be due to initialization of views in
                                        // Payment Gateway Activity or may be due to //
                                        // initialization of webview. // Error Message details
                                        // the error occurred.
                                    }

                                    @Override
                                    public void onTransactionResponse(Bundle inResponse) {
                                        Log.d("LOG", "Payment Transaction : " + inResponse);
                                        Toast.makeText(getApplicationContext(), "Payment Transaction response " + inResponse.toString(), Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void networkNotAvailable() {

                                    }

                                    @Override
                                    public void clientAuthenticationFailed(String inErrorMessage) {

                                    }

                                    @Override
                                    public void onErrorLoadingWebPage(int iniErrorCode,
                                                                      String inErrorMessage, String inFailingUrl) {

                                    }

                                    // had to be added: NOTE
                                    @Override
                                    public void onBackPressedCancelTransaction() {
                                        // TODO Auto-generated method stub
                                    }

                                    @Override
                                    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                                        Log.d("LOG", "Payment Transaction Failed " + inErrorMessage);
                                        Toast.makeText(getBaseContext(), "Payment Transaction Failed ", Toast.LENGTH_LONG).show();
                                    }

                                });
                    }


                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error"+error, Toast.LENGTH_SHORT).show();
                        android.util.Log.e("ERROR", String.valueOf(error));
                        error.printStackTrace();
                    }

                });
                Volley.newRequestQueue(MainActivity.this).add(jsonObjectRequest);
            }


        });
    }
}



