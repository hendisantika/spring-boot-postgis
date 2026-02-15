// Initialize map
const map = L.map('map').setView([-2.5489, 118.0149], 5);

L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; OpenStreetMap contributors',
    maxZoom: 18
}).addTo(map);

const markersLayer = L.layerGroup().addTo(map);
const boundaryLayer = L.layerGroup().addTo(map);

// Handle provinsi selection
function handleProvinsiChange(kode) {
    console.log('Province selected:', kode);
    if (kode) {
        loadDetail(kode);

        if (typeof htmx !== 'undefined') {
            htmx.ajax('GET', '/wilayah/kabupaten-select/' + kode, {
                target: '#kabupaten-container',
                swap: 'innerHTML'
            });
        }
        resetKecamatanSelect();
        resetDesaSelect();
    }
}

// Handle kabupaten selection
function handleKabupatenChange(kode) {
    if (kode) {
        loadDetail(kode);

        if (typeof htmx !== 'undefined') {
            htmx.ajax('GET', '/wilayah/kecamatan-select/' + kode, {
                target: '#kecamatan-container',
                swap: 'innerHTML'
            });
        }
        resetDesaSelect();
    }
}

// Handle kecamatan selection
function handleKecamatanChange(kode) {
    if (kode) {
        loadDetail(kode);

        if (typeof htmx !== 'undefined') {
            htmx.ajax('GET', '/wilayah/desa-select/' + kode, {
                target: '#desa-container',
                swap: 'innerHTML'
            });
        }
    }
}

// Handle desa/kelurahan selection
function handleDesaChange(kode) {
    if (kode) {
        loadDetail(kode);
    }
}

// Reset select helpers
function resetKabupatenSelect() {
    document.getElementById('kabupaten-container').innerHTML =
        '<label class="w3-text-theme"><b><i class="fa fa-city"></i> Kabupaten/Kota</b></label>' +
        '<select class="w3-select w3-border w3-margin-bottom" id="kabupaten-select" disabled>' +
        '<option value="">Pilih Kabupaten/Kota</option>' +
        '</select>';
}

function resetKecamatanSelect() {
    document.getElementById('kecamatan-container').innerHTML =
        '<label class="w3-text-theme"><b><i class="fa fa-building"></i> Kecamatan</b></label>' +
        '<select class="w3-select w3-border w3-margin-bottom" id="kecamatan-select" disabled>' +
        '<option value="">Pilih Kecamatan</option>' +
        '</select>';
}

function resetDesaSelect() {
    document.getElementById('desa-container').innerHTML =
        '<label class="w3-text-theme"><b><i class="fa fa-home"></i> Desa/Kelurahan</b></label>' +
        '<select class="w3-select w3-border w3-margin-bottom" id="desa-select" disabled>' +
        '<option value="">Pilih Desa/Kelurahan</option>' +
        '</select>';
}

// Load detail for selected wilayah
function loadDetail(kode) {
    if (kode) {
        // Load detail panel
        if (typeof htmx !== 'undefined') {
            htmx.ajax('GET', '/wilayah/detail/' + kode, {target: '#detail-panel'});
        }

        // Load boundaries
        fetch('/wilayah/api/boundary/' + kode)
            .then(function(response) { return response.json(); })
            .then(function(data) {
                markersLayer.clearLayers();
                boundaryLayer.clearLayers();

                // Add marker if coordinates available
                if (data.lat && data.lng) {
                    L.marker([data.lat, data.lng])
                        .bindPopup('<b>' + data.nama + '</b><br>Kode: ' + data.kode)
                        .addTo(markersLayer);
                }

                // Display boundary if available
                if (data.coordinates) {
                    try {
                        var parsed = JSON.parse(data.coordinates);
                        var layer;

                        if (parsed.type && parsed.coordinates) {
                            // GeoJSON format from PostGIS ST_AsGeoJSON
                            layer = L.geoJSON(parsed, {
                                style: {
                                    color: '#3388ff',
                                    fillColor: '#3388ff',
                                    fillOpacity: 0.2,
                                    weight: 2
                                }
                            })
                            .bindPopup('<b>' + data.nama + '</b><br>' + data.level)
                            .addTo(boundaryLayer);
                        } else if (Array.isArray(parsed)) {
                            // Coordinate array from path field
                            layer = L.polygon(parsed, {
                                color: '#3388ff',
                                fillColor: '#3388ff',
                                fillOpacity: 0.2,
                                weight: 2
                            })
                            .bindPopup('<b>' + data.nama + '</b><br>' + data.level)
                            .addTo(boundaryLayer);
                        }

                        if (layer) {
                            map.fitBounds(layer.getBounds(), {padding: [50, 50]});
                        }
                    } catch (e) {
                        console.error('Error parsing boundary:', e);
                        if (data.lat && data.lng) {
                            map.setView([data.lat, data.lng], 10);
                        }
                    }
                } else if (data.lat && data.lng) {
                    map.setView([data.lat, data.lng], 10);
                }
            })
            .catch(function(error) { console.error('Error loading boundaries:', error); });
    }
}

// Reset map view
function resetMapView() {
    map.setView([-2.5489, 118.0149], 5);
    markersLayer.clearLayers();
    boundaryLayer.clearLayers();

    // Reset all dropdowns
    var provinsiSelect = document.getElementById('provinsi-select');
    if (provinsiSelect) {
        provinsiSelect.selectedIndex = 0;
    }
    resetKabupatenSelect();
    resetKecamatanSelect();
    resetDesaSelect();

    // Clear detail panel
    document.getElementById('detail-panel').innerHTML =
        '<p class="w3-text-grey w3-center"><i class="fa fa-arrow-up"></i> Pilih wilayah untuk melihat detail</p>';

    // Show provinces again
    showProvinces();
}

// Show all provinces
function showProvinces() {
    fetch('/wilayah/api/all')
        .then(function(response) { return response.json(); })
        .then(function(data) {
            markersLayer.clearLayers();
            boundaryLayer.clearLayers();
            var provinces = data.filter(function(w) { return w.kode.length === 2; });
            provinces.forEach(function(prov) {
                if (prov.lat && prov.lng) {
                    L.marker([prov.lat, prov.lng])
                        .bindPopup('<b>' + prov.nama + '</b><br>Kode: ' + prov.kode)
                        .addTo(markersLayer);
                }
            });
            map.setView([-2.5489, 118.0149], 5);
        });
}

// Load provinces on start
showProvinces();
