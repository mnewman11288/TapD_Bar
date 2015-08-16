package com.example.matt.tapd_bar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SignupActivity extends ActionBarActivity {

    // UI references.
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText passwordAgainEditText;
    private EditText emailEditText;
    private EditText barNameText;
    private EditText name, email, address, cityState, postalCode;
    ParseUser user = new ParseUser();
    String userName;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);



        // Set up the signup form.
        usernameEditText = (EditText) findViewById(R.id.username_edit_text);

        emailEditText = (EditText) findViewById(R.id.signUp_Email);
        barNameText = (EditText) findViewById(R.id.barNameSignup);
        passwordEditText = (EditText) findViewById(R.id.password_edit_text);
        passwordAgainEditText = (EditText) findViewById(R.id.password_again_edit_text);
        passwordAgainEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == R.id.edittext_action_signup ||
                        actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    signup();
                    return true;
                }
                return false;
            }
        });

        // Set up the submit button click handler
        Button mActionButton = (Button) findViewById(R.id.action_button);
        mActionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                signup();
            }
        });
    }

    private void signup() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String passwordAgain = passwordAgainEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String barName = barNameText.getText().toString().trim();

        userName = username;
        userEmail = email;

        // Validate the sign up data
        boolean validationError = false;
        StringBuilder validationErrorMessage = new StringBuilder(getString(R.string.error_intro));
        if (username.length() == 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_username));
        }


        if(!isEmailValid(email)) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_invalid_email));

        }
        if (password.length() == 0) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_password));
        }
        if (!password.equals(passwordAgain)) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_mismatched_passwords));
        }
        validationErrorMessage.append(getString(R.string.error_end));

        // If there is a validation error, display the error
        if (validationError) {
            Toast.makeText(SignupActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG)
                    .show();
            return;
        }
        // Set up a progress dialog
        final ProgressDialog dialog = new ProgressDialog(SignupActivity.this);
        dialog.setMessage(getString(R.string.progress_signup));
        dialog.show();

        // Set up a new Parse user

        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.put("BarName", barName);
        user.put("owner",true);

        // Call the Parse signup method
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                dialog.dismiss();
                if (e != null) {
                    // Show the error message
                    Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    // Start an intent for the dispatch activity

                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("objectId", userName);
                    // params.put("size", (selectedItem.hasSize() ? selectedItem.getSize() : "N/A"));

                    params.put("email", userEmail);

                    ParseCloud.callFunctionInBackground("stripeCreateConnectedAccount", params, new FunctionCallback<Object>() {
                        public void done(Object response, ParseException e) {
                            String ID = response.toString();
                            try {
                                JSONObject jObj = new JSONObject(ID);
                                ID = jObj.get("id").toString();
                            } catch (JSONException me) {
                                Log.e("MYAPP", "unexpected JSON exception", me);
                                // Do something to recover ... or kill the app.
                            }


                            if (e == null) {

                                Log.d("Cloud Response", "There were no exceptions! " + response.toString());
                                user.put("accountId", ID);
                                user.saveInBackground();
                                Toast.makeText(getApplicationContext(),
                                        "Account Created Successfully ",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Log.d("Cloud Response", "Exception: " + e);
                                Toast.makeText(getApplicationContext(),
                                        e.getMessage().toString(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });


                    Intent intent = new Intent(SignupActivity.this, DispatchActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_signup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
