package com.innovakumiai.oxxopays;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.innovakumiai.oxxopays.WebService.VolleyS;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.valueOf;

public class OxxoPay extends AppCompatActivity{
    private VolleyS volley;
    protected RequestQueue fRequestQueue;

    private TextView label, label1, label2, label3, label4, label5;
    private Button connector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oxxo_pay);

        volley = VolleyS.getInstance(getBaseContext());
        fRequestQueue = volley.getRequestQueue();

        label =  findViewById(R.id.label);
        label1 = findViewById(R.id.label1);
        label2 = findViewById(R.id.label2);
        label3 = findViewById(R.id.label3);
        label4 = findViewById(R.id.label4);
        label5 = findViewById(R.id.label5);
        connector = findViewById(R.id.connection_button);

        connector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeRequest();
            }
        });
    }


    private void makeRequest() {
        String url = "https://kan-ek.com/webservice/oxxo.php";

        Map<String, String> api_key= new HashMap<>();
        api_key.put("api_key", "key_FCdVivZPeJp5dJzFq6B3Rg");

        Map<String, String> line_items= new HashMap<>();
        line_items.put("name", "Tacos");
        line_items.put("unit_price", "300");
        line_items.put("quantity", "1");

        Map<String, String> shipping_lines= new HashMap<>();
        shipping_lines.put("amount", "1500");
        shipping_lines.put("carrier", "No aplica");

        Map<String, String> currency= new HashMap<>();
        currency.put("currency", "MXN");

        Map<String, String> customer_info= new HashMap<>();
        customer_info.put("name", "Ulisses");
        customer_info.put("email", "ulissesmercado2012@icloud.com");
        customer_info.put("phone", "+5215567871593");

        Map<String, String> shipping_contact= new HashMap<>();
        shipping_contact.put("phone", "+5215567871593");
        shipping_contact.put("receiver", "Juan Carlos");
        shipping_contact.put("street1", "Instituto Politecnico Nacional");
        shipping_contact.put("city", "Sin Especificar");
        shipping_contact.put("state", "Mexico");
        shipping_contact.put("country", "MX");
        shipping_contact.put("postal_code", "07456");

        Map<String, String> metadata= new HashMap<>();
        metadata.put("reference", "auosfd2347yo8ren");
        metadata.put("more_info", "98y394nr92834nt2");

        Map<String, String> payment_method= new HashMap<>();
        payment_method.put("type", "oxxo_cash");

        Map<String, Map> oxxoArray= new HashMap<>();
        oxxoArray.put("principal_key", api_key);
        oxxoArray.put("line_items", line_items);
        oxxoArray.put("shipping_lines", shipping_lines);
        oxxoArray.put("currency", currency);
        oxxoArray.put("customer_info", customer_info);
        oxxoArray.put("shipping_contact", shipping_contact);
        oxxoArray.put("metadata", metadata);
        oxxoArray.put("payment_method", payment_method);


        JsonObjectRequest request = new JsonObjectRequest(url, new JSONObject(oxxoArray), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                label5.setText(jsonObject.toString());
                try {
                    label.setText(jsonObject.getString("id"));
                    label1.setText(jsonObject.getJSONArray("charges").getJSONObject(0)
                            .getJSONObject("payment_method").getString("service_name"));
                    label2.setText(jsonObject.getJSONArray("charges").getJSONObject(0)
                            .getJSONObject("payment_method").getString("reference"));

                    double amount = jsonObject.getDouble("amount")/100;
                    label3.setText(valueOf(amount));

                    double pricePerUnit = jsonObject.getJSONArray("line_items").getJSONObject(0)
                            .getDouble("unit_price")/100;
                    label4.setText("Order : quantity - "+jsonObject.getJSONArray("line_items").getJSONObject(0)
                            .getString("quantity")+" - name - "+jsonObject.getJSONArray("line_items").getJSONObject(0)
                            .getString("name")+" - unit price - $"+valueOf(pricePerUnit));



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(OxxoPay.this, volleyError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        if (request != null) {
            request.setTag(this);
            if (fRequestQueue == null)
                fRequestQueue = volley.getRequestQueue();
            request.setRetryPolicy(new DefaultRetryPolicy(
                    60000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));
            fRequestQueue.add(request);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (fRequestQueue != null) {
            fRequestQueue.cancelAll(this);
        }
    }





}
