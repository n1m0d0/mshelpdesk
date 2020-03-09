package minsalud.com.helpdesk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Forms extends AppCompatActivity {
    LinearLayout llContainer;
    int id;
    int profileId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forms);

        llContainer = findViewById(R.id.llContainer);

        Bundle data = getIntent().getExtras();
        id = data.getInt("id");
        profileId = data.getInt("profileId");

        String url = getString(R.string.url) + "forms.php";

        try {
            JSONObject petition = new JSONObject();
            JSONObject assignments = new JSONObject();
            assignments.put("profileId", profileId);
            petition.put("assignments", assignments);
            Log.w("petition", petition.toString());
            connectionServer(petition, url);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //connectionServer();
    }

    private void connectionServer(JSONObject petition, String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, petition, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.w("response", response.toString());
                try {
                    JSONObject answer = response.getJSONObject("answer");
                    if (answer.getInt("code") == 200) {
                        JSONArray forms = response.getJSONArray("forms");
                        for (int i = 0; i < forms.length(); i++) {
                            JSONObject data = forms.getJSONObject(i);
                            createComponent(data.getInt("id"), data.getString("name"), llContainer);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w("error", error.toString());
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void createComponent(final int formId, String name, LinearLayout llContainer) {
        LinearLayout llBody = new LinearLayout(Forms.this);
        LinearLayout.LayoutParams llBodyParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        llBodyParams.setMargins(0, 5, 0, 5);
        llBody.setWeightSum(10);
        llBody.setBackgroundResource(R.drawable.card_login);
        llBody.setLayoutParams(llBodyParams);
        llBody.setOrientation(LinearLayout.HORIZONTAL);
        llBody.setGravity(Gravity.CENTER);
        llContainer.addView(llBody);

        ImageView ivForm = new ImageView(Forms.this);
        LinearLayout.LayoutParams paramsIvForm = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150, 8);
        paramsIvForm.setMargins(0, 10, 0, 10);
        ivForm.setLayoutParams(paramsIvForm);
        ivForm.setImageResource(R.drawable.ic_assignment_black_24dp);
        llBody.addView(ivForm);

        TextView tvForm = new TextView(Forms.this);
        LinearLayout.LayoutParams paramsTvForm = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 2);
        tvForm.setLayoutParams(paramsTvForm);
        tvForm.setText(name);
        llBody.addView(tvForm);

        llBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ir = new Intent(Forms.this, Registered.class);
                ir.putExtra("id", id);
                ir.putExtra("profileId", profileId);
                ir.putExtra("formId", formId);
                startActivity(ir);
                finish();
            }
        });
    }
}
