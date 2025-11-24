package com.example.diorama_project;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class HomeFragment extends Fragment {

    CardView front_card, back_card, left_card, inside_card;
    SwitchCompat front_switch, back_switch, left_switch, inside_switch;
    TextView device_status_value, claps_value, lums_value, visitors_value;
    MaterialButton all_btn;

    ApiClient api = new ApiClient();

    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean ignoreSwitchEvents = false;
    private boolean isPosting = false;
    private boolean initializedValues = false;

    private final Runnable fetchSensorsRunnable = new Runnable() {
        @Override
        public void run() {
            api.get("/api/arduino/sensors", Sensors.class, new ApiClient.ApiCallback<Sensors>() {
                @Override
                public void onSuccess(Sensors result) {

                    visitors_value.setText(String.valueOf(result.getVisitors_val()));
                    claps_value.setText(String.valueOf(result.getClaps_val()));
                    lums_value.setText(String.valueOf(result.getLums_val()));

                    // handle dark mode / light mode based on luminosity value

                    if(result.getLums_val() > 300) {
                      // set light mode
                      AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                    } else {
                      // set dark mode
                      AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    }

                    // handle device status

                    String createdAt = result.getCreatedAt();
                    SimpleDateFormat isoFormat =
                            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                    isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                    try {
                        Date recordDate = isoFormat.parse(createdAt);
                        long diffMillis = System.currentTimeMillis() - recordDate.getTime();
                        long diffSeconds = diffMillis / 1000;
                        boolean isRecent = diffSeconds <= 20;

                        if (isRecent) {
                            device_status_value.setText("Online");
                            device_status_value.setTextColor(
                                    getResources().getColor(android.R.color.holo_green_dark)
                            );
                        } else {
                            device_status_value.setText("Offline");
                            device_status_value.setTextColor(
                                    getResources().getColor(android.R.color.holo_red_dark)
                            );
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        device_status_value.setText("Invalid timestamp");
                        device_status_value.setTextColor(
                                getResources().getColor(android.R.color.darker_gray)
                        );
                    }
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                }
            });

            handler.postDelayed(this, 2000);
        }
    };


    private final Runnable fetchControllersRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isPosting) {
                api.get("/api/arduino/controllers", Controllers.class, new ApiClient.ApiCallback<Controllers>() {
                    @Override
                    public void onSuccess(Controllers result) {
                        if (!isAdded() || getView() == null) return;

                        if (result.isArduino() || !initializedValues) {
                            ignoreSwitchEvents = true;
                            front_switch.setChecked(result.isFront_switch());
                            back_switch.setChecked(result.isBack_switch());
                            left_switch.setChecked(result.isLeft_switch());
                            inside_switch.setChecked(result.isInside_switch());
                            ignoreSwitchEvents = false;
                            if (!initializedValues) initializedValues = true;
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            // Re-run every 2 seconds
            handler.postDelayed(this, 2000);
        }
    };

    private void startFetching() {
        handler.post(fetchSensorsRunnable);
        handler.post(fetchControllersRunnable);
    }

    private void stopFetching() {
        handler.removeCallbacks(fetchSensorsRunnable);
        handler.removeCallbacks(fetchControllersRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        startFetching();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopFetching();
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        device_status_value = view.findViewById(R.id.device_status_value);
        claps_value = view.findViewById(R.id.claps_value);
        lums_value = view.findViewById(R.id.lums_value);
        visitors_value = view.findViewById(R.id.visitors_count_value);

        front_card = view.findViewById(R.id.front_card);
        back_card = view.findViewById(R.id.back_card);
        left_card = view.findViewById(R.id.left_card);
        inside_card = view.findViewById(R.id.inside_card);

        front_switch = view.findViewById(R.id.front_switch);
        back_switch = view.findViewById(R.id.back_switch);
        left_switch = view.findViewById(R.id.left_switch);
        inside_switch = view.findViewById(R.id.inside_switch);

        all_btn = view.findViewById(R.id.btn_turn_all);

        setupCardToggle(front_card, front_switch);
        setupCardToggle(back_card, back_switch);
        setupCardToggle(left_card, left_switch);
        setupCardToggle(inside_card, inside_switch);

        setupSwitchListener(front_switch);
        setupSwitchListener(back_switch);
        setupSwitchListener(left_switch);
        setupSwitchListener(inside_switch);

        all_btn.setOnClickListener(v -> {
            boolean allOn =
                    !(front_switch.isChecked() &&
                    back_switch.isChecked() &&
                    left_switch.isChecked() &&
                    inside_switch.isChecked()
                    );

            // Toggle all switches programmatically (will not trigger listeners)
            front_switch.setChecked(allOn);
            back_switch.setChecked(allOn);
            left_switch.setChecked(allOn);
            inside_switch.setChecked(allOn);

            // Update button text dynamically
            all_btn.setText(allOn ? "Turn Off All" : "Turn On All");

            // Now send the combined status once
            sendDeviceStatus();
        });
    }


    private void setupCardToggle(CardView card, SwitchCompat sw) {
        card.setOnClickListener(v -> {
            sw.toggle();
            sendDeviceStatus();
        });
    }

    private void setupSwitchListener(SwitchCompat sw) {
        sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed() && !ignoreSwitchEvents) {
                sendDeviceStatus();
            }
        });
    }

    private void setControlsEnabled(boolean enabled) {
        front_switch.setEnabled(enabled);
        back_switch.setEnabled(enabled);
        left_switch.setEnabled(enabled);
        inside_switch.setEnabled(enabled);
        all_btn.setEnabled(enabled);
    }

    private void sendDeviceStatus() {
        if (isPosting) return; // prevent spamming multiple posts
        isPosting = true; // mark as posting

        setControlsEnabled(false);

        Controllers controllers = new Controllers();
        controllers.setFront_switch(front_switch.isChecked());
        controllers.setBack_switch(back_switch.isChecked());
        controllers.setLeft_switch(left_switch.isChecked());
        controllers.setInside_switch(inside_switch.isChecked());
        controllers.setArduino(false);

        api.post("/api/arduino/controllers", controllers, Controllers.class, new ApiClient.ApiCallback<Controllers>() {
            @Override
            public void onSuccess(Controllers result) {
                System.out.println("Controller updated successfully, ID: " + result.getId());

                isPosting = false;
                setControlsEnabled(true);

                handler.removeCallbacks(fetchControllersRunnable);
                handler.postDelayed(fetchControllersRunnable, 1500);


            }

            @Override
            public void onError(Exception e) {
                System.err.println("Controller update failed: " + e.getMessage());

                setControlsEnabled(true);
                isPosting = false;
            }
        });
    }
}