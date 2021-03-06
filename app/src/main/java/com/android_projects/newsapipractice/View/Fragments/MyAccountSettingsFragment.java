package com.android_projects.newsapipractice.View.Fragments;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.android_projects.newsapipractice.R;
import com.android_projects.newsapipractice.Utils.Utility;
import com.android_projects.newsapipractice.View.LoginActivity;
import com.android_projects.newsapipractice.View.Managers.SharedPrefManager;
import com.android_projects.newsapipractice.databinding.DialogFontSizeBinding;
import com.android_projects.newsapipractice.databinding.FragmentAccountSettingBinding;
import com.google.android.gms.tasks.Task;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import static com.android_projects.newsapipractice.View.LoginActivity.googleSignInClient;

public class MyAccountSettingsFragment extends Fragment {
    private final String TAG = MyAccountSettingsFragment.class.getSimpleName();
    private Utility utility;

    private FragmentAccountSettingBinding settingBinding;
    private DialogFontSizeBinding dialogBinding;

    private View v;
    private RadioGroup fontSizeRadioGroup;

    private SwitchMaterial wifiSwitch, mobileDataSwitch;
    private WifiManager wifiMgr;
    private final int REQUEST_CODE_WIFI_SETTING = 110;

    private SharedPrefManager sharedPrefMgr;
    private final String KEY_MOBILE_DATA_PREF = "key_mobile_data_pref";
    private final String KEY_WIFI_SWITCH_PREF = "key_wifi_switch_pref";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        settingBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_account_setting, container, false);
        dialogBinding = DataBindingUtil.inflate(inflater.from(getContext()), R.layout.dialog_font_size, null, false);

        return v = settingBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fontSizeRadioGroup = dialogBinding.dialogRadioGroup;
        wifiSwitch = settingBinding.settingsWifi.prefSettingSwitch;
        mobileDataSwitch = settingBinding.settingsMobileData.prefSettingSwitch;

        wifiMgr = (WifiManager) getActivity().getApplicationContext().getSystemService(v.getContext().WIFI_SERVICE);
        utility = new Utility();
        sharedPrefMgr = new SharedPrefManager(v.getContext());

        setSettingsUI();
        wifiSwitch.setChecked(true);
        settingsTabOnClick();

        logoutBtn();
    }

    private void settingsTabOnClick() {
        enableDisableWifi();
        setMobileDataSwitch();
    }

    private void onRadioButtonChecked() {
        //for dialog not alert dialog
        fontSizeRadioGroup.setOnCheckedChangeListener((RadioGroup radioGroup, int checkedId) -> {
            int childCount = radioGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                MaterialRadioButton materialRb = (MaterialRadioButton) radioGroup.getChildAt(i);
                if (materialRb.getId() == checkedId) {
                    Log.d(TAG, "Selected radio button -> " + materialRb.getText().toString());
                }
            }
        });
    }

    private void enableDisableWifi() {
        //WifiManager.setWifiEnable(boolean) is deprecated in Android 10, API level 29
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            wifiSwitch.setVisibility(View.GONE);
            settingBinding.settingsWifi.prefSettingsLinearLayout.setOnClickListener((View v) -> {
                Intent panelIntent = new Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY);
                startActivityForResult(panelIntent, REQUEST_CODE_WIFI_SETTING);
            });
        } else {
            toggleWifi();
        }
    }

    private void toggleWifi() {
        if (wifiMgr != null) {
            wifiSwitch.setOnCheckedChangeListener((CompoundButton compoundBtn, boolean isChecked) -> {
                if (isChecked) {
                    Log.d(TAG, "Checked");
                    wifiMgr.setWifiEnabled(true);
                    mobileDataSwitch.setChecked(false);
                }else{
                    Log.d(TAG, "Unchecked ");
                    wifiMgr.setWifiEnabled(false);
                }
                sharedPrefMgr.saveSwitchState(KEY_WIFI_SWITCH_PREF, isChecked);
            });
            wifiSwitch.setChecked(sharedPrefMgr.getWifiSwitchState(KEY_WIFI_SWITCH_PREF,wifiMgr));
        }
    }

    private void setMobileDataSwitch() {
        mobileDataSwitch.setChecked(sharedPrefMgr.getMobileDataSwitchState(KEY_MOBILE_DATA_PREF));
        mobileDataSwitch.setOnCheckedChangeListener((CompoundButton compoundBtn, boolean isChecked) -> {
            if (isChecked) {
                wifiSwitch.setChecked(false);
                wifiMgr.setWifiEnabled(false);
                Log.d(TAG, "Is Wifi On: " + wifiMgr.isWifiEnabled());
            } else {
                wifiSwitch.setChecked(true);
                Log.d(TAG, "Is Wifi Off: " + wifiMgr.isWifiEnabled());

            }
            sharedPrefMgr.saveSwitchState(KEY_MOBILE_DATA_PREF, isChecked);

        });
    }

    /*private void showFontStyleDialog() {
        String[] items = {"Small", "Medium", "Large", "Extra Large"};
        fontSizeRadioGroup.setVisibility(View.VISIBLE);

        dialog = new AlertDialog.Builder(v.getContext()).setTitle("Select A Size")
                .setCancelable(false);
        dialog.setSingleChoiceItems(items, -1, (DialogInterface dialogInterface, int item) -> {
            for (int i = 0; i < items.length; i++) {
                if (i == item) {
                    Log.d(TAG, "Selected radio button -> " + item);
                }
            }
            dialogInterface.dismiss();
        });
        dialog.show();

        Log.d(TAG, "Clicked");
    }*/

    private void logoutBtn() {
        settingBinding.preferenceLogOutBtn.setOnClickListener((View v) -> {
            Log.d(TAG, "Clicked");
            Intent intent = new Intent(v.getContext(), LoginActivity.class);
            googleSignInClient.signOut().addOnCompleteListener((Task<Void> task) -> {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();
            });

        });

    }

    private void setSettingsUI() {
        settingBinding.settingsWifi.prefSettingTitle.setText(getString(R.string.settings_title_wifi));
        settingBinding.settingsWifi.prefSettingDescription.setText(getString(R.string.settings_description_wifi));
        settingBinding.settingsWifi.prefSettingSwitch.setEnabled(true);
        settingBinding.settingsWifi.prefSettingListImgView.setImageResource(R.mipmap.ic_wifi);

        settingBinding.settingsMobileData.prefSettingTitle.setText(getString(R.string.settings_title_mobile_data));
        settingBinding.settingsMobileData.prefSettingListImgView.setImageResource(R.mipmap.ic_cellular_network);
        settingBinding.settingsMobileData.prefSettingDescription.setText(getString(R.string.settings_description_mobile_data));

        /*settingBinding.settingsTextFont.prefSettingTitle.setText(getString(R.string.settings_title_font));
        settingBinding.settingsTextFont.prefSettingDescription.setText(getString(R.string.settings_description_font));
        settingBinding.settingsTextFont.prefSettingListImgView.setImageResource(R.mipmap.ic_font);*/

    }
}
