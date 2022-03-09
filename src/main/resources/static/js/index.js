// Change userType value on click
// For userType field on Register form
function userTypeClick(value) {
    document.getElementById('userType').value=value

    if(value === "vet") {
        document.getElementById('vet').classList.add('bg-primary', 'text-light')
        document.getElementById('petOwner').classList.remove('bg-primary', 'text-light')
    } else {
        document.getElementById('petOwner').classList.add('bg-primary', 'text-light')
        document.getElementById('vet').classList.remove('bg-primary', 'text-light')
    }
}

function handleFilter() {
    window.location = `/dashboard-pet-owner?filter=${document.querySelector('input[name="filterRadio"]:checked').value}`
}

function getLocation() {
    navigator.geolocation.getCurrentPosition(showPosition);
}

function showPosition(position) {
    fetch(`https://maps.googleapis.com/maps/api/geocode/json?latlng=${position.coords.latitude},${position.coords.longitude}&result_type=neighborhood&key=AIzaSyCjFOLSO6Os1tn4eSNktZ_tyKEm6yWUHh4`).then(function(response) {
        return response.json();
    }).then(function(data) {
        const term = (data.results[0].address_components[1].long_name);
        document.getElementById("term").value = term;
        document.getElementById("searchForm").submit();
    })
}

function handleStarClick(star) {
    const ratingStars = [...document.getElementsByClassName("rating_star")];
    let starValue = star.control.value;


    for(let i = starValue - 1; i >= 0; i--) {
        ratingStars[i].classList.add("checked");
    }


    for(let i = starValue; i < 5; i++) {
        ratingStars[i].classList.remove("checked");
    }
}

