package com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.DAO;

import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.ML.Result;
import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.ML.Usuario;


public interface IUsuarioDAO {

    
    Result Add(Usuario usuario);
   
    Result GetAll();
    
    Result GetById(int IdUsuario);
}
