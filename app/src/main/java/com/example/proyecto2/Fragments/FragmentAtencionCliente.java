package com.example.proyecto2.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.example.proyecto2.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentAtencionCliente#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentAtencionCliente extends Fragment {

    TextView fab;

    public FragmentAtencionCliente() {
        // Required empty public constructor
    }

    public static FragmentAtencionCliente newInstance(String param1, String param2) {
        FragmentAtencionCliente fragment = new FragmentAtencionCliente();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_atencion_cliente, container, false);

        fab = (TextView) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:688801411"));

                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // Solicitar permiso para realizar la llamada telefónica
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
                    return;
                }

                startActivity(intent);
            }
        });
        return view;
    }
}