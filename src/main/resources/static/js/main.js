const logoutAdminButton = document.getElementById('logout-admin-button');

logoutAdminButton.addEventListener('click', () => {
    fetch(BASE_URL + `api/auth/logout`, {
        method: 'POST'
    })
    .then(response => response.json())
    .then(status => {
        if (status.success)
            window.location.href = BASE_URL + 'admin';
        else
            openPopupNotify('Thất bại', status.message, 'error');
    })
    .catch(error => {
        openPopupNotify('Thất bại', error.message, 'error');
        console.error(error);
    });
});