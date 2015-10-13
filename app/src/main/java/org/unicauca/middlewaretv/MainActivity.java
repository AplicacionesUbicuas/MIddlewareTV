package org.unicauca.middlewaretv;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.flurry.android.FlurryAgent;

import org.unicauca.middlewaretv.animation.AnimationFactory;
import org.unicauca.middlewaretv.middleware.client.MantissClient;
import org.unicauca.middlewaretv.middleware.client.MantissClientFactory;
import org.unicauca.middlewaretv.middleware.handler.MantissConnectionHandler;
import org.unicauca.middlewaretv.middleware.handler.MantissControlHandler;
import org.unicauca.middlewaretv.middleware.handler.MantissEventHandler;
import org.unicauca.middlewaretv.middleware.message.MantissMessage;
import org.unicauca.middlewaretv.middleware.message.MantissMessageFactory;
import org.unicauca.middlewaretv.middleware.message.MantissMotionEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import de.tavendo.autobahn.WebSocket;

public class MainActivity extends Activity implements
        MantissConnectionHandler, MantissEventHandler, MantissControlHandler {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Handler handler;
    private ArrayList<View> slots = new ArrayList<View>();
    private ArrayList<View> containers = new ArrayList<View>();
    private String ipT = "";
    private ConcurrentHashMap<String, Long> connectedClients = new ConcurrentHashMap<String, Long>();
    private static final float SWIPE_MIN_DISTANCE = 250;
    private static final String CANNOT_CONNECT_ALERT_TAG = "CannotConnectAlertTag";
    private static final String CONNECTION_LOST_ALERT_TAG = "ConnectionLostAlertTag";
    private static final String CONNECT_PROGRESSDIALOG_TAG = "ConnectProgressDialog";
    private final String FLURRY_APIKEY = "QB8QQZ3P9C43YJXMB8WQ"; // DEVELOP_MANTISS
    private boolean isConnected = true;
    private MantissClient mClient;
    private SharedPreferences sp;
    private int currenSelected = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = getSharedPreferences(
                getResources().getString(R.string.shared_preferences),
                MODE_PRIVATE);
        mClient = MantissClientFactory.getInstance(
                "ws://" + sp.getString("ip", "192.168.1.1") + ":8080", this,
                this, this);

        ViewAnimator viewAnimator1 = (ViewAnimator) findViewById(R.id.viewFlipper1);
        viewAnimator1.setOnClickListener(onClickListener);
        viewAnimator1.setOnFocusChangeListener(onFucusChangeListener);
        // viewAnimator1.clearFocus();

        ViewAnimator viewAnimator2 = (ViewAnimator) findViewById(R.id.viewFlipper2);
        viewAnimator2.setOnClickListener(onClickListener);
        viewAnimator2.setOnFocusChangeListener(onFucusChangeListener);

        ViewAnimator viewAnimator3 = (ViewAnimator) findViewById(R.id.viewFlipper3);
        viewAnimator3.setOnClickListener(onClickListener);
        viewAnimator3.setOnFocusChangeListener(onFucusChangeListener);

        ViewAnimator viewAnimator4 = (ViewAnimator) findViewById(R.id.viewFlipper4);
        viewAnimator4.setOnClickListener(onClickListener);
        viewAnimator4.setOnFocusChangeListener(onFucusChangeListener);

        ViewAnimator viewAnimator5 = (ViewAnimator) findViewById(R.id.viewFlipper5);
        viewAnimator5.setOnClickListener(onClickListener);
        viewAnimator5.setOnFocusChangeListener(onFucusChangeListener);

        ViewAnimator viewAnimator6 = (ViewAnimator) findViewById(R.id.viewFlipper6);
        viewAnimator6.setOnClickListener(onClickListener);
        viewAnimator6.setOnFocusChangeListener(onFucusChangeListener);

        containers.add(viewAnimator1);
        containers.add(viewAnimator2);
        containers.add(viewAnimator3);
        containers.add(viewAnimator4);
        containers.add(viewAnimator5);
        containers.add(viewAnimator6);

        handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                setupViews();
            }
        }, 10);

        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                // client = new WSClientTV(MainActivity.this);
                // client.connect();
            }
        }, 1000);

    }

    private void schedulePingRequest() {
        handler.postDelayed(requestPingMessages, 3000);
    }

    private void scheduleDisconnect() {
        handler.postDelayed(clearDisconnectedClients, 10000);
    }

    private Runnable requestPingMessages = new Runnable() {
        public void run() {
            Log.i(TAG, "Requesting pingMessage to connected clients ");

            Iterator<Entry<String, Long>> it = connectedClients.entrySet()
                    .iterator();
            while (it.hasNext()) {
                Entry<String, Long> entry = (Entry<String, Long>) it
                        .next();
                MantissMessage pingMessage = MantissMessageFactory
                        .getInstancePing("REQUEST", entry.getKey());
                mClient.publishControl(pingMessage);
            }

            schedulePingRequest();

            Log.i(TAG, "Connected clients " + connectedClients.keySet().size());
        }
    };

    private Runnable clearDisconnectedClients = new Runnable() {

        public void run() {
            Log.i(TAG, "Permforming cleanup of disconnected clients ");

            Iterator<Entry<String, Long>> it = connectedClients.entrySet()
                    .iterator();
            while (it.hasNext()) {
                Entry<String, Long> entry = (Entry<String, Long>) it
                        .next();
                if ((entry.getValue() + 10000) <= System.currentTimeMillis()) {
                    Log.i(TAG, "Removing cliente " + entry.getKey()
                            + " due time experation");
                    connectedClients.remove(entry.getKey());
                }
            }

            scheduleDisconnect();
        }
    };

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, FLURRY_APIKEY);

        if (!mClient.isConnected()) {
            showReconnectButton();
            showProgressDialog();
            mClient.connect();
        }

        hideReconnectButton();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        FlurryAgent.onEndSession(this);
        super.onStop();
    }

    private void showReconnectButton() {

    }

    private void hideReconnectButton() {

    }

    private void showAlertDialog(String title, String message, String tag) {

    }

    private void showProgressDialog() {

    }

    private void dismissFragmentDialog(String tag) {

    }

    private OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Log.i(TAG, "Clic en el elemento");
            ViewAnimator viewAnimator = (ViewAnimator) v;
            AnimationFactory.flipTransition(viewAnimator,
                    AnimationFactory.FlipDirection.LEFT_RIGHT);

        }
    };

    private OnFocusChangeListener onFucusChangeListener = new OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            clearFocusBackground();

            if (!hasFocus) {
                ViewAnimator viewAnimator = (ViewAnimator) v;
                // Log.i(TAG, "Child count " + viewAnimator.getChildCount());
                if (viewAnimator.getCurrentView().equals(
                        viewAnimator.getChildAt(1))) {
                    AnimationFactory.flipTransition(viewAnimator,
                            AnimationFactory.FlipDirection.LEFT_RIGHT);
                }
            }

            if (isConnected) {
                switch (v.getId()) {

                    case R.id.viewFlipper1:
                        View v1 = findViewById(R.id.fragmentFS1);
                        v1.setBackgroundResource(R.drawable.background_slot_blue);
                        break;

                    case R.id.viewFlipper2:
                        View v2 = findViewById(R.id.fragmentFS2);
                        v2.setBackgroundResource(R.drawable.background_slot_blue);
                        break;

                    case R.id.viewFlipper3:
                        View v3 = findViewById(R.id.fragmentFS3);
                        v3.setBackgroundResource(R.drawable.background_slot_blue);
                        break;

                    case R.id.viewFlipper4:
                        View v4 = findViewById(R.id.fragmentFS4);
                        v4.setBackgroundResource(R.drawable.background_slot_blue);
                        break;

                    case R.id.viewFlipper5:
                        View v5 = findViewById(R.id.fragmentFS5);
                        v5.setBackgroundResource(R.drawable.background_slot_blue);
                        break;

                    case R.id.viewFlipper6:
                        View v6 = findViewById(R.id.fragmentFS6);
                        v6.setBackgroundResource(R.drawable.background_slot_blue);
                        break;
                }
            }
        }
    };

    private void clearFocusBackground() {
        for (View view : slots) {
            view.setBackgroundResource(R.drawable.background_slot);
        }
    }

    private void setupViews() {

        View v1 = findViewById(R.id.fragmentFS1);
        ImageView imageView1 = (ImageView) v1.findViewById(R.id.imageView1);
        imageView1.setImageResource(R.drawable.poster01);

        View v2 = findViewById(R.id.fragmentFS2);
        ImageView imageView2 = (ImageView) v2.findViewById(R.id.imageView1);
        imageView2.setImageResource(R.drawable.poster02);

        View v3 = findViewById(R.id.fragmentFS3);
        ImageView imageView3 = (ImageView) v3.findViewById(R.id.imageView1);
        imageView3.setImageResource(R.drawable.poster03);

        View v4 = findViewById(R.id.fragmentFS4);
        ImageView imageView4 = (ImageView) v4.findViewById(R.id.imageView1);
        imageView4.setImageResource(R.drawable.poster04);

        View v5 = findViewById(R.id.fragmentFS5);
        ImageView imageView5 = (ImageView) v5.findViewById(R.id.imageView1);
        imageView5.setImageResource(R.drawable.poster05);

        View v6 = findViewById(R.id.fragmentFS6);
        ImageView imageView6 = (ImageView) v6.findViewById(R.id.imageView1);
        imageView6.setImageResource(R.drawable.poster06);

        slots.add(v1);
        slots.add(v2);
        slots.add(v3);
        slots.add(v4);
        slots.add(v5);
        slots.add(v6);

    }

    private void moveIndicatorLeft() {
        if (currenSelected <= 0) {
            currenSelected = containers.size() - 1;
        } else {
            currenSelected--;
        }
        Log.i(TAG, "moveIndicatorLeft currentSelected " + currenSelected);
        containers.get(currenSelected).requestFocus();
    }

    private void moveIndicatorRight() {
        if (currenSelected >= (containers.size() - 1)) {
            currenSelected = 0;
        } else {
            currenSelected++;
        }
        Log.i(TAG, "moveIndicatorRight currentSelected " + currenSelected);
        containers.get(currenSelected).requestFocus();
    }

    private void moveIndicatorUp() {
        currenSelected -= 3;
        if (currenSelected < 0) {
            currenSelected += 6;
        }
        Log.i(TAG, "moveIndicatorUp currentSelected " + currenSelected);
        containers.get(currenSelected).requestFocus();
    }

    private void moveIndicatorDown() {
        currenSelected = (currenSelected + 3) % containers.size();
        Log.i(TAG, "moveIndicatorDown currentSelected " + currenSelected);
        containers.get(currenSelected).requestFocus();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(TAG, "onKeyDown keyCode:" + keyCode + " event:" + event);
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Handle WAMP onOpen event
     */
    @Override
    public void onOpen() {
        Log.i(TAG, "YA estamos conectados!!");
        dismissFragmentDialog(CONNECT_PROGRESSDIALOG_TAG);
        hideReconnectButton();

        mClient.subscribe();
        mClient.subscribeControl();

        schedulePingRequest();
        scheduleDisconnect();
    }

    /**
     * Handle WAMP onClose event
     */
    @Override
    public void onClose(int code, String reason) {

        switch (code) {
            case WebSocket.ConnectionHandler.CLOSE_NORMAL:
                break;

            case WebSocket.ConnectionHandler.CLOSE_CANNOT_CONNECT:
            case WebSocket.ConnectionHandler.CLOSE_SERVER_ERROR:
            case WebSocket.ConnectionHandler.CLOSE_INTERNAL_ERROR:

                dismissFragmentDialog(CONNECT_PROGRESSDIALOG_TAG);
                dismissFragmentDialog(CANNOT_CONNECT_ALERT_TAG);
                showAlertDialog(getString(R.string.title_cannot_connect), reason,
                        CANNOT_CONNECT_ALERT_TAG);
                showReconnectButton();
                break;

            case WebSocket.ConnectionHandler.CLOSE_CONNECTION_LOST:
                dismissFragmentDialog(CONNECTION_LOST_ALERT_TAG);
                showAlertDialog(getString(R.string.title_connection_lost), reason
                                + "/r" + getString(R.string.message_connection_lost),
                        CONNECTION_LOST_ALERT_TAG);
                break;

            case WebSocket.ConnectionHandler.CLOSE_PROTOCOL_ERROR:
                break;

            case WebSocket.ConnectionHandler.CLOSE_RECONNECT:
                break;

        }
    }

    @Override
    public void onEvent(String topicUri, MantissMessage message) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMove(String topicUri, MantissMessage message) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPress(String topicUri, MantissMessage message) {
        // TODO Auto-generated method stub
        Toast.makeText(this, "onPress", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onLongPress(String topicUri, MantissMessage message) {
        // TODO Auto-generated method stub
        Toast.makeText(this, "onLongPress", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onTap(String topicUri, MantissMessage message) {
        View focus = this.getCurrentFocus();
        if (focus != null) {
            focus.performClick();
        }
    }

    @Override
    public void onDoubleTap(String topicUri, MantissMessage message) {
        // TODO Auto-generated method stub
        Toast.makeText(this, "onDoubleTap", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSwipe(String topicUri, MantissMessage message) {

        MantissMotionEvent startMME = MantissMessageFactory
                .deserializeMotionEvent(message.getStartContent());
        MantissMotionEvent endMME = MantissMessageFactory
                .deserializeMotionEvent(message.getEndContent());

        float xi = startMME.getX();
        float xf = endMME.getX();
        float yi = startMME.getY();
        float yf = endMME.getY();

        Log.i(TAG, "xi: " + xi + ", xf: " + xf + ", yi: " + yi + ", yf: " + yf);

        if (Math.abs(Math.abs(xi) - Math.abs(xf)) >= SWIPE_MIN_DISTANCE) {
            if (xi > xf) {
                moveIndicatorLeft();
            } else {
                moveIndicatorRight();
            }

        } else if (Math.abs(Math.abs(yi) - Math.abs(yf)) >= SWIPE_MIN_DISTANCE) {
            if (yi > yf) {
                moveIndicatorUp();
            } else {
                moveIndicatorDown();
            }
        }

    }

    @Override
    public void onCustom(String topicUri, MantissMessage message) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDrag(String topicUri, MantissMessage message) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSetup(String topicUri, MantissMessage message) {
        // Log.i(TAG, "onSetup " + message.toString());
        // Toast.makeText(this, "onSetup", Toast.LENGTH_SHORT).show();
        if ("REQUEST".equals(message.getStartContent())) {
            String uuid = message.getEndContent();

            connectedClients.put(uuid, System.currentTimeMillis());

            MantissMessage respMessage = MantissMessageFactory
                    .getInstanceSetup("RESPONSE", uuid);
            Log.i(TAG, "Respondiendo mensaje de inicio " + message.toString());
            mClient.publishControl(respMessage);

            Log.i(TAG,
                    "Total clientes conectados ahora "
                            + connectedClients.size());
        }
    }

    @Override
    public void onPing(String topicUri, MantissMessage message) {
        if ("RESPONSE".equals(message.getStartContent())) {
            String uuid = message.getEndContent();
            if (connectedClients.containsKey(uuid)) {
                connectedClients.replace(uuid, System.currentTimeMillis());
                Log.i(TAG, "Updating ping time for " + uuid);
            }
        } else {

        }

    }

    @Override
    public void onBye(String topicUri, MantissMessage message) {
        if ("REQUEST".equals(message.getStartContent())) {
            String uuid = message.getEndContent();
            if (connectedClients.containsKey(uuid)) {
                connectedClients.replace(uuid, System.currentTimeMillis());
                Log.i(TAG, "Deleting " + uuid + " client due to bye message");
            }
        } else {

        }

    }

    public void settingsDialog() {
        final Dialog dialog = new Dialog(this,
                R.style.NoBackgroundTitleDialogTheme);
        dialog.setContentView(R.layout.dialog_ip_config);
        Button acceptBtn = (Button) dialog.findViewById(R.id.btnAccept);
        Button cancelBtn = (Button) dialog.findViewById(R.id.btnCancel);
        final EditText ip = (EditText) dialog.findViewById(R.id.ipEditText);
        acceptBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                if (!ip.getText().toString().isEmpty()) {
                    ipT = ip.getText().toString();
                    // Writing data to SharedPreferences
                    Editor editor = sp.edit();
                    editor.putString("ip", ipT);
                    editor.commit();
                    Toast.makeText(getApplicationContext(),
                            "Ip Agregada correctamente", Toast.LENGTH_SHORT)
                            .show();
                    dialog.dismiss();
                    finish();
                    startActivity(new Intent(getApplicationContext(),
                            MainActivity.class));
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Por favor ingresa una dirección",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancelBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();
        if (item.getItemId() == R.id.ip_conf) {
            settingsDialog();
        }
        return super.onOptionsItemSelected(item);
    }

}
