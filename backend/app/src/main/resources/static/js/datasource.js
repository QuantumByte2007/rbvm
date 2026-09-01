// Data-ingestion page: gates each source's "Launch" button behind a successful
// upload, and re-locks it once a launch has fired (success or failure) so a new
// file must be uploaded before the next launch. Runs once per card instance -
// this script tag ships inside the swapped fragment, so it re-executes on every
// full load and every htmx navigation to /joblaunch; the __ingestInit flag
// keeps a re-run from double-binding listeners on the same DOM nodes.
(function () {
    var STATUS = {
        idle: 'Waiting for a file.',
        uploaded: 'File uploaded — ready to launch.',
        uploadError: 'Upload failed — check the file and try again.',
        launched: 'Digestion launched — upload a new file to run it again.',
        launchError: 'Launch failed — upload a new file and try again.'
    };

    function setStep(card, step) {
        var items = card.querySelectorAll('[data-ingest-steps] li');
        items.forEach(function (li) {
            var isUpload = li.getAttribute('data-step') === 'upload';
            li.classList.toggle('active', step === 'upload' ? isUpload : !isUpload);
            li.classList.toggle('done', step === 'launch' && isUpload);
        });
    }

    function setStatus(card, text) {
        var el = card.querySelector('[data-ingest-status]');
        if (el) el.textContent = text;
    }

    function initCard(card) {
        if (card.__ingestInit) return;
        card.__ingestInit = true;

        var uploadForm = card.querySelector('[data-upload-form]');
        var launchBtn = card.querySelector('[data-launch-btn]');
        var fileInput = card.querySelector('input[type=file]');
        var dropzoneText = card.querySelector('.ingest-dropzone-text');

        if (fileInput && dropzoneText) {
            var defaultText = dropzoneText.textContent;
            fileInput.addEventListener('change', function () {
                dropzoneText.textContent = fileInput.files.length ? fileInput.files[0].name : defaultText;
            });
        }

        if (uploadForm) {
            uploadForm.addEventListener('htmx:afterRequest', function (evt) {
                if (evt.detail && evt.detail.successful) {
                    if (launchBtn) launchBtn.disabled = false;
                    setStep(card, 'launch');
                    setStatus(card, STATUS.uploaded);
                } else {
                    if (launchBtn) launchBtn.disabled = true;
                    setStatus(card, STATUS.uploadError);
                }
            });
        }

        if (launchBtn) {
            launchBtn.addEventListener('htmx:afterRequest', function (evt) {
                // Always re-lock: a launch (successful or not) always requires a fresh
                // upload before it can run again.
                launchBtn.disabled = true;
                setStep(card, 'upload');
                setStatus(card, evt.detail && evt.detail.successful ? STATUS.launched : STATUS.launchError);
            });
        }
    }

    document.querySelectorAll('[data-ingest-card]').forEach(initCard);
})();
