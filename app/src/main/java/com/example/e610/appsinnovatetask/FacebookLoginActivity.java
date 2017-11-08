package com.example.e610.appsinnovatetask;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.e610.appsinnovatetask.Adapters.UserFriendAdapter;
import com.example.e610.appsinnovatetask.Utils.MySharedPreferences;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FacebookLoginActivity extends AppCompatActivity {

    /***   1.Create a Login screen using Facebook SDK with a "Remember Me" option
     2.Create a screen to show facebook friends *User fullname and UserPicture
     5.Image caching

     Note-> 5.Image caching::
     I did this task by using both of Glide and Picasso to load, cache and display images.
     I did it in UserFriendAdapter class and in this activity

     after a lot search i find that
     and "developer.android.com" says
     Note: For most cases, we recommend that you use the Glide library to fetch, decode, and display bitmaps in your app
     source -> https://developer.android.com/topic/performance/graphics/cache-bitmap.html
     ***/

    /****** To test "Share Photo to facebook" Task and "Display user friends' name and user friends' photo" Task
     *  you should send me one facebook username or more than one to add this/these user/users  in Tester Users
     *  to be able to test these  tasks (Share Photo to facebook" Task and "Display user friends' name and user friends' photo" Task)
     *  send these users to me on this e-mail ( mohamedm9595@gmail.com )
     *  *****/

    LoginButton loginButton;
    CallbackManager callbackManager;
    ImageView profilePicture;
    TextView userNameTxt;
    ProfileTracker profileTracker;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_login);

        MySharedPreferences.setUpMySharedPreferences(this,"AccessTokens");

        //FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        loginButton = (LoginButton) findViewById(R.id.login_button);
        userNameTxt=(TextView)findViewById(R.id.text_name) ;
        profilePicture=(ImageView)findViewById(R.id.img_profile) ;

        /*** Facebook Login ****/
        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions("user_friends","email");
        //loginButton.setPublishPermissions("publish_actions");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                /*** Facebook Login Success ****/
                Profile currentProfile=Profile.getCurrentProfile();
                if(currentProfile!=null) {
                    Picasso.with(getApplicationContext()).load(currentProfile.getProfilePictureUri(160, 160)).into(profilePicture);
                    userNameTxt.setText(currentProfile.getName());
                }

                   /*** Save Access Token to Use it in Case "Remember Me" ***/
                     //Set<String> set=AccessToken.getCurrentAccessToken().getPermissions();
                    String userID=AccessToken.getCurrentAccessToken().getUserId();
                    String accessToken=AccessToken.getCurrentAccessToken().getToken();
                    MySharedPreferences.SaveAccessToken(userID,accessToken);
                    //Toast.makeText(FacebookLoginActivity.this,"AccessToken = \n " + accessToken, Toast.LENGTH_LONG).show();

                /**** get Facebook  user friends ****/
                fetchUserFriends();
                //loginButton.clearPermissions();
                //LoginManager.getInstance().logInWithPublishPermissions(FacebookLoginActivity.this,Arrays.asList("publish_actions"));
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

/*** Display user information like name and profile picture **/
        profileTracker=new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                if(currentProfile!=null) {
                    /*** I use Picasso to Image caching  task **/
                    Picasso.with(getApplicationContext()).load(currentProfile.getProfilePictureUri(160, 160)).into(profilePicture);
                    userNameTxt.setText(currentProfile.getName());
                    //fetchUserFriends();
                }else {
                    /*** when User make  log out ***/
                    // this line will make ImageView Empty
                    Picasso.with(getApplicationContext()).load("asd").into(profilePicture);
                    userNameTxt.setText("");
                    if(recyclerView!=null)
                        recyclerView.setAdapter(new UserFriendAdapter());
                }

            }
        };
        profileTracker.startTracking();

    }


    /**** get Facebook  user friends ****/
    private void fetchUserFriends(){
        if(AccessToken.getCurrentAccessToken()!=null)
              /* make the API call */
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/me/taggable_friends",
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
            /* handle the result */
                            /** Parsing Json **/
                            try {
                                ArrayList<String[]> usersList=new ArrayList<String[]>();
                                JSONObject jsonObject = response.getJSONObject();
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for(int i=0;i<jsonArray.length();i++){
                                    JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);
                                    String [] userData=new String[2];
                                    userData[0]=jsonObject1.getString("name");
                                    JSONObject pic = jsonObject1.getJSONObject("picture");
                                    JSONObject urlData = pic.getJSONObject("data");
                                    userData[1]=urlData.getString("url");
                                    usersList.add(userData);
                                }
                                /**  Display user friends' photo and user friends' name
                                 * by using RecyclerView and UserFriendAdapter
                                 * note-> in UserFriendAdapter I use a Glide(library)that load ,cache and display images **/
                                StaggeredGridLayoutManager sglm =
                                        new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                                recyclerView=(RecyclerView)findViewById(R.id.recycler);
                                recyclerView.setLayoutManager(sglm);

                                UserFriendAdapter adapter=new UserFriendAdapter(usersList,FacebookLoginActivity.this);
                                recyclerView.setAdapter(adapter);

                            }catch (Exception e){}
                        }
                    }
            ).executeAsync();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** to forward facebook login result to its callbacks**/
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*** Display user information like name and profile picture **/
        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
            /*** I use Picasso to Image caching  task **/
            Picasso.with(getApplicationContext()).load(profile.getProfilePictureUri(160, 160)).into(profilePicture);
            userNameTxt.setText(profile.getName());
            /*** get  facebook user friends ***/
            fetchUserFriends();

            /*** get  Access Token to use it in case "Remember Me" **/
            String userID = AccessToken.getCurrentAccessToken().getUserId();
            String accesstoken = MySharedPreferences.getAccessToken(userID);
            //Toast.makeText(FacebookLoginActivity.this, "AccessToken = \n " + accesstoken, Toast.LENGTH_LONG).show();

        }
    }

}
