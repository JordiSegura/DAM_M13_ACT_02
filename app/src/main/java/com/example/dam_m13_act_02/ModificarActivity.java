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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class ModificarActivity extends MainActivity implements View.OnClickListener {



    //Buscar
    private TextView textViewNombreResultado;
    private TextView textViewApellidosResultado;
    private TextView textViewDeparamentoResultado;
    private Button buttonAtras;

    private Button buttonModificar;

    boolean completadoOK = false;



    private String nombre,apellidos,depto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar);
        //Buscar
        textViewNombreResultado = findViewById(R.id.editTextNombreResultado);
        textViewApellidosResultado = findViewById(R.id.editTextApellidosResultado);
        textViewDeparamentoResultado = findViewById(R.id.editTextDepartamentoResultado);
        //textViewApellidosResultado.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        buttonAtras = findViewById(R.id.buttonBack);
        buttonAtras.setOnClickListener(this);

        buttonModificar = findViewById(R.id.buttonConfirm);
        buttonModificar.setOnClickListener(this);

        // Retrieve data from the Intent
        Intent intent = getIntent();
        nombre = intent.getStringExtra("NOMBRE_KEY");
        apellidos = intent.getStringExtra("CONTRASEÑA_KEY");
        depto = intent.getStringExtra("DEPTO_KEY");

        // Display the data in TextViews or handle it as needed
        textViewNombreResultado.setText("Nombre:\n"+nombre);
        textViewApellidosResultado.setText("Apellidos:\n"+apellidos);
        textViewDeparamentoResultado.setText("Departamento:\n"+depto);

    }
    @Override
    public void onClick(View view) {

        int viewId = view.getId();

        if (viewId == R.id.buttonBack) {
            System.out.println("buttonBack ");
            Intent intent = new Intent(ModificarActivity.this, MainActivity.class);
            startActivity(intent);
        } else if (viewId == R.id.buttonConfirm) {
            System.out.println("buttonConfirm ");
            new GetDataFromDatabase().execute();


        }

    }

    private class GetDataFromDatabase extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String result = "";
            PreparedStatement preparedStatement = null;
            Connection connection = null;
            PreparedStatement checkStatement = null;

            try {
                Class.forName("com.mysql.jdbc.Driver");

                connection = (Connection) DriverManager.getConnection(
                        "jdbc:mysql://10.0.2.2/empresa",
                        "androidDBUser",
                        "0310");

                    String insertQuery = "UPDATE empleados set nombre = ? , contrasena = ?, departamento = ? where nombre = ? and contrasena = ? and departamento = ?";
                    preparedStatement = connection.prepareStatement(insertQuery);
                    preparedStatement.setString(1, textViewNombreResultado.getText().toString().replace("Nombre:\n",""));
                    preparedStatement.setString(2, textViewApellidosResultado.getText().toString().replace("Apellidos:\n",""));
                    preparedStatement.setString(3, textViewDeparamentoResultado.getText().toString().replace("Departamento:\n",""));

                    preparedStatement.setString(4, nombre);
                    preparedStatement.setString(5, apellidos);
                    preparedStatement.setString(6, depto);

                    preparedStatement.executeUpdate();
                    completadoOK = true;

                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
                result = "Error: " + e.getMessage();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            System.out.println(completadoOK);
            if (completadoOK) {
                Toast.makeText(ModificarActivity.this, "Record has been modified in database. Going back to main page", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ModificarActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (!completadoOK){
                Toast.makeText(ModificarActivity.this, "Record has not been updated", Toast.LENGTH_LONG).show();

            }
        }
    }
}
