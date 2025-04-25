
// Variables globales
let clientes = [];
let habitaciones = [];
let reservas = [];

// Funciones para manejar las pestañas
function openTab(tabName) {
    const tabs = document.getElementsByClassName('tab-content');
    for (let i = 0; i < tabs.length; i++) {
        tabs[i].classList.remove('active');
    }

    const tabButtons = document.getElementsByClassName('tab');
    for (let i = 0; i < tabButtons.length; i++) {
        tabButtons[i].classList.remove('active');
    }

    document.getElementById(tabName).classList.add('active');
    event.currentTarget.classList.add('active');

    // Cargar datos cuando se cambia de pestaña
    if (tabName === 'clientes') cargarClientes();
    if (tabName === 'habitaciones') cargarHabitaciones();
    if (tabName === 'reservas') cargarReservas();
}

// ===== CLIENTES =====
function mostrarFormularioCliente() {
    document.getElementById('formCliente').style.display = 'block';
}

function ocultarFormularioCliente() {
    document.getElementById('formCliente').style.display = 'none';
    document.getElementById('clienteForm').reset();
}

async function cargarClientes() {
    try {
        const response = await fetch('http://localhost:8080/clientes');
        clientes = await response.json();
        renderizarClientes();
    } catch (error) {
        console.error('Error al cargar clientes:', error);
    }
}

function renderizarClientes() {
    const tbody = document.querySelector('#tablaClientes');
    tbody.innerHTML = '';

    clientes.forEach(cliente => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${cliente.id}</td>
            <td>${cliente.nombre}</td>
            <td>${cliente.email}</td>
            <td>
                <button onclick="eliminarCliente(${cliente.id})" class="btn btn-sm btn-danger">Eliminar</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

async function agregarCliente(cliente) {
    try {
        const response = await fetch('http://localhost:8080/clientes', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(cliente)
        });

        if (response.ok) {
            await cargarClientes();
            document.getElementById('clienteForm').reset();
            ocultarFormularioCliente();
        }
    } catch (error) {
        console.error('Error al agregar cliente:', error);
    }
}

async function eliminarCliente(id) {
    if (confirm('¿Está seguro de eliminar este cliente?')) {
        try {
            const response = await fetch(`http://localhost:8080/clientes/${id}`, {
                method: 'DELETE'
            });

            if (response.ok) {
                await cargarClientes();
            }
        } catch (error) {
            console.error('Error al eliminar cliente:', error);
        }
    }
}

// ===== HABITACIONES =====
function mostrarFormularioHabitacion() {
    document.getElementById('formHabitacion').style.display = 'block';
}

function ocultarFormularioHabitacion() {
    document.getElementById('formHabitacion').style.display = 'none';
    document.getElementById('habitacionForm').reset();
}

async function cargarHabitaciones() {
    try {
        const response = await fetch('http://localhost:8080/habitaciones');
        habitaciones = await response.json();
        renderizarHabitaciones();
    } catch (error) {
        console.error('Error al cargar habitaciones:', error);
    }
}

function renderizarHabitaciones() {
    const tbody = document.querySelector('#tablaHabitaciones tbody');
    tbody.innerHTML = '';

    habitaciones.forEach(habitacion => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${habitacion.id}</td>
            <td>${habitacion.numero}</td>
            <td>${habitacion.tipo}</td>
            <td>$${habitacion.precioPorNoche.toFixed(2)}</td>
            <td>
                <button onclick="eliminarHabitacion(${habitacion.id})" class="btn btn-sm btn-danger">Eliminar</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

async function agregarHabitacion(habitacion) {
    try {
        const response = await fetch('http://localhost:8080/habitaciones', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(habitacion)
        });

        if (response.ok) {
            await cargarHabitaciones();
            document.getElementById('habitacionForm').reset();
            ocultarFormularioHabitacion();
        }
    } catch (error) {
        console.error('Error al agregar habitación:', error);
    }
}

async function eliminarHabitacion(id) {
    if (confirm('¿Está seguro de eliminar esta habitación?')) {
        try {
            const response = await fetch(`http://localhost:8080/habitaciones/${id}`, {
                method: 'DELETE'
            });

            if (response.ok) {
                await cargarHabitaciones();
            }
        } catch (error) {
            console.error('Error al eliminar habitación:', error);
        }
    }
}

// ===== RESERVAS =====
async function cargarReservas() {
    try {
        // Primero cargamos clientes y habitaciones para los selects
        await cargarClientes();
        await cargarHabitaciones();

        // Luego cargamos las reservas
        const response = await fetch('http://localhost:8080/reservas');
        reservas = await response.json();
        renderizarReservas();
    } catch (error) {
        console.error('Error al cargar reservas:', error);
    }
}

function renderizarReservas() {
    const tbody = document.querySelector('#tablaReservas tbody');
    tbody.innerHTML = '';

    reservas.forEach(reserva => {
        const cliente = clientes.find(c => c.id === reserva.clienteId);
        const habitacion = habitaciones.find(h => h.id === reserva.habitacionId);

        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${reserva.id}</td>
            <td>${cliente ? cliente.nombre : 'Cliente no encontrado'}</td>
            <td>${habitacion ? habitacion.numero + ' (' + habitacion.tipo + ')' : 'Habitación no encontrada'}</td>
            <td>${new Date(reserva.fechaInicio).toLocaleDateString()} - ${new Date(reserva.fechaFin).toLocaleDateString()}</td>
            <td>$${reserva.total ? reserva.total.toFixed(2) : '0.00'}</td>
            <td>
                <button onclick="eliminarReserva(${reserva.id})" class="btn btn-sm btn-danger">Eliminar</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

async function cargarDatosParaReserva() {
    try {
        const [clientesResponse, habitacionesResponse] = await Promise.all([
            fetch('http://localhost:8080/clientes'),
            fetch('http://localhost:8080/habitaciones')
        ]);

        const clientes = await clientesResponse.json();
        const habitaciones = await habitacionesResponse.json();

        const selectCliente = document.getElementById('clienteReserva');
        const selectHabitacion = document.getElementById('habitacionReserva');

        selectCliente.innerHTML = '';
        selectHabitacion.innerHTML = '';

        // Agregar opción por defecto
        const defaultOption = document.createElement('option');
        defaultOption.value = '';
        defaultOption.textContent = '-- Seleccione --';
        defaultOption.disabled = true;
        defaultOption.selected = true;
        selectCliente.appendChild(defaultOption.cloneNode(true));
        selectHabitacion.appendChild(defaultOption.cloneNode(true));

        // Llenar clientes
        clientes.forEach(cliente => {
            const option = document.createElement('option');
            option.value = cliente.id;
            option.textContent = `${cliente.nombre} (${cliente.email})`;
            selectCliente.appendChild(option);
        });

        // Llenar habitaciones
        habitaciones.forEach(habitacion => {
            const option = document.createElement('option');
            option.value = habitacion.id;
            option.textContent = `${habitacion.numero} - ${habitacion.tipo} ($${habitacion.precioPorNoche.toFixed(2)})`;
            selectHabitacion.appendChild(option);
        });
    } catch (error) {
        console.error('Error al cargar datos para reserva:', error);
    }
}

function mostrarFormularioReserva() {
    cargarDatosParaReserva();
    document.getElementById('formReserva').style.display = 'block';
}

function ocultarFormularioReserva() {
    document.getElementById('formReserva').style.display = 'none';
    document.getElementById('reservaForm').reset();
}

async function agregarReserva(reserva) {
    try {
        const response = await fetch('http://localhost:8080/reservas', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(reserva)
        });

        if (response.ok) {
            await cargarReservas();
            document.getElementById('reservaForm').reset();
            ocultarFormularioReserva();
        }
    } catch (error) {
        console.error('Error al agregar reserva:', error);
    }
}

async function eliminarReserva(id) {
    if (confirm('¿Está seguro de eliminar esta reserva?')) {
        try {
            const response = await fetch(`http://localhost:8080/reservas/${id}`, {
                method: 'DELETE'
            });

            if (response.ok) {
                await cargarReservas();
            }
        } catch (error) {
            console.error('Error al eliminar reserva:', error);
        }
    }
}

// Event Listeners
document.addEventListener('DOMContentLoaded', () => {
    // Configurar event listeners para formularios
    document.getElementById('clienteForm').addEventListener('submit', function(e) {
        e.preventDefault();
        const nombre = document.getElementById('nombreCliente').value;
        const email = document.getElementById('emailCliente').value;

        agregarCliente({ nombre, email });
    });

    document.getElementById('habitacionForm').addEventListener('submit', function(e) {
        e.preventDefault();
        const numero = document.getElementById('numeroHabitacion').value;
        const tipo = document.getElementById('tipoHabitacion').value;
        const precioPorNoche = parseFloat(document.getElementById('precioHabitacion').value);

        agregarHabitacion({ numero, tipo, precioPorNoche });
    });

    document.getElementById('reservaForm').addEventListener('submit', function(e) {
        e.preventDefault();
        const clienteId = parseInt(document.getElementById('clienteReserva').value);
        const habitacionId = parseInt(document.getElementById('habitacionReserva').value);
        const fechaInicio = document.getElementById('fechaInicio').value;
        const fechaFin = document.getElementById('fechaFin').value;

        agregarReserva({ clienteId, habitacionId, fechaInicio, fechaFin });
    });

    // Cargar datos iniciales
    cargarClientes();
});