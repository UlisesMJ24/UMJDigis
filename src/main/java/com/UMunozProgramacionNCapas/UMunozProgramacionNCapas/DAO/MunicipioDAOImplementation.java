package com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.DAO;

import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.ML.Result;
import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.ML.Municipio;
import java.sql.ResultSet;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MunicipioDAOImplementation implements IMunicipioDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Result GetMunicipioByEstado(int IdEstado) {

        Result result = new Result();
        result.Objects = new ArrayList<>();

        try {

            result.Correct = jdbcTemplate.execute("CALL GetMunicipioByIdEstado(?,?)", (CallableStatementCallback<Boolean>) callableSP -> {
                
                callableSP.setInt(1,IdEstado);
                callableSP.registerOutParameter(2,java.sql.Types.REF_CURSOR);
                callableSP.execute();
                
                ResultSet resultSet = (ResultSet) callableSP.getObject(2);
                
                while(resultSet.next()){
                    
                    Municipio municipio = new Municipio();
                    municipio.setIdMunicipio(resultSet.getInt("IdMunicipio"));
                    municipio.setNombre(resultSet.getString("Nombre"));
                    
                    result.Objects.add(municipio);
                    
                }
                
                return true;
            });

        } catch (Exception ex) {
            result.Correct = false;
        }
        return result;
    }
}


