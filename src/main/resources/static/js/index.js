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