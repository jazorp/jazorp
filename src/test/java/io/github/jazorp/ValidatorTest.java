package io.github.jazorp;

import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static io.github.jazorp.Validators.*;
import static org.junit.Assert.*;

public class ValidatorTest {

    private Person person;

    @Before
    public void setup() {
        ErrorFormatter.getInstance().reset();
    }

    private static class Person {

        private String name;
        public String getName() { return name; }

        private int age;
        public int getAge() { return age; }

        private Address address;
        public Address getAddress() { return address; }

        private List<Pet> pets;
        public List<Pet> getPets() { return pets; }

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public Person(String name, int age, String address, String zip) {
            this(name, age);
            this.address = new Address(address, zip);
        }

        public Person(String name, int age, List<Pet> pets) {
            this(name, age);
            this.pets = pets;
        }
    }

    private static class Address {

        private String street;
        public String getStreet() { return street; }

        private String zip;
        public String getZip() { return zip; }

        public Address(String street, String zip) {
            this.street = street;
            this.zip = zip;
        }
    }

    private static class Pet {

        private String name;
        public String getName() { return name; }

        public Pet(String name) {
            this.name = name;
        }
    }

    private static class PersonValidator implements Validator<Person> {

        @Override
        public Aggregation collect(Person person) {
            return aggregate(notBlank("name", person.getName()),
                             positive("age", person.getAge()));
        }
    }

    @Test
    public void valid_test() {
        PersonValidator validator = new PersonValidator();
        person = new Person("Morty", 16);
        Result result = validator.validate(person);
        assertTrue(result.isValid());
    }

    @Test
    public void invalid_test() {
        PersonValidator validator = new PersonValidator();
        person = new Person("", 0);
        Result result = validator.validate(person);
        assertFalse(result.isValid());
    }

    @Test
    public void error_format() {
        PersonValidator validator = new PersonValidator();
        person = new Person("", 0);
        Result result = validator.validate(person);
        assertFalse(result.isValid());
        Map<String, Set<String>> errors = new HashMap<>();
        errors.put("age", Collections.singleton("age must be positive"));
        errors.put("name", Collections.singleton("name cannot be blank"));
        assertEquals(errors, result.getErrors());
    }

    private static class MultiplePersonValidator implements Validator<Person> {

        @Override
        public Aggregation collect(Person person) {
            return aggregate(notBlank("name", person.getName()),
                             notNull("name", person.getName()),
                             positive("age", person.getAge()));
        }
    }

    @Test
    public void mutliple_validators_per_field_test() {
        MultiplePersonValidator validator = new MultiplePersonValidator();
        person = new Person(null, 0);
        Result result = validator.validate(person);
        assertFalse(result.isValid());
        Map<String, Set<String>> errors = new HashMap<>();
        errors.put("age", Collections.singleton("age must be positive"));
        errors.put("name", new HashSet<>(Arrays.asList("name cannot be blank", "name cannot be null")));
        assertEquals(errors, result.getErrors());
    }

    private static class BlockingPersonValidator implements Validator<Person> {

        @Override
        public Aggregation collect(Person person) {
            return aggregate(notBlank("name", person.getName()).blocking(),
                             positive("age", person.getAge()));
        }
    }

    @Test
    public void blocking_test() {
        BlockingPersonValidator validator = new BlockingPersonValidator();
        person = new Person("", 0);
        Result result = validator.validate(person);
        assertFalse(result.isValid());
        assertEquals(Collections.singletonMap("name",
                Collections.singleton("name cannot be blank")), result.getErrors());
    }

    private static class UnorderedBlockingPersonValidator implements Validator<Person> {

        @Override
        public Aggregation collect(Person person) {
            return aggregate(positive("age", person.getAge()),
                             notBlank("name", person.getName()).blocking());
        }
    }

    @Test
    public void unordered_blocking_test() {
        UnorderedBlockingPersonValidator validator = new UnorderedBlockingPersonValidator();
        person = new Person("", 0);
        Result result = validator.validate(person);
        assertFalse(result.isValid());
        assertEquals(Collections.singletonMap("name",
                Collections.singleton("name cannot be blank")), result.getErrors());
    }

    private static class AddressValidator implements Validator<Address> {

        @Override
        public Aggregation collect(Address address) {
            return aggregate(notBlank("street", address.getStreet()),
                             length("zip", address.getZip(), 5));
        }
    }

    private static AddressValidator addressValidator = new AddressValidator();

    private static class NestedPersonValidator implements Validator<Person> {

        @Override
        public Aggregation collect(Person person) {
            return aggregate(notBlank("name", person.getName()),
                             positive("age", person.getAge()))
                    .nested("address", addressValidator, person.getAddress());
        }

    }

    @Test
    public void valid_nested_test() {
        NestedPersonValidator validator = new NestedPersonValidator();
        person = new Person("Morty", 10, "Hmmmm", "12345");
        Result result = validator.validate(person);
        assertTrue(result.isValid());
    }

    @Test
    public void invalid_nested_test() {
        NestedPersonValidator validator = new NestedPersonValidator();
        person = new Person("Morty", 0, "", "123456");
        Result result = validator.validate(person);
        assertFalse(result.isValid());
        Map<String, Object> errors = new HashMap<>();
        Map<String, Object> nested = new HashMap<>();
        nested.put("street", Collections.singleton("street cannot be blank"));
        nested.put("zip", Collections.singleton("zip must be exactly 5 characters long"));
        errors.put("address", nested);
        errors.put("age", Collections.singleton("age must be positive"));
        assertEquals(errors, result.getErrors());
    }

    private static class PetValidator implements Validator<Pet> {

        @Override
        public Aggregation collect(Pet pet) {
            return aggregate(notBlank("name", pet.getName()));
        }
    }

    private static PetValidator petValidator = new PetValidator();

    private static class NestedListPersonValidator implements Validator<Person> {

        @Override
        public Aggregation collect(Person person) {
            return aggregate(notBlank("name", person.getName()),
                             positive("age", person.getAge()))
                    .nestedList("pet", petValidator, person.getPets());
        }
    }

    @Test
    public void invalid_nested_list_test() {
        NestedListPersonValidator validator = new NestedListPersonValidator();
        person = new Person("Morty", 0, Arrays.asList(new Pet("Snuffles"), new Pet(""), new Pet("Gazorpazorpfield")));
        Result result = validator.validate(person);
        assertFalse(result.isValid());
        Map<String, Object> errors = new HashMap<>();
        errors.put("age", Collections.singleton("age must be positive"));
        Map<String, Set<String>> nested = new HashMap<>();
        nested.put("name", Collections.singleton("name cannot be blank"));
        errors.put("pet[1]", nested);
        assertEquals(errors, result.getErrors());
    }

    @Test
    public void top_level_valid_should_proceed_to_nested() {
        person = new Person("Morty", 10, "Foo", "123");
        NestedPersonValidator validator = new NestedPersonValidator();
        Result result = validator.validate(person);
        Map<String, Object> errors = Collections.singletonMap("address",
                Collections.singletonMap("zip", Collections.singleton("zip must be exactly 5 characters long")));
        assertEquals(errors, result.getErrors());
    }

    @Test
    public void error_customization_test() {
        Map<ErrorType, String> custom = new HashMap<>();
        custom.put(ErrorType.LENGTH, "The provided length (%2$s) for %s is not correct. It should be exactly %3$s characters long.");
        ErrorFormatter.getInstance().override(custom);
        person = new Person("Morty", 10, "Foo", "123");
        NestedPersonValidator validator = new NestedPersonValidator();
        Result result = validator.validate(person);
        Map<String, Object> errors = Collections.singletonMap("address",
                Collections.singletonMap("zip", Collections.singleton(
                        "The provided length (123) for zip is not correct. It should be exactly 5 characters long.")));
        assertEquals(errors, result.getErrors());
    }

    private ErrorFormattingFunc fmtFunc = new ErrorFormattingFunc() {

        @Override
        public String format(ErrorType error, Env env, Object[] args) {
            if (error == ErrorType.NOT_BLANK && env.get("locale").equals("el")) {
                return String.format("Το %s δεν μπορεί να είναι κενό", args);
            }
            fail("Formatting attrs where not matched");
            return null;
        }
    };

    @Test
    public void error_customization_using_env() {
        Env env = new Env.Builder().set("locale", "el").build();
        ErrorFormatter.getInstance().override(fmtFunc);
        person = new Person("", 10);
        Validator<Person> validator = (p) -> Aggregation.of(notBlank("name", p.getName()));
        Result result = validator.validate(person, env);
        assertEquals(Collections.singletonMap("name",
                Collections.singleton("Το name δεν μπορεί να είναι κενό")), result.getErrors());
    }

    @Test
    public void optional_test() {
        Validator<Person> validator = (p) -> Aggregation.of(notBlank("name", p.getName()).optional(),
                                                            positive("age", p.getAge()));
        Person person = new Person(null, 0);
        Result result = validator.validate(person);
        assertEquals(Collections.singletonMap("age",
                Collections.singleton("age must be positive")), result.getErrors());
    }

    @Test
    public void validator_composition() {
        Validator<Person> nameValidator = (p) -> Aggregation.of(notBlank("name", p.getName()));
        Validator<Person> ageValidator = (p) -> Aggregation.of(notNull("name", p.getName()), positive("age", p.getAge()));
        Validator<Person> personValidator = nameValidator.compose(ageValidator);
        Person person = new Person(null, 0);
        Result result = personValidator.validate(person);
        Map<String, Set<String>> errors = new HashMap<>();
        errors.put("age", Collections.singleton("age must be positive"));
        errors.put("name", new HashSet<>(Arrays.asList("name cannot be blank", "name cannot be null")));
        assertEquals(errors, result.getErrors());
    }

    //  TODO unique set
    // TODO composition with nested
    // TODO deeply nested
}