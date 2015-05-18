package ar.com.wolox.lucasdelatorre.training.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import ar.com.wolox.lucasdelatorre.training.R;
import ar.com.wolox.lucasdelatorre.training.User;
import ar.com.wolox.lucasdelatorre.training.Utils;
import ar.com.wolox.lucasdelatorre.training.api.RestApiAdapter;
import ar.com.wolox.lucasdelatorre.training.api.RestApiAdapterToken;
import ar.com.wolox.lucasdelatorre.training.services.LoginService;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class Login extends Activity {

    private TextView mTermsTv;
    private Button mLoginBn;
    private Button mSignupBn;
    private EditText mUsernameEt;
    private EditText mPasswordEt;
    private String mUsername;
    private String mPassword;
    private User mUser;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isLogged();

        setContentView(R.layout.activity_login);

        init();
        setUi();
        populate();
        setListeners();
    }

    private void init() {
        mActivity = this;
    }

    private void setUi() {
        mTermsTv = (TextView) findViewById(R.id.tv_terms);
        mLoginBn = (Button) findViewById(R.id.bn_login_login);
        mSignupBn = (Button) findViewById(R.id.bn_login_signup);
        mUsernameEt = (EditText) findViewById(R.id.et_login_user);
        mPasswordEt = (EditText) findViewById(R.id.et_login_pass);
    }

    private void populate() {
        mTermsTv.setText(Html.fromHtml(getString(R.string.login_terms)));
        mTermsTv.setMovementMethod(LinkMovementMethod.getInstance());
        mPasswordEt.setTypeface(mUsernameEt.getTypeface());
    }

    private void setListeners() {
        mLoginBn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                attemptToLogin();
            }
        });

        mSignupBn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openSignup();
            }
        });
    }

    private void attemptToLogin() {
        mUsername = mUsernameEt.getText().toString();
        mPassword = mPasswordEt.getText().toString();

        checkCredentials();
    }

    private boolean validateInputs() {
        if (mUsername.isEmpty()) {
            Utils.showToast(this, R.string.login_emptyuser);
            return false;
        } else if (mPassword.isEmpty()) {
            Utils.showToast(this, R.string.login_emptypass);
            return false;
        } else if (!Utils.validate(mUsername, Utils.EMAIL_PATTERN)) {
            Utils.showToast(this, R.string.login_invaliduser);
            return false;
        }

        return true;
    }

    private void saveCredentials() {
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();

        Gson gson = new Gson();
        String json = gson.toJson(mUser);
        editor.putString(Utils.USER_KEY, json);
        editor.commit();
    }

    private void checkCredentials() {
        if (!validateInputs()) return;

        LoginService loginTry = RestApiAdapter.getAdapter().create(LoginService.class);
        loginTry.login(mUsername, mPassword, new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                mUser = user;
                saveCredentials();
                openBoard();
            }

            @Override
            public void failure(RetrofitError error) {
                User errorEntity = (User) error.getBody();

                if (errorEntity.getCode().equalsIgnoreCase(Utils.CODE_INVALID_AUTH)) {
                    Utils.showToast(mActivity, R.string.login_invaliduserpass);
                } else {
                    Utils.showToast(mActivity, R.string.login_error);
                }
            }
        });
    }

    private void isLogged() {
        mUser = Utils.getUserSaved(this);
        if (!Utils.isAValidSession(mUser)) return;

        LoginService tokenTry = RestApiAdapterToken.
                getAdapter(mUser.getSessionToken()).create(LoginService.class);

        tokenTry.checkToken(new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                openBoard();
            }

            @Override
            public void failure(RetrofitError error) {
                User errorEntity = (User) error.getBody();

                if (errorEntity.getCode().equalsIgnoreCase(Utils.CODE_INVALID_TOKEN)) {
                    Utils.showToast(mActivity, R.string.login_invalidtoken);
                } else {
                    Utils.showToast(mActivity, R.string.login_error);
                }
            }
        });
    }

    private void openBoard() {
        Intent intent = new Intent(this, Board.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void openSignup() {
        Intent intent = new Intent(this, Signup.class);
        startActivity(intent);
    }
}
