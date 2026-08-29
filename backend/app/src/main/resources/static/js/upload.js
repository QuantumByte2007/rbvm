function initDataTables() {
    if ($('#reconciliationTable').length) {
        if ($.fn.DataTable.isDataTable('#reconciliationTable')) {
            $('#reconciliationTable').DataTable().destroy();
        }

        var table = $('#reconciliationTable').DataTable({
            aaSorting: [], // Pas de tri initial forcé
            pagingType: 'full_numbers',
            buttons: [
                {
                    extend: 'excelHtml5',
                    // ⚠️ On n'exporte que les colonnes 0 (DNS) et 1 (ENV)
                    exportOptions: { columns: [ 0, 1 ] }
                },
                {
                    extend: 'pdfHtml5',
                    exportOptions: { columns: [ 0, 1 ] }
                }
            ],
            responsive: true,
            language: {
                "search": "Recherche : ",
                "lengthMenu": "Afficher : _MENU_ serveurs",
                "info": "Du _START_ au _END_ sur _TOTAL_ serveurs scannés",
                "paginate": {
                    "first": '<i class="fa-solid fa-angles-left fa-xs"></i>',
                    "last": '<i class="fa-solid fa-angles-right fa-xs"></i>',
                    "next": '<i class="fa-solid fa-angle-right fa-xs"></i>',
                    "previous": '<i class="fa-solid fa-angle-left fa-xs"></i>'
                },
                "emptyTable": "Aucune donnée disponible",
                "zeroRecords": "Aucun serveur trouvé"
            }
        });

        // Placement des boutons PDF/Excel en haut à gauche
        table.buttons().container().appendTo('#reconciliationTable_wrapper .col-md-6:eq(0)');

        // Style de la barre de recherche
        $(".dataTables_filter input").attr("placeholder", "Chercher un DNS ou ENV...").css({width: "250px", display: "inline-block"});
    }
}

// 1. Déclenchement au chargement normal
$(document).ready(initDataTables);

// 2. Déclenchement après un appel HTMX
document.body.addEventListener('htmx:load', initDataTables);