package sia.sever.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "app_user")
public class User {

    // Fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 25, nullable = false, unique = true)
    private String userName;

    @Column(length = 50, nullable = false, unique = true)
    private String email;

    @Column(length = 60, nullable = false)
    private String password;

    // Users can have multiple cars(One-to-many relationship)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Car> cars = new ArrayList<>();

    public void addCar(Car car){
        cars.add(car);
        car.setUser(this);
    }

    public void removeCar(Car car){
        cars.remove(car);
        car.setUser(null);
    }

    // Constructor
    public User(){}
    public User(String userName, String email, String password){

        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    // Getters
    public Long getId(){
        return id;
    }

    public String getUserName(){
        return userName;
    }

    public String getEmail(){
        return email;
    }

    public String getPassword(){
        return password;
    }

    public List<Car> getCars(){
        return cars;
    }

    // Setters
    public void setUserName(String userName){
        this.userName = userName;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setPassword(String password){
        this.password = password;
    }
}
