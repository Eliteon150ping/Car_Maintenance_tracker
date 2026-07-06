import "../styles/SummaryCard.css"

function SummaryCard({ title, value }) {

    return (
        <div className="summary-card">
            <h3 className="summary-title">{title}</h3>
            <p className="summary-value">{value}</p>
        </div>
    );
}

export default SummaryCard;