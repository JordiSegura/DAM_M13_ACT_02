package com.example.dam_m13_act_02;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //Buscar
    private TextView textViewNombreBuscar;
    private TextView textViewApellidosBuscar;
    private TextView textViewDeparamentoBuscar;
    private Button buttonBuscar;
    //Crear
    private TextView textViewNombreCrear;
    private TextView textViewApellidosCrear;
    private TextView textViewDeparamentoCrear;
    private Button buttonCrear;
    //Borrar
    private TextView textViewNombreBorrar;
    private TextView textViewApellidosBorrar;
    private TextView textViewDeparamentoBorrar;
    private Button buttonBorrar;
    //Modificar
    private TextView textViewNombreModificar;
    private TextView textViewApellidosModificar;
    private TextView textViewDeparamentoModificar;
    private Button buttonModificar;
    private Button buttonLogout;

    boolean checkExist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Buscar
        textViewNombreBuscar = findViewById(R.id.editTextTextNombre);
        textViewApellidosBuscar = findViewById(R.id.editTextTextApellidos);
        textViewDeparamentoBuscar = findViewById(R.id.editTextDepartamento);
        textViewApellidosBuscar.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        buttonBuscar = findViewById(R.id.buttonBuscar);
        buttonBuscar.setOnClickListener(this);
        //Crear
        textViewNombreCrear = findViewById(R.id.editTextNombreCrear);
        textViewApellidosCrear = findViewById(R.id.editTextTextApellidosCrear);
        textViewDeparamentoCrear = findViewById(R.id.editTextDepartamento2);
        textViewApellidosCrear.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);


        buttonCrear = findViewById(R.id.buttonCrear);
        buttonCrear.setOnClickListener(this);

        //Borrar
        textViewNombreBorrar = findViewById(R.id.editTextNombreBorrar);
        textViewApellidosBorrar = findViewById(R.id.editTextTextApellidosBorrar);
        textViewDeparamentoBorrar = findViewById(R.id.editTextDepartamentoBorrar);
        textViewApellidosBorrar.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);


        buttonBorrar = findViewById(R.id.buttonBorrar);
        buttonBorrar.setOnClickListener(this);

        //Modificar
        textViewNombreModificar = findViewById(R.id.editTextNombreModificar);
        textViewApellidosModificar = findViewById(R.id.editTextTextApellidosModificar);
        textViewDeparamentoModificar = findViewById(R.id.editTextDepartamentoModificar);
        textViewApellidosModificar.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        buttonModificar = findViewById(R.id.buttonModificar);
        buttonModificar.setOnClickListener(this);
        //Logout
        buttonLogout = findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {

        int viewId = view.getId();

        if (viewId == R.id.buttonBuscar) {
            System.out.println("buttonBuscar ");
            Intent intent = new Intent(MainActivity.this, BuscarActivity.class);
            // Put data in the Intent

            intent.putExtra("NOMBRE_KEY", textViewNombreBuscar.getText().toString());
            intent.putExtra("CONTRASEÑA_KEY", textViewApellidosBuscar.getText().toString());
            intent.putExtra("DEPTO_KEY", textViewDeparamentoBuscar.getText().toString());

            startActivity(intent);
        } else if (viewId == R.id.buttonCrear) {
            System.out.println("buttonCrear ");
            new PostDataToDatabase().execute();
        } else if (viewId == R.id.buttonBorrar) {
            System.out.println("buttonBorrar ");
            new DeleteDatafromDatabase().execute();
        } else if (viewId == R.id.buttonModificar) {
            System.out.println("buttonModificar ");
            new CheckDataToDatabase().execute();
            System.out.println(checkExist);
            for (int i = 0; i < 2; i++) {

            if (checkExist) {
                Intent intent = new Intent(MainActivity.this, ModificarActivity.class);
                // Put data in the Intent
                intent.putExtra("NOMBRE_KEY", textViewNombreModificar.getText().toString());
                intent.putExtra("CONTRASEÑA_KEY", textViewApellidosModificar.getText().toString());
                intent.putExtra("DEPTO_KEY", textViewDeparamentoModificar.getText().toString());
                startActivity(intent);

            } else if (!checkExist) {
                Toast.makeText(MainActivity.this, "No record has been found in database. Please check the information and try again", Toast.LENGTH_LONG).show();
            }}

        } else if (viewId == R.id.buttonLogout) {
            System.out.println("buttonLogout ");
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }


    private class PostDataToDatabase extends AsyncTask<Void, Void, String> {
        boolean completadoOK = false;

        @Override
        protected String doInBackground(Void... voids) {
            String result = "";
            PreparedStatement preparedStatement = null;
            Connection connection = null;
            PreparedStatement checkStatement = null;
            try {
                String nombreC = String.valueOf(textViewNombreCrear.getText());
                String apellidosC = String.valueOf(textViewApellidosCrear.getText());
                String departamentoC = String.valueOf(textViewDeparamentoCrear.getText());

                Class.forName("com.mysql.jdbc.Driver");
                connection = (Connection) DriverManager.getConnection(
                        "jdbc:mysql://10.0.2.2/empresa",
                        "androidDBUser",
                        "0310");

                String checkQuery = "SELECT * FROM empleados WHERE  contrasena = ?";
                checkStatement = connection.prepareStatement(checkQuery);
                checkStatement.setString(1, apellidosC);
                //checkStatement.setString(2, apellidosC);
                //checkStatement.setString(3, departamentoC);
                ResultSet resultSet = checkStatement.executeQuery();

                if (!resultSet.next()) {
                    String insertQuery = "INSERT INTO empleados (nombre, contrasena, departamento) VALUES (?,?,?)";
                    preparedStatement = connection.prepareStatement(insertQuery);
                    preparedStatement.setString(1, nombreC);
                    preparedStatement.setString(2, apellidosC);
                    preparedStatement.setString(3, departamentoC);
                    preparedStatement.executeUpdate();
                    completadoOK = true;
                } else if (resultSet.next()) {
                    completadoOK = false;
                }
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (preparedStatement != null) {
                        preparedStatement.close();
                    }
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            System.out.println("valor del boolean");
            System.out.println(completadoOK);
            if (completadoOK) {
                Toast.makeText(MainActivity.this, "Record has been inserted in database", Toast.LENGTH_LONG).show();

                textViewNombreCrear.setText("");
                textViewApellidosCrear.setText("");
                textViewDeparamentoCrear.setText("");
            } else if (!completadoOK) {
                Toast.makeText(MainActivity.this, "Password or user is already in use", Toast.LENGTH_LONG).show();

            }

        }
    }

    private class CheckDataToDatabase extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String result = "";
            PreparedStatement preparedStatement = null;
            Connection connection = null;
            PreparedStatement checkStatement = null;


            try {

                String nombreC = String.valueOf(textViewNombreModificar.getText());
                String apellidosC = String.valueOf(textViewApellidosModificar.getText());
                String departamentoC = String.valueOf(textViewDeparamentoModificar.getText());
                Class.forName("com.mysql.jdbc.Driver");
                connection = (Connection) DriverManager.getConnection(
                        "jdbc:mysql://10.0.2.2/empresa",
                        "androidDBUser",
                        "0310");
                String checkQuery = "SELECT * FROM empleados WHERE nombre = ? AND  contrasena = ? ";
                checkStatement = connection.prepareStatement(checkQuery);
                checkStatement.setString(1, nombreC);
                checkStatement.setString(2, apellidosC);
                ResultSet resultSet = checkStatement.executeQuery();

                if (resultSet.next()) {

                    checkExist = true;
                } else {
                    checkExist = false;
                }
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    // Close resources
                    if (preparedStatement != null) {
                        preparedStatement.close();
                    }
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            System.out.println("onPostExecute-Check");
            System.out.println(checkExist);
            if (checkExist) {
                checkExist = true;
            } else if (!checkExist) {
                checkExist = false;
            }


        }
    }

    private class DeleteDatafromDatabase extends AsyncTask<Void, Void, String> {
        boolean completadoOK = false;

        @Override
        protected String doInBackground(Void... voids) {
            String result = "";
            PreparedStatement preparedStatement = null;
            PreparedStatement preparedStatement2 = null;

            Connection connection = null;

            try {
                Class.forName("com.mysql.jdbc.Driver");
                connection = (Connection) DriverManager.getConnection(
                        "jdbc:mysql://10.0.2.2/empresa",
                        "androidDBUser",
                        "0310");

                String deleteQuery = "DELETE FROM empleados where nombre = ? and contrasena = ? and departamento = ? ";
                preparedStatement = connection.prepareStatement(deleteQuery);
                preparedStatement.setString(1, String.valueOf(textViewNombreBorrar.getText()));
                preparedStatement.setString(2, String.valueOf(textViewApellidosBorrar.getText()));
                preparedStatement.setString(3, String.valueOf(textViewDeparamentoBorrar.getText()));

                preparedStatement.executeUpdate();

                String nombreC2 = String.valueOf(textViewNombreBorrar.getText());
                String apellidosC2 = String.valueOf(textViewApellidosBorrar.getText());

                Class.forName("com.mysql.jdbc.Driver");
                connection = (Connection) DriverManager.getConnection(
                        "jdbc:mysql://10.0.2.2/empresa",
                        "androidDBUser",
                        "0310");
                String checkQuery = "SELECT * FROM empleados WHERE nombre = ? AND  contrasena = ? ";
                preparedStatement2 = connection.prepareStatement(checkQuery);
                preparedStatement2.setString(1, nombreC2);
                preparedStatement2.setString(2, apellidosC2);
                ResultSet resultSet = preparedStatement2.executeQuery();

                if (!resultSet.next()) {
                    completadoOK = true;
                } else {
                    completadoOK = false;
                }
            } catch (ClassNotFoundException | SQLException e) {
                completadoOK = false;
                e.printStackTrace();


            } finally {
                try {
                    // Close resources
                    if (preparedStatement != null) {
                        preparedStatement.close();
                    }
                    if (connection != null) {

                        connection.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            if (!completadoOK) {

                Toast.makeText(MainActivity.this, "Record has not been deleted", Toast.LENGTH_LONG).show();
            } else if (completadoOK) {
                Toast.makeText(MainActivity.this, "Record has been deleted in database", Toast.LENGTH_LONG).show();
                textViewNombreBorrar.setText("");
                textViewApellidosBorrar.setText("");
                textViewDeparamentoBorrar.setText("");

            }

        }
    }

    private class UpdateDatafromDatabase extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String result = "";
            PreparedStatement preparedStatement = null;
            Connection connection = null;

            try {
                String nombreC = String.valueOf(textViewNombreCrear.getText());
                String apellidosC = String.valueOf(textViewApellidosCrear.getText());
                String departamentoC = String.valueOf(textViewDeparamentoCrear.getText());


                // Carga el controlador JDBC para MySQL
                Class.forName("com.mysql.jdbc.Driver");

                // Establece la conexión con tu base de datos en PhpMyAdmin
                connection = (Connection) DriverManager.getConnection(
                        "jdbc:mysql://10.0.2.2/empresa",
                        "androidDBUser",
                        "0310");


                // Crea una declaración SQL y ejecuta la consulta
                String deleteQuery = "UPDATE  empleados SET nombre = ? and contrasena = ? and departamento = ?" +
                        "WHERE nombre = ? and contrasena = ? and departamento = ? ";
                preparedStatement = connection.prepareStatement(deleteQuery);
                preparedStatement.setString(1, String.valueOf(textViewNombreModificar.getText()));
                preparedStatement.setString(2, String.valueOf(textViewApellidosModificar.getText()));
                preparedStatement.setString(3, String.valueOf(textViewDeparamentoModificar.getText()));
                preparedStatement.setString(4, String.valueOf(textViewNombreModificar.getText()));
                preparedStatement.setString(5, String.valueOf(textViewApellidosModificar.getText()));
                preparedStatement.setString(6, String.valueOf(textViewDeparamentoModificar.getText()));

                // Execute the delete statement
                preparedStatement.executeUpdate();


                // Cierra la conexión
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();


            } finally {
                try {
                    // Close resources
                    if (preparedStatement != null) {
                        preparedStatement.close();
                    }
                    if (connection != null) {
                        Toast.makeText(getApplicationContext(), "Record deleted from database", Toast.LENGTH_SHORT).show();

                        connection.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
        }
    }


}