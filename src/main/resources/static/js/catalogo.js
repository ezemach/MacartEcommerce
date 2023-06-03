const { createApp } = Vue;

createApp({
	data() {
		return {
			sexo: '',
			categoriaTipo: '',
			check: [],
			checkCategoria: [],
			checkCatalogo: new URLSearchParams(location.search).get('check'),
			productosFiltrados: {},
			rol: '',
			clienteIngresado: '',
			productos: [],
			isCarritoInactivo: true,
			carrito: [],
			carritos: {},
			correo: '',
			correoRegistro: '',
			contraseña: '',
			contraseñaRegistro: '',
			primerNombre: '',
			segundoNombre: '',
			primerApellido: '',
			segundoApellido: '',
			telefono: '',
			clienteId: '',
			token: "",
			verificado: false,
		};
	},
	created() {
		// this.roles();
		this.data();
		this.totalProductos();
		this.clienteId = sessionStorage.getItem('clienteId'); // Obtén el identificador único del cliente desde el sessionStorage
		this.carritos = JSON.parse(localStorage.getItem('carritos')) || {}; // Obtiene los carritos almacenados en el localStorage
		if (!this.carritos[this.clienteId]) {
			this.carritos[this.clienteId] = []; // Crea un carrito vacío para el cliente si no existe
		}
		this.carrito = this.carritos[this.clienteId]; // Asi
	},
	mounted() {
		this.roles();
		this.check.push(this.checkCatalogo);
	},
	methods: {
		totalProductos() {
			axios
				.get('/api/productoTienda')
				.then(response => {
					this.productos = response.data;
					console.log(this.productos);
					this.productosFiltrados = this.productos;
					this.sexo = Array.from(new Set(this.productos.map(sexo => sexo.categoriaGenero)));
					console.log(this.sexo);
					this.categoriaTipo = Array.from(new Set(this.productos.map(tipo => tipo.subCategoria)));
					console.log(this.categoriaTipo);
				})
				.catch(error => console.log(error));
		},

		data() {
			axios
				.get('/api/clientes/actual')
				.then(response => {
					this.datos = response.data;
					this.clienteIngresado = response.data;
					this.clienteId = response.data.id;
					sessionStorage.setItem('clienteId', this.clienteId); // Almacena el identificador único del cliente en el sessionStorage
					if (!this.carritos[this.clienteId]) {
						this.carritos[this.clienteId] = []; // Crea un carrito vacío para el cliente si no existe
					}
					this.carrito = this.carritos[this.clienteId]; // Asigna el carrito correspondiente al cliente actual
					this.verificado = response.data.verificado === true;
				})
				.catch(error => console.log(error));
		},
		roles() {
			axios
				.get('/api/clientes/actual/rol')
				.then(response => {
					this.rol = response.data;
				})
				.catch(error => {
					console.log(error);
				});
		},
		abrirCarrito() {
			if (this.clienteIngresado.verificado == false) {
				Swal.fire('Debes verificar tu cuenta para entrar al carrito de compra, dirigete al inicio para verificarte.')
			} else {
				this.isCarritoInactivo = !this.isCarritoInactivo;
			}
		},
		agregarAlCarrito(item) {
			if (this.rol === 'VISITANTE') {
				Swal.fire('Debes registrarte para poder agregar productos al carrito de compra. Dirígete al inicio para registrarte.');
			} else if (this.clienteIngresado.verificado === false) {
				Swal.fire('Debes verificar tu cuenta para añadir los productos al carrito de compra. Dirígete al inicio para verificar tu cuenta.');
			} else {
				if (this.verificado === true && (this.clienteIngresado.rol === 'CLIENTE' || this.clienteIngresado.rol === 'ADMIN')) {
					if (!this.productosRepetidos(item.id)) {
						this.carrito.push({
							nombre: item.nombre,
							id: item.id,
							contadorBoton: 1,
							imagen: item.imagenesUrl[0],
							precio: item.precio,
						});
					} else {
						item.contadorBoton++;
					}
				}
			}
		},
		productosRepetidos(productoId) {
			return this.carrito.some(item => item.id === productoId);
		},
		agregarCantidadProducto(producto) {
			if (producto.contadorBoton <= 19) {
				producto.contadorBoton += 1;
			}
		},
		disminuirCantidadProducto(producto) {
			if (producto.contadorBoton > 1) {
				producto.contadorBoton -= 1;
			}
		},
		elimarDelCarrito(producto) {
			this.carrito = this.carrito.filter(item => !(item.id === producto.id));
		},
		ingresar() {
			axios
				.post('/api/login', 'correo=' + this.correo + '&contraseña=' + this.contraseña)
				.then(response => {
					Swal.fire({
						icon: 'success',
						text: 'Ingreso Exitoso',
						showConfirmButton: false,
						timer: 2000,
					}).then(() => {
						if (this.correo == 'admin@gmail.com') {
							window.location.replace('/html/catalogo.html');
						} else {
							window.location.replace('/html/catalogo.html');
						}
					});
				})
				.catch(error =>
					Swal.fire({
						icon: 'error',
						text: error.response.data,
						confirmButtonColor: '#7c601893',
					})
				);
		},
		register() {
			axios
				.post(
					'/api/clientes',
					'primerNombre=' +
					this.primerNombre +
					'&segundoNombre=' +
					this.segundoNombre +
					'&primerApellido=' +
					this.primerApellido +
					'&segundoApellido=' +
					this.segundoApellido +
					'&telefono=' +
					this.telefono +
					'&correo=' +
					this.correoRegistro +
					'&contraseña=' +
					this.contraseñaRegistro
				)
				.then(response => {
					this.correo = this.correoRegistro;
					this.contraseña = this.contraseñaRegistro;
					this.ingresar();
				})
				.catch(error =>
					Swal.fire({
						icon: 'error',
						text: error.response.data,
						confirmButtonColor: '#7c601893',
					})
				);
		},

		salir() {
			Swal.fire({
				title: '¿Estas seguro que quieres salir de tu cuenta?',
				inputAttributes: {
					autocapitalize: 'off',
				},
				showCancelButton: true,
				cancelButtonText: 'Cancelar',
				confirmButtonText: 'Salir',
				showLoaderOnConfirm: true,
				preConfirm: login => {
					return axios
						.post('/api/logout')
						.then(response => {
							window.location.href = '/index.html';
						})
						.catch(error =>
							Swal.fire({
								icon: 'error',
								text: error.response.data,
								confirmButtonColor: '#7c601893',
							})
						);
				},
				allowOutsideClick: () => !Swal.isLoading(),
			});
		},
	},
	computed: {
		primeraMayuscula() {
			this.primerNombre = this.primerNombre.charAt(0).toUpperCase() + this.primerNombre.slice(1);
			this.segundoNombre = this.segundoNombre.charAt(0).toUpperCase() + this.segundoNombre.slice(1);
			this.primerApellido = this.primerApellido.charAt(0).toUpperCase() + this.primerApellido.slice(1);
			this.segundoApellido = this.segundoApellido.charAt(0).toUpperCase() + this.segundoApellido.slice(1);
		},
		guardarDatos() {
			this.carritos[this.clienteId] = this.carrito;
			localStorage.setItem('carritos', JSON.stringify(this.carritos));
		},
		totalDelCarrito() {
			return this.carrito.reduce((acc, currentValue) => {
				acc += currentValue.precio * currentValue.contadorBoton;
				return acc;
			}, 0);
		},
		filtroCruzados() {
			this.productosFiltrados = this.productos.filter(producto => {
				return (
					producto.nombre.toLowerCase() &&
					((this.check.includes(producto.categoriaGenero) && (this.checkCategoria.includes(producto.subCategoria) || this.checkCategoria == 0)) ||
						this.check == 0)
				);
			});
		},
		filtrosCategoria() {
			this.productosFiltrados = this.productos.filter(producto => {
				return producto.nombre.toLowerCase() && (this.checkCategoria.includes(producto.subCategoria) || this.checkCategoria == 0);
			});
		},
	},
}).mount('#app');
