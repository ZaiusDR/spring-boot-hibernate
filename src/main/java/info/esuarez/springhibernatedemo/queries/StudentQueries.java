package info.esuarez.springhibernatedemo.queries;

import info.esuarez.springhibernatedemo.entity.Student;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class StudentQueries {

    private Student tempStudent1 = new Student("Paul", "Wall", "paul.wall@example.com");
    private Student tempStudent2 = new Student("Mary", "Poppins", "mary.poppins@example.com");
    private Student tempStudent3 = new Student("Bonita", "Applebum", "bonita.appelbum@example.com");

    SessionFactory factory;


    public StudentQueries(SessionFactory factory) {
        this.factory = factory;

    }

    public void insertStudents() {
        try (Session session = factory.getCurrentSession()) {

            session.beginTransaction();
            session.save(tempStudent1);
            session.save(tempStudent2);
            session.save(tempStudent3);
            session.getTransaction().commit();
        }
    }

    public void getSimpleStudent() {
        try (Session session = factory.getCurrentSession()) {

            session.beginTransaction();
            Student student = session.get(Student.class, tempStudent1.getId());
            session.getTransaction().commit();

            System.out.println("Retrieved user : " + student.getFirstName());
        }
    }

    public void getStudentsWithLastName() {
        try (Session session = factory.getCurrentSession()) {

            session.beginTransaction();
            List<Student> students = session
                    .createQuery("from Student s where s.lastName='Wall'"
                            + "OR s.firstName='Daffy'")
                    .getResultList();
            session.getTransaction().commit();

            for (Student tempStudent: students) {
                System.out.println("Retrieved user by LastName 'Wall': " + tempStudent.getFirstName());
            }
        }
    }

    public void updateStudent() {
        int studentId = 1;

        try (Session session = factory.getCurrentSession()) {

            session.beginTransaction();
            Student student = session.get(Student.class, studentId);
//            student.setEmail("example@mail.com");

            session.getTransaction().commit();

            System.out.println("Student data: " + student.toString());

        }
    }

    public void updateAllStudents() {
        try (Session session = factory.getCurrentSession()) {

            session.beginTransaction();
            int recordsUpdated = session.createQuery("update Student set email='foo@gmail.com'").executeUpdate();

            session.getTransaction().commit();

            System.out.println("Student records updated: " + recordsUpdated);

        }
    }

    public void deleteStudent() {
        int studentId = 1;

        try (Session session = factory.getCurrentSession()) {

            session.beginTransaction();
            Student student = session.get(Student.class, studentId);
            session.delete(student);

            session.getTransaction().commit();

            System.out.println("Student data: " + student.toString());

        }
    }

    public void deleteAllStudents() {

        try (Session session = factory.getCurrentSession()) {

            session.beginTransaction();
            int deletedStudents = session.createQuery("delete from Student").executeUpdate();

            session.getTransaction().commit();

            System.out.println("Student records deleted: " + deletedStudents);

        }
    }

    public void dropStudentTable() {
        try (Session session = factory.getCurrentSession()) {

            session.beginTransaction();
            session.createSQLQuery("DROP TABLE IF EXISTS student, hibernate_sequence").executeUpdate();

            session.getTransaction().commit();
        }
    }
}
