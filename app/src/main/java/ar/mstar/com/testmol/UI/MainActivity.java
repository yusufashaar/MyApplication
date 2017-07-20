package ar.mstar.com.testmol.UI;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.molpay.molpayxdk.MOLPayActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ar.mstar.com.testmol.Model.DataModel;
import ar.mstar.com.testmol.Adapter.DrawerItemCustomAdapter;
import ar.mstar.com.testmol.R;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 1;

    Button btn_method, btn_topup;
    ImageView img_;
    DrawerLayout mDrawerLayout;
    String mTitle="";
    String sTitle="";
    String userToken;
    EditText ed_nominal;
    HashMap<String, Object> paymentDetails;
    String methodPayment;
    String mCount = "IDR";
    String mCountId ="ID";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawerlayout_activity_main);
        btn_method = (Button)findViewById(R.id.btn_method);
        btn_topup = (Button) findViewById(R.id.bt_topup);
        img_ = (ImageView) findViewById(R.id.Img_method);
        ed_nominal = (EditText) findViewById(R.id.ed_nominal);

        userToken = "e8453cc8a5f569a3b9d547222f6989e0";

        verificationMOL();


        //Todo disini tambah menunya
        ListView mDrawerList = (ListView) findViewById(R.id.nav_view);

        if(mCount == "IDR" && mCountId =="ID"){
            final DataModel[] drawerItem = new DataModel[3];

            drawerItem[0] = new DataModel(R.drawable.icons_happy, "affin happy");
            drawerItem[1] = new DataModel(R.drawable.icons_happy, "affin happy");
            drawerItem[2] = new DataModel(R.drawable.icons_happy, "affin happy");
            DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this, R.layout.drawer_list_item , drawerItem);
            mDrawerList.setAdapter(adapter);

        }else if (mCount =="MYR" && mCountId == "MY"){
            final DataModel[] drawerItem = new DataModel[4];
            drawerItem[0] = new DataModel(R.drawable.affinbank, "affin ic_bank");
            drawerItem[1] = new DataModel(R.drawable.cimbclick, "cimb click");
            drawerItem[2] = new DataModel(R.drawable.hongleong, "hong leong");
            drawerItem[3] = new DataModel(R.drawable.fpx, "fpx");
            DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this, R.layout.drawer_list_item , drawerItem);
            mDrawerList.setAdapter(adapter);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerLockMode(Gravity.RIGHT);




        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String[] rivers = getResources().getStringArray(R.array.navigation_drawer_items_array);
                String[] methodPayments = getResources().getStringArray(R.array.methodpayment);

                mTitle = rivers[position];
                methodPayment = methodPayments[position];

                if(mCount =="IDR"&& mCountId =="ID"){
                    int[] imgArray = new int[3];
                    imgArray[0]=R.drawable.affinbank;
                    imgArray[1]=R.drawable.affinbank;
                    imgArray[2]=R.drawable.affinbank;
                    btn_method.setText(mTitle);
                    img_.setImageResource(imgArray[position]);
                    mDrawerLayout.closeDrawer(Gravity.END);
                }else if( mCount=="MYR" && mCountId =="MY"){
                    int[] imgArray = new int[4];
                    imgArray[0]=R.drawable.affinbank;
                    imgArray[1]=R.drawable.cimbclick;
                    imgArray[2]=R.drawable.hongleong;
                    imgArray[3]=R.drawable.fpx;
                    btn_method.setText(mTitle);
                    img_.setImageResource(imgArray[position]);
                    mDrawerLayout.closeDrawer(Gravity.END);
                }

                //RiverFragment rFragment = new RiverFragment();
                Bundle data = new Bundle();
                data.putInt("position", position);
                FragmentManager fragmentManager  = getFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.commit();



            }
        });

        btn_method.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    mDrawerLayout.closeDrawer(Gravity.RIGHT);

                } else {
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                }
            }
        });

        btn_topup.setOnClickListener(new  View.OnClickListener(){
            @Override
            public void onClick(View v) {
                paymentDetails.put(MOLPayActivity.mp_amount, ed_nominal.getText().toString());
                getDataUser();
            }
        });


    }

    private  void verificationMOL(){
        paymentDetails = new HashMap<>();
        paymentDetails.put(MOLPayActivity.mp_username, "api_kynga");
        paymentDetails.put(MOLPayActivity.mp_password, "api_kyNG154a@");
        paymentDetails.put(MOLPayActivity.mp_merchant_ID, "kynga_Dev");
        paymentDetails.put(MOLPayActivity.mp_app_name, "mStar");
        paymentDetails.put(MOLPayActivity.mp_verification_key, "16b06b1ba748579aaa2a641e4ba2aef0");
    }

    private  void getDataUser ()
    {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        String url = "http://paybill.mstar.co.id/payment/user";
        StringRequest MyStringReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try {
                    setPaymentDetails(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Token", userToken);
                return header;
            }
        };
        MyRequestQueue.add(MyStringReq);
    }

    private void setPaymentDetails(String response) throws JSONException {
        Log.d("aaaa", response);

        JSONObject o = new JSONObject(response);
        JSONObject r = o.getJSONObject("Result");
        paymentDetails.put(MOLPayActivity.mp_order_ID, r.getString("order_id"));
       //   paymentDetails.put(MOLPayActivity.mp_currency, r.getString("mp_currency"));
       //   paymentDetails.put(MOLPayActivity.mp_country, r.getString("mp_country"));
        paymentDetails.put(MOLPayActivity.mp_currency, "MYR");
        paymentDetails.put(MOLPayActivity.mp_country, "MY");
        paymentDetails.put(MOLPayActivity.mp_currency, mCount);
        paymentDetails.put(MOLPayActivity.mp_country, "MY");

        // Optional String.
        paymentDetails.put(MOLPayActivity.mp_bill_description, r.getString("mp_bill_description"));
        paymentDetails.put(MOLPayActivity.mp_bill_name, r.getString("fullname"));
        paymentDetails.put(MOLPayActivity.mp_bill_email,  r.getString("email"));
        paymentDetails.put(MOLPayActivity.mp_bill_mobile, r.getString("phone_number"));
        //  paymentDetails.put(MOLPayActivity.mp_channel, "fpx");
        paymentDetails.put(MOLPayActivity.mp_channel, methodPayment);
        paymentDetails.put(MOLPayActivity.mp_express_mode, true);

        callMolPayment();

    }

    public void callMolPayment(){
        Intent intent = new Intent(MainActivity.this, MOLPayActivity.class);
        intent.putExtra(MOLPayActivity.MOLPayPaymentDetails, paymentDetails);
        startActivityForResult(intent,REQUEST_CODE);

    }



}
