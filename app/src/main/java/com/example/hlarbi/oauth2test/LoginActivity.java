package com.example.hlarbi.oauth2test;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hlarbi.oauth2test.api.APIClient;
import com.example.hlarbi.oauth2test.api.GetResquest;
import com.example.hlarbi.oauth2test.api.ServiceGenerator;
import com.example.hlarbi.oauth2test.api.objects.Oauth.AccessToken;
import com.example.hlarbi.oauth2test.api.objects.FitBitApi.Activities;


import java.util.HashMap;
import java.util.Map;

import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    Button loginButton;



    // TODO Replace this with your own data
    public static final String API_LOGIN_URL = "https://www.fitbit.com/oauth2/authorize?response_type=code";
    public static final String client_id = "22CT2D";
    public static final String client_secret = "1a26ad3ac2d4fb2a8cfa7410bd5847bb";
    public static final String API_OAUTH_REDIRECT = "futurestudio://callback";
    public static final String content_type = "application/x-www-form-urlencoded";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = (Button) findViewById(R.id.loginButton);

        //Au clic, l'app ouvre le navigateur (browser)
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(API_LOGIN_URL + "&client_id=" + client_id + "&redirect_uri=" + API_OAUTH_REDIRECT + "&scope=activity&expired_in=604800"));
                // This flag is set to prevent the browser with the login form from showing in the history stack
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                startActivity(intent);

                //On ferme la le navi et maintenant on va passer à la méthode OnResume
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        final TextView textView = findViewById(R.id.textView);
        String base2 = Credentials.basic(client_id, client_secret).substring(6);


        //Collect de données grace au lien uri
        Uri uri = getIntent().getData();
        //Si l'uri n'est pas nul on va attraper le code redonné par l'uri
        if(uri != null && uri.toString().startsWith(API_OAUTH_REDIRECT)) {
            String code = uri.getQueryParameter("code");
            if(code != null) {
                // TODO We can probably do something with this code! Show the user that we are logging them in

                final SharedPreferences prefs = this.getSharedPreferences(
                        BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
                //Nous appelons l'intercepteur (ServiceGenerator) qui va communiquer avec le serveur
                APIClient client = ServiceGenerator.createService(APIClient.class);
                //Pour avoir le token d'acces nous devons envoyer certains parametre définis dans la document pas FitBit
                Call<AccessToken> call = client.getNewAccessToken("authorization_code",
                        client_id,
                        API_OAUTH_REDIRECT,
                        code,
                        base2);
                //Nous demandons la réponse par la commande suivante :
                call.enqueue(new Callback<AccessToken>() {
                    @Override
                    public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                        int statusCode = response.code();
                        if (statusCode == 200) {

                            //Nous obtenons le token d'acces ainsi que d'autres paramètres
                            AccessToken token = response.body();
                            prefs.edit().putBoolean("oauth.loggedin", true).apply();
                            prefs.edit().putString("oauth.accesstoken", token.getAccessToken()).apply();
                            prefs.edit().putString("oauth.refreshtoken", token.getRefreshToken()).apply();
                            prefs.edit().putString("oauth.tokentype", token.getTokenType()).apply();
                            prefs.edit().putString("oauth.tokentype", token.getUser_ID()).apply();

                            //Dès à présent nous commencons à construire le Header qui nous permettra de demander les Datas

                            String headertoken = " " + String.valueOf(token.getTokenType()) + " " + String.valueOf(token.getAccessToken());


                            // TODO Show the user they are logged in

                            //ActivitiesCall
                            GetResquest clientg = ServiceGenerator.createService(GetResquest.class);

                            Map<String, String> map = new HashMap<>();

                            map.put("Authorization", headertoken);
                            Toast.makeText(LoginActivity.this, String.valueOf(map), Toast.LENGTH_SHORT).show();


                            Call<Activities> callg = clientg.getActivitiesData(map,token.getUser_ID());
                            callg.enqueue(new Callback<Activities>() {


                                @Override
                                public void onResponse(Call<Activities> call, Response<Activities> response) {
                                    Activities activities =response.body();

                                    textView.setText(String.valueOf(activities.getSummary().getSteps()));




                                }




                                @Override
                                public void onFailure(Call<Activities> call, Throwable t) {

                                }
                            });





                        } else {
                            // TODO Handle a missing code in the redirect URI
                        }
                    }

                    @Override
                    public void onFailure(Call<AccessToken> call, Throwable t) {

                    }

                });
            }
        }
    }
}




