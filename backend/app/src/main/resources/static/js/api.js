// Couche d'accès à l'API — ne touche jamais au DOM
const Api = {
    async uploadFile(file) {
        const formData = new FormData();
        formData.append('file', file);

        const res = await fetch('/api/v1/upload/file', {
            method: 'POST',
            body: formData
        });

        if (!res.ok) {
            throw new Error(`Erreur ${res.status}: ${await res.text()}`);
        }
        return res.json(); // ton API renvoie du JSON
    },

    async uploadTempFile(file, type) {
        const formData = new FormData();
        formData.append('file', file);

        const res = await fetch(`/api/v1/upload/tempfile/${type}`, {
            method: 'POST',
            body: formData
        });

        if (!res.ok) {
            throw new Error(`Erreur ${res.status}: ${await res.text()}`);
        }
        return res.json();
    }
};