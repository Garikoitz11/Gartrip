package com.example.proyecto2.Fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import com.example.proyecto2.R;

public class FragmentPreferencias extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        //Vinculamos el archivo de prefrencias con el fragment
        addPreferencesFromResource(R.xml.preferencias);
    }

}
