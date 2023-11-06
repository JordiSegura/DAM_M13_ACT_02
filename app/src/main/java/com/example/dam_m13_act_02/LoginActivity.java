package com.example.dam_m13_act_02;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class LoginActivity extends MainActivity implements View.OnClickListener {


    //Buscar
    private TextView textViewNombreContraseña;
    private TextView textViewContraseña;
    private Button buttonLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Buscar
        textViewNombreContraseña = findViewById(R.id.editTextNombreLogin);
        textViewContraseña = findViewById(R.id.editTextContraseñaLogin);

        buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {

        int viewId = view.getId();

        if (viewId == R.id.buttonLogin) {
            System.out.println("buttonLogin ");
            new GetDataFromDatabase().execute();

        }

    }

    private class GetDataFromDatabase extends AsyncTask<Void, Void, String> {
        boolean completadoOK = false;

        @Override
        protected String doInBackground(Void... voids) {
            String result = "";
            PreparedStatement preparedStatement = null;
            Connection connection = null;
            PreparedStatement checkStatement = null;
            try {
                // Carga el controlador JDBC para MySQL
                Class.forName("com.mysql.jdbc.Driver");
                // Establece la conexión con tu base de datos en PhpMyAdmin
                connection = (Connection) DriverManager.getConnection("jdbc:mysql://10.0.2.2/empresa", "androidDBUser", "0310");
                // Realizamos la sentencia SQL
                String checkQuery = "SELECT * FROM empleados WHERE nombre = ? AND contrasena = ?";
                checkStatement = connection.prepareStatement(checkQuery);
                //Asignamos valor a los parámetros de la consulta '?'
                checkStatement.setString(1, textViewNombreContraseña.getText().toString());
                checkStatement.setString(2, textViewContraseña.getText().toString());
                //Ejecutamos la consulta
                ResultSet resultSet = checkStatement.executeQuery();

                // Si la consulta devuelve resultados, quiere decir que el usuario existe, marcamos el boolean como true
                if (resultSet.next()) {
                    completadoOK = true;
                } else if (!resultSet.next()) {
                    completadoOK = false;
                }
                //Cierra la conexión
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
                result = "Error: " + e.getMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (completadoOK) {
                //Crear mensaje Toast informando del login correcto
                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_LONG).show();
                //Abrimos otra vista
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (!completadoOK) {
                //Crear mensaje Toast informando del login incorrecto
                Toast.makeText(LoginActivity.this, "Login unsuccessful. No user / password combination found in database", Toast.LENGTH_LONG).show();

            }
        }
    }
}
