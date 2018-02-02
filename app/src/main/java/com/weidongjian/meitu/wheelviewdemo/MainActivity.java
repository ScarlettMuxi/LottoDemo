package com.weidongjian.meitu.wheelviewdemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.weigan.loopview.LoopView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private LoopView loopView;

    private Handler handler;

    private Timer mTimer;

    private enum STATE {
        STOP, QUICK_SCROLL, SLOW_DOWN
    }

    private STATE state = STATE.STOP;

    private int deltaY = 40;

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loopView = (LoopView) findViewById(R.id.loopView);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 0) {
                    loopView.invalidate();
                }else if(msg.what == 1){
                    button.setText("开始");
                }
            }
        };

        mTimer = new Timer();

        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add("item " + i);
        }

        //设置原始数据
        loopView.setItems(list);

        //设置初始位置
        loopView.setInitPosition(0);

        button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(state == STATE.STOP) {
                    button.setText("停止");
                    mTimer = new Timer();

                    //匀速滚动
                    mTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            deltaY = 40;
                            loopView.totalScrollY += deltaY;
                            handler.sendEmptyMessage(0);
                        }
                    }, 0, 40);
                    state = STATE.QUICK_SCROLL;
                }else if(state == STATE.QUICK_SCROLL){
                    mTimer.cancel();

                    mTimer = new Timer();

                    //减速滚动
                    mTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            deltaY--;
                            if(deltaY > 0) {
                                loopView.totalScrollY += deltaY;
                                handler.sendEmptyMessage(0);
                            }else {
                                mTimer.cancel();
                                loopView.smoothScroll(LoopView.ACTION.DAGGLE);
                                handler.sendEmptyMessage(1);
                                state = STATE.STOP;
                            }
                        }
                    }, 0, 40);
                    state = STATE.SLOW_DOWN;
                }else if(state == STATE.SLOW_DOWN){
                    return;
                }
            }
        });
    }

}
