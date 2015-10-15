package ir.weblogestaan.sardabir;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ir.weblogestaan.sardabir.Classes.OnSwipeTouchListener;
import ir.weblogestaan.sardabir.Classes.PostParams;
import ir.weblogestaan.sardabir.Classes.Session;
import ir.weblogestaan.sardabir.Classes.Subject;
import ir.weblogestaan.sardabir.Classes.User;
import ir.weblogestaan.sardabir.Network.HttpService;

public class BaseActivity extends Activity {

    private static final String TAG = "BaseActivity";
    public Session session;
    protected static boolean isDrawerOpen = false;
    protected static boolean isGuest = false;
    protected static final String STATUSBAR_COLOR = "#2371AF";//"#C62B33";

    private ImageView current_user_logo;
    protected ImageButton imgBtnDrawer, btnImgNotifications, btnImgSubjects, btnLogout, btnActivityFriends, btnImgSuggest, btnImgFeed, btnImgHot;
    protected ImageButton headerImgBtnFirst;
    protected TextView txtNofitsCount;
    protected View navigation_pan;
    public boolean prependData;

    protected void setStatusbarColor()
    {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(STATUSBAR_COLOR));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void initNavigation(final int layoutResID)
    {
        if (layoutResID == R.layout.activity_main ||
                layoutResID == R.layout.activity_term ||
                layoutResID == R.layout.activity_subjects ||
                layoutResID == R.layout.activity_hot ||
                layoutResID == R.layout.activity_friends ||
                layoutResID == R.layout.activity_users ||
                layoutResID == R.layout.activity_guest) {
            current_user_logo = (ImageView) findViewById(R.id.imgProfileImage);
            btnImgFeed = (ImageButton) findViewById(R.id.btnImgFeed);
            btnImgFeed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_on_click));
                    CloseDrawer();
                    if (sayGuest("") && layoutResID != R.layout.activity_main)
                        gotoMainActivity(false);
                }
            });

            if (isGuest || layoutResID == R.layout.activity_main)
                btnImgFeed.setVisibility(View.GONE);

            btnImgHot = (ImageButton) findViewById(R.id.btnImgHot);
            btnImgHot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_on_click));
                    CloseDrawer();
                    if (sayGuest("برای دیدن داغ ترین مطالب باید وارد برنامه شوید") && layoutResID != R.layout.activity_hot)
                        gotoHotActivity(false);
                }
            });
            btnImgNotifications = (ImageButton) findViewById(R.id.btnImgNotifications);
            btnImgNotifications.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_on_click));
                    CloseDrawer();
                    if (sayGuest("برای دیدن اعلان ها باید وارد برنامه شوید") && layoutResID != R.layout.activity_notifications)
                        gotoNotificationPage(false);
                }
            });
            btnImgSubjects = (ImageButton) findViewById(R.id.btnImgSubjects);
            btnImgSubjects.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_on_click));
                    CloseDrawer();
                    if (sayGuest("برای دیدن و انتخاب موضوعات باید وارد برنامه شوید") && layoutResID != R.layout.activity_subjects)
                        gotoSubjectActivity(false,false);
                }
            });
            txtNofitsCount = (TextView) findViewById(R.id.txtNotifCount);
            btnLogout = (ImageButton) findViewById(R.id.btnImgLogout);

            if (isGuest) {
                btnLogout.setVisibility(View.GONE);
            } else {
                btnLogout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CloseDrawer();
                        v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_on_click));
                        new AlertDialog.Builder(v.getContext())
                                .setIcon(android.R.drawable.ic_delete)
                                .setTitle("مطمئن هستید؟")
                                .setMessage("آیا تمایل به خروج از حساب کاربری خود دارید؟")
                                .setPositiveButton("بله", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        logOutUser();
                                    }

                                })
                                .setNegativeButton("انصراف", null)
                                .show();
                    }
                });
            }

            btnActivityFriends = (ImageButton) findViewById(R.id.btnImgFriends);
            btnActivityFriends.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_on_click));
                    CloseDrawer();
                    if (sayGuest("برای دیدن مطالب دوستان باید وارد برنامه شوید") && layoutResID != R.layout.activity_friends)
                        gotoFriendActivity(false);
                }
            });
            btnImgSuggest = (ImageButton) findViewById(R.id.btnImgSuggest);
            btnImgSuggest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_on_click));
                    CloseDrawer();
                    if (sayGuest("برای پیشنهاد سایت باید وارد برنامه شوید"))
                        gotoSuggestPage(false);
                }
            });
            navigation_pan = findViewById(R.id.navigation_pan);
            if (isDrawerOpen)
                navigation_pan.setVisibility(View.VISIBLE);
            else
                navigation_pan.setVisibility(View.GONE);
            imgBtnDrawer = (ImageButton) findViewById(R.id.imgBtnDrawer);
            imgBtnDrawer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_on_click));
                    if (isDrawerOpen) {
                        CloseDrawer();
                    } else {
                        OpenDrawer();
                    }
                }
            });
        }
        if (layoutResID == R.layout.activity_main ||
                layoutResID == R.layout.activity_hot ||
                layoutResID == R.layout.activity_term ||
                layoutResID == R.layout.activity_subjects ||
                layoutResID == R.layout.activity_friends ||
                layoutResID == R.layout.activity_users ||
                layoutResID == R.layout.activity_guest) {
            headerImgBtnFirst = (ImageButton) findViewById(R.id.headerImgBtnFirst);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        session = new Session(getApplicationContext());
        isGuest = layoutResID == R.layout.activity_guest;
        initNavigation(layoutResID);
    }

    protected void OpenDrawer()
    {
        if (navigation_pan == null)
            return;
        isDrawerOpen = true;
        navigation_pan.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_fade_in));
        navigation_pan.setVisibility(View.VISIBLE);
    }

    protected void CloseDrawer()
    {
        if (navigation_pan == null)
            return;
        isDrawerOpen = false;
        navigation_pan.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_fade_out));
        navigation_pan.setVisibility(View.GONE);
    }

    public interface LoginResult {
        public void onError();
        public void onSuccess();
    }

    public interface HeaderInitializer {
        public void init();
    }

    public void initHeader(final HeaderInitializer headerInitializer)
    {
        if (headerImgBtnFirst != null) {
            headerImgBtnFirst.setVisibility(View.VISIBLE);
            headerImgBtnFirst.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    headerInitializer.init();
                }
            });
        }
    }

    protected void setNotifsCount(String count)
    {
        if (txtNofitsCount == null) {
            return;
        }
        txtNofitsCount.setText(count);

        if (count.equals("0"))
            txtNofitsCount.setVisibility(View.GONE);
        else
            txtNofitsCount.setVisibility(View.VISIBLE);
    }

    private void setUserProfile()
    {
        if (current_user_logo != null) {
            String url =  (PostParams.current_user != null) ? PostParams.current_user.image : "" ;
            if (url != "" && url != null) {
                try {
                    Picasso.with(this).load(url).placeholder(R.mipmap.user_placeholder).into(current_user_logo);
                } catch (Exception e) {
                    current_user_logo.setImageResource(R.mipmap.user_placeholder);
                }
            }

            current_user_logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (sayGuest("")) {
                        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                        intent.putExtra("user", PostParams.current_user);
                        startActivity(intent);
                    }
                }
            });
        } else {
            Log.e("current_log","is null");
        }
    }

    protected void afterLogin()
    {
        setUserProfile();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        setUserProfile();
    }

    protected void gotoGuestPage(boolean finish)
    {
        Intent intent = new Intent(getApplicationContext(), GuestActivity.class);
        startActivity(intent);
        if (finish)
            finish();
    }

    protected void gotoResetPage(boolean finish)
    {
        Intent intent = new Intent(getApplicationContext(), RessetpassActivity.class);
        startActivity(intent);
        if (finish)
            finish();
    }

    protected void gotoNotificationPage(boolean finish)
    {
        Intent intent = new Intent(getApplicationContext(), NotificationsActivity.class);
        startActivity(intent);
        if (finish)
            finish();
    }


    protected void gotoSuggestPage(boolean finish)
    {
        Intent intent = new Intent(getApplicationContext(), SuggestActivity.class);
        startActivity(intent);
        if (finish)
            finish();
    }

    protected void logOutUser()
    {
        User.Logout();
        session.StoreUserPass("", "");
        session.StoreMod("");
        goToLoginPage(true, false);
    }



    protected void StoreUserPass(String username,String pswd)
    {
        session.StoreUserPass(username, pswd);
    }

    protected boolean hasUserPass()
    {
        return session.hasUserPass();
    }

    protected void goToLoginPage(boolean finish, boolean fromRegister)
    {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.putExtra("fromRegister", fromRegister);
        startActivity(intent);
        if (finish)
            finish();
    }
    protected void goToRegisterPage(boolean finish)
    {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
        if (finish)
            finish();
    }

    protected String EncodeUserPass()
    {
        return session.EncodeUserPass();
    }

    public void CheckLogin(LoginResult loginResult) {
        if (!hasUserPass()) {
            // goto Login Activity
            Log.e("SplashActivity","no user and pass");
            goToLoginPage(true, false);
        } else {
            String mod = EncodeUserPass();
            session.StoreMod(mod);
            String url = PostParams.getBaseUrl()+"/REST/action/user_login?ok=BEZAR17";
            HttpService p = new HttpService(getApplicationContext(),url, false);
            p.ls = session.BasePostParams();
            LoginTask lt = new LoginTask();
            lt.loginResult = loginResult;
            lt.execute(p);
        }
    }

    public class LoginTask extends AsyncTask<HttpService,Long,JSONObject>
    {
        boolean getToAdd = false;
        LoginResult loginResult = null;
        protected void onPreExecute() {

        }

        @Override
        protected JSONObject doInBackground(HttpService... params) {
            getToAdd = params[0].toAdd;
            return params[0].postDataObject();
        }

        @Override
        protected void onPostExecute(JSONObject jsonResp)
        {
            try {
                ParseResult(jsonResp,loginResult);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    protected void gotoHotActivity(boolean finish)
    {
        Intent intent = new Intent(getApplicationContext(), HotActivity.class);
        startActivity(intent);
        if (finish)
            finish();
    }

    protected void gotoMainActivity(boolean finish)
    {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        if (finish)
            finish();
    }

    protected void gotoSubjectActivity(boolean finish, boolean isFromLogin)
    {
        Intent intent = new Intent(getApplicationContext(), SubjectsActivity.class);
        intent.putExtra("isFromLogin", isFromLogin);
        startActivity(intent);
        if (finish)
            finish();
    }

    protected void gotoFriendActivity(boolean finish)
    {
        Intent intent = new Intent(getApplicationContext(), FriendsActivity.class);
        startActivity(intent);
        if (finish)
            finish();
    }

    protected void LoginFailed()
    {
        goToLoginPage(true, false);
    }

    protected void LoginSuccess(User user)
    {
        User.Login(user);
        if (user.subjects_count.equals("0"))
        {
            // go to subject selection activity
            gotoSubjectActivity(true, true);
        } else {
            // go to main activity
            gotoMainActivity(true);
        }
    }

    protected boolean ParseResult(JSONObject jsonResp, LoginResult loginResult) throws JSONException
    {
        if(jsonResp == null) {
            if (loginResult != null)
                loginResult.onError();
            else
                LoginFailed();
            return false;
        }

        String resultType = jsonResp.getString("result_type");
        if (resultType.equals("boolean")) {
            String result = jsonResp.getString("result");
            if (result.equals("false") || Boolean.parseBoolean(result) == false) {
                if (loginResult != null)
                    loginResult.onError();
                else
                    LoginFailed();
            }
            return false;
        } else if (resultType.equals("array")) {
            JSONArray result = jsonResp.getJSONArray("result");
            try {
                User loggedInUser = User.parseUser(result.getJSONObject(0));
                LoginSuccess(loggedInUser);
            } catch (IllegalAccessException e) {
                LoginFailed();
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    protected boolean sayGuest(String msg)
    {
        if(isGuest) {
            if (msg.equals(""))
                msg = "برای استفاده از این امکان باید وارد برنامه شوید";
            Toast t = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
            t.setGravity(Gravity.CENTER,0,0);
            t.show();
        }
        return !isGuest;
    }
}

