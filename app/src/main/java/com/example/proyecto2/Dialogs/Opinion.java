package com.example.proyecto2.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.LayoutDirection;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;

import com.example.proyecto2.ActividadPrincipal;
import com.example.proyecto2.InterfaceIdioma;
import com.example.proyecto2.R;
import com.example.proyecto2.opinionListener;

public class Opinion extends DialogFragment {

    private RatingBar ratingBarEstrellas;
    private EditText editTextComentario;

    opinionListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            Log.i("PRUEBA",String.valueOf(context));
            listener = (opinionListener) context;
        }
        catch (ClassCastException e) {
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.opinion_dialog_layout,null);

        builder.setView(view)
                .setTitle("Opinión del hotel")
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String comentario = String.valueOf(editTextComentario.getText());
                        float puntuacion = ratingBarEstrellas.getRating();

                        if(puntuacion > 0){
                            listener.enviarOpinion(comentario,puntuacion);
                        } else {
                            Toast.makeText(getContext(),"Debe puntuar el hotel",Toast.LENGTH_LONG).show();
                        }
                    }
                });

        editTextComentario = view.findViewById(R.id.opinion);
        ratingBarEstrellas = view.findViewById(R.id.estrellas);

        return builder.create();
    }
}
