package com.puerta18.enclase;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.puerta18.model.Socio;

@Controller
public class SociosController {

	@Autowired
	private Environment env;

	// landing page, muestra un formulario de busqueda
	// y tambien muestra los resultados con un parametro no requerido
	@GetMapping("/")
	public String landing() {
		return "index";
	}

	@GetMapping("/socios/nuevo") // formulario de alta vacio
	public String nuevo() {
		return "nuevosocio";
	}

	@GetMapping("/socios/nuevo/procesar")
	public String insertarNuevoCurso(@RequestParam String nombre, @RequestParam String apellido,
			@RequestParam String email, @RequestParam String dni,@RequestParam  boolean presente,@RequestParam String celular,
			@RequestParam String telefono,@RequestParam String telefono2,@RequestParam String direccion,@RequestParam String genero,@RequestParam Date fecha_de_nacimiento) throws SQLException {

		Connection connection; // Usar el import de java.sql

		connection = DriverManager.getConnection(env.getProperty("spring.datasource.url"),
				env.getProperty("spring.datasource.username"), env.getProperty("spring.datasource.password"));

		PreparedStatement consulta = connection
				.prepareStatement("INSERT INTO socios(nombre, apellido, email, dni, presente, celular, telefono, telefono2, direccion, genero, fecha_de_nacimiento  ) VALUES(?, ?, ?, ?, ? , ? , ?, ? , ? ,? , ? );");

		consulta.setString(1, nombre);
		consulta.setString(2, apellido);
		consulta.setString(3, email);
		consulta.setString(4, dni);
		consulta.setBoolean(5, presente);
		consulta.setString(6, celular);
		consulta.setString(7, telefono);
		consulta.setString(8, telefono2);
		consulta.setString(9, direccion);
		consulta.setString(10, genero);
		consulta.setDate(11, fecha_de_nacimiento);
		

		consulta.execute();
		connection.close();

		return "redirect:/";
	}

	@GetMapping("/socios/busqueda") // inserta nuevos socios
	public String insertarnuevo(@RequestParam(required = false) String buscar, Model template) throws SQLException {

		if (buscar == null)

		{

			buscar = "";

		}

		// buscar= buscar.toLowerCase();

		Connection connection;

		connection = DriverManager.getConnection(

				env.getProperty("spring.datasource.url"),

				env.getProperty("spring.datasource.username"),

				env.getProperty("spring.datasource.password"));

		PreparedStatement consulta = connection.prepareStatement(
				"SELECT * FROM socios WHERE LOWER(unaccent(nombre)) LIKE LOWER(unaccent(?)) OR  LOWER(unaccent(apellido)) LIKE  LOWER(unaccent(?)) OR  LOWER(email) LIKE  LOWER(?) OR  dni=?");

		consulta.setString(1, "%" + buscar + "%");

		consulta.setString(2, "%" + buscar + "%");

		consulta.setString(3, "%" + buscar + "%");

		consulta.setString(4, "%" + buscar + "%");

		ResultSet resultados = consulta.executeQuery();

		ArrayList<Socio> lossocio = new ArrayList<Socio>();

		System.out.println("hola2");
		while (resultados.next())

		{
			System.out.println("hola");
			int id = resultados.getInt("id");

			String nombre = resultados.getString("nombre");

			String apellido = resultados.getString("apellido");

			String email = resultados.getString("email");
			String dni = resultados.getString("dni");

			String celular = resultados.getString("celular");
			String telefono = resultados.getString("telefono");
		    String telefono2 = resultados.getString("telefono2");
			String direccion = resultados.getString("direccion");
			String genero = resultados.getString("genero");
			String localidad = resultados.getString("localidad");
			boolean presente = resultados.getBoolean("presente");
			Date fecha_de_nacimiento = resultados.getDate("fecha_de_nacimiento");

			Socio elsocio = new Socio(id,nombre,apellido,email,dni,presente,celular,telefono,telefono2,direccion,genero,fecha_de_nacimiento);

			lossocio.add(elsocio);
		}

		template.addAttribute("socio", lossocio);

		connection.close();

		return "busqueda";

	}

	@GetMapping("/socios/checkin/{id}") //
	public String checkIn(@PathVariable int id, Model template) throws SQLException {

		Connection connection; // Usar el import de java.sql

		connection = DriverManager.getConnection(env.getProperty("spring.datasource.url"),
				env.getProperty("spring.datasource.username"), env.getProperty("spring.datasource.password"));

		PreparedStatement consulta = connection
				.prepareStatement("INSERT INTO checks(id_socio , momento, tipo ) VALUES(?, NOW(),'IN');");
		PreparedStatement consulta2 = connection.prepareStatement("UPDATE socios SET presente=true WHERE id=?");

		consulta.setInt(1, id);
		consulta2.setInt(1, id);

		consulta.execute();
		consulta2.execute();
		connection.close();

		return "redirect:/";
	}

	@GetMapping("/socios/checkout/{id}") //
	public String checkOut(@PathVariable int id, Model template) throws SQLException {

		Connection connection; // Usar el import de java.sql

		connection = DriverManager.getConnection(env.getProperty("spring.datasource.url"),
				env.getProperty("spring.datasource.username"), env.getProperty("spring.datasource.password"));

		PreparedStatement consulta = connection
				.prepareStatement("INSERT INTO checks(id_socio , momento, tipo ) VALUES(?, NOW(),'out');");
		PreparedStatement consulta2 = connection.prepareStatement("UPDATE  socios SET presente=false WHERE id=?");

		consulta.setInt(1, id);
		consulta2.setInt(1, id);

		consulta.execute();
		consulta2.execute();
		connection.close();

		return "redirect:/";
	}

	// estas rutas mas adelante vamos a protegerlas con usuario y contraseña
	// @GetMapping("/socios/mostrar/{id}") // muestra el detalle completo de un
	// socio
	// @GetMapping("/socios/listado") // muestra el listado completo sin paginacion,
	// por ahora

	// @GetMapping("/socios/modificar/{id}")
	// @GetMapping("/socios/modificar/procesar/{id}")
}
