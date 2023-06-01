package Macart.Ecommerce.DTO;

import Macart.Ecommerce.Modelos.*;

import javax.persistence.ElementCollection;
import java.util.List;

public class ProductoTiendaDTO {
    private long id;
    private String nombre;
    private double precio;
    private String descripcion;
    private List<ProductoTiendaTallaSuperior> tallaSuperior;
    private List<ProductoTiendaTallaInferior> tallaInferior;
    private List<String> imagenesUrl;
    private ProductoTiendaCategoriaGenero categoriaGenero;
    private String subCategoria;
    private int stock;
    public ProductoTiendaDTO(ProductoTienda productoTienda) {
        this.id = productoTienda.getId();
        this.nombre = productoTienda.getNombre();
        this.precio = productoTienda.getPrecio();
        this.descripcion = productoTienda.getDescripcion();
        this.tallaSuperior = productoTienda.getTallaSuperior();
        this.tallaInferior = productoTienda.getTallaInferior();
        this.imagenesUrl = productoTienda.getImagenenesUrl();
        this.categoriaGenero = productoTienda.getCategoriaGenero();
        this.subCategoria = productoTienda.getSubCategoria();
        this.stock = productoTienda.getStock();
    }

    public long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public List<ProductoTiendaTallaSuperior> getTallaSuperior() {
        return tallaSuperior;
    }

    public List<ProductoTiendaTallaInferior> getTallaInferior() {
        return tallaInferior;
    }

    public ProductoTiendaCategoriaGenero getCategoriaGenero() {
        return categoriaGenero;
    }

    public String getSubCategoria() {
        return subCategoria;
    }

    public List<String> getImagenesUrl() {
        return imagenesUrl;
    }

    public int getStock() {
        return stock;
    }
}
