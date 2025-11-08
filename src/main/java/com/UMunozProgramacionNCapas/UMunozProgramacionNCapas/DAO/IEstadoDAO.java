package com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.DAO;

import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.ML.Result;


public interface IEstadoDAO {
    Result GetEstadoByPais(int IdPais);
}
