package org.wifly.wiflydemo;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.DataSetObserver;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
//import com.afollestad.materialdialogs.MaterialDialog;
//import com.dd.CircularProgressButton;

//import org.telegram.messenger.AndroidUtilities;
//import org.telegram.messenger.LocaleController;
import org.wifly.wiflydemo.R;
import org.wifly.wiflydemo.MainActivity;

import java.util.Locale;

public class Intro extends Activity {
    private ViewPager viewPager;
    private ImageView topImage1;
    private ImageView topImage2;
    private ViewGroup bottomPages;
    private int lastPage = 0;
    private boolean justCreated = false;
   // private boolean startPressed = false;
    private int[] icons;
    private int[] titles;
    private int[] messages;
    private static final String TAG = "wifly.intro";
    public static float density = 1;
    private boolean canProceed = true;
    private EditText MobileNumber, CountryCode;
    private View CustomeDialogView;
    private View verifyOTPDialogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.intro_layout);
            icons = new int[] {
                    R.drawable.intro1,
                    R.drawable.intro2,
                    R.drawable.intro3,

            };
            titles = new int[] {
                    R.string.Page1Title,
                    R.string.Page2Title,
                    R.string.Page3Title,
            };
            messages = new int[] {
                    R.string.Page1Message,
                    R.string.Page2Message,
                    R.string.Page3Message,
            };
        viewPager = (ViewPager)findViewById(R.id.intro_view_pager);
        ImageButton goButton = (ImageButton) findViewById(R.id.Go);
        MobileNumber = (EditText) findViewById(R.id.phoneNumber);
        CountryCode = (EditText) findViewById(R.id.code);
        //startMessagingButton.setText("Go".toUpperCase());
        if (Build.VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            animator.addState(new int[] {android.R.attr.state_pressed}, ObjectAnimator.ofFloat(goButton, "translationZ", dp(2), dp(4)).setDuration(200));
            animator.addState(new int[] {}, ObjectAnimator.ofFloat(goButton, "translationZ", dp(4), dp(2)).setDuration(200));
            goButton.setStateListAnimator(animator);
        }
        topImage1 = (ImageView)findViewById(R.id.icon_image1);
        topImage2 = (ImageView)findViewById(R.id.icon_image2);
        bottomPages = (ViewGroup)findViewById(R.id.bottom_pages);
        topImage2.setVisibility(View.GONE);
        viewPager.setAdapter(new IntroAdapter());
        viewPager.setPageMargin(0);
        viewPager.setOffscreenPageLimit(1);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {
                if (i == ViewPager.SCROLL_STATE_IDLE || i == ViewPager.SCROLL_STATE_SETTLING) {
                    if (lastPage != viewPager.getCurrentItem()) {
                        lastPage = viewPager.getCurrentItem();

                        final ImageView fadeoutImage;
                        final ImageView fadeinImage;
                        if (topImage1.getVisibility() == View.VISIBLE) {
                            fadeoutImage = topImage1;
                            fadeinImage = topImage2;

                        } else {
                            fadeoutImage = topImage2;
                            fadeinImage = topImage1;
                        }

                        fadeinImage.bringToFront();
                        fadeinImage.setImageResource(icons[lastPage]);
                        fadeinImage.clearAnimation();
                        fadeoutImage.clearAnimation();


                        Animation outAnimation = AnimationUtils.loadAnimation(Intro.this, R.anim.icon_anim_fade_out);
                        outAnimation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                fadeoutImage.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                        Animation inAnimation = AnimationUtils.loadAnimation(Intro.this, R.anim.icon_anim_fade_in);
                        inAnimation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                fadeinImage.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });


                        fadeoutImage.startAnimation(outAnimation);
                        fadeinImage.startAnimation(inAnimation);
                    }
                }
            }
        });

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if (startPressed) {
                //    return;
                //}
                //startPressed = true;

                final String countryCode = CountryCode.getText().toString();
                final String mobileNumber = MobileNumber.getText().toString();
                if (TextUtils.isEmpty(countryCode)) {
                    CountryCode.setError(getString(R.string.enter_country_code));
                    canProceed = false;
                }

                if (TextUtils.isEmpty(mobileNumber)) {
                    MobileNumber.setError(getString(R.string.enter_mobile_number));
                    canProceed = false;
                }
                else if (mobileNumber.length() != 10) {

                    Toast.makeText(Intro.this, getResources().getString(R.string.enter_valid_phone_number), Toast.LENGTH_SHORT).show();
                    canProceed = false;
                }
                else {
                    final String formattedMobileNumber = String.format(Locale.US, "+%s-%s", countryCode, mobileNumber);
                    canProceed = false;
                    confirmPhoneNumberDialog(formattedMobileNumber);
                    Log.i(TAG, "Returned with canProceed = " + canProceed);
                    //canProceed = false;

                }

            }
        });

        justCreated = true;
    }

    private void confirmPhoneNumberDialog(final String mobileNumber) {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        CustomeDialogView = inflater
                .inflate(R.layout.layout_dialog_confirm_number, null);
        TextView mobileNumberText = (TextView) CustomeDialogView.findViewById(R.id.mobilenumber);
        mobileNumberText.setText(mobileNumber);
        final AlertDialog alertDialog = new AlertDialog.Builder(Intro.this).create();
        alertDialog.setTitle("Confirm Number");
        alertDialog.setView(CustomeDialogView);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //alertDialog.dismiss();
                verifyOTP(mobileNumber);
                }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "EDIT", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                canProceed = false;
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void moveToMainActivity(String mobileNumber) {
        if(canProceed) {

            SharedPreferences.Editor editor = MainActivity.pref.edit();
            editor.putString("mobileNumber", mobileNumber);
            editor.apply();
            Intent intent2 = new Intent(Intro.this, MainActivity.class);
            intent2.putExtra("fromIntro", true);
            startActivity(intent2);
            finish();
        }
    }

    private void verifyOTP(final String mobilenumber) {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        verifyOTPDialogView = inflater
                .inflate(R.layout.layout_dialog_verify_otp, null);
        final EditText otpNum = (EditText) verifyOTPDialogView.findViewById(R.id.otp);
       // mobileNumberText.setText(mobileNumber);
        final AlertDialog alertDialog = new AlertDialog.Builder(Intro.this).create();
        alertDialog.setTitle("Verify OTP");
        alertDialog.setView(verifyOTPDialogView);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Verify", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                final String num = otpNum.getText().toString();
                Log.i(TAG, "OTP=" + num);
                if (num.trim().equals("1234")) {
                    Log.i(TAG, "Inside 1234 = " + num);
                    canProceed = true;
                    moveToMainActivity(mobilenumber);
                } else {
                    Log.i(TAG, "Inside wrong = " + num);
                    Toast.makeText(Intro.this, "Not a valid OTP", Toast.LENGTH_SHORT).show();
                    canProceed = false;
                }

            }
        });
        alertDialog.show();
       }

    public static int dp(float value) {
        if (value == 0) {
            return 0;
        }
        return (int)Math.ceil(density * value);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (justCreated) {
            viewPager.setCurrentItem(0);
            lastPage = 0;
            justCreated = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private class IntroAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return 3;
        }

       @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = View.inflate(container.getContext(), R.layout.intro_view_layout, null);
            TextView headerTextView = (TextView)view.findViewById(R.id.header_text);
            TextView messageTextView = (TextView)view.findViewById(R.id.message_text);
            container.addView(view, 0);

            headerTextView.setText(getString(titles[position]));
            messageTextView.setText((messages[position]));

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            int count = bottomPages.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = bottomPages.getChildAt(a);
                if (a == position) {
                    child.setBackgroundColor(0xff2ca5e0);
                } else {
                    child.setBackgroundColor(0xffbbbbbb);
                }
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            if (observer != null) {
                super.unregisterDataSetObserver(observer);
            }
        }
    }
}