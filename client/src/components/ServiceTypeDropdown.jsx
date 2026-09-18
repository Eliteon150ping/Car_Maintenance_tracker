import { useState } from "react";
import {
    FaChevronDown,
    FaOilCan,
    FaFilter,
    FaPlug,
    FaCog,
    FaClock,
    FaTint,
    FaBatteryFull,
    FaSyncAlt,
    FaCar,
    FaCircle,
    FaStopCircle,
    FaGasPump,
    FaTools
} from "react-icons/fa";
import "../styles/ServiceTypeDropdown.css";

const serviceTypeIcons = {
    ENGINE_OIL_AND_FILTER: FaOilCan,
    AIR_FILTER: FaFilter,
    SPARK_PLUGS: FaPlug,
    SERPENTINE_BELT: FaCog,
    TIMING_BELT: FaClock,

    COOLANT_FLUSH: FaTint,

    BATTERY: FaBatteryFull,

    DIFFERENTIAL_OIL: FaOilCan,
    TRANSMISSION_FLUID: FaSyncAlt,

    TYRE_ROTATION: FaCircle,
    WHEEL_ALIGNMENT: FaCar,

    BRAKE_PADS: FaStopCircle,

    FUEL_FILTER: FaFilter,
    FUEL_INJECTOR_CLEANING: FaGasPump,

    OTHER: FaTools
};

function ServiceTypeDropdown({ serviceRecord,serviceTypes, serviceType, setServiceType, hasError, shake }) {

    const [isOpen, setIsOpen] = useState(false);

    const selectedService = serviceTypes.find(
        type => type.displayName.toLowerCase() === serviceType?.toLowerCase()
    );

    const SelectedIcon = serviceTypeIcons[selectedService?.value];

    return (

        <div className="service-type-dropdown">

            <button
                type="button"
                disabled={serviceRecord != null}
                className={`service-type-button ${hasError ? (shake ? "input-error input-shake" : "input-error") : ""}`}
                onClick={() => setIsOpen(!isOpen)}
            >
                <span className="service-type-selected">
                    {SelectedIcon && <SelectedIcon />}
                    {serviceType || "Select Service"}
                </span>

                <FaChevronDown />
            </button>

            {isOpen && (
                <div className="service-type-options">
                    {serviceTypes.map(type => {
                        const Icon = serviceTypeIcons[type.value];

                        return (
                            <button
                                type="button"
                                key={type.value}
                                onClick={() => {
                                    setServiceType(type.value);
                                    setIsOpen(false);
                                }} >
                                {Icon && <Icon />}
                                <span>{type.displayName}</span>
                            </button>
                        );
                    })}
                </div>
            )}
        </div>
    );
}

export default ServiceTypeDropdown;