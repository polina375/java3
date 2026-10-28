async function renderStudentView() {
    const params = new URLSearchParams(window.location.search);
    const id = Number(params.get('id'));

    const details = document.getElementById('studentDetails');
    const message = document.getElementById('viewMessage');

    function fail(text) {
        details.hidden = true;
        message.textContent = text;
        message.hidden = false;
    }

    if (!Number.isInteger(id) || id <= 0) {
        fail('Некорректный ID студента');
        return;
    }

    try {
        const student = await getStudentById(id);

        document.getElementById('viewFullName').textContent =
            student.fullName ?? '—';

        document.getElementById('viewGroup').textContent =
            student.groupName ?? '—';

        document.getElementById('viewIsuId').textContent =
            student.isuId ?? '—';

        document.getElementById('viewDormNumber').textContent =
            student.dormNumber ?? '—';

        document.getElementById('viewRoom').textContent =
            student.room ?? '—';

        document.getElementById('viewMoveInDate').textContent =
            student.moveInDate ?? '—';

        document.getElementById('viewIsForeigner').textContent =
            student.isForeigner ? 'Да' : 'Нет';

        document.getElementById('viewNotes').textContent =
            student.notes || '—';

    } catch (error) {
        fail(error.message);
    }
}

document.addEventListener('DOMContentLoaded', renderStudentView);
