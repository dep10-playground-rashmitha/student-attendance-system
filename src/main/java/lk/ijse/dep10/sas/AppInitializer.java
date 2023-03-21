package lk.ijse.dep10.sas;

import javafx.application.Application;
import javafx.stage.Stage;
import lk.ijse.dep10.sas.db.DBConnection;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class AppInitializer extends Application {

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            try{
                System.out.println("Database connection is about to close");
                if(DBConnection.getDbConnection().getConnection()!=null &&
                        !DBConnection.getDbConnection().getConnection().isClosed()) {
                    DBConnection.getDbConnection().getConnection().close();
                }
            }catch (SQLException e){
                throw new RuntimeException(e);
            }
        }));
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        generateSchemaIfNotExist();
    }

    private void generateSchemaIfNotExist(){
        Connection connection = DBConnection.getDbConnection().getConnection();
        try {
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SHOW TABLES ");
            HashSet<String> tableNameSet=new HashSet<>();
            while (rst.next()){
                tableNameSet.add(rst.getString(1));
            }

            boolean tableExist = tableNameSet.containsAll(Set.of("Attendance", "Picture", "Student", "User"));
            // Set.of is easy to make set.this come after java 9

            if(!tableExist){
                System.out.println("Some Tables are missing. so it created now missing tables");
                stm.execute(readDBScript());
            }

            System.out.println(tableExist);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private String readDBScript(){
        InputStream is = getClass().getResourceAsStream("/schema.sql");
        try(BufferedReader br=new BufferedReader(new InputStreamReader(is))){
            String line;
            StringBuilder dbScriptBuilder=new StringBuilder();
            while ((line=br.readLine())!=null){
                dbScriptBuilder.append(line).append("\n");
            }
            return dbScriptBuilder.toString();
        }catch (IOException e){
            throw new RuntimeException(e);
        }

    }

}
