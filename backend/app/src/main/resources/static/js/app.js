// Shared Edit/Save gating for the risk-calculator config forms (weights, role/env/mttr,
// factors). Fields render read-only; "Edit" unlocks them; "Save" stays disabled until a
// value actually differs from what the form loaded with, and re-locks after a
// successful HTMX save. Opt in per-form via data-edit-save-form / data-edit-btn / data-save-btn.
function initEditSaveForms() {
    document.querySelectorAll('[data-edit-save-form]').forEach(function (form) {
        if (form.__editSaveInit) {
            return; // htmx:load also fires for the form's own #result swap - don't rebind
        }
        form.__editSaveInit = true;

        var saveBtn = form.querySelector('[data-save-btn]');
        var editBtn = form.querySelector('[data-edit-btn]');
        var fields = form.querySelectorAll('input, select, textarea');

        function snapshot() {
            var values = {};
            fields.forEach(function (f) { values[f.name || f.id] = f.value; });
            return values;
        }

        var initial = snapshot();

        function lock() {
            fields.forEach(function (f) { f.disabled = true; });
            if (saveBtn) saveBtn.disabled = true;
        }

        function checkDirty() {
            if (!saveBtn) return;
            var current = snapshot();
            var dirty = Object.keys(initial).some(function (k) { return initial[k] !== current[k]; });
            saveBtn.disabled = !dirty;
        }

        lock();

        if (editBtn) {
            editBtn.addEventListener('click', function () {
                fields.forEach(function (f) { f.disabled = false; });
                if (saveBtn) saveBtn.disabled = true; // unlocked, but nothing changed yet
                fields.length && fields[0].focus();
            });
        }

        fields.forEach(function (f) {
            f.addEventListener('input', checkDirty);
            f.addEventListener('change', checkDirty);
        });

        // Only the inner #result div is hx-target'd on submit, so the form itself is
        // never re-rendered - re-lock it client-side once the save succeeds.
        form.addEventListener('htmx:afterRequest', function (evt) {
            if (evt.detail && evt.detail.successful) {
                initial = snapshot();
                lock();
            }
        });
    });
}

initEditSaveForms();
document.addEventListener('htmx:load', initEditSaveForms);
