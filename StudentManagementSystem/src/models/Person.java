package models;

public abstract class Person {
    protected String name;
    protected String email;
    
    public Person(String name, String email) {
        this.name = name;
        this.email = email;
    }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public abstract void displayInfo();
    
    @Override
    public String toString() {
        return "Name: " + name + ", Email: " + email;
    }
}