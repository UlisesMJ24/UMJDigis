package com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.DAO;

import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.ML.Result;
import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.ML.Usuario;
import java.util.List;


public interface IUsuarioDAO {

    Result CargaMasiva(List<Usuario> usuarios);
    
    Result Add(Usuario usuario);
   
    Result GetAll();
    
    Result GetById(int IdUsuario);
    
    Result SearchUsuarioDireccion(Usuario usuario);
}
