function ServiceRecordFilter({
    serviceTypeOptions,
    serviceCategoryOptions,
    selectedServiceTypes,
    setSelectedServiceTypes,
    selectedServiceCategories,
    setSelectedServiceCategories,
    handleFilterChange,
    clearFilters,
    applyFilters,
    showFilters
}) {

    return showFilters && (
        <div className="service-filter-panel">

            <h3>Filter Service Records</h3>

            <div className="service-filter-section">
                <h4>Service Type</h4>

                {serviceTypeOptions.map(option => (
                    <label key={option.value}>
                        <input
                            type="checkbox"
                            value={option.value}
                            onChange={(e) =>
                                handleFilterChange(
                                    e,
                                    selectedServiceTypes,
                                    setSelectedServiceTypes
                                )
                            }
                            checked={selectedServiceTypes.includes(option.value)}
                        />
                        {option.label}
                    </label>
                ))}
            </div>

            <div className="service-filter-section">
                <h4>Service Category</h4>

                {serviceCategoryOptions.map(option => (
                    <label key={option.value}>
                        <input
                            type="checkbox"
                            value={option.value}
                            onChange={(e) =>
                                handleFilterChange(
                                    e,
                                    selectedServiceCategories,
                                    setSelectedServiceCategories
                                )
                            }
                            checked={selectedServiceCategories.includes(option.value)}
                        />
                        {option.label}
                    </label>
                ))}
            </div>

            <div className="service-filter-actions">
                <button type="button"
                    onClick={clearFilters}
                >
                    Clear
                </button>

                <button type="button"
                onClick={applyFilters}
                >
                    Apply Filters
                </button>
            </div>

        </div>
    );
}

export default ServiceRecordFilter;