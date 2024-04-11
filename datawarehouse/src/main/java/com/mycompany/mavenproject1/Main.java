package com.mycompany.mavenproject1;

import java.io.FileReader;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Main {

    public static void main(String[] args) {
        ScheduledExecutorService programar = Executors.newScheduledThreadPool(1);//crea un servicio de ejecución programada que ejecuta tareas de manera programada
        programar.scheduleAtFixedRate(() -> {//con scheduleAtFixedRate programamos una tarea para ejecutarse periodicamente en un intervalo de tiempo fijo, lo hice con lambda
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/datawarehouse", "root", "root")) {//nos conectamos a la base de datos

                if (!jsonYaCargadoHoy(connection)) {

                    cargarJson(connection);
                } else {
                    System.out.println("El JSON ya se ha cargado hoy.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.MINUTES) ;// Indica la unidad de tiempo para el retraso inicial y el intervalo entre ejecuciones de la tarea, ademas del delay


        try {
            programar.awaitTermination(3, TimeUnit.HOURS);// bloquea la ejecución del hilo actual hasta que todas las tareas se hayan terminado
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private static boolean jsonYaCargadoHoy(Connection connection) throws SQLException {

        LocalDate fechaActual = LocalDate.now();

        String query = "SELECT COUNT(*) FROM dispo_alquiler_diaria WHERE fecha_dispo = ?";//cuenta el número de registros en la tabla dispo_alquiler_diaria que tienen la misma fecha de disponibilidad que la fecha actual
        try (PreparedStatement statement = connection.prepareStatement(query)) {//evita ataques de inyección SQL
            statement.setObject(1, fechaActual);// Se establece el valor del parámetro de la consulta sql con la fecha actual.
            try (ResultSet resultSet = statement.executeQuery()) {//realizamos la consulta y obtenemos el resultado

                if (resultSet.next()) {// Se mueve el cursor del conjunto de resultados al primer registro.
                    return resultSet.getInt(1) > 0;//Se devuelve true si el valor de la primera columna del primer registro del conjunto de resultados es mayor que cero, lo que indica que se ha cargado al menos un JSON hoy
                }
            }
        }
        return false;
    }

    private static void cargarJson(Connection connection) throws SQLException {
        LocalDate fechaActual = LocalDate.now();
        LocalDateTime fechaDispo = LocalDateTime.of(fechaActual, LocalTime.now());//ombina la fecha obtenida en el paso anterior con la hora actual para obtener un objeto LocalDateTime, que representa la fecha y hora actual. Esto se utilizará para establecer la fecha de disponibilidad en la base de datos.

        JSONParser parser = new JSONParser();//Se crea un objeto JSONParser del paquete org.json.simple.parser para analizar el contenido del archivo JSON.
        try (FileReader reader = new FileReader("datos.JSON")) {
            Object obj = parser.parse(reader);
            JSONArray jsonArray = (JSONArray) obj;
            Set<String> nombresCampos = obtenerNombresCampos(connection); // Obtener los nombres de los campos de la tabla

            String query = "INSERT INTO dispo_alquiler_diaria (id, rubro_nombre, estado, bienEmpresa_nombre, contratoEmpresa_nombre, propietario, contrato_estado, contrato_numeroComp, contrato_fechaFin, contrato_fecha, contrato_cliente_nombre, contrato_numero, contrato_fechaInicio, linea_nombre, propio, enTransito, ordenDeTrabajo_descripcion, ordenDeTrabajo_estado, ordenDeTrabajo_numeroComp, ordenDeTrabajo_fechaEntrega, ordenDeTrabajo_fechaInicio, sucursal_nombre, entregado, proveedor_nombre, articulo_codigo, bien_descripcion, bien_estado, bien_aFabricacion, bien_depositoAlmacen_nombre, bien_identificacion, bien_modelo, bien_serie, Fecha_dispo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                for (Object item : jsonArray) {
                    JSONObject jsonObject = (JSONObject) item;
                    int index = 1; // Reiniciar el índice en cada iteración del bucle

                    // Establecer la fecha de disponibilidad como el primer parámetro
                    statement.setObject(index++, fechaDispo);

                    // Establecer los valores de los campos
                    for (String campo : nombresCampos) {
                        Object valorCampo = jsonObject.get(campo);
                        if (valorCampo != null) {
                            statement.setString(index++, valorCampo.toString());
                        } else {
                            statement.setNull(index++, Types.VARCHAR);
                        }
                    }

                    statement.addBatch();
                }
                statement.executeBatch();
                System.out.println("JSON cargado correctamente.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static Set<String> obtenerNombresCampos(Connection connection) throws SQLException {
        Set<String> nombresCampos = new HashSet<>();

        // Consulta para obtener los nombres de las columnas de la tabla dispo_alquiler_diaria
        String query = "SHOW COLUMNS FROM dispo_alquiler_diaria";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                String nombreCampo = resultSet.getString("Field");
                nombresCampos.add(nombreCampo);
            }
        }
        return nombresCampos;
    }
    private static void eliminarRegistrosExistente(Connection connection) throws SQLException {
        String query = "DELETE FROM dispo_alquiler_diaria WHERE fecha_dispo = ?";
        LocalDate fechaActual = LocalDate.now();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, fechaActual);
            statement.executeUpdate();
        }
    }

}
