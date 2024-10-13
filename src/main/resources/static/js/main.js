// const logoutAdminButton = document.getElementById('logout-admin-button');
//
// logoutAdminButton.addEventListener('click', () => {
//     fetch(BASE_URL + `api/auth/logout`, {
//         method: 'POST'
//     })
//     .then(response => response.json())
//     .then(status => {
//         if (status.success)
//             window.location.href = BASE_URL + 'admin';
//         else
//             openPopupNotify('Thất bại', status.message, 'error');
//     })
//     .catch(error => {
//         openPopupNotify('Thất bại', error.message, 'error');
//         console.error(error);
//     });
// });

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

    fetch(BASE_URL + 'api/esp32/change-time-analyze', {
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
