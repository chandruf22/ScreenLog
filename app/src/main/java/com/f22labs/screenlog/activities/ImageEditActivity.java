package com.f22labs.screenlog.activities;

import android.content.ClipData;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.f22labs.screenlog.R;
import com.f22labs.screenlog.utils.Utils;
import com.f22labs.screenlog.views.ZoomTextView;
import com.f22labs.screenlog.views.ZoomableView;
import com.f2prateek.dart.Dart;
import com.f2prateek.dart.InjectExtra;
import com.meetic.dragueur.Direction;
import com.meetic.dragueur.DraggableView;

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
    @BindView(R.id.frame_view)
    FrameLayout frameView;

    float dX;
    float dY;
    int lastAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_edit);
        ButterKnife.bind(this);

        Dart.inject(this);

        Utils.loadImage(this, imageAbsolutePath, imgScreenshot);
    }

    @OnClick({R.id.frame_view, R.id.img_add})
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.frame_view:

                if (llInput.getVisibility() == View.VISIBLE) {
                    llInput.setVisibility(View.GONE);
                } else {
                    llInput.setVisibility(View.VISIBLE);
                }

                break;

            case R.id.img_add:

                createNewTextView(edtInput.getText().toString());

                break;

        }
    }


    private void createNewTextView(String text) {

        final FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);

        lparams.gravity = Gravity.CENTER;


        final TextView textView = new TextView(this);
        textView.setLayoutParams(lparams);
        textView.setText(text);
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundColor(Color.CYAN);
        textView.setPadding(20,20,20,20);




        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = view.getX() - event.getRawX();
                        dY = view.getY() - event.getRawY();
                        lastAction = MotionEvent.ACTION_DOWN;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        view.setY(event.getRawY() + dY);
                        view.setX(event.getRawX() + dX);
                        lastAction = MotionEvent.ACTION_MOVE;
                        break;

                    case MotionEvent.ACTION_UP:
                        if (lastAction == MotionEvent.ACTION_DOWN)
                            Toast.makeText(ImageEditActivity.this, "Clicked!", Toast.LENGTH_SHORT).show();
                        break;

                    default:
                        return false;
                }
                return true;

            }
        });


        frameView.addView(textView);

    }





}
