# jazorp

[![Build Status](https://travis-ci.org/jazorp/jazorp.svg?branch=master)](https://travis-ci.org/jazorp/jazorp)
[![Coverage Status](https://coveralls.io/repos/github/jazorp/jazorp/badge.svg?branch=master)](https://coveralls.io/github/jazorp/jazorp?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.jazorp/jazorp/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.jazorp/jazorp)


Jazorp is a java library that can be used to validate Java Objects.

###*Motivation*

Jazorp was designed to provide a simple, yet effective, way of
validating Java objects and provide structured error data that reflect the actual Object that is validated.


###*Features*

Despite the fact that a declarative approach looks much more attractive and cleaner in regards of data validation, Jazorp is imperative. This can be perceived as a rather ugly approach at a first glance but it has some very important advantages:

- Object data to field name mapping can be arbitrarily changed without a hustle.
- Domain Objects are not overblown with annotations, especially in cases when an Object can be validated differently depending on the context/action.
- For the reason described above there is no need for reflection. There is only plain old data processing and recursion.
- No reflection means that type erasure is irrelevant when it comes to validation of nested lists.
- A series of complex validations can be performed on an object and they can all "live" in the same validation class. This can make the code easier to understand.
- Jazorp's validation classes are context agnostic and for that reason they can be invoked at any point the user chooses.

Extra features include:

- Blocking validators
- All validators are evaluated lazily
- Message customization for stock validators
- Extended customization for stock validators
- Optional validators
- Validator composition


###*Built-in validators*

Jazop comes with a set of built in validations which are the following:

- [x] `notNull` - `Object`
- [x] `notBlank` - `String`
- [x] `minLength` - `String`
- [x] `length` - `String`
- [x] `positive` - `Number`
- [x] `email` - `String`
- [ ] `min` - `Number`
- [ ] `max` - `Number`
- [ ] `memberOf` - `T, Iterable<T>`
- [ ] `memberOf` - `T, T...`


###*Example*

Consider the following domain object (getters and setters are omitted for brevity in all classes):

```java
class Person {

    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
```

You can create a validator for that class as follows:

```java
import static io.github.jazorp.Validators.*;

class PersonValidator implements Validator<Person> {

    @Override
    public Aggregation collect(Person person) {
        return aggregate(notBlank("name", person.getName()),
                         positive("age", person.getAge()));
    }
}
```

Now you can validate `Person` objects:

```java
PersonValidator validator = new PersonValidator();
Person person = new Person("Morty", 16);
Result result = validator.validate(person);
assertTrue(result.isValid());
person = new Person("", 0);
result = validator.validate(person);
assertFalse(result.isValid());
assertEquals("{age=[age must be positive], name=[name cannot be blank]}",
        result.getErrors().toString());
```

Now consider a more complex example. There is an `Address` class:

```java
class Address {

    private String street;
    private String zip;

    public Address(String street, String zip) {
        this.street = street;
        this.zip = zip;
    }
}
```

And the `Person` has an `Address`:

```java
class Person {

    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public Person(String name, int age, String address, String zip) {
        this(name, age);
        this.address = new Address(address, zip);
    }
}
```

An `Address` validator can be formed as follows:

```java
import static io.github.jazorp.Validators.*;

class AddressValidator implements Validator<Address> {

    @Override
    public Aggregation collect(Address address) {
        return aggregate(notBlank("street", address.getStreet()),
                         length("zip", address.getZip(), 5));
    }
}
```

We can create an extended version our existing validator:

```java
import static io.github.jazorp.Validators.*;

class NestedPersonValidator implements Validator<Person> {

    @Override
    public Aggregation collect(Person person) {
        return aggregate(notBlank("name", person.getName()),
                         positive("age", person.getAge()))
                .nested("address", addressValidator, person.getAddress());
    }
}
```

_Note:_ It is assumed that the `addressValidator` instance is passed to `NestedPersonValidator` using some form of
dependency injection (either automatic or manual), or by plain old statically initialized classes.

Validating the extended `Person`:

```java
NestedPersonValidator validator = new NestedPersonValidator();
Person person = new Person("Morty", 0, "", "123456");
Result result = validator.validate(person);
assertFalse(result.isValid());
assertEquals("{address={street=[street cannot be blank], zip=[zip must be exactly 5 characters long]}, age=[age must be positive]}",
        result.getErrors().toString());
```

Notice the error nesting.


###*Download*

Download from [Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.jazorp/jazorp)


###*Documentation*

Until it is written check the [ValidatorTest.java](https://github.com/jazorp/jazorp/blob/master/src/test/java/io/github/jazorp/ValidatorTest.java)


###*Contributors*

Andrew Iatropoulos


###*License*

Jazorp is released under the [Apache 2.0 license](LICENSE).

```
Copyright 2016 Kostas Georgiadis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```