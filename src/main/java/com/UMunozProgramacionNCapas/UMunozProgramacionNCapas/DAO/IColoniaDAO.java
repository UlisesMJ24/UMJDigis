package com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.DAO;

import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.ML.Result;

public interface IColoniaDAO {
    
    public Result GetColoniaByMunicipio(int IdMunicipio);
    
}
