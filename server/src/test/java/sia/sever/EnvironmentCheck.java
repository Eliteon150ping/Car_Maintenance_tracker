package sia.sever;

public class EnvironmentCheck {

    public static void main(String[] args) {
        String password = System.getenv("DB_PASSWORD");

        System.out.println(
                password == null
                        ? "DB_PASSWORD is missing"
                        : "DB_PASSWORD received. Length: " + password.length()
        );
    }
}
