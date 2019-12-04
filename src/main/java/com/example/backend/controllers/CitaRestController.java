package com.example.backend.controllers;

import java.io.UnsupportedEncodingException;
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
		dni = encriptador.encriptarDni(dni);
		List<Cita> listaCitas = citaService.findCitasByDniPaciente(dni);
		for (Cita cita : citaService.findCitasByDniPaciente(dni)) {
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
	public Cita addCita(@Valid @RequestBody Object object) throws UnsupportedEncodingException {
		Cita cita = (Cita) object;
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
	public Cita modificarFechaCita(@PathVariable("id") ObjectId id, @Valid @RequestBody Object object)
			throws UnsupportedEncodingException {
		Cita cita = (Cita) object;
		cita.set_id(id);
		cita = encriptador.encriptarCita(cita);
		citaService.saveCita(cita);
		return cita;

	}
	
	@GetMapping("/citas/huecoslibres/{dniMedico}")
	public List<String> getHuecos(@PathVariable("dniMedico") String dniMedico, int dia, int mes, int ano) {

		List<String> listaHuecosLibres = new ArrayList<String>();
		Medico medico = usuarioService.findMedicoByDni(dniMedico);
		Horario horario = horarioService.findHorarioByDnimedicoAndDiaAndMesAndAno(dniMedico, dia, mes, ano);
		Especialidad especialidad=especialidadService.findEspecialidadByNombre(medico.getEspecialidad());
		int duracionCita = especialidad.get_duracionCita();
		
		List<Date> listaCitas = horario.getListaCitas();
		
		LocalTime horaInicio = LocalTime.of(7, 00);
		LocalTime horaFin = LocalTime.of(13, 00);	
		horaFin = horaFin.minus(duracionCita, ChronoUnit.MINUTES);
		LocalTime horaIncrementada = horaInicio.plus(duracionCita,ChronoUnit.MINUTES);
		for (LocalTime i = horaInicio; i.equals(horaFin); i=horaIncrementada) {
			for(int j=0; j<listaCitas.size(); j++) {
		    	if(!((listaCitas.get(j).getHours()==i.getHour()) && (listaCitas.get(j).getMinutes()==i.getMinute()))) {
		    		listaHuecosLibres.add(i.toString());	
		    		horaIncrementada=i.plus(duracionCita,ChronoUnit.MINUTES);
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
		return especialidadService.findEspecialidadByNombre(nombreEspecialidad).get_listaMedicos();
	}
	
}
