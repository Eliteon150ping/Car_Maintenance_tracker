import "../styles/SummaryCard.css"

function SummaryCard({ title, value, onClick, className }) {

    return (
        <div className={`summary-card ${className || ""}`} onClick={onClick}>
            <h3 className="summary-title">{title}</h3>
            <p className="summary-value">{value}</p>
        </div>
    );
}

export default SummaryCard;