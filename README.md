# Spring Boot with Hibernate Notes

Some notes taken while following the tutorial

## Hibernate Basics
 
Hibernate is framework for persisting/retrieving data in a database. Basically an ORM, reducing JDBC code needed.
 
It uses HQL (Stands for Hibernate query language).

### Sample Config File

`hibernate.cfg.xml`

```
<!DOCTYPE hibernate-configuration PUBLIC
        "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
        "http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">

<hibernate-configuration>
    <session-factory>
        <!-- JDBC Database connection settings -->
        <property name="connection.url">jdbc:mysql://127.0.0.1:3306/your_databse</property>
        <property name="connection.username">user</property>
        <property name="connection.password">password</property>

        <!-- JDBC connection pool settings... using built-in test pool -->
        <property name="connection.pool_size">1</property>

        <!-- Select our SQL dialect -->
        <property name="dialect">org.hibernate.dialect.MySQL57Dialect</property>

        <!-- Echo the SQL to stdout -->
        <property name="show_sql">true</property>

        <!-- Set the current session context -->
        <property name="current_session_context_class">thread</property>

        <!-- Automatically create entities -->
        <property name="hibernate.hbm2ddl.auto">update</property>
    </session-factory>
</hibernate-configuration>
```
 
### Entity Classes
 
Just plain classes mapped to a database table.
 
```
@Entity
@Table(name = "student")
public class Student {

 @Id
 @Column(name = "id")
 private int id;

 @Column(name = "first_name")
 private String firstName;

 @Column(name = "last_name")
 private String lastName;

 @Column(name = "email")
 private String email;


 public Student() {
 }

 public Student(String firstName, String lastName, String email) {
     this.firstName = firstName;
     this.lastName = lastName;
     this.email = email;
 }

 ...
 // Getters/Setters for all fields.
 ...

 @Override
 public String toString() {
     return "Student{" +
             "id=" + id +
             ", firstName='" + firstName + '\'' +
             ", lastName='" + lastName + '\'' +
             ", email='" + email + '\'' +
             '}';
 }
}

```

### Main Hibernate Classes

* `SessionFactory` - Reads Hibernate config file, creates session objects. Heavy-weight object, singleton.
* `Session` - Wraps a JDBC connection. Used to Save/Retrieve operations. Short lived, created at `SessionFactory`

```
SpringApplication.run(SpringBootHibernateDemoApplication.class, args);

        SessionFactory factory = new Configuration()
                .configure()
                .addAnnotatedClass(Student.class)
                .buildSessionFactory();

        Session session = factory.getCurrentSession();
```

## CRUD

### Creating objects

```
Student tempStudent = new Student("Paul", "Wall", "paul.wall@example.com");

session.beginTransaction();
session.save(tempStudent);
session.getTransaction().commit();
```

### Retrieving objects

Transactions are needed to retrieve objects as well `¯\_(ツ)_/¯`
```
session.beginTransaction();
session.get(Student.class, tempStudent.getId());
session.getTransaction().commit();
```

If the object is not found it returns `null`

It's possible also to create studentQueries with HQL (Hibernate Query Language):

```
List<Student> students = session
                    .createQuery("from Student s where s.lastName='Wall'"
                                 + "OR s.firstName='Daffy'")
                    .getResultList();
```

### Update objects

```
Student student = session.get(Student.class, studentId);
student.setEmail("example@mail.com");
```

_Note the objects are updated in-place. No need to retrieve them again._

```
session.createQuery("update Student set email='foo@gmail.com'").executeUpdate();
```

### Delete objects

```
Student student = session.get(Student.class, studentId);
session.delete(student);
```

```
session.createQuery("delete from Student where id=2");
```

### Custom SQL Queries:

```
session.createSQLQuery("DROP TABLE student").executeUpdate();
```

## Advanced Mappings

### Entity Lifecycle


```
________________________________________________________________________________________________
| Operations | Description                                                                      |
|_______________________________________________________________________________________________|
| Detach     | If entity is detached, it is not associated with a Hibernate session             |
| Merge      | If instance is detrached from session, then merge will reattach to session       |
| Persist    | Transitions new instances to managed state. Next flush/commit will save in db    |
| Remove     | Transitions managed entity to be removed. Next flush/commit will delete from db  |
| Refresh    | Reload/synch object with data from db. Prevents stale data                       |
|_______________________________________________________________________________________________|
```

### Cascade types

```
@OneToOne(cascade=CascadeType.ALL)
```

Or for fine grained config

```
@OneToOne(cascade={CascadeType.DETACH,
                   CascadeType.MERGE,
                   CascadeType.PERSIST,
                   CascadeType.REFRESH,
                   CascadeType.REMOVE})
```

### One to One Relationships unidirectional

Saving related objects:

```
private Instructor instructor1 = new Instructor("Chad", "Darby", "darby@luv2code.com");
private InstructorDetail instructorDetail1 =
            new InstructorDetail("http://www.luv2code.com/youtube", "Luv 2 code!!");

instructor1.setInstructorDetail(instructorDetail1);
...
...
session.save(instructor1);
```

Deleting related objects:

```
...
Instructor instructor = session.get(Instructor.class, id);
session.delete(instructor);
...
```
This will result in the deletion of the instructor detail as well:
```
Hibernate: delete from instructor where id=?
Hibernate: delete from instructor_detail where id=?
```

### One to One Relationships bidirectional

Reverse lookups by setting bidirectional relationship. No DB schema changes are required, Hibernate can
take care of it. Just add the following annotation to the child relation in a field to hold the parent object:

```
@OneToOne(mappedBy = "instructorDetail", cascade = CascadeType.ALL)
private Instructor instructor;
```

Hibernate will look for the field in the `mappedBy` annotation attribute, in the parent relation/class:

```
@OneToOne(cascade = CascadeType.ALL)
@JoinColumn(name = "instructor_detail_id")
private InstructorDetail instructorDetail;
```

### One To Many

Instructor can have multiple courses. So in `Instructor`:

```
@OneToMany(mappedBy = "instructor",
                cascade = {CascadeType.PERSIST, CascadeType.MERGE,
                           CascadeType.DETACH, CascadeType.REFRESH})
private List<Course> courses;
```

Then in `Course`:

```
@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
                      CascadeType.DETACH, CascadeType.REFRESH})
@JoinColumn(name = "instructor_id")
private Instructor instructor;
```

### Many to many

To create a m2m relationship:

I.e: 

* In Student entity
```
@ManyToMany
@JoinTable(
        name = "course_student",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
)
private List<Course> courses;
```

* In Course entity

```
@ManyToMany
@JoinTable(
        name = "course_student",
        joinColumns = @JoinColumn(name = "course_id"),
        inverseJoinColumns = @JoinColumn(name = "student_id")
)
private List<Student> students;
```

## Eager/Lazy loading

Evidently:

- Eager loads all related objects to the one retrieved.
- Lazy load the related objects on demand.

So Lazy is preferred.

Default Types:

```
@OneToOne -> Eager
@OneToMany -> Lazy
@ManyToOne -> Eager
@ManyToMany -> Lazy
```

To define the type use the `fetch` parameter in the relationship annotation.
 
```
@OneToMany(fetch = FetchType.LAZY, ...)
```

__Take into account that for lazy retrieving data, a session to the DB must be opened__

