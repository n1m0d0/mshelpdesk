package minsalud.com.helpdesk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {
    EditText etUser, etPassword;
    Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUser = findViewById(R.id.etUser);
        etPassword = findViewById(R.id.etPassword);
        btnStart = findViewById(R.id.btnStart);

        final String url = getString(R.string.url) + "login.php";

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etUser.getText().toString().trim().equals("") || etPassword.getText().toString().trim().equals("")) {
                    Toast.makeText(Login.this, getString(R.string.alertLogin), Toast.LENGTH_SHORT).show();
                } else {
                    MyUtilities myUtilities = new MyUtilities(Login.this);
                    String password = myUtilities.encryptUser(etUser.getText().toString().trim());
                    password = myUtilities.encryptPassword(password + etPassword.getText().toString().trim());
                    Log.w("password", password);
                    try {
                        JSONObject petition = new JSONObject();
                        JSONObject authentication = new JSONObject();
                        authentication.put("password", password);
                        petition.put("authentication", authentication);
                        Log.w("petition", petition.toString());
                        connectionServer(petition, url);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

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
                        JSONArray user = response.getJSONArray("user");
                        JSONObject data = user.getJSONObject(0);
                        Intent ir = new Intent(Login.this, Forms.class);
                        ir.putExtra("id", data.getInt("id"));
                        ir.putExtra("profileId", data.getInt("profileId"));
                        startActivity(ir);
                        finish();
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
}
