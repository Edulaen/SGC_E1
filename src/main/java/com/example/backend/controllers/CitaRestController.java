package com.example.backend.controllers;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.validation.Valid;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.models.entity.Cita;
import com.example.backend.models.entity.Especialidad;
import com.example.backend.models.entity.Horario;
import com.example.backend.models.entity.Medico;
import com.example.backend.models.entity.Usuario;
import com.example.backend.models.services.ICitaService;
import com.example.backend.models.services.IEspecialidadService;
import com.example.backend.models.services.IHorarioService;
import com.example.backend.models.services.IUsuarioService;
import com.example.backend.models.utiles.Encriptador;

//@CrossOrigin(value = "https://sgcequipo1.herokuapp.com") 
@CrossOrigin(value = "http://localhost:4200") // PARA DESARROLLO
@RestController
@RequestMapping("api")
public class CitaRestController {

	@Autowired
	private ICitaService citaService;

	@Autowired
	private IUsuarioService usuarioService;

	@Autowired
	private IHorarioService horarioService;
	
	@Autowired
	private IEspecialidadService especialidadService;

	private SecretKey key;
	private Cipher cipher;
	private String algoritmo = "AES";
	private int keysize = 16;
	private String clave = "seguridad";

	private Encriptador encriptador = new Encriptador(key, cipher, algoritmo, keysize, clave);
	
	/**
	 * obtener todas las citas
	 * @return citas
	 */
	@GetMapping("/citas")
	public List<Cita> getAllCitas() {
		return citaService.findAll();
	}

	/**
	 * obtener las citas del usuario en concreto
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("/citas/fecha/{id}")
	public Cita getCitaByid(@PathVariable("id") String id) {
		return citaService.findCitaById(id);
	}

	/**
	 * obtener las citas del usuario en concreto
	 * 
	 * @param id
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@GetMapping("/citas/paciente/{dni}")
	public List<Cita> getCitasPacienteByid(@PathVariable("dni") String dni) throws UnsupportedEncodingException {
		List<Cita> listaCitas = new ArrayList<Cita>();
		dni = encriptador.encriptarDni(dni);
		listaCitas = citaService.findCitasByDniPaciente(dni);
		for (Cita cita : listaCitas) {
			encriptador.desencriptarCita(cita);
		}
		return listaCitas;
	}

	@GetMapping("/citas/medico/{dni}")
	public List<Cita> getCitasMedicoByid(@PathVariable("dni") String dni) {
		return citaService.findCitasByDniMedico(dni);
	}

	/**
	 * add citas a un usuario
	 * 
	 * @param cita
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@PostMapping("/citas")
	public Cita addCita(@Valid @RequestBody Cita cita) throws UnsupportedEncodingException {
		cita.set_id(ObjectId.get());
		encriptador.encriptarCita(cita);
		citaService.saveCita(cita);
		return cita;
	}

	/**
	 * borra citas a un usuario
	 * 
	 * @param cita
	 */
	@DeleteMapping("/citas/{id}")
	public void deleteCita(@PathVariable("id") String id) {
		citaService.deleteCita(id);
	}

	/**
	 * Modificar la fecha de la cita
	 * 
	 * @param teléfono
	 * @param modificarDatosContacto
	 * @throws UnsupportedEncodingException
	 */
	@PutMapping("/citas/{id}")
	public Cita modificarFechaCita(@PathVariable("id") ObjectId id, @Valid @RequestBody Cita cita)
			throws UnsupportedEncodingException {
		cita.set_id(id);
		cita = encriptador.encriptarCita(cita);
		citaService.saveCita(cita);
		return cita;

	}

	/*
	 * //RespuestaHuecos?? List<String/int/date> getHuecos(dniMedico, dia, mes,
	 * año)//fecha separada o completa{ List huecosLibres;
	 * medico=medicoService.findByDni(dniMedico) //si se pone la duración de la cita
	 * en Medico o en especialidad
	 * 
	 * horario=horarioService.findByDniAnd....(dniMedico, etc...)
	 * duracionCita=horario.getDuracionCita() String[]/ArrayList<Date>
	 * listaHoras=horario.getListaCitas()
	 * 
	 * for(i=hora_minima; i<=hora_maxima-duracioncita; i+=duracionCita){
	 * if(!listaCitas.contains(i)){ huecosLibres.add(i); } }
	 * 
	 * return huecosLibres;
	 * 
	 * }
	 * 
	 * 
	 */
	
	///Comprobar getMapping
	@GetMapping("/citas/huecoslibres/{dniMedico}")
	public ArrayList<String> getHuecos(@PathVariable("dniMedico") String dniMedico, int dia, int mes, int ano) {

		ArrayList<String> listaHuecosLibres = new ArrayList<String>();
		Medico medico = usuarioService.findMedicoByDni(dniMedico);
		Horario horario = horarioService.findHorarioByDnimedicoAndDiaAndMesAndAno(dniMedico, dia, mes, ano);
		Especialidad especialidad=especialidadService.findEspecialidadByNombre(medico.getEspecialidad());
		int duracionCita = especialidad.get_duracionCita();
		
		ArrayList<Date> listaCitas = horario.getListaCitas();
		
		LocalTime horaInicio = LocalTime.of(7, 00);
		LocalTime horaFin = LocalTime.of(13, 00);	
		horaFin = horaFin.minus(duracionCita, ChronoUnit.MINUTES);
		
		for (LocalTime i = horaInicio; i.equals(horaFin); i.plus(duracionCita, ChronoUnit.MINUTES)) {
			for(int j=0; j<listaCitas.size(); j++) {
		    	if(!((listaCitas.get(j).getHours()==i.getHour()) && (listaCitas.get(j).getMinutes()==i.getMinute()))) {
		    		listaHuecosLibres.add(i.toString());	zz
		    	}
		    }
		}
		return listaHuecosLibres;
	}

    
    
    /**
     * @return especialidades
     */
    @GetMapping("/citas/especialidades")
	public List<Especialidad> getAllEspecialidades() {
		return especialidadService.findAll();
	}
	
	/**
     * obtener los medicos de una especialidad
     * @param id
     * @return
     */
	@GetMapping("/citas/especialidades/{nombreEspecialidad}")
	public String[] getEspecialidadesByid(@PathVariable ("nombreEspecialidad") String nombreEspecialidad){
		Especialidad especialidad = new Especialidad();
		especialidad = especialidadService.findEspecialidadByNombre(nombreEspecialidad);
		return especialidad.get_listaMedicos();
	}
	
}
