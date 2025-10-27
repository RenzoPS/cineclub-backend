#!/bin/bash

# Script para poblar la base de datos con datos de prueba
# Asegúrate de que el servidor esté corriendo en http://localhost:8080

BASE_URL="http://localhost:8080"
ADMIN_NAME="admin"
ADMIN_EMAIL="admin@gmail.com"
ADMIN_PASSWORD="admin123"

echo "=========================================="
echo "Poblando base de datos de CineClub"
echo "=========================================="
echo "Usuario Admin: $ADMIN_EMAIL"
echo ""

# Colores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

post_request() {
    local endpoint=$1
    local data=$2
    local description=$3
    
    response=$(curl -s -w "\n%{http_code}" -X POST "${BASE_URL}${endpoint}" \
        -H "Content-Type: application/json" \
        -u "${ADMIN_EMAIL}:${ADMIN_PASSWORD}" \
        -d "$data")
    
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')
    
    if [ "$http_code" -ge 200 ] && [ "$http_code" -lt 300 ]; then
        echo -e "${GREEN}✓${NC} $description"
    else
        echo -e "${RED}✗${NC} Error en $description (HTTP $http_code)"
        if [ ! -z "$body" ]; then
            echo "   Respuesta: $body"
        fi
    fi
}

echo ""
echo "1. Creando 20 Salas..."
post_request "/api/rooms" '{"number": 1, "capacity": 150}' "Sala 1"
post_request "/api/rooms" '{"number": 2, "capacity": 150}' "Sala 2"
post_request "/api/rooms" '{"number": 3, "capacity": 120}' "Sala 3"
post_request "/api/rooms" '{"number": 4, "capacity": 120}' "Sala 4"
post_request "/api/rooms" '{"number": 5, "capacity": 100}' "Sala 5"
post_request "/api/rooms" '{"number": 6, "capacity": 100}' "Sala 6"
post_request "/api/rooms" '{"number": 7, "capacity": 80}' "Sala 7"
post_request "/api/rooms" '{"number": 8, "capacity": 80}' "Sala 8"
post_request "/api/rooms" '{"number": 9, "capacity": 60}' "Sala 9"
post_request "/api/rooms" '{"number": 10, "capacity": 60}' "Sala 10"
post_request "/api/rooms" '{"number": 11, "capacity": 200}' "Sala 11"
post_request "/api/rooms" '{"number": 12, "capacity": 180}' "Sala 12"
post_request "/api/rooms" '{"number": 13, "capacity": 90}' "Sala 13"
post_request "/api/rooms" '{"number": 14, "capacity": 90}' "Sala 14"
post_request "/api/rooms" '{"number": 15, "capacity": 70}' "Sala 15"
post_request "/api/rooms" '{"number": 16, "capacity": 70}' "Sala 16"
post_request "/api/rooms" '{"number": 17, "capacity": 50}' "Sala 17"
post_request "/api/rooms" '{"number": 18, "capacity": 50}' "Sala 18"
post_request "/api/rooms" '{"number": 19, "capacity": 40}' "Sala 19"
post_request "/api/rooms" '{"number": 20, "capacity": 40}' "Sala 20"

echo ""
echo "2. Creando 20 Películas..."
post_request "/api/movies" '{"title":"Oppenheimer","description":"La historia del físico J. Robert Oppenheimer y su papel en el desarrollo de la bomba atómica.","duration":180,"genre":"Drama/Biografía","rating":5}' "Oppenheimer"
post_request "/api/movies" '{"title":"Barbie","description":"Barbie vive en Barbieland donde todo es perfecto. Cuando comienza a tener pensamientos sobre la muerte, emprende un viaje al mundo real.","duration":114,"genre":"Comedia/Fantasía","rating":4}' "Barbie"
post_request "/api/movies" '{"title":"Dune: Parte Dos","description":"Paul Atreides se une a Chani y los Fremen mientras busca venganza contra los conspiradores que destruyeron a su familia.","duration":166,"genre":"Ciencia Ficción","rating":5}' "Dune: Parte Dos"
post_request "/api/movies" '{"title":"Guardianes de la Galaxia Vol. 3","description":"Los Guardianes emprenden una misión peligrosa para proteger a uno de los suyos.","duration":150,"genre":"Acción/Aventura","rating":4}' "Guardianes Vol. 3"
post_request "/api/movies" '{"title":"El Niño y la Garza","description":"Un joven llamado Mahito anhela a su madre fallecida. Un día, una misteriosa garza parlante lo guía.","duration":124,"genre":"Animación/Fantasía","rating":5}' "El Niño y la Garza"
post_request "/api/movies" '{"title":"Killers of the Flower Moon","description":"Miembros de la nación Osage son asesinados bajo misteriosas circunstancias en la década de 1920.","duration":206,"genre":"Drama/Crimen","rating":5}' "Killers of the Flower Moon"
post_request "/api/movies" '{"title":"Spider-Man: A Través del Spider-Verso","description":"Miles Morales se embarca en una aventura épica que lo transportará a través del Multiverso.","duration":140,"genre":"Animación/Acción","rating":5}' "Spider-Man"
post_request "/api/movies" '{"title":"Misión Imposible: Sentencia Mortal","description":"Ethan Hunt y su equipo se embarcan en su misión más peligrosa hasta la fecha.","duration":163,"genre":"Acción/Thriller","rating":4}' "Misión Imposible 7"
post_request "/api/movies" '{"title":"Los Asesinos de la Luna","description":"Una serie de asesinatos brutales de miembros de la tribu Osage en Oklahoma durante los años 20.","duration":206,"genre":"Drama/Historia","rating":5}' "Los Asesinos"
post_request "/api/movies" '{"title":"La Sociedad de la Nieve","description":"La historia real del accidente aéreo de 1972 en los Andes y la lucha por la supervivencia.","duration":144,"genre":"Drama/Supervivencia","rating":4}' "La Sociedad de la Nieve"
post_request "/api/movies" '{"title":"Poor Things","description":"Una joven mujer es traída de vuelta a la vida por un científico brillante pero poco ortodoxo.","duration":141,"genre":"Comedia/Drama","rating":4}' "Poor Things"
post_request "/api/movies" '{"title":"Napoleón","description":"Una mirada personal a los orígenes de Napoleón Bonaparte y su rápido ascenso al poder.","duration":158,"genre":"Drama/Historia","rating":4}' "Napoleón"
post_request "/api/movies" '{"title":"Wonka","description":"La historia de cómo el joven Willy Wonka se convirtió en el chocolatero más famoso del mundo.","duration":116,"genre":"Musical/Fantasía","rating":4}' "Wonka"
post_request "/api/movies" '{"title":"El Exorcista: Creyente","description":"Cuando dos niñas desaparecen, sus padres desesperados buscan la ayuda de Chris MacNeil.","duration":111,"genre":"Terror","rating":3}' "El Exorcista"
post_request "/api/movies" '{"title":"Ferrari","description":"El verano de 1957. Enzo Ferrari está en crisis mientras su empresa se tambalea al borde de la bancarrota.","duration":130,"genre":"Drama/Biografía","rating":4}' "Ferrari"
post_request "/api/movies" '{"title":"Anatomía de una Caída","description":"Una mujer es sospechosa de asesinato tras la muerte de su esposo en circunstancias misteriosas.","duration":151,"genre":"Drama/Thriller","rating":5}' "Anatomía de una Caída"
post_request "/api/movies" '{"title":"Godzilla Minus One","description":"Japón post-guerra es devastado por un Godzilla gigante. ¿Qué posdata le espera al país?","duration":124,"genre":"Acción/Ciencia Ficción","rating":5}' "Godzilla Minus One"
post_request "/api/movies" '{"title":"Maestro","description":"La compleja relación entre el compositor Leonard Bernstein y su esposa Felicia Montealegre.","duration":129,"genre":"Drama/Biografía","rating":4}' "Maestro"
post_request "/api/movies" '{"title":"El Color Púrpura","description":"Una mujer afroamericana enfrenta los desafíos de la vida en el sur de Estados Unidos.","duration":141,"genre":"Drama/Musical","rating":4}' "El Color Púrpura"
post_request "/api/movies" '{"title":"Saltburn","description":"Un estudiante de Oxford se obsesiona con un compañero aristocrático y es invitado a su extravagante mansión.","duration":131,"genre":"Thriller/Drama","rating":4}' "Saltburn"

echo ""
echo "3. Creando 50 Funciones..."
# HOY 27/10 - Funciones populares con múltiples horarios
post_request "/api/screenings" '{"movieId":1,"roomId":11,"startTime":"2025-10-27T14:00:00","endTime":"2025-10-27T17:00:00"}' "Oppenheimer 14:00"
post_request "/api/screenings" '{"movieId":1,"roomId":12,"startTime":"2025-10-27T18:00:00","endTime":"2025-10-27T21:00:00"}' "Oppenheimer 18:00"
post_request "/api/screenings" '{"movieId":1,"roomId":11,"startTime":"2025-10-27T21:30:00","endTime":"2025-10-28T00:30:00"}' "Oppenheimer 21:30"
post_request "/api/screenings" '{"movieId":3,"roomId":1,"startTime":"2025-10-27T15:00:00","endTime":"2025-10-27T17:46:00"}' "Dune 2 - 15:00"
post_request "/api/screenings" '{"movieId":3,"roomId":2,"startTime":"2025-10-27T18:30:00","endTime":"2025-10-27T21:16:00"}' "Dune 2 - 18:30"
post_request "/api/screenings" '{"movieId":3,"roomId":1,"startTime":"2025-10-27T22:00:00","endTime":"2025-10-28T00:46:00"}' "Dune 2 - 22:00"
post_request "/api/screenings" '{"movieId":7,"roomId":3,"startTime":"2025-10-27T16:00:00","endTime":"2025-10-27T18:20:00"}' "Spider-Man 16:00"
post_request "/api/screenings" '{"movieId":7,"roomId":3,"startTime":"2025-10-27T19:00:00","endTime":"2025-10-27T21:20:00"}' "Spider-Man 19:00"
post_request "/api/screenings" '{"movieId":2,"roomId":4,"startTime":"2025-10-27T17:00:00","endTime":"2025-10-27T18:54:00"}' "Barbie 17:00"
post_request "/api/screenings" '{"movieId":2,"roomId":5,"startTime":"2025-10-27T20:00:00","endTime":"2025-10-27T21:54:00"}' "Barbie 20:00"
post_request "/api/screenings" '{"movieId":4,"roomId":6,"startTime":"2025-10-27T16:30:00","endTime":"2025-10-27T19:00:00"}' "Guardianes 16:30"
post_request "/api/screenings" '{"movieId":4,"roomId":6,"startTime":"2025-10-27T19:30:00","endTime":"2025-10-27T22:00:00"}' "Guardianes 19:30"
post_request "/api/screenings" '{"movieId":17,"roomId":7,"startTime":"2025-10-27T18:00:00","endTime":"2025-10-27T20:04:00"}' "Godzilla 18:00"
post_request "/api/screenings" '{"movieId":17,"roomId":8,"startTime":"2025-10-27T21:00:00","endTime":"2025-10-27T23:04:00"}' "Godzilla 21:00"
post_request "/api/screenings" '{"movieId":5,"roomId":9,"startTime":"2025-10-27T17:30:00","endTime":"2025-10-27T19:34:00"}' "El Niño y la Garza"
post_request "/api/screenings" '{"movieId":13,"roomId":10,"startTime":"2025-10-27T16:00:00","endTime":"2025-10-27T17:56:00"}' "Wonka 16:00"

# 28/10 - Mañana
post_request "/api/screenings" '{"movieId":1,"roomId":11,"startTime":"2025-10-28T15:00:00","endTime":"2025-10-28T18:00:00"}' "Oppenheimer 15:00 (28)"
post_request "/api/screenings" '{"movieId":1,"roomId":12,"startTime":"2025-10-28T20:00:00","endTime":"2025-10-28T23:00:00"}' "Oppenheimer 20:00 (28)"
post_request "/api/screenings" '{"movieId":3,"roomId":1,"startTime":"2025-10-28T16:00:00","endTime":"2025-10-28T18:46:00"}' "Dune 2 - 16:00 (28)"
post_request "/api/screenings" '{"movieId":3,"roomId":2,"startTime":"2025-10-28T19:30:00","endTime":"2025-10-28T22:16:00"}' "Dune 2 - 19:30 (28)"
post_request "/api/screenings" '{"movieId":6,"roomId":12,"startTime":"2025-10-28T14:00:00","endTime":"2025-10-28T17:26:00"}' "Killers 14:00 (28)"
post_request "/api/screenings" '{"movieId":6,"roomId":11,"startTime":"2025-10-28T19:00:00","endTime":"2025-10-28T22:26:00"}' "Killers 19:00 (28)"
post_request "/api/screenings" '{"movieId":7,"roomId":3,"startTime":"2025-10-28T15:30:00","endTime":"2025-10-28T17:50:00"}' "Spider-Man 15:30 (28)"
post_request "/api/screenings" '{"movieId":7,"roomId":4,"startTime":"2025-10-28T20:00:00","endTime":"2025-10-28T22:20:00"}' "Spider-Man 20:00 (28)"
post_request "/api/screenings" '{"movieId":8,"roomId":5,"startTime":"2025-10-28T18:00:00","endTime":"2025-10-28T20:43:00"}' "Misión Imposible (28)"
post_request "/api/screenings" '{"movieId":10,"roomId":6,"startTime":"2025-10-28T17:00:00","endTime":"2025-10-28T19:24:00"}' "La Sociedad (28)"

# 29/10
post_request "/api/screenings" '{"movieId":16,"roomId":13,"startTime":"2025-10-29T16:00:00","endTime":"2025-10-29T18:31:00"}' "Anatomía 16:00 (29)"
post_request "/api/screenings" '{"movieId":16,"roomId":13,"startTime":"2025-10-29T19:00:00","endTime":"2025-10-29T21:31:00"}' "Anatomía 19:00 (29)"
post_request "/api/screenings" '{"movieId":11,"roomId":14,"startTime":"2025-10-29T17:30:00","endTime":"2025-10-29T19:51:00"}' "Poor Things 17:30 (29)"
post_request "/api/screenings" '{"movieId":11,"roomId":14,"startTime":"2025-10-29T20:30:00","endTime":"2025-10-29T22:51:00"}' "Poor Things 20:30 (29)"
post_request "/api/screenings" '{"movieId":12,"roomId":15,"startTime":"2025-10-29T18:00:00","endTime":"2025-10-29T20:38:00"}' "Napoleón (29)"
post_request "/api/screenings" '{"movieId":15,"roomId":16,"startTime":"2025-10-29T19:00:00","endTime":"2025-10-29T21:10:00"}' "Ferrari (29)"
post_request "/api/screenings" '{"movieId":14,"roomId":17,"startTime":"2025-10-29T21:00:00","endTime":"2025-10-29T22:51:00"}' "El Exorcista 21:00 (29)"
post_request "/api/screenings" '{"movieId":14,"roomId":18,"startTime":"2025-10-29T22:30:00","endTime":"2025-10-30T00:21:00"}' "El Exorcista 22:30 (29)"

# 30/10
post_request "/api/screenings" '{"movieId":18,"roomId":19,"startTime":"2025-10-30T17:00:00","endTime":"2025-10-30T19:09:00"}' "Maestro (30)"
post_request "/api/screenings" '{"movieId":19,"roomId":20,"startTime":"2025-10-30T18:30:00","endTime":"2025-10-30T20:51:00"}' "El Color Púrpura (30)"
post_request "/api/screenings" '{"movieId":20,"roomId":19,"startTime":"2025-10-30T20:00:00","endTime":"2025-10-30T22:11:00"}' "Saltburn (30)"
post_request "/api/screenings" '{"movieId":13,"roomId":20,"startTime":"2025-10-30T15:00:00","endTime":"2025-10-30T16:56:00"}' "Wonka 15:00 (30)"
post_request "/api/screenings" '{"movieId":17,"roomId":7,"startTime":"2025-10-30T19:00:00","endTime":"2025-10-30T21:04:00"}' "Godzilla 19:00 (30)"
post_request "/api/screenings" '{"movieId":2,"roomId":8,"startTime":"2025-10-30T16:30:00","endTime":"2025-10-30T18:24:00"}' "Barbie 16:30 (30)"
post_request "/api/screenings" '{"movieId":4,"roomId":9,"startTime":"2025-10-30T20:30:00","endTime":"2025-10-30T23:00:00"}' "Guardianes 20:30 (30)"

# 31/10 - Halloween
post_request "/api/screenings" '{"movieId":14,"roomId":17,"startTime":"2025-10-31T20:00:00","endTime":"2025-10-31T21:51:00"}' "El Exorcista 20:00 (Halloween)"
post_request "/api/screenings" '{"movieId":14,"roomId":18,"startTime":"2025-10-31T22:30:00","endTime":"2025-11-01T00:21:00"}' "El Exorcista 22:30 (Halloween)"
post_request "/api/screenings" '{"movieId":1,"roomId":11,"startTime":"2025-10-31T16:00:00","endTime":"2025-10-31T19:00:00"}' "Oppenheimer 16:00 (31)"
post_request "/api/screenings" '{"movieId":3,"roomId":1,"startTime":"2025-10-31T17:00:00","endTime":"2025-10-31T19:46:00"}' "Dune 2 - 17:00 (31)"
post_request "/api/screenings" '{"movieId":7,"roomId":3,"startTime":"2025-10-31T14:00:00","endTime":"2025-10-31T16:20:00"}' "Spider-Man 14:00 (31)"
post_request "/api/screenings" '{"movieId":5,"roomId":9,"startTime":"2025-10-31T15:00:00","endTime":"2025-10-31T17:04:00"}' "El Niño y la Garza (31)"

echo ""
echo "=========================================="
echo "✓ Proceso completado!"
echo "=========================================="
echo "Resumen:"
echo "- 20 Salas creadas"
echo "- 20 Películas creadas"
echo "- 50 Funciones creadas"
echo ""
