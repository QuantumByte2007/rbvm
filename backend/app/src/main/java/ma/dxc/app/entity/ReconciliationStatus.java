package ma.dxc.app.entity;

// Result of matching a Qualys-scanned Host against CMDB by IP (or vice versa).
// Anything other than RECONCILED is surfaced as "Shadow IT" in the UI: an asset
// nobody has a complete picture of, either because it was never registered in
// the CMDB, or because it's registered but nobody has scanned it.
public enum ReconciliationStatus {
    RECONCILED,
    SHADOW_QUALYS_ONLY,
    SHADOW_CMDB_ONLY
}
