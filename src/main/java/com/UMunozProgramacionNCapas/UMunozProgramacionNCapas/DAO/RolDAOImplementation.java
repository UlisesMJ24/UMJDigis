package com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.DAO;

import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.ML.Result;
import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.ML.Usuario;
import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.ML.Rol;
import java.sql.ResultSet;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RolDAOImplementation implements IRolDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Result GetAll() {

        Result result = new Result();
        result.Objects = new ArrayList<>();

        try {

            result.Correct = jdbcTemplate.execute("CALL GetAllRol(?)", (CallableStatementCallback<Boolean>) callableSP -> {

                callableSP.registerOutParameter(1, java.sql.Types.REF_CURSOR);
                callableSP.execute();

                try(ResultSet resultSet = (ResultSet) callableSP.getObject(1)){

                while (resultSet.next()) {

                    Rol rol = new Rol();

                    rol.setIdRol(resultSet.getInt("IdRol"));
                    rol.setNombre(resultSet.getString("Nombre"));

                    result.Objects.add(rol);
                }
                return true;
                }});

        } catch (Exception ex) {
            result.Correct = false;
            result.ex = ex;
        }

        return result;
    }

}
