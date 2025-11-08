
package com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.DAO;

import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.ML.Estado;
import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.ML.Result;
import java.sql.ResultSet;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class EstadoDAOImplementation implements IEstadoDAO{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Result GetEstadoByPais(int IdPais) {
       Result result = new Result();
       
       result.Objects = new ArrayList<>();
       try{
           
           result.Correct = jdbcTemplate.execute("CALL GetEstadosByPais(?,?)", (CallableStatementCallback<Boolean>)  callableStatemente ->{
               
               callableStatemente.setInt(1, IdPais);
               callableStatemente.registerOutParameter(2,java.sql.Types.REF_CURSOR);
               callableStatemente.execute();
               
               ResultSet resultSet = (ResultSet) callableStatemente.getObject(2);
               
               while(resultSet.next()){
                   Estado estado = new Estado();
                   estado.setIdEstado(resultSet.getInt("IdEstado"));
                   estado.setNombre(resultSet.getString("Nombre"));
                   
                   result.Objects.add(estado);
               }               
               
               return true;
           });
           
       }
       catch(Exception ex){
           result.Correct = false;
       }
       
       
       return result;
    }
    
    
    
}
