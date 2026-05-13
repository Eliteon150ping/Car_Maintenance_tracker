package sia.sever.specification;

import org.springframework.data.jpa.domain.Specification;
import sia.sever.entity.Car;
import sia.sever.entity.User;

// This is used for better filtering methods
public class CarSpecification {

    public static Specification<Car> hasBrand(String brand){
        return(root, query, builder) ->
                brand == null ? null : builder.like(builder.lower(root.get("brand")), "%" + brand.toLowerCase() + "%");
    }

    public static Specification<Car> hasModel(String model){
        return(root, query, builder) ->
                model == null ? null : builder.like(builder.lower(root.get("model")), "%" + model.toLowerCase() + "%");

    }

    public static Specification<Car> hasYear(Integer year){
        return(root, query, builder) ->
                year == null ? null : builder.equal(root.get("year"), year);
    }

    public static Specification<Car> hasUser(User user){
        return((root, query, builder) ->
                user == null ? null : builder.equal(root.get("user"), user));
    }
}
