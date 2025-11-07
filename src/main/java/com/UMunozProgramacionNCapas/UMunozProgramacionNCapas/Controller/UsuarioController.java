package com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.Controller;

import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.DAO.ColoniaDAOImplementation;
import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.DAO.EstadoDAOImplementation;
import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.DAO.MunicipioDAOImplementation;
import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.DAO.PaisDAOImplementation;
import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.DAO.RolDAOImplementation;
import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.DAO.UsuarioDAOImplementation;
import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.ML.Colonia;
import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.ML.Direccion;
import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.ML.ErrorCarga;
import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.ML.Municipio;
import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.ML.Result;
import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.ML.Usuario;
import com.UMunozProgramacionNCapas.UMunozProgramacionNCapas.Service.ValidationService;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
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
    private PaisDAOImplementation paisDAOImplementation;

    @Autowired
    private EstadoDAOImplementation estadoDAOImplementation;

    @Autowired
    private MunicipioDAOImplementation municipioDAOImplementation;

    @Autowired
    private ColoniaDAOImplementation coloniaDAOImplementation;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private RolDAOImplementation rolDAOImplementation;

    @GetMapping("/add")
    public String Add(Model model) {

        Usuario usuario = new Usuario();
        Direccion direccion = new Direccion();
        Colonia colonia = new Colonia();
        Municipio municipio = new Municipio();

        direccion.setColonia(colonia);

        usuario.setDirecciones(new ArrayList<>(Arrays.asList(direccion)));

        model.addAttribute("Paises", paisDAOImplementation.GetAll().Objects);
        model.addAttribute("Usuario", usuario);

        return "UsuarioForm";

    }

    @PostMapping("/add")
    public String Add(@ModelAttribute Usuario usuario,
            @RequestParam("imagenFiel") MultipartFile imagenFile, Model model) {

        if (imagenFile != null) {
            try {

                String extencion = imagenFile.getOriginalFilename().split("\\.")[1];
                if (extencion.equals("jpg") || extencion.equals("png")) {

                    byte[] byteImagen = imagenFile.getBytes();
                    String imagenBase64 = Base64.getEncoder().encodeToString(byteImagen);
                    usuario.setImagen(imagenBase64);

                } else {
                    model.addAttribute("Error", "Formato de imagen no valida");
                    return "UsuarioForm";
                }

            } catch (Exception ex) {
                model.addAttribute("Error: Ocurrio un error con la imagen");
                return "UsuarioForm";
            }
        }

        Result result = usuarioDAOImplementation.Add(usuario);

        if (result.Correct) {
            return "redirect:/Usuario/GetAll";
        } else {
            model.addAttribute("Error: Error al registrar al usuario" + result.ErrorMessage);
            return "UsuarioForm";
        }

    }

    @GetMapping("/GetAll")
    public String GetAll(Model model) {
        Result result = usuarioDAOImplementation.GetAll();
        Result resultRol = rolDAOImplementation.GetAll();
        model.addAttribute("roles", resultRol.Objects);
        model.addAttribute("usuarios", result.Objects);
        model.addAttribute("usuarioBusqueda", new Usuario());
        return "index";
    }

    @PostMapping("GetAll")
    public String SearchUsuarioDireccion(@ModelAttribute("usuarioBusqueda") Usuario usuario, Model model) {
        Result result = usuarioDAOImplementation.SearchUsuarioDireccion(usuario);

        model.addAttribute("usuarios", result.Objects);
        return "index";
    }

    @GetMapping("detail/{idUsuario}")
    public String Detail(@PathVariable("idUsuario") int IdUsuario, Model model) {

        Result result = usuarioDAOImplementation.GetById(IdUsuario);

        model.addAttribute("usuario", result.Object);

        return "UsuarioDetail";
    }

    @GetMapping("GetEstados/{IdPais}")
    @ResponseBody
    public Result GetEstadosByPais(@PathVariable int IdPais) {

        Result result = estadoDAOImplementation.GetEstadoByPais(IdPais);

        return result;
    }

    @GetMapping("GetMunicipio/{IdEstado}")
    @ResponseBody
    public Result GetMunicipioByEstado(@PathVariable int IdEstado) {

        Result result = municipioDAOImplementation.GetMunicipioByEstado(IdEstado);

        return result;
    }

    @GetMapping("GetColonia/{IdMunicipio}")
    @ResponseBody
    public Result GetColoniaByMunicipio(@PathVariable int IdMunicipio) {

        Result result = coloniaDAOImplementation.GetColoniaByMunicipio(IdMunicipio);

        return result;

    }

    @GetMapping("/CargaMasiva")
    public String CargaMasiva() {
        return "CargaMasiva";
    }

    @PostMapping("/CargaMasiva")
    public String CargaMasiva(@RequestParam("archivo") MultipartFile archivo, Model model) {
        String nombreArchivo = archivo.getOriginalFilename();
        if (nombreArchivo == null || !nombreArchivo.contains(".")) {
            model.addAttribute("error", "Nombre de archivo inválido");
            return "CargaMasiva";
        }
        String Extencion = archivo.getOriginalFilename().split("\\.")[1];

        //Aqui va lo de guardar el archivos
        String path = System.getProperty("user.dir");
        String pathArchivo = "src/main/resources/archivosCarga";
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmSS"));
        String pathDefinitivo = path + "/" + pathArchivo + "/" + fecha + archivo.getOriginalFilename();

        try {
            archivo.transferTo(new File(pathDefinitivo));
        } catch (Exception ex) {
            model.addAttribute("error al cargar el archivo");
            return "CargaMasiva";
        }

        File archivoGuardado = new File(pathDefinitivo);
        List<Usuario> usuarios = new ArrayList<>();
        List<ErrorCarga> errores = new ArrayList<>();

        try {

            if (Extencion.equals("txt")) {
                usuarios = LecturaArchivoTXT((MultipartFile) archivoGuardado);
                errores = ValidarDatosArchivo(usuarios);

            } else if (Extencion.equals("xlsx")) {
                usuarios = LecturaArchivoXLSX((MultipartFile) archivoGuardado);
                errores = ValidarDatosArchivo(usuarios);

            } else {
                model.addAttribute("Error por la extencion del archivo");
                return "CargaMasiva";

            }

            errores = ValidarDatosArchivo(usuarios);

            if (errores.isEmpty()) {
                model.addAttribute("usuario", usuarios);
                model.addAttribute("mensaje", "Archivo valido, listo para procesar.");
            } else {
                model.addAttribute("errores", errores);
            }

        } catch (Exception ex) {
            model.addAttribute("error", "Error al leer archivo");

        }
        return "CargaMasiva";

    }
    

    public List<ErrorCarga> ValidarDatosArchivo(List<Usuario> usuarios) {

        List<ErrorCarga> erroresCarga = new ArrayList<>();

        int lineaError = 0;

        for (Usuario usuario : usuarios) {
            lineaError++;
            BindingResult bindingResult = validationService.validateObject(usuario);
            List<ObjectError> errors = bindingResult.getAllErrors();
            for (ObjectError error : errors) {
                if (error instanceof FieldError) {
                    FieldError fieldError = (FieldError) error;
                    ErrorCarga errorCarga = new ErrorCarga();
                    errorCarga.campo = fieldError.getField();
                    errorCarga.descripcion = fieldError.getDefaultMessage();
                    errorCarga.linea = lineaError;
                    erroresCarga.add(errorCarga);
                }
            }
        }
        return erroresCarga;
    }

    public List<Usuario> LecturaArchivoTXT(MultipartFile archivo) {
        List<Usuario> usuarios = new ArrayList<>();
        try (InputStream inputStream = archivo.getInputStream(); BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));) {

            String linea = "";

            while ((linea = bufferedReader.readLine()) != null) {
                String[] campos = linea.split("\\|");
                if (campos.length >= 5) { // valida cantidad de columnas mínimas
                    Usuario usuario = new Usuario();
                    usuario.setUserName(campos[0]);
                    usuario.setNombre(campos[1]);
                    usuario.setApellidoPaterno(campos[2]);
                    usuario.setEmail(campos[3]);
                    usuario.setPassword(campos[4]);
                    
                }
            }

            return null;
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return null;
    }

    private List<Usuario> LecturaArchivoXLSX(MultipartFile archivoGuardado) {

        List<Usuario> usuarios = new ArrayList<>();

        try (XSSFWorkbook workBook = new XSSFWorkbook(archivoGuardado.getInputStream())) {
            XSSFSheet workSheet = workBook.getSheetAt(0);

            for (Row row : workSheet) {
                Usuario usuario = new Usuario();

                usuario.setUserName(row.getCell(0).toString());
                usuario.setNombre(row.getCell(1).toString());
                usuario.setApellidoPaterno(row.getCell(2).toString());
                usuario.setApellidoMaterno(row.getCell(3).toString());
                usuario.setEmail(row.getCell(4).toString());
                usuario.setPassword(row.getCell(5).toString());
                usuario.setFechaNacimiento(row.getCell(6).getDateCellValue());
                usuario.setSexo(row.getCell(7).toString().charAt(0));
                usuario.setTelefono(row.getCell(8).toString());
                usuario.setCelular(row.getCell(9).toString());
                usuario.setCURP(row.getCell(10).toString());
                usuario.setImagen(row.getCell(11).toString());

                usuarios.add(usuario);
            }

        } catch (Exception ex) {
            return null;
        }

        return usuarios;

    }

}
