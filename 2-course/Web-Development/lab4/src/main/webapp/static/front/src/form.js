import React, { useState } from 'react';
import { useDispatch } from 'react-redux';
import { loginSuccess, loginFailure } from './authSlice';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const AuthForm = () => {
    const [isLogin, setIsLogin] = useState(true);
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');

    const dispatch = useDispatch();
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();

        const user = { username, password };

        try {
            const response = await axios.get('http://localhost:8080/web_4th_lab-1.0/rest/authorization/'+(isLogin ? 'authorize' : 'register'), {
                params: {
                    username: user.username,
                    password: user.password,
                },
            });


            if (response.status === 200) {
                if(response.data.startsWith('Authorized:')) {
                    const authData = response.data.split(':')[1];
                    const [userId, token] = authData.split('%');
                    dispatch(loginSuccess({ id: userId, token }));
                    sessionStorage.setItem('id', userId);
                    sessionStorage.setItem('token', token);
                    navigate('/web_4th_lab-1.0/main_page');
                }else{
                    setErrorMessage((isLogin ? 'неверное имя пользователя или пароль' : 'пользователь с таким именем уже существует'));
                }
            }
        } catch (error) {
            setErrorMessage('Ошибка авторизации. Проверьте имя пользователя и пароль');
            dispatch(loginFailure('Неверные данные для входа'));
        }
    };

    return (
        <div style={{ maxWidth: '400px', margin: '0 auto', padding: '60px', textAlign: 'center', border: '1px solid #ccc', borderRadius: '8px', alignContent: "center" }}>
            <h2>{isLogin ? 'Авторизация' : 'Регистрация'}</h2>
            <form onSubmit={handleLogin}>
                { (
                    <div style={{ marginBottom: '10px' }}>
                        <label>
                            Имя пользователя:
                            <input
                                type="text"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                placeholder="Введите имя"
                                style={{ display: 'block', width: '100%', padding: '8px', marginTop: '5px' }}
                            />
                        </label>
                    </div>
                )}

                <div style={{ marginBottom: '20px' }}>
                    <label>
                        Пароль:
                        <input
                            type="password"
                            placeholder="Введите пароль"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            style={{ display: 'block', width: '100%', padding: '8px', marginTop: '5px' }}
                        />
                    </label>
                </div>

                <button type="submit" style={{ padding: '10px 20px', backgroundColor: '#007bff', color: '#fff', border: 'none', borderRadius: '4px' }}>
                    {isLogin ? 'Войти' : 'Зарегистрироваться'}
                </button>
            </form>
            {errorMessage && <p>{errorMessage}</p>}
            <div style={{ marginTop: '20px' }}>
                <p>
                    {isLogin ? 'Нет аккаунта?' : 'Уже есть аккаунт?'}
                    <button
                        type="button"
                        onClick={() => setIsLogin(!isLogin)}
                        style={{ marginLeft: '5px', background: 'none', border: 'none', color: '#007bff', cursor: 'pointer' }}
                    >
                        {isLogin ? 'Зарегистрироваться' : 'Войти'}
                    </button>
                </p>
            </div>
        </div>
    );
};

export default AuthForm;
