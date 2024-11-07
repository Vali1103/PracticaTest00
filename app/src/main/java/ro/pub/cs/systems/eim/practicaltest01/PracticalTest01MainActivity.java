package ro.pub.cs.systems.eim.practicaltest01;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import ro.pub.cs.systems.eim.practicaltest01.Constants;


public class PracticalTest01MainActivity extends Activity {
    private EditText leftEditText;
    private EditText rightEditText;
    private Button pressMeButton, pressMeTooButton;
    private Button navigateToSecondaryActivity;

//    d1
    private int serviceStatus = Constants.SERVICE_STOPPED;
//    d2
    private IntentFilter intentFilter = new IntentFilter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test01_main);

        leftEditText = (EditText)findViewById(R.id.left_edit_text);
        rightEditText = (EditText)findViewById(R.id.right_edit_text);

        pressMeButton = (Button)findViewById(R.id.left_button);
        pressMeTooButton = (Button)findViewById(R.id.right_button);

        leftEditText.setText(String.valueOf(0));
        rightEditText.setText(String.valueOf(0));

        pressMeButton.setOnClickListener(buttonClickListener);
        pressMeTooButton.setOnClickListener(buttonClickListener);

//        B2
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(Constants.LEFT_COUNT)) {
                leftEditText.setText(savedInstanceState.getString(Constants.LEFT_COUNT));
            } else {
                leftEditText.setText(String.valueOf(0));
            }
            if (savedInstanceState.containsKey(Constants.RIGHT_COUNT)) {
                rightEditText.setText(savedInstanceState.getString(Constants.RIGHT_COUNT));
            } else {
                rightEditText.setText(String.valueOf(0));
            }
        } else {
            leftEditText.setText(String.valueOf(0));
            rightEditText.setText(String.valueOf(0));
        }
        navigateToSecondaryActivity = (Button) findViewById(R.id.navigate_to_secondary_activity_button);
        navigateToSecondaryActivity.setOnClickListener(buttonClickListener);

//        d2
        for (int index = 0; index < Constants.actionTypes.length; index++) {
            intentFilter.addAction(Constants.actionTypes[index]);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(Constants.LEFT_COUNT, leftEditText.getText().toString());
        savedInstanceState.putString(Constants.RIGHT_COUNT, rightEditText.getText().toString());
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(Constants.LEFT_COUNT)) {
            leftEditText.setText(savedInstanceState.getString(Constants.LEFT_COUNT));
        } else {
            leftEditText.setText(String.valueOf(0));
        }
        if (savedInstanceState.containsKey(Constants.RIGHT_COUNT)) {
            rightEditText.setText(savedInstanceState.getString(Constants.RIGHT_COUNT));
        } else {
            rightEditText.setText(String.valueOf(0));
        }
    }

    private class ButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int leftNumberOfClicks = Integer.valueOf(leftEditText.getText().toString());
            int rightNumberOfClicks = Integer.valueOf(rightEditText.getText().toString());

            if (view.getId() == R.id.left_button) {
                leftNumberOfClicks++;
                leftEditText.setText(String.valueOf(leftNumberOfClicks));
            } else if (view.getId() == R.id.right_button) {
                rightNumberOfClicks++;
                rightEditText.setText(String.valueOf(rightNumberOfClicks));
            } else if (view.getId() == R.id.navigate_to_secondary_activity_button) {
                Intent intent = new Intent(getApplicationContext(), PracticalTest01SecondaryActivity.class);
                int numberOfClicks = Integer.parseInt(leftEditText.getText().toString()) +
                        Integer.parseInt(rightEditText.getText().toString());
                intent.putExtra(Constants.NUMBER_OF_CLICKS, numberOfClicks);
                startActivityForResult(intent, Constants.SECONDARY_ACTIVITY_REQUEST_CODE);
            }
//            D1
            if (leftNumberOfClicks + rightNumberOfClicks > Constants.NUMBER_OF_CLICKS_THRESHOLD
                    && serviceStatus == Constants.SERVICE_STOPPED) {
                Intent intent = new Intent(getApplicationContext(), PracticalTest01Service.class);
                intent.putExtra(Constants.FIRST_NUMBER, leftNumberOfClicks);
                intent.putExtra(Constants.SECOND_NUMBER, rightNumberOfClicks);
                getApplicationContext().startService(intent);
                serviceStatus = Constants.SERVICE_STARTED;
            }
        }
    }
    private ButtonClickListener buttonClickListener = new ButtonClickListener();
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == Constants.SECONDARY_ACTIVITY_REQUEST_CODE) {
            Toast.makeText(this, "The activity returned with result " + resultCode, Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onDestroy() {
        Intent intent = new Intent(this, PracticalTest01Service.class);
        stopService(intent);
        super.onDestroy();
    }

    private MessageBroadcastReceiver messageBroadcastReceiver = new MessageBroadcastReceiver();
    private class MessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(Constants.BROADCAST_RECEIVER_TAG, intent.getStringExtra(Constants.BROADCAST_RECEIVER_EXTRA));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(messageBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(messageBroadcastReceiver);
        super.onPause();
    }
}
