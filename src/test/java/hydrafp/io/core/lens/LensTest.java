package hydrafp.io.core.lens;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LensTest {

    // Test classes
    static class Person {
        private final String name;
        private final int age;

        Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() { return name; }
        public int getAge() { return age; }
        public Person withName(String newName) { return new Person(newName, age); }
        public Person withAge(int newAge) { return new Person(name, newAge); }
    }

    static class Address {
        private final String street;
        private final String city;

        Address(String street, String city) {
            this.street = street;
            this.city = city;
        }

        public String getStreet() { return street; }
        public String getCity() { return city; }
        public Address withStreet(String newStreet) { return new Address(newStreet, city); }
        public Address withCity(String newCity) { return new Address(street, newCity); }
    }

    static class PersonWithAddress {
        private final Person person;
        private final Address address;

        PersonWithAddress(Person person, Address address) {
            this.person = person;
            this.address = address;
        }

        public Person getPerson() { return person; }
        public Address getAddress() { return address; }
        public PersonWithAddress withPerson(Person newPerson) { return new PersonWithAddress(newPerson, address); }
        public PersonWithAddress withAddress(Address newAddress) { return new PersonWithAddress(person, newAddress); }
    }

    // Lenses
    private static final Lens<Person, String> nameLens = Lens.of(
            Person::getName,
            person -> person::withName
    );

    private static final Lens<Person, Integer> ageLens = Lens.of(
            Person::getAge,
            person -> person::withAge
    );

    private static final Lens<Address, String> streetLens = Lens.of(
            Address::getStreet,
            address -> address::withStreet
    );

    private static final Lens<Address, String> cityLens = Lens.of(
            Address::getCity,
            address -> address::withCity
    );

    private static final Lens<PersonWithAddress, Person> personLens = Lens.of(
            PersonWithAddress::getPerson,
            pwa -> pwa::withPerson
    );

    private static final Lens<PersonWithAddress, Address> addressLens = Lens.of(
            PersonWithAddress::getAddress,
            pwa -> pwa::withAddress
    );

    @Test
    void testLensGet() {
        Person person = new Person("John", 30);
        assertEquals("John", nameLens.get(person));
        assertEquals(30, ageLens.get(person));
    }

    @Test
    void testLensSet() {
        Person person = new Person("John", 30);
        Person updatedPerson = nameLens.set(person, "Jane");
        assertEquals("Jane", updatedPerson.getName());
        assertEquals(30, updatedPerson.getAge());

        updatedPerson = ageLens.set(person, 31);
        assertEquals("John", updatedPerson.getName());
        assertEquals(31, updatedPerson.getAge());
    }

    @Test
    void testLensModify() {
        Person person = new Person("John", 30);
        Person updatedPerson = nameLens.modify(person, String::toUpperCase);
        assertEquals("JOHN", updatedPerson.getName());
        assertEquals(30, updatedPerson.getAge());

        updatedPerson = ageLens.modify(person, age -> age + 1);
        assertEquals("John", updatedPerson.getName());
        assertEquals(31, updatedPerson.getAge());
    }

    @Test
    void testLensComposition() {
        PersonWithAddress pwa = new PersonWithAddress(
                new Person("John", 30),
                new Address("Main St", "New York")
        );

        Lens<PersonWithAddress, String> personNameLens = personLens.compose(nameLens);
        Lens<PersonWithAddress, Integer> personAgeLens = personLens.compose(ageLens);
        Lens<PersonWithAddress, String> addressStreetLens = addressLens.compose(streetLens);
        Lens<PersonWithAddress, String> addressCityLens = addressLens.compose(cityLens);

        // Test getting values through composed lenses
        assertEquals("John", personNameLens.get(pwa));
        assertEquals(30, personAgeLens.get(pwa));
        assertEquals("Main St", addressStreetLens.get(pwa));
        assertEquals("New York", addressCityLens.get(pwa));

        // Test setting values through composed lenses
        PersonWithAddress updatedPwa = personNameLens.set(pwa, "Jane");
        assertEquals("Jane", updatedPwa.getPerson().getName());
        assertEquals(30, updatedPwa.getPerson().getAge());
        assertEquals("Main St", updatedPwa.getAddress().getStreet());
        assertEquals("New York", updatedPwa.getAddress().getCity());

        updatedPwa = addressStreetLens.set(pwa, "Broadway");
        assertEquals("John", updatedPwa.getPerson().getName());
        assertEquals(30, updatedPwa.getPerson().getAge());
        assertEquals("Broadway", updatedPwa.getAddress().getStreet());
        assertEquals("New York", updatedPwa.getAddress().getCity());

        // Test modifying values through composed lenses
        updatedPwa = personNameLens.modify(pwa, String::toUpperCase);
        assertEquals("JOHN", updatedPwa.getPerson().getName());
        assertEquals(30, updatedPwa.getPerson().getAge());
        assertEquals("Main St", updatedPwa.getAddress().getStreet());
        assertEquals("New York", updatedPwa.getAddress().getCity());

        updatedPwa = addressCityLens.modify(pwa, city -> city + ", NY");
        assertEquals("John", updatedPwa.getPerson().getName());
        assertEquals(30, updatedPwa.getPerson().getAge());
        assertEquals("Main St", updatedPwa.getAddress().getStreet());
        assertEquals("New York, NY", updatedPwa.getAddress().getCity());
    }

    @Test
    void testMultipleLensComposition() {
        PersonWithAddress pwa = new PersonWithAddress(
                new Person("John", 30),
                new Address("Main St", "New York")
        );

        Lens<PersonWithAddress, String> personNameLens = personLens.compose(nameLens);
        Lens<PersonWithAddress, String> addressStreetLens = addressLens.compose(streetLens);

        // Compose a lens that focuses on both the person's name and the address street
        Lens<PersonWithAddress, PersonWithAddress> combinedLens = Lens.of(
                pwad -> pwad,
                pwad -> newPwad -> new PersonWithAddress(
                        pwad.getPerson().withName(personNameLens.get(newPwad)),
                        pwad.getAddress().withStreet(addressStreetLens.get(newPwad))
                )
        );

        PersonWithAddress updatedPwa = combinedLens.modify(pwa, pwad ->
                new PersonWithAddress(
                        new Person("Jane", 30),
                        new Address("Broadway", "New York")
                )
        );

        assertEquals("Jane", updatedPwa.getPerson().getName());
        assertEquals(30, updatedPwa.getPerson().getAge());
        assertEquals("Broadway", updatedPwa.getAddress().getStreet());
        assertEquals("New York", updatedPwa.getAddress().getCity());
    }
}