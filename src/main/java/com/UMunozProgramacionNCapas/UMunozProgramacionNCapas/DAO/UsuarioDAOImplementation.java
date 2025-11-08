package com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.DAO;

import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.ML.Colonia;
import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.ML.Direccion;
import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.ML.Estado;
import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.ML.Municipio;
import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.ML.Pais;
import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.ML.Result;
import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.ML.Rol;
import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.ML.Usuario;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UsuarioDAOImplementation implements IUsuarioDAO {

    @Autowired
    public JdbcTemplate jdbcTemplate;

    
    
    @Transactional
    public Result CargaMasiva(List<Usuario> usuarios) {
        Result result = new Result();

        String sql = "INSERT INTO Usuario (UserName, Nombre, ApellidoPaterno, ApellidoMaterno, "
                + "Email, Password, FechaNacimiento, Sexo, Telefono, Celular, CURP, Imagen) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, usuarios, usuarios.size(),
                (ps, u) -> {
                    ps.setString(1, u.getUserName());
                    ps.setString(2, u.getNombre());
                    ps.setString(3, u.getApellidoPaterno());
                    ps.setString(4, u.getApellidoMaterno());
                    ps.setString(5, u.getEmail());
                    ps.setString(6, u.getPassword());
                    ps.setDate(7, new java.sql.Date(u.getFechaNacimiento().getTime()));
                    ps.setString(8, String.valueOf(u.getSexo()));
                    ps.setString(9, u.getTelefono());
                    ps.setString(10, u.getCelular());
                    ps.setString(11, u.getCURP());
                    ps.setString(12, u.getImagen());
                }
        );

        result.Correct = true;
        return result;
    }


    
    
    
    @Override
    public Result Add(Usuario usuario) {

        Result result = new Result();

        if (usuario.getDirecciones() == null || usuario.getDirecciones().isEmpty()) {
            result.Correct = false;
            result.ErrorMessage = "El usuario debe de tener al menos una direccion";
            return result;
        }

        Direccion direccion = usuario.getDirecciones().get(0);
        int idColonia = direccion.getColonia().getIdColonia();

        result.Correct = jdbcTemplate.execute("CALL AddUsuario(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", (CallableStatementCallback<Boolean>) callableStatement -> {

            callableStatement.setString(1, usuario.getUserName());
            callableStatement.setString(2, usuario.getNombre());
            callableStatement.setString(3, usuario.getApellidoPaterno());
            callableStatement.setString(4, usuario.getApellidoMaterno());
            callableStatement.setString(5, usuario.getEmail());
            callableStatement.setString(6, usuario.getPassword());
            callableStatement.setDate(7, new java.sql.Date(usuario.getFechaNacimiento().getTime()));
            callableStatement.setString(8, String.valueOf(usuario.getSexo()));
            callableStatement.setString(9, usuario.getTelefono());
            callableStatement.setString(10, usuario.getCelular());
            callableStatement.setString(11, usuario.getCURP());
            callableStatement.setString(12, usuario.getImagen());
            callableStatement.setString(13, direccion.getCalle());
            callableStatement.setString(14, direccion.getNumeroInterior());
            callableStatement.setString(15, direccion.getNumeroExterior());
            callableStatement.setInt(16, idColonia);

            boolean isAffect = callableStatement.execute();

            if (isAffect) {
                return true;
            }
            return false;

        });
        return result;
    }

    @Override
    public Result GetAll() {

        Result result = new Result();
        result = jdbcTemplate.execute("CALL GetAllUsuario(?)", (CallableStatementCallback<Result>) callableStatement -> {

            Result resultSP = new Result();
            resultSP.Objects = new ArrayList<>();

            callableStatement.registerOutParameter(1, oracle.jdbc.OracleTypes.CURSOR);
            callableStatement.execute();

            ResultSet resultSet = (ResultSet) callableStatement.getObject(1);
            int idUsuario = 0;
            try {
                while (resultSet.next()) {

                    idUsuario = resultSet.getInt("IdUsuario");

                    if (!resultSP.Objects.isEmpty() && idUsuario == ((Usuario) (resultSP.Objects.get(resultSP.Objects.size() - 1))).getIdUsuario()) {

                        Direccion direccion = new Direccion();
                        Colonia colonia = new Colonia();
                        Municipio municipio = new Municipio();
                        Estado estado = new Estado();
                        Pais pais = new Pais();

                        estado.setPais(pais);
                        municipio.setEstado(estado);
                        colonia.setMunicipio(municipio);
                        direccion.setColonia(colonia);

                        direccion.setCalle(resultSet.getString("Calle"));
                        direccion.setNumeroInterior(resultSet.getString("NumeroInterior"));
                        direccion.setNumeroExterior(resultSet.getString("NumeroExterior"));

                        direccion.getColonia().setNombre(resultSet.getString("NombreColonia"));
                        direccion.getColonia().setCodigoPostal(resultSet.getString("CodigoPostal"));
                        direccion.getColonia().getMunicipio().setNombre(resultSet.getString("NombreMunicipio"));
                        direccion.getColonia().getMunicipio().getEstado().setNombre(resultSet.getString("NombreEstado"));
                        direccion.getColonia().getMunicipio().getEstado().getPais().setNombre(resultSet.getString("NombrePais"));

                        Usuario usuario = (Usuario) resultSP.Objects.get(resultSP.Objects.size() - 1);
                        usuario.Direcciones.add(direccion);

                    } else {
                        Usuario usuario = new Usuario();
                        Rol rol = new Rol();
                        usuario.setRol(rol);

                        usuario.setIdUsuario(resultSet.getInt("IdUsuario"));
                        usuario.setUserName(resultSet.getString("UserName"));
                        usuario.setNombre(resultSet.getString("Nombre"));
                        usuario.setApellidoPaterno(resultSet.getString("ApellidoPaterno"));
                        usuario.setApellidoMaterno(resultSet.getString("ApellidoMaterno"));
                        usuario.setEmail(resultSet.getString("Email"));
                        usuario.setPassword(resultSet.getString("Password"));
                        usuario.setFechaNacimiento(resultSet.getDate("FechaNacimiento"));
                        usuario.setSexo(resultSet.getString("Sexo").charAt(0));
                        usuario.setTelefono(resultSet.getString("Telefono"));
                        usuario.setCelular(resultSet.getString("Celular"));
                        usuario.setCURP(resultSet.getString("CURP"));
                        usuario.setImagen(resultSet.getString("Imagen"));
                        usuario.getRol().setIdRol(resultSet.getInt("IdRol"));
                        usuario.getRol().setNombre(resultSet.getString("NombreRol"));

                        usuario.Direcciones = new ArrayList<>();

                        Direccion direccion = new Direccion();
                        Colonia colonia = new Colonia();
                        Municipio municipio = new Municipio();
                        Estado estado = new Estado();
                        Pais pais = new Pais();

                        estado.setPais(pais);
                        municipio.setEstado(estado);
                        colonia.setMunicipio(municipio);
                        direccion.setColonia(colonia);

                        direccion.setIdDireccion(resultSet.getInt("IdDireccion"));
                        direccion.setCalle(resultSet.getString("Calle"));
                        direccion.setNumeroInterior(resultSet.getString("NumeroInterior"));
                        direccion.setNumeroExterior(resultSet.getString("NumeroExterior"));

                        direccion.getColonia().setNombre(resultSet.getString("NombreColonia"));
                        direccion.getColonia().setCodigoPostal(resultSet.getString("CodigoPostal"));
                        direccion.getColonia().getMunicipio().setNombre(resultSet.getString("NombreMunicipio"));
                        direccion.getColonia().getMunicipio().getEstado().setNombre(resultSet.getString("NombreEstado"));
                        direccion.getColonia().getMunicipio().getEstado().getPais().setNombre(resultSet.getString("NombrePais"));

                        usuario.Direcciones.add(direccion);
                        resultSP.Objects.add(usuario);

                        idUsuario = usuario.getIdUsuario();
                    }

                }
            } catch (Exception ex) {
                resultSP.Correct = false;
            }

            resultSP.Correct = true;
            return resultSP;
        });

        return result;
    }

    @Override
    public Result GetById(int IdUsuario) {

        Result result = jdbcTemplate.execute("CALL GetByIdUsuario(?,?)", (CallableStatementCallback<Result>) callableSP -> {

            Result innerResult = new Result();
            callableSP.setInt(1, IdUsuario);
            callableSP.registerOutParameter(2, java.sql.Types.REF_CURSOR);

            callableSP.execute();

            ResultSet resultSet = (ResultSet) callableSP.getObject(2);

            innerResult.Objects = new ArrayList<>();
            int idUsuario = 0;

            if (resultSet.next()) {

                Usuario usuario = new Usuario();

                usuario.setUserName(resultSet.getString("UserName"));
                usuario.setNombre(resultSet.getString("Nombre"));
                usuario.setApellidoPaterno(resultSet.getString("ApellidoPaterno"));
                usuario.setApellidoMaterno(resultSet.getString("ApellidoMaterno"));
                usuario.setEmail(resultSet.getString("Email"));
                usuario.setPassword(resultSet.getString("Password"));
                usuario.setFechaNacimiento(resultSet.getDate("FechaNacimiento"));
                usuario.setSexo(resultSet.getString("Sexo").charAt(0));
                usuario.setTelefono(resultSet.getString("Telefono"));
                usuario.setCelular(resultSet.getString("Celular"));
                usuario.setCURP(resultSet.getString("CURP"));
                usuario.setImagen(resultSet.getString("IMAGEN"));

                usuario.Direcciones = new ArrayList<>();
                do {
                    Direccion direccion = new Direccion();
                    direccion.colonia = new Colonia();
                    direccion.colonia.municipio = new Municipio();
                    direccion.colonia.municipio.estado = new Estado();
                    direccion.colonia.municipio.estado.pais = new Pais();

                    direccion.setCalle(resultSet.getString("Calle"));
                    direccion.setNumeroInterior(resultSet.getString("NumeroInterior"));
                    direccion.setNumeroExterior(resultSet.getString("NumeroExterior"));
                    direccion.colonia.setNombre(resultSet.getString("NombreColonia"));
                    direccion.colonia.setCodigoPostal(resultSet.getString("CodigoPostal"));
                    direccion.colonia.municipio.setNombre(resultSet.getString("NombreMunicipio"));
                    direccion.colonia.municipio.estado.setNombre(resultSet.getString("NombreEstado"));
                    direccion.colonia.municipio.estado.pais.setNombre(resultSet.getString("NombrePais"));

                    usuario.Direcciones.add(direccion);
                } while (resultSet.next());

                innerResult.Object = usuario;
                innerResult.Correct = true;

            }

            return innerResult;
        });

        return result;
    }

    @Override
    public Result SearchUsuarioDireccion(Usuario usuarioFiltro) {

        Result result = new Result();
        result.Objects = new ArrayList<>();

        try {

            result.Correct = jdbcTemplate.execute("{CALL BusquedaUsuarioDireccion(?,?,?,?,?)}", (CallableStatementCallback<Boolean>) callableSP -> {

                callableSP.setString(1, usuarioFiltro.getNombre());
                callableSP.setString(2, usuarioFiltro.getApellidoPaterno());
                callableSP.setString(3, usuarioFiltro.getApellidoMaterno());
                callableSP.setInt(4, usuarioFiltro.rol.getIdRol());
                callableSP.registerOutParameter(5, java.sql.Types.REF_CURSOR);

                callableSP.execute();

                ResultSet resultSet = (ResultSet) callableSP.getObject(5);

                Usuario ultimoUsuario = null;

                while (resultSet.next()) {

                    int idUsuario = resultSet.getInt("IdUsuario");

                    if (ultimoUsuario != null && idUsuario == ultimoUsuario.getIdUsuario()) {
                        Direccion direccion = new Direccion();
                        direccion.setIdDireccion(resultSet.getInt("IdDireccion"));
                        direccion.setCalle(resultSet.getString("Calle"));
                        direccion.setNumeroInterior(resultSet.getString("NumeroInterior"));
                        direccion.setNumeroExterior(resultSet.getString("NumeroExterior"));

                        Colonia colonia = new Colonia();
                        colonia.setNombre(resultSet.getString("NombreColonia"));
                        colonia.setCodigoPostal(resultSet.getString("CodigoPostal"));

                        Municipio municipio = new Municipio();
                        municipio.setNombre(resultSet.getString("NombreMunicipio"));

                        Estado estado = new Estado();
                        estado.setNombre(resultSet.getString("NombreEstado"));

                        Pais pais = new Pais();
                        pais.setNombre(resultSet.getString("NombrePais"));

                        estado.setPais(pais);
                        municipio.setEstado(estado);
                        colonia.setMunicipio(municipio);
                        direccion.setColonia(colonia);

                        ultimoUsuario.Direcciones.add(direccion);

                    } else {
                        Usuario usuario = new Usuario();
                        usuario.setIdUsuario(idUsuario);
                        usuario.setUserName(resultSet.getString("UserName"));
                        usuario.setNombre(resultSet.getString("NombreUsuario"));
                        usuario.setApellidoPaterno(resultSet.getString("ApellidoPaterno"));
                        usuario.setApellidoMaterno(resultSet.getString("ApellidoMaterno"));
                        usuario.setEmail(resultSet.getString("Email"));
                        usuario.setPassword(resultSet.getString("Password"));
                        usuario.setFechaNacimiento(resultSet.getDate("FechaNacimiento"));
                        usuario.setSexo(resultSet.getString("Sexo").charAt(0));
                        usuario.setTelefono(resultSet.getString("Telefono"));
                        usuario.setCelular(resultSet.getString("Celular"));
                        usuario.setCURP(resultSet.getString("CURP"));
                        usuario.setImagen(resultSet.getString("Imagen"));

                        usuario.rol = new Rol();
                        usuario.rol.setIdRol(resultSet.getInt("IdRol"));
                        usuario.rol.setNombre(resultSet.getString("NombreRol"));

                        usuario.Direcciones = new ArrayList<>();
                        Direccion direccion = new Direccion();
                        direccion.setIdDireccion(resultSet.getInt("IdDireccion"));
                        direccion.setCalle(resultSet.getString("Calle"));
                        direccion.setNumeroInterior(resultSet.getString("NumeroInterior"));
                        direccion.setNumeroExterior(resultSet.getString("NumeroExterior"));

                        Colonia colonia = new Colonia();
                        colonia.setNombre(resultSet.getString("NombreColonia"));
                        colonia.setCodigoPostal(resultSet.getString("CodigoPostal"));

                        Municipio municipio = new Municipio();
                        municipio.setNombre(resultSet.getString("NombreMunicipio"));

                        Estado estado = new Estado();
                        estado.setNombre(resultSet.getString("NombreEstado"));

                        Pais pais = new Pais();
                        pais.setNombre(resultSet.getString("NombrePais"));

                        estado.setPais(pais);
                        municipio.setEstado(estado);
                        colonia.setMunicipio(municipio);
                        direccion.setColonia(colonia);

                        usuario.Direcciones.add(direccion);
                        result.Objects.add(usuario);

                        ultimoUsuario = usuario;
                    }
                }

                return true;
            });

        } catch (Exception ex) {
            result.Correct = false;
            result.ex = ex;
        }

        return result;
    }

}
