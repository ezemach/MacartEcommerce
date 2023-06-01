package Macart.Ecommerce.Controladores;

import Macart.Ecommerce.DTO.ProductoTiendaDTO;
import Macart.Ecommerce.Modelos.ProductoTienda;
import Macart.Ecommerce.Modelos.ProductoTiendaCategoriaGenero;
import Macart.Ecommerce.Modelos.ProductoTiendaTallaInferior;
import Macart.Ecommerce.Modelos.ProductoTiendaTallaSuperior;

import Macart.Ecommerce.Servicios.ClienteServicio;
import Macart.Ecommerce.Servicios.ProductoTiendaServicio;
import Macart.Ecommerce.Utilidades.ProductoTiendaUtilidades;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


@RestController
public class ProductoTiendaControlador {

    @Autowired
    private ProductoTiendaServicio productoTiendaServicio;
    @Autowired
    private ClienteServicio clienteServicio;

    @GetMapping("/api/productoTienda")
    public ResponseEntity<Object> obtenerPedidoProductoTienda() {
        return new ResponseEntity<>(productoTiendaServicio.obtenerTodosLosProductos(),HttpStatus.ACCEPTED);
    }

    @GetMapping("/api/productoTienda/{id}")
    public ResponseEntity<Object> obtenerProductoTiendaPorId(@PathVariable Long id) {
        ProductoTienda productoTienda = productoTiendaServicio.obtenerProductoPorId(id);

        if (productoTienda != null) {
            ProductoTiendaDTO productoTiendaDTO = new ProductoTiendaDTO(productoTienda);
            return ResponseEntity.ok(productoTiendaDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró el producto");
        }
    }


    @PostMapping("/api/productoTienda")
    public ResponseEntity<Object> crearNuevoProductoTienda(
            @RequestParam String nombre,
            @RequestParam double precio,
            @RequestParam String descripcion,
            @RequestParam(required = false) List<ProductoTiendaTallaSuperior> tallaSuperior,
            @RequestParam(required = false) List<ProductoTiendaTallaInferior> tallaInferior,
            @RequestParam(required = false) List<String> imagenesUrl,
            @RequestParam ProductoTiendaCategoriaGenero categoriaGenero,
            @RequestParam String subCategoria,
            @RequestParam int stock,
            Authentication authentication) throws Exception {

        ProductoTienda productoTiendaExistente = productoTiendaServicio.obtenerProductoPorNombre(nombre);

        if (productoTiendaExistente != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("El producto ya existe.");
        }
        if(nombre.isBlank()){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("El nombre no puede estar en blanco");
        }
        if (!Pattern.matches("^[a-z A-Z]+$", nombre)) {
            return new ResponseEntity<>("El nombre solo puede contener letras", HttpStatus.FORBIDDEN);
        }

        if(stock < 1 ){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("El stock no puede ser negativo u cero");
        }

        if(precio < 0){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("El precio no puede estar en negativo");
        }
        if(subCategoria.isBlank()){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("La subcategoria no puede estar en blanco");
        }

        ProductoTienda nuevoProductoTienda = new ProductoTienda(nombre);

        List<String> imagenes = new ArrayList<>();
        for(String imagen : imagenesUrl){
            if(imagen.isEmpty()){

                return new ResponseEntity<>("Las imagenes estan vacios", HttpStatus.FORBIDDEN);
            }
            if(imagen.endsWith(".png")&&
                    imagen.endsWith(".jpeg")&&
                    imagen.endsWith(".png")){

                return new ResponseEntity<>("Tipo de archivo no permitido", HttpStatus.FORBIDDEN);
            }
            imagenes.add(imagen);
        }

        nuevoProductoTienda.setImagenenesUrl(imagenes);
        nuevoProductoTienda.setPrecio(precio);
        nuevoProductoTienda.setDescripcion(descripcion);
        nuevoProductoTienda.setSubCategoria(subCategoria);
        nuevoProductoTienda.setCategoriaGenero(categoriaGenero);
        nuevoProductoTienda.setStock(stock);

        productoTiendaServicio.guardarProducto(nuevoProductoTienda);

        return ResponseEntity.status(HttpStatus.CREATED).body("Se creó el nuevo producto.");
    }

    @PutMapping("/api/productoTienda")
    public ResponseEntity<Object> modificarProductoTienda(
            @RequestParam long id,
            @RequestParam String nombre,
            @RequestParam double precio,
            @RequestParam String descripcion,
            @RequestParam(required = false) List<ProductoTiendaTallaSuperior> tallaSuperior,
            @RequestParam(required = false) List<ProductoTiendaTallaInferior> tallaInferior,
            @RequestParam(required = false) List<String> imagenesUrl,
            @RequestParam String categoriaGenero,
            @RequestParam String subCategoria,
            @RequestParam int stock
    ) {
        ProductoTienda productoTiendaExistente = productoTiendaServicio.obtenerProductoPorId(id);

        if (productoTiendaExistente != null) {
            if(nombre.isBlank()){
                return new ResponseEntity<>("El nombre esta vacio", HttpStatus.FORBIDDEN);
            }
            if (!Pattern.matches("^[a-z A-Z]+$", nombre)) {
                return new ResponseEntity<>("El nombre solo puede contener letras", HttpStatus.FORBIDDEN);
            }
            if (precio < 0 ){
                return new ResponseEntity<>("El precio no puede ser negativo", HttpStatus.FORBIDDEN);
            }
            if(stock < 1 ){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("El stock no puede ser negativo u cero");
            }
            if(subCategoria.isBlank()){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("La subcategoria no puede estar en blanco");
            }
            productoTiendaExistente.setNombre(nombre);
            productoTiendaExistente.setPrecio(precio);
            productoTiendaExistente.setDescripcion(descripcion);
            productoTiendaExistente.setTallaSuperior(tallaSuperior);
            productoTiendaExistente.setTallaInferior(tallaInferior);
            productoTiendaExistente.setStock(stock);

            List<String> imagenes = new ArrayList<>();
            for(String imagen : imagenesUrl){
                if(imagen.isEmpty()){

                    return new ResponseEntity<>("El archivo está vacio", HttpStatus.FORBIDDEN);
                }
                if(imagen.endsWith(".png")&&
                        imagen.endsWith(".jpeg")&&
                        imagen.endsWith(".png")){

                    return new ResponseEntity<>("Tipo de archivo no permitido", HttpStatus.FORBIDDEN);
                }
                imagenes.add(imagen);
            }
            productoTiendaExistente.setImagenenesUrl(imagenes);
            productoTiendaExistente.setCategoriaGenero(ProductoTiendaCategoriaGenero.valueOf(categoriaGenero));
            productoTiendaExistente.setSubCategoria(subCategoria);

            productoTiendaServicio.guardarProducto(productoTiendaExistente);

            return ResponseEntity.status(HttpStatus.CREATED).body("Se modificó el producto con el nombre de: " + nombre);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El producto con el nombre " + nombre + " no fue encontrado");
        }
    }

//    private <T extends Enum<T>> T toEnum(Class<T> enumClass, String value) {
//        if (value != null) {
//            try {
//                return Enum.valueOf(enumClass, value.toUpperCase());
//            } catch (IllegalArgumentException e) {
//                // Manejar el error si el valor proporcionado no coincide con ninguna constante del enumerado
//            }
//        }
//        return null;
//    }

    @DeleteMapping("/api/productoTienda")
    public ResponseEntity<Object> eliminarProductoTienda(@RequestParam long id) {
        ProductoTienda productoTiendaExistente = productoTiendaServicio.obtenerProductoPorId(id);

        if (productoTiendaExistente != null) {
            productoTiendaServicio.borrarProducto(productoTiendaExistente);
            return ResponseEntity.status(HttpStatus.OK).body("Se eliminó el producto con el nombre de : " + productoTiendaExistente.getNombre());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El producto con el nombre de : " + productoTiendaExistente.getNombre() + " no fue encontrado");
        }
    }


}
