package com.bihe0832.android.lib.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bihe0832.android.lib.ui.image.GlideExtKt;
import com.bihe0832.android.lib.utils.os.DisplayUtil;


public class CommonDialog extends Dialog {

    /**
     * 显示的标题
     */
    private TextView titleTv;

    /**
     * 显示的内容
     */
    private LinearLayout content_layout;
    private TextView contentTv;

    /**
     * 确认和取消按钮
     */
    private Button negativeBn, positiveBn;
    /**
     * 中间图片
     */
    private ImageView imageView;

    /**
     * 按钮之间的分割线
     */
    private View columnLineView;
    private TextView feedback;
    private CheckBox nomoreCb;
    private boolean isShowCheckBox = false;
    private OnCheckedListener onCheckedListener;
    private int contentColor = -1;

    public CommonDialog(Context context) {
        super(context, R.style.CommonDialog);
    }

    /**
     * 都是内容数据
     */
    private String message;
    private String title;
    private String content;
    private String htmlContent;
    private String feedbackContent;
    private String positiveString;
    private String negativeString;
    private String imageUrl = null;
    private boolean shouldCanceledOutside = false;
    private int imageContentResId = -1;
    private int imageResId = -1;
    private int maxLine = -1;
    private static final int MAX_LINES_LANDSCAPE = 3;
    private static final int MAX_LINES_PORTRAIT = 8;
    private View extraView;

    /**
     * 底部是否只有一个按钮
     */
    private boolean isSingle = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.com_bihe0832_commonm_dialog_layout);
        //初始化界面控件
        initView();
        //初始化界面数据
        refreshView();
        //初始化界面控件的事件
        initEvent();
    }

    /**
     * 初始化界面的确定和取消监听器
     */
    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        positiveBn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickBottomListener != null) {
                    onClickBottomListener.onPositiveClick();
                }
            }
        });
        //设置取消按钮被点击后，向外界提供监听
        negativeBn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickBottomListener != null) {
                    onClickBottomListener.onNegativeClick();
                }
            }
        });

        setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (onClickBottomListener != null) {
                    onClickBottomListener.onCancel();
                }
            }
        });
    }

    /**
     * 初始化界面控件的显示数据
     */
    private void refreshView() {
        //如果用户自定了title和message
        setCanceledOnTouchOutside(shouldCanceledOutside);
        setCancelable(shouldCanceledOutside);
        int screenWidth = DisplayUtil.getScreenWidth(getContext());
        int screenheight = DisplayUtil.getScreenHeight(getContext());
        if (titleTv != null) {
            if (!TextUtils.isEmpty(title)) {
                titleTv.setText(title);
                titleTv.setVisibility(View.VISIBLE);
            } else {
                titleTv.setVisibility(View.GONE);
            }
        }

        if (null != contentTv) {
            if (!TextUtils.isEmpty(content) || !TextUtils.isEmpty(htmlContent)) {
                if (!TextUtils.isEmpty(content)) {
                    contentTv.setText(content);
                } else if (!TextUtils.isEmpty(htmlContent)) {
                    CharSequence charSequence = Html.fromHtml(htmlContent);//支持html
                    contentTv.setText(charSequence);
                }
                contentTv.setVisibility(View.VISIBLE);
                contentTv.setMovementMethod(new ScrollingMovementMethod());
                if (contentColor != -1) {
                    contentTv.setTextColor(contentColor);
                }
                if (screenWidth > screenheight) {
                    if (maxLine > 0) {
                        if (maxLine > MAX_LINES_LANDSCAPE) {
                            contentTv.setMaxLines(MAX_LINES_LANDSCAPE);
                        } else {
                            contentTv.setMaxLines(maxLine);
                        }
                    } else {
                        contentTv.setMaxLines(MAX_LINES_LANDSCAPE);
                    }
                } else {
                    if (maxLine > 0) {
                        if (maxLine > MAX_LINES_PORTRAIT) {
                            contentTv.setMaxLines(MAX_LINES_PORTRAIT);
                        } else {
                            contentTv.setMaxLines(maxLine);
                        }
                    } else {
                        contentTv.setMaxLines(MAX_LINES_PORTRAIT);
                    }
                }
            } else {
                contentTv.setVisibility(View.GONE);
            }
        }

        if (extraView != null && content_layout != null && extraView.getParent() == null) {
            content_layout.addView(extraView);
        }
        //如果设置按钮的文字
        if (positiveBn != null) {
            if (!TextUtils.isEmpty(positiveString)) {
                positiveBn.setText(positiveString);
            } else {
                positiveBn.setText("确定");
            }
        }

        if (negativeBn != null) {
            if (!TextUtils.isEmpty(negativeString)) {
                negativeBn.setText(negativeString);
            }
        }

        if (imageView != null) {
            if (imageContentResId != -1) {
                imageView.setImageResource(imageContentResId);
                imageView.setVisibility(View.VISIBLE);
            } else if (imageUrl != null) {
                GlideExtKt.loadCenterCropImage(imageView, imageUrl, Color.BLACK, Color.WHITE);
                imageView.setVisibility(View.VISIBLE);
            } else {
                imageView.setVisibility(View.GONE);
            }

            if (imageView != null && imageView.getVisibility() == View.VISIBLE) {
                ViewGroup.LayoutParams para = imageView.getLayoutParams();
                if (para.width > 0) {
                    para.height = para.width * 1080 / 720;
                } else {
                    para.height = (int) ((screenWidth - DisplayUtil.dip2px(getContext(), 88)) * 720 / 1080);
                }
                imageView.setLayoutParams(para);
                if (screenWidth > screenheight) {
                    para.height = (int) (screenheight * 0.3);
                }
            }
        }

        if (feedback != null) {
            if (!TextUtils.isEmpty(feedbackContent)) {
                feedback.setText(feedbackContent);
                feedback.setVisibility(View.VISIBLE);
            } else {
                feedback.setVisibility(View.GONE);
            }
        }
        /**
         * 只显示一个按钮的时候隐藏取消按钮，回掉只执行确定的事件
         */
        if (columnLineView != null && negativeBn != null) {
            if (isSingle || TextUtils.isEmpty(negativeString)) {
                columnLineView.setVisibility(View.GONE);
                negativeBn.setVisibility(View.GONE);
            } else {
                negativeBn.setVisibility(View.VISIBLE);
                columnLineView.setVisibility(View.VISIBLE);
            }
        }

        if (isShowCheckBox) {
            nomoreCb.setVisibility(View.VISIBLE);
            nomoreCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (onCheckedListener != null) {
                        onCheckedListener.onChecked(isChecked);
                    }
                }
            });
        }
    }

    @Override
    public void show() {
        if (!isShowing()) {
            super.show();
        }
        refreshView();
    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        negativeBn = (Button) findViewById(R.id.negtive);
        positiveBn = (Button) findViewById(R.id.positive);
        titleTv = (TextView) findViewById(R.id.title);
        content_layout = findViewById(R.id.content_layout);
        contentTv = (TextView) findViewById(R.id.content);
        columnLineView = findViewById(R.id.column_line);
        imageView = (ImageView) findViewById(R.id.content_img);
        feedback = (TextView) findViewById(R.id.feedback);
        nomoreCb = (CheckBox) findViewById(R.id.nomore_cb);
    }

    /**
     * 设置确定取消按钮的回调
     */
    public OnDialogListener onClickBottomListener;

    public CommonDialog setOnClickBottomListener(OnDialogListener onClickBottomListener) {
        this.onClickBottomListener = onClickBottomListener;
        return this;
    }

    public CommonDialog setOnCheckedListener(OnCheckedListener onCheckedListener) {
        this.onCheckedListener = onCheckedListener;
        return this;
    }

    public CommonDialog setIsShowCheckBox(boolean isShowCheckBox) {
        this.isShowCheckBox = isShowCheckBox;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public CommonDialog setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getContent() {
        return content;
    }

    public CommonDialog setContent(String content) {
        this.content = content;
        return this;
    }

    public CommonDialog setContentTextColor(int colorRes) {
        this.contentColor = colorRes;
        return this;
    }

    public CommonDialog setHtmlContent(String content) {
        this.htmlContent = content;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public CommonDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getPositive() {
        return positiveString;
    }


    public void addViewToContent(View view) {
        extraView = view;
    }

    public CommonDialog setPositive(String positive) {
        this.positiveString = positive;
        return this;
    }

    public String getNegative() {
        return negativeString;
    }

    public CommonDialog setShouldCanceled(boolean flag) {
        shouldCanceledOutside = flag;
        return this;
    }

    public CommonDialog setNegative(String negtive) {
        this.negativeString = negtive;
        return this;
    }

    public int getImageResId() {
        return imageResId;
    }

    public boolean isSingle() {
        return isSingle;
    }

    public CommonDialog setSingle(boolean single) {
        isSingle = single;
        return this;
    }

    public CommonDialog setImageResId(int imageResId) {
        this.imageResId = imageResId;
        return this;
    }

    public int getImageContentResId() {
        return imageContentResId;
    }

    public CommonDialog setImageContentResId(int imageContentResId) {
        this.imageContentResId = imageContentResId;
        return this;
    }

    public CommonDialog setFeedBackContent(String feedbackContent) {
        this.feedbackContent = feedbackContent;
        return this;
    }

    public CommonDialog setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public CommonDialog setContentMaxLine(int maxLine) {
        this.maxLine = maxLine;
        return this;
    }

    public interface OnCheckedListener {

        public void onChecked(boolean isChecked);
    }
}