package com.example.proyecto2.Services;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ObtenerPerfilBDService extends Worker {

    private Context context;
    public ObtenerPerfilBDService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public ListenableWorker.Result doWork() {
        //Declaramos la variable para devolver el resultado de la consulta
        Data resultados = new Data.Builder()
                .build();;

        //Obtenemos los datos enviados desde la actividad
        String email = getInputData().getString("email");

        //Realizamos la conexion con el servidor (con el php a ejecutar)
        String direccion = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/gsalaberria004/WEB/gartrip/obtenerPerfil.php";
        HttpURLConnection urlConnection = null;
        URL destino = null;
        try {
            destino = new URL(direccion);
            urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);

            //Le añadimos los parametros a la llamada al servidor
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("email", email);
            String parametros = builder.build().getEncodedQuery();

            //Especificamos el tipo de peticion
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            //Envia los parametros a la url indicada
            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametros);
            out.close();

            //Espera a la respues del servidor
            int statusCode = urlConnection.getResponseCode();

            //Si la respuesta es correcta lee el resultado y lo almacena en la variable de tipo data para devolverlo a la actividad
            if (statusCode == 200) {
                BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line, result = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }

                //Obtenemos la info del json devuelto por el servidor
                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(result);
                boolean operacionCorrecta = (boolean) json.get("operacionCorrecta");
                String nombre = (String) json.get("nombre");
                String apellidos = (String) json.get("apellidos");
                String foto = (String) json.get("foto");

                String ubicacionImagen = null;

                if (foto != null) {
                    //Si la imagen no es nula la decodica y obtiene el bitmap
                    byte[] fotoDecodificada = Base64.decode(foto, Base64.DEFAULT);
                    Bitmap fotoBitmap = BitmapFactory.decodeByteArray(fotoDecodificada, 0, fotoDecodificada.length);

                    //Guarda la imagen en un directorio privado que hara de intermediario ya que el objeto data no puede pasar mas de 10KB al worker
                    File eldirectorio = context.getFilesDir();
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                    String nombrefichero = "IMG_" + timeStamp + "_";
                    File imagenFich = new File(eldirectorio, nombrefichero + ".jpg");
                    OutputStream os;
                    try {
                        os = new FileOutputStream(imagenFich);
                        fotoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                        os.flush();
                        os.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //Guarda la ubicacion para pasarselo al data
                    ubicacionImagen = imagenFich.getAbsolutePath();

                }

                //Creamos un objeto de tipo data para devolver a la actividad que lo llamo
                resultados = new Data.Builder()
                        .putBoolean("operacionCorrecta", operacionCorrecta)
                        .putString("nombre", nombre)
                        .putString("apellidos", apellidos)
                        .putString("imagen", ubicacionImagen)
                        .build();
                inputStream.close();
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        //Se devuelve el objeto de tipo data
        return ListenableWorker.Result.success(resultados);
    }
}
