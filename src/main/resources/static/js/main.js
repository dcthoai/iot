const ledStatusElement = document.getElementById('led-status');
const lcdStatusElement = document.getElementById('lcd-status');
const timeRefreshData = document.getElementById('refreshTime');
const analyzeTime = document.getElementById('analyzeTime');

function getEsp32Config() {
    fetch(BASE_URL + 'api/sensor/config')
        .then(response => response.json())
        .then(data => {
            ledStatusElement.innerHTML = data.ledStatus == 1 ? 'Bật' : 'Tắt';
            lcdStatusElement.innerHTML = data.lcdStatus == 1 ? 'Bật' : 'Tắt';
            timeRefreshData.value = data.timeRefreshData;
            analyzeTime.value = data.timeAnalyze;

            timeRefreshDataInterval = parseInt(data.timeRefreshData);
            console.log(timeRefreshDataInterval);
        })
        .catch(error => {
            console.log(error);
        });
}

function sendRequest(url) {
    fetch(BASE_URL + url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            getEsp32Config();
            openPopupNotify('Cập nhật thành công', '', 'success');
        } else {
            openPopupNotify('Thất bại', data.message, 'error');
        }
    })
    .catch(error => {
        openPopupNotify('Thất bại', error.message, 'error');
        console.log(error);
    });
}

function notifyLCD() {
    const message = document.getElementById('message').value;

    fetch(BASE_URL + 'api/esp32/lcd/notify', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: new URLSearchParams({
            'message': message
        })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            openPopupNotify('Cập nhật thành công', '', 'success');
        } else {
            openPopupNotify('Thất bại', data.message, 'error');
        }
    })
    .catch(error => {
        openPopupNotify('Thất bại', error.message, 'error');
        console.log(error);
    });
}

function changeRefreshTime() {
    const refreshTime = document.getElementById('refreshTime').value;

    fetch(BASE_URL + 'api/esp32/change-refresh-data-time', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: new URLSearchParams({
            'millisecond': refreshTime
        })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            getEsp32Config();
            openPopupNotify('Cập nhật thành công', '', 'success');
        } else {
            openPopupNotify('Thất bại', data.message, 'error');
        }
    })
    .catch(error => {
        openPopupNotify('Thất bại', error.message, 'error');
        console.log(error);
    });
}

function changeAnalyzeTime() {
    const analyzeTime = document.getElementById('analyzeTime').value;

    fetch(BASE_URL + 'api/sensor/change-time-analyze', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: new URLSearchParams({
            'seconds': analyzeTime
        })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            getEsp32Config();
            openPopupNotify('Cập nhật thành công', '', 'success');
        } else {
            openPopupNotify('Thất bại', data.message, 'error');
        }
    })
    .catch(error => {
        openPopupNotify('Thất bại', error.message, 'error');
        console.log(error);
    });
}

getEsp32Config();
