function ConfirmationModal({
    title, message, confirmText = "Confirm", cancelText = "Cancel", onConfirm, onCancel}){

    return(

        <div className="modal-overlay">
            <div className="confirmation-modal">
                <h2>{title}</h2>
                <p>{message}</p>
                <div className="modal-buttons">
                    <button type="button" onClick={onConfirm}>{confirmText}</button>
                    <button type="button" onClick={onCancel}>{cancelText}</button>
                </div>
            </div>
        </div>
    );
}

export default ConfirmationModal;