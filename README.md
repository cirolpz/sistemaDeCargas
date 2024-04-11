
CREAR EN LA BASE DE DATOS LOCAL:
Un schema llamado "datawarehouse" y dentro crear esta tabla:

CREATE TABLE `dispo_alquiler_diaria` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`rubro.nombre` varchar(120) DEFAULT NULL,
`estado` varchar(45) DEFAULT NULL,
`bienEmpresa.nombre` varchar(120) DEFAULT NULL,
`contratoEmpresa.nombre` varchar(120) DEFAULT NULL,
`propietario` varchar(120) DEFAULT NULL,
`contrato.estado` varchar(120) DEFAULT NULL,
`contrato.numeroComp` varchar(120) DEFAULT NULL,
`contrato.fechaFin` date DEFAULT NULL,
`contrato.fecha` date DEFAULT NULL,
`contrato.cliente.nombre` varchar(120) DEFAULT NULL,
`contrato.numero` varchar(120) DEFAULT NULL,
`contrato.fechaInicio` date DEFAULT NULL,
`linea.nombre` varchar(120) DEFAULT NULL,
`propio` varchar(120) DEFAULT NULL,
`enTransito` varchar(120) DEFAULT NULL,
`ordenDeTrabajo.descripcion` text CHARACTER SET utf8,
`ordenDeTrabajo.estado` varchar(120) DEFAULT NULL,
`ordenDeTrabajo.numeroComp` varchar(120) DEFAULT NULL,
`ordenDeTrabajo.fechaEntrega` date DEFAULT NULL,
`ordenDeTrabajo.fechaInicio` date DEFAULT NULL,
`sucursal.nombre` varchar(120) DEFAULT NULL,
`entregado` varchar(120) DEFAULT NULL,
`proveedor.nombre` varchar(120) DEFAULT NULL,
`articulo.codigo` varchar(120) DEFAULT NULL,
`bien.descripcion` varchar(200) CHARACTER SET utf8 DEFAULT NULL,
`bien.estado` varchar(120) DEFAULT NULL,
`bien.aFabricacion` varchar(120) DEFAULT NULL,
`bien.depositoAlmacen.nombre` varchar(120) DEFAULT NULL,
`bien.identificacion` varchar(120) DEFAULT NULL,
`bien.modelo` varchar(120) DEFAULT NULL,
`bien.serie` varchar(120) DEFAULT NULL,
`Fecha_dispo` date DEFAULT NULL,
PRIMARY KEY (`id`)
);

Objetivos:

- Insertar en la tabla los datos provenientes del JSON.
- Fecha_dispo es la fecha actual.
- El programa debe contar con una validación que evite que se cargue el JSON dos veces en el mismo día.
- El programa debe estar preparado para ejecutarse 3 veces por día, la primera para subir el json, la segunda y tercera por si la primera falló al subir, deberá reintentarse. Debe haber una hora de diferencia entre cada comprobación.
