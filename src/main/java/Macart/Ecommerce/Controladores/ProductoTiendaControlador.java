package Macart.Ecommerce.Controladores;

import Macart.Ecommerce.DTO.ProductoTiendaDTO;
import Macart.Ecommerce.Modelos.ProductoTienda;
import Macart.Ecommerce.Modelos.ProductoTiendaCategoriaGenero;

import Macart.Ecommerce.Servicios.ClienteServicio;
import Macart.Ecommerce.Servicios.ProductoTiendaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
            @RequestBody(required = false) ProductoTienda productoTienda,
            Authentication authentication) throws Exception {

        ProductoTienda productoTiendaExistente = productoTiendaServicio.obtenerProductoPorNombre(productoTienda.getNombre());

        if (productoTiendaExistente != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("El producto ya existe.");
        }
        if(productoTienda.getNombre().isEmpty()){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("El nombre no puede estar en blanco");
        }
        if (!Pattern.matches("^[a-z A-Z]+$", productoTienda.getNombre())) {
            return new ResponseEntity<>("El nombre solo puede contener letras", HttpStatus.FORBIDDEN);
        }

        if(productoTienda.getStock() < 1 ){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("El stock no puede ser negativo u cero");
        }

        if(productoTienda.getPrecio() < 0){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("El precio no puede estar en negativo");
        }
        if(productoTienda.getSubCategoria().isBlank()){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("La subcategoria no puede estar en blanco");
        }
        for(String talla : productoTienda.getTallaSuperior()){
            if (!talla.equalsIgnoreCase("XS") && !talla.equalsIgnoreCase("S") && !talla.equalsIgnoreCase("M") &&
                    !talla.equalsIgnoreCase("L") && !talla.equalsIgnoreCase("XL")) {
                return new ResponseEntity<>("Las tallas superiores disponibles son : 'XS','S','M','L','XL'", HttpStatus.FORBIDDEN);
            }
        }

        for(String talla : productoTienda.getTallaInferior()){
            if (!talla.equalsIgnoreCase("S") && !talla.equalsIgnoreCase("M") &&
                    !talla.equalsIgnoreCase("L") && !talla.equalsIgnoreCase("XL")) {
                return new ResponseEntity<>("Las tallas superiores disponibles son : 'S','M','L'", HttpStatus.FORBIDDEN);
            }
        }

        ProductoTienda nuevoProductoTienda = new ProductoTienda(productoTienda.getNombre());

        for(String imagen : productoTienda.getImagenesUrl()){
            if(imagen.isEmpty()){

                return new ResponseEntity<>("Las imagenes estan vacios", HttpStatus.FORBIDDEN);
            }
            if(imagen.endsWith(".png")&&
                    imagen.endsWith(".jpeg")&&
                    imagen.endsWith(".png")){

                return new ResponseEntity<>("Tipo de archivo no permitido", HttpStatus.FORBIDDEN);
            }
        }


        nuevoProductoTienda.setImagenesUrl(productoTienda.getImagenesUrl());
        nuevoProductoTienda.setPrecio(productoTienda.getPrecio());
        nuevoProductoTienda.setDescripcion(productoTienda.getDescripcion());
        nuevoProductoTienda.setSubCategoria(productoTienda.getSubCategoria());
        nuevoProductoTienda.setCategoriaGenero(productoTienda.getCategoriaGenero());
        nuevoProductoTienda.setTallaSuperior(productoTienda.getTallaSuperior());
        nuevoProductoTienda.setTallaInferior(productoTienda.getTallaInferior());
        nuevoProductoTienda.setStock(productoTienda.getStock());
        nuevoProductoTienda.setActivo(true);

        productoTiendaServicio.guardarProducto(nuevoProductoTienda);

        return ResponseEntity.status(HttpStatus.CREATED).body("Se creó el nuevo producto.");
    }

    @PutMapping("/api/productoTienda")
    public ResponseEntity<Object> modificarProductoTienda(
            @RequestBody(required = false) ProductoTienda productoTienda
    ) {
        ProductoTienda productoTiendaExistente = productoTiendaServicio.obtenerProductoPorId(productoTienda.getId());

        if (productoTiendaExistente != null) {
            if (productoTienda.getNombre().isBlank()) {
                return new ResponseEntity<>("El nombre esta vacio", HttpStatus.FORBIDDEN);
            }
            if (!Pattern.matches("^[a-z A-Z]+$", productoTienda.getNombre())) {
                return new ResponseEntity<>("El nombre solo puede contener letras", HttpStatus.FORBIDDEN);
            }
            if (productoTienda.getPrecio() < 0) {
                return new ResponseEntity<>("El precio no puede ser negativo", HttpStatus.FORBIDDEN);
            }
            if (productoTienda.getStock() < 1) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("El stock no puede ser negativo u cero");
            }
            if (productoTienda.getSubCategoria().isBlank()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("La subcategoria no puede estar en blanco");
            }

            for (String talla : productoTienda.getTallaSuperior()) {
                if (!talla.equalsIgnoreCase("XS") && !talla.equalsIgnoreCase("S") && !talla.equalsIgnoreCase("M") &&
                        !talla.equalsIgnoreCase("L") && !talla.equalsIgnoreCase("XL")) {
                    return new ResponseEntity<>("Las tallas superiores disponibles son : 'XS','S','M','L','XL'", HttpStatus.FORBIDDEN);
                }
            }
            for (String talla : productoTienda.getTallaInferior()) {
                if (!talla.equalsIgnoreCase("S") && !talla.equalsIgnoreCase("M") &&
                        !talla.equalsIgnoreCase("L") && !talla.equalsIgnoreCase("XL")) {
                    return new ResponseEntity<>("Las tallas superiores disponibles son : 'S','M','L'", HttpStatus.FORBIDDEN);
                }
                }
            List<String> imagenes = new ArrayList<>();
            for (String imagen : productoTienda.getImagenesUrl()) {
                if (imagen.isEmpty()) {

                    return new ResponseEntity<>("El archivo está vacio", HttpStatus.FORBIDDEN);
                }
                if (imagen.endsWith(".png") &&
                        imagen.endsWith(".jpeg") &&
                        imagen.endsWith(".png")) {

                    return new ResponseEntity<>("Tipo de archivo no permitido", HttpStatus.FORBIDDEN);
                }
                imagenes.add(imagen);
            }
            productoTiendaExistente.setNombre(productoTienda.getNombre());
            productoTiendaExistente.setPrecio(productoTienda.getPrecio());
            productoTiendaExistente.setDescripcion(productoTienda.getDescripcion());
            productoTiendaExistente.setTallaInferior(productoTienda.getTallaInferior());
            productoTiendaExistente.setTallaSuperior(productoTienda.getTallaSuperior());
            productoTiendaExistente.setStock(productoTienda.getStock());
            productoTiendaExistente.setImagenesUrl(productoTienda.getImagenesUrl());
            productoTiendaExistente.setCategoriaGenero(productoTienda.getCategoriaGenero());
            productoTiendaExistente.setSubCategoria(productoTienda.getSubCategoria());

            productoTiendaServicio.guardarProducto(productoTiendaExistente);
            return ResponseEntity.status(HttpStatus.OK).body("Se modificó el producto con exito");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El producto con el nombre " + productoTienda.getNombre() + " no fue encontrado");
    }

    @PatchMapping("/api/productoTienda/{id}")
    public ResponseEntity<Object> eliminarProductoTienda(@PathVariable long id) {
        ProductoTienda productoTiendaExistente = productoTiendaServicio.obtenerProductoPorId(id);

        if (productoTiendaExistente != null) {
            if (productoTiendaExistente.isActivo()){
                productoTiendaServicio.desactivarProducto(productoTiendaExistente);
            }else{
                productoTiendaServicio.activarProducto(productoTiendaExistente);
            }
            return ResponseEntity.status(HttpStatus.OK).body("Se eliminó el producto con el nombre de : " + productoTiendaExistente.getNombre());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El producto con el nombre de : " + productoTiendaExistente.getNombre() + " no fue encontrado");
        }

    }


}
