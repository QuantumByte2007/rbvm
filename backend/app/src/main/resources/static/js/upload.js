// Logique de la page upload — lit le DOM, appelle Api, met à jour l'affichage
document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('upload-form');
    const fileInput = document.getElementById('file-input');
    const output = document.getElementById('out');
    const button = form.querySelector('button');

    form.addEventListener('submit', async (e) => {
        e.preventDefault(); // empêche le rechargement

        const file = fileInput.files[0];
        if (!file) {
            output.textContent = 'Aucun fichier sélectionné.';
            return;
        }

        button.disabled = true;
        output.textContent = 'Envoi en cours…';

        try {
            const result = await Api.uploadFile(file);
            output.textContent = 'Succès : ' + JSON.stringify(result, null, 2);
        } catch (err) {
            output.textContent = err.message;
        } finally {
            button.disabled = false;
        }
    });
});