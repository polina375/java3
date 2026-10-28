// ПРЕДПОЛОЖЕНИЯ: сверь с Swagger (раздел /api/auth) и при необходимости поменяй
const LOGIN_PATH = '/login';
const REGISTER_PATH = '/register';
const LOGIN_FIELD = 'username';   // как называется поле имени у коллеги: username / login / email

async function authRequest(path, body) {
    try {
        const response = await fetch(`${AUTH_URL}${path}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });

        const data = await response.json().catch(() => null);

        if (!response.ok) {
            const message = data?.error?.message
                || data?.message
                || `Код ${response.status}`;
            const error = new Error(message);
            error.details = data?.error?.details || null;
            throw error;
        }

        return data;
    } catch (error) {
        if (error instanceof TypeError) {
            throw new Error('Не удалось подключиться к серверу');
        }
        throw error;
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('loginForm');
    const registerForm = document.getElementById('registerForm');
    const title = document.getElementById('authTitle');
    const box = document.getElementById('authMessage');

    function showMessage(text, isError = false) {
        box.textContent = text;
        box.classList.toggle('message-error', isError);
        box.hidden = !text;
    }

    function formatError(error) {
        const parts = [error.message];
        if (error.details) {
            parts.push(...Object.values(error.details));
        }
        return parts.join('\n');
    }

    function showMode(mode) {
        loginForm.style.display = mode === 'login' ? '' : 'none';
        registerForm.style.display = mode === 'register' ? '' : 'none';
        title.textContent = mode === 'login' ? 'Вход' : 'Регистрация';
        showMessage('');
    }

    showMode('login');

    document.getElementById('showRegister')
        .addEventListener('click', () => showMode('register'));
    document.getElementById('showLogin')
        .addEventListener('click', () => showMode('login'));

    loginForm.addEventListener('submit', async event => {
        event.preventDefault();
        showMessage('Вход...');

        try {
            const data = await authRequest(LOGIN_PATH, {
                [LOGIN_FIELD]: document.getElementById('loginUsername').value.trim(),
                password: document.getElementById('loginPassword').value
            });

            saveTokens(data);
            window.location.href = '/index.html';
        } catch (error) {
            showMessage(formatError(error), true);
        }
    });

    registerForm.addEventListener('submit', async event => {
        event.preventDefault();
        showMessage('Регистрация...');

        try {
            const data = await authRequest(REGISTER_PATH, {
                username: document.getElementById('regUsername').value.trim(),
                email: document.getElementById('regEmail').value.trim(),
                password: document.getElementById('regPassword').value
            });

            saveTokens(data);
            window.location.href = '/index.html';
        } catch (error) {
            showMessage(formatError(error), true);
        }
    });
});