package com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.Controller;

import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.DAO.ColoniaDAOImplementation;
import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.DAO.EstadoDAOImplementation;
import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.DAO.MunicipioDAOImplementation;
import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.DAO.PaisDAOImplementation;
import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.ML.Usuario;
import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.DAO.UsuarioDAOImplementation;
import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.ML.Colonia;
import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.ML.Direccion;
import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.ML.Municipio;
import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.ML.Result;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("Usuario")
public class UsuarioController {

    @Autowired
    private UsuarioDAOImplementation usuarioDAOImplementation;
    
    @Autowired
    private PaisDAOImplementation paisDAOImplmenetation;
    
    @Autowired
    private EstadoDAOImplementation estadoDAOImplementation;
    
    @Autowired
    private MunicipioDAOImplementation municipioDAOImplementation;
    
    @Autowired
    private ColoniaDAOImplementation coloniaDAOImplementation;

    @GetMapping("/add")
    public String Add(Model model) {

        Usuario usuario = new Usuario();
        Direccion direccion = new Direccion();
        Colonia colonia = new Colonia();
        Municipio municipio = new Municipio();
        
        direccion.setColonia(colonia);
        
        
        usuario.setDirecciones(new ArrayList<>(Arrays.asList(direccion)));
        
        model.addAttribute("Paises", paisDAOImplmenetation.GetAll().Objects);
        model.addAttribute("Usuario", usuario);

        return "UsuarioForm";

    }

    @PostMapping("/add")
    public String Add(@ModelAttribute Usuario usuario,
            @RequestParam("imagenFiel") MultipartFile imagenFile, Model model) {

        if(imagenFile != null){
            try{
            
                String extencion = imagenFile.getOriginalFilename().split("\\.")[1];
                if(extencion.equals("jpg") || extencion.equals("png")){
                    
                    byte[] byteImagen = imagenFile.getBytes();
                    String imagenBase64 = Base64.getEncoder().encodeToString(byteImagen);
                    usuario.setImagen(imagenBase64);
                    
                }else{
                    model.addAttribute("Error", "Formato de imagen no valida");
                    return "UsuarioForm";
                }
                
            }catch(Exception ex){
                model.addAttribute("Error: Ocurrio un error con la imagen");
                return "UsuarioForm";
            }
        }
        
        Result result = usuarioDAOImplementation.Add(usuario);
        
        if(result.Correct){
            return "redirect:/Usuario/GetAll";
        }else{
            model.addAttribute("Error: Error al registrar al usuario" + result.ErrorMessage);
            return "UsuarioForm";
        }

    }

    @GetMapping("/GetAll")
    public String GetAll(Model model) {
        
        Result result = usuarioDAOImplementation.GetAll();
        model.addAttribute("Usuario", result.Objects);

        return "index";
    }

    /*
    path // ruta/1
    requestparam // ruta?idUsaurio=1
     */
    @GetMapping("detail/{idUsuario}")
    public String Detail(@PathVariable("idUsuario") int IdUsuario, Model model) {

        Result result = usuarioDAOImplementation.GetById(IdUsuario);
        
        model.addAttribute("usuario", result.Object);
        
        // recuperar el usuario 
        return "UsuarioDetail";
    }
    
    @GetMapping("GetEstados/{IdPais}")
    @ResponseBody
    public Result GetEstadosByPais(@PathVariable int IdPais){
        
        Result result = estadoDAOImplementation.GetEstadoByPais(IdPais);
        
        return result;
    }
    
    @GetMapping("GetMunicipio/{IdEstado}")
    @ResponseBody
    public Result GetMunicipioByEstado(@PathVariable int IdEstado){
        
        Result result = municipioDAOImplementation.GetMunicipioByEstado(IdEstado);
        
        return result;
    }
    
    @GetMapping("GetColonia/{IdMunicipio}")
    @ResponseBody
    public Result GetColoniaByMunicipio(@PathVariable int IdMunicipio){
        
        Result result = coloniaDAOImplementation.GetColoniaByMunicipio(IdMunicipio);
        
        return result;
        
    }
}
