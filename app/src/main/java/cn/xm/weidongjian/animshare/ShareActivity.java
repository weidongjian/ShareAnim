package cn.xm.weidongjian.animshare;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Weidongjian on 2015/7/10.
 */
public class ShareActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvFriend, tvTimeline, tvQrcode, tvCopylink;
    private TextView tvCode = null;
    private int screenWidth = 0;
    private ViewGroup rootView;
    private static final int ANIM_TIME = 500;
    private OvershootInterpolator overshootInterpolator = new OvershootInterpolator();
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        context = this;
        screenWidth = getScreenWidth(context);
        initUI();
    }

    private void initUI() {
        tvFriend = (TextView) findViewById(R.id.wxFriend);
        tvFriend.setOnClickListener(this);
        tvTimeline = (TextView) findViewById(R.id.wxTimeline);
        tvTimeline.setOnClickListener(this);
        tvQrcode = (TextView) findViewById(R.id.qrcode);
        tvQrcode.setOnClickListener(this);
        tvCopylink = (TextView) findViewById(R.id.copyLink);
        tvCopylink.setOnClickListener(this);
        findViewById(R.id.parent).setOnClickListener(this);
        findViewById(R.id.qqFriend).setOnClickListener(this);
        findViewById(R.id.qzone).setOnClickListener(this);

        rootView = (ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content);

        tvFriend.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                tvFriend.getViewTreeObserver().removeOnPreDrawListener(this);
                tvFriend.setTranslationX(-screenWidth / 2);
                tvFriend.setTranslationY(-tvFriend.getHeight() * 2);
                return false;
            }
        });
        tvTimeline.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                tvTimeline.getViewTreeObserver().removeOnPreDrawListener(this);
                tvTimeline.setTranslationX(screenWidth / 2);
                tvTimeline.setTranslationY(-tvFriend.getHeight() * 2);
                return false;
            }
        });
        tvQrcode.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                tvQrcode.getViewTreeObserver().removeOnPreDrawListener(this);
                tvQrcode.setTranslationX(-screenWidth / 2);
                tvQrcode.setTranslationY(tvFriend.getHeight() * 2);
                return false;
            }
        });
        tvCopylink.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                tvCopylink.getViewTreeObserver().removeOnPreDrawListener(this);
                tvCopylink.setTranslationX(screenWidth / 2);
                tvCopylink.setTranslationY(tvFriend.getHeight() * 2);
                return false;
            }
        });

        tvFriend.post(new Runnable() {
            @Override
            public void run() {
                moveInAnim(false);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wxFriend:
                break;
            case R.id.wxTimeline:
                break;
            case R.id.qrcode:
                moveOutAnim(false, true);
                break;
            case R.id.copyLink:
                copyToClipBoard();
                break;
            case R.id.parent:
                back();
                break;
            case R.id.tvScan:
                moveInAnim(true);
                break;
            case R.id.qqFriend:
                break;
            case R.id.qzone:
                break;
            default:
                break;
        }
    }


    private void moveInAnim(boolean isHideCode) {
        ObjectAnimator friendAnimatorX = ObjectAnimator.ofFloat(tvFriend, "TranslationX", 0);
        ObjectAnimator friendAnimatorY = ObjectAnimator.ofFloat(tvFriend, "TranslationY", 0);
        ObjectAnimator timelineAnimatorX = ObjectAnimator.ofFloat(tvTimeline, "TranslationX", 0);
        ObjectAnimator timelineAnimatorY = ObjectAnimator.ofFloat(tvTimeline, "TranslationY", 0);
        ObjectAnimator qrcodeAnimatorX = ObjectAnimator.ofFloat(tvQrcode, "TranslationX", 0);
        ObjectAnimator qrcodeAnimatorY = ObjectAnimator.ofFloat(tvQrcode, "TranslationY", 0);
        ObjectAnimator copyAnimatorX = ObjectAnimator.ofFloat(tvCopylink, "TranslationX", 0);
        ObjectAnimator copyAnimatorY = ObjectAnimator.ofFloat(tvCopylink, "TranslationY", 0);

        AnimatorSet set = new AnimatorSet();
        set.setDuration(ANIM_TIME);

        if (isHideCode) {
            ObjectAnimator animatorX = ObjectAnimator.ofFloat(tvCode, "ScaleX", 0.1f);
            ObjectAnimator animatorY = ObjectAnimator.ofFloat(tvCode, "ScaleY", 0.1f);
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    tvCode.setVisibility(View.INVISIBLE);
                }
            });
            set.playTogether(friendAnimatorX, friendAnimatorY, timelineAnimatorX, timelineAnimatorY
                    , qrcodeAnimatorX, qrcodeAnimatorY, copyAnimatorX, copyAnimatorY, animatorX, animatorY);
        } else {
            set.setInterpolator(new FastOutSlowInInterpolator());
            set.playTogether(friendAnimatorX, friendAnimatorY, timelineAnimatorX, timelineAnimatorY
                    , qrcodeAnimatorX, qrcodeAnimatorY, copyAnimatorX, copyAnimatorY);
        }

        set.start();
    }


    private void addQrcode() {
        if (tvCode != null) {
            tvCode.setVisibility(View.VISIBLE);
            return;
        }
        tvCode = new TextView(context);
        tvCode.setBackgroundColor(Color.BLACK);
        tvCode.setGravity(Gravity.CENTER_HORIZONTAL);
        tvCode.setText("请扫描二维码");
        tvCode.setPadding(80, 40, 80, 40);
        tvCode.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_scan);
        tvCode.setCompoundDrawablePadding(20);
        tvCode.setTextColor(Color.WHITE);
        tvCode.setTextSize(20);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        tvCode.setScaleX(0.1f);
        tvCode.setScaleY(0.1f);
        tvCode.setId(R.id.tvScan);
        tvCode.setOnClickListener(this);
        rootView.addView(tvCode, params);
        tvCode.setOnClickListener(this);
    }


    private void moveOutAnim(boolean isFinishActivity, boolean isShowCode) {
        ObjectAnimator friendAnimatorX = ObjectAnimator.ofFloat(tvFriend, "TranslationX", -screenWidth / 2);
        ObjectAnimator friendAnimatorY = ObjectAnimator.ofFloat(tvFriend, "TranslationY", -tvFriend.getHeight() * 2);
        ObjectAnimator timelineAnimatorX = ObjectAnimator.ofFloat(tvTimeline, "TranslationX", screenWidth / 2);
        ObjectAnimator timelineAnimatorY = ObjectAnimator.ofFloat(tvTimeline, "TranslationY", -tvFriend.getHeight() * 2);
        ObjectAnimator qrcodeAnimatorX = ObjectAnimator.ofFloat(tvQrcode, "TranslationX", -screenWidth / 2);
        ObjectAnimator qrcodeAnimatorY = ObjectAnimator.ofFloat(tvQrcode, "TranslationY", tvFriend.getHeight() * 2);
        ObjectAnimator copyAnimatorX = ObjectAnimator.ofFloat(tvCopylink, "TranslationX", screenWidth / 2);
        ObjectAnimator copyAnimatorY = ObjectAnimator.ofFloat(tvCopylink, "TranslationY", tvFriend.getHeight() * 2);

        AnimatorSet set = new AnimatorSet();
        set.setDuration(ANIM_TIME);

        if (isShowCode) {
            addQrcode();
            ObjectAnimator animatorScaleX = ObjectAnimator.ofFloat(tvCode, "ScaleX", 1f);
            ObjectAnimator animatorScaleY = ObjectAnimator.ofFloat(tvCode, "ScaleY", 1f);
            animatorScaleX.setInterpolator(overshootInterpolator);
            animatorScaleY.setInterpolator(overshootInterpolator);
            set.playTogether(friendAnimatorX, friendAnimatorY, timelineAnimatorX, timelineAnimatorY
                    , qrcodeAnimatorX, qrcodeAnimatorY, copyAnimatorX, copyAnimatorY, animatorScaleX, animatorScaleY);
        } else {
            set.playTogether(friendAnimatorX, friendAnimatorY, timelineAnimatorX, timelineAnimatorY
                    , qrcodeAnimatorX, qrcodeAnimatorY, copyAnimatorX, copyAnimatorY);
        }

        if (isFinishActivity) {
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    finish();
                    overridePendingTransition(0, 0);
                }
            });
        }

        set.start();
    }

    private void copyToClipBoard() {
        ClipboardManager myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = android.content.ClipData.newPlainText("Copied Text", "http://daijiushi.com/");
        myClipboard.setPrimaryClip(clip);
        Toast.makeText(context, "分享链接已复制到剪切板", Toast.LENGTH_SHORT).show();
    }

    private void back() {
        if (tvCode != null && tvCode.isShown()) {
            moveInAnim(true);
            return;
        }
        moveOutAnim(true, false);
    }

    @Override
    public void onBackPressed() {
        back();
    }

    private int getScreenWidth(Context c) {
        if (screenWidth == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;
        }
        return screenWidth;
    }
}

