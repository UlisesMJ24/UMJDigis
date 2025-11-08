
package com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.DAO;

import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.ML.Pais;
import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.ML.Result;
import java.sql.ResultSet;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PaisDAOImplementation implements IPaisDAO{

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Override
    public Result GetAll() {
        
        Result result = new Result();
        result.Objects = new ArrayList<>();
        
        try{
            result.Correct = jdbcTemplate.execute("CALL GetAllPaises(?)", (CallableStatementCallback<Boolean>) callableStatemente ->{
                
                callableStatemente.registerOutParameter(1, java.sql.Types.REF_CURSOR);
                callableStatemente.execute();
                
                ResultSet resultSet = (ResultSet) callableStatemente.getObject(1);
                
                while(resultSet.next()){
                    Pais pais  = new Pais();
                    pais.setIdPais(resultSet.getInt("IdPais"));
                    pais.setNombre(resultSet.getString("Nombre"));
                    
                    result.Objects.add(pais);
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
