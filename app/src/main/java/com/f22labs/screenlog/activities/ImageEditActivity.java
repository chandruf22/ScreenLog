package com.f22labs.screenlog.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.f22labs.screenlog.R;
import com.f22labs.screenlog.utils.Utils;
import com.f2prateek.dart.Dart;
import com.f2prateek.dart.InjectExtra;
import com.xiaopo.flying.sticker.Sticker;
import com.xiaopo.flying.sticker.StickerView;
import com.xiaopo.flying.sticker.TextSticker;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImageEditActivity extends AppCompatActivity {


    @InjectExtra
    String imageAbsolutePath;
    @BindView(R.id.img_screenshot)
    ImageView imgScreenshot;
    @BindView(R.id.edt_input)
    EditText edtInput;
    @BindView(R.id.img_add)
    ImageView imgAdd;
    @BindView(R.id.ll_input)
    LinearLayout llInput;


    float dX;
    float dY;
    int lastAction;
    @BindView(R.id.sticker_view)
    StickerView stickerView;
    @BindView(R.id.constraint_lyt)
    ConstraintLayout constraintLyt;
    private boolean isInputLayoutEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_edit);
        ButterKnife.bind(this);

        Dart.inject(this);

        Utils.loadImage(this, imageAbsolutePath, imgScreenshot);



        stickerView.setOnStickerOperationListener(new StickerView.OnStickerOperationListener() {
            @Override
            public void onStickerAdded(@NonNull Sticker sticker) {

            }

            @Override
            public void onStickerClicked(@NonNull Sticker sticker) {


                Log.i("Clicked", "Sticker clicked");

                stickerView.remove(sticker);
            }

            @Override
            public void onStickerDeleted(@NonNull Sticker sticker) {

            }

            @Override
            public void onStickerDragFinished(@NonNull Sticker sticker) {

            }

            @Override
            public void onStickerZoomFinished(@NonNull Sticker sticker) {

            }

            @Override
            public void onStickerFlipped(@NonNull Sticker sticker) {

            }

            @Override
            public void onStickerDoubleTapped(@NonNull Sticker sticker) {

            }
        });


        constraintLyt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {


                if (llInput.getVisibility() == View.VISIBLE) {
                    llInput.setVisibility(View.GONE);
                } else {
                    llInput.setVisibility(View.VISIBLE);
                }


                return false;
            }
        });
    }



    @OnClick({R.id.img_add})
    public void onClick(View v) {

        switch (v.getId()) {

//            case R.id.sticker_view:
//
//
//                if (llInput.getVisibility() == View.VISIBLE) {
//                    llInput.setVisibility(View.GONE);
//                } else {
//                    llInput.setVisibility(View.VISIBLE);
//                }
//
//                break;

            case R.id.img_add:

                createNewTextView(edtInput.getText().toString());

                break;

        }
    }


    private void createNewTextView(String text) {

//        final FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
//
//        lparams.gravity = Gravity.CENTER;

//        StickerView stickerView = new StickerView(this);
//
//        stickerView.setLayoutParams(lparams);
//
//        stickerView.setBackgroundColor(Color.WHITE);
//        stickerView.setLocked(false);
//        stickerView.setConstrained(true);


        final TextSticker textSticker = new TextSticker(this);
        textSticker.setText(text);
        textSticker.setTextColor(Color.BLACK);
        textSticker.setTextAlign(Layout.Alignment.ALIGN_CENTER);
        textSticker.resizeText();


        stickerView.addSticker(textSticker);


//        textView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent event) {
//
//                switch (event.getActionMasked()) {
//                    case MotionEvent.ACTION_DOWN:
//                        dX = view.getX() - event.getRawX();
//                        dY = view.getY() - event.getRawY();
//                        lastAction = MotionEvent.ACTION_DOWN;
//                        break;
//
//                    case MotionEvent.ACTION_MOVE:
//                        view.setY(event.getRawY() + dY);
//                        view.setX(event.getRawX() + dX);
//                        lastAction = MotionEvent.ACTION_MOVE;
//                        break;
//
//                    case MotionEvent.ACTION_UP:
//                        if (lastAction == MotionEvent.ACTION_DOWN)
//                            Toast.makeText(ImageEditActivity.this, "Clicked!", Toast.LENGTH_SHORT).show();
//                        break;
//
//                    default:
//                        return false;
//                }
//                return true;
//
//            }
//        });


//        stickerView.addView(stickerView);

    }


}
