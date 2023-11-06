package com.example.dam_m13_act_02;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class BuscarActivity extends MainActivity implements View.OnClickListener {



    //Buscar
    private TextView textViewNombreResultado;
    private TextView textViewApellidosResultado;
    private TextView textViewDeparamentoResultado;
    private Button buttonAtras;


    private String nombre,apellidos,depto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);
        System.out.println("Entrando en BuscarActivity");
        //Buscar
        textViewNombreResultado = findViewById(R.id.editTextNombreResultado);
        textViewApellidosResultado = findViewById(R.id.editTextApellidosResultado);
        textViewDeparamentoResultado = findViewById(R.id.editTextDepartamentoResultado);
        textViewApellidosResultado.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        buttonAtras = findViewById(R.id.buttonBack);
        buttonAtras.setOnClickListener(this);




        // Retrieve data from the Intent
        Intent intent = getIntent();
             nombre = intent.getStringExtra("NOMBRE_KEY");
             apellidos = intent.getStringExtra("CONTRASEÑA_KEY");
             depto = intent.getStringExtra("DEPTO_KEY");

        // Display the data in TextViews or handle it as needed


   new GetDataFromDatabase().execute();

    }
    @Override
    public void onClick(View view) {

        int viewId = view.getId();

        if (viewId == R.id.buttonBack) {
            System.out.println("buttonBack ");
            Intent intent = new Intent(BuscarActivity.this, MainActivity.class);
            startActivity(intent);
        }

    }

    private class GetDataFromDatabase extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String result = "";
            ResultSet resultSet = null;

            try {


                // Carga el controlador JDBC para MySQL
                Class.forName("com.mysql.jdbc.Driver");

                // Establece la conexión con tu base de datos en PhpMyAdmin
                Connection connection = (Connection) DriverManager.getConnection(
                        "jdbc:mysql://10.0.2.2/empresa",
                        "androidDBUser",
                        "0310");


                // Crea una declaración SQL y ejecuta la consulta
                Statement statement = connection.createStatement();
                 resultSet = statement.executeQuery("SELECT * FROM empleados where " +
                        "nombre ='" + nombre + "' and " +
                        "contrasena ='" + apellidos + "'and " +
                        "departamento ='" + depto + "'" +
                        "");




                // Procesa los resultados
                while (resultSet.next()) {

                    // Obtén los datos del resultado
                    String dataNombre = resultSet.getString("nombre");
                    String dataContraseña = resultSet.getString("contrasena");
                    String dataDepartamento = resultSet.getString("departamento");

                    result += dataNombre + ",";
                    result += dataContraseña + ",";
                    result += dataDepartamento + ",";


                }


                // Cierra la conexión
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();

                result = "Error: " + e.getMessage();

            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            // Actualiza la interfaz de usuario con los datos recuperados de la base de datos
            // Split the string using the delimiter (comma in this case)
            String[] values = result.split(",");



// Set the values in the respective EditText widgets
            if (values.length >= 3) {
                Toast.makeText(getApplicationContext(),"Results found",Toast.LENGTH_SHORT).show();

                textViewNombreResultado.setText(values[0]); // Set the first value in editText1
                textViewApellidosResultado.setText(values[1]); // Set the second value in editText2
                textViewDeparamentoResultado.setText(values[2]); // Set the third value in editText3
            } else {
                Toast.makeText(getApplicationContext(),"No results found. Going back to main page",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(BuscarActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
    }
}
