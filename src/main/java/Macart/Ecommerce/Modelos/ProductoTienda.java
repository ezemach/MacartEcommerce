package Macart.Ecommerce.Modelos;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class ProductoTienda {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String nombre;
    private double precio;
    private String descripcion;
    private int stock;
    @ElementCollection
    private  List<ProductoTiendaTallaSuperior> tallaSuperior = new ArrayList<ProductoTiendaTallaSuperior>();
    @ElementCollection
    private List<ProductoTiendaTallaInferior> tallaInferior = new ArrayList<ProductoTiendaTallaInferior>();
    @ElementCollection
    private List<String> imagenesUrl = new ArrayList<String>();
    private ProductoTiendaCategoriaGenero categoriaGenero;
    private String subCategoria;
    @OneToMany(mappedBy="productoTienda", fetch= FetchType.EAGER)
    private Set<PedidoProducto> pedidoproductos = new HashSet<>();

    public ProductoTienda() {
    }

    public ProductoTienda(String nombre, double precio, String descripcion, List<ProductoTiendaTallaSuperior> tallaSuperior, List<ProductoTiendaTallaInferior> tallaInferior, List<String>imagenesUrl, ProductoTiendaCategoriaGenero categoriaGenero, String subCategoria, int stock) {
        this.nombre = nombre;
        this.precio = precio;
        this.descripcion = descripcion;
        this.tallaSuperior = tallaSuperior;
        this.tallaInferior = tallaInferior;
        this.imagenesUrl = imagenesUrl;
        this.categoriaGenero = categoriaGenero;
        this.subCategoria = subCategoria;
        this.stock = stock;
    }

    public ProductoTienda(String nombre) {
        this.nombre = nombre;
    }

    public void agregarPedidoProducto(PedidoProducto pedidoproducto) {
        pedidoproducto.setProductoTienda(this);
        pedidoproductos.add(pedidoproducto);
    }

    public long getId() {
        return id;
    }

    public int getStock() {
        return stock;
    }

    public void setTallaSuperior(List<ProductoTiendaTallaSuperior> tallaSuperior) {
        this.tallaSuperior = tallaSuperior;
    }

    public void setTallaInferior(List<ProductoTiendaTallaInferior> tallaInferior) {
        this.tallaInferior = tallaInferior;
    }

    public List<ProductoTiendaTallaSuperior> getTallaSuperior() {
        return tallaSuperior;
    }

    public List<ProductoTiendaTallaInferior> getTallaInferior() {
        return tallaInferior;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public List<String> getImagenesUrl() {
        return imagenesUrl;
    }

    public void setImagenesUrl(List<String> imagenesUrl) {
        this.imagenesUrl = imagenesUrl;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public ProductoTiendaCategoriaGenero getCategoriaGenero() {
        return categoriaGenero;
    }

    public void setCategoriaGenero(ProductoTiendaCategoriaGenero categoriaGenero) {
        this.categoriaGenero = categoriaGenero;
    }

    public String getSubCategoria() {
        return subCategoria;
    }

    public void setSubCategoria(String subCategoria) {
        this.subCategoria = subCategoria;
    }

    public Set<PedidoProducto> getPedidoproductos() {
        return pedidoproductos;
    }

    public void setPedidoproductos(Set<PedidoProducto> pedidoproductos) {
        this.pedidoproductos = pedidoproductos;
    }

    public List<String> getImagenenesUrl() {
        return imagenesUrl;
    }

    public void setImagenenesUrl(List<String> imagenenesUrl) {
        this.imagenesUrl = imagenenesUrl;
    }
}
