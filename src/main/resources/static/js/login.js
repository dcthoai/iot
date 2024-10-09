function login(user){
    fetch(BASE_URL + 'api/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(user),
    })
    .then(response => response.json())
    .then(status => {
        if (status.success)
            window.location.href = BASE_URL;
        else
            openPopupNotify('Đăng nhập thất bại: ', status.message, 'error');
    })
    .catch(error => {
        openPopupNotify('Đăng nhập thất bại!', error.message, 'error');
        console.log(error);
    })
}

function validateLogin(){
    Validator({
        form: 'form-login',
        formInput: '.input-box',
        errorMessage: '.label-error',
        rules: [
            Validator.isRequired('#username', 'Vui lòng nhập username hoặc email!'),
            Validator.isRequired('#password', 'Vui lòng nhập mật khẩu của bạn!'),
            Validator.minLength('#password', 4, 'Vui lòng nhập tối thiểu 8 kí tự!'),
        ],
        onSubmit: function(data){
            let user = {
        		username: data['username'],
                password: data['password']
            }

        	login(user);
        }
    });
}

validateLogin();
