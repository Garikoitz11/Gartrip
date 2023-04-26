package com.example.proyecto2.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.proyecto2.ActividadMapa;
import com.example.proyecto2.ActividadPrincipal;
import com.example.proyecto2.R;

public class Compra extends DialogFragment {

    private String email;

    public Compra (String email) {
        this.email = email;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //Añadimos info al dialogo
        builder.setTitle(R.string.compraRealizada);
        builder.setMessage(R.string.facturaEnviada);

        //Le añadimos un boton que cuando pulse cierre el dialogo
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Intent intent = new Intent(getContext(), ActividadPrincipal.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
        return builder.create();
    }
}
