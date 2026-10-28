const API_URL = '/api/students';
const AUTH_URL = '/api/auth';
const LOGIN_PAGE = '/login.html';

// ---------- Токены ----------

function getAccessToken() {
    return localStorage.getItem('accessToken');
}

function getRefreshToken() {
    return localStorage.getItem('refreshToken');
}

function saveTokens(data) {
    localStorage.setItem('accessToken', data.accessToken);
    if (data.refreshToken) {
        localStorage.setItem('refreshToken', data.refreshToken);
    }
}

function clearTokens() {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
}

function goToLogin() {
    clearTokens();
    window.location.href = LOGIN_PAGE;
}

// Получить новый access-токен по refresh-токену
async function refreshAccessToken() {
    const refreshToken = getRefreshToken();

    if (!refreshToken) {
        return false;
    }

    try {
        const response = await fetch(`${AUTH_URL}/refresh`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ refreshToken })
        });

        if (!response.ok) {
            return false;
        }

        saveTokens(await response.json());
        return true;
    } catch (error) {
        return false;
    }
}

// ---------- Запросы ----------

async function apiRequest(url, options = {}, canRetry = true) {
    try {
        const headers = { ...(options.headers || {}) };
        const token = getAccessToken();

        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const response = await fetch(url, { ...options, headers });

        // Токен истёк или его нет: пробуем обновить, иначе отправляем на вход
        if (response.status === 401 && canRetry) {
            if (await refreshAccessToken()) {
                return apiRequest(url, options, false);
            }
            goToLogin();
            throw new Error('Необходима авторизация');
        }

        if (response.status === 204) {
            return null;
        }

        const data = await response.json().catch(() => null);

        if (!response.ok) {
            const message = data?.error?.message
                || data?.message
                || response.statusText
                || 'Неизвестная ошибка';

            const error = new Error(`Код ${response.status}: ${message}`);
            error.status = response.status;
            error.details = data?.error?.details || null;
            throw error;
        }

        return data;
    } catch (error) {
        if (error instanceof TypeError) {
            throw new Error('Не удалось подключиться к Spring Boot');
        }

        throw error;
    }
}

// Получение списка студентов с серверной фильтрацией
async function getStudents(filters = {}) {
    const params = new URLSearchParams();

    for (const [key, value] of Object.entries(filters)) {
        if (value !== '' && value !== null && value !== undefined) {
            params.append(key, value);
        }
    }

    if (!params.has('page')) {
        params.set('page', '0');
    }

    if (!params.has('size')) {
        params.set('size', '20');
    }

    const result = await apiRequest(
        `${API_URL}/search?${params.toString()}`
    );

    return result.content;
}

// Получение одного студента
function getStudentById(id) {
    return apiRequest(`${API_URL}/${id}`);
}

// Добавление студента
function addStudent(student) {
    return apiRequest(API_URL, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(student)
    });
}

// Полное обновление студента
function updateStudent(id, student) {
    return apiRequest(`${API_URL}/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(student)
    });
}

// Удаление студента
function deleteStudent(id) {
    return apiRequest(`${API_URL}/${id}`, {
        method: 'DELETE'
    });
}

// Кнопка «Выйти» (если есть на странице)
document.addEventListener('DOMContentLoaded', () => {
    const logoutButton = document.getElementById('logoutButton');

    if (logoutButton) {
        logoutButton.addEventListener('click', goToLogin);
    }
});