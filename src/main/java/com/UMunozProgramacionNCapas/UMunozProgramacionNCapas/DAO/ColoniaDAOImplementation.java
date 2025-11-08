package com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.DAO;

import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.ML.Result;
import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.ML.Colonia;
import java.sql.Array;
import java.sql.ResultSet;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ColoniaDAOImplementation
        implements IColoniaDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Result GetColoniaByMunicipio(int IdMunicipio) {

        Result result = new Result();
        result.Objects = new ArrayList<>();

        try {
            result.Correct = jdbcTemplate.execute("CALL GetColoniaByIdMunicipio(?,?)", (CallableStatementCallback<Boolean>) callableSP -> {

                callableSP.setInt(1, IdMunicipio);
                callableSP.registerOutParameter(2, java.sql.Types.REF_CURSOR);
                callableSP.execute();
                ResultSet resultSet = (ResultSet) callableSP.getObject(2);

                while (resultSet.next()) {

                    Colonia colonia = new Colonia();

                    colonia.setIdColonia(resultSet.getInt("IdColonia"));
                    colonia.setNombre(resultSet.getString("Nombre"));
                    colonia.setCodigoPostal(resultSet.getString("CodigoPostal"));

                    result.Objects.add(colonia);

                }

                return true;
            });
        } catch (Exception ex) {
            result.Correct = false;
        }

        return result;
    }

}
