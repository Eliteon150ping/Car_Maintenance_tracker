import "../styles/ConfirmationModal.css";

function ConfirmationModal({
    title, message, confirmText = "Confirm", cancelText = "Cancel", onConfirm, onCancel, variant }) {
    console.log("Modal variant:", variant);

    return (

        <div className="modal-overlay">
            <div className="confirmation-modal">
                <h2 className="modal-h2">{title}</h2>
                <p className="modal-p">{message}</p>
                <div className="modal-buttons">
                    <button className={`modal-button ${variant === "save" ? "save-confirm" : "cancel-confirm"}`}>{confirmText}</button>
                    <button
                        className={`modal-button ${variant === "save" ? "save-cancel" : "cancel-cancel"}`}
                        type="button"
                        onClick={onCancel}
                    >
                        {cancelText}
                    </button>
                </div>
            </div>
        </div>
    );
}

export default ConfirmationModal;