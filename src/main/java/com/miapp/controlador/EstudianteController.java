    package com.miapp.controlador;

import com.miapp.modelo.Estudiante;
import com.miapp.vista.EstudianteView;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
    

/**
 * Controlador: gestiona la lógica entre la Vista y el Modelo.
 * Contiene el array de estudiantes y responde a las búsquedas.
 *
 * IMPORTANTE (MVC): el Controlador es el ÚNICO que conoce tanto la Vista
 * como el Modelo. Es el responsable de traducir objetos Estudiante
 * (Modelo) a Object[] / List<Object[]> (datos "neutros") antes de
 * entregárselos a la Vista. La Vista nunca recibe ni conoce la clase
 * Estudiante directamente.
 */
public class EstudianteController {

    // ── Vista ─────────────────────────────────────────────────────────────────
    private EstudianteView vista;

    // ── Array de estudiantes (fuente de datos) ────────────────────────────────
    private List<Estudiante> estudiantes;
    private List<Estudiante> ultimosResultados;
    private boolean ordenAscendente = true;

    // ── Constructor ───────────────────────────────────────────────────────────

    public EstudianteController(EstudianteView vista) {
        this.vista = vista;
        this.vista.setControlador(this);
        cargarDatos();
    }

    
    
    // ── Carga de datos iniciales ──────────────────────────────────────────────

    /**
     * Inicializa el array de estudiantes con datos de ejemplo.
     * En un proyecto real este array vendría de una base de datos o servicio.
     */
    private void cargarDatos() {
         
            estudiantes = new ArrayList<>();
            estudiantes.add(new Estudiante(1,  "Ana García",        "Ingeniería de Sistemas", 4.5));
            estudiantes.add(new Estudiante(2,  "Carlos López",      "Ingeniería Civil",       3.8));
            estudiantes.add(new Estudiante(3,  "María Rodríguez",   "Medicina",               4.9));
            estudiantes.add(new Estudiante(4,  "José Martínez",     "Derecho",                3.5));
            estudiantes.add(new Estudiante(5,  "Laura Sánchez",     "Administración",         4.1));
            estudiantes.add(new Estudiante(6,  "Andrés Torres",     "Ingeniería de Sistemas", 3.9));
            estudiantes.add(new Estudiante(7,  "Valentina Gómez",   "Psicología",             4.3));
            estudiantes.add(new Estudiante(8,  "Luis Herrera",      "Economía",               3.7));
            estudiantes.add(new Estudiante(9,  "Sofía Díaz",        "Ingeniería Civil",       4.6));
            estudiantes.add(new Estudiante(10, "Juliana Morales",   "Medicina",               4.8));
            estudiantes.add(new Estudiante(11, "Ana Milena Ruiz",   "Derecho",                4.0));
            estudiantes.add(new Estudiante(12, "Carlos Andrés Paz", "Administración",         3.6));
            
            
    }

    // ── Lógica de búsqueda ────────────────────────────────────────────────────

    /**
     * Busca estudiantes cuyo nombre contenga el criterio (sin distinción de mayúsculas).
     * Luego llama a vista.mostrarEstudiante(fila) para una coincidencia,
     * o a vista.mostrarEstudiantes(filas) cuando hay varias.
     *
     * @param criterio texto ingresado por el usuario en la Vista
     */
    public void buscarEstudiante(String criterio) {

        // Validación básica
        if (criterio == null || criterio.isEmpty()) {
            vista.mostrarError("Por favor ingrese un nombre para buscar.");
            return;
        }

        List<Estudiante> resultados = new ArrayList<>();
        String criterioBajo = criterio.toLowerCase();

        for (Estudiante e : estudiantes) {
            if (e.getNombre().toLowerCase().contains(criterioBajo)) {
                resultados.add(e);
            }
        }
        
        ultimosResultados= new ArrayList<>(resultados);
        
        if (resultados.isEmpty()) {
            vista.mostrarEstudiantes(new ArrayList<>()); // mostrará mensaje vacío
        } else if (resultados.size() == 1) {
            // Un solo resultado: se convierte a fila y se usa vista.mostrarEstudiante(fila)
            vista.mostrarEstudiante(convertirAFila(resultados.get(0)));
        } else {
            // Varios resultados: se convierte toda la lista antes de enviarla a la Vista
            vista.mostrarEstudiantes(convertirAFilas(resultados));
        }
    }
    
    //Agregar Estudiante
    public void agregarEstudiante(String nombre, String carrera, double promedio) {

        // Validar que el nombre no esté vacío
        if (nombre == null || nombre.isEmpty()) {
            vista.mostrarError("El nombre no puede estar vacío.");
            return;
        }

        // Validar que el promedio esté entre 0.0 y 5.0
        if (promedio < 0.0 || promedio > 5.0) {
            vista.mostrarError("El promedio debe estar entre 0.0 y 5.0.");
            return;
        }

        // Generar un nuevo ID
        int nuevoId = estudiantes.size() + 1;

        // El Controlador crea el objeto Estudiante
        Estudiante nuevoEstudiante = new Estudiante(
            nuevoId,
            nombre,
            carrera,
            promedio
        );

        // Agregarlo a la lista
        estudiantes.add(nuevoEstudiante);

        // Mostrar confirmación
        vista.mostrarConfirmacion("Estudiante agregado correctamente.");
        
        // Guardar los estudiantes mostrados actualmente
        ultimosResultados = new ArrayList<>(estudiantes);
        
        // Refrescar la tabla mostrando todos
        vista.mostrarEstudiantes(convertirAFilas(estudiantes));
    }
    
    
    public void mostrarTodos() {

    // Guardar todos los estudiantes como últimos resultados
    ultimosResultados = new ArrayList<>(estudiantes);

    // Mostrar todos los estudiantes en la tabla
    vista.mostrarEstudiantes(convertirAFilas(ultimosResultados));
    }
    public void ordenarPor(String criterio) {

    // Verificar si hay resultados para ordenar
    if (ultimosResultados == null || ultimosResultados.isEmpty()) {
        vista.mostrarError("No hay resultados para ordenar.");
        return;
    }

    // Ordenar por nombre
    if (criterio.equals("Nombre")) {

        ultimosResultados.sort(
            Comparator.comparing(
                Estudiante::getNombre,
                String.CASE_INSENSITIVE_ORDER
            )
        );

    // Ordenar por promedio
    } else if (criterio.equals("Promedio")) {

        ultimosResultados.sort(
            Comparator.comparingDouble(Estudiante::getPromedio)
        );
    }

    // Cambiar a descendente si corresponde
    if (!ordenAscendente) {
        java.util.Collections.reverse(ultimosResultados);
    }

    // Mostrar nuevamente los resultados ordenados
    vista.mostrarEstudiantes(convertirAFilas(ultimosResultados));

    // Cambiar el sentido para el próximo clic
    ordenAscendente = !ordenAscendente;
    }
    
    // ── Traducción Modelo → datos para la Vista ───────────────────────────────
    // Estos métodos son el "puente" que evita que la Vista dependa de Estudiante.

    /**
     * Convierte un Estudiante (Modelo) en un arreglo genérico que la Vista
     * puede pintar sin conocer la clase Estudiante.
     */
    private Object[] convertirAFila(Estudiante e) {
        return new Object[]{
            e.getId(),
            e.getNombre(),
            e.getCarrera(),
            String.format("%.2f", e.getPromedio())
        };
    }

    /**
     * Convierte una lista de Estudiante en una lista de filas genéricas.
     */
    private List<Object[]> convertirAFilas(List<Estudiante> lista) {
        List<Object[]> filas = new ArrayList<>();
        for (Estudiante e : lista) {
            filas.add(convertirAFila(e));
        }
        return filas;
    }

   
}