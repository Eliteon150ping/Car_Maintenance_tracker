import "../styles/ConfirmationModal.css";
import { useState, useRef } from "react";

function ConfirmationModal({
    title, message, confirmText = "Confirm", cancelText = "Cancel", onConfirm, onCancel, variant }) {

    const [isProcessing, setIsProcessing] = useState(false);
    const processingRef = useRef(false);

    async function handleConfirm() {
    if (processingRef.current) return;

    processingRef.current = true;
    setIsProcessing(true);

    try {
        await onConfirm();
    } catch (error) {
        processingRef.current = false;
        setIsProcessing(false);
    }
}

    return (

        <div className="modal-overlay">
            <div className="confirmation-modal">
                <h2 className="modal-h2">{title}</h2>
                <p className="modal-p">{message}</p>
                <div className="modal-buttons">
                    <button
                        className={`modal-button ${variant === "save" ? "save-confirm" : "cancel-confirm"}`}
                        type="button"
                        onClick={handleConfirm}
                        disabled={isProcessing}
                    >
                        {confirmText}
                    </button>

                    <button
                        className={`modal-button ${variant === "save" ? "save-cancel" : "cancel-cancel"}`}
                        type="button"
                        onClick={onCancel}
                        disabled={isProcessing}
                    >
                        {cancelText}
                    </button>
                </div>
            </div>
        </div>
    );
}

export default ConfirmationModal;